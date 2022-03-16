package com.accessibility.utils;

import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.results.Rule;
import org.testng.Assert;

import java.util.HashSet;

public class Utility {

    public static void assertViolations(Results results, HashSet<String> expecteds) {
        HashSet<String> actuals = new HashSet<>();
        for (Rule r : results.getViolations()) {
            actuals.add(r.getId());
        }
//        HashSet<String> expecteds = new HashSet<>();
//        expecteds.addAll(Arrays.asList(expectedsList));

        expecteds.removeAll(actuals);
        actuals.removeAll(expecteds);

        String failureMessage = "";
        if (!expecteds.isEmpty()) {
            failureMessage += "Wanted but didn't find:\n";
            for (String exp : expecteds) {
                failureMessage += exp + "\n";
            }
        }

        if (!actuals.isEmpty()) {
            failureMessage += "Found unexpected:\n";
            for (String act : actuals) {
                failureMessage += act + "\n";
            }
        }

        if (!expecteds.isEmpty() || !actuals.isEmpty()) {
            Assert.fail(failureMessage);
        }
    }

    public static String formatString(String sString, String color){
        return "<b style='color:"+color+"'>"+sString+"</b>";
    }

    public static String formatTextArea(String sString){
        return "<textarea rows='20' cols='40' style='border:none;'>"+sString+"</textarea>";
    }
}
