package com.qa.pages;

import com.qa.BaseTest;
import com.qa.utils.TestUtils;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AndroidBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;

public class LoginPage extends BaseTest {
    TestUtils utils =  new TestUtils();
    @AndroidFindBy(accessibility = "test-Username")
    @iOSXCUITFindBy(id = "test-Username")
    private MobileElement usernameTxtField;
    @AndroidFindBy(accessibility = "test-Password")
    @iOSXCUITFindBy(id = "test-Password")
    private MobileElement passwordTxtField;
    @AndroidFindBy(accessibility = "test-LOGIN")
    @iOSXCUITFindBy(id = "test-LOGIN")
    private MobileElement loginBtn;
    @AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"test-Error message\"]/android.widget.TextView")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeOther[@name=\"test-Error message\"]/child::XCUIElementTypeStaticText")
    private MobileElement errTxt;

     public LoginPage enterUserName(String username) {
         clear(usernameTxtField);
         sendKeys(usernameTxtField, username,"Login with " + username);
         return this;
     }

    public LoginPage enterPassword(String password){
         clear(passwordTxtField);
        sendKeys(passwordTxtField,password,"password is " + password);
        return this;
    }

    public ProductsPage pressLoginBtn(){
        click(loginBtn,"Press login button");
        return new ProductsPage();
    }

    public ProductsPage login(String username, String password){
         enterUserName(username);
         enterPassword(password);
         return pressLoginBtn();
    }

    public String getErrTxt(){
         String err = getText(errTxt,"error text is - ");
         return err;
    }

    }
