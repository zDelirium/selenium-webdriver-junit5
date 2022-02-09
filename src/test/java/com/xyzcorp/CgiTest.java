package com.xyzcorp;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

public class CgiTest {

    WebDriver driver;
    Properties properties;
    
    static final Logger log = getLogger(lookup().lookupClass());
    
    /**
     * Sets up the appropriate chromedriver version
     */
    @BeforeAll
    static void setupClass() {
        
        WebDriverManager.chromedriver().setup();
        
    }
    
    /**
     * Creates a Properties object containing configuration variables and a new ChromeDriver instance that
     * @throws IOException if the config file does not exist at the expected location
     */
    @BeforeEach
    void setup() throws IOException {
        
        // Load variables from properties file
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/cgi-test-config.properties");
        properties = new Properties();
        properties.load(resourceAsStream);
        
        // Maximise window of a new ChromeDriver instance
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        
        // Go to cgi.com homepage
        driver.get(properties.getProperty("sut-url"));
        
    }
    
    /**
     * Closes down every test as to minimize memory management risks
     */
    @AfterEach
    void teardown() {
    
        if (properties != null) {
            properties = null;
        }
        if (driver != null) {
            driver.quit();
        }
    
    }

    /**
     * Accept all cookies on cgi.com and verify that there are a greater number of cookies, that there is a cookie-agreed-categories
     * and that cookie-agreed value is 2
     */
    @Test
    public void testAcceptAllCGICookies() {

        Options options = driver.manage();

        // Get the number of cookies before accepting all cookies
        int beforeAcceptCookiesNb = options.getCookies().size();
        log.debug("There are {} cookies before accepting all cookies on cgi.com", beforeAcceptCookiesNb);

        // Accept all cookies
        driver.findElement(By.xpath(properties.getProperty("accept-cookies-button-xpath"))).click();

        // Get the number of cookies after accepting all cookies
        Set<Cookie> afterCookies = options.getCookies();
        int afterAcceptCookiesNb = afterCookies.size();
        log.debug("There are {} cookies after accepting all cookies on cgi.com", afterAcceptCookiesNb);
        
        // Assert that there are more cookies than there was initially
        assertThat(afterAcceptCookiesNb).isGreaterThan(beforeAcceptCookiesNb); 

        // Assert that there is a cookie-agreed-categories cookie
        assertThat(options.getCookieNamed(properties.getProperty("cookie-agreed-categories-name"))).isNotNull();

        // Assert that all cookies were accepted
        assertThat(options.getCookieNamed(properties.getProperty("cookie-agreed-name")).getValue()).isEqualTo(properties.getProperty("cookie-agreed-value"));

        // Prints all cookies in the logs
        for (Cookie cookie: afterCookies) {
            log.debug("Cookie pair: {}={}", cookie.getName(), cookie.getValue());
        }

    }

    /**
     * Decline all cookies on cgi.com and verify that the cookie-agreed value is 0
     * and that there is no cookie-agreed-categories cookie
     */
    @Test
    public void testDeclineAllCGICookies() {

        Options options = driver.manage();

        // Decline all cookies
        driver.findElement(By.xpath(properties.getProperty("decline-cookies-button-xpath"))).click();

        // Assert that there is no cookie-agreed-categories-cookie
        assertThat(options.getCookieNamed(properties.getProperty("cookie-agreed-categories-name"))).isNull();

        // Assert that all non-required cookies were declined
        assertThat(options.getCookieNamed(properties.getProperty("cookie-agreed-name")).getValue()).isEqualTo(properties.getProperty("cookie-not-agreed-value"));

    }

    /**
     * Tests bypassing the cookie popup window.
     * @throws NumberFormatException
     * @throws InterruptedException
     */
    @Test
    public void testAccessCGINoCookiePopup() throws NumberFormatException, InterruptedException {

        Options options = driver.manage();
        // Add all the necessary cookies
        Set<Cookie> cookies = setupCookies();
        for (Cookie cookie : cookies) {
            log.debug("Cookie pair added: {}={}", cookie.getName(), cookie.getValue());
            options.addCookie(cookie);
        }

        // Refresh cgi.com homepage
        driver.navigate().refresh();

        // Wait and assert that the cookie popup does not appear
        Thread.sleep(Duration.ofSeconds(Integer.parseInt(properties.getProperty("default-sleep-time"))).toMillis());
        assertThatThrownBy(() -> driver.findElement(By.id(properties.getProperty("cookie-popup-id")))).isInstanceOf(NoSuchElementException.class);

    }

    /**
     * Goes to CGI.com, validates that it starts in English, switch to French,
     * makes a search and validates that the page is still in French
     */
    @Test
    public void testFrenchTranslation() {

        String enLang = "en-lang", frLang = "fr-lang";
        String languageSwitcherDivElement = "language-switcher-div-id";
        String switchToFRText = "switch-to-fr-link-text";

        // Go to cgi.com
        driver.get(properties.getProperty("sut-url"));

        // Bypass cookies (accepted)
        driver.findElement(By.xpath(properties.getProperty("accept-cookies-button-xpath"))).click();

        // Assert that current language is English
        assertLanguage(enLang);

        // Switch to French and assert that it did so
        driver.findElement(By.id(properties.getProperty(languageSwitcherDivElement))).click();
        driver.findElement(By.linkText(properties.getProperty(switchToFRText))).click();
        assertLanguage(frLang);

        // Make a search (in French)
        driver.findElement(By.xpath(properties.getProperty("main-navbar-expand-search-bar-button-xpath"))).click();
        WebElement searchBar = driver.findElement(By.id(properties.getProperty("main-navbar-search-bar-input-id")));
        searchBar.sendKeys(properties.getProperty("fr-search-value"));
        searchBar.sendKeys(Keys.ENTER);

        // Wait until the search results div load
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(Integer.parseInt(properties.getProperty("default-max-wait-time"))));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(properties.getProperty("search-results-div-id"))));

        // Assert that it is still in French
        assertLanguage(frLang);

    }

    /**
     * Asserts that the language of website is the one that it should be
     * @param lang The language of interest
     */
    private void assertLanguage(String lang) {
        assertThat(driver.getCurrentUrl().split("/")[3]).isEqualTo(properties.getProperty(lang));
        assertThat(driver.findElement(By.id(properties.getProperty("language-switcher-div-id"))).getText()).isEqualToIgnoringCase(properties.getProperty(lang));
    }

    /**
     * Manually sets up cookies that are necessary for bypassing cgi.com cookies popup
     * Not a good way to go at it to be honest
     * @return A set of all accepted cgi cookies
     */
    private Set<Cookie> setupCookies() {

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

        return cookies;

    }

}
