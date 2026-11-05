# Page Object Maven Project

A Java 17 Maven project using Selenium and the Page Object Model. `LoginPage` is an example page object; replace its locators with those from your application.

## Requirements

- JDK 17 or newer
- Maven 3.8 or newer
- Google Chrome installed for the browser-driven login test; Selenium Manager resolves the matching ChromeDriver.

## Build and test

From this directory, run:

```shell
mvn clean test
```

`LoginPageTest` opens Chrome in headed mode, signs in, and checks for the Logout button. `CreateEventTest` signs in, navigates through Events, and submits a new conference. The event test creates persistent data in EventHub; the app replaces its oldest event after the six-event limit is reached, so run it only when that side effect is intended. Run either test with `mvn -Dtest=LoginPageTest test` or `mvn -Dtest=CreateEventTest test`, or run the full suite with `mvn clean test`.
