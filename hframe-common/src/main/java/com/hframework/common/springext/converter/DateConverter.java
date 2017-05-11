package com.hframework.common.springext.converter;

import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhangquanhong on 2016/6/23.
 */
public class DateConverter implements Converter<String, Date> {
    private String format = "yyyy-MM-dd HH:mm:ss";

    public DateConverter() {
        this.format = format;
    }
    public DateConverter(String format) {
        this.format = format;
    }

    public Date convert(String source) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setLenient(false);
        try {
            return dateFormat.parse(source);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
