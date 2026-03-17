package jp.kurashina.commons.annotation;

import jakarta.annotation.Nonnull;
import org.hibernate.generator.GeneratorCreationContext; // 変更
import org.hibernate.id.OptimizableGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.io.Serial;
import java.lang.reflect.Member;
import java.util.Properties;

public class CustomSequenceGenerator extends SequenceStyleGenerator {

    @Serial
    private static final long serialVersionUID = 3504831270855765030L;

    private final SequenceGenerated config;

    /**
     * コンストラクタ
     * 第3引数を GeneratorCreationContext に変更し、その中で初期化を完結させます。
     */
    public CustomSequenceGenerator(@Nonnull SequenceGenerated config,
                                   @Nonnull Member annotatedMember,
                                   @Nonnull GeneratorCreationContext context) {
        super(); // Hibernate 7 の SequenceStyleGenerator は引数なし super() を使用
        this.config = config;

        // 以前 configure で行っていたパラメータ設定をここで行う
        Properties appliedParams = new Properties();

        // デフォルト設定を注入（必要に応じて context から取得した既存設定をマージ）
        appliedParams.put(OptimizableGenerator.INITIAL_PARAM, config.startWith());
        appliedParams.put(OptimizableGenerator.INCREMENT_PARAM, config.incrementBy());

        // シーケンス名の決定
        // Hibernate 7 では TABLE 定数などが整理されているため、明示的に取得ロジックを確認
        String sequenceName = config.sequenceName();
        if (sequenceName == null || sequenceName.isEmpty()) {
            // annotatedMember (フィールド名) 等から動的に決めるのが安全です
            sequenceName = annotatedMember.getName() + "_seq";
        }
        appliedParams.put(SequenceStyleGenerator.SEQUENCE_PARAM, sequenceName);

        // コンストラクタから内部的な初期化メソッドを呼び出す
        this.configure(null, appliedParams, context.getServiceRegistry());
    }

    /**
     * Hibernate 7 では configure は非推奨ですが、
     * 独自のプロパティを差し込む現時点での現実的な回避策としてオーバーライドし、警告を抑制します。
     */
    @Override
    @SuppressWarnings("deprecation")
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) {
        super.configure(type, params, serviceRegistry);
    }
}