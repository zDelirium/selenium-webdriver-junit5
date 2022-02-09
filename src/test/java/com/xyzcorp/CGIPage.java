package com.xyzcorp;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

public class CGIPage {
    
    private WebDriver driver;
    private Properties properties;
    static final Logger log = getLogger(lookup().lookupClass());

    /**
     * Instantiates a new CGIPageObj by 
     * @param driver
     * @param properties
     */
    public CGIPage(WebDriver driver, Properties properties) {
        this.driver = driver;
        this.properties = properties;
    }

    /**
     * Bypasses the cookie popup window by adding the cookies to the webdriver manually
     */
    void bypassCookiesPopup() {
        
        Options options = driver.manage();
        
        // Setup the necessary cookies manually
        Set<Cookie> cookies = new HashSet<>();
        
        String[] cookieNames = {
            properties.getProperty("cookie-agreed-categories-name"), 
            properties.getProperty("cookie-agreed-version-name"),
            properties.getProperty("RT-name"),
            properties.getProperty("AKA_A2-name"),
            properties.getProperty("_gat_UA-399437-1-name"),
            properties.getProperty("cookie-agreed-name"),
            properties.getProperty("_ga-name"),
            properties.getProperty("_fbp-name"),
            properties.getProperty("language-name"),
            properties.getProperty("_ga_LC0YVRL587-name"),
            properties.getProperty("_gid-name")
        };
        
        for (String cookieName : cookieNames) {
            cookies.add(new Cookie(cookieName, properties.getProperty(cookieName)));
        }
        
        // Add those cookies to the webdrivers
        for (Cookie cookie : cookies) {
            log.debug("Cookie pair added: {}={}", cookie.getName(), cookie.getValue());
            options.addCookie(cookie);
        }
        
        // Refresh the page
        driver.navigate().refresh();
        
    }

    /**
     * Switches the website language to the desired one
     * @param language Desired language
     */
    void switchToLanguage(String language) {
        driver.findElement(By.id(properties.getProperty("language-switcher-div-id"))).click();
        driver.findElement(By.linkText(language)).click();
    }

    /**
     * Performs a search using the main navigation bar on cgi.com
     * @param searchTerm search keyword
     */
    void search(String searchTerm) {
        driver.findElement(By.xpath(properties.getProperty("main-navbar-expand-search-bar-button-xpath"))).click();
        WebElement searchBar = driver.findElement(By.id(properties.getProperty("main-navbar-search-bar-input-id")));
        searchBar.sendKeys(searchTerm);
        searchBar.sendKeys(Keys.ENTER);
    }

}
