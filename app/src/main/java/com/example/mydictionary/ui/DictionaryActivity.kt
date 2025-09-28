package com.example.mydictionary.ui

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.mydictionary.R
import com.example.mydictionary.data.DictionaryDatabase
import com.example.mydictionary.data.Word
import com.example.mydictionary.repository.WordRepository
import com.example.mydictionary.viewmodel.DictionaryViewModel
import com.example.mydictionary.viewmodel.SearchResult
import com.example.mydictionary.viewmodel.WordViewModel
import com.example.mydictionary.viewmodel.WordViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

@AndroidEntryPoint
class DictionaryActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var wordViewModel: WordViewModel
    private val dictionaryViewModel: DictionaryViewModel by viewModels()
    private lateinit var textToSpeech: TextToSpeech
    private var currentWordDetails: String = ""
    private var searchJob: Job? = null

    // View bindings
    private lateinit var searchEditText: TextInputEditText
    private lateinit var searchButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var wordDetailsLayout: CardView
    private lateinit var wordTextView: TextView
    private lateinit var phoneticTextView: TextView
    private lateinit var meaningTextView: TextView
    private lateinit var exampleTextView: TextView
    private lateinit var readAloudButton: Button
    private lateinit var logoutButton: Button
    private lateinit var pauseButton: Button
    private lateinit var playButton: Button
    private lateinit var errorTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary)

        try {
            initializeViews()
            initializeTextToSpeech()
            initializeViewModel()
            setupSearch()
            setupButtons()
            observeSearchResults()
        } catch (e: Exception) {
            handleInitializationError(e)
        }
    }

    private fun initializeViews() {
        searchEditText = findViewById(R.id.searchEditText)
        searchButton = findViewById(R.id.searchButton)
        progressBar = findViewById(R.id.progressBar)
        wordDetailsLayout = findViewById(R.id.wordDetailsLayout)
        wordTextView = findViewById(R.id.wordTextView)
        phoneticTextView = findViewById(R.id.phoneticTextView)
        meaningTextView = findViewById(R.id.meaningTextView)
        exampleTextView = findViewById(R.id.exampleTextView)
        readAloudButton = findViewById(R.id.readAloudButton)
        logoutButton = findViewById(R.id.logoutButton)
        pauseButton = findViewById(R.id.pauseButton)
        playButton = findViewById(R.id.playButton)
        errorTextView = findViewById(R.id.errorTextView)
    }

    private fun initializeTextToSpeech() {
        textToSpeech = TextToSpeech(this, this)
    }

    private fun initializeViewModel() {
        val wordDao = DictionaryDatabase.getDatabase(this).wordDao()
        val repository = WordRepository(wordDao)
        wordViewModel = ViewModelProvider(this, WordViewModelFactory(repository))[WordViewModel::class.java]
    }

    private fun handleInitializationError(e: Exception) {
        Toast.makeText(this, "Error initializing app: ${e.message}", Toast.LENGTH_LONG).show()
        navigateToLogin()
    }

    private fun navigateToLogin() {
        try {
            // First cleanup resources
            searchJob?.cancel()
            if (textToSpeech.isSpeaking) {
                textToSpeech.stop()
            }
            textToSpeech.shutdown()

            // Show success message
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

            // Navigate to login
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            // Ignore any cancellation exceptions during logout
        }
    }

    private fun observeSearchResults() {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            try {
                dictionaryViewModel.searchResult.collectLatest { result ->
                    when (result) {
                        is SearchResult.Loading -> handleLoadingState()
                        is SearchResult.Success -> handleSuccessState(result)
                        is SearchResult.Error -> handleErrorState(result)
                        SearchResult.Initial -> Unit // Do nothing for initial state
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@DictionaryActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleLoadingState() {
        progressBar.visibility = View.VISIBLE
        hideWordDetails()
        errorTextView.visibility = View.GONE
    }

    private fun handleSuccessState(result: SearchResult.Success) {
        progressBar.visibility = View.GONE
        errorTextView.visibility = View.GONE
        
        result.data.firstOrNull()?.let { wordResponse ->
            val word = createWordFromResponse(wordResponse)
            wordViewModel.saveWord(word)
            currentWordDetails = buildReadableText(word)
            displayWordDetails(word)
        } ?: run {
            // If no data is returned
            showError("Word not found")
        }
    }

    private fun handleErrorState(result: SearchResult.Error) {
        progressBar.visibility = View.GONE
        
        val query = searchEditText.text?.toString().orEmpty()
        if (query.isNotEmpty()) {
            lifecycleScope.launch {
                try {
                    val localWord = wordViewModel.getWord(query)
                    if (localWord != null) {
                        errorTextView.visibility = View.GONE
                        displayWordDetails(localWord)
                    } else {
                        showError("Word not found")
                    }
                } catch (e: Exception) {
                    showError("Error: ${e.message}")
                }
            }
        }
    }

    private fun createWordFromResponse(wordResponse: com.example.mydictionary.model.DictionaryResponse): Word {
        val detailedMeaning = buildDetailedMeaning(wordResponse)
        return Word(
            word = wordResponse.word,
            phonetic = (wordResponse.phonetic ?: wordResponse.phonetics.firstOrNull()?.text ?: "").trim(),
            meaning = detailedMeaning.toString().trim(),
            example = findFirstExample(wordResponse),
            isOffline = false
        )
    }

    private fun buildDetailedMeaning(wordResponse: com.example.mydictionary.model.DictionaryResponse): StringBuilder {
        return StringBuilder().apply {
            wordResponse.meanings.forEach { meaning ->
                append("${meaning.partOfSpeech?.replaceFirstChar { it.uppercase() } ?: "Unknown"}\n")
                
                meaning.definitions.forEachIndexed { index, definition ->
                    append("${index + 1}. ${definition.definition}\n")
                    
                    if (!definition.synonyms.isNullOrEmpty()) {
                        append("Synonyms: ${definition.synonyms.take(5).joinToString(", ")}\n")
                    }
                    if (!definition.antonyms.isNullOrEmpty()) {
                        append("Antonyms: ${definition.antonyms.take(5).joinToString(", ")}\n")
                    }
                    append("\n")
                }
            }
        }
    }

    private fun findFirstExample(wordResponse: com.example.mydictionary.model.DictionaryResponse): String {
        return wordResponse.meanings
            .flatMap { it.definitions }
            .firstOrNull { !it.example.isNullOrEmpty() }
            ?.example ?: ""
    }

    private fun setupSearch() {
        searchButton.setOnClickListener {
            performSearch()
        }

        searchEditText.setOnEditorActionListener { _, _, _ ->
            performSearch()
            true
        }
    }

    private fun setupButtons() {
        readAloudButton.setOnClickListener {
            if (currentWordDetails.isNotEmpty()) {
                textToSpeech.speak(currentWordDetails, TextToSpeech.QUEUE_FLUSH, null, null)
                readAloudButton.visibility = View.GONE
                pauseButton.visibility = View.VISIBLE
                playButton.visibility = View.GONE
            }
        }

        pauseButton.setOnClickListener {
            if (textToSpeech.isSpeaking) {
                textToSpeech.stop()
                pauseButton.visibility = View.GONE
                playButton.visibility = View.VISIBLE
            }
        }

        playButton.setOnClickListener {
            if (currentWordDetails.isNotEmpty()) {
                textToSpeech.speak(currentWordDetails, TextToSpeech.QUEUE_FLUSH, null, null)
                playButton.visibility = View.GONE
                pauseButton.visibility = View.VISIBLE
            }
        }

        logoutButton.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun performSearch() {
        val query = searchEditText.text?.toString().orEmpty()
        
        if (query.isEmpty()) {
            Toast.makeText(this, "Please enter a word to search", Toast.LENGTH_SHORT).show()
            return
        }

        dictionaryViewModel.searchWord(query)
    }

    private fun displayWordDetails(word: Word) {
        wordDetailsLayout.visibility = View.VISIBLE
        wordTextView.text = word.word
        phoneticTextView.apply {
            text = word.phonetic
            visibility = if (!word.phonetic.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
        meaningTextView.text = word.meaning
        exampleTextView.apply {
            text = word.example
            visibility = if (!word.example.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
        readAloudButton.visibility = View.VISIBLE
        pauseButton.visibility = View.GONE
        playButton.visibility = View.GONE
    }

    private fun hideWordDetails() {
        wordDetailsLayout.visibility = View.GONE
        readAloudButton.visibility = View.GONE
        pauseButton.visibility = View.GONE
        playButton.visibility = View.GONE
    }

    private fun showError(message: String) {
        hideWordDetails()
        errorTextView.text = message
        errorTextView.visibility = View.VISIBLE
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.language = Locale.US
        } else {
            Toast.makeText(this, "Text-to-Speech initialization failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buildReadableText(word: Word): String {
        return buildString {
            append("Word: ${word.word}. ")
            if (!word.phonetic.isNullOrEmpty()) {
                append("Pronounced as: ${word.phonetic}. ")
            }
            append(word.meaning)
            if (!word.example.isNullOrEmpty()) {
                append(" Example: ${word.example}")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            searchJob?.cancel()
            if (::textToSpeech.isInitialized) {
                if (textToSpeech.isSpeaking) {
                    textToSpeech.stop()
                }
                textToSpeech.shutdown()
            }
        } catch (e: Exception) {
            // Ignore any cancellation exceptions during cleanup
        }
    }
} 