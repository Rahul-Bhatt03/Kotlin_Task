# Project Architecture

The SOM Customer Service Booking app follows the **Clean Architecture** principles and **MVVM (Model-View-ViewModel)** pattern.

## Layers

1.  **UI Layer**: Contains Compose screens and ViewModels.
2.  **Domain Layer (implied)**: Currently represented by the `model` package and Repository interfaces.
3.  **Data Layer**: Contains Repository implementations and API abstractions (`ApiService`).

## Data Flow

`ApiService` (Mock/Remote) -> `Repository` -> `ViewModel` -> `UI (Compose)`
