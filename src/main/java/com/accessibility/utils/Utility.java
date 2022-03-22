package com.accessibility.utils;

import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.results.Rule;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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
        return "<textarea rows='5' cols='5' style='border:none;'>"+sString+"</textarea>";
    }

    public static String createXpathFromHTML(String html){
        String xpath;
        String substring = html.substring(html.indexOf('<')+1, html.indexOf('>'));
        String tag = substring.substring(0, substring.indexOf(" "));

        String newSubstring = substring.substring(tag.length()).trim();

        String[] pairs = newSubstring.split("\"\\s+");

        String collect = Arrays.stream(pairs)
                .filter(e -> !(e.startsWith("ping")||e.startsWith("html")))
                .map(e -> e.replaceAll("amp;", ""))
                .map(e -> e = "@" + e)
                .collect(Collectors.joining("\" and ", "[", "]"));
        xpath = "//" + tag + collect;
        if (xpath.charAt(xpath.length()-2)!='"'){
            xpath = xpath.substring(0, xpath.length()-1)+"\"]";
        }

        System.out.println("generatedxpath = "+xpath);
        return xpath;
    }

    public static void main(String[] args) {
        String html = "<a class=\"pHiOh\" href=\"https://about.google/?utm_source=google-IN&amp;utm_medium=referral&amp;utm_campaign=hp-footer&amp;fg=1\" ping=\"/url?sa=t&amp;rct=j&amp;source=webhp&amp;url=https://about.google/%3Futm_source%3Dgoogle-IN%26utm_medium%3Dreferral%26utm_campaign%3Dhp-footer%26fg%3D1&amp;ved=0ahUKEwjnt8OG8tT2AhU1wosBHd9yBoAQkNQCCBg\">";
        createXpathFromHTML(html);
    }

}
