package com.elangsegara.webautomation.pages;

import org.openqa.selenium.By;
import com.elangsegara.webautomation.core.BasePage;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HomePage extends BasePage {
    public void verifyInventoryListIsDisplayed() {
        assertTrue(findElement(By.id("inventory_container")).isDisplayed());
    }
} 