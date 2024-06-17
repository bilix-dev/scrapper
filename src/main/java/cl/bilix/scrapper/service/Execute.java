package cl.bilix.scrapper.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import cl.bilix.scrapper.dto.Input;
import cl.bilix.scrapper.helpers.Terminal;
import cl.bilix.scrapper.helpers.WebScrapperException;
import cl.bilix.scrapper.helpers.WebScrapperMessage;

public class Execute {
    public static void apply(Input input) throws WebScrapperException, Exception {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options);
        try {
            driver.get(input.getUrl());
            // driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(input.getTimeout()));
            switch (input.getTerminal()) {
                case Terminal.PC -> pc(driver, input);
                case Terminal.STI -> sti(driver, input);
                case Terminal.SILOGPORT -> silogport(driver, input);
                case Terminal.TPS -> tps(driver, input);
                default -> throw new WebScrapperException(WebScrapperMessage.UNINMPLEMENTED);
            }
            ;
        } finally {
            driver.quit();
        }
    }

    private static void pc(WebDriver driver, Input input)
            throws WebScrapperException, Exception {

        Wait<WebDriver> wait = new WebDriverWait(driver,
                Duration.ofSeconds(input.getTimeout()));

        // try {

        // } catch (TimeoutException e) {
        // throw new WebScrapperException(WebScrapperMessage.UNAUTHORIZED, e);
        // }

        final WebElement form = driver.findElement(By.name("loginForm"));

        final WebElement userName = form.findElement(By.name("username"));
        final WebElement password = form.findElement(By.name("password"));
        final WebElement submitButton = form.findElement(By.xpath("//button[@type='submit']"));

        // INGRESO
        userName.sendKeys(input.getUserName());
        password.sendKeys(input.getPassword());
        submitButton.click();

        // COMPROBAR LOGIN EXITOSO
        try {
            final List<WebElement> errors = new ArrayList<WebElement>(
                    Arrays.asList(driver.findElement(By.xpath("//div[@ng-show='authenticationDisabledError']")),
                            driver
                                    .findElement(
                                            By.xpath(
                                                    "//div[@ng-show='authenticationError && !authenticationRetries']")),
                            driver
                                    .findElement(
                                            By.xpath(
                                                    "//div[@ng-show='authenticationError && authenticationRetries']"))));
            errors.forEach(error -> wait.until(ExpectedConditions.stalenessOf(error)));
        } catch (TimeoutException e) {
            throw new WebScrapperException(WebScrapperMessage.UNAUTHORIZED, e);
        }

        // PESTAÑA EXPORTACION
        final WebElement exportAnchor = driver.findElement(By.xpath("//a[@ui-sref='exports']"));
        exportAnchor.sendKeys(Keys.ENTER);

        // TIPEAR CONTENEDOR
        final WebElement container = driver.findElement(By.id("containerSearch"));
        container.sendKeys(input.getPayload().getContainer() + Keys.ENTER);

        // COMPROBAR CONTENEDOR
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[text()='Nuevo Preaviso']")));
        } catch (TimeoutException e) {
            throw new WebScrapperException(WebScrapperMessage.ERROR, e);
        }

        final WebElement expoForm = driver.findElement(By.name("expoForm"));
        final WebElement booking = expoForm.findElement(By.name("booking"));
        booking.sendKeys(input.getPayload().getBooking() + Keys.ENTER);

    }

    private static void sti(WebDriver driver, Input input) {
        final WebElement form = driver.findElement(By.name("formulario"));
        final WebElement userName = form.findElement(By.name("userd"));
        final WebElement password = form.findElement(By.name("pass"));
        final WebElement submitButton = form.findElement(By.name("accion2"));
        // INGRESO
        userName.sendKeys(input.getUserName());
        password.sendKeys(input.getPassword());
        submitButton.submit();
    }

    private static void silogport(WebDriver driver, Input input) {
        final WebElement form = driver.findElement(By.id("kc-form"));
        final WebElement userName = form.findElement(By.name("username"));
        final WebElement password = form.findElement(By.name("password"));
        final WebElement submitButton = form.findElement(By.name("login"));
        // INGRESO
        userName.sendKeys(input.getUserName());
        password.sendKeys(input.getPassword());
        submitButton.submit();
    }

    private static void tps(WebDriver driver, Input input) {
        final WebElement form = driver.findElement(By.id("tps_login_form"));
        final WebElement userName = form.findElement(By.name("correo"));
        final WebElement password = form.findElement(By.name("clave"));
        final WebElement submitButton = form.findElement(By.id("tps_login_button"));
        // INGRESO
        userName.sendKeys(input.getUserName());
        password.sendKeys(input.getPassword());
        submitButton.submit();
    }
}
