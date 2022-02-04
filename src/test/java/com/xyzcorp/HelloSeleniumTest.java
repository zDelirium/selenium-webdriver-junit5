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

    // Using a chrome webdriver for this example
    private static ChromeDriver chromeDriver;

    @BeforeAll
    public static void setup() {
        // Setting up property manually. A true pain in the you know what. An absolute
        // path too at that
        System.setProperty("webdriver.chrome.driver",
                "C:\\Users\\ernest.nguyen\\drivers\\chrome\\98\\chromedriver_win32\\chromedriver.exe");

        // Instantiate a new driver
        chromeDriver = new ChromeDriver();
    }

    @Test
    public void testSearchAndGoToBddWiki() {
        // Open google.com
        String rootURL = "https://www.google.com/";
        chromeDriver.get(rootURL);
        chromeDriver.manage().window().setSize(new Dimension(945, 1020));

        // Click search box, enter search term and search
        String searchTerm = "Behavior-driven development";
        chromeDriver.findElement(By.name("q")).click();
        chromeDriver.findElement(By.name("q")).sendKeys(searchTerm);
        chromeDriver.findElement(By.name("q")).sendKeys(Keys.ENTER);

        // Click the first link: it should be a wiki link
        WebElement wikiLink = chromeDriver.findElement(By
                .cssSelector("#rso > div:nth-child(1) > div.g.tF2Cxc > div > div.NJo7tc.Z26q7c.jGGQ5e > div > a > h3"));
        assertTrue(wikiLink.getText().contains("Wikipedia"));
        wikiLink.click();

        // Assert that the title of the wiki page is "Behavior-driven development"
        String wikiPageTitle = chromeDriver.findElement(By.id("firstHeading")).getText();
        assertTrue(wikiPageTitle.equals(searchTerm));

    }

    @AfterAll
    public static void cleanup() {
        chromeDriver.close(); // ALWAYS
    }

}
