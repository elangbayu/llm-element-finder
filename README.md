# Selenium WebDriver Automation Project

## Overview
This is a Java-based web automation project using Selenium WebDriver, following best practices for maintainability and separation of concerns.

## Prerequisites
- Java 21 or higher
- Gradle
- Chrome Browser

## Project Structure
- `src/main/java/com/example/webautomation/config/`: WebDriver configuration
- `src/main/java/com/example/webautomation/core/`: Base page and core utilities
- `src/main/java/com/example/webautomation/pages/`: Page Object Model classes
- `src/test/java/`: Test cases

## Setup
1. Clone the repository
2. Ensure you have Java 17 and Gradle installed
3. Install Chrome browser

## Running Tests
```bash
./gradlew test
```

## Key Dependencies
- Selenium WebDriver
- WebDriverManager
- JUnit 5
- SLF4J Logging

## Best Practices
- Page Object Model design pattern
- Centralized WebDriver configuration
- Robust error handling
- Comprehensive logging

## Troubleshooting
- Ensure Chrome browser is up to date
- Verify Java and Gradle installations 