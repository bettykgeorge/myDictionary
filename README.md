# 📖 MyDictionary — Android Dictionary App

![Kotlin](https://img.shields.io/badge/Kotlin-100%25-7F52FF?logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-Studio-3DDC84?logo=android&logoColor=white)
![Hilt](https://img.shields.io/badge/Hilt-DI-FF6F00?logo=google&logoColor=white)
![API](https://img.shields.io/badge/Dictionary-API-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

A clean, fast **Android dictionary app** built with **Kotlin** and **Hilt** that lets users look up any word instantly — get definitions, synonyms, antonyms, and pronunciation all in one place. Designed to make vocabulary learning quick and accessible.

---

## 📌 Overview

MyDictionary is a native Android application that integrates with a Dictionary API to deliver real-time word lookups. Type any word and instantly get its meaning, part of speech, example sentences, and synonyms — no internet connection delays, no clutter, just the information you need.

---

## 🎯 Features

- 🔍 **Word Search** — look up any word instantly via Dictionary API
- 📝 **Definitions** — detailed meanings with part of speech labels
- 🔄 **Synonyms & Antonyms** — expand your vocabulary with related words
- 🔊 **Pronunciation** — phonetic spelling for every word
- 💉 **Hilt Dependency Injection** — clean, scalable architecture
- ⚡ **Fast & Lightweight** — native Kotlin for smooth performance

---

## 🏗️ Architecture

```
UI Layer (Activities / Fragments)
        ↓
ViewModel (state & logic)
        ↓
Repository (data coordination)
        ↓
Retrofit API Service (Dictionary API)
        ↓
Hilt (dependency injection across all layers)
```

---

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Kotlin | Primary programming language |
| Android Studio | IDE & project management |
| Hilt (2.48) | Dependency injection |
| Retrofit | HTTP client for API calls |
| Dictionary API | Word definitions, synonyms, phonetics |
| Gradle KTS | Build configuration (Kotlin DSL) |
| ViewModel + LiveData | UI state management |

---

## 🗂️ Project Structure

```
myDictionary/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/.../mydictionary/
│   │   │   │   ├── di/             # Hilt modules
│   │   │   │   ├── api/            # Retrofit API service
│   │   │   │   ├── model/          # Data models
│   │   │   │   ├── repository/     # Data repository
│   │   │   │   ├── viewmodel/      # ViewModels
│   │   │   │   └── ui/             # Activities & Fragments
│   │   │   └── res/                # Layouts, drawables, strings
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
│
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── README.md
```

---

## ⚙️ Getting Started

### Prerequisites

- Android Studio (Hedgehog or later recommended)
- Android SDK (API level 24+)
- Internet connection (for API calls)

### 1️⃣ Clone the repository

```bash
git clone https://github.com/bettykgeorge/myDictionary.git
```

### 2️⃣ Open in Android Studio

- Open Android Studio → **File → Open** → select the cloned folder
- Let Gradle sync complete

### 3️⃣ Run the app

- Connect an Android device or start an emulator
- Click **Run ▶** or press `Shift+F10`

> No API key needed — the app uses the free [DictionaryAPI.dev](https://dictionaryapi.dev/) which requires no authentication.

---

## 💬 Example Usage

```
User searches: "ephemeral"

Result:
📝 Definition: lasting for a very short time
🏷️  Part of speech: adjective
🔊 Phonetic: /ɪˈfem(ə)r(ə)l/
🔄 Synonyms: transitory, fleeting, momentary, brief
```

---

## 🖥️ Screenshots

<img width="385" height="863" alt="Screenshot 2026-05-29 181154" src="https://github.com/user-attachments/assets/0f13a00c-90b1-41ce-9b65-1db3dc68cc29" />
<img width="393" height="869" alt="Screenshot 2026-05-29 181247" src="https://github.com/user-attachments/assets/1a2e7863-cd9d-41b3-a395-84efb1b9fc8f" />
<img width="376" height="851" alt="Screenshot 2026-05-29 190732" src="https://github.com/user-attachments/assets/53bca1f2-f911-4c1e-97e6-0180ce99127c" />
<img width="382" height="775" alt="Screenshot 2026-05-29 190808" src="https://github.com/user-attachments/assets/da279cf4-5c3d-41b8-af7c-4d4694dd386a" />


## 🔮 Future Improvements

- [ ] Word history — save previously searched words
- [ ] Offline mode with local database (Room)
- [ ] Flashcard mode for vocabulary practice
- [ ] Dark mode support
- [ ] Text-to-speech for word pronunciation

---

## 🧠 Key Concepts Demonstrated

- Native Android development with Kotlin
- Dependency injection using Hilt
- REST API integration with Retrofit
- MVVM architecture (ViewModel + LiveData)
- Kotlin DSL for Gradle build configuration
- Clean separation of concerns across layers

---

## 👩‍💻 Author

**Betty K George**

[![GitHub](https://img.shields.io/badge/GitHub-bettykgeorge-181717?logo=github)](https://github.com/bettykgeorge)

---

## 📌 License

This project is for academic and learning purposes.
