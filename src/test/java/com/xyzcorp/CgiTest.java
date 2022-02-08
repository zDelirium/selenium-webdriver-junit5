package com.xyzcorp;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

public class CgiTest {

    WebDriver driver;
    WebDriverWait wait;
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
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/cgi-test-config.properties");
        properties = new Properties();
        properties.load(resourceAsStream);
    
        driver = new ChromeDriver();
        driver.manage().window().maximize();

    }

    /**
     * Accept all cookies on cgi.com and verify that there are a greater number of cookies, that there is a cookie-agreed-categories
     * and that cookie-agreed value is 2
     */
    @Test
    public void testAcceptAllCGICookies() {
        driver.get(properties.getProperty("sut-url"));

        Options options = driver.manage();

        int beforeAcceptCookiesNb = options.getCookies().size();
        log.debug("There are {} cookies before accepting all cookies on cgi.com", beforeAcceptCookiesNb);

        driver.findElement(By.xpath(properties.getProperty("accept-cookies-button-xpath"))).click();;

        Set<Cookie> afterCookies = options.getCookies();
        int afterAcceptCookiesNb = afterCookies.size();
        log.debug("There are {} cookies after accepting all cookies on cgi.com", afterAcceptCookiesNb);
        
        assertThat(afterAcceptCookiesNb).isGreaterThan(beforeAcceptCookiesNb); 
        assertThat(options.getCookieNamed(properties.getProperty("cookie-agreed-categories-name"))).isNotNull();
        assertThat(options.getCookieNamed(properties.getProperty("cookie-agreed-name")).getValue()).isEqualTo(properties.getProperty("cookie-agreed-value"));

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
        driver.get(properties.getProperty("sut-url"));

        Options options = driver.manage();

        WebElement declineCookieButton = driver.findElement(By.xpath(properties.getProperty("decline-cookies-button-xpath")));
        declineCookieButton.click();

        assertThat(options.getCookieNamed(properties.getProperty("cookie-agreed-categories-name"))).isNull();
        assertThat(options.getCookieNamed(properties.getProperty("cookie-agreed-name")).getValue()).isEqualTo(properties.getProperty("cookie-not-agreed-value"));
    }

    /**
     * Tests bypassing the cookie popup window.
     * @throws NumberFormatException
     * @throws InterruptedException
     */
    @Test
    public void testAccessCGINoCookiePopup() throws NumberFormatException, InterruptedException {
        driver.get(properties.getProperty("sut-url"));

        Options options = driver.manage();

        Set<Cookie> cookies = setupCookies();
        for (Cookie cookie : cookies) {
            log.debug("Cookie pair added: {}={}", cookie.getName(), cookie.getValue());
            options.addCookie(cookie);
        }

        driver.navigate().refresh();
        //driver.get(properties.getProperty("sut-url"));
        Thread.sleep(Long.parseLong(properties.getProperty("default-sleep-time")));
        assertThat(driver.findElements(By.id(properties.getProperty("cookie-popup-id")))).isEmpty();;

    }

    /**
     * Closes down every test as to minimize the risk of 
     */
    @AfterEach
    void teardown() {
        properties = null;
        wait = null;
        driver.quit();
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
