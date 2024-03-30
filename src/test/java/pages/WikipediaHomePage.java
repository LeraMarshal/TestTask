package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

// Для работы с браузером из IDEA
// page_url = https://ru.wikipedia.org
public class WikipediaHomePage {
    /**
     * URL главной страницы
     */
    public String url = "https://ru.wikipedia.org";

    /**
     * Драйвер Selenium
     */
    WebDriver driver;

    /**
     * Поле поиска
     **/
    @FindBy(id = "searchInput")
    WebElement searchInput;

    /**
     * Блок с саджестами
     **/
    @FindBy(className = "suggestions")
    WebElement suggestions;

    /**
     * Все саджесты
     **/
    @FindBy(className = "suggestions-result")
    List<WebElement> suggestionsResult;

    /**
     * Все span-ы из саджестов
     **/
    @FindBy(xpath = "//div[@class='suggestions-results']//span[@class='highlight']")
    List<WebElement> suggestionHighlights;

    /**
     * Кнопка поиск (лупа справа)
     **/
    @FindBy(id = "searchButton")
    WebElement searchButton;

    /**
     * Подсказка "Поиск страниц, содержащих"
     **/
    @FindBy(className = "suggestions-special")
    WebElement specialLabel;

    // Инициализируем поля PageObject с помощью PageFactory
    public WikipediaHomePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @Step("Ожидаем появления поля поиска")
    public void waitForSearchQueryVisibility() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOf(searchInput));
    }

    @Step("Набираем запрос {query} в поле поиска")
    public void enterSearchQuery(String query) {
        searchInput.click();
        searchInput.sendKeys(query);
    }

    @Step("Ожидаем появления списка саджестов")
    public void waitForSuggestionsVisibility() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOf(suggestions));
    }

    @Step("Получаем содержимое всех саджестов")
    public List<String> getAllSuggestionsResult() {
        return suggestionsResult.stream().map(
                (element) -> element.getAttribute("innerHTML").toLowerCase()
        ).toList();
    }

    @Step("Получаем жирность всех span-ов в саджестах")
    public List<String> getAllSuggestionsHighlightFontWeight() {
        return suggestionHighlights.stream().map(
                (element) -> element.getCssValue("font-weight")
        ).toList();
    }

    @Step("Кликаем по первому саджесту")
    public WikipediaArticlePage clickOnFirstSuggest() {
        suggestionsResult.get(0).click();

        return new WikipediaArticlePage(driver);
    }

    @Step("Кликаем на кнопку поиск (лупа справа)")
    public Object clickOnSearchButton() {
        // Текст, который набран в строке поиска.
        String currentSearchText = searchInput.getAttribute("value").toLowerCase();

        // Есть ли саджест с полным совпадением.
        boolean hadSuggestHighlight = !suggestionHighlights.isEmpty()
                && Objects.equals(suggestionHighlights.get(0).getText().toLowerCase(), currentSearchText);

        // Нажимаем на кнопку поиска.
        searchButton.click();

        if (hadSuggestHighlight) {
            // Если был полностью соответствующий поисковому запросу саджест, возвращаем страницу статьи.
            return new WikipediaArticlePage(driver);
        } else {
            // Если такого саджеста не было, возвращаем страницу поиска.
            return new WikipediaSearchPage(driver);
        }
    }

    @Step("Ожидаем появления подсказки \"Поиск страниц, содержащих\"")
    public void waitForSpecialLabelVisibility() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOf(specialLabel));
    }

    @Step("Кликаем на подсказку \"Поиск страниц, содержащих\"")
    public WikipediaSearchPage clickOnSpecialLabel() {
        specialLabel.click();

        return new WikipediaSearchPage(driver);
    }
}
