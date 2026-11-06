package com.example.pages;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.WebDriver;

final class TestEventFactory {
    private static final DateTimeFormatter UNIQUE_TITLE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static final DateTimeFormatter EVENT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy");
    private static final String DESCRIPTION = "A conference for sharing practical QA automation techniques.";
    private static final String VENUE = "Bangalore International Exhibition Centre, Bengaluru";
    private static final String CITY = "Bangalore";
    private static final String PRICE = "250";

    private TestEventFactory() {
    }

    static TestEvent create(WebDriver driver, String titlePrefix) {
        LocalDateTime eventDateTime = LocalDateTime.now()
                .plusDays(30)
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        String title = titlePrefix + LocalDateTime.now().format(UNIQUE_TITLE_FORMAT);

        new CreateEventPage(driver)
                .openFromEvents()
                .enterTitle(title)
                .enterDescription(DESCRIPTION)
                .selectCategory("Conference")
                .enterCity(CITY)
                .enterVenue(VENUE)
                .enterDateTime(eventDateTime.format(EVENT_DATE_FORMAT))
                .enterPrice(PRICE)
                .enterTotalSeats("500")
                .submit();

        ManageEventsByAdmin events = new ManageEventsByAdmin(driver);
        events.openManageEvents();
        events.waitForEventInAdminToAppear(title);
        events.openDashboard();
        String eventUrl = events.getPublicEventUrl(title);

        return new TestEvent(title, eventUrl, eventDateTime.format(DISPLAY_DATE_FORMAT),
                "$" + PRICE, "$1,250");
    }

    record TestEvent(String title, String url, String date, String pricePerTicket, String totalPrice) {
    }
}
