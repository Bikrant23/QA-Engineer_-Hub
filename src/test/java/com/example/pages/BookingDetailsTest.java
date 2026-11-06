package com.example.pages;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingDetailsTest {
    private static final String CUSTOMER_NAME = "QA Ticket Buyer";
    private static final String CUSTOMER_EMAIL = "qa.ticket.buyer@example.com";
    private static final String CUSTOMER_PHONE = "+91 98765 43210";
    private static final int TICKET_COUNT = 5;
    private static final String CONFIRMED = "confirmed";

    private WebDriver driver;
    private TestEventFactory.TestEvent testEvent;

    @BeforeEach
    void setUp() {
//      driver = new ChromeDriver(new ChromeOptions());
        ChromeOptions options = new ChromeOptions();

        if ("true".equalsIgnoreCase(System.getenv("CI"))) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }

        driver = new ChromeDriver(options);

        LoginPage loginPage = new LoginPage(driver)
                .open()
                .enterEmail(BasePage.EMAIL)
                .enterPassword(BasePage.PASSWORD);
        loginPage.submit();
        assertTrue(loginPage.isLoggedIn(), "Login should reach the dashboard");
        testEvent = TestEventFactory.create(driver, "QA Booking Event ");
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void verifiesBookingDetailsAndCancellationFlow() throws IOException {
        BookingDetails bookingDetails = new BookingDetails(driver).openMyBookings();
        Set<String> previousReferences =
                new HashSet<>(bookingDetails.getBookingReferencesForEvent(testEvent.title()));

        driver.get(testEvent.url());
        BookTickets bookTickets = new BookTickets(driver);
        int seatsBeforeBooking = bookTickets.getAvailableSeats();
        bookTickets.selectTicketCount(TICKET_COUNT)
                .enterFullName(CUSTOMER_NAME)
                .enterEmail(CUSTOMER_EMAIL)
                .enterPhoneNumber(CUSTOMER_PHONE)
                .confirmBooking();

        driver.get(BasePage.BASE_URL + "bookings");
        bookingDetails.waitForMyBookingsToLoad();
        String bookingReference = new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ignored -> bookingDetails.getBookingReferencesForEvent(testEvent.title()).stream()
                        .filter(reference -> !previousReferences.contains(reference))
                        .findFirst()
                        .orElse(null));

        assertEquals(CONFIRMED, bookingDetails.getBookingStatusInList(bookingReference),
                "The new booking should be confirmed in My Bookings");
        String bookingId = bookingDetails.getBookingIdInList(bookingReference);
        saveScreenshot("booking-details-01-confirmed-in-my-bookings.png");

        bookingDetails.openDetailsForBooking(bookingReference);
        assertEquals(bookingReference, bookingDetails.getBookingReferenceInDetails(),
                "Booking reference should match My Bookings");
        assertEquals(CONFIRMED, bookingDetails.getBookingStatusInDetails(),
                "Booking status should match My Bookings");
        assertEquals(bookingId, bookingDetails.getBookingIdInDetails(),
                "Booking ID should match My Bookings");

        assertEquals(testEvent.title(), bookingDetails.getDetailValue("Event Details", "Event"));
        assertEquals("Conference", bookingDetails.getDetailValue("Event Details", "Category"));
        assertEquals(testEvent.date(),
                bookingDetails.getDetailValue("Event Details", "Date"));
        assertEquals("Bangalore International Exhibition Centre, Bengaluru",
                bookingDetails.getDetailValue("Event Details", "Venue"));
        assertEquals("Bangalore", bookingDetails.getDetailValue("Event Details", "City"));

        assertEquals(CUSTOMER_NAME, bookingDetails.getDetailValue("Customer Details", "Name"));
        assertEquals(CUSTOMER_EMAIL, bookingDetails.getDetailValue("Customer Details", "Email"));
        assertEquals(CUSTOMER_PHONE, bookingDetails.getDetailValue("Customer Details", "Phone"));

        assertEquals(String.valueOf(TICKET_COUNT),
                bookingDetails.getDetailValue("Payment Summary", "Tickets"));
        assertEquals(testEvent.pricePerTicket(),
                bookingDetails.getDetailValue("Payment Summary", "Price per ticket"));
        assertEquals(testEvent.totalPrice(), bookingDetails.getDetailValue("Payment Summary", "Total Paid"));
        saveScreenshot("booking-details-02-verified-detail-sections.png");

        bookingDetails.openCancelBookingDialog();
        assertTrue(bookingDetails.isCancelDialogDisplayed(), "Cancel confirmation dialog should be displayed");
        saveScreenshot("booking-details-03-cancel-dialog.png");
        bookingDetails.dismissCancelBookingDialog();
        assertFalse(bookingDetails.isCancelDialogDisplayed(), "Cancel should dismiss the dialog");
        assertEquals(CONFIRMED, bookingDetails.getBookingStatusInDetails(),
                "Dismissing the dialog should leave the booking confirmed");
        saveScreenshot("booking-details-04-cancel-dismissed-still-confirmed.png");

        driver.get(testEvent.url());
        int seatsBeforeCancellation = bookTickets.getAvailableSeats();
        assertEquals(seatsBeforeBooking - TICKET_COUNT, seatsBeforeCancellation,
                "Dismissing cancellation must not restore seats");
        driver.get(BasePage.BASE_URL + "bookings/" + bookingId.substring(1));
        bookingDetails.openCancelBookingDialog();
        bookingDetails.confirmCancellation();
        saveScreenshot("booking-details-05-after-confirming-cancellation.png");

        driver.get(BasePage.BASE_URL + "bookings");
        bookingDetails.waitForMyBookingsToLoad();
        bookingDetails.waitForBookingToDisappearFromList(bookingReference);
        assertFalse(bookingDetails.isBookingListed(bookingReference),
                "Cancelled booking should disappear from My Bookings");
        saveScreenshot("booking-details-06-after-cancellation-my-bookings.png");

        driver.get(BasePage.BASE_URL + "bookings/" + bookingId.substring(1));
        bookingDetails.waitForBookingDetailsToDisappear();
        assertTrue(bookingDetails.isBookingNotFound(),
                "Cancelled booking details should no longer be accessible");
        saveScreenshot("booking-details-07-after-cancellation-details.png");

        driver.get(testEvent.url());
        int seatsAfterCancellation = bookTickets.getAvailableSeats();
        assertEquals(seatsBeforeBooking, seatsAfterCancellation,
                "Cancelling the booking should restore all five seats");
        saveScreenshot("booking-details-08-seats-restored.png");

        driver.get(testEvent.url());
        bookTickets.selectTicketCount(1)
                .enterFullName(CUSTOMER_NAME)
                .enterEmail(CUSTOMER_EMAIL)
                .enterPhoneNumber(CUSTOMER_PHONE)
                .confirmBooking();
        bookTickets.waitForBookingAttemptCompletion();

        bookingDetails.openMyBookings();
        bookingDetails.waitForAtLeastOneBooking();
        Set<String> bookingsBeforeClear = new HashSet<>(bookingDetails.getAllBookingReferences());
        assertFalse(bookingsBeforeClear.isEmpty(), "At least one booking should exist before clearing");
        bookingDetails.clickClearAllBookings();
        assertTrue(bookingDetails.getClearBookingsConfirmationText().contains("Clear all your bookings?"),
                "Clear all confirmation should be shown");
        bookingDetails.dismissClearBookingsConfirmation();
        assertEquals(bookingsBeforeClear, new HashSet<>(bookingDetails.getAllBookingReferences()),
                "Dismissing clear confirmation should keep all bookings listed");
        String retainedReference = bookingsBeforeClear.iterator().next();
        bookingDetails.openDetailsForBooking(retainedReference);
        assertEquals(retainedReference, bookingDetails.getBookingReferenceInDetails(),
                "Booking details should remain accessible after dismissing clear confirmation");
        saveScreenshot("booking-details-09-clear-dismissed-details-remain.png");

        bookingDetails.openMyBookings()
                .clickClearAllBookings();
        assertTrue(bookingDetails.getClearBookingsConfirmationText().contains("Clear all your bookings?"),
                "Clear all confirmation should be shown before clearing");
        bookingDetails.acceptClearBookingsConfirmation();
        bookingDetails.waitForNoBookingsMessage();
        assertTrue(bookingDetails.isNoBookingsMessageDisplayed(),
                "My Bookings should display the empty state after clearing");
        assertTrue(bookingDetails.isBrowseEventsButtonDisplayed(),
                "The empty state should provide a Browse Events button");
        assertTrue(bookingDetails.getAllBookingReferences().isEmpty(),
                "All booking cards should be removed after confirming clear all");
        saveScreenshot("booking-details-10-all-bookings-cleared.png");
    }

    private void saveScreenshot(String fileName) throws IOException {
        Path screenshot = Path.of("target", "screenshots", fileName);
        Files.createDirectories(screenshot.getParent());
        Files.copy(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE).toPath(),
                screenshot, StandardCopyOption.REPLACE_EXISTING);
    }
}
