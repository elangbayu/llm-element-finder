package com.elangsegara.webautomation;

import com.elangsegara.webautomation.config.WebDriverConfig;
import com.elangsegara.webautomation.core.BasePage;
import com.elangsegara.webautomation.pages.HomePage;
import com.elangsegara.webautomation.pages.LoginPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SauceDemoTest extends BasePage {
  @BeforeEach
  public void setUp() {
    WebDriverConfig.createChromeDriver();
  }

  @Test
  public void testSauceDemo() {
    // Navigate to Swag Labs
    navigateTo("https://www.saucedemo.com");

    // Login
    LoginPage loginPage = new LoginPage();
    loginPage.login("standard_user", "secret_sauce");

    // Verify user successfully logged in by asserting that the inventory list is
    // displayed
    HomePage homePage = new HomePage();
    homePage.verifyInventoryListIsDisplayed();
  }

  @AfterEach
  public void tearDown() {
    WebDriverConfig.quitDriver();
  }
}
