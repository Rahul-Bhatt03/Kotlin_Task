# Project Architecture

The SOM Customer Service Booking app follows **Clean Architecture** principles and the **MVVM (Model-View-ViewModel)** design pattern. This ensures a clear separation of concerns, making the app testable and easy to maintain.

## Folder & Package Structure

- **`com.example.somcustomerbooking.model`**: Contains pure data classes (Domain Models) representing the core business entities: `Service`, `AvailabilitySlot`, and `Booking`.
- **`com.example.somcustomerbooking.data`**:
    - **`api`**: Contains the `ApiService` interface, defining the contract for data operations.
    - **`mock`**: Contains `MockApiService` and `MockData` for simulated backend behavior.
    - **`repository`**: Contains `BookingRepository` (Interface) and `BookingRepositoryImpl` (Implementation), which acts as the single source of truth for the UI layer.
- **`com.example.somcustomerbooking.viewmodel`**: ViewModels that hold screen-level state and handle business logic. Includes `ViewModelFactory` for dependency injection.
- **`com.example.somcustomerbooking.ui`**:
    - **`screens`**: Jetpack Compose functions representing entire screens.
    - **`components`**: Reusable UI widgets like `LoadingView`, `ErrorView`, and `EmptyView`.
- **`com.example.somcustomerbooking.navigation`**: Contains the `NavGraph` and `Screen` definitions using Jetpack Compose Navigation.

## Responsibilities

- **Data Layer**: Responsible for fetching data from an external source (Mock API) and mapping it to Domain Models.
- **Repository**: Abstracts the data source. The UI doesn't know if data comes from a local mock or a real server.
- **ViewModel**: Transforms data from the repository into UI state. It handles user interactions and survives configuration changes.
- **UI (Compose)**: A passive layer that observes the state from the ViewModel and renders the interface.

## State Management

The app uses **Unidirectional Data Flow (UDF)**:
1.  **ViewModel** exposes state via `StateFlow<UiState<T>>` or specialized UI state classes (e.g., `BookingUiState`).
2.  **Compose UI** observes this state using `collectAsStateWithLifecycle()`.
3.  User actions are sent as events back to the ViewModel (e.g., `viewModel.onQueryChanged(query)`).

## API Boundary & Mock Replacement

The boundary is defined by the `ApiService` interface. Currently, `AppContainer` initializes the repository with `MockApiService`.

To replace the mock with a real network client (e.g., Retrofit):
1.  Create `RetrofitApiService` implementing `ApiService`.
2.  In `AppContainer.kt`, change the `apiService` initialization to use the new implementation.
3.  No changes are required in the Repositories, ViewModels, or UI.
