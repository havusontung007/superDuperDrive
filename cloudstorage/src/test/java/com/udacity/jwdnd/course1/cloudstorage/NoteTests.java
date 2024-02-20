package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.entity.Note;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

/** Tests for Note Creation, Viewing, Editing, and Deletion. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NoteTests {
    protected WebDriver driver;
    @LocalServerPort
    protected int port;

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
    private void createNote(String noteTitle, String noteDescription, HomePage homePage) {
        homePage.navToNotesTab();
        homePage.addNewNote();
        homePage.setNoteTitle(noteTitle);
        homePage.setNoteDescription(noteDescription);
        homePage.saveNoteChanges();
        ResultPage resultPage = new ResultPage(driver);
        resultPage.clickOk();
        homePage.navToNotesTab();
    }

    private void deleteNote(HomePage homePage) {
        homePage.deleteNote();
        ResultPage resultPage = new ResultPage(driver);
        resultPage.clickOk();
    }

    @Test
    public void testDelete() {
        String noteTitle = "Title";
        String noteDescription = "Description";
        HomePage homePage = signUpAndLogin();
        createNote(noteTitle, noteDescription, homePage);
        homePage.navToNotesTab();
        homePage = new HomePage(driver);
        deleteNote(homePage);
        Assertions.assertTrue(homePage.noNotes(driver));
    }

    @Test
    public void testCreateAndDisplay() {
        String noteTitle = "New Title";
        String noteDescription = "New Description";
        HomePage homePage = signUpAndLogin();
        createNote(noteTitle, noteDescription, homePage);
        homePage.navToNotesTab();
        homePage = new HomePage(driver);
        Note note = homePage.getFirstNote();
        Assertions.assertEquals(noteTitle, note.getNotetitle());
        Assertions.assertEquals(noteDescription, note.getNotedescription());
        deleteNote(homePage);
        homePage.logout();
    }

    @Test
    public void testModify() {
        String noteTitle = "New Title";
        String noteDescription = "New Description";
        HomePage homePage = signUpAndLogin();
        createNote(noteTitle, noteDescription, homePage);
        homePage.navToNotesTab();
        homePage = new HomePage(driver);
        homePage.editNote();
        String modifiedNoteTitle = "Modified Title";
        homePage.modifyNoteTitle(modifiedNoteTitle);
        String modifiedNoteDescription = "Modified Description";
        homePage.modifyNoteDescription(modifiedNoteDescription);
        homePage.saveNoteChanges();
        ResultPage resultPage = new ResultPage(driver);
        resultPage.clickOk();
        homePage.navToNotesTab();
        Note note = homePage.getFirstNote();
        Assertions.assertEquals(modifiedNoteTitle, note.getNotetitle());
        Assertions.assertEquals(modifiedNoteDescription, note.getNotedescription());
    }
}
