package jp.kurashina.commons.annotation;

import jakarta.annotation.Nonnull;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.GeneratorCreationContext;
import org.hibernate.id.OptimizableGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.io.Serial;
import java.lang.reflect.Member;
import java.util.Properties;

/**
 * カスタムシーケンスジェネレータ
 * Hibernate 6/7 の @IdGeneratorType で使用されることを想定しています。
 */
public class CustomSequenceGenerator extends SequenceStyleGenerator implements BeforeExecutionGenerator {

    @Serial
    private static final long serialVersionUID = 3504831270855765030L;

    /**
     * Hibernate の @IdGeneratorType によって呼び出されるコンストラクタです。
     * Spring の Bean としてインスタンス化されないよう、引数には Spring Bean ではない型が含まれています。
     */
    public CustomSequenceGenerator(@Nonnull SequenceGenerated config,
                                   @Nonnull Member annotatedMember,
                                   @Nonnull GeneratorCreationContext context) {
        super();
        
        Properties appliedParams = new Properties();
        appliedParams.put(OptimizableGenerator.INITIAL_PARAM, config.startWith());
        appliedParams.put(OptimizableGenerator.INCREMENT_PARAM, config.incrementBy());

        String sequenceName = config.sequenceName();
        if (sequenceName == null || sequenceName.isEmpty()) {
            sequenceName = annotatedMember.getName() + "_seq";
        }
        appliedParams.put(SequenceStyleGenerator.SEQUENCE_PARAM, sequenceName);

        // ServiceRegistry を使用して親クラスを初期化
        this.configure(null, appliedParams, context.getServiceRegistry());
    }

    @Override
    @SuppressWarnings("deprecation")
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) {
        super.configure(type, params, serviceRegistry);
    }

    @Override
    public Object generate(SharedSessionContractImplementor session, Object owner, Object currentValue, EventType eventType) {
        // BeforeExecutionGenerator の実装として、既存の generate メソッドを呼び出す
        return super.generate(session, owner);
    }
}
