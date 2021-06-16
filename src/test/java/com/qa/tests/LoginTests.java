package com.qa.tests;

import com.aventstack.extentreports.Status;
import com.qa.BaseTest;
import com.qa.pages.LoginPage;
import com.qa.pages.ProductsPage;
import com.qa.reports.ExtentReport;
import com.qa.utils.TestUtils;
import io.appium.java_client.MobileElement;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

public class LoginTests extends BaseTest {
    LoginPage loginPage;
    ProductsPage productsPage;
    JSONObject loginUsers;
    TestUtils utils = new TestUtils();

    @BeforeClass
    public void beforeClass() throws Exception {
        InputStream datais = null;
        try {
            String dataFileName = "data/loginUsers.json";
            datais = getClass().getClassLoader().getResourceAsStream(dataFileName);
            JSONTokener tokener = new JSONTokener(datais);
            loginUsers = new JSONObject(tokener);
        } catch (Exception e){
            e.printStackTrace();
            throw e;
        } finally {
            if(datais != null){
                datais.close();
            }
        }
        closeApp();
        launchApp();
    }
    @AfterClass
    public void afterClass(){

    }
    @BeforeMethod
    public void beforeMethod(Method m){
        utils.log().info("loginTest before method");
        loginPage = new LoginPage();
        utils.log().info("\n" + "****** starting test: " + m.getName() + "******" + "\n");
    }
    @AfterMethod
    public void afterMethod(){
        utils.log().info("loginTest after method");
    }

    @Test
    public void invalidUserName() {
            loginPage.enterUserName(loginUsers.getJSONObject("invalidUser").getString("username"));
            loginPage.enterPassword(loginUsers.getJSONObject("invalidUser").getString("password"));
            loginPage.pressLoginBtn();
            String actualErrTxt = loginPage.getErrTxt();
            String expectedErrTxt = getString().get("err_invalid_username_or_password");
            utils.log().info("actual error text - " + actualErrTxt + "\n" + "expected error text - " + expectedErrTxt);
            Assert.assertEquals(actualErrTxt,expectedErrTxt);
    }

    @Test
    public void invalidPassword(){
        loginPage.enterUserName(loginUsers.getJSONObject("invalidPassword").getString("username"));
        loginPage.enterPassword(loginUsers.getJSONObject("invalidPassword").getString("password"));
        loginPage.pressLoginBtn();

        String actualErrTxt = loginPage.getErrTxt();
        String expectedErrTxt = getString().get("err_invalid_username_or_password");
        utils.log().info("actual error text - " + actualErrTxt + "\n" + "expected error text - " + expectedErrTxt);
        Assert.assertEquals(actualErrTxt,expectedErrTxt);
    }

    @Test
    public void successfulLogin(){
        loginPage.enterUserName(loginUsers.getJSONObject("validUser").getString("username"));
        loginPage.enterPassword(loginUsers.getJSONObject("validUser").getString("password"));
        productsPage = loginPage.pressLoginBtn();

        String actualProductTitle = productsPage.getTitle();
        String expectedProductTitle = getString().get("product_title");
        utils.log().info("actual product title - " + actualProductTitle + "\n" + "expected product title - " + expectedProductTitle);
        Assert.assertEquals(actualProductTitle,expectedProductTitle);
    }

}