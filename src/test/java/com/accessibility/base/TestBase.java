package com.accessibility.base;


import com.accessibility.engine.ConditionState;
import com.accessibility.engine.GeneralState;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class TestBase extends AbstractTestNGCucumberTests {

    public static final long START_TIME = System.currentTimeMillis();
    public static final String REPORT_DIR = "./reports/Run_" + START_TIME;
    public static final String SCREENSHOT_DIR = REPORT_DIR+"/screenshots";

    public static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    public static ExtentReports report;
    public static ExtentSparkReporter reporter;

    public static WebDriver driver;
    public static JavascriptExecutor jse;


    static {
        File reportDir = new File(REPORT_DIR);
        if (!reportDir.exists()) {
            boolean mkdir = reportDir.mkdir();
            if (!mkdir) {
                System.out.println("Directory creation failed.");
            }
        }

        File screenshotdir = new File(SCREENSHOT_DIR);
        if (!screenshotdir.exists()) {
            boolean mkdir = screenshotdir.mkdir();
            if (!mkdir) {
                System.out.println("Directory creation failed.");
            }
        }
    }

    @BeforeSuite
    public void startReport() {
        reporter = new ExtentSparkReporter(REPORT_DIR + "/Report.html");
        report = new ExtentReports();
        report.attachReporter(reporter);

        //Driver init
        WebDriverManager.chromedriver()
                .setup();
        ChromeDriverService service = ChromeDriverService.createDefaultService();
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
//                "--headless",
                "--disable-gpu",
                "--window-size=1920,1200",
                "--ignore-certificate-errors",
                "--disable-blink-features=AutomationControlled");
        Map<String, Object> pref = new HashMap<>();
        pref.put("safebrowsing.enabled", true);
        pref.put("plugins.plugins_disabled", new ArrayList<String>(){
            {
                add("Chrome PDF Viewer");
            }
        });
        options.setExperimentalOption("prefs", pref);
        driver = new ChromeDriver(service, options);
        driver.manage()
                .window()
                .maximize();

        //JavascriptExecutor init
        jse = (JavascriptExecutor) driver;
    }

    @AfterMethod
    public void getResult(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            test.get()
                    .log(Status.FAIL, "Test Case Failed is " + result.getName());
            test.get()
                    .log(Status.FAIL, "Test Case Failed is " + result.getThrowable());
        } else if (result.getStatus() == ITestResult.SKIP) {
            test.get()
                    .log(Status.SKIP, "Test Case Skipped is " + result.getName());
        }
    }

    @AfterTest
    public void endReport() {
        report.flush();
    }

    @AfterSuite
    public void afterSuite() {
        if (driver != null) {
            driver.close();
            driver.quit();
        }
    }

    protected void acceptCookies() {
        String cookiesXpath = "//div[@id='cookie_bar']//*[contains(text(),'ACCEPT COOKIES')]";
        this.waitFor(new GeneralState(x -> {
            WebElement element = driver.findElement(By.xpath(cookiesXpath));
            element.click();
            return driver.findElements(By.xpath(cookiesXpath)).size() == 0;
        }), 100, 3000);
    }

    public void waitFor(ConditionState state, long pollingEvery, long timeout){
        try{
            Wait wait = new FluentWait(driver)
                    .withTimeout(Duration.ofMillis(timeout))
                    .pollingEvery(Duration.ofMillis(pollingEvery))
                    .ignoring(NotFoundException.class)
                    .ignoring(TimeoutException.class)
                    .withMessage("Timeout on Browser with state : "+ state);

            wait.until(state.condition());
        }catch (Exception e){
            //ignore
        }
    }
}
