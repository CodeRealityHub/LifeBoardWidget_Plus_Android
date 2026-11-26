# LifeBoard (reconstructed recovery build)

This project reconstructs the pre-Domain/UseCase Entity-based implementation from source files previously shared in the conversation. It retains the original package `com.example.lifeboardapp`, Compose screens, Room entities/DAOs, repositories, ViewModels, and manual `AppContainer`.

## Open
Open this folder in Android Studio and allow Gradle sync. Use JDK 17 and install Android SDK 35.

## Important recovery notes
- The project was reconstructed from individually uploaded source files, not from a complete original project archive.
- The original Gradle files, wrapper, and some resource files were not available; the Gradle configuration and basic resources were recreated.
- The old Entity-based repository flow is retained; Domain models and the partially migrated UseCases are intentionally excluded.
- No claim is made that this project has passed an Android build. Run `./gradlew :app:assembleDebug` after Gradle sync and address any environment-specific errors.
- Database schema is set to version 1 to match the recovered entity definitions. Do not uninstall an existing app or clear its data if you need to preserve its Room database.
