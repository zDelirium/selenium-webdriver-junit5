package com.xyzcorp;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;
import org.slf4j.Logger;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
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
        
        //WebElement earthLink = driver.findElement(new ByAll(By.cssSelector("td > a[title=\"Earth\"]"), By.linkText("Earth")));
        // TODO figure out a more solid way to get the earth link
        WebElement earthLink = driver.findElements(By.cssSelector("td > a[title=\"Earth\"]")).get(1);
        assertThat(earthLink.getText().equals("Earth"));

        RelativeBy relativeBy = RelativeLocator.with(By.tagName("td"));
        WebElement earthDiameter = driver.findElement(relativeBy.toRightOf(earthLink));
        System.out.println("The equatorial diameter of the Earth relative to the Earth is " + earthDiameter.getText());

    }

    // TODO get diameters of all planet in the Solar System without directly accessing the diameters itself in the Planet wiki page
    @Test
    public void testGetAllSolarSystemPlanetDiameters() {

    }

    @AfterEach
    void teardown() {
        driver.quit();
    }
}
