package com.qa;

import com.aventstack.extentreports.Status;
import com.qa.reports.ExtentReport;
import com.qa.utils.TestUtils;
import io.appium.java_client.FindsByAndroidUIAutomator;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.screenrecording.CanRecordScreen;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.aspectj.lang.annotation.After;
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.*;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;

import java.io.*;
import java.net.ServerSocket;
import java.net.URL;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.remote.DesiredCapabilities;

public class BaseTest {
    protected static ThreadLocal <AppiumDriver> driver = new ThreadLocal<AppiumDriver>();
    protected static ThreadLocal <Properties> props = new ThreadLocal<Properties>();
    protected static ThreadLocal <HashMap<String, String>> strings = new ThreadLocal<HashMap<String, String>>();
    protected static  ThreadLocal <String> platform = new ThreadLocal<String>();
    protected static ThreadLocal <String> dateTime = new ThreadLocal<String>();
    protected static ThreadLocal <String> deviceName = new ThreadLocal<String>();
    private static AppiumDriverLocalService server;
    TestUtils utils =  new TestUtils();


    public AppiumDriver  getDriver(){
        return driver.get();
    }

    public void setDriver(AppiumDriver driver2){
        driver.set(driver2);
    }

    public Properties getProps(){
        return props.get();
    }

    public void setProps(Properties props2){
        props.set(props2);
    }

    public HashMap<String, String> getString(){
        return strings.get();
    }

    public void setStrings(HashMap<String, String> strings2){
        strings.set(strings2);
    }

    public String getPlatform(){
        return platform.get();
    }

    public void setPlatform(String platform2){
        platform.set(platform2);
    }


    public String getDateTime(){
        return dateTime.get();
    }

    public void setDeviceName(String deviceName2){
        deviceName.set(deviceName2);
    }

    public String getDeviceName(){
        return deviceName.get();
    }

    public void setDateTime(String dateTime2){
        dateTime.set(dateTime2);
    }

    public BaseTest() {
        PageFactory.initElements(new AppiumFieldDecorator(getDriver()), this);
    }

    @BeforeMethod
    public void beforeMethod(){
        ((CanRecordScreen)getDriver()).startRecordingScreen();
    }

    @AfterMethod
    public synchronized void afterMethod(ITestResult result) throws IOException {
        String media = ((CanRecordScreen)getDriver()).stopRecordingScreen();

            Map<String, String> params = result.getTestContext().getCurrentXmlTest().getAllParameters();

            String dir = "videos" + File.separator + params.get("platformName") + "_" + params.get("platformVersion") + "_"+ params.get("deviceName") + File.separator +
                    getDateTime() + File.separator + result.getTestClass().getRealClass().getSimpleName();

            File videoDir = new File(dir);

            synchronized (videoDir){
                if(!videoDir.exists()){
                    videoDir.mkdirs();
                }
            }
            FileOutputStream stream = new FileOutputStream(videoDir + File.separator + result.getName() + ".mp4");
            stream.write(Base64.decodeBase64(media));
    }

    @BeforeSuite
    public void beforeSuite() throws Exception, Exception {
        ThreadContext.put("ROUTINGKEY", "ServerLogs");
        server = getAppiumService();
        if(!checkIfAppiumServerIsRunnning(4723)) {
            server.start();
            server.clearOutPutStreams();
            utils.log().info("Appium server started");
        } else {
            utils.log().info("Appium server already running");
        }
    }

    public boolean checkIfAppiumServerIsRunnning(int port) throws Exception {
        boolean isAppiumServerRunning = false;
        ServerSocket socket;
        try {
            socket = new ServerSocket(port);
            socket.close();
        } catch (IOException e) {
            System.out.println("1");
            isAppiumServerRunning = true;
        } finally {
            socket = null;
        }
        return isAppiumServerRunning;
    }

    @AfterSuite
    public void afterSuite() {
        server.stop();
        utils.log().info("Appium server stopped");
    }

    public AppiumDriverLocalService getAppiumServerDefault() {
        return AppiumDriverLocalService.buildDefaultService();
    }

