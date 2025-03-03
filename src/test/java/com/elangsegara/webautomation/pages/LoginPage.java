package com.elangsegara.webautomation.pages;

import com.elangsegara.webautomation.core.BasePage;
import org.openqa.selenium.By;

public class LoginPage extends BasePage {
  public void login(String username, String password) {
    findElement(By.id("user-name")).sendKeys(username);
    findElement(By.id("password")).sendKeys(password);
    click("LOGIN_BUTTON", By.id("login_button"), "login button");
  }
}
