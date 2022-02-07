package com.xyzcorp;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.List;

import org.slf4j.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.locators.RelativeLocator;
import org.openqa.selenium.support.locators.RelativeLocator.RelativeBy;

import io.github.bonigarcia.wdm.WebDriverManager;

public class FindPlanetsTest {
    
    WebDriver driver;
    static final Logger log = getLogger(lookup().lookupClass());

    @BeforeAll
    static void setupClass() {
        WebDriverManager.edgedriver().setup();
    }

    @BeforeEach
    void setup() {
        driver = new EdgeDriver();
    }

    /**
     * Get the equatorial diameter of the Earth relative to the Earth in the Planet wiki page
     */ 
    @Test
    public void testGetDiameterOfEarth() {
        String sutURL = "https://en.wikipedia.org/wiki/Planet";
        driver.get(sutURL);
        driver.manage().window().setSize(new Dimension(945, 1020));
        
        WebElement earthLink = driver.findElements(By.cssSelector("td > a[title=\"Earth\"]")).get(1);
        assertThat(earthLink.getText().equals("Earth"));

        RelativeBy relativeBy = RelativeLocator.with(By.tagName("td"));
        WebElement earthDiameter = driver.findElement(relativeBy.toRightOf(earthLink));
        System.out.println("The equatorial diameter of the Earth relative to the Earth is " + earthDiameter.getText());

    }

    /**
     * Searches for the diameter of Earth without accessing it directly in the Solar System wiki page
     */
    @Test
    public void testGetAbsoluteEarthDiameterInSolarSystemWikiPage() {
        String sutURL = "https://en.wikipedia.org/wiki/Solar_System";
        driver.get(sutURL);
        driver.manage().window().setSize(new Dimension(945, 1020));

        WebElement earthTr = driver.findElement(By.xpath("//th[contains(text(), 'Earth')][1]"));
        RelativeBy relativeBy = RelativeLocator.with(By.tagName("td"));
        WebElement earthDiameter = driver.findElement(relativeBy.near(earthTr));
        System.out.println("The diameter of the Earth is " + earthDiameter.getText() + " km");

    }

    /**
     * Get absolute diameters of all major bodies (Sun + planets) in the Solar System wiki page
     */
    @Test
    public void testGetAllSolarSystemBodiesDiameters() {
        String sutURL = "https://en.wikipedia.org/wiki/Solar_System";
        driver.get(sutURL);
        driver.manage().window().setSize(new Dimension(945, 1020));

        List<WebElement> bodies = driver.findElements(By.xpath("//*[@id=\"mw-content-text\"]/div[1]/table[3]/tbody/tr/th"));
        List<WebElement> diameters = driver.findElements(By.xpath("//*[@id=\"mw-content-text\"]/div[1]/table[3]/tbody/tr/td[1]"));
        assertEquals(bodies.size(), diameters.size());

        for (int i = 0; i < bodies.size(); i++) {
            System.out.println("The diameter of " + bodies.get(i).getText().strip() + " is " + diameters.get(i).getText().strip() + " km");
        }
    }

    @AfterEach
    void teardown() {
        driver.quit();
    }
}
