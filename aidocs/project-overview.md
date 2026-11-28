# common-utils プロジェクト俯瞰

## 目的

`common-utils` は、複数の Spring Boot アプリケーションから共有する Java ライブラリです。
主な責務は、API レスポンスの共通形、ページング処理、動的なレスポンス項目・ソート指定、
JPA/Hibernate 補助、文字列・日時などの汎用処理、業務例外の提供です。

アプリケーション単体ではなく Maven artifact として配布されます。OSS として公開する準備を進めています(方針は `README.md` の「今後の方針」)。

- groupId: `jp.kurashina`
- artifactId: `common-utils`
- version: `${revision}`（現在の `revision` は `4.0.9`）
- Java: 21
- Spring Boot: 4.0.9
- JSON: Jackson 3（アノテーションは互換パッケージの `com.fasterxml.jackson.annotation`）
- 配布先: GitHub Packages (`kurashina-design/CommonUtils`)

## ディレクトリと成果物

| 場所 | 役割 |
| --- | --- |
| `src/main/java/jp/kurashina/commons` | 公開ライブラリ実装 |
| `src/test/java/jp/kurashina/commons` | JUnit のテスト |
| `README.md` | 利用者向けの説明、使い方の例、ビルドと公開 |
| `AGENTS.md` | AI が作業するときの引き継ぎルール |
| `aidocs/` | AI 向けの補助ドキュメント(`handoff.md` が引き継ぎの正本) |
| `pom.xml` | Java/Spring の依存関係、source/javadoc jar、配布設定 |
| `target/` | Maven の生成物（編集対象ではない） |

テストは `ApiErrorResponseFactoryTest`、`BigDecimalIntegerSerializerTest`、`PaginationHelperTest`、`QuerySortArgumentResolverTest`、`RequestIdFilterTest`、`SortUtilsTest`、`SortableFieldsTest` の7クラス です(2026-11-28 時点)。

## パッケージ別の責務

### `resource`

API が返す共通 DTO 群です。

- `AbstractAuditResponse`: `createdBy`, `createdDate`, `lastModifiedBy`, `lastModifiedDate`
- `AbstractEntityResponse`: 監査情報に `version` を追加
- `ResultResponse<T>`: `result` と任意の `data`
- `Pagination<T>`: content、件数、ページ、sort、前後リンクなどのページング情報
- `PaginationPath`, `PaginationPathParameters`: ページングリンクのパスとクエリ情報
- `CurrentNumberOfElementsMap`: ページ位置と現在件数の対応情報
- `CountResponse`, `PingResponse`: 小さな定型レスポンス

Jackson の `NON_EMPTY` と、フィールド名そのまま(camelCase)の JSON 名が外部 API 契約の一部です。
DTO の JSON 名・型・継承を変えると、利用側 API の契約が変わります。

### `annotation` / `annotation.support`

Spring MVC と Hibernate の拡張点です。

- `@QueryFields`: `fields=id,children[id,name]` をドット記法の一覧へ展開
- `QueryFieldsArgumentResolver`: 深度制限と除外規則を適用
- `@QuerySort`: `sort=priority,-createdAt` をソートキー一覧として受け取る
- `QuerySortArgumentResolver`: カンマ区切り・複数パラメータ・必須/default を処理
- `@ValidPerPage` / `PerPageValidator`: 1ページ当たり件数の範囲検証
- シークエンスID生成には Jakarta Persistence 標準の `@GeneratedValue` と
  `@SequenceGenerator` を使用する（詳細は `spring-boot-4-migration.md`）

引数リゾルバーはクラスを置くだけでは有効になりません。利用側アプリケーションで
`WebMvcConfigurer#addArgumentResolvers` などを使って登録する必要があります。

### `api.error` / `web`

API エラーレスポンスの共通部品です。詳細は `aidocs/api-error-components.md` にあります。

- `ApiErrorResponse` / `ApiErrorDetail`: 共通のエラーボディとフィールド単位のエラー
- `ApiErrorResponseFactory`: 4xx は具体的な detail を残し、5xx は固定文言に置き換える
- `ApiErrorResponseWriter`: HTTP status、JSON ボディ、`X-Request-Id` ヘッダーを書き出す
- `RequestIdFilter` / `RequestIdUtils`: 上流の `X-Request-Id` を引き継ぎ、無ければ UUID を作る

`@RestControllerAdvice` や、例外から HTTP status への対応付けは利用側 API に置きます。
フィルターと Writer は自動登録されないので、利用側で Bean として登録します。

### `helper`

`PaginationHelper<E, R>` が Spring Data の `Page` / `Pageable` / `Sort` と
このライブラリの `Pagination<R>` を橋渡しします。ページ番号は API 上の 1 始まりと
Spring Data の 0 始まりを変換します。また、リフレクションを使った親子階層の組み立ても担います。

### `dto`

