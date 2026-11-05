package com.example.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {
    public static final String BASE_URL = "https://eventhub.rahulshettyacademy.com/";
    public static final String EMAIL = "testeventhub@example.com";
    public static final String PASSWORD = "Password@1";

    protected static final By EVENTS_NAV_LINK = By.id("nav-events");
    protected static final By LOGOUT_BUTTON = By.id("logout-btn");

    protected final WebDriver driver;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
    }

    protected WebElement find(By locator) {
        return driver.findElement(locator);
    }

    protected WebElement waitForVisible(By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void type(By locator, String text) {
        WebElement element = waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected void click(By locator) {
        WebElement element = waitForVisible(locator);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", element);
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.elementToBeClickable(locator))
                .click();
    }

    protected void clickUsingJavaScript(By locator) {
        WebElement element = waitForVisible(locator);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'}); arguments[0].click();", element);
    }
}
