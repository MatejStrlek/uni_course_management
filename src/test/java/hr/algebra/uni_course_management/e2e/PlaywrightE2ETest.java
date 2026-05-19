package hr.algebra.uni_course_management.e2e;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PlaywrightE2ETest {

    @LocalServerPort
    private int port;

    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeAll
    static void launchBrowser() throws IOException {
        Files.createDirectories(Paths.get("target/playwright-screenshots"));
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @AfterAll
    static void closeBrowser() {
        playwright.close();
    }

    @BeforeEach
    void createContext() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    void takeScreenshotAndClose(TestInfo testInfo) {
        String name = testInfo.getDisplayName().replaceAll("[^a-zA-Z0-9_-]", "_");
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get("target/playwright-screenshots/" + name + ".png")));
        context.close();
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void loginPageHasCorrectTitle() {
        page.navigate(url("/login"));
        assertThat(page).hasTitle("Login - University Course Management");
    }

    @Test
    void loginPageHasHeading() {
        page.navigate(url("/login"));
        assertThat(page.locator("h3")).containsText("University Login");
    }

    @Test
    void loginPageHasUsernameField() {
        page.navigate(url("/login"));
        assertThat(page.locator("#username")).isVisible();
    }

    @Test
    void loginPageHasPasswordField() {
        page.navigate(url("/login"));
        assertThat(page.locator("#password")).isVisible();
    }

    @Test
    void loginPageHasSubmitButton() {
        page.navigate(url("/login"));
        assertThat(page.locator("button[type=submit]")).isVisible();
        assertThat(page.locator("button[type=submit]")).containsText("Login");
    }

    @Test
    void rootRedirectsToLogin() {
        page.navigate(url("/"));
        assertThat(page).hasURL(Pattern.compile(".*/login.*"));
    }

    @Test
    void invalidCredentialsShowErrorMessage() {
        page.navigate(url("/login"));
        page.fill("#username", "wronguser");
        page.fill("#password", "wrongpassword");
        page.click("button[type=submit]");
        assertThat(page.locator(".alert-danger")).isVisible();
        assertThat(page.locator(".alert-danger")).containsText("Invalid username or password");
    }

    @Test
    void logoutParamShowsSuccessMessage() {
        page.navigate(url("/login?logout=true"));
        assertThat(page.locator(".alert-success")).isVisible();
        assertThat(page.locator(".alert-success")).containsText("successfully logged out");
    }

    @Test
    void protectedRouteRedirectsToLogin() {
        page.navigate(url("/admin/courses"));
        assertThat(page).hasURL(Pattern.compile(".*/login.*"));
    }

    @Test
    void validLoginRedirectsToDashboard() {
        page.navigate(url("/login"));
        page.fill("#username", "admin");
        page.fill("#password", "password");
        page.click("button[type=submit]");
        assertThat(page).hasURL(Pattern.compile(".*/dashboard.*"));
    }
}
