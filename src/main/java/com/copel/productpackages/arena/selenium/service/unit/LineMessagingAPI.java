package com.copel.productpackages.arena.selenium.service.unit;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 【フレームワーク部品】
 * LINE Messaging APIでLINE通知を送るクラス
 *
 * @author 鈴木一矢
 *
 */
public class LineMessagingAPI {
    // ================================
    // フィールド定義
    // ================================
    /**
     * メッセージ送信APIのエンドポイント
     */
    private static final String PUSH_MESSAGE_API_ENDPOINT = "https://api.line.me/v2/bot/message/push";
    /**
     * ブロードキャストAPIのエンドポイント
     */
    private static final String BROADCAST_API_ENDPOINT = "https://api.line.me/v2/bot/message/broadcast";
    /**
     * 通知対象のLINEユーザーID
     */
    private String userId;
    /**
     * メッセージリスト
     */
    private List<String> messageList;
    /**
     * チャンネルアクセストークン.
     */
    private String channelAccessToken;

    // ================================
    // コンストラクタ定義
    // ================================
    public LineMessagingAPI(final String channelAccessToken) {
        this.channelAccessToken = channelAccessToken;
        this.messageList = new ArrayList<String>();
    }
    public LineMessagingAPI(final String channelAccessToken, final String userId) {
        this.channelAccessToken = channelAccessToken;
        this.userId = userId;
        this.messageList = new ArrayList<String>();
    }

    // ================================
    // メソッド定義
    // ================================
    /**
     * このオブジェクトのmessageListフィールドに引数のmessageを格納します
     *
     * @param message メッセージ
     */
    public void addMessage(final String message) {
        this.messageList.add(message);
    }

    /**
     * このオブジェクトのmessageListを返却します.
     *
     * @return メッセージ
     */
    public String getMessages() {
        String result = "";
        for (String message : this.messageList) {
            result += message;
        }
        return result;
    }

    /**
     * このオブジェクトが持つmessageListをString型のリストに変換して返却します
     *
     * @return String型のメッセージ配列
     */
    public List<String> toStringList() {
        List<String> list = new ArrayList<String>();
        for (String message : this.messageList) {
            list.add(message);
        }
        return list;
    }

    /**
     * messageListの各要素ごとにこのオブジェクトにもつuserIdの個別LINEにメッセージ送信リクエストを送信するメソッド
     * @return ResultObject 送信結果
     */
    public boolean sendSeparate() {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            for (final String message : this.messageList) {
                // JSON形式のメッセージボディを作成
                String json = "{\"to\":\""
                    + this.userId
                    + "\",\"messages\":[{\"type\":\"text\",\"text\":\""
                    + message
                    + "\"}]}";

                // HTTPリクエストの作成
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(PUSH_MESSAGE_API_ENDPOINT))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + this.channelAccessToken)
                        .POST(BodyPublishers.ofString(json))
                        .build();
                
                // リクエスト送信
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                // レスポンスステータスの確認
                if (response.statusCode() != 200) {
                    System.out.println("メッセージの送信に失敗しました。" + response.body());
                    return false;
                }
            }
        } catch (Exception e) {
            System.out.println("メッセージの送信に失敗しました。" + e.getStackTrace());
            return false;
        }

        return true;
    }

    /**
     * messageListの各要素を全て結合しLINE Messaging APIにメッセージ送信リクエストを送信するメソッド
     * @return ResultObject 送信結果
     */
    public boolean sendAll() {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            // JSON形式のメッセージボディを作成
            String json = "{\"to\":\""
                    + this.userId
                    + "\",\"messages\":[{\"type\":\"text\",\"text\":\""
                    + String.join("", this.toStringList())
                    + "\"}]}";

            // HTTPリクエストの作成
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(PUSH_MESSAGE_API_ENDPOINT))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + this.channelAccessToken)
                    .POST(BodyPublishers.ofString(json))
                    .build();
            
            // リクエスト送信
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // レスポンスステータスの確認
            if (response.statusCode() != 200) {
                System.out.println("メッセージの送信に失敗しました。" + response.body());
                return false;
            }
        } catch (Exception e) {
            System.out.println("メッセージの送信に失敗しました。" + e.getStackTrace());
            return false;
        }

        return true;
    }

    /**
     * このオブジェクトに格納されているメッセージを全ユーザーにメッセージ配信する
     *
     * @return 実行結果
     */
    public boolean broadCast() {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
 
            // メッセージボディを作成
            String jsonMessage = "{\"messages\":[{\"type\":\"text\",\"text\":\"";
            for (String lineMessage : this.messageList) {
                jsonMessage += lineMessage;
            }
            jsonMessage += "\"}]}";

            // HTTPリクエストの作成
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(BROADCAST_API_ENDPOINT))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + this.channelAccessToken)
                    .POST(BodyPublishers.ofString(jsonMessage))
                    .build();

            // リクエストを送信
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // レスポンスステータスの確認
            if (response.statusCode() != 200) {
                System.out.println("メッセージの送信に失敗しました。" + response.body());
                return false;
            }
        } catch (Exception e) {
            System.out.println("メッセージの送信に失敗しました。" + e.getStackTrace());
            return false;
        }

        return true;
    }
}
