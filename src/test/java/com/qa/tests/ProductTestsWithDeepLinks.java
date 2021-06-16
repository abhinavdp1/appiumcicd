package com.qa.tests;

import com.qa.BaseTest;
import com.qa.pages.LoginPage;
import com.qa.pages.ProductDetailsPage;
import com.qa.pages.ProductsPage;
import com.qa.pages.SettingsPage;
import com.qa.utils.TestUtils;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.io.InputStream;
import java.lang.reflect.Method;

public class ProductTestsWithDeepLinks extends BaseTest {
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
    }
    @AfterClass
    public void afterClass(){

    }
    @BeforeMethod
    public void beforeMethod(Method m){
        /*loginPage = new LoginPage();
        productsPage = loginPage.login(loginUsers.getJSONObject("validUser").getString("username"),loginUsers.getJSONObject("validUser").getString("password"));
        utils.log().info("\n" + "****** starting test: " + m.getName() + "******" + "\n");*/
    }
    @AfterMethod
    public void afterMethod(){
/*        settingsPage = productsPage.pressSettingbtn();
        loginPage = settingsPage.pressLogoutBtn();*/
    }

    @Test
    public void validateProductOnProductsPage() {
            ProductsPage productsPage = new ProductsPage();
            SoftAssert sa = new SoftAssert();
            OpenAppWithDeepLinks("swaglabs://swag-overview/0,1");
            sa.assertEquals(productsPage.getSLBTitle(), getString().get("products_page_slb_title"));
            sa.assertEquals(productsPage.getSLBPrice(), getString().get("products_page_slb_price"));
            sa.assertAll();
    }

    @Test
    public void validateProductOnProductDetailsPage() {
        ProductsPage productsPage = new ProductsPage();
        ProductDetailsPage productDetailsPage = new ProductDetailsPage();
        SoftAssert sa = new SoftAssert();
        OpenAppWithDeepLinks("swaglabs://swag-overview/0,1");
        productDetailsPage = productsPage.pressSLBTitle();
        sa.assertEquals(productDetailsPage.getSLBTitle(), getString().get("product_details_page_slb_title"));
        sa.assertEquals(productDetailsPage.getSLBTxt(), getString().get("product_details_page_slb_txt"));
        sa.assertAll();
    }
}
