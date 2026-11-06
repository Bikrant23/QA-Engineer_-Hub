package com.example.pages;

import java.util.ArrayList;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BookingDetails extends BasePage {
    private static final By MY_BOOKINGS_LINK = By.id("nav-bookings");
    private static final By BOOKINGS_HEADING = By.xpath("//main//h1[normalize-space()='My Bookings']");
    private static final By BOOKING_CARDS = By.cssSelector("[data-testid='booking-card']");
    private static final By CLEAR_ALL_BOOKINGS_BUTTON =
            By.xpath("//main//button[normalize-space()='Clear all bookings']");
    private static final By NO_BOOKINGS_MESSAGE =
            By.xpath("//main//*[self::h2 or self::h3 or self::p][normalize-space()='No bookings yet']");
    private static final By BROWSE_EVENTS_BUTTON =
            By.xpath("//main//a[.//button[normalize-space()='Browse Events']] | //main//button[normalize-space()='Browse Events']");
    private static final By BOOKING_NOT_FOUND =
            By.xpath("//main//h3[normalize-space()='Booking not found']");
    private static final By DETAIL_BOOKING_REFERENCE =
            By.xpath("//main//span[contains(@class,'font-mono') and contains(@class,'text-indigo-600')]");
    private static final By DETAIL_BOOKING_STATUS =
            By.xpath("//main//span[contains(@class,'font-mono') and contains(@class,'text-indigo-600')]/following-sibling::span[1]");
    private static final By CANCEL_BOOKING_BUTTON =
            By.xpath("//main//button[normalize-space()='Cancel Booking']");
    private static final By CANCEL_DIALOG = By.cssSelector("[role='dialog']");
    private static final By CANCEL_DIALOG_DISMISS_BUTTON =
            By.xpath("//*[@role='dialog']//button[normalize-space()='Cancel']");
    private static final By CONFIRM_CANCEL_BUTTON = By.id("confirm-dialog-yes");

    public BookingDetails(WebDriver driver) {
        super(driver);
    }

    public BookingDetails openMyBookings() {
        click(MY_BOOKINGS_LINK);
        waitForMyBookingsToLoad();
        return this;
    }

    public void waitForMyBookingsToLoad() {
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.visibilityOfElementLocated(BOOKINGS_HEADING));
    }

    public void waitForAtLeastOneBooking() {
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ignored -> !getAllBookingReferences().isEmpty());
    }

    public List<String> getAllBookingReferences() {
        List<String> references = new ArrayList<>();
        for (WebElement card : driver.findElements(BOOKING_CARDS)) {
            references.add(card.findElement(By.cssSelector(".booking-ref")).getText());
        }
        return references;
    }

    public BookingDetails clickClearAllBookings() {
        click(CLEAR_ALL_BOOKINGS_BUTTON);
        return this;
    }

    public String getClearBookingsConfirmationText() {
        return new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.alertIsPresent())
                .getText();
    }

    public BookingDetails dismissClearBookingsConfirmation() {
        driver.switchTo().alert().dismiss();
        return this;
    }

    public BookingDetails acceptClearBookingsConfirmation() {
        driver.switchTo().alert().accept();
        return this;
    }

    public void waitForNoBookingsMessage() {
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.visibilityOfElementLocated(NO_BOOKINGS_MESSAGE));
    }

    public boolean isNoBookingsMessageDisplayed() {
        return !driver.findElements(NO_BOOKINGS_MESSAGE).isEmpty()
                && driver.findElement(NO_BOOKINGS_MESSAGE).isDisplayed();
    }

    public boolean isBrowseEventsButtonDisplayed() {
        return !driver.findElements(BROWSE_EVENTS_BUTTON).isEmpty()
                && driver.findElement(BROWSE_EVENTS_BUTTON).isDisplayed();
    }

    public void waitForBookingToDisappearFromList(String bookingReference) {
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ignored -> !isBookingListed(bookingReference));
    }

    public void waitForBookingDetailsToDisappear() {
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.visibilityOfElementLocated(BOOKING_NOT_FOUND));
    }

    public void waitForCancelDialogToClose() {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.invisibilityOfElementLocated(CANCEL_DIALOG));
    }

    public void waitForBookingStatusInDetails(String expectedStatus) {
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ignored -> expectedStatus.equals(getBookingStatusInDetails()));
    }

    public void waitForBookingStatusInList(String bookingReference, String expectedStatus) {
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ignored -> expectedStatus.equals(getBookingStatusInList(bookingReference)));
    }

    public boolean isCancelDialogDisplayed() {
        return !driver.findElements(CANCEL_DIALOG).isEmpty()
                && driver.findElement(CANCEL_DIALOG).isDisplayed();
    }

    public BookingDetails openDetailsForBooking(String bookingReference) {
        WebElement card = findBookingCard(bookingReference);
        WebElement detailsButton = card.findElement(By.xpath(".//button[normalize-space()='View Details']"));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", detailsButton);
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(detailsButton))
                .click();
        waitForVisible(DETAIL_BOOKING_REFERENCE);
        return this;
    }

    public List<String> getBookingReferencesForEvent(String eventTitle) {
        List<String> references = new ArrayList<>();
        for (WebElement card : driver.findElements(BOOKING_CARDS)) {
            if (card.findElement(By.cssSelector("h3")).getText().equals(eventTitle)) {
                references.add(card.findElement(By.cssSelector(".booking-ref")).getText());
            }
        }
        return references;
    }

    public String getBookingStatusInList(String bookingReference) {
        WebElement card = findBookingCard(bookingReference);
        return card.findElement(By.cssSelector(".booking-ref + span")).getText();
    }

    public boolean isBookingListed(String bookingReference) {
        return driver.findElements(BOOKING_CARDS).stream()
                .anyMatch(card -> card.findElement(By.cssSelector(".booking-ref"))
                        .getText().equals(bookingReference));
    }

    public boolean isBookingNotFound() {
        return !driver.findElements(BOOKING_NOT_FOUND).isEmpty();
    }

    public String getBookingIdInList(String bookingReference) {
        WebElement card = findBookingCard(bookingReference);
        return card.findElement(By.cssSelector("[data-testid='booking-id']")).getText();
    }

    public String getBookingReferenceInDetails() {
        return readTextWhenStable(DETAIL_BOOKING_REFERENCE);
    }

    public String getBookingStatusInDetails() {
        return readTextWhenStable(DETAIL_BOOKING_STATUS);
    }

    public String getBookingIdInDetails() {
        return getDetailValue("Booking Information", "Booking ID");
    }

    public String getDetailValue(String section, String label) {
        By locator = By.xpath("//h2[normalize-space()='" + section + "']/following-sibling::div"
                + "//div[span[normalize-space()='" + label + "']]/span[2]");
        return readTextWhenStable(locator);
    }

    public BookingDetails openCancelBookingDialog() {
        click(CANCEL_BOOKING_BUTTON);
        waitForVisible(CANCEL_DIALOG);
        return this;
    }

    public BookingDetails dismissCancelBookingDialog() {
        click(CANCEL_DIALOG_DISMISS_BUTTON);
        waitUntilDialogCloses();
        return this;
    }

    public BookingDetails confirmCancellation() {
        click(CONFIRM_CANCEL_BUTTON);
        waitUntilDialogCloses();
        return this;
    }

    private WebElement findBookingCard(String bookingReference) {
        for (WebElement card : driver.findElements(BOOKING_CARDS)) {
            if (card.findElement(By.cssSelector(".booking-ref")).getText().equals(bookingReference)) {
                return card;
            }
        }
        throw new NoSuchElementException("No booking card found for reference " + bookingReference);
    }

    private String readTextWhenStable(By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(15)).until(ignored -> {
            try {
                WebElement element = driver.findElement(locator);
                String text = element.getText();
                return element.isDisplayed() && !text.isBlank() ? text : null;
            } catch (StaleElementReferenceException exception) {
                return null;
            }
        });
    }

    private void waitUntilDialogCloses() {
        waitForCancelDialogToClose();
    }
}
