package com.accessibility.stepDef;

import com.accessibility.base.TestBase;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.deque.html.axecore.axeargs.AxeRunOnlyOptions;
import com.deque.html.axecore.axeargs.AxeRunOptions;
import com.deque.html.axecore.results.Node;
import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.results.Rule;
import com.deque.html.axecore.selenium.AxeBuilder;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.accessibility.utils.Utility.*;

public class PageStepDef extends TestBase {

    private Results analyze;

    private String site;

    /**
     * return default Result
     * @return
     */
    private Results getAnalyze(){
        return new AxeBuilder().analyze(driver);
    }

    /**
     * return Result with particular tags
     * @param tags
     * @return
     */
    private Results getAnalyze(List<String> tags) {
        AxeRunOnlyOptions runOnlyOptions = new AxeRunOnlyOptions();
        runOnlyOptions.setType("tag");
        runOnlyOptions.setValues(tags);

        AxeRunOptions options = new AxeRunOptions();
        options.setRunOnly(runOnlyOptions);
        return new AxeBuilder()
                .withOptions(options)
                .analyze(driver);
    }

    @Given("I navigate to {string}")
    public void iNavigateToUrl(String site) {
        this.site = site;
        driver.get(System.getProperty(site));
        this.acceptCookies();
        test.get().log(Status.INFO, "Chrome started with url : " + System.getProperty(site));
    }

    @When("I run axe tool")
    public void iRunAxeTool(DataTable dataTable) throws IOException {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        List<String> tags = new ArrayList<>();
        for (Map<String, String> col: rows){
            tags.add(col.get("tag"));
        }

        analyze = getAnalyze(tags);

        this.printViolation();

        this.printIncomplete();
//        HashSet<String> expecteds = new HashSet<>(Arrays.asList("aria-required-attr",
//                                                  "region",
//                                                  "color-contrast",
//                                                  "page-has-heading-one",
//                                                  "landmark-one-main"));
//        assertViolations(analyze, expecteds);
    }

    @Then("I should get report")
    public void iShouldGetReport() throws IOException {

        //JSON report file
        File jsonReportFile = new File(REPORT_DIR + "/Run_" + START_TIME + "_" + this.site + "_Report.json");
        try {
            boolean newFile = jsonReportFile.createNewFile();
            if (newFile) {
                System.out.println("file created for JSON report" + jsonReportFile.getCanonicalPath()); //returns the path string
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        FileWriter writer = new FileWriter(jsonReportFile);
        Map<String, Object> reportMap = (Map<String, Object>) jse.executeAsyncScript("var callback = arguments[arguments.length - 1]; axe.run().then(results => callback(results));");

        JSONObject jsonObject = new JSONObject(reportMap);
        String s = jsonObject.toString();

        writer.write(s);
        writer.flush();
        writer.close();
    }

    public void printViolation() throws IOException {
        test.get().log(Status.INFO, formatString("Violation list size : "+analyze.getViolations().size(), "green"));

        for (Rule r : analyze.getViolations()) {
            this.logAndAddScreenshot(r);
        }

    }

    public void printIncomplete() throws IOException {
        test.get().log(Status.INFO, formatString("Incomplete list size : "+analyze.getIncomplete().size(), "green"));

        for (Rule r : analyze.getIncomplete()) {
            this.logAndAddScreenshot(r);
        }

    }

    private void logAndAddScreenshot(Rule r) throws IOException {
        test.get().log(Status.INFO,formatString("RULE : "+r.getId(), "black"));
        test.get().log(Status.INFO, "Description = "+ formatTextArea(r.getDescription()));
        test.get()
                .log(Status.INFO, "Impact = " + formatString(r.getImpact(),
                                                             r.getImpact().equals("critical") ? "red"  :
                                                                     r.getImpact().equals("serious") ? "orange":
                                                                             "green"));
        test.get().log(Status.INFO,"Guideline violated = "+r.getTags());
        test.get().log(Status.INFO,"Help Url = "+r.getHelpUrl());


        for (Node nodes : r.getNodes()){
            String html = nodes.getHtml();
            System.out.println(html);
            String generatedXpath = createXpathFromHTML(html);
            if (!generatedXpath.equals("") && !driver.findElements(By.xpath(generatedXpath)).isEmpty()) {
                WebElement element = driver.findElement(By.xpath(generatedXpath));
                try {

                    File screenshotAs = element.getScreenshotAs(OutputType.FILE);
                    String fileName = site + "_" + element.getText()
                            .replaceAll("[\\s+,.'\"?/]", "") + ".png";
                    String destination = SCREENSHOT_DIR + "/" + fileName;
                    File finalDestination = new File(destination);
                    FileUtils.copyFile(screenshotAs, finalDestination);
                    test.get()
                            .info(fileName, MediaEntityBuilder.createScreenCaptureFromPath("./screenshots/" + fileName)
                                    .build());
                }catch (Exception | Error e){
                    System.out.println("Error : " +e.getMessage());
                }
            }else{
                test.get()
                        .info("Can not able to find element. reference HTML : " + formatTextArea(html));
            }
        }
    }

    @And("I clear cache")
    public void iClearCache() {
        driver.manage().deleteAllCookies();
        jse.executeScript("window.localStorage.clear();");
    }
}
