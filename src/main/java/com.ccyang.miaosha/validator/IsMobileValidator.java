package com.ccyang.miaosha.validator;

import com.ccyang.miaosha.util.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * realize @IsMobile annotation handle logic 实现 @IsMoblie在注解的处理逻辑
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    private boolean required = false;

    @Override
    public void initialize(IsMobile isMobile) {
        required = isMobile.required();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {

        if(required){
            return ValidatorUtil.isMobile(s);

        }else{   // 可以为空
            if(StringUtils.isEmpty(s)){
                return true;
            }else{
                return ValidatorUtil.isMobile(s);
            }

        }
    }
}
