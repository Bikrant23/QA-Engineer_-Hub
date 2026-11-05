package com.example.pages;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateEventTest {
    private static final DateTimeFormatter UNIQUE_TITLE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final DateTimeFormatter EVENT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver(new ChromeOptions());
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void logsInNavigatesToEventsAndCreatesConference() throws IOException {
        LoginPage loginPage = new LoginPage(driver)
                .open()
                .enterEmail(BasePage.EMAIL)
                .enterPassword(BasePage.PASSWORD);
        loginPage.submit();
        assertTrue(loginPage.isLoggedIn(), "Login should reach the dashboard");

        String title = "QA Automation Summit " + LocalDateTime.now().format(UNIQUE_TITLE_FORMAT);
        String dateTime = LocalDateTime.now().plusDays(30)
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
                .format(EVENT_DATE_FORMAT);

        CreateEventPage createEventPage = new CreateEventPage(driver)
                .openFromEvents()
                .enterTitle(title)
                .enterDescription("A conference for sharing practical QA automation techniques.")
                .selectCategory("Conference")
                .enterCity("Bangalore")
                .enterVenue("Bangalore International Exhibition Centre, Bengaluru")
                .enterDateTime(dateTime)
                .enterPrice("250")
                .enterTotalSeats("500");

        assertEquals(dateTime, driver.findElement(By.id("event-date-&-time"))
                .getDomProperty("value"), "The event date and time should be entered");
        assertTrue((Boolean) ((JavascriptExecutor) driver)
                        .executeScript("return document.querySelector('#admin-event-form').checkValidity();"),
                "All required event fields should be valid before submission");
        createEventPage.submit();

        boolean eventListed = new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ignored -> createEventPage.isEventListed(title));
        assertTrue(eventListed,
                "The newly created event should appear in the events table");

        Path screenshot = Path.of("target", "screenshots", "create-event.png");
        Files.createDirectories(screenshot.getParent());
        Files.copy(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE).toPath(),
                screenshot, StandardCopyOption.REPLACE_EXISTING);
    }
}
