# APIエラーレスポンス共通部品

## 目的

`vehicle-api` と `content-api` が同じJSON契約とrequest ID伝達規則を使うための、薄い共通部品を提供する。
このライブラリはHTTPエラー処理全体を所有せず、レスポンスの組み立てと書き出しだけを支援する。

## 提供する部品

- `ApiErrorDetail`: フィールド単位のエラー。`detail` は必須、`field` は任意。
- `ApiErrorResponse`: 共通エラーボディ。`statusMessage` は `statusCode` から自動導出され、setterは提供しない。
- `ApiErrorResponseFactory`: 4xxの具体的なdetailを保持し、5xxは固定文言へ置換する。`data` の予約キーも拒否する。
- `ApiErrorResponseWriter`: HTTP status、JSONボディ、`X-Request-Id` レスポンスヘッダーを書き出す。
- `RequestIdUtils`: request IDの取得・生成とrequest attributeへの保存を行う。
- `RequestIdFilter`: 上流の `X-Request-Id` を引き継ぎ、存在しなければUUIDを生成してレスポンスにも設定する。

## 責務境界

common-utilsには次を置かない。

- 全例外を捕捉する `@RestControllerAdvice`
- 例外型からHTTP statusへのプロジェクト固有マッピング
- Spring Securityの `AuthenticationEntryPoint` / `AccessDeniedHandler`
- ロギング、監視、業務固有のエラーコード

これらはAPIごとに例外体系やSecurity設定が異なるため、`vehicle-api` / `content-api` 側に残す。
`data` はプロジェクト固有メタデータ専用とし、status、detail、requestId、errorsの代替やスタックトレースを格納しない。

## 導入手順

各APIで `RequestIdFilter` と `ApiErrorResponseWriter` をBean登録する。

```java
@Bean
RequestIdFilter requestIdFilter() {
    return new RequestIdFilter();
}

@Bean
ApiErrorResponseWriter apiErrorResponseWriter(ObjectMapper objectMapper) {
    return new ApiErrorResponseWriter(objectMapper);
}
```

`ApiExceptionHandler` では `RequestIdUtils.getOrCreate(request)` でIDを取得し、factoryでbodyを作る。
通常のMVC例外は `ResponseEntity<ApiErrorResponse>` で返せる。Spring Securityのfilter層ではwriterを使う。

```java
String requestId = RequestIdUtils.getOrCreate(request);
ApiErrorResponse body = ApiErrorResponseFactory.create(status, detail, requestId, errors, data);
```

## APIプロジェクト側に残す実装

- `MethodArgumentNotValidException`、`BindException`、`ConstraintViolationException`、`HttpMessageNotReadableException` 等を変換する `ApiExceptionHandler`
- 既存の業務例外とHTTP status/detailの対応付け
- Security用の `AuthenticationEntryPoint` と `AccessDeniedHandler`
- request IDを含むログ出力（必要ならMDC連携）
- 各endpointのOpenAPI responsesから `common-utils.yaml` のschemaを参照する設定
