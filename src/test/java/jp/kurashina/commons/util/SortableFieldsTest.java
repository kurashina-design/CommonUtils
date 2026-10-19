package jp.kurashina.commons.util;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jp.kurashina.commons.exception.BadRequestException;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SortableFieldsTest {

    private static StandardServiceRegistry registry;
    private static EntityManagerFactory entityManagerFactory;
    private static SortableFields sortableFields;

    @BeforeAll
    static void setUp() {
        registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.dialect", "org.hibernate.dialect.MariaDBDialect")
                .applySetting("hibernate.boot.allow_jdbc_metadata_access", "false")
                .applySetting("hibernate.temp.use_jdbc_metadata_defaults", "false")
                .build();
        MetadataSources sources = new MetadataSources(registry);
        sources.addAnnotatedClass(Child.class);
        sources.addAnnotatedClass(Parent.class);
        sources.addAnnotatedClass(Grand.class);
        sources.addAnnotatedClass(Item.class);
        sources.addAnnotatedClass(EmbeddedValue.class);
        entityManagerFactory = sources.buildMetadata().buildSessionFactory();
        sortableFields = new SortableFields(entityManagerFactory);
    }

    @AfterAll
    static void tearDown() {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    @Test
    void includesInheritedAttributes() {
        assertThat(sortableFields.of(Child.class)).contains("id", "createdDate");
    }

    @Test
    void includesBasicAttributes() {
        assertThat(sortableFields.of(Child.class)).contains("name");
    }

    @Test
    void includesOneLevelAssociationAndEmbeddedAttributes() {
        assertThat(sortableFields.of(Child.class)).contains("parent.code", "embedded.value");
    }

    @Test
    void excludesPathsDeeperThanOneLevel() {
        assertThat(sortableFields.of(Child.class))
                .contains("parent.grand")
                .doesNotContain("parent.grand.value");
    }

    @Test
    void excludesCollectionAttributes() {
        assertThat(sortableFields.of(Child.class))
                .doesNotContain("items")
                .noneMatch(field -> field.startsWith("items."));
    }

    @Test
    void returnsUnmodifiableSet() {
        Set<String> fields = sortableFields.of(Child.class);

        assertThatThrownBy(() -> fields.add("anotherField"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void cachesFieldsByEntityClass() {
        assertThat(sortableFields.of(Child.class)).isSameAs(sortableFields.of(Child.class));
    }

    @Test
    void rejectsClassOutsideMetamodel() {
        assertThatThrownBy(() -> sortableFields.of(String.class))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void worksWithSortValidation() {
        Set<String> fields = sortableFields.of(Child.class);

        assertThatCode(() -> SortUtils.validateSortKeys(
                List.of("id", "-createdDate", "parent.code"), fields))
                .doesNotThrowAnyException();
        assertThatThrownBy(() -> SortUtils.validateSortKeys(List.of("bogus"), fields))
                .isInstanceOf(BadRequestException.class);
        assertThatThrownBy(() -> SortUtils.validateSortKeys(List.of("items"), fields))
                .isInstanceOf(BadRequestException.class);
    }

    @MappedSuperclass
    static abstract class BaseEntity {

        @Id
        private Long id;

        private LocalDateTime createdDate;
    }

    @Entity(name = "SortableFieldsChild")
    @Table(name = "sortable_fields_child")
    static class Child extends BaseEntity {

        private String name;

        @ManyToOne
        private Parent parent;

        @OneToMany(mappedBy = "child")
        private List<Item> items = new ArrayList<>();

        @Embedded
        private EmbeddedValue embedded;
    }

    @Entity(name = "SortableFieldsParent")
    @Table(name = "sortable_fields_parent")
    static class Parent {

        @Id
        private Long id;

        private String code;

        @ManyToOne
        private Grand grand;
    }

    @Entity(name = "SortableFieldsGrand")
    @Table(name = "sortable_fields_grand")
    static class Grand {

        @Id
        private Long id;

        private String value;
    }

    @Entity(name = "SortableFieldsItem")
    @Table(name = "sortable_fields_item")
    static class Item {

        @Id
        private Long id;

        @ManyToOne
        private Child child;

        private String value;
    }

    @Embeddable
    static class EmbeddedValue {

        private String value;
    }
}
