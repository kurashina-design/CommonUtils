# AGENTS.md

## 引き継ぎ（最初に読むこと）

この repository は、会話履歴やローカルの未 commit ファイルを持たない別のエージェント
（Codex など）がすぐに作業を引き継げる状態を保つ。

- 作業を始める前に `aidocs/handoff.md` の「いまの状況」を読む。
- セッションを終える前に「いまの状況」（現状、次にやることの順番、利用者の判断待ち、
  守るルール、関連リポジトリ）を更新する。
- 作業ログには、変更したこと、確認したこと、確認していないことを追記する。
- 引き継ぎ文書は終了前に commit・push する。ローカルにだけ残さない。
- 秘密情報や個人の連絡先を引き継ぎ文書に書かない。

## 参考文書

- `aidocs/project-overview.md`: プロジェクトの俯瞰
- `aidocs/api-error-components.md`: API エラーレスポンス共通部品
- `aidocs/spring-boot-4-migration.md`: Spring Boot 4 移行ガイド（`boot4` ブランチ）
