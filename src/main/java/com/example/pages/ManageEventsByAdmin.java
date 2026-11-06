package com.example.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ManageEventsByAdmin extends BasePage {
    private static final By ADMIN_MENU_BUTTON =
            By.xpath("//nav//button[normalize-space()='Admin']");
    private static final By MANAGE_EVENTS_LINK = By.cssSelector("a[href='/admin/events']");
    private static final By EVENT_ROWS = By.cssSelector("tr[data-testid='event-table-row']");
    private static final By DELETE_EVENT_BUTTON = By.cssSelector("[data-testid='delete-event-btn']");
    private static final By DELETE_DIALOG = By.cssSelector("[role='dialog']");
    private static final By CANCEL_DELETE_BUTTON =
            By.xpath("//*[@role='dialog']//button[normalize-space()='Cancel']");
    private static final By CONFIRM_DELETE_BUTTON =
            By.xpath("//*[@role='dialog']//button[normalize-space()='Delete event']");
    private static final By DASHBOARD_HEADING =
            By.xpath("//main//h1[contains(normalize-space(.), 'Discover & Book')]");
    private static final By EVENTS_HEADING =
            By.xpath("//main//h1[normalize-space()='Upcoming Events']");

    public ManageEventsByAdmin(WebDriver driver) {
        super(driver);
    }

    public ManageEventsByAdmin openFromDashboard() {
        click(ADMIN_MENU_BUTTON);
        click(MANAGE_EVENTS_LINK);
        waitForAdminEvents();
        return this;
    }

    public ManageEventsByAdmin openManageEvents() {
        driver.get(BASE_URL + "admin/events");
        waitForAdminEvents();
        return this;
    }

    private void waitForAdminEvents() {
        waitForVisible(By.xpath("//main//h2[normalize-space()='All Events']"));
    }

    public boolean isEventListedInAdmin(String title) {
        return findEventRow(title) != null;
    }

    public ManageEventsByAdmin clickDeleteForEvent(String title) {
        waitForEventInAdminToAppear(title);
        WebElement row = findEventRow(title);
        if (row == null) {
            throw new NoSuchElementException("No Admin event row found for " + title);
        }
        WebElement deleteButton = row.findElement(DELETE_EVENT_BUTTON);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", deleteButton);
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOf(deleteButton));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", deleteButton);
        waitForVisible(DELETE_DIALOG);
        return this;
    }

    public boolean isDeleteDialogDisplayed() {
        return !driver.findElements(DELETE_DIALOG).isEmpty()
                && driver.findElement(DELETE_DIALOG).isDisplayed();
    }

    public ManageEventsByAdmin dismissDeleteConfirmation() {
        click(CANCEL_DELETE_BUTTON);
        waitForDeleteDialogToClose();
        return this;
    }

    public ManageEventsByAdmin confirmDeleteEvent() {
        click(CONFIRM_DELETE_BUTTON);
        waitForDeleteDialogToClose();
        return this;
    }

    public void waitForEventInAdminToDisappear(String title) {
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ignored -> !isEventListedInAdmin(title));
    }

    public void waitForEventInAdminToAppear(String title) {
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ignored -> isEventListedInAdmin(title));
    }

    public void waitForPublicEventListToLoad() {
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ignored -> driver.findElements(By.cssSelector("main article")).size() >= 3);
    }

    public ManageEventsByAdmin openDashboard() {
        driver.get(BASE_URL);
        waitForVisible(DASHBOARD_HEADING);
        return this;
    }

    public ManageEventsByAdmin openEventsPage() {
        driver.get(BASE_URL + "events");
        waitForVisible(EVENTS_HEADING);
        return this;
    }

    public boolean isEventListedOnPublicPage(String title) {
        By eventCard = By.xpath("//main//article[.//a[normalize-space()='" + title + "']]");
        return !driver.findElements(eventCard).isEmpty();
    }

    public String getPublicEventUrl(String title) {
        By eventLink = By.xpath("//main//article//a[normalize-space()='" + title + "']");
        return waitForVisible(eventLink).getDomProperty("href");
    }

    private WebElement findEventRow(String title) {
        for (WebElement row : driver.findElements(EVENT_ROWS)) {
            if (row.findElement(By.cssSelector("td:first-child")).getText().equals(title)) {
                return row;
            }
        }
        return null;
    }

    private void waitForDeleteDialogToClose() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.invisibilityOfElementLocated(DELETE_DIALOG));
    }
}
