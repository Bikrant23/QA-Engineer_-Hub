package com.example.pages;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginPageTest {
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
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void entersCredentialsAndSubmitsForm() {
        new LoginPage(driver)
                .open()
                .enterEmail(BasePage.EMAIL)
                .enterPassword(BasePage.PASSWORD)
                .submit();

        assertTrue(new LoginPage(driver).isLoggedIn());
    }
}
