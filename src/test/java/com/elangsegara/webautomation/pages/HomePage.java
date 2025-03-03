package com.elangsegara.webautomation.pages;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.elangsegara.webautomation.core.BasePage;
import org.openqa.selenium.By;

public class HomePage extends BasePage {
  public void verifyInventoryListIsDisplayed() {
    assertTrue(findElement(By.id("inventory_container")).isDisplayed());
  }
}
