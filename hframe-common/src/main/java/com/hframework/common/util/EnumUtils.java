package com.hframework.common.util;

import com.google.common.base.Enums;
import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangquanhong on 2016/5/26.
 */
public class EnumUtils {

    private static final Logger logger = LoggerFactory.getLogger(EnumUtils.class);

    public static <T extends Enum<T>> boolean compare(T enum1, String value) {
        return compare(enum1,value,false);
    }

    public static <T extends Enum<T>> boolean compareIfNullTrue(T enum1, String value) {
        return compare(enum1,value,true);
    }


    public static <T extends Enum<T>> boolean compare(T enum1, String value, boolean ifNullTrue) {
//        logger.debug("request : {}",enum1,value,ifNullTrue);
        if(StringUtils.isBlank(value) && ifNullTrue) {
            return true;
        }
        if(StringUtils.isBlank(value) && !ifNullTrue) {
            return false;
        }
        Optional<? extends Enum> ifPresent = Enums.getIfPresent(enum1.getClass(), value);
        if(ifPresent != null && ifPresent.orNull() == enum1) {
            return true;
        }
        return false;
    }

}
