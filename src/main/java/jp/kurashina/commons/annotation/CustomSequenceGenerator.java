package jp.kurashina.commons.annotation;

import org.hibernate.generator.AnnotationBasedGenerator;
import org.hibernate.generator.GeneratorCreationContext;
import org.hibernate.id.OptimizableGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import java.io.Serial;
import java.lang.reflect.Member;
import java.util.Properties;

/**
 * カスタムシーケンスジェネレータ
 * Hibernate 6/7 の @IdGeneratorType で使用されることを想定しています。
 */
public class CustomSequenceGenerator extends SequenceStyleGenerator
        implements AnnotationBasedGenerator<SequenceGenerated> {

    @Serial
    private static final long serialVersionUID = -5417410472072468910L;
    private Properties parameters = new Properties();

    /**
     * Hibernate 6.6 では @IdGeneratorType の設定値は initialize() で受け取る。
     */
    @Override
    public void initialize(SequenceGenerated config,
                           Member annotatedMember,
                           GeneratorCreationContext context) {
        Properties appliedParams = new Properties();
        appliedParams.put(OptimizableGenerator.INITIAL_PARAM, Integer.toString(config.startWith()));
        appliedParams.put(OptimizableGenerator.INCREMENT_PARAM, Integer.toString(config.incrementBy()));

        String sequenceName = config.sequenceName();
        if (sequenceName == null || sequenceName.isBlank()) {
            // テーブル名が取得しづらいため、エンティティのクラス名を小文字にしたものをベースにするか、
            // あるいは以前のロジックを模倣して、明示的な指定がない場合は Hibernate のデフォルトに任せず
            // 独自の命名規則を適用します。
            // ここではクラス名をベースに "_id" を付与します。
            String className = annotatedMember.getDeclaringClass().getSimpleName().toLowerCase();
            sequenceName = className + "_id";
        }

        appliedParams.put(SequenceStyleGenerator.SEQUENCE_PARAM, sequenceName);
        this.parameters = appliedParams;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void create(GeneratorCreationContext context) {
        // create 時点での context から型情報を引き継ぐ
        super.configure(context.getProperty().getType(), parameters, context.getServiceRegistry());
        super.create(context);
    }
}
