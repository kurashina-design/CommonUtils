# Spring Boot 4 移行ガイド

`boot4` ブランチは Spring Boot 4 系向けです。Spring Boot 3 系のプロダクトは、
引き続き `master` ブランチからリリースされたライブラリを利用してください。

## シークエンスIDの移行

Spring Boot 4（Hibernate ORM 7）への依存を安定させるため、Hibernate固有の
`@SequenceGenerated` と `CustomSequenceGenerator` は廃止しました。
エンティティでは Jakarta Persistence 標準のアノテーションを使用してください。

移行前:

```java
@Id
@SequenceGenerated
private Long id;
```

移行後（例: `OrderItem`）:

```java
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_item_id_generator")
@SequenceGenerator(
        name = "order_item_id_generator",
        sequenceName = "order_item_id",
        initialValue = 1,
        allocationSize = 1
)
private Long id;
```

必要な import は次のとおりです。

```java
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.SequenceGenerator;
```

### 互換性を保つための規則

- `sequenceName` は、従来 `@SequenceGenerated` で明示していた値をそのまま指定する。
- 従来 `sequenceName` を省略していた場合は、エンティティの単純クラス名を
  snake_case にして `_id` を付ける（例: `OrderItem` → `order_item_id`）。
- `initialValue` は従来の `startWith` と同じ値にする。省略時の互換値は `1`。
- `allocationSize` は従来の `incrementBy` と同じ値にする。省略時の互換値は `1`。
- `generator` と `@SequenceGenerator(name = ...)` には同じ、エンティティ内で一意の
  論理名を指定する。これはDB上のシークエンス名とは別の名前である。

特に `allocationSize` はJPAのデフォルト値が `50` です。指定を省略すると、従来の
`incrementBy = 1` と一致せず、既存DBのシークエンス定義との不整合や採番の飛び方の
変化につながるため、必ず明示してください。

DB側の既存シークエンスについても、`INCREMENT BY` が `allocationSize` と一致すること、
および現在値を維持したまま移行することを確認してください。移行のためにシークエンスを
作り直す必要はありません。

## 既存プロダクトの移行対象

2026年11月1日時点の `content-api` と `vehicle-api` を確認したところ、すべて引数なしの
`@SequenceGenerated` を使用しています。そのため、各エンティティでは次の
`sequenceName`、`initialValue = 1`、`allocationSize = 1` を明示してください。

### content-api

| エンティティ | sequenceName |
| --- | --- |
| `Article` | `article_id` |
| `ArticleCategory` | `article_category_id` |
| `ArticleTag` | `article_tag_id` |
| `Directory` | `directory_id` |
| `LegalDocument` | `legal_document_id` |
| `LegalDocumentAcceptance` | `legal_document_acceptance_id` |
| `LegalDocumentVersion` | `legal_document_version_id` |
| `Office` | `office_id` |
| `OfficeCatalog` | `office_catalog_id` |
| `OfficeCatalogVersion` | `office_catalog_version_id` |
| `OfficeGroup` | `office_group_id` |

### vehicle-api

| エンティティ | sequenceName |
| --- | --- |
| `Announcement` | `announcement_id` |
| `AssetInspectionSchedule` | `asset_inspection_schedule_id` |
| `CarModel` | `car_model_id` |
| `ClientRelease` | `client_release_id` |
| `GarageSite` | `garage_site_id` |
| `LocationEmailRecipientUnresolvedNotificationQueue` | `location_email_recipient_unresolved_notification_queue_id` |
| `MileageRecord` | `mileage_record_id` |
| `Notification` | `notification_id` |
| `NotificationRule` | `notification_rule_id` |
| `Owner` | `owner_id` |
| `TemplateVariable` | `template_variable_id` |
| `User` | `user_id` |

`vehicle-api/docker/mariadb/docker-entrypoint-initdb.d/v1/vehicle_202610010240.sql` では、
上記のうち `garage_site_id` の作成定義が確認できませんでした。既存環境にシークエンスが
存在するか確認し、存在しない場合はSpring Boot 4移行前にDBマイグレーションへ追加して
ください。アプリケーション起動時の自動DDL生成には依存しないでください。

両プロダクトの `pom.xml` は現在、common-utilsのバージョンを
`${project.parent.version}-22` から組み立てています。Boot 4版common-utilsの成果物は
`4.0.9` なので、移行時は `common-utils.version` をBootの親バージョンから切り離し、
利用するcommon-utilsのリリース番号を明示してください。

## Jackson 3への移行

Boot 4版common-utilsはJackson 3を使用します。利用側アプリケーションでは、Jacksonの
databind/coreクラスのimportを `com.fasterxml.jackson.*` から `tools.jackson.*` へ変更して
ください。`@JsonInclude` や `@JsonProperty` などのJacksonアノテーションだけは、Jackson 3
でも引き続き `com.fasterxml.jackson.annotation.*` を使用します。

特に `ApiErrorResponseWriter` のコンストラクタへ渡す型は
`tools.jackson.databind.ObjectMapper` です。また、`BigDecimalIntegerSerializer` は
Jackson 3の `tools.jackson.databind.ValueSerializer` として提供されます。
