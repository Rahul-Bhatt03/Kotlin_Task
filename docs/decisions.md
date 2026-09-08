# Architecture Decisions

1.  **Package Structure**: Organized by layer (model, data, ui, viewmodel, navigation) to keep the project clean and navigable as it grows.
2.  **API Abstraction**: Used an interface `ApiService` to allow switching between a Mock implementation (during development) and a real Network implementation (later).
3.  **Repository Pattern**: Encapsulates the data origin and provides a clean API to the ViewModels. Uses `Result` type to handle success and failure cases.
4.  **Date/Time**: Used `java.time.LocalDateTime` for modern date/time handling (requires minSdk 26).