    public AppiumDriverLocalService getAppiumService() {
        HashMap<String, String> environment = new HashMap<String, String>();
        environment.put("PATH", "/Library/Java/JavaVirtualMachines/jdk-16.jdk/Contents/Home/bin:/Users/abhinav/Library/Android/sdk/tools:/Users/abhinav/Library/Android/sdk/platform-tools:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin:/usr/local/munki:/Library/Apple/usr/bin" + System.getenv("PATH"));
        environment.put("ANDROID_HOME", "/Users/abhinav/Library/Android/sdk");
        return AppiumDriverLocalService.buildService(new AppiumServiceBuilder()
                .usingDriverExecutable(new File("/usr/local/bin/node"))
                .withAppiumJS(new File("/usr/local/lib/node_modules/appium/build/lib/main.js"))
                .usingPort(4723)
                .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
                .withEnvironment(environment)
                .withLogFile(new File("ServerLogs/server.log")));
    }

    @Parameters({"emulator", "platformName", "udid","deviceName","systemPort", "chromeDriver","wdaLocalPort",
    "webkitDebugProxyPort"})
    @BeforeTest
    public void beforeTest(@Optional("iOSOnly") String emulator , String platformName, String udid, String deviceName,
                           @Optional("androidOnly") String systemPort, @Optional("androidOnly")String chromeDriverPort,
                           @Optional("iOSOnly")String wdaLocalPort, @Optional("iOSOnly")String webkitDebugProxyPort) throws IOException {

        setDateTime(utils.dateTime());
        setPlatform(platformName);
        setDeviceName(deviceName);
        URL url;
        InputStream inputStream = null;
        InputStream stringsis = null;
        Properties props = new Properties();
        AppiumDriver driver;

        String strFile = "logs" + File.separator + platformName + "_" + deviceName;
        File logFile = new File(strFile);
        if (!logFile.exists()) {
            logFile.mkdirs();
        }
        //route logs to separate file for each thread
        ThreadContext.put("ROUTINGKEY", strFile);
        utils.log().info("log path: " + strFile);

        try {
            props = new Properties();
            String propFileName = "config.properties";
            String xmlFileName = "strings/strings.xml";

            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
            props.load(inputStream);
            setProps(props);

            stringsis = getClass().getClassLoader().getResourceAsStream(xmlFileName);
            setStrings(utils.parseStringXML(stringsis));

            DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
            desiredCapabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, platformName);
            desiredCapabilities.setCapability(MobileCapabilityType.DEVICE_NAME, deviceName);
            desiredCapabilities.setCapability(MobileCapabilityType.UDID, udid);
            url = new URL(props.getProperty("appiumURL") + "4723/wd/hub");

            switch (platformName) {
                case "Android":
                    desiredCapabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, props.getProperty("androidAutomationName"));
                    desiredCapabilities.setCapability("appPackage", props.getProperty("androidAppPackage"));
                    desiredCapabilities.setCapability("appActivity", props.getProperty("androidAppActivity"));
                    desiredCapabilities.setCapability("systemPort", props.getProperty("systemPort"));
                    desiredCapabilities.setCapability("chromeDriverPort", props.getProperty("chromeDriverPort"));
                    if(emulator.equalsIgnoreCase("true")){
                        desiredCapabilities.setCapability("avd","pixel2");
                    }
                    String androidAppUrl = getClass().getResource(props.getProperty("androidAppLocation")).getFile();
                    //desiredCapabilities.setCapability("app", androidAppUrl);
                    utils.log().info(androidAppUrl);
                    driver = new AndroidDriver(url, desiredCapabilities);
                    break;

                case "iOS":
                    desiredCapabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, props.getProperty("iOSAutomationName"));
                    String iOSappUrl = getClass().getResource(props.getProperty("iOSAppLocation")).getFile();
                    //desiredCapabilities.setCapability("app", iOSappUrl);
                    desiredCapabilities.setCapability("bundleId", props.getProperty("iOSBundleId"));
                    desiredCapabilities.setCapability("wdaLocalPort", props.getProperty("wdaLocalPort"));
                    desiredCapabilities.setCapability("webkitDebugProxyPort", props.getProperty("webkitDebugProxyPort"));
                    driver = new IOSDriver(url, desiredCapabilities);
                    break;
                default:
                    throw new Exception("Invalid platform! - " + platformName);
            }
            setDriver(driver);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (stringsis != null) {
                stringsis.close();
            }
        }
    }

    public void waitForVisibility(MobileElement e) {
        WebDriverWait wait = new WebDriverWait(getDriver(), TestUtils.WAIT);
        wait.until(ExpectedConditions.visibilityOf(e));
    }

    public void click(MobileElement e) {
        waitForVisibility(e);
        e.click();
    }

    public void click(MobileElement e, String msg) {
        waitForVisibility(e);
        utils.log().info(msg);
        ExtentReport.getTest().log(Status.INFO, msg);
        e.click();
    }

    public void sendKeys(MobileElement e, String txt) {
        waitForVisibility(e);
        e.sendKeys(txt);
    }

    public void sendKeys(MobileElement e, String txt, String msg) {
        waitForVisibility(e);
        utils.log().info(msg);
        ExtentReport.getTest().log(Status.INFO, msg);
        e.sendKeys(txt);
    }

    public String getAttribute(MobileElement e, String attribute) {
        waitForVisibility(e);
        return e.getAttribute(attribute);
    }

    public String getText(MobileElement e, String msg){
        String txt = null;
        switch (getPlatform()){
            case "Android":
                txt = getAttribute(e,"text");
                break;
            case "iOS":
                txt = getAttribute(e,"label");
                break;
        }
        utils.log().info(msg + txt);
        ExtentReport.getTest().log(Status.INFO, msg + txt);
        return txt;
    }

    public void OpenAppWithDeepLinks(String url){

        switch(Objects.requireNonNull(getDriver().getPlatformName())){
            case "android":
                HashMap<String, String> deepUrl = new HashMap<>();
                deepUrl.put("url", url);
                deepUrl.put("package", "com.swaglabsmobileapp");
                getDriver().executeScript("mobile: deepLink", deepUrl);
                break;
            case "ios":
                By urlBtn = MobileBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' && name CONTAINS 'URL'");
                By urlFld = MobileBy.iOSNsPredicateString("type == 'XCUIElementTypeTextField' && name CONTAINS 'URL'");
                By openBtn = MobileBy.iOSNsPredicateString("type == 'XCUIElementTypeButton' && name CONTAINS 'Open'");
                getDriver().activateApp("com.apple.mobilesafari");
                WebDriverWait wait = new WebDriverWait(getDriver(), 10);
                wait.until(ExpectedConditions.visibilityOfElementLocated(urlBtn)).click();
                wait.until(ExpectedConditions.visibilityOfElementLocated(urlFld)).sendKeys("" + url + "\uE007");
                wait.until(ExpectedConditions.visibilityOfElementLocated(openBtn)).click();
                break;
        }
    }

    public void closeApp(){
        getDriver().closeApp();
    }

    public void launchApp(){
        getDriver().launchApp();
    }

    public void clear(MobileElement e){
        waitForVisibility(e);
        e.clear();
    }

 /*   public MobileElement scrollToElement() {
        return (MobileElement) ((FindsByAndroidUIAutomator) getDriver()).findElementByAndroidUIAutomator(
                "new UiScrollable(new UiSelector()" + ".scrollable(true)).scrollIntoView("
                        + "new UiSelector().description(\"test-Price\"));");
    }

    public void iOSScrollToElement() {
        RemoteWebElement element = (RemoteWebElement)getDriver().findElement(By.name("test-ADD TO CART"));
        String elementID = element.getId();
        HashMap<String, String> scrollObject = new HashMap<String, String>();
        scrollObject.put("element", elementID);
//	  scrollObject.put("direction", "down");
//	  scrollObject.put("predicateString", "label == 'ADD TO CART'");
//	  scrollObject.put("name", "test-ADD TO CART");
        scrollObject.put("toVisible", "sdfnjksdnfkld");
        getDriver().executeScript("mobile:scroll", scrollObject);
    }*/

    @AfterTest
    public void afterTest() {
        getDriver().quit();
    }
}
