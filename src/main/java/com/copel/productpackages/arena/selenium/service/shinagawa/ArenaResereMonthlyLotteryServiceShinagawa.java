package com.copel.productpackages.arena.selenium.service.shinagawa;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.copel.productpackages.arena.selenium.service.unit.JapaneseHoliday;
import com.copel.productpackages.arena.selenium.service.unit.LineMessagingAPI;
import com.copel.productpackages.arena.selenium.service.unit.OriginalDate;
import com.copel.productpackages.arena.selenium.service.unit.WebBrowser;

import lombok.extern.slf4j.Slf4j;

/**
 * 品川区の毎月の抽選申し込み処理クラス.
 *
 * <p>利用月の4か月前の21日〜末日が抽選申込期間のため、毎月21日に実行し、
 * 4か月後の月の土日祝の枠へ抽選を申し込む。
 * 1施設1か月あたり最大10件。優先順は 午後1 &gt; 午後2 &gt; 午前 &gt; 夜間、同じ枠の中では日付の早い順。
 * 土曜は午前/午後1/午後2/夜間、日曜・祝日は午前/午後1/午後2（夜間なし）。</p>
 *
 * <p>DRY_RUN=true の場合、申込み内容の入力までを行い、最後の「申込み」押下・確認ダイアログのOKは行わない。</p>
 *
 * <p>ログに利用者番号・パスワードは出力しない（****でマスク）。</p>
 *
 * @author 鈴木一矢
 */
@Slf4j
public class ArenaResereMonthlyLotteryServiceShinagawa {
    /** 環境変数: 通知用LINEアカウントのチャンネルアクセストークン. */
    private static String LINE_CHANNEL_ACCESS_TOKEN = System.getenv("LINE_CHANNEL_ACCESS_TOKEN");
    /** 環境変数: 通知の宛先LINE ID（空可）. */
    private static List<String> NOTIFY_LINE_ID_LIST = parseList(System.getenv("NOTIFY_LINE_ID_LIST"));
    /** 環境変数: 品川区の団体登録ID. */
    private static String ACCOUNT_ID = System.getenv("ACCOUNT_ID");
    /** 環境変数: 品川区の団体登録パスワード. */
    private static String ACCOUNT_PASSWORD = System.getenv("ACCOUNT_PASSWORD");
    /** 環境変数: 対象体育館名（スクエア荏原 / 戸越体育館 / 総合体育館）. */
    private static String TARGET_ARENA_NAME = System.getenv("TARGET_ARENA_NAME");
    /** 環境変数: 対象コート名（既定: 全面）. */
    private static String TARGET_COURT_NAME = System.getenv("TARGET_COURT_NAME");
    /** 環境変数: trueなら最終送信をしない. */
    private static boolean DRY_RUN = "true".equalsIgnoreCase(System.getenv("DRY_RUN"));
    /** 環境変数: スクリーンショット保存先（未指定時は保存しない）. */
    private static String SCREENSHOT_DIR = System.getenv("SCREENSHOT_DIR");
    /** 環境変数: 対象月の上書き（yyyy-MM、テスト用）. */
    private static String TARGET_MONTH = System.getenv("TARGET_MONTH");

    /** 品川区施設予約システムのURL. */
    private static final String SHINAGAWA_RESERVE_SYSTEM_URL = "https://www.cm9.eprs.jp/shinagawa/web/";
    /** 1施設1か月あたりの申込上限. */
    public static final int MAX_APPLICATIONS = 10;
    /** 枠の優先順. */
    public static final List<品川区抽選枠> SLOT_PRIORITY = List.of(品川区抽選枠.午後1, 品川区抽選枠.午後2, 品川区抽選枠.午前, 品川区抽選枠.夜間);
    /** 利用目的・催し物名. */
    private static final String PURPOSE = "バスケットボール";
    /** 利用人数. */
    private static final String PEOPLE = "20";

