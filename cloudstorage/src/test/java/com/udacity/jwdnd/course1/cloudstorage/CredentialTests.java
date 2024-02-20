package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.entity.Credentials;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

/** Tests for Credential Creation, Viewing, Editing, and Deletion. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CredentialTests {

    protected WebDriver driver;
    @LocalServerPort protected int port;

    @BeforeAll
    static void beforeAll() {
        WebDriverManager.edgedriver().setup();
    }

    @BeforeEach
    public void beforeEach() {
        this.driver = new EdgeDriver();
    }

    @AfterEach
    public void afterEach() {
        if (this.driver != null) {
            driver.quit();
        }
    }

    public static final String FIRST_URL = "https://www.google.com/";
    public static final String SECOND_URL = "http://www.facebook.com/";
    public static final String THIRD_URL = "http://www.twitter.com/";
    public static final String FIRST_USERNAME = "user1";
    public static final String FIRST_PASSWORD = "user1";
    public static final String SECOND_USERNAME = "user2";
    public static final String SECOND_PASSWORD = "user2";
    public static final String THIRD_USERNAME = "user3";
    public static final String THIRD_PASSWORD = "user3";

    private void createAndVerifyCredential(
            String url, String username, String password, HomePage homePage) {
        createCredential(url, username, password, homePage);
        homePage.navToCredentialsTab();
        Credentials credential = homePage.getFirstCredential();
        Assertions.assertEquals(url, credential.getUrl());
        Assertions.assertEquals(username, credential.getUsername());
        Assertions.assertNotEquals(password, credential.getPassword());
    }

    protected HomePage signUpAndLogin() {
        driver.get("http://localhost:" + this.port + "/signup");
        SignupPage signupPage = new SignupPage(driver);
        signupPage.setFirstName("tung");
        signupPage.setLastName("hvs");
        signupPage.setUserName("tunghvs");
        signupPage.setPassword("tunghvs");
        signupPage.signUp();
        driver.get("http://localhost:" + this.port + "/login");
        LoginPage loginPage = new LoginPage(driver);
        loginPage.setUserName("tunghvs");
        loginPage.setPassword("tunghvs");
        loginPage.login();

        return new HomePage(driver);
    }

    private void createCredential(String url, String username, String password, HomePage homePage) {
        homePage.navToCredentialsTab();
        homePage.addNewCredential();
        setCredentialFields(url, username, password, homePage);
        homePage.saveCredentialChanges();
        ResultPage resultPage = new ResultPage(driver);
        resultPage.clickOk();
        homePage.navToCredentialsTab();
    }

    private void setCredentialFields(
            String url, String username, String password, HomePage homePage) {
        homePage.setCredentialUrl(url);
        homePage.setCredentialUsername(username);
        homePage.setCredentialPassword(password);
    }

    @Test
    public void testCreateCredential() {
        HomePage homePage = signUpAndLogin();
        createAndVerifyCredential(FIRST_URL, FIRST_USERNAME, FIRST_PASSWORD, homePage);
        homePage.deleteCredential();
        ResultPage resultPage = new ResultPage(driver);
        resultPage.clickOk();
        homePage.logout();
    }

    @Test
    public void testCredentialModification() {
        HomePage homePage = signUpAndLogin();
        createAndVerifyCredential(FIRST_URL, FIRST_USERNAME, FIRST_PASSWORD, homePage);
        Credentials originalCredential = homePage.getFirstCredential();
        String firstEncryptedPassword = originalCredential.getPassword();
        homePage.editCredential();
        String newUrl = SECOND_URL;
        String newCredentialUsername = SECOND_USERNAME;
        String newPassword = SECOND_PASSWORD;
        setCredentialFields(newUrl, newCredentialUsername, newPassword, homePage);
        homePage.saveCredentialChanges();
        ResultPage resultPage = new ResultPage(driver);
        resultPage.clickOk();
        homePage.navToCredentialsTab();
        Credentials modifiedCredential = homePage.getFirstCredential();
        Assertions.assertEquals(newUrl, modifiedCredential.getUrl());
        Assertions.assertEquals(newCredentialUsername, modifiedCredential.getUsername());
        String modifiedCredentialPassword = modifiedCredential.getPassword();
        Assertions.assertNotEquals(newPassword, modifiedCredentialPassword);
        Assertions.assertNotEquals(firstEncryptedPassword, modifiedCredentialPassword);
        homePage.deleteCredential();
        resultPage.clickOk();
        homePage.logout();
    }

    @Test
    public void testDeletion() {
        HomePage homePage = signUpAndLogin();
        createCredential(FIRST_URL, FIRST_USERNAME, FIRST_PASSWORD, homePage);
        createCredential(SECOND_URL, SECOND_USERNAME, SECOND_PASSWORD, homePage);
        createCredential(THIRD_URL, THIRD_USERNAME, THIRD_PASSWORD, homePage);
        homePage.deleteCredential();
        ResultPage resultPage = new ResultPage(driver);
        resultPage.clickOk();
        homePage.navToCredentialsTab();
        homePage.deleteCredential();
        resultPage.clickOk();
        homePage.navToCredentialsTab();
        homePage.deleteCredential();
        resultPage.clickOk();
        homePage.navToCredentialsTab();
        Assertions.assertTrue(homePage.noCredentials(driver));
        homePage.logout();
    }
}
