package com.example.pages;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookTicketsTest {
    private static final String EVENT_URL = BasePage.BASE_URL + "events/33402";
    private static final int BOOKING_TICKET_COUNT = 5;

    private WebDriver driver;

    @BeforeEach
    void setUp() {
//        driver = new ChromeDriver(new ChromeOptions());

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
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void booksFiveTicketsAndVerifiesSeatCountDecreases() throws IOException {
        driver.get(EVENT_URL);
        BookTickets bookTickets = new BookTickets(driver);
        int seatsBeforeBooking = bookTickets.getAvailableSeats();

        bookTickets.selectTicketCount(BOOKING_TICKET_COUNT)
                .enterFullName("QA Ticket Buyer")
                .enterEmail("qa.ticket.buyer@example.com")
                .enterPhoneNumber("+91 98765 43210");
        assertEquals("5", bookTickets.getTicketCount(), "Five tickets should be selected");

        bookTickets.confirmBooking();
        bookTickets.waitForBookingAttemptCompletion();

        driver.get(EVENT_URL);
        int seatsAfterBooking = new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ignored -> {
                    int currentSeats = bookTickets.getAvailableSeats();
                    return currentSeats == seatsBeforeBooking - BOOKING_TICKET_COUNT
                            ? currentSeats : null;
                });
        assertEquals(seatsBeforeBooking - BOOKING_TICKET_COUNT, seatsAfterBooking,
                "Booking five tickets should reduce available seats by five");
        saveScreenshot("book-tickets-success.png");
    }

    @Test
    void rejectsInvalidEmailAndPhoneWithoutReducingSeats() throws IOException {
        driver.get(EVENT_URL);
        BookTickets bookTickets = new BookTickets(driver);
        int seatsBeforeInvalidAttempt = bookTickets.getAvailableSeats();
        bookTickets.selectTicketCount(BOOKING_TICKET_COUNT)
                .enterFullName("QA Invalid Test")
                .enterEmail("invalid-email")
                .enterPhoneNumber("invalid-phone");
        bookTickets.confirmBooking();
        bookTickets.waitForBookingAttemptCompletion();
        assertEquals("Enter a valid email", bookTickets.getEmailValidationMessage());
        assertEquals("Enter a valid 10-digit phone", bookTickets.getPhoneValidationMessage());
        saveScreenshot("book-tickets-invalid-details.png");

        driver.get(EVENT_URL);
        assertEquals(seatsBeforeInvalidAttempt, bookTickets.getAvailableSeats(),
                "Rejected invalid booking should not reduce available seats");
    }

    private void saveScreenshot(String fileName) throws IOException {
        Path screenshot = Path.of("target", "screenshots", fileName);
        Files.createDirectories(screenshot.getParent());
        Files.copy(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE).toPath(),
                screenshot, StandardCopyOption.REPLACE_EXISTING);
    }
}
