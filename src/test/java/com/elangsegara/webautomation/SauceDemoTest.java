package com.elangsegara.webautomation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import com.elangsegara.webautomation.config.WebDriverConfig;
import com.elangsegara.webautomation.core.BasePage;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
        findElement(By.id("user-name")).sendKeys("standard_user");
        findElement(By.id("password")).sendKeys("secret_sauce");

        // Use customized click method to use AI-based mechanism as fallback if the
        // conventional method is failed
        click("LOGIN_BUTTON", By.id("login_button"), "login button");

        // Verify user successfully logged in by asserting that the inventory list is
        // displayed
        assertTrue(findElement(By.id("inventory_container")).isDisplayed());
    }

    @AfterEach
    public void tearDown() {
        WebDriverConfig.quitDriver();
    }
}