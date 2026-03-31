package jp.kurashina.commons.annotation;

import jakarta.annotation.Nonnull;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.hibernate.type.spi.TypeConfiguration;

import java.io.Serial;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Properties;

public class CustomSequenceGenerator extends SequenceStyleGenerator {

    @Serial
    private static final long serialVersionUID = 3504831270855765030L;

    public CustomSequenceGenerator(@Nonnull SequenceGenerated config,
                                   @Nonnull Member annotatedMember,
                                   @Nonnull CustomIdGeneratorCreationContext context) {

        // 1. ServiceRegistryの取得
        ServiceRegistry serviceRegistry = context.getServiceRegistry();

        // 2. TypeConfigurationの取得 (Hibernate 6.6での安全なルート)
        // context 自体が内部で TypeConfiguration を持っているため、リフレクション等を使わず
        // context のメソッドから直接、または TypeConfiguration のスタティックな解決を試みます。
        // ここでは、最も確実な「型レジストリからの直接解決」を行います。
        TypeConfiguration typeConfiguration = new TypeConfiguration();

        Class<?> memberType = getMemberType(annotatedMember);
        // Javaの型(Long等)からHibernateのTypeに変換
        Type hibernateType = typeConfiguration.getBasicTypeRegistry().getRegisteredType(memberType);

        // 3. パラメータの構築（今まで通りのロジック）
        Properties params = new Properties();
        params.put(INITIAL_PARAM, String.valueOf(config.startWith()));
        params.put(INCREMENT_PARAM, String.valueOf(config.incrementBy()));

        // 4. シークエンス名の決定ロジック（今まで通り）
        String sequenceName = config.sequenceName();
        if (sequenceName == null || sequenceName.isEmpty()) {
            // クラス名から推測（3.5.13では物理テーブル名取得がコンストラクタ時点では提供されないため）
            String entityName = annotatedMember.getDeclaringClass().getSimpleName();
            sequenceName = entityName.toLowerCase() + "_id";
        }
        params.put(SEQUENCE_PARAM, sequenceName);

        // 5. 親クラスの初期化
        // 3.5.3での configure(Type, Properties, ServiceRegistry) と同等の処理
        super.configure(hibernateType, params, serviceRegistry);
    }

    private Class<?> getMemberType(Member member) {
        if (member instanceof Field f) {
            return f.getType();
        } else if (member instanceof Method m) {
            return m.getReturnType();
        }
        throw new IllegalArgumentException("Unsupported member type: " + member.getClass());
    }

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) {
        // コンストラクタで完結させるため、ここは空にする
    }
}