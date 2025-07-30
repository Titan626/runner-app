# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Runner** - An Android application for generating personalized jogging routes using AI-powered descriptions and Google Maps integration. Built with Kotlin and Jetpack Compose, targeting modern Android development practices.

- **Package**: `Runner` (placeholder - should be renamed to `com.runner.app` or similar)
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36 (latest)
- **Architecture**: Single Activity with Jetpack Compose UI

## Build Commands

```bash
# Build the application
./gradlew build

# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew Runner

# Run instrumentation tests (requires device/emulator)
./gradlew connectedAndroidTest

# Clean build
./gradlew clean
```

## Project Structure

```
app/src/main/
├── java/com/example/test/
│   ├── MainActivity.kt          # Main entry point with Compose UI
│   └── ui/theme/               # Material 3 theming (Color, Theme, Type)
├── AndroidManifest.xml         # App configuration and permissions
└── res/                       # Resources (strings, colors, drawables)

app/src/test/                  # Unit tests (JUnit)
app/src/androidTest/           # Instrumentation tests (Espresso + Compose)
```

## Technology Stack

- **Language**: Kotlin 2.0.21
- **UI Framework**: Jetpack Compose with Material 3
- **Build System**: Gradle with Kotlin DSL and Version Catalog (`gradle/libs.versions.toml`)
- **Testing**: JUnit 4 (unit), AndroidX Test + Espresso (instrumentation)

## Planned Integrations (Per PRD)

- **Firebase**: Auth, Firestore, Cloud Functions, Storage, Analytics
- **Google Maps SDK**: Route visualization and display
- **Google Directions API**: Route generation
- **Claude API**: AI-powered route descriptions and labeling
- **Location Services**: GPS-based route starting points

## Architecture Patterns to Implement

Based on the PRD requirements, implement:

1. **MVVM/Clean Architecture**: Repository pattern with use cases
2. **Dependency Injection**: Likely Hilt for Firebase and API clients
3. **State Management**: Compose state with ViewModels
4. **Navigation**: Compose Navigation for multi-screen flow
5. **Data Layer**: Room (local) + Firestore (remote) with offline support

## Key Features to Develop

- Distance input and route generation (2-3 alternatives per request)
- Loop routes starting/ending at current location
- Claude AI route summaries and labels
- Map visualization with polylines
- Route filtering (flattest, shortest, scenic)
- Favorite route persistence
- Firebase Authentication
- Route caching (24 hours per PRD)

## UI Design Style & References

**Design Direction**: Athletic-focused, clean interface inspired by Nike Run Club
- **UI References**: 296 Nike Run Club iOS screenshots in `Ui Reference/` folder
- **Color Palette**: Black/white primary with orange/red accents
- **Typography**: Urbanist font family with bold headers, clean body text, high contrast, using Tailwind CSS sizing convention
- **Components**: Rounded buttons, minimal cards, full-screen maps

**Current Theme Files**:
- `app/src/main/java/com/example/test/ui/theme/Color.kt` - Needs update from Material purple to athletic palette
- `app/src/main/java/com/example/test/ui/theme/Type.kt` - Implement Urbanist font with Tailwind sizing (text-xs: 12sp, text-sm: 14sp, text-base: 16sp, text-lg: 18sp, text-xl: 20sp, text-2xl: 24sp, text-3xl: 30sp, text-4xl: 36sp, text-5xl: 48sp)
- `app/src/main/java/com/example/test/ui/theme/Theme.kt` - Implement dark/light variants

**Key UI Patterns to Implement**:
- Card-based route selection with AI summaries
- Full-screen map with overlay route information
- Bottom tab navigation (Home, Run, Profile, Settings)
- Bold CTAs and prominent distance input

## Development Notes

- Current codebase is a fresh scaffold - minimal implementation exists
- All major features need to be built from scratch
- Follow Nike Run Club design patterns from UI reference screenshots
- Package structure will need expansion for features (data, domain, presentation layers)
- API keys and configuration will need secure handling
- Implement proper error handling for network calls and location services

## Testing Strategy

- Unit tests for business logic and repositories
- Compose UI tests for user interactions
- Integration tests for Firebase and API connections
- Location and maps testing will require emulator/device testing