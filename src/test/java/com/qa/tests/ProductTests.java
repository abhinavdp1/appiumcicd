package com.qa.tests;

import com.qa.BaseTest;
import com.qa.pages.LoginPage;
import com.qa.pages.ProductDetailsPage;
import com.qa.pages.ProductsPage;
import com.qa.pages.SettingsPage;
import com.qa.utils.TestUtils;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.io.InputStream;
import java.lang.reflect.Method;

public class ProductTests extends BaseTest {
    LoginPage loginPage;
    ProductsPage productsPage;
    SettingsPage settingsPage;
    ProductDetailsPage productDetailsPage;
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
        loginPage = new LoginPage();
        productsPage = loginPage.login(loginUsers.getJSONObject("validUser").getString("username"),loginUsers.getJSONObject("validUser").getString("password"));
        utils.log().info("\n" + "****** starting test: " + m.getName() + "******" + "\n");
    }
    @AfterMethod
    public void afterMethod(){
        settingsPage = productsPage.pressSettingbtn();
        loginPage = settingsPage.pressLogoutBtn();
    }

    @Test
    public void validateProductOnProductsPage() {
            productsPage = new ProductsPage();
            SoftAssert sa = new SoftAssert();
            String SLBTitle =  productsPage.getSLBTitle();
            sa.assertEquals(SLBTitle, getString().get("products_page_slb_title"));

            String SLBPrice = productsPage.getSLBPrice();
            sa.assertEquals(SLBPrice, getString().get("products_page_slb_price"));
            sa.assertAll();
    }

    @Test
    public void validateProductOnProductDetailsPage() {
        SoftAssert sa = new SoftAssert();
        productsPage = new ProductsPage();
        productDetailsPage = new ProductDetailsPage();
        productDetailsPage = productsPage.pressSLBTitle();
        String SLBTitle =  productDetailsPage.getSLBTitle();
        sa.assertEquals(SLBTitle, getString().get("product_details_page_slb_title"));

        String SLBPrice = productDetailsPage.getSLBTxt();
        sa.assertEquals(SLBPrice, getString().get("product_details_page_slb_txt"));

        productsPage = productDetailsPage.pressBackToProductsBtn();
        sa.assertAll();
    }
}
