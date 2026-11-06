package com.example.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CreateEventPage extends BasePage {
    private static final By ADD_NEW_EVENT_BUTTON =
            By.xpath("//a[@href='/admin/events']//button[normalize-space()='Add New Event']");
    private static final By TITLE = By.id("event-title-input");
    private static final By DESCRIPTION = By.cssSelector("form[data-testid='admin-event-form'] textarea");
    private static final By CATEGORY = By.id("category");
    private static final By CITY = By.id("city");
    private static final By VENUE = By.id("venue");
    private static final By EVENT_DATE_TIME = By.id("event-date-&-time");
    private static final By PRICE = By.id("price-($)");
    private static final By TOTAL_SEATS = By.id("total-seats");
    private static final By SUBMIT_BUTTON = By.id("add-event-btn");
    private static final By EVENT_CREATED_SUCCESS_MESSAGE =
            By.xpath("//*[normalize-space()='Event created!']");

    public CreateEventPage(WebDriver driver) {
        super(driver);
    }

    public CreateEventPage openFromEvents() {
        click(EVENTS_NAV_LINK);
        clickUsingJavaScript(ADD_NEW_EVENT_BUTTON);
        waitForVisible(TITLE);
        return this;
    }

    public CreateEventPage enterTitle(String title) {
        type(TITLE, title);
        return this;
    }

    public CreateEventPage enterDescription(String description) {
        type(DESCRIPTION, description);
        return this;
    }

    public CreateEventPage selectCategory(String category) {
        new Select(waitForVisible(CATEGORY)).selectByVisibleText(category);
        return this;
    }

    public CreateEventPage enterCity(String city) {
        type(CITY, city);
        return this;
    }

    public CreateEventPage enterVenue(String venue) {
        type(VENUE, venue);
        return this;
    }

    public CreateEventPage enterDateTime(String dateTime) {
        ((JavascriptExecutor) driver).executeScript(
                "const input = arguments[0];"
                        + "const setter = Object.getOwnPropertyDescriptor(HTMLInputElement.prototype, 'value').set;"
                        + "setter.call(input, arguments[1]);"
                        + "input.dispatchEvent(new Event('input', { bubbles: true }));"
                        + "input.dispatchEvent(new Event('change', { bubbles: true }));",
                waitForVisible(EVENT_DATE_TIME), dateTime);
        return this;
    }

    public CreateEventPage enterPrice(String price) {
        type(PRICE, price);
        return this;
    }

    public CreateEventPage enterTotalSeats(String totalSeats) {
        type(TOTAL_SEATS, totalSeats);
        return this;
    }

    public void submit() {
        click(SUBMIT_BUTTON);
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.visibilityOfElementLocated(EVENT_CREATED_SUCCESS_MESSAGE));
    }

    public boolean isEventListed(String title) {
        By eventRow = By.xpath("//tr[@data-testid='event-table-row']"
                + "[.//td[1]//span[normalize-space()='" + title + "']]");
        return !driver.findElements(eventRow).isEmpty();
    }
}
