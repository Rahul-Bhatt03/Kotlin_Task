# Architecture & Technical Decisions

## 1. Sealed Interface for API Results (`ApiResult`)

- **Decision**: Use a `sealed interface ApiResult<out T>` to wrap all service responses.
- **Why**: Standard `Result` or `Either` types often only cover Success/Failure. By using a custom sealed interface, we can explicitly represent domain-specific outcomes like `ValidationError` (with a map of fields) and `Conflict` (for concurrency).
- **Alternatives Considered**: Using standard `kotlin.Result` with exceptions for different errors.
- **Why Alternatives were not selected**: Exceptions are expensive and less readable for business-logic flows. Sealed interfaces force the developer to handle all possible outcomes at compile-time.

## 2. Dependency Injection via `AppContainer`

- **Decision**: Use a manual `AppContainer` object for dependency management instead of Hilt or Dagger.
- **Why**: For a small to medium-sized project or a technical assignment, Hilt/Dagger adds significant boilerplate and build-time overhead. Manual DI is transparent and easier to debug for this scope.
- **Alternatives Considered**: Hilt.
- **Why Alternatives were not selected**: Hilt was explicitly prohibited in the assignment requirements to keep the architecture simple.

## 3. Generic UI State Wrapper (`UiState`)

- **Decision**: Use a generic `UiState<out T>` sealed interface for handling screen loading, success, and error states.
- **Why**: This provides a consistent way to handle common UI scenarios (showing a progress bar, an error message, or an empty view) across all screens, reducing code duplication in Compose functions.
- **Alternatives Considered**: Managing `isLoading`, `errorMessage`, and `data` as separate `StateFlow` variables in each ViewModel.
- **Why Alternatives were not selected**: Separate variables lead to "illegal states" (e.g., `isLoading = true` AND `data != null` simultaneously). The sealed interface ensures the UI is always in exactly one valid state.