`ResponseField` は、要求されたレスポンス項目をルート項目とネスト項目の木として保持します。
`@QueryFields` の解析結果から構築し、動的レスポンス生成や Bean コピーの選択に利用します。

### `util`

- `BeanUtils`: Spring の Bean コピーを補強し、PATCH や指定項目コピーを支援
- `CollectionUtils`, `StringUtils`, `ObjectUtils`: 汎用コレクション・文字列・cast 操作
- `DateTimeUtils`, `ZoneIdConstants`: Asia/Tokyo・UTC と和暦変換
- `SortUtils`, `EntityFieldUtils`: Sort 生成と JPA entity のフィールド抽出
- `SortableFields`: JPA メタモデルから、エンティティごとのソート可能な項目の許可リストを作る(自動登録されないので利用側で Bean 登録する)
- `ResponseFieldUtils`: `ResponseField` の生成・判定
- `CorporateNameNormalizer`, `PrefectureUtils`: 日本向けの名称正規化
- `UserAgentUtils`: User-Agent 候補からランダム選択

### その他

- `exception`: HTTP/API 層で意味付けして使う RuntimeException 群。ライブラリ内に共通例外ハンドラーはない
- `cache.key`: 呼び出し URI と引数に基づく Spring Cache key 生成
- `jackson`: `BigDecimal` を小数部なしの JSON 数値として出す serializer
- `service`: classpath resource を文字列・stream・file として読む `FileService`
- `security`: `SecurityRole` enum と `SecurityMapping`

## 主な処理の流れ

### fields 指定

1. Controller 引数に `@QueryFields` を付ける
2. `QueryFieldsArgumentResolver` がブラケット記法をフラットなパスへ変換する
3. `maxDepth` と `excluding` を適用する
4. `ResponseFieldUtils.build` / `ResponseField` で項目ツリーにする
5. レスポンス DTO の構築や `BeanUtils` の選択コピーに使う

### ページングと sort

1. Controller 引数に `@QuerySort` を付ける
2. `QuerySortArgumentResolver` が昇順キーと `-` 付き降順キーを解析する
3. `SortableFields` の許可リストで検証し、`SortUtils` と `PaginationHelper#getPageable` で Spring Data の問い合わせ条件にする
4. Repository の `Page<E>` を `PaginationHelper#get` で API レスポンスへ変換する

## 依存関係の性質

これは軽量な純 Java utility jar ではありません。公開 API が次のフレームワーク型・機構へ直接依存します。

- Spring Web MVC
- Spring Data JPA
- Jakarta Validation
- Hibernate ORM
- Jackson
- Lombok
- Apache Commons Lang / Collections

Spring Boot/Web/Log4j2 の一部は `provided` ですが、Data JPA、Validation、Jackson、Hibernate は通常依存です。
依存スコープを変える場合は、利用側で必要な型が classpath に残るか確認します。

## 変更時の確認事項

1. 公開クラス・メソッドの変更は、利用側サービスとのバイナリ/API 互換性を確認する。
2. `resource` DTO の JSON 名、型、継承を変えるときは、利用側 API の契約変更として扱う。
3. `@QueryFields` / `@QuerySort` の変更は、未指定、空文字、default、required、ネスト、除外を確認する。
4. ページング変更では 0 始まり/1 始まり、空ページ、先頭・末尾ページを確認する。
5. リフレクションを使う処理では、継承フィールド、null、存在しないフィールドを確認する。
6. Java 21 と現在の Spring Boot/Hibernate の組み合わせで Maven build を通す。
7. 公開前に `mvn versions:display-dependency-updates`、配布時に認証済み環境で `mvn deploy` を使う。

## 現状把握で見えた保守上の注意

- テストが無い領域が多い: `ResponseFieldUtils`、`BeanUtils`、`CollectionUtils`、`StringUtils`、`QueryFieldsArgumentResolver`、`DateTimeUtils`、`CorporateNameNormalizer` など。
- `README.md` に、Spring Boot のバージョンと切り離したバージョニング方針がまだない。
- `enumuration`, `TokenTimoutException`, `getSortableFiels` など、公開名に綴りの揺れがある。修正は破壊的変更になり得る。
- utility class の多くは static メソッドのみですが `@Component` も付いており、利用方針が二重になっている。
- RuntimeException 群は型だけを提供し、HTTP status への対応付けは利用側に委ねられている。

## AI が次回作業を始めるときの読み順

1. `AGENTS.md` と `aidocs/handoff.md` の「いまの状況」
2. `pom.xml`
3. 本文書
4. 変更対象パッケージの実装
5. 利用箇所の確認が必要なら、この repository 外の consumer 側コード(事前に利用者の許可を得る)

既存の公開名や JSON 契約を「誤記だから」という理由だけで変更しないでください。
consumer への影響を確認し、必要なら旧 API を残した段階的移行にします。
