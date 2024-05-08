package cl.bilix.scrapper.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import cl.bilix.scrapper.helpers.Terminal;
import cl.bilix.scrapper.helpers.WebScrapperException;
import cl.bilix.scrapper.helpers.WebScrapperMessage;
import cl.bilix.scrapper.properties.Properties;

public class Execute {
    public static void apply(Properties properties) throws WebScrapperException, Exception {
        WebDriver driver = new ChromeDriver();
        try {
            driver.get(properties.getUrl());
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(properties.getTimeout()));
            switch (properties.getMap()) {
                case Terminal.PC -> pc(driver, properties);
                case Terminal.STI -> sti(driver, properties);
                case Terminal.SILOGPORT -> silogport(driver, properties);
                case Terminal.TPS -> tps(driver, properties);
                default -> throw new WebScrapperException(WebScrapperMessage.UNINMPLEMENTED);
            }
            ;
        } finally {
            driver.quit();
        }
    }

    private static void pc(WebDriver driver, Properties properties) throws WebScrapperException, Exception {
        final WebElement form = driver.findElement(By.name("loginForm"));
        final List<WebElement> errors = new ArrayList<WebElement>(
                Arrays.asList(driver.findElement(By.xpath("//div[@ng-show='authenticationDisabledError']")),
                        driver
                                .findElement(
                                        By.xpath("//div[@ng-show='authenticationError && !authenticationRetries']")),
                        driver
                                .findElement(
                                        By.xpath("//div[@ng-show='authenticationError && authenticationRetries']"))));
        final WebElement userName = form.findElement(By.name("username"));
        final WebElement password = form.findElement(By.name("password"));
        final WebElement submitButton = form.findElement(By.xpath("//button[@type='submit']"));

        // INGRESO
        userName.sendKeys(properties.getUsername());
        password.sendKeys(properties.getPassword());
        submitButton.click();

        // COMPROBAR LOGIN EXITOSO
        try {
            Wait<WebDriver> wait = new WebDriverWait(driver,
                    Duration.ofSeconds(properties.getTimeout()));
            errors.forEach(error -> wait.until(ExpectedConditions.stalenessOf(error)));
        } catch (TimeoutException e) {
            throw new WebScrapperException(WebScrapperMessage.UNAUTHORIZED, e);
        }
    }

    private static void sti(WebDriver driver, Properties properties) {
        final WebElement form = driver.findElement(By.name("formulario"));
        final WebElement userName = form.findElement(By.name("userd"));
        final WebElement password = form.findElement(By.name("pass"));
        final WebElement submitButton = form.findElement(By.name("accion2"));
        // INGRESO
        userName.sendKeys(properties.getUsername());
        password.sendKeys(properties.getPassword());
        submitButton.submit();
    }

    private static void silogport(WebDriver driver, Properties properties) {
        final WebElement form = driver.findElement(By.id("kc-form"));
        final WebElement userName = form.findElement(By.name("username"));
        final WebElement password = form.findElement(By.name("password"));
        final WebElement submitButton = form.findElement(By.name("login"));
        // INGRESO
        userName.sendKeys(properties.getUsername());
        password.sendKeys(properties.getPassword());
        submitButton.submit();
    }

    private static void tps(WebDriver driver, Properties properties) {
        final WebElement form = driver.findElement(By.id("tps_login_form"));
        final WebElement userName = form.findElement(By.name("correo"));
        final WebElement password = form.findElement(By.name("clave"));
        final WebElement submitButton = form.findElement(By.id("tps_login_button"));
        // INGRESO
        userName.sendKeys(properties.getUsername());
        password.sendKeys(properties.getPassword());
        submitButton.submit();
    }
}
