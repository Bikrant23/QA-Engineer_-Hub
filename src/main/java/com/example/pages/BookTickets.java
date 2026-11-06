package com.example.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BookTickets extends BasePage {
    private static final By TICKET_COUNT = By.id("ticket-count");
    private static final By DECREASE_TICKET_BUTTON =
            By.xpath("//form[.//button[@id='confirm-booking']]//button[@type='button'][normalize-space()='−']");
    private static final By INCREASE_TICKET_BUTTON =
            By.xpath("//form[.//button[@id='confirm-booking']]//button[@type='button'][normalize-space()='+']");
    private static final By FULL_NAME = By.id("customerName");
    private static final By EMAIL = By.id("customer-email");
    private static final By EMAIL_ERROR = By.xpath("//input[@id='customer-email']/following-sibling::p");
    private static final By PHONE_NUMBER = By.id("phone");
    private static final By PHONE_ERROR = By.xpath("//input[@id='phone']/following-sibling::p");
    private static final By CONFIRM_BOOKING_BUTTON = By.id("confirm-booking");
    private static final By AVAILABLE_SEATS =
            By.xpath("//span[contains(normalize-space(), ' / ') and contains(normalize-space(), ' seats')]");
    private static final By BOOKING_CONFIRMED =
            By.xpath("//main//h3[contains(normalize-space(), 'Booking Confirmed!')]");

    public BookTickets(WebDriver driver) {
        super(driver);
    }

    public BookTickets increaseTicketCount() {
        click(INCREASE_TICKET_BUTTON);
        return this;
    }

    public BookTickets decreaseTicketCount() {
        click(DECREASE_TICKET_BUTTON);
        return this;
    }

    public BookTickets selectTicketCount(int ticketCount) {
        if (ticketCount < 1 || ticketCount > 10) {
            throw new IllegalArgumentException("Ticket count must be between 1 and 10");
        }

        int currentCount = Integer.parseInt(getTicketCount());
        while (currentCount < ticketCount) {
            increaseTicketCount();
            currentCount++;
        }
        while (currentCount > ticketCount) {
            decreaseTicketCount();
            currentCount--;
        }
        return this;
    }

    public String getTicketCount() {
        return waitForVisible(TICKET_COUNT).getText();
    }

    public int getAvailableSeats() {
        String seatSummary = waitForVisible(AVAILABLE_SEATS).getText();
        return Integer.parseInt(seatSummary.substring(0, seatSummary.indexOf('/')).trim());
    }

    public BookTickets enterFullName(String fullName) {
        type(FULL_NAME, fullName);
        return this;
    }

    public BookTickets enterEmail(String email) {
        type(EMAIL, email);
        return this;
    }

    public String getEmailValidationMessage() {
        return waitForVisible(EMAIL_ERROR).getText();
    }

    public BookTickets enterPhoneNumber(String phoneNumber) {
        type(PHONE_NUMBER, phoneNumber);
        return this;
    }

    public String getPhoneValidationMessage() {
        return waitForVisible(PHONE_ERROR).getText();
    }

    public void confirmBooking() {
        click(CONFIRM_BOOKING_BUTTON);
    }

    public void waitForBookingAttemptCompletion() {
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.or(
                        ExpectedConditions.visibilityOfElementLocated(BOOKING_CONFIRMED),
                        ExpectedConditions.visibilityOfElementLocated(EMAIL_ERROR),
                        ExpectedConditions.visibilityOfElementLocated(PHONE_ERROR)));
    }
}
