package com.copel.productpackages.arena.selenium.service.unit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.TreeSet;

import org.openqa.selenium.Cookie;

public class Session {
    /**
     * セッション.
     */
    private Set<Cookie> cookies;
    /**
     * 保存先ファイルパス.
     */
    private String filePath;

    /**
     * コンストラクタ.
     *
     * @param cookies セッション
     */
    public Session(final Set<Cookie> cookies, final String filePath) {
        this.cookies = cookies;
        this.filePath = filePath;
    }

    /**
     * このオブジェクトにもつfilePathをセットします.
     *
     * @param filePath ファイルパス
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * このオブジェクトにクッキーをセットします.
     *
     * @param cookies クッキー
     */
    public void setCookies(Set<Cookie> cookies) {
        this.cookies = cookies;
    }

    /**
     * このオブジェクトに持っているクッキーを返却します.
     *
     * @return cookies
     */
    public Set<Cookie> getCookies() {
        return this.cookies;
    }

    /**
     * このセッションをファイルに保存する.
     *
     * @throws IOException
     */
    public void save() throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(this.filePath));
        for (Cookie cookie : this.cookies) {
            writer.println(cookieToString(cookie));
        }
        writer.close();
    }

    /**
     * ファイルから読み込んだセッションをこのオブジェクトにセットする.
     *
     * @throws IOException
     */
    public void loead() throws IOException {
        this.cookies = new TreeSet<Cookie>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(";");
            String name = parts[0];
            String value = parts[1];
            String domain = parts[2];
            String path = parts[3];
            String expiryStr = parts[4];
            boolean isSecure = Boolean.parseBoolean(parts[5]);

            Cookie cookie = new Cookie.Builder(name, value)
                .domain(domain)
                .path(path)
                .expiresOn("null".equals(expiryStr) ? null : java.sql.Timestamp.valueOf(expiryStr))
                .isSecure(isSecure)
                .build();

            this.cookies.add(cookie);
        }
        reader.close();
    }

    /**
     * Cookieを文字列化するメソッド.
     *
     * @param cookie クッキー
     * @return 文字列
     */
    private String cookieToString(Cookie cookie) {
        return String.format("%s;%s;%s;%s;%s;%s",
            cookie.getName(),
            cookie.getValue(),
            cookie.getDomain(),
            cookie.getPath(),
            cookie.getExpiry() != null ? cookie.getExpiry().toString() : "null",
            cookie.isSecure());
    }
}
