package com.qa.pages;

import com.qa.MenuPage;
import com.qa.utils.TestUtils;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;

public class ProductDetailsPage extends MenuPage {
    TestUtils utils =  new TestUtils();
    @AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"test-Description\"]/android.widget.TextView[1]")
    @iOSXCUITFindBy(id = "Sauce Labs Backpack")
    private MobileElement SLBTitle;
    @AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"test-Description\"]/android.widget.TextView[2]")
    @iOSXCUITFindBy(id = "carry.allTheThings() with the sleek, streamlined Sly Pack that melds uncompromising style with unequaled laptop and tablet protection.")
    private MobileElement SLBTxt;
    @AndroidFindBy(accessibility = "test-BACK TO PRODUCTS")
    @iOSXCUITFindBy(id = "test-BACK TO PRODUCTS")
    private MobileElement backToProductsBtn;

    public String getSLBTitle(){
        String title = getText(SLBTitle,"title is -");
        return title;
    }

    public String getSLBTxt(){
        String txt = getText(SLBTxt,"txt is -");
        return txt;
    }

    public ProductsPage pressBackToProductsBtn(){
        click(backToProductsBtn,"navigate back to products page");
        return new ProductsPage();
    }
}
