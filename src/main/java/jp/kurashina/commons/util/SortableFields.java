package jp.kurashina.commons.util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.ManagedType;
import jakarta.persistence.metamodel.SingularAttribute;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JPAメタモデルから、エンティティごとのソート可能なフィールド名を作成します。
 * 継承した属性を含み、コレクション属性を除外します。単一値の関連または埋め込みについては
 * {@code parent.name} のような1階層先までを含めますが、2階層以上は含めません。
 * 結果は変更不可で、エンティティクラスごとにスレッドセーフにキャッシュされます。
 * <p>
 * このクラスはコンポーネントとして自動登録されません。利用側で次のようにBean登録してください。
 * </p>
 * <pre>{@code
 * @Bean
 * public SortableFields sortableFields(EntityManagerFactory entityManagerFactory) {
 *     return new SortableFields(entityManagerFactory);
 * }
 * }</pre>
 */
public class SortableFields {

    private final EntityManagerFactory entityManagerFactory;
    private final Map<Class<?>, Set<String>> cache = new ConcurrentHashMap<>();

    public SortableFields(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    /**
     * 指定したエンティティのソート可能なフィールド名を返します。
     *
     * @param entityClass 対象のエンティティクラス
     * @return 変更不可のソート可能フィールド名セット
     * @throws IllegalArgumentException 指定したクラスがJPAメタモデルに存在しない場合
     */
    public Set<String> of(Class<?> entityClass) {
        return cache.computeIfAbsent(entityClass, this::collect);
    }

    private Set<String> collect(Class<?> entityClass) {
        ManagedType<?> type = entityManagerFactory.getMetamodel().managedType(entityClass);
        Set<String> names = new TreeSet<>();
        for (Attribute<?, ?> attribute : type.getAttributes()) {
            if (attribute.isCollection()) {
                continue;
            }
            names.add(attribute.getName());
            if (attribute instanceof SingularAttribute<?, ?> singular
                    && singular.getType() instanceof ManagedType<?> nested) {
                for (Attribute<?, ?> nestedAttribute : nested.getAttributes()) {
                    if (!nestedAttribute.isCollection()) {
                        names.add(attribute.getName() + "." + nestedAttribute.getName());
                    }
                }
            }
        }
        return Collections.unmodifiableSet(names);
    }
}
