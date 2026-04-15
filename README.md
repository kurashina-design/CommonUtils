'''
mvn versions:display-dependency-updates
'''

'''
mvn deploy
'''

## common-utils.yaml作成依頼プロンプト

あなたはJava/Springのライブラリ仕様書作成アシスタントです。
目的は、このプロジェクトの共通レスポンスDTOから OpenAPI スキーマ定義ファイル `common-utils.yaml` を生成/更新することです。

【対象】
- パッケージ: `jp.kurashina.commons.resource`
- 対象クラス: response系DTO（abstract含む）
- Java: 21
- フレームワーク: Spring Boot / Spring Data JPA / Lombok / jakarta

【出力ルール（厳守）】
1. 出力は YAML のみ（説明文なし）
2. ルートは `components.schemas` から始める
3. 各 schema に `x-java-fqcn` を必ず付与する
4. Java型→OpenAPI型のマッピングを正確に行う
   - String -> type: string
   - int/Integer -> integer, format: int32（Integerは nullable: true）
   - long/Long -> integer, format: int64（Longは nullable: true）
   - boolean/Boolean -> boolean（Booleanは nullable: true）
   - ZonedDateTime -> string, format: date-time
   - List<T> -> array
   - Map<String, V> -> object + additionalProperties
5. Jackson注釈を反映する
   - `@JsonProperty("per_page")` などのプロパティ名変換を反映
   - `@JsonInclude(NON_EMPTY)` は required を最小化（原則 required を付けない）
6. 継承を反映する
   - 親子関係は `allOf` で表現する
7. ジェネリクスを反映する
   - `ResultResponse<T>` などは少なくとも `ResultResponse_Object` を生成
   - `Pagination<T>` などは少なくとも `Pagination_Object` を生成
8. enum があれば enum 定義を付ける（例: ASC, DESC）
9. 既存の `common-utils.yaml` がある場合は、重複を避けて差分更新する
10. YAML として妥当な構文のみを出力する

【補足方針】
- 例（example）は妥当な最小限を付ける
- abstract class も schema として定義する
- 不明な型は一旦 `type: object` で表現し、`description` に型名を残す

では、現在のコードベースを読み取り、`common-utils.yaml` の最終版を出力してください。