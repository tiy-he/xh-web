package com.xiaohe66.web.base.util;

import com.xiaohe66.web.base.data.CodeEnum;
import com.xiaohe66.web.base.exception.XhException;

import java.util.regex.Pattern;

/**
 *
 * 正则表达式验证
 *
 * @author xh
 * @date 18-10-19 019
 */
public class RegexUtils {

    /**
     * 1-16位中英文数字和下划线组合
     */
    public static final String USR_NAME_REGEX = "^([\\u4e00-\\u9fa5]|[a-z]|[A-Z]|[0-9]|_){1,16}$";

    /**
     * email正则
     */
    public static final String EMAIL_REGEX = "^[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?$";

    public static boolean testUsrName(String value){
        return Pattern.matches(USR_NAME_REGEX,value);
    }

    public static boolean testEmail(String value){
        return Pattern.matches(EMAIL_REGEX,value);
    }

    public static boolean test(String regex,String value){
        if(value == null || regex == null || regex.length() == 0){
            throw new XhException(CodeEnum.NULL_EXCEPTION);
        }
        return Pattern.matches(regex,value);
    }

}
