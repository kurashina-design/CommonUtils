package jp.kurashina.commons.annotation;

import jakarta.annotation.Nonnull;
import org.hibernate.id.OptimizableGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.io.Serial;
import java.lang.reflect.Member;
import java.util.Properties;

public class CustomSequenceGenerator extends SequenceStyleGenerator {

    @Serial
    private static final long serialVersionUID = 3504831270855765030L;

    private final SequenceGenerated config;

    public CustomSequenceGenerator(@Nonnull SequenceGenerated config, @Nonnull Member annotatedMember,
                                   @Nonnull CustomIdGeneratorCreationContext context) {
        this.config = config;
    }

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) {
        Properties appliedParams = new Properties();
        appliedParams.putAll(params);
        appliedParams.put(OptimizableGenerator.INITIAL_PARAM, config.startWith());
        appliedParams.put(OptimizableGenerator.INCREMENT_PARAM, config.incrementBy());
        String tableName = params.getProperty(SequenceStyleGenerator.TABLE);
        String sequenceName = config.sequenceName();
        if (sequenceName == null || sequenceName.isEmpty()) {
            sequenceName = tableName + "_id";
        }
        appliedParams.put(SequenceStyleGenerator.SEQUENCE_PARAM, sequenceName);
        super.configure(type, appliedParams, serviceRegistry);
    }

}