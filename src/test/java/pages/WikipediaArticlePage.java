package pages;

import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WikipediaArticlePage {
    /**
     * Драйвер Selenium.
     */
    WebDriver driver;

    /**
     * Первый заголовок
     **/
    @FindBy(xpath = "//h1[@id='firstHeading']//span")
    WebElement heading;

    // Инициализируем поля PageObject с помощью PageFactory
    public WikipediaArticlePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    @Step("Ожидаем появления заголовка")
    public void waitForHeadingVisibility() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOf(heading));
    }

    @Step("Получаем содержимое заголовка")
    public String getHeading() {
        return heading.getText().toLowerCase();
    }
}
