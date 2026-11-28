# 引き継ぎ

## いまの状況

最初にこの節を読むこと。セッション終了前に必ず更新し、commit・push する。

### 現状（2026-11-28 時点）

- リリース系統は2本ある。
  - `master`: Spring Boot 3.5.13 系。成果物は `3.5.13-22`。Boot 3 のプロダクト向け。
  - `boot4`: Spring Boot 4.0.9 / Jackson 3 系。成果物は `4.0.9`。`origin/boot4` と同期済み。
- `boot4` では `@SequenceGenerated` / `CustomSequenceGenerator` を廃止し、Jakarta 標準の
  `@SequenceGenerator` へ移行した。詳細と利用側の移行表は `aidocs/spring-boot-4-migration.md` にある。
- コード変更中の作業はない。直近のセッション（2026-11-24〜28）はプロジェクトの俯瞰だけを行った。

### 次にやること（順番）

1. 利用者が判断するまで保留: `aidocs/project-overview.md` の古い記述を直す。
   - 「`src/test` は無い」は誤り（現在は複数のテストクラスがある）。
   - すでに削除された `common-utils.yaml` を前提にしている。
   - `api.error`、`web`（RequestId）、`SortableFields` の記述が無い。
   - `master` 側は version が `-17` のまま（実際は `-22`）。
2. `README.md` の全面書き直しは `boot4` の作業ツリーで済んでいるが、利用者の確認待ちで未 commit。
   公開 repository の表示に使われる `master` にも反映するか決める。
3. テストが無い領域（`QueryFieldsArgumentResolver`、`BeanUtils`、`StringUtils`、
   `DateTimeUtils`、`CorporateNameNormalizer`）にテストを足す。

### 利用者の判断待ち

- 上の 1. の project-overview.md 更新は、2026-11-24 に利用者が「今はいい」と見送った。
- README.md 書き直しの commit・push と、`master` への反映。
- `content-api` / `vehicle-api` の Boot 4 移行時期（移行手順は migration ガイドにある）。

### 守るルール

- 公開クラス名・メソッド名・JSON 名は、綴りの揺れ（`enumuration`、`TokenTimoutException` など）
  があっても利用側の確認なしに変えない。
- Boot 3 向けの修正は `master`、Boot 4 向けは `boot4` に入れる。両方に必要なら両方へ入れる。
- エラー処理の `@RestControllerAdvice` や例外と HTTP status の対応付けは利用側 API に置く
  （`aidocs/api-error-components.md`）。
- 秘密情報や個人の連絡先をこの文書に書かない。

### 関連リポジトリ

- `content-api`、`vehicle-api`: このライブラリの利用側。
- 配布先: GitHub Packages `kurashina-design/CommonUtils`。

## 作業ログ

### 2026-11-24〜28

- 変更: コードの変更なし。`README.md` を全面的に書き直した（未 commit。古い OpenAPI 生成用プロンプトは削除）。この引き継ぎ文書を新規作成し、`AGENTS.md` に引き継ぎルールを追記した。
- 確認したこと: `master` と `boot4` のパッケージ構成、`pom.xml`、`aidocs/` の各文書、直近のコミット履歴。
- 確認していないこと: `mvn test` / `mvn package` は実行していない。
