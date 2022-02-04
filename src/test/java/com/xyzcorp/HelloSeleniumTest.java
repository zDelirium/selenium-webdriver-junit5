package com.xyzcorp;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class HelloSeleniumTest {

    private static ChromeDriver chromeDriver;

    /**
     * Manually sets ups chrome driver property and instantiates a new driver.
     */
    @BeforeAll
    public static void setup() {
        System.setProperty("webdriver.chrome.driver",
                "C:\\Users\\ernest.nguyen\\drivers\\chrome\\98\\chromedriver_win32\\chromedriver.exe");
        chromeDriver = new ChromeDriver();
    }

    /**
     * This test will go to Google and search for BDD.
     * It will ensure that it lands on the Wiki page.
     */
    @Test
    public void testSearchAndGoToBddWiki() {
        String rootURL = "https://www.google.com/";
        chromeDriver.get(rootURL);
        chromeDriver.manage().window().setSize(new Dimension(945, 1020));

        String searchTerm = "Behavior-driven development";
        chromeDriver.findElement(By.name("q")).click();
        chromeDriver.findElement(By.name("q")).sendKeys(searchTerm);
        chromeDriver.findElement(By.name("q")).sendKeys(Keys.ENTER);

        WebElement wikiLink = chromeDriver.findElement(By
                .cssSelector("#rso > div:nth-child(1) > div.g.tF2Cxc > div > div.NJo7tc.Z26q7c.jGGQ5e > div > a > h3"));
        assertTrue(wikiLink.getText().contains("Wikipedia"));
        wikiLink.click();

        String wikiPageTitle = chromeDriver.findElement(By.id("firstHeading")).getText();
        assertTrue(wikiPageTitle.equals(searchTerm));

    }

    /**
     * Closes the webdriver after all test cases.
     */
    @AfterAll
    public static void cleanup() {
        chromeDriver.close();
    }

}
