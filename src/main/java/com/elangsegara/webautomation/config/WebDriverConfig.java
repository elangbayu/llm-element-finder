package com.elangsegara.webautomation.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebDriverConfig {
  private static final Logger logger = LoggerFactory.getLogger(WebDriverConfig.class);
  private static WebDriver driver;

  public static void createChromeDriver() {
    try {
      // Configure Chrome options
      ChromeOptions options = new ChromeOptions();
      options.addArguments("--start-maximized");
      options.addArguments("--disable-extensions");

      // Create and return WebDriver instance
      driver = new ChromeDriver(options);
    } catch (Exception e) {
      logger.error("Error creating WebDriver: {}", e.getMessage(), e);
      throw new RuntimeException("Failed to initialize WebDriver", e);
    }
  }

  public static void quitDriver() {
    if (driver != null) {
      try {
        driver.quit();
        logger.info("WebDriver closed successfully");
      } catch (Exception e) {
        logger.error("Error closing WebDriver: {}", e.getMessage(), e);
      }
    }
  }

  public static WebDriver webDriver() {
    return driver;
  }
}
