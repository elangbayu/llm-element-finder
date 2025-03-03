package com.elangsegara.webautomation.utils;

import com.elangsegara.webautomation.config.WebDriverConfig;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.JsonValue;
import com.openai.models.ChatCompletionCreateParams;
import com.openai.models.ResponseFormatJsonSchema;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class AiFallback {
  public static By getElementFromAi(String queryElement) {
    String pageSource = domProcessor(WebDriverConfig.webDriver());
    OpenAIClient client =
        OpenAIOkHttpClient.builder()
            .baseUrl("https://openrouter.ai/api/v1")
            .apiKey(System.getenv("OPENROUTER_API_KEY"))
            .build();
    ResponseFormatJsonSchema.JsonSchema.Schema schem =
        ResponseFormatJsonSchema.JsonSchema.Schema.builder()
            .putAllAdditionalProperties(
                Map.of(
                    "type", JsonValue.from("object"),
                    "properties",
                        JsonValue.from(
                            Map.of(
                                "css_selector",
                                JsonValue.from(
                                    Map.of(
                                        "type", "string",
                                        "description",
                                            "CSS Selector of the given element output.")))),
                    "required", JsonValue.from(List.of("css_selector"))))
            .build();
    ResponseFormatJsonSchema.JsonSchema schemas =
        ResponseFormatJsonSchema.JsonSchema.builder()
            .name("CSS Selector Extractor")
            .strict(true)
            .schema(schem)
            .build();
    ResponseFormatJsonSchema schema =
        ResponseFormatJsonSchema.builder()
            .jsonSchema(schemas)
            .putAdditionalProperty("required", JsonValue.from(List.of("css_selector")))
            .build();

    ChatCompletionCreateParams params =
        ChatCompletionCreateParams.builder()
            .addSystemMessage(
                "Given the HTML source, extract the element with the given query. Always return in"
                    + " json format.")
            .addUserMessage(
                String.format(
                    "Return element's %s css selector based on this page source: ```%s```",
                    queryElement, pageSource))
            .responseFormat(schema)
            .model("google/gemini-2.0-flash-lite-001")
            .maxCompletionTokens(200)
            .build();

    return By.cssSelector(
        client.chat().completions().create(params).choices().stream()
            .flatMap(contentBlock -> contentBlock.message().content().stream())
            .map(
                toolUseBlock -> {
                  JsonObject jsonObject = JsonParser.parseString(toolUseBlock).getAsJsonObject();
                  String result = jsonObject.get("css_selector").getAsString();
                  highlightElement(result);
                  return result;
                })
            .findFirst()
            .orElse(""));
  }

  private static String domProcessor(WebDriver driver) {
    WebElement nextDiv = driver.findElement(By.id("root"));
    JavascriptExecutor js = (JavascriptExecutor) driver;
    return (String)
        js.executeScript(
            "function isElementInViewport(el) {  var rect = el.getBoundingClientRect();  return (  "
                + "  rect.top >= 0 &&    rect.left >= 0 &&    rect.bottom <= (window.innerHeight ||"
                + " document.documentElement.clientHeight) &&    rect.right <= (window.innerWidth"
                + " || document.documentElement.clientWidth)  );}function isElementInteractable(el)"
                + " {  var style = window.getComputedStyle(el);  return (    el.offsetWidth > 0 &&"
                + " el.offsetHeight > 0 &&    style.display !== 'none' &&    style.visibility !=="
                + " 'hidden' &&    style.pointerEvents !== 'none' &&    !el.disabled  );}function"
                + " analyzeInteractableNodes(root) {  var result = { html: '', interactableCount: 0"
                + " };  if (!root) return result;  var clone = root.cloneNode(true);  if"
                + " (isElementInteractable(root)) {    result.html = clone.outerHTML;   "
                + " result.interactableCount++;   "
                + " Array.from(root.children).forEach(function(child) {      var childAnalysis ="
                + " analyzeInteractableNodes(child);      result.interactableCount +="
                + " childAnalysis.interactableCount;    });  }  return result;}var analysis ="
                + " analyzeInteractableNodes(arguments[0]);return JSON.stringify({  html:"
                + " analysis.html,  interactableCount: analysis.interactableCount,  inViewport:"
                + " isElementInViewport(arguments[0])});",
            nextDiv);
  }

  private static void highlightElement(String selector) {
    WebElement element = WebDriverConfig.webDriver().findElement(By.cssSelector(selector));
    JavascriptExecutor js = (JavascriptExecutor) WebDriverConfig.webDriver();
    js.executeScript("arguments[0].style.border='3px solid red'", element);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      // ignore
    }
  }
}