    private static List<String> parseList(final String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return Arrays.stream(value.split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
    }

    /**
     * メイン文.
     *
     * @param args コマンドライン引数
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        String court = (TARGET_COURT_NAME == null || TARGET_COURT_NAME.isBlank()) ? "全面" : TARGET_COURT_NAME;
        ArenaResereMonthlyLotteryServiceShinagawa service = new ArenaResereMonthlyLotteryServiceShinagawa(
                true, LINE_CHANNEL_ACCESS_TOKEN, NOTIFY_LINE_ID_LIST, ACCOUNT_ID, ACCOUNT_PASSWORD,
                品川区体育館.getEnumByName(TARGET_ARENA_NAME), court);
        service.setDryRun(DRY_RUN);
        if (TARGET_MONTH != null && !TARGET_MONTH.isBlank()) {
            service.setTargetMonth(YearMonth.parse(TARGET_MONTH));
        }
        boolean ok = service.execute();
        System.exit(ok ? 0 : 1);
    }

    /** 申込候補. */
    public static class Candidate {
        public final LocalDate date;
        public final 品川区抽選枠 slot;
        public Candidate(final LocalDate date, final 品川区抽選枠 slot) {
            this.date = date;
            this.slot = slot;
        }
        public String code() {
            return this.date.format(DateTimeFormatter.BASIC_ISO_DATE) + "_" + slotSuffix(this.slot);
        }
        @Override
        public String toString() {
            return this.date.format(DateTimeFormatter.ofPattern("M/d(E)", Locale.JAPAN)) + " " + this.slot.name();
        }
    }

    /** 申込結果. */
    private static class Result {
        final Candidate candidate;
        final String status;
        final String reason;
        Result(final Candidate c, final String status, final String reason) {
            this.candidate = c;
            this.status = status;
            this.reason = reason;
        }
    }

    private WebBrowser webBrowser;
    private final boolean isHeadlessMode;
    private List<String> notifyLineIdList;
    private String channelAccessToken;
    private String accountId;
    private String accountPassword;
    private 品川区体育館 targetArena;
    private String targetCourtName;
    private boolean dryRun = false;
    private YearMonth targetMonth = YearMonth.now(ZoneId.of("Asia/Tokyo")).plusMonths(4);
    private int shotNo = 0;
    private final List<Result> results = new ArrayList<>();

    public ArenaResereMonthlyLotteryServiceShinagawa(final String channelAccessToken, final List<String> notifyLineIdList, final String accountId, final String accountPassword, final 品川区体育館 targetArena) {
        this(true, channelAccessToken, notifyLineIdList, accountId, accountPassword, targetArena, "全面");
    }
    public ArenaResereMonthlyLotteryServiceShinagawa(final boolean isHeadlessMode, final String channelAccessToken, final List<String> notifyLineIdList, final String accountId, final String accountPassword, final 品川区体育館 targetArena) {
        this(isHeadlessMode, channelAccessToken, notifyLineIdList, accountId, accountPassword, targetArena, "全面");
    }
    public ArenaResereMonthlyLotteryServiceShinagawa(final boolean isHeadlessMode, final String channelAccessToken, final List<String> notifyLineIdList, final String accountId, final String accountPassword, final 品川区体育館 targetArena, final String targetCourtName) {
        this.isHeadlessMode = isHeadlessMode;
        this.notifyLineIdList = notifyLineIdList == null ? List.of() : notifyLineIdList;
        this.channelAccessToken = channelAccessToken;
        this.accountId = accountId;
        this.accountPassword = accountPassword;
        this.targetArena = targetArena;
        this.targetCourtName = targetCourtName;
    }

    public void setDryRun(final boolean dryRun) {
        this.dryRun = dryRun;
    }
    public void setTargetMonth(final YearMonth targetMonth) {
        this.targetMonth = targetMonth;
    }

    // ------------------------------------------------------------------
    // 申込候補の作成
    // ------------------------------------------------------------------

    /** 祝日判定（計算＋祝日API の和集合）. */
    public static boolean isHoliday(final LocalDate date) {
        if (JapaneseHoliday.isHoliday(date)) {
            return true;
        }
        try {
            return new OriginalDate(date).is祝日();
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * 対象月の申込候補を優先順に並べて返す（上限なしの全候補）.
     * 土曜: 午前/午後1/午後2/夜間、日曜・祝日: 午前/午後1/午後2.
     */
    public static List<Candidate> buildCandidates(final YearMonth month) {
        List<Candidate> list = new ArrayList<>();
        for (品川区抽選枠 slot : SLOT_PRIORITY) {
            for (int d = 1; d <= month.lengthOfMonth(); d++) {
                LocalDate date = month.atDay(d);
                boolean sunOrHoliday = date.getDayOfWeek() == DayOfWeek.SUNDAY || isHoliday(date);
                boolean saturday = date.getDayOfWeek() == DayOfWeek.SATURDAY;
                if (sunOrHoliday) {
                    if (slot != 品川区抽選枠.夜間) list.add(new Candidate(date, slot));
                } else if (saturday) {
                    list.add(new Candidate(date, slot));
                }
            }
        }
        return list;
    }

    private static String slotSuffix(final 品川区抽選枠 slot) {
        switch (slot) {
            case 午前: return "10";
            case 午後1: return "20";
            case 午後2: return "30";
            case 夜間: return "40";
            default: return "";
        }
    }

    // ------------------------------------------------------------------
    // 画面操作ヘルパー
    // ------------------------------------------------------------------

    private WebDriver d() {
        return this.webBrowser.getDriver();
    }

    private Object js(final String script, final Object... args) {
        return ((JavascriptExecutor) d()).executeScript(script, args);
    }

    private void shot(final String name) {
        if (SCREENSHOT_DIR == null || SCREENSHOT_DIR.isBlank()) return;
        this.shotNo++;
        String prefix = String.format("%s/%s_%02d_%s", SCREENSHOT_DIR, this.targetArena.name(), this.shotNo, name);
        this.webBrowser.saveScreenshot(prefix + ".png");
        try {
            String src = d().getPageSource();
            if (this.accountId != null && !this.accountId.isEmpty()) src = src.replace(this.accountId, "****");
            if (this.accountPassword != null && !this.accountPassword.isEmpty()) src = src.replace(this.accountPassword, "****");
            Path p = Paths.get(prefix + ".html");
            Files.createDirectories(p.getParent());
            Files.writeString(p, src);
        } catch (Exception e) {
            // 無視
        }
    }

    private List<WebElement> find(final String xpath) {
        return d().findElements(By.xpath(xpath));
    }

    private WebElement firstVisible(final String... xpaths) {
        for (String x : xpaths) {
            for (WebElement e : find(x)) {
                try {
                    if (e.isDisplayed()) return e;
                } catch (Exception ignore) {
                    // 無視
                }
            }
        }
        return null;
    }

    private void jsClick(final WebElement e) throws InterruptedException {
        js("arguments[0].scrollIntoView({block:'center'}); arguments[0].click();", e);
        this.webBrowser.wait(2);
    }

    /** アラート/確認ダイアログの文言を取得して閉じる（accept=falseならキャンセル）. */
    private String handleAlert(final boolean accept) {
        try {
            Alert a = d().switchTo().alert();
            String text = a.getText();
            if (accept) a.accept(); else a.dismiss();
            return text;
        } catch (NoAlertPresentException e) {
            return null;
        }
    }

    /** メニューのdoActionを実行して画面遷移する. */
    private void doMenuAction(final String actionVarName) throws InterruptedException {
        js("doAction(document.form1, " + actionVarName + ");");
        this.webBrowser.wait(3);
        handleAlert(true);
    }

    private String bodyText() {
        try {
            return d().findElement(By.tagName("body")).getText();
        } catch (Exception e) {
            return "";
        }
    }

    // ------------------------------------------------------------------
    // 処理本体
    // ------------------------------------------------------------------

    /**
     * 処理.
     *
     * @return 処理が最後まで完了した場合true
     */
    public boolean execute() throws InterruptedException, IOException {
        List<Candidate> all = buildCandidates(this.targetMonth);
        log.info("品川区の月次抽選申込を開始します. 館: {}, コート: {}, 対象月: {}, DRY_RUN: {}, 候補数: {}",
                this.targetArena.name(), this.targetCourtName, this.targetMonth, this.dryRun, all.size());
        log.info("候補（優先順）: {}", all);

        if (this.accountId == null || this.accountId.isBlank() || this.accountPassword == null || this.accountPassword.isBlank()) {
            log.error("ACCOUNT_ID / ACCOUNT_PASSWORD が未設定のため終了します");
            return false;
        }

        this.webBrowser = new WebBrowser(this.isHeadlessMode);
        d().manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
        boolean completed = false;
        try {
            // (1) ログイン（失敗時は再試行しない：アカウントロック防止）
            if (!login()) {
                log.error("ログインに失敗しました。アカウントロック防止のため再試行せず終了します");
                return false;
            }

            // (2) 申込済みの抽選を確認（重複申込防止）
            List<String> applied = readAppliedLotteries();

            // (3) 優先順に最大10件申し込む
            int done = 0;
            int consecutiveErrors = 0;
            boolean dryRunFormChecked = false;
            boolean aborted = false;
            for (Candidate c : all) {
                if (done >= MAX_APPLICATIONS) {
                    this.results.add(new Result(c, "対象外", "上限10件に達したため"));
                    continue;
                }
                if (isAlreadyApplied(applied, c)) {
                    this.results.add(new Result(c, "スキップ", "申込済み"));
                    done++;
                    continue;
                }
                if (this.dryRun && dryRunFormChecked) {
                    this.results.add(new Result(c, "DRY_RUN", "申込予定（送信なし）"));
                    done++;
                    continue;
                }
                try {
                    String r = applyOne(c);
                    consecutiveErrors = 0;
                    if (r == null) {
                        this.results.add(new Result(c, this.dryRun ? "DRY_RUN" : "申込済", this.dryRun ? "入力まで確認（送信なし）" : "申込完了"));
                        done++;
                        if (this.dryRun) dryRunFormChecked = true;
                    } else {
                        this.results.add(new Result(c, "スキップ", r));
                    }
                } catch (FatalException fe) {
                    this.results.add(new Result(c, "中断", fe.getMessage()));
                    log.error("処理を中断します: {}", fe.getMessage());
                    aborted = true;
                    break;
                } catch (Exception e) {
                    consecutiveErrors++;
                    shot("error_" + c.code());
                    String msg = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage().split("\n")[0];
                    this.results.add(new Result(c, "エラー", msg));
                    log.warn("{} の申込中にエラー: {}", c, msg);
                    if (consecutiveErrors >= 3) {
                        log.error("連続3件エラーのため中断します");
                        break;
                    }
                }
            }
            completed = !aborted;
            return completed;
        } finally {
            try {
                logout();
            } catch (Exception ignore) {
                // 無視
            }
            this.webBrowser.quit();
            printSummary(completed);
            notifyLine();
        }
    }

    /** 処理継続不能な状態. */
    private static class FatalException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        FatalException(final String m) {
            super(m);
        }
    }

    private boolean login() throws InterruptedException {
        this.webBrowser.access(SHINAGAWA_RESERVE_SYSTEM_URL);
        this.webBrowser.wait(2);
        this.webBrowser.clickByXpathWithJS("//*[@id=\"btn-login\"]");
        log.info("「ログイン」ボタンを押下");
        this.webBrowser.wait(2);
        this.webBrowser.sendKeysByXpath("//*[@id=\"userId\"]", this.accountId);
        log.info("「利用者番号」で****を入力");
        this.webBrowser.sendKeysByXpath("//*[@id=\"password\"]", this.accountPassword);
        log.info("「パスワード」で****を入力");
        this.webBrowser.clickByXpathWithJS("//*[@id=\"btn-go\"]");
        log.info("「ログイン」ボタンを押下");
        this.webBrowser.wait(4);
        String alert = handleAlert(true);
        if (alert != null) log.info("ログイン時のダイアログ: {}", alert);
        boolean failed = !find("//*[@id=\"password\"]").isEmpty() || find("//*[@id=\"btn-logout\"]").isEmpty();
        shot("after_login");
        if (failed) {
            String t = bodyText();
            log.error("ログイン後の画面が想定と異なります: {}", t.length() > 200 ? t.substring(0, 200) : t);
            return false;
        }
        log.info("ログインに成功しました");
        return true;
    }

    private void logout() throws InterruptedException {
        WebElement b = firstVisible("//*[@id=\"btn-logout\"]");
        if (b != null) {
            jsClick(b);
            handleAlert(true);
            log.info("ログアウトしました");
        }
    }

    /** 抽選申込の確認・取消（抽選受付済一覧）から申込済みの行テキストを取得する. */
    private List<String> readAppliedLotteries() {
        List<String> rows = new ArrayList<>();
        try {
            doMenuAction("gLotWTransLotCancelListAction");
            shot("applied_list");
            for (WebElement tr : find("//article[@id='lottery-application']//tr")) {
                String t = tr.getText().replace("\n", " ").trim();
                if (!t.isEmpty()) rows.add(t);
            }
            log.info("申込済み一覧: {}件の行を取得", rows.size());
        } catch (Exception e) {
            log.warn("申込済み一覧を取得できませんでした（重複確認なしで続行）: {}", e.getMessage());
        }
        return rows;
    }

    private boolean isAlreadyApplied(final List<String> rows, final Candidate c) {
        String[] dateForms = {
            c.date.format(DateTimeFormatter.ofPattern("yyyy年M月d日")),
            c.date.format(DateTimeFormatter.ofPattern("yyyy/M/d")),
            c.date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
            c.date.format(DateTimeFormatter.ofPattern("M月d日")),
        };
        String[] slotForms = slotTimeForms(c.slot);
        for (String r : rows) {
            if (!r.contains(this.targetArena.name())) continue;
            boolean dateHit = Arrays.stream(dateForms).anyMatch(r::contains);
            boolean slotHit = r.contains(c.slot.name()) || Arrays.stream(slotForms).anyMatch(r::contains);
            if (dateHit && slotHit) return true;
        }
        return false;
    }

    private static String[] slotTimeForms(final 品川区抽選枠 slot) {
        switch (slot) {
            case 午前: return new String[] {"9:00", "09:00", "9時00分", "09時00分"};
            case 午後1: return new String[] {"12:00", "12時00分"};
            case 午後2: return new String[] {"15:00", "15時00分", "14:30", "14時30分"};
            case 夜間: return new String[] {"18:00", "18時00分"};
            default: return new String[] {};
        }
    }

    /**
     * 1件申し込む.
     *
     * @return null=成功（DRY_RUNでは入力まで成功）、それ以外=スキップ理由
     */
    private String applyOne(final Candidate c) throws InterruptedException {
        log.info("--- {} の申込を開始 ---", c);

        // (a) 抽選の申込み → 抽選一覧
        doMenuAction("gLotWOpeLotSearchAction");
        WebElement all = firstVisible("//*[@id=\"btn-all\"]");
        if (all != null) jsClick(all);
        shot("lottery_list");

        // (b) 対象の抽選分類の「申込」ボタン
        WebElement entry = findLotteryEntryButton();
        if (entry == null) {
            throw new FatalException("受付中の抽選分類（申込ボタン）が見つかりません（申込期間外の可能性）");
        }
        jsClick(entry);
        String a = handleAlert(true);
        if (a != null) throw new FatalException("抽選申込ボタン押下時のメッセージ: " + a);
        shot("facility_select");

        // (c) 館・施設を選択
        selectIfPresent("//*[@id=\"mansion-select\"]", this.targetArena.name());
        selectIfPresent("//*[@id=\"facility-select\"]", this.targetArena.getDisplayWithCourtName(this.targetCourtName));

        // (d) 対象日の表示まで移動
        String cellId = c.code();
        if (!moveToDate(c.date, cellId)) {
            shot("date_not_found_" + cellId);
            return "日付のセル(" + cellId + ")が見つからない（休館/対象外の可能性）";
        }

        // (e) 枠を選択
        WebElement cell = firstVisible("//*[@id=\"" + cellId + "\"]");
        String state = "";
        try {
            WebElement img = cell.findElement(By.xpath(".//img"));
            state = String.valueOf(img.getAttribute("alt"));
        } catch (Exception e) {
            state = cell.getText();
        }
        if (state.contains("休") || state.contains("×") || state.contains("不可") || state.contains("期間外") || state.contains("予約あり")) {
            return "選択不可（" + state + "）";
        }
        jsClick(cell);
        handleAlert(true);
        String selected = "";
        try {
            selected = String.valueOf(d().findElement(By.id("S_" + cellId)).getAttribute("value"));
        } catch (Exception e) {
            // 入力項目がない画面もある
        }
        if (!selected.isEmpty() && !"1".equals(selected)) {
            return "枠を選択できない（状態: " + state + "）";
        }
        shot("slot_selected_" + cellId);

        // (f) 「申込み」ボタン（枠選択画面）→ 申込内容入力画面
        WebElement go = firstVisible("//*[@id=\"btn-go\"]", "//button[contains(normalize-space(.),'申込')]", "//input[contains(@value,'申込')]");
        if (go == null) throw new IllegalStateException("枠選択画面の「申込み」ボタンが見つかりません");
        jsClick(go);
        String a2 = handleAlert(true);
        if (a2 != null) {
            shot("after_slot_go_alert");
            return "枠の申込み押下時のメッセージ: " + a2;
        }
        shot("apply_form");

        // (g) 利用目的・催し物名・利用人数・申込番号/希望番号を入力
        fillApplicationForm();
        shot("apply_form_filled_" + cellId);

        if (this.dryRun) {
            log.info("DRY_RUNのため最終の「申込み」は押下しません: {}", c);
            return null;
        }

        // (h) 「申込み」→ 確認ダイアログOK
        WebElement submit = firstVisible("//*[@id=\"btn-go\"]", "//button[contains(normalize-space(.),'申込')]");
        if (submit == null) throw new IllegalStateException("申込内容画面の「申込み」ボタンが見つかりません");
        jsClick(submit);
        String confirm = handleAlert(true);
        log.info("確認ダイアログ: {}", confirm);
        this.webBrowser.wait(2);
        String extra = handleAlert(true);
        if (extra != null) log.info("追加ダイアログ: {}", extra);
        this.webBrowser.wait(3);
        shot("apply_done_" + cellId);
        String t = bodyText();
        if (t.contains("受付") || t.contains("完了") || t.contains("続けて")) {
            log.info("{} の抽選申込が完了しました", c);
            return null;
        }
        throw new IllegalStateException("申込完了画面を確認できませんでした");
    }

    private WebElement findLotteryEntryButton() {
        String arena = this.targetArena.name();
        List<WebElement> rows = find("//tbody/tr");
        WebElement fallback = null;
        for (WebElement tr : rows) {
            String t;
            try {
                t = tr.getText();
            } catch (Exception e) {
                continue;
            }
            List<WebElement> btns = tr.findElements(By.xpath(".//*[contains(@onclick,'doLotEntry') or contains(@href,'doLotEntry') or normalize-space(.)='申込']"));
            if (btns.isEmpty()) continue;
            if (t.contains(arena)) return btns.get(0);
            if (fallback == null && (t.contains("体育") || t.contains("スポーツ") || t.contains("屋内"))) fallback = btns.get(0);
        }
        if (fallback != null) {
            log.info("館名を含む抽選分類が見つからないため、体育施設の抽選分類を使用します");
        }
        return fallback;
    }

    private void selectIfPresent(final String xpath, final String text) throws InterruptedException {
        List<WebElement> es = find(xpath);
        if (es.isEmpty()) return;
        Select s = new Select(es.get(0));
        for (WebElement o : s.getOptions()) {
            if (o.getText().trim().equals(text) || o.getText().contains(text)) {
                s.selectByVisibleText(o.getText());
                js("arguments[0].dispatchEvent(new Event('change',{bubbles:true}));", es.get(0));
                this.webBrowser.wait(2);
                log.info("{} で「{}」を選択", xpath, o.getText().trim());
                return;
            }
        }
        log.warn("{} に「{}」の選択肢がありません", xpath, text);
    }

    private boolean moveToDate(final LocalDate date, final String cellId) throws InterruptedException {
        if (!find("//*[@id=\"" + cellId + "\"]").isEmpty()) return true;
        // 日付入力欄がある場合は直接入力
        List<WebElement> inputs = find("//input[@type='date' or @id='daystart' or @id='date']");
        if (!inputs.isEmpty()) {
            js("arguments[0].value=arguments[1]; arguments[0].dispatchEvent(new Event('change',{bubbles:true}));",
                    inputs.get(0), date.toString());
            this.webBrowser.wait(3);
            if (!find("//*[@id=\"" + cellId + "\"]").isEmpty()) return true;
        }
        // 「次へ」系ボタンで移動（最大10回）
        for (int i = 0; i < 10; i++) {
            WebElement next = firstVisible("//*[@id='next-week']", "//*[@id='next-month']", "//*[@id='btn-next']",
                    "//button[contains(normalize-space(.),'次の') or contains(normalize-space(.),'翌')]",
                    "//a[contains(normalize-space(.),'次の') or contains(normalize-space(.),'翌')]");
            if (next == null) break;
            jsClick(next);
            handleAlert(true);
            if (!find("//*[@id=\"" + cellId + "\"]").isEmpty()) return true;
        }
        return false;
    }

    private void fillApplicationForm() throws InterruptedException {
        // 利用目的
        List<WebElement> purpose = find("//select[@id='purpose' or @name='purpose' or contains(@name,'Pps')]");
        if (!purpose.isEmpty()) {
            Select s = new Select(purpose.get(0));
            String pick = null;
            for (WebElement o : s.getOptions()) {
                if (o.getText().contains(PURPOSE)) {
                    pick = o.getText();
                    break;
                }
            }
            if (pick == null && s.getOptions().size() > 1) pick = s.getOptions().get(1).getText();
            if (pick != null) {
                s.selectByVisibleText(pick);
                js("arguments[0].dispatchEvent(new Event('change',{bubbles:true}));", purpose.get(0));
                this.webBrowser.wait(1);
                log.info("「利用目的」で「{}」を選択", pick.trim());
            }
        }
        // 催し物名
        for (WebElement e : find("//input[@name='eventName' or starts-with(@id,'eventname')]")) {
            if (e.isDisplayed()) {
                e.clear();
                e.sendKeys(PURPOSE);
                log.info("「催し物名」を入力");
                break;
            }
        }
        // 利用人数
        for (WebElement e : find("//input[@name='applyPepopleNum' or @name='applyNum' or starts-with(@id,'peoples')]")) {
            if (e.isDisplayed()) {
                e.clear();
                e.sendKeys(PEOPLE);
                log.info("「利用人数」を入力");
                break;
            }
        }
        // 申込番号・希望番号（"申込番号-希望番号" 形式。"0-0" は未選択）
        List<WebElement> hope = find("//select[@name='applyHopeNo' or @id='apply']");
        if (!hope.isEmpty()) {
            Select s = new Select(hope.get(0));
            for (WebElement o : s.getOptions()) {
                String v = String.valueOf(o.getAttribute("value"));
                if (!"0-0".equals(v) && !v.isBlank()) {
                    s.selectByValue(v);
                    log.info("「申込番号・希望番号」で「{}」を選択", o.getText().trim());
                    break;
                }
            }
        }
    }

    // ------------------------------------------------------------------
    // 結果
    // ------------------------------------------------------------------

    private String summaryText(final boolean completed) {
        StringBuilder sb = new StringBuilder();
        sb.append("【品川区 月次抽選申込】").append(this.targetArena.name()).append(" ")
          .append(this.targetArena.getDisplayWithCourtName(this.targetCourtName))
          .append(" 対象月 ").append(this.targetMonth).append(this.dryRun ? "（DRY_RUN・送信なし）" : "").append("\n");
        if (!completed) sb.append("※ 処理が途中で終了しました\n");
        for (Result r : this.results) {
            sb.append("・").append(r.candidate).append(" … ").append(r.status);
            if (r.reason != null && !r.reason.isEmpty()) sb.append("（").append(r.reason).append("）");
            sb.append("\n");
        }
        if (this.results.isEmpty()) sb.append("（申込処理を行った枠はありません）\n");
        return sb.toString();
    }

    private void printSummary(final boolean completed) {
        String s = summaryText(completed);
        System.out.println("==== SUMMARY ====");
        System.out.println(s);
        System.out.println("=================");
    }

    private void notifyLine() {
        if (this.channelAccessToken == null || this.channelAccessToken.isBlank() || this.notifyLineIdList.isEmpty()) {
            return;
        }
        try {
            String msg = summaryText(true).replace("\n", "\\n");
            for (String id : this.notifyLineIdList) {
                LineMessagingAPI api = new LineMessagingAPI(this.channelAccessToken, id);
                api.addMessage(msg);
                api.sendAll();
            }
            log.info("LINEに通知を送信しました");
        } catch (Exception e) {
            log.warn("LINE通知に失敗しました: {}", e.getMessage());
        }
    }
}
