package jp.kurashina.commons.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PerPageValidator implements ConstraintValidator<ValidPerPage, Integer> {

    private int minValue;
    private int maxValue;

    /**
     * バリデーターを初期化し、アノテーションの属性値を取得します。
     * このメソッドは、isValidが呼ばれる前に一度だけ呼び出されます。
     */
    @Override
    public void initialize(ValidPerPage constraintAnnotation) {
        this.minValue = constraintAnnotation.min();
        this.maxValue = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // nullは許容
        }

        // 動的に設定されたmin/max値で検証
        return value >= this.minValue && value <= this.maxValue;
    }
}