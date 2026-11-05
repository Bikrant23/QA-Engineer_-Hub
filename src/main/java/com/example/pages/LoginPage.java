package com.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {
    private static final By EMAIL = By.id("email");
    private static final By PASSWORD = By.id("password");
    private static final By LOGIN_BUTTON = By.id("login-btn");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        driver.get(BASE_URL + "login");
        return this;
    }

    public LoginPage enterEmail(String email) {
        type(EMAIL, email);
        return this;
    }

    public LoginPage enterPassword(String password) {
        type(PASSWORD, password);
        return this;
    }

    public void submit() {
        click(LOGIN_BUTTON);
    }

    public boolean isLoggedIn() {
        return waitForVisible(LOGOUT_BUTTON).isDisplayed();
    }
}
