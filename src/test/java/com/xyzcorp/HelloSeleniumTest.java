package com.xyzcorp;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class HelloSeleniumTest {

    WebDriver chromeDriver;

    /**
     * Automatically sets ups chrome driver
     */
    @BeforeAll
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    /**
     * Create a new webdriver instance every test
     */
    @BeforeEach
    public void setup() {
        chromeDriver = new ChromeDriver();
    }

    /**
     * This test will go to Google and search for BDD.
     * It will ensure that it lands on the Wiki page.
     */
    @Test
    public void testSearchAndGoToBddWiki() {
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/hello-selenium-test-config.properties");
        Properties properties = new Properties();
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        chromeDriver.get(properties.getProperty("bdd.sut-url"));
        chromeDriver.manage().window().setSize(new Dimension(945, 1020));

        String searchTerm = properties.getProperty("bdd.search-term");
        chromeDriver.findElement(By.name("q")).click();
        chromeDriver.findElement(By.name("q")).sendKeys(searchTerm);
        chromeDriver.findElement(By.name("q")).sendKeys(Keys.ENTER);

        String partialLinkText = properties.getProperty("bdd.partial-link-text");
        WebElement wikiLink = chromeDriver.findElement(By.partialLinkText(partialLinkText));
        assertThat(wikiLink.getText().contains(partialLinkText));
        wikiLink.click();

        String wikiPageTitle = chromeDriver.findElement(By.id(properties.getProperty("bdd.wiki-page-title-id"))).getText();
        assertThat(wikiPageTitle.equals(searchTerm));

    }

    /**
     * Quit webdriver after every test
     */
    @AfterEach
    public void teardown() {
        chromeDriver.quit();
    }

}
