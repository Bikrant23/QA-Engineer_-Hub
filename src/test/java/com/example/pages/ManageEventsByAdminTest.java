package com.example.pages;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ManageEventsByAdminTest {
    private static final List<String> CREATED_EVENTS = List.of(
            "QA Automation Summit 20261105135824",
            "QA Automation Summit Manual 20261105135500");

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver(new ChromeOptions());
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
    void cancelsAndConfirmsDeletionForBothCreatedEvents() throws IOException {
        ManageEventsByAdmin manageEvents = new ManageEventsByAdmin(driver);
        manageEvents.openDashboard();
        assertEventsVisibleOnDashboard(manageEvents, CREATED_EVENTS);
        saveScreenshot("manage-events-01-dashboard-before-deletion.png");
        manageEvents.openEventsPage();
        assertEventsVisibleOnPublicPage(manageEvents, CREATED_EVENTS);

        manageEvents.openDashboard();
        manageEvents.openFromDashboard();
        assertEventsVisibleInAdmin(manageEvents);
        saveScreenshot("manage-events-02-admin-events-before-deletion.png");

        for (int index = 0; index < CREATED_EVENTS.size(); index++) {
            String title = CREATED_EVENTS.get(index);
            String suffix = index == 0 ? "first" : "second";

            manageEvents.clickDeleteForEvent(title);
            assertTrue(manageEvents.isDeleteDialogDisplayed(),
                    "Delete confirmation should be shown for " + title);
            saveScreenshot("manage-events-03-delete-dialog-" + suffix + ".png");

            manageEvents.dismissDeleteConfirmation();
            assertTrue(manageEvents.isEventListedInAdmin(title),
                    "Cancel should retain " + title + " in Manage Events");
            saveScreenshot("manage-events-04-after-cancel-" + suffix + ".png");
            assertEventsVisibleOnPublicPages(manageEvents,
                    CREATED_EVENTS.subList(index, CREATED_EVENTS.size()));

            manageEvents.clickDeleteForEvent(title);
            assertTrue(manageEvents.isDeleteDialogDisplayed(),
                    "Delete confirmation should be shown before deleting " + title);
            saveScreenshot("manage-events-05-confirm-delete-dialog-" + suffix + ".png");
            manageEvents.confirmDeleteEvent();
            manageEvents.waitForEventInAdminToDisappear(title);
            assertFalse(manageEvents.isEventListedInAdmin(title),
                    "Confirmed deletion should remove " + title + " from Manage Events");
            saveScreenshot("manage-events-06-after-delete-admin-" + suffix + ".png");

            manageEvents.openDashboard();
            assertFalse(manageEvents.isEventListedOnPublicPage(title),
                    "Confirmed deletion should remove " + title + " from the Dashboard");
            saveScreenshot("manage-events-07-after-delete-dashboard-" + suffix + ".png");

            manageEvents.openEventsPage();
            assertFalse(manageEvents.isEventListedOnPublicPage(title),
                    "Confirmed deletion should remove " + title + " from the Events page");
            saveScreenshot("manage-events-08-after-delete-events-page-" + suffix + ".png");

            manageEvents.openManageEvents();
        }
    }

    private void assertEventsVisibleInAdmin(ManageEventsByAdmin manageEvents) {
        for (String title : CREATED_EVENTS) {
            manageEvents.waitForEventInAdminToAppear(title);
            assertTrue(manageEvents.isEventListedInAdmin(title),
                    "Created event should be present in Manage Events: " + title);
        }
    }

    private void assertEventsVisibleOnPublicPages(
            ManageEventsByAdmin manageEvents, List<String> expectedEvents) {
        assertEventsVisibleOnDashboard(manageEvents, expectedEvents);
        manageEvents.openEventsPage();
        assertEventsVisibleOnPublicPage(manageEvents, expectedEvents);
        manageEvents.openManageEvents();
        for (String title : expectedEvents) {
            manageEvents.waitForEventInAdminToAppear(title);
        }
    }

    private void assertEventsVisibleOnDashboard(
            ManageEventsByAdmin manageEvents, List<String> expectedEvents) {
        manageEvents.openDashboard();
        manageEvents.waitForPublicEventListToLoad();
        for (String title : expectedEvents) {
            assertTrue(manageEvents.isEventListedOnPublicPage(title),
                    "Created event should be present on the Dashboard: " + title);
        }
    }

    private void assertEventsVisibleOnPublicPage(
            ManageEventsByAdmin manageEvents, List<String> expectedEvents) {
        manageEvents.waitForPublicEventListToLoad();
        for (String title : expectedEvents) {
            assertTrue(manageEvents.isEventListedOnPublicPage(title),
                    "Created event should be present on the Events page: " + title);
        }
    }

    private void saveScreenshot(String fileName) throws IOException {
        Path screenshot = Path.of("target", "screenshots", fileName);
        Files.createDirectories(screenshot.getParent());
        Files.copy(((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE).toPath(),
                screenshot, StandardCopyOption.REPLACE_EXISTING);
    }
}
