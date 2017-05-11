package com.hframework.common.springext.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhangquanhong on 2016/6/22.
 */
public class DateJsonDeserializer extends JsonDeserializer<Date> {
    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        if(StringUtils.isBlank(jsonParser.getText())) {
            return null;
        }

        try {
            return format.parse(jsonParser.getText());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}