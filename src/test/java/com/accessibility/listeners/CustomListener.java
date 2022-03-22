package com.accessibility.listeners;

import com.accessibility.base.TestBase;
import com.aventstack.extentreports.Status;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.Arrays;

public class CustomListener extends TestBase implements ITestListener {

    public static String scenarioName;
    @Override
    public synchronized void onTestStart(ITestResult result) {
        scenarioName = Arrays.stream(result.getParameters())
                .findFirst()
                .orElse("null")
                .toString()
                .replaceAll("\"", "");
        test.set(report.createTest(scenarioName));
        test.get()
                .log(Status.INFO, scenarioName + " Has Started");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        test.get()
                .log(Status.PASS, scenarioName+" Has completed successfully");
        ITestListener.super.onTestSuccess(result);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        test.get()
                .log(Status.FAIL, scenarioName+" Has failed");
        ITestListener.super.onTestFailure(result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        test.get()
                .log(Status.SKIP, scenarioName+" Has skipped");
        ITestListener.super.onTestSkipped(result);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        ITestListener.super.onTestFailedButWithinSuccessPercentage(result);
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        ITestListener.super.onTestFailedWithTimeout(result);
    }

    @Override
    public void onStart(ITestContext context) {
        ITestListener.super.onStart(context);
    }

    @Override
    public void onFinish(ITestContext context) {
        ITestListener.super.onFinish(context);
    }
}
