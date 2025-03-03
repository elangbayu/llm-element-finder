package com.elangsegara.webautomation.core;

import com.elangsegara.webautomation.config.WebDriverConfig;
import com.elangsegara.webautomation.utils.AiFallback;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Properties;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasePage {
  private static Logger logger = LoggerFactory.getLogger(BasePage.class);

  // Path to your .properties file.
  // Adjust accordingly (e.g., you might keep it in src/test/resources/).
  private static final Path ELEMENT_SELECTORS_FILE =
      Path.of("src/test/resources/elementSelectors.properties");

  public static WebElement findElement(By locator) {
    WebDriverWait wait = new WebDriverWait(WebDriverConfig.webDriver(), Duration.ofSeconds(10));
    try {
      return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    } catch (Exception e) {
      logger.error("Element not found: {}", locator, e);
      throw new RuntimeException("Element not found: " + locator, e);
    }
  }

  public static void click(By locator) {
    WebDriverWait wait = new WebDriverWait(WebDriverConfig.webDriver(), Duration.ofSeconds(10));
    try {
      wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
      logger.info("Clicked element: {}", locator);
    } catch (Exception e) {
      logger.error("Failed to click element: {}", locator, e);
      throw new RuntimeException("Failed to click element: " + locator, e);
    }
  }

  /**
   * A reusable click method that: 1) Tries to get a cached selector by 'key' from a .properties
   * file. 2) Falls back to 'defaultSelector' if cache fails. 3) Falls back to AI-based search if
   * both fail. 4) Saves newly found selector from AI to the .properties file.
   *
   * @param key The key used in the .properties file for caching the selector.
   * @param defaultSelector The conventional/default selector to try if caching fails.
   * @param aiPrompt The prompt to send to an AI-based mechanism if both caching and default fails.
   * @param wait An instance of WebDriverWait to locate elements.
   */
  public static void click(String key, By defaultSelector, String aiPrompt) {
    WebDriverWait wait = new WebDriverWait(WebDriverConfig.webDriver(), Duration.ofSeconds(10));
    // 1. Attempt to load the cached selector
    String cachedSelector = loadCachedSelector(key);

    try {
      if (cachedSelector != null && !cachedSelector.isEmpty()) {
        // If we have a cached selector, try it first
        logger.info("Trying cached selector for key '{}': {}", key, cachedSelector);
        By byCached = By.cssSelector(cachedSelector);
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(byCached));
        element.click();
        logger.info("Successfully clicked element (cached) for key '{}'.", key);
        return; // Return after successful click
      }
    } catch (TimeoutException e) {
      logger.warn("Cached selector for key '{}' failed: {}", key, e.getMessage());
      // Fall through to defaultSelector
    } catch (Exception e) {
      logger.warn("Error using cached selector for key '{}'. Cause: {}", key, e.getMessage());
      // Fall through to defaultSelector
    }

    // 2. Fallback to default selector if caching not present or fails
    try {
      logger.info("Trying default selector for key '{}': {}", key, defaultSelector);
      WebElement element =
          wait.until(ExpectedConditions.visibilityOfElementLocated(defaultSelector));
      element.click();
      logger.info("Successfully clicked element (default) for key '{}'.", key);
      return;
    } catch (TimeoutException e) {
      logger.warn("Default selector failed for key '{}': {}", key, e.getMessage());
      // Fall through to AI fallback
    } catch (Exception e) {
      logger.warn("Error using default selector for key '{}'. Cause: {}", key, e.getMessage());
      // Fall through to AI fallback
    }

    // 3. Fallback to AI-based mechanism if both cached and default fail
    try {
      logger.info("Falling back to AI for key '{}', prompt: {}", key, aiPrompt);
      By aiBy = AiFallback.getElementFromAi(aiPrompt);
      WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(aiBy));
      element.click();
      logger.info("Successfully clicked element (AI) for key '{}'.", key);

      // 4. Save newly found selector to .properties file
      // (We assume that AiFallback returns a By.cssSelector(...) for simplicity.)
      String aiSelectorString = extractCssSelectorFromBy(aiBy);
      saveCachedSelector(key, aiSelectorString);

      logger.info("Saved new AI selector for key '{}' -> '{}'", key, aiSelectorString);
    } catch (TimeoutException e) {
      logger.error("AI-based selector timed out for key '{}'.", key, e);
      throw e; // or handle gracefully
    } catch (Exception e) {
      logger.error("Error in AI fallback for key '{}'.", key, e);
      throw e; // or handle gracefully
    }
  }

  /**
   * Loads the cached selector from .properties file if present.
   *
   * @param key The key to look up.
   * @return The cached selector (CSS) or null if not present.
   */
  private static String loadCachedSelector(String key) {
    Properties properties = new Properties();
    if (!Files.exists(ELEMENT_SELECTORS_FILE)) {
      // If no file yet, there's nothing to load
      return null;
    }
    try (InputStream is = Files.newInputStream(ELEMENT_SELECTORS_FILE)) {
      properties.load(is);
      return properties.getProperty(key);
    } catch (IOException e) {
      logger.warn("Unable to load cached selectors from file: {}", e.getMessage());
      return null;
    }
  }

  /**
   * Saves a selector to the .properties file with the given key.
   *
   * @param key The key used in the .properties file.
   * @param selector The CSS selector found by AI.
   */
  private static void saveCachedSelector(String key, String selector) {
    Properties properties = new Properties();

    // Load existing entries if the file already exists
    if (Files.exists(ELEMENT_SELECTORS_FILE)) {
      try (InputStream is = Files.newInputStream(ELEMENT_SELECTORS_FILE)) {
        properties.load(is);
      } catch (IOException e) {
        logger.warn("Unable to load existing properties before saving: {}", e.getMessage());
      }
    }

    // Update or add the new selector
    properties.setProperty(key, selector);

    // Save back to file
    try (OutputStream os = Files.newOutputStream(ELEMENT_SELECTORS_FILE)) {
      properties.store(os, "Updated selectors for UI elements");
    } catch (NoSuchFileException e) {
      try {
        Files.createDirectories(ELEMENT_SELECTORS_FILE.getParent());
        Files.createFile(ELEMENT_SELECTORS_FILE);
        logger.info("Created new selectors file: {}", ELEMENT_SELECTORS_FILE);
        // Retry saving the properties after creating the file
        try (OutputStream os = Files.newOutputStream(ELEMENT_SELECTORS_FILE)) {
          properties.store(os, "Updated selectors for UI elements");
        } catch (IOException ex) {
          logger.error("Failed to save selector to .properties file after creating it", ex);
        }
      } catch (IOException ex) {
        logger.error("Unable to create new selectors file: {}", ex.getMessage());
      }
    } catch (IOException e) {
      logger.error("Failed to save selector to .properties file", e);
    }
  }

  /**
   * Attempts to extract the CSS selector string from a {@link By} object that is presumably a
   * `By.cssSelector("someCss")`.
   *
   * @param by a By object (expected to be By.cssSelector).
   * @return the raw CSS selector if parseable, or an empty string otherwise.
   */
  private static String extractCssSelectorFromBy(By by) {
    String byString = by.toString();
    // Typically "By.cssSelector: someCss"
    if (byString.startsWith("By.cssSelector: ")) {
      return byString.replace("By.cssSelector: ", "").trim();
    }
    // Fallback if any other format
    return "";
  }

  protected void sendKeys(By locator, String text) {
    WebElement element = findElement(locator);
    try {
      element.clear();
      element.sendKeys(text);
      logger.info("Sent keys to element: {}", locator);
    } catch (Exception e) {
      logger.error("Failed to send keys to element: {}", locator, e);
      throw new RuntimeException("Failed to send keys to element: " + locator, e);
    }
  }

  public String getPageTitle() {
    return WebDriverConfig.webDriver().getTitle();
  }

  public void navigateTo(String url) {
    try {
      WebDriverConfig.webDriver().get(url);
      logger.info("Navigated to URL: {}", url);
    } catch (Exception e) {
      logger.error("Failed to navigate to URL: {}", url, e);
      throw new RuntimeException("Failed to navigate to URL: " + url, e);
    }
  }
}
