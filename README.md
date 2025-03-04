# Selenium Web Automation with AI-Powered Element Search 🤖🔎

Web automation that supercharged with an AI-powered feature for automatic element searching. This nifty feature uses special algorithms to find web elements more efficiently, cutting down on manual work and boosting test reliability.

## Key Features
- 🤖 **AI-Powered Element Search**: Let AI do the heavy lifting in locating web elements!
- 📐 **Page Object Model Design Pattern**: Keep your test architecture neat and scalable.
- 🛠️ **Centralized WebDriver Configuration**: Easy setup and management of WebDriver instances. 
- 💪 **Robust Error Handling**: Tests that can handle surprises! 
- 📝 **Comprehensive Logging**: Detailed logs for smooth debugging and analysis. 

## Prerequisites
- Java 21 or higher
- Chrome Browser
- LLM Provider API key. ⚠️ *Only choose model that support structured output*.
- Environment variables:
  - `LLM_PROVIDER_API_KEY`: API Key to your LLM Provider
  - `LLM_MODEL`: Model to use as element finder
  - `LLM_BASE_API`: Base URL to call the API Key

## Project Structure
- `src/main/java/com/elangsegara/webautomation/config/`: WebDriver configuration
- `src/main/java/com/elangsegara/webautomation/core/`: Base page and core utilities
- `src/main/java/com/elangsegara/webautomation/pages/`: Page Object Model classes
- `src/test/java/`: Test cases

## Running Tests
```bash
./gradlew test
```
