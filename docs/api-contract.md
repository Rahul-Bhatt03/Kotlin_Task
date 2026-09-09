# API Contract

This document defines the communication contract between the app and the service layer. The app uses the `ApiResult` sealed interface to handle these responses uniformly.

## Endpoints

### 1. Get Services
- **Method**: `GET /services`
- **Query Params**: `query: String?` (optional search term)
- **Response**: `ApiResult.Success<List<Service>>`

### 2. Get Service Details
- **Method**: `GET /services/{serviceId}`
- **Response**: `ApiResult.Success<Service>` or `ApiResult.ServerError` (if ID not found)

### 3. Get Availability
- **Method**: `GET /availability`
- **Query Params**: `serviceId: String`, `date: String` (ISO_LOCAL_DATE)
- **Response**: `ApiResult.Success<List<AvailabilitySlot>>`

### 4. Create Booking
- **Method**: `POST /bookings`
- **Body**: `BookingRequest`
- **Response**: 
    - `ApiResult.Success<Booking>`: Booking confirmed.
    - `ApiResult.ValidationError`: Client-side data failed server validation (e.g., empty name).
    - `ApiResult.Conflict`: The slot was taken by another user during the process.
    - `ApiResult.ServerError`: General server failure.

### 5. Get Bookings
- **Method**: `GET /bookings`
- **Response**: `ApiResult.Success<List<Booking>>`

## Status & Error Handling

- **Success**: Data returned successfully.
- **Validation Errors**: Returns a `Map<String, String>` where keys are field names and values are error messages.
- **Business Errors (Conflict)**: Specifically handled when a race condition occurs for a time slot.
- **Server Errors**: Generic failure message for infrastructure or unexpected issues.
