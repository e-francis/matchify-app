# Matchify - Dating App

## Overview
Matchify is a fictitous dating application built with Kotlin, following Material Design principles and implementing MVVM architecture. The app features user authentication, profile creation, and a responsive.

## Technologies & Libraries
- Kotlin
- Kotlin Coroutines & Flow
- Retrofit for API communication
- Dagger Hilt for dependency injection
- Material Design 3
- ViewPager2 for carousel
- Navigation Component
- ViewModel & LiveData
- Shared prferences for local storage

## Project Structure
```
app/
├── build.gradle
├── src/
    ├── main/
    │   ├── java/com/matchify/
    │   │   ├── data/
    │   │   │   ├── api/
    │   │   │   ├── models/
    │   │   │   └── repositories/
    │   │   ├── di/
    │   │   |
    │   │   ├── presentation/
    │   │   ├── utils/
    │   │   └── MainActivity.kt
    │   └── res/
    └── test/
```

## Setup Instructions

### Prerequisites
- Android Studio
- JDK 21
- Android SDK 34
- Gradle 8.12

### Getting Started
1. Clone the repository:
```bash
git clone https://github.com/e-francis/matchify.git
```

2. Open Android Studio and select "Open an existing Android Studio project"

3. Navigate to the cloned directory and click "OK"

4. Let Android Studio sync the project with Gradle files

### Building the Project
- To build the debug APK:
```bash
./gradlew assembleDebug
```
- To build the release APK:
```bash
./gradlew assembleRelease
```

The generated APKs will be located in `app/build/outputs/apk/`

### Running the App
1. Connect an Android device or start an emulator
2. Click the "Run" button (green play button) in Android Studio
3. Select your target device and click "OK"


## Key Features

1. Authentication
   - Secure login system
   - Input validation
   - Error handling

3. Profile Creation
   - Image upload with preview
   - Image upload 
   - Interest selection
   - Location selection with dropdown

4. Security Features
   - Base64 image conversion
   - Secure password handling
   - Input validation
   - Error handling

## Code Style & Architecture
- MVVM Architecture
- Clean Architecture principles
- Dependency Injection

## API Integration
The app integrates with the following endpoints:
- Login: `https://macthify-be.vercel.app/v1/api/auth/login`
- Profile Creation: `https://macthify-be.vercel.app/v1/api/create-profile`

## License
This project is licensed under the MIT License - see the LICENSE.md file for details
