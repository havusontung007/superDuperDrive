package com.udacity.jwdnd.course1.cloudstorage;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.concurrent.TimeUnit;

/** Tests for User Signup, Login, and Unauthorized Access Restrictions. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserTests{

    @LocalServerPort private int port;

    private WebDriver driver;

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

    @Test
    public void testPageAccess() {
        driver.get("http://localhost:" + this.port + "/login");
        Assertions.assertEquals("Login", driver.getTitle());

        driver.get("http://localhost:" + this.port + "/signup");
        Assertions.assertEquals("Sign Up", driver.getTitle());

        driver.get("http://localhost:" + this.port + "/home");
        Assertions.assertEquals("Login", driver.getTitle());
    }

    @Test
    public void testUserAction() {
        driver.get("http://localhost:" + this.port + "/signup");
        Assertions.assertEquals("Sign Up", driver.getTitle());

        SignupPage signupPage = new SignupPage(driver);
        signupPage.setFirstName("John");
        signupPage.setLastName("Lennon");
        signupPage.setUserName("lennon");
        signupPage.setPassword("julia");
        signupPage.signUp();

        driver.get("http://localhost:" + this.port + "/login");
        Assertions.assertEquals("Login", driver.getTitle());

        LoginPage loginPage = new LoginPage(driver);
        loginPage.setUserName("lennon");
        loginPage.setPassword("julia");
        loginPage.login();

        HomePage homePage = new HomePage(driver);
        homePage.logout();

        driver.get("http://localhost:" + this.port + "/home");
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        Assertions.assertEquals("Login", driver.getTitle());
    }
}