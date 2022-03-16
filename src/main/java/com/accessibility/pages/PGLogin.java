package com.accessibility.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PGLogin {
    private WebDriver driver;

    PGLogin(WebDriver driver){
        this.driver = driver;
    }

    public void doLogin(String username, String password){
        String xpath_username = "//input[@id='username']";
        String xpath_password = "//input[@id='pass']";
        String xpath_login = "//input[@id='submit']";

        WebElement elementUsername = this.driver.findElement(By.xpath(xpath_username));
        elementUsername.clear();
        elementUsername.sendKeys(username);


        WebElement elementPass = this.driver.findElement(By.xpath(xpath_password));
        elementPass.clear();
        elementPass.sendKeys(password);


        WebElement elementLogin = this.driver.findElement(By.xpath(xpath_login));
        elementLogin.click();
    }
}
