package com.copel.productpackages.arena.selenium.service.unit;

import java.io.IOException;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * 画面項目を扱うクラス.
 *
 * @author 鈴木一矢
 *
 */
public class WebBrowser {
    /**
     * WebDriverクライアント.
     */
    private WebDriver driver;
    /**
     * WebDriverWaitインスタンス.
     */
    private WebDriverWait wait;
    /**
     * セッション.
     */
    private Session session;
    /**
     * Lambda環境での実行であるかどうか.
     */
    private boolean isLambda = false;
    /**
     * GitHUB Actions環境での実行であるかどうか.
     */
    private boolean isGithubActions = false;

    /**
     * デフォルトコンストラクタ(ヘッドレスモードなし).
     */
    public WebBrowser() {
        this(false);
    }
    /**
     * コンストラクタ.
     *
     * @param sessionFilePath セッションファイルの場所
     * @param isHeadlessMode ヘッドレスモードフラグ
     */
    public WebBrowser(final String sessionFilePath, final boolean isHeadlessMode) {
        this(isHeadlessMode);
        this.session = new Session(this.driver.manage().getCookies(), sessionFilePath);
    }
    /**
     * コンストラクタ.
     *
     * @param isHeadlessMode ヘッドレスモードフラグ
     */
    public WebBrowser(final boolean isHeadlessMode) {
        // Lambda環境での実行かどうかを判定
        this.isLambda = System.getenv("AWS_LAMBDA_FUNCTION_NAME") != null;
        // GitHub Actions環境での実行かどうかを判定
        this.isGithubActions = Boolean.TRUE.toString().equals(System.getenv("GITHUB_ACTIONS"));

        // オプションを定義
        ChromeOptions options = new ChromeOptions();

        // ブラウザを人間が操作しているように見せる
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/122.0.0.0 Safari/537.36");

        // ヘッドレスモードの指定
        if (isHeadlessMode && !this.isLambda && !this.isGithubActions) {
            options.addArguments("--headless=new");
        }

        // Lambdaで動かすときの設定（必ずヘッドレスモードになる）
        if (this.isLambda) {
            System.setProperty("webdriver.chrome.driver", "/opt/bin/chromedriver");
            options.setBinary("/opt/bin/chrome");
            options.addArguments("--headless");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1280x1696");
            options.addArguments("--single-process");
            options.addArguments("--disable-extensions");
            options.addArguments("--disable-dev-tools");
            options.addArguments("--disable-setuid-sandbox");
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--remote-debugging-port=9222");
        }
        // isGithubActionsで動かすときの設定（必ずヘッドレスモードになる）
        else if (this.isGithubActions) {
            System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
            options.setBinary("/usr/bin/google-chrome");
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1280,1696");
        }
        // ローカルではChromeDriver を自動でセットアップ
        else {
            WebDriverManager.chromedriver().setup();
        }

        // WebDriverを作成
        this.driver = new ChromeDriver(options);

        // navigator.webdriver を false にする
        ((JavascriptExecutor) this.driver).executeScript(
            "Object.defineProperty(navigator, 'webdriver', {get: () => undefined})"
        );

        // WebDriverWaitを作成
        this.wait = new WebDriverWait(this.driver, Duration.ofSeconds(15));
    }

    /**
     * 現時点での状態のセッションをファイルに保存する.
     *
     * @throws IOException
     */
    public void saveSession() throws IOException {
        this.session.save();
    }

    /**
     * ファイルからセッションを読み込みこのブラウザにセットする.
     *
     * @throws IOException
     */
    public void loadSession() throws IOException {
        this.session.loead();
        for (final Cookie cookie : this.session.getCookies()) {
            this.driver.manage().addCookie(cookie);
        }
    }

    /**
     * 引数のURL先にアクセスする.
     *
     * @param url アクセス先URL
     * @throws InterruptedException 
     */
    public void access(final String url) throws InterruptedException {
        this.driver.get(url);
        this.wait(3);
    }

    /**
     * 画面項目に文字を入力する(Xpathで探索).
     *
     * @param xpath XPath
     * @param text 入力する文字列
     * @throws InterruptedException
     */
    public void sendKeysByXpath(final String xpath, final String text) throws InterruptedException {
        this.wait(1);
        WebElement element = this.wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        element = this.driver.findElement(By.xpath(xpath));
        for (char c : text.toCharArray()) {
            element.sendKeys(Character.toString(c));
        }
    }

    /**
     * 画面項目に文字を入力する(name属性で探索).
     *
     * @param name name属性値
     * @param text 入力する文字列
     * @throws InterruptedException
     */
    public void sendKeysByName(final String name, final String text) throws InterruptedException {
        this.wait(1);
        WebElement element = this.wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(name)));
        element = this.driver.findElement(By.name(name));
        for (char c : text.toCharArray()) {
            element.sendKeys(Character.toString(c));
        }
    }

    /**
     * 画面項目をクリックする(Xpathで探索).
     *
     * @param xpath XPath
     */
    public void clickByXpath(final String xpath) {
        WebElement element = this.driver.findElement(By.xpath(xpath));
        element.click();
    }

    /**
     * 画面項目をクリックする(name属性で探索).
     *
     * @param name 名前
     */
    public void clickByName(final String name) {
        WebElement element = this.driver.findElement(By.name(name));
        element.click();
    }

    /**
     * 指定したXPathの要素のalt属性を取得する.
     *
     * @param xpath XPath
     * @return alt属性の値
     */
    public String getAltAttributeByXpath(final String xpath) {
        WebElement element = this.wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        return element.getAttribute("alt");
    }

    /**
     * 指定したXPathのプルダウン（<select>）要素から、指定したオプションを選択する.
     *
     * @param xpath XPath
     * @param visibleText 選択するオプションの表示テキスト
     */
    public void selectOptionByXpath(final String xpath, final String visibleText) {
        WebElement dropdownElement = this.wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        Select dropdown = new Select(dropdownElement);
        dropdown.selectByVisibleText(visibleText);
    }

    /**
     * 引数の秒数だけ処理を待機する.
     *
     * @param second 待機秒数
     * @throws InterruptedException 
     */
    public void wait(final int second) throws InterruptedException {
        Thread.sleep(second * 1000);
    }

    /**
     * 引数のURL先に遷移が完了するまで待機する.
     *
     * @param url 遷移先URL
     */
    public void waitByUrl(final String url) {
        this.wait.until(ExpectedConditions.urlContains(url));
    }

    /**
     * ドライバを終了する.
     */
    public void quit() {
        this.driver.quit();
    }
}
