# MoneyTracker

## Project Architecture

This project is built upon the principles of **Clean Architecture**, a software design philosophy that separates concerns, making the codebase clean, maintainable, testable, and scalable.

The architecture is divided into three main layers: `domain`, `data`, and `presentation`.

```
app/
└── src/
    └── main/
        └── java/
            └── com/
                └── example/
                    └── moneytracker/
                        ├── data/          # Data Layer
                        ├── di/            # Dependency Injection
                        ├── domain/        # Domain Layer (Business Logic)
                        ├── presentation/  # Presentation Layer (UI)
                        ├── ui/            # UI Elements (Compose)
                        └── util/          # Common Utilities
```

### 1. `domain` - The Core Layer

This is the central and most independent layer of the application, containing all the business logic. This layer is completely independent of any other layer.

*   **`entities` or `model`**: Represents the core business objects (e.g., `Transaction`, `Category`).
*   **`repository` (Interfaces)**: Defines the contracts for how data should be fetched and stored. The `data` layer is responsible for implementing these interfaces.
*   **`use_case` or `interactor`**: Each use case represents a specific task or business scenario (e.g., `AddTransactionUseCase`, `GetDailyTransactionsUseCase`). They orchestrate the flow of data from repositories to the `presentation` layer.

### 2. `data` - The Data Layer

This layer is responsible for providing data to the `domain` layer. It implements the repository interfaces defined in the `domain` layer.

*   **`repository` (Implementations)**: Concrete implementations of the repository interfaces. This is where the decision to fetch data from a local database, a remote API, or a cache is made.
*   **`local`**: Contains components for accessing local data, such as the Room Database, DAOs (Data Access Objects), or SharedPreferences.
*   **`remote`**: Contains components for accessing remote data, such as Retrofit service interfaces for API calls.
*   **`mapper`**: Classes used to map between data models of the `data` layer (e.g., DTOs from an API) and the models of the `domain` layer (entities).

### 3. `presentation` - The UI Layer

This is the outermost layer, responsible for displaying the user interface (UI) and handling user interactions.

*   **`viewmodel`**: Connects the `domain` layer (via use cases) to the UI. The ViewModel holds and manages the UI state, allowing data to survive configuration changes (e.g., screen rotation).
*   **`screen` or `fragment`/`activity`**: Contains Composable functions (in Jetpack Compose) or Fragments/Activities to build the interface. This layer simply "listens" to data from the ViewModel to display it and notifies the ViewModel of user-generated events.

### Other Directories

*   **`di` (Dependency Injection)**: Contains Hilt (or Dagger) modules to provide dependencies for the entire application, reducing hard dependencies and increasing modularity.
*   **`ui`**: Contains common UI elements such as `theme`, `component` (reusable Composables), and `navigation`.
*   **`util`**: Contains utility classes, constants, or extension functions used in multiple places throughout the application.

## Business Logic

The core business logic for financial calculations is handled through SQL queries within the `TransactionDao`.

### 1. Calculating Total Income or Expense (`getTotalAmountByTypeAndDateRange`)

*   The application calculates the total amount for a specific transaction type (`income` or `expense`).
*   This calculation is performed within a specified date range (`startDate` and `endDate`).

### 2. Monthly Statistics (`getMonthlyStatistics`)

*   The application groups all transactions by month and year.
*   For each month, it calculates the total income by summing up all transactions of type `income`.
*   Simultaneously, it calculates the total expense by summing up all transactions of type `expense`.
*   The result is a list of objects, where each object contains the month, total income, and total expense for that month.
