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
    private static final long serialVersionUID = -2045947366157883421L;

    public CustomSequenceGenerator(@Nonnull SequenceGenerated config,
                                   @Nonnull Member annotatedMember,
                                   @Nonnull CustomIdGeneratorCreationContext context) {

        ServiceRegistry serviceRegistry = context.getServiceRegistry();

        // 1. 型の解決 (Hibernate 6.6での標準的な取得)
        TypeConfiguration typeConfiguration = new TypeConfiguration();
        Class<?> memberType = getMemberType(annotatedMember);
        Type hibernateType = typeConfiguration.getBasicTypeRegistry().getRegisteredType(memberType);

        // 2. パラメータの構築
        Properties params = new Properties();

        // 数値設定
        params.put(INITIAL_PARAM, String.valueOf(config.startWith()));
        params.put(INCREMENT_PARAM, String.valueOf(config.incrementBy()));

        // 3. シークエンス名の決定ロジック
        String sequenceName = config.sequenceName();
        if (sequenceName == null || sequenceName.isEmpty()) {
            String entityName = annotatedMember.getDeclaringClass().getSimpleName();
            sequenceName = toSnakeCase(entityName) + "_id";
        }

        // 4. Hibernate 6.x で命名戦略を上書きするための設定
        params.put(SEQUENCE_PARAM, sequenceName);

        // 5. 親クラスの初期化
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

    private String toSnakeCase(String value) {
        return value
                .replaceAll("([a-z0-9])([A-Z])", "$1_$2")
                .replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2")
                .toLowerCase();
    }

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) {
        // コンストラクタで super.configure を実行済みなので、
        // Hibernate本体からの後続の呼び出しで設定がデフォルト(テーブル名_seq)に
        // 戻されないよう、すでに初期化済みの場合は何もしないようにします。
        if (getDatabaseStructure() == null) {
            super.configure(type, params, serviceRegistry);
        }
    }
}