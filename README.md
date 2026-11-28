# common-utils

Spring Boot で作る社内 API が共通で使う Java ライブラリです。

> このプロジェクトは作り直す予定です。現在は保守のみを行っています。

## ブランチと対応バージョン

| ブランチ | Spring Boot | 成果物のバージョン |
| --- | --- | --- |
| `master` | 3.5 系 | `3.5.13-22` |
| `boot4` | 4.0 系（Jackson 3） | `4.0.9` |

Java 21 が必要です。成果物は GitHub Packages で配布しています。

## 主な機能

- **共通レスポンス DTO**（`resource`）: `Pagination<T>`、`ResultResponse<T>` など
- **ページングとソート**: `@QuerySort`、`SortableFields`、`PaginationHelper`
- **レスポンス項目の指定**: `@QueryFields`（例: `fields=id,children[id,name]`）
- **API エラーレスポンス**: `ApiErrorResponseFactory`、`ApiErrorResponseWriter`、`RequestIdFilter`
- **ユーティリティ**: 文字列、日時（和暦を含む）、Bean コピー、法人名の正規化など

## 使い方の例

### ソート

`sort` パラメータは、カンマ区切りでも同じパラメータの繰り返しでも指定できます。先頭に `-` を付けると降順になります。

```text
sort=id,-createdDate
sort=id&sort=-createdDate
```

ソートできる項目は、JPA メタモデルから作った許可リストで検証します。

```java
@Bean
public SortableFields sortableFields(EntityManagerFactory entityManagerFactory) {
    return new SortableFields(entityManagerFactory);
}
```

```java
@GetMapping("/page")
public Pagination<AssetResponse> page(..., @QuerySort(defaultValue = "id") List<String> sortList) {
    Pageable pageable = paginationHelper.getPageable(perPage, page, sortList, sortableFields.of(Asset.class));
    ...
}
```

引数リゾルバーとフィルターは自動では有効になりません。利用側のアプリケーションで Bean として登録してください。

## ビルドと公開

```bash
mvn package
```

```bash
mvn deploy
```

`mvn deploy` には GitHub Packages の認証設定が必要です。

## ドキュメント

- [aidocs/project-overview.md](aidocs/project-overview.md): パッケージ構成と設計方針
- [aidocs/api-error-components.md](aidocs/api-error-components.md): API エラーレスポンス部品
- [aidocs/spring-boot-4-migration.md](aidocs/spring-boot-4-migration.md): Spring Boot 4 への移行（`boot4` ブランチ）
