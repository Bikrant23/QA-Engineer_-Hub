# Test Scenarios

| ID | Scenario | Covered test class(es) | Coverage |
|---|---|---|---|
| TS-LOGIN-01 | Authenticate a test user | `LoginPageTest` | Verify a user can submit the configured credentials and reach an authenticated view. |
| TS-EVENT-01 | Create an event through Admin | `CreateEventTest` | Verify navigation to event management, valid event entry, and persistence in the Admin events table. |
| TS-BOOK-01 | Book tickets and update inventory | `BookTicketsTest` | Verify a valid five-ticket booking decreases available seats by the requested count. |
| TS-BOOK-02 | Reject invalid booking contact details | `BookTicketsTest` | Verify invalid email and phone details produce validation feedback without changing inventory. |
| TS-DETAIL-01 | Verify booking information end to end | `BookingDetailsTest` | Compare booking reference/status and event, customer, and payment details with the booking input data. |
| TS-DETAIL-02 | Cancel a booking and restore seats | `BookingDetailsTest` | Confirm a dismissed cancellation leaves the booking confirmed; confirm cancellation removes the record and restores availability. |
| TS-DETAIL-03 | Clear all bookings with both confirmation outcomes | `BookingDetailsTest` | Verify dismissing the browser confirmation preserves bookings and accepting it displays the empty-bookings state. |
| TS-ADMIN-01 | Delete created events with cancel and confirm outcomes | `ManageEventsByAdminTest` | Verify cancel preserves each event, while confirmation removes it from Admin and public listings. |

Screenshots for the executed scenarios are stored in `target/screenshots/`, separate from these scenario descriptions and from `Test-Cases.md`.
