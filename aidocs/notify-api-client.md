# notify-api 共通クライアント

## 配置と依存方針

notify-api 固有のコードは `jp.kurashina.commons.notify` に閉じています。

- `dto`: notify-api の公開 JSON 契約
- `client`: `POST /v1/emails` の低レベル呼び出し
- `exception`: 通信・JSON・非202応答の共通例外

WebClient と Reactor は追加せず、Java 21 標準の `java.net.http.HttpClient` を使います。
JSON処理は common-utils が既に依存している Jackson を使います。

## 利用例

```java
NotifyApiClient client = new NotifyApiClient("https://notify-api.example.com");

NotifyEmailRequest request = new NotifyEmailRequest();
request.setFromAddress(new NotifyEmailAddress("no-reply@example.com", "Example Service"));
request.setTo(List.of(new NotifyEmailAddress("user@example.com", "User")));
request.setReplyTo(new NotifyEmailAddress("support@example.com", null));
request.setSubject("お知らせ");
request.setText("本文です");

NotifyEmailResponse response = client.sendEmail(firebaseIdToken, request);
```

Base URL に `/v1/emails` は含めません。Firebase IDトークンの取得・更新と、
`roles.NOTIFY_API` に `SERVICE` を含めるための認証設定は利用側アプリケーションの責務です。

クライアントは同期呼び出しです。成功は HTTP 202 のみとし、それ以外は
`NotifyApiException` を投げます。非202応答では `getStatusCode()` と
`getResponseBody()` で notify-api の応答を確認できます。ログへトークンを出力しないでください。

利用側で共通設定済みの Jackson や `HttpClient` を使いたい場合は、3引数constructorへ渡せます。

## JSONマッピング

| Java | JSON |
| --- | --- |
| `fromAddress` | `from` |
| `replyTo` | `replyTo` |
| `messageId` | `messageId` |
| `smtpServer` | `smtpServer` |

差込変数、テンプレート展開、本文生成、送信対象決定、Firebaseトークン生成はこのライブラリの責務外です。
