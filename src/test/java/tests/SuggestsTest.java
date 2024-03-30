package tests;

import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import pages.WikipediaArticlePage;
import pages.WikipediaHomePage;
import pages.WikipediaSearchPage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SuggestsTest {
    public WebDriver driver;

    public WikipediaHomePage homePage;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();

        homePage = new WikipediaHomePage(driver);
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    @Test
    @DisplayName("Первые саджесты начинаются на поисковый запрос")
    public void firstSuggestsStartingWithSearchQueryTest() {
        String query = "Васильеви";

        // Наберем запрос поиска и дождемся саджестов.
        navigateEnterSearchQueryWaitForSuggestions(query);

        // Строка поиска в нижнем регистре для сравнений.
        String lowercasedQuery = query.toLowerCase();

        // Собираем содержимое саджестов.
        List<String> allSuggestionsResult = homePage.getAllSuggestionsResult();

        // Проверяем все саджесты. Флаг означает, что найден первый выделенный сайджест, начинающийся с запроса.
        boolean highlightedSuggestFound = false;
        for (String suggestion : allSuggestionsResult) {
            // Если саджест не содержит запрос, значит его нельзя считать первым. (На усмотрение поискового движка).
            if (!suggestion.contains(lowercasedQuery)) {
                break;
            }

            // Ожидаем, что саджест начинается с выделенного запроса.
            String expectedBeginning = String.format("<span class=\"highlight\">%s</span>", lowercasedQuery);

            // Убедимся, что саджест начинается с запроса.
            assertTrue(
                    suggestion.startsWith(expectedBeginning),
                    String.format("Suggestion [%s] does not start with [%s]", suggestion, expectedBeginning)
            );

            // Выставим, что нашли выделенный саджест.
            highlightedSuggestFound = true;
        }

        // Убеждаемся, что в нашем случае нашелся хотя-бы один выделенный саджест.
        assertTrue(highlightedSuggestFound, "No highlighted suggest found");
    }

    @Test
    @DisplayName("Поисковый запрос выделяется жирным шрифтом")
    public void searchQueryIsBoldTest() {
        String query = "Васильеви";

        // Наберем запрос поиска и дождемся саджестов.
        navigateEnterSearchQueryWaitForSuggestions(query);

        // Собираем жирность шрифта выделенной части саджестов.
        List<String> fontWeights = homePage.getAllSuggestionsHighlightFontWeight();

        // Убедимся, что есть хотя бы 1 жирный саджест.
        assertNotEquals(0, fontWeights.size());

        // Проверяем все саджесты.
        for (String fontWeight : fontWeights) {
            // Ожидаем, что жирность будет либо 700, либо bold.
            assertTrue(
                    fontWeight.equals("700") || fontWeight.equalsIgnoreCase("bold"),
                    String.format("Font weight %s is not bold", fontWeight)
            );
        }
    }

    @Test
    @DisplayName("Переход из саджеста по поисковой фразе идет в точности на ее страницу")
    public void directSuggestNavigationTest() {
        String query = "Иван";

        // Наберем запрос поиска и дождемся саджестов.
        navigateEnterSearchQueryWaitForSuggestions(query);

        // Переходим по первому саджесту.
        WikipediaArticlePage articlePage = homePage.clickOnFirstSuggest();

        // Ожидаем появления заголовка.
        articlePage.waitForHeadingVisibility();

        // Проверяем, что заголовок соответствует запросу.
        assertEquals(query.toLowerCase(), articlePage.getHeading());
    }

    @Test
    @DisplayName("При нажатии на кнопку поиска, переходим на страницу из 1-го саджеста")
    public void searchButtonArticleNavigationTest() {
        String query = "Иван";

        // Наберем запрос поиска и дождемся саджестов.
        navigateEnterSearchQueryWaitForSuggestions(query);

        // Переходим по первому саджесту. Это должна быть страница статьи.
        WikipediaArticlePage articlePage = (WikipediaArticlePage) homePage.clickOnSearchButton();

        // Ожидаем появления заголовка.
        articlePage.waitForHeadingVisibility();

        // Проверяем, что заголовок соответствует запросу.
        assertEquals(query.toLowerCase(), articlePage.getHeading());
    }

    @Test
    @DisplayName("При нажатии на кнопку поиска, переходим на страницу поиска")
    public void searchButtonSearchNavigationTest() {
        String query = "Иванннннн";

        // Наберем запрос поиска и дождемся саджестов.
        navigateEnterSearchQueryWaitForSuggestions(query);

        // Переходим на страницу поиска.
        WikipediaSearchPage searchPage = (WikipediaSearchPage) homePage.clickOnSearchButton();

        // Ожидаем появления заголовка.
        searchPage.waitForHeadingVisibility();

        // Проверяем, что заголовок соответствует заголовку страницы поиска.
        assertEquals("результаты поиска", searchPage.getHeading());
    }

    @Test
    @DisplayName("При нажатии на подсказку \"Поиск страниц, содержащих\", переходим на страницу поиска")
    public void specialLabelButtonNavigationTest() {
        // Не автоматизирован. Подсказка появляется не всегда.
        throw new RuntimeException("Не автоматизирован");
//
//        String query = "Иван";
//
//        // Наберем запрос поиска и дождемся саджестов.
//        navigateEnterSearchQueryWaitForSuggestions(query);
//
//        // Ждем появления подсказки "Поиск страниц, содержащих".
//        homePage.waitForSpecialLabelVisibility();
//
//        // Переходим на страницу поиска.
//        WikipediaSearchPage searchPage = homePage.clickOnSpecialLabel();
//
//        // Ожидаем появления заголовка.
//        searchPage.waitForHeadingVisibility();
//
//        // Проверяем, что заголовок соответствует заголовку страницы поиска.
//        assertEquals("результаты поиска", searchPage.getHeading());
    }

    /**
     * Переходит на главную страницу Википедии.
     * Вводит поисковый запрос.
     * Ожидает блок с подсказками.
     **/
    private void navigateEnterSearchQueryWaitForSuggestions(String query) {
        // Переходим на главную страницу Википедии.
        driver.get(homePage.url);

        // Ждем, пока поле поиска прогрузится.
        homePage.waitForSearchQueryVisibility();

        // В поле поиска набираем query.
        homePage.enterSearchQuery(query);

        // Ждем, пока появится блок с подсказками.
        homePage.waitForSuggestionsVisibility();
    }
}
