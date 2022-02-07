package com.xyzcorp;

import static org.assertj.core.api.Assertions.assertThat;

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
        String sutURL = "https://www.google.com/";
        chromeDriver.get(sutURL);
        chromeDriver.manage().window().setSize(new Dimension(945, 1020));

        String searchTerm = "Behavior-driven development";
        chromeDriver.findElement(By.name("q")).click();
        chromeDriver.findElement(By.name("q")).sendKeys(searchTerm);
        chromeDriver.findElement(By.name("q")).sendKeys(Keys.ENTER);

        // TODO Change findElement to link by text (or partial)
        //WebElement wikiLink = chromeDriver.findElement(By
        //        .cssSelector("#rso > div:nth-child(1) > div.g.tF2Cxc > div > div.NJo7tc.Z26q7c.jGGQ5e > div > a > h3"));
        WebElement wikiLink = chromeDriver.findElement(By.partialLinkText("Wikipedia"));
        assertThat(wikiLink.getText().contains("Wikipedia"));
        wikiLink.click();

        String wikiPageTitle = chromeDriver.findElement(By.id("firstHeading")).getText();
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
