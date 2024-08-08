package cl.bilix.scrapper.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import cl.bilix.scrapper.dto.Input;
import cl.bilix.scrapper.helpers.Terminal;
import cl.bilix.scrapper.helpers.WebScrapperException;
import cl.bilix.scrapper.helpers.WebScrapperMessage;

public class Execute {
        public static String apply(Input input) throws WebScrapperException, Exception {
                ChromeOptions options = new ChromeOptions();
                if (input.isHeadless()) {
                        options.addArguments("--headless");
                }
                WebDriver driver = new ChromeDriver(options);
                try {
                        driver.get(input.getUrl());
                        switch (input.getTerminal()) {
                                case Terminal.PC -> pc(driver, input);
                                case Terminal.STI -> sti(driver, input);
                                case Terminal.SILOGPORT -> silogport(driver, input);
                                case Terminal.TPS -> tps(driver, input);
                                default -> throw new WebScrapperException(WebScrapperMessage.UNINMPLEMENTED);
                        }
                        ;
                        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);
                } finally {
                        driver.quit();
                }
        }

        private static void pc(WebDriver driver, Input input)
                        throws WebScrapperException, Exception {

                JavascriptExecutor js = (JavascriptExecutor) driver;

                Wait<WebDriver> wait = new WebDriverWait(driver,
                                Duration.ofSeconds(input.getTimeout()));

                Wait<WebDriver> wait_modal = new WebDriverWait(driver,
                                Duration.ofSeconds(60));

                final WebElement form = driver.findElement(By.name("loginForm"));

                final WebElement userName = form.findElement(By.name("username"));
                final WebElement password = form.findElement(By.name("password"));
                final WebElement submitButton = form.findElement(By.xpath("//button[@type='submit']"));

                // INGRESO
                userName.sendKeys(input.getUserName());
                password.sendKeys(input.getPassword());
                js.executeScript("arguments[0].click();", submitButton);

                // COMPROBAR LOGIN EXITOSO
                try {
                        final List<WebElement> errors = new ArrayList<WebElement>(
                                        Arrays.asList(driver.findElement(
                                                        By.xpath("//div[@ng-show='authenticationDisabledError']")),
                                                        driver
                                                                        .findElement(
                                                                                        By.xpath(
                                                                                                        "//div[@ng-show='authenticationError && !authenticationRetries']")),
                                                        driver
                                                                        .findElement(
                                                                                        By.xpath(
                                                                                                        "//div[@ng-show='authenticationError && authenticationRetries']"))));
                        errors.forEach(error -> wait.until(ExpectedConditions.stalenessOf(error)));
                        // PANTALLA DE CAMBIO DE PASSWORD
                        wait.until(ExpectedConditions
                                        .not(ExpectedConditions.visibilityOfElementLocated(
                                                        By.xpath("//div[@ng-show='changePassError']"))));

                } catch (TimeoutException e) {
                        throw new WebScrapperException(WebScrapperMessage.UNAUTHORIZED, e);
                }

                try {
                        // PESTAÑA EXPORTACION
                        final By exports_path = By.xpath("//a[@ui-sref='exports']");
                        wait.until(ExpectedConditions.visibilityOfElementLocated(exports_path));
                        final WebElement exportAnchor = driver.findElement(exports_path);
                        exportAnchor.sendKeys(Keys.ENTER);

                        // TIPEAR CONTENEDOR
                        final By container_path = By.id("containerSearch");
                        wait.until(ExpectedConditions.visibilityOfElementLocated(container_path));
                        final WebElement container = driver.findElement(container_path);
                        container.sendKeys(input.getPayload().getContainer());
                        container.sendKeys(Keys.ENTER);

                        // COMPROBAR BOOKING
                        final By booking_path = By.id("booking");
                        wait.until(ExpectedConditions.visibilityOfElementLocated(booking_path));
                        final WebElement booking = driver.findElement(booking_path);
                        booking.sendKeys(input.getPayload().getBooking());
                        booking.sendKeys(Keys.ENTER);

                        wait_modal
                                        .until(ExpectedConditions.invisibilityOfElementLocated(
                                                        By.xpath("//div[@modal-render='true']")));

                        // COMPROBAR SHIPPER
                        final By shipper_path = By.name("shipperId");
                        wait.until(ExpectedConditions.visibilityOfElementLocated(shipper_path));
                        final WebElement shipper = driver.findElement(shipper_path);
                        shipper.sendKeys(input.getPayload().getClientRut());
                        shipper.sendKeys(Keys.ENTER);
                        // TIPO CONTENEDOR
                        final By type_path = By.xpath("//select[@ng-model='booking.item']");
                        final WebElement type = driver.findElement(type_path);

                        Select select = new Select(type);
                        select.getOptions().get(1).click();

                        final WebElement weight = driver.findElement(By.name("grossweight"));
                        weight.sendKeys(input.getPayload().getWeight());

                        final WebElement tariff = driver.findElement(By.name("commodity"));
                        tariff.sendKeys(input.getPayload().getTariffCode());

                        // ESPERAR A QUE SE OCULTE EL MODAL
                        wait_modal
                                        .until(ExpectedConditions.invisibilityOfElementLocated(
                                                        By.xpath("//div[@modal-render='true']")));

                        final WebElement vgm = driver.findElement(By.id("radioVgm"));
                        js.executeScript("arguments[0].click();", vgm);

                        final WebElement micdta = driver.findElement(By.xpath("//input[@value='MICDTA']"));
                        js.executeScript("arguments[0].click();", micdta);

                        // final WebElement tipo_doc = driver.findElement(By.name("tipoDoc"));
                        final WebElement tipoo_doc = driver
                                        .findElement(By.xpath(
                                                        "//tags-input[@name='tipoDoc']//input[@ng-model='newTag.text']"));
                        tipoo_doc.sendKeys(input.getPayload().getMicdta());
                        tipoo_doc.sendKeys(Keys.ENTER);

                        final WebElement gd = driver
                                        .findElement(By.xpath(
                                                        "//tags-input[@name='guia']//input[@ng-model='newTag.text']"));
                        gd.sendKeys(input.getPayload().getMicdta());
                        gd.sendKeys(Keys.ENTER);

                        final WebElement inspeccion = driver.findElement(By.xpath("//input[@value='SIN INSPECCION']"));
                        js.executeScript("arguments[0].click();", inspeccion);

                        final WebElement seal = driver
                                        .findElement(By.xpath(
                                                        "//tags-input[@name='seals']//input[@ng-model='newTag.text']"));
                        seal.sendKeys(input.getPayload().getSeal());
                        seal.sendKeys(Keys.ENTER);

                        final WebElement truckingExport = driver
                                        .findElement(By.name("truckingExport"));

                        truckingExport.sendKeys(input.getPayload().getDispatcher());
                        truckingExport.sendKeys(Keys.ENTER);

                        final WebElement security = driver.findElement(By.name("uniqueInvoice"));
                        js.executeScript("arguments[0].click();", security);

                        final WebElement prealert = driver
                                        .findElement(By.xpath("//button[@ng-click='openVGMModal()']"));
                        js.executeScript("arguments[0].click();", prealert);

                        // FINALIZAR PROCESO
                        if (input.isEnd()) {
                                final By end_path = By.id("aceptModal");
                                wait.until(ExpectedConditions.visibilityOfElementLocated(end_path));
                                final WebElement end = driver.findElement(end_path);
                                js.executeScript("arguments[0].click();", end);
                                wait_modal
                                                .until(ExpectedConditions.invisibilityOfElementLocated(
                                                                By.xpath("//div[@modal-render='true']")));
                        }
                } catch (TimeoutException e) {
                        throw new WebScrapperException(WebScrapperMessage.ERROR, e);
                }
        }

        private static void sti(WebDriver driver, Input input) throws WebScrapperException, Exception {

                Wait<WebDriver> wait = new WebDriverWait(driver,
                                Duration.ofSeconds(input.getTimeout()));

                JavascriptExecutor js = (JavascriptExecutor) driver;

                try {
                        final By form_path = By.name("formulario");
                        wait.until(ExpectedConditions.visibilityOfElementLocated(form_path));
                        // INGRESO
                        final WebElement form = driver.findElement(form_path);
                        final WebElement userName = form.findElement(By.name("userd"));
                        final WebElement password = form.findElement(By.name("pass"));
                        final WebElement submitButton = form.findElement(By.name("accion2"));
                        userName.sendKeys(input.getUserName());
                        password.sendKeys(input.getPassword());
                        submitButton.submit();

                } catch (TimeoutException e) {
                        throw new WebScrapperException(WebScrapperMessage.UNAUTHORIZED, e);
                }

                try {
                        final WebElement visar = driver
                                        .findElement(By.xpath("//a[contains(text(),'Visar contenedor')]"));

                        driver.get(visar.getAttribute("href"));

                        final By booking_path = By.id("reserva");
                        wait.until(ExpectedConditions.visibilityOfElementLocated(booking_path));

                        final WebElement booking = driver.findElement(booking_path);

                        booking.sendKeys(input.getPayload().getBooking());

                        final WebElement buscar = driver.findElement(By.name("Buscar"));
                        buscar.submit();

                        final By nav_path = By
                                        .xpath("//td[contains(text(),'" + input.getPayload().getOperation()
                                                        + "')]/./..");

                        wait.until(ExpectedConditions.visibilityOfElementLocated(nav_path));
                        // INGRESO
                        final WebElement nav_tr = driver.findElement(nav_path);
                        final WebElement nav = nav_tr.findElement(By.xpath("td/a"));
                        driver.get(nav.getAttribute("href"));

                        final By nv_path = By
                                        .xpath("//td//a[contains(text(),'" + input.getPayload().getShippingCompany()
                                                        + "')]");

                        wait.until(ExpectedConditions.visibilityOfElementLocated(nv_path));
                        final WebElement nv = driver.findElement(nv_path);
                        driver.get(nv.getAttribute("href"));

                        final By visacion_path = By.xpath("//td/a");
                        wait.until(ExpectedConditions.visibilityOfElementLocated(visacion_path));
                        final WebElement visacion = driver.findElement(visacion_path);
                        driver.get(visacion.getAttribute("href"));

                        final By table_path = By.id("tbody_visados");
                        wait.until(ExpectedConditions.visibilityOfElementLocated(table_path));
                        // tbody_visados
                        final WebElement table = driver.findElement(table_path);

                        final WebElement table_item = table.findElement(By.xpath("tr//td[text()=' 0-']"));
                        js.executeScript("arguments[0].click();", table_item);

                        final By form_path = By.id("form1");
                        wait.until(ExpectedConditions.visibilityOfElementLocated(form_path));

                        final WebElement cod_sigla = driver.findElement(By.id("cod_sigla"));
                        final WebElement cod_numero = driver.findElement(By.id("cod_numero"));
                        final WebElement cod_digito = driver.findElement(By.id("cod_digito"));
                        final WebElement cnt_guardar = driver.findElement(By.id("Guardar"));

                        final String sigla = input.getPayload().getContainer().substring(0, 4);
                        final String numero = input.getPayload().getContainer().substring(4, 10);
                        final String digito = input.getPayload().getContainer().substring(10, 11);

                        cod_sigla.sendKeys(sigla);
                        cod_numero.sendKeys(numero);
                        cod_digito.sendKeys(digito);
                        js.executeScript("arguments[0].click();", cnt_guardar);

                        final By peso_neto_path = By.id("peso_neto");
                        wait.until(ExpectedConditions.visibilityOfElementLocated(peso_neto_path));

                        final WebElement peso_neto = driver.findElement(peso_neto_path);
                        peso_neto.sendKeys(input.getPayload().getWeight());

                        final WebElement patente = driver.findElement(By.id("patente"));
                        patente.sendKeys("XXX");

                        final WebElement seal = driver.findElement(By.id("dw_sellos"));
                        seal.sendKeys(input.getPayload().getSeal());

                        final WebElement seal_btn = driver.findElement(By.id("EnviarSellos"));
                        js.executeScript("arguments[0].click();", seal_btn);

                        final WebElement type = driver.findElement(By.id("eligeDoc"));
                        Select select = new Select(type);
                        select.getOptions().get(8).click();

                        final WebElement dw_micdta = driver.findElement(By.id("dw_dus"));
                        dw_micdta.sendKeys(input.getPayload().getMicdta());

                        final WebElement dw_micdta_btn = driver.findElement(By.id("EnviarDus"));
                        js.executeScript("arguments[0].click();", dw_micdta_btn);

                        final WebElement dw_guia = driver.findElement(By.id("dw_guia"));
                        dw_guia.sendKeys(input.getPayload().getGd());

                        final WebElement dw_guia_btn = driver.findElement(By.id("EnviarGuia"));
                        js.executeScript("arguments[0].click();", dw_guia_btn);

                        final WebElement rut_factura = driver.findElement(By.id("rut_factura"));

                        final String rut = input.getPayload().getClientRut().substring(0,
                                        input.getPayload().getClientRut().length() - 1);

                        final String dv = input.getPayload().getClientRut()
                                        .substring(input.getPayload().getClientRut().length() - 1);

                        rut_factura.sendKeys(rut + "-" + dv);

                        final WebElement rut_btn = driver
                                        .findElement(By.xpath("//div[@class='facturacion_visa_expo_body']/button"));
                        js.executeScript("arguments[0].click();", rut_btn);

                        final WebElement checkverificado = driver.findElement(By.id("checkverificado"));
                        js.executeScript("arguments[0].click();", checkverificado);
                        ;

                        final WebElement peso_verificado = driver.findElement(By.id("peso_verificado"));
                        peso_verificado.sendKeys(input.getPayload().getVgmWeight());

                        final WebElement checkmetodo2 = driver.findElement(By.id("checkmetodo2"));
                        js.executeScript("arguments[0].click();", checkmetodo2);

                        final WebElement flg_emp_pesa_extranjera = driver.findElement(By.id("flg_emp_pesa_extranjera"));
                        js.executeScript("arguments[0].click();", flg_emp_pesa_extranjera);

                        final WebElement nombre_empresa_pesaje = driver.findElement(By.id("nombre_empresa_pesaje"));
                        nombre_empresa_pesaje.sendKeys(input.getPayload().getBusinessName());

                        final WebElement rep_empresa_pesaje = driver.findElement(By.id("rep_empresa_pesaje"));
                        rep_empresa_pesaje.sendKeys(input.getPayload().getBusinessName());

                        final WebElement checkacepto = driver.findElement(By.id("checkacepto"));
                        js.executeScript("arguments[0].click();", checkacepto);

                        if (input.isEnd()) {
                                final WebElement save = driver.findElement(
                                                By.xpath("//input[@id='Guardar' and @value='Guardar']"));
                                js.executeScript("arguments[0].click();", save);
                        }
                } catch (TimeoutException e) {
                        throw new WebScrapperException(WebScrapperMessage.ERROR, e);
                }

        }

        private static void silogport(WebDriver driver, Input input) throws WebScrapperException, Exception {

                Wait<WebDriver> wait = new WebDriverWait(driver,
                                Duration.ofSeconds(input.getTimeout()));

                try {
                        final By form_path = By.id("kc-form");
                        wait.until(ExpectedConditions.visibilityOfElementLocated(form_path));
                        // INGRESO
                        final WebElement form = driver.findElement(form_path);
                        final WebElement userName = form.findElement(By.name("username"));
                        final WebElement password = form.findElement(By.name("password"));
                        final WebElement submitButton = form.findElement(By.name("login"));
                        // INGRESO
                        userName.sendKeys(input.getUserName());
                        password.sendKeys(input.getPassword());
                        submitButton.submit();

                } catch (TimeoutException e) {
                        throw new WebScrapperException(WebScrapperMessage.UNAUTHORIZED, e);
                }

                try {
                        driver.get(input.getUrl() + "/documentary-contingency/micdta");
                        final By ship_path = By.xpath("//app-seeker[@controlname='ship']/p-autocomplete/span/input");
                        wait.until(ExpectedConditions.visibilityOfElementLocated(ship_path));
                        final WebElement ship = driver.findElement(ship_path);
                        ship.sendKeys(input.getPayload().getShip());
                        final By ship_select_path = By
                                        .xpath("//span[contains(text(),'" + input.getPayload().getShip() + "')]");
                        wait.until(ExpectedConditions.visibilityOfElementLocated(ship_select_path));
                        final WebElement ship_select = driver.findElement(ship_select_path);
                        ship_select.click();

                        final WebElement custom = driver.findElement(
                                        By.xpath("//app-seeker[@controlname='custom']/p-autocomplete/span/input"));
                        custom.sendKeys(input.getPayload().getCustom());

                        final By custom_select_path = By
                                        .xpath("//span[contains(text(),'" + input.getPayload().getCustom() + "')]");
                        wait.until(ExpectedConditions.visibilityOfElementLocated(custom_select_path));
                        final WebElement custom_select = driver.findElement(custom_select_path);
                        custom_select.click();

                        final WebElement micdta = driver
                                        .findElement(By.xpath("//input[@formcontrolname='documentNumber']"));

                        micdta.sendKeys(input.getPayload().getMicdta());

                        final WebElement booking = driver
                                        .findElement(By.xpath("//input[@formcontrolname='booking']"));
                        booking.sendKeys(input.getPayload().getBooking());

                        final WebElement country = driver.findElement(
                                        By.xpath("//app-seeker[@controlname='driverCountry']/p-autocomplete/span/input"));
                        country.sendKeys(input.getPayload().getCountry());

                        final By country_select_path = By
                                        .xpath("//span[contains(text(),'" + input.getPayload().getCountry() + "')]");
                        wait.until(ExpectedConditions.visibilityOfElementLocated(country_select_path));
                        final WebElement country_select = driver.findElement(country_select_path);
                        country_select.click();

                        wait.until(ExpectedConditions.invisibilityOfElementLocated(country_select_path));

                        final WebElement dni = driver
                                        .findElement(By.xpath("//input[@formcontrolname='driverIdentifier']"));
                        dni.sendKeys(input.getPayload().getDni());

                        final WebElement plate_country = driver.findElement(
                                        By.xpath("//app-seeker[@controlname='plateCountry']/p-autocomplete/span/input"));
                        plate_country.sendKeys(input.getPayload().getCountry());

                        final By plate_country_select_path = By
                                        .xpath("//span[contains(text(),'" + input.getPayload().getCountry() + "')]");
                        wait.until(ExpectedConditions.visibilityOfElementLocated(plate_country_select_path));
                        final WebElement plate_country_select = driver.findElement(plate_country_select_path);
                        plate_country_select.click();

                        final WebElement plateNumber = driver
                                        .findElement(By.xpath("//input[@formcontrolname='plateIdentifier']"));
                        plateNumber.sendKeys(input.getPayload().getPlateNumber());

                        final WebElement button = driver.findElement(By.xpath("//span[text()='Agregar Carga']"));
                        button.click();

                        final WebElement cargo_type = driver
                                        .findElement(By.xpath("//p-dropdown[@formcontrolname='cargoType']"));
                        cargo_type.click();

                        final By cargo_type_select_path = By
                                        .xpath("//span[contains(text(),'CONTENEDORIZADA')]");
                        wait.until(ExpectedConditions.visibilityOfElementLocated(cargo_type_select_path));
                        final WebElement cargo_type_select = driver
                                        .findElement(cargo_type_select_path);
                        cargo_type_select.click();

                        final WebElement container_type = driver
                                        .findElement(By.xpath("//p-dropdown[@formcontrolname='packType']"));
                        container_type.click();

                        final By container_type_select_path = By
                                        .xpath("//span[contains(text(),'" + input.getPayload().getContainerType()
                                                        + "')]");
                        wait.until(ExpectedConditions.visibilityOfElementLocated(container_type_select_path));
                        final WebElement container_type_select = driver
                                        .findElement(container_type_select_path);
                        container_type_select.click();

                        final WebElement container = driver
                                        .findElement(By.xpath("//input[@formcontrolname='cargoNumber']"));

                        final String contenedor = input.getPayload().getContainer().substring(0, 10);
                        final String digito = input.getPayload().getContainer().substring(10, 11);
                        container.sendKeys(contenedor + "-" + digito);

                        final WebElement amount = driver
                                        .findElement(By.xpath("//input[@formcontrolname='amount']"));
                        amount.sendKeys("1");

                        final WebElement confirm_button = driver
                                        .findElement(By.xpath(
                                                        "//div[@class='micdta-item-dialog-buttons']/button[text()='Guardar']"));

                        confirm_button.click();

                        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//p-dynamicdialog")));

                        final WebElement header_checkbox = driver
                                        .findElement(By.xpath(
                                                        "// p-tableheadercheckbox"));
                        header_checkbox.click();

                        if (input.isEnd()) {
                                final WebElement end_button = driver
                                                .findElement(By.xpath("//span[text()='Inyectar MICDTA']"));
                                end_button.click();
                                wait.until(ExpectedConditions
                                                .visibilityOfElementLocated(By.xpath("//p-dynamicdialog")));
                        }

                } catch (TimeoutException e) {
                        throw new WebScrapperException(WebScrapperMessage.ERROR, e);
                }

        }

        private static void tps(WebDriver driver, Input input) throws WebScrapperException, Exception {
                Wait<WebDriver> wait = new WebDriverWait(driver,
                                Duration.ofSeconds(input.getTimeout()));

                try {
                        final By form_path = By.id("tps_login_form");
                        wait.until(ExpectedConditions.visibilityOfElementLocated(form_path));
                        final WebElement form = driver.findElement(form_path);
                        final WebElement userName = form.findElement(By.name("correo"));
                        final WebElement password = form.findElement(By.name("clave"));
                        final WebElement submitButton = form.findElement(By.id("tps_login_button"));
                        // INGRESO
                        userName.sendKeys(input.getUserName());
                        password.sendKeys(input.getPassword());
                        submitButton.submit();
                } catch (TimeoutException e) {
                        throw new WebScrapperException(WebScrapperMessage.UNAUTHORIZED, e);
                }

                try {
                        final By carga_extranjera_radio_path = By.xpath("//span[text()='CARGA EXTRANJERA']");
                        wait.until(ExpectedConditions.visibilityOfElementLocated(carga_extranjera_radio_path));
                        final WebElement carga_extranjera_radio = driver.findElement(carga_extranjera_radio_path);
                        carga_extranjera_radio.click();

                        final WebElement booking = driver.findElement(By.id("booking_1"));
                        booking.sendKeys(input.getPayload().getBooking());

                        final WebElement booking_btn = driver
                                        .findElement(By.xpath("//button[@data-id-input='booking_1']"));
                        booking_btn.click();

                        Thread.sleep(100000);
                        // CARGA EXTRANJERA

                } catch (TimeoutException e) {
                        throw new WebScrapperException(WebScrapperMessage.ERROR, e);
                }

        }
}
