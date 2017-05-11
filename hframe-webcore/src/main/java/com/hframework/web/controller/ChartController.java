package com.hframework.web.controller;

import com.google.common.collect.Lists;
import com.hframework.beans.controller.ResultCode;
import com.hframework.beans.controller.ResultData;
import com.hframework.common.client.hbase.HBaseClient;
import com.hframework.common.springext.datasource.DataSourceContextHolder;
import com.hframework.common.util.DateUtils;
import com.hframework.web.auth.AuthServiceProxy;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * User: zhangqh6
 * Date: 2016/5/11 0:16:16
 */
@Controller
public class ChartController {
    private static final Logger logger = LoggerFactory.getLogger(ChartController.class);

    @Resource
    private AuthServiceProxy authServiceProxy;



    /**
     * 页面跳转
     * @return
     * @throws Throwable
     */
    @RequestMapping(value = "/chart/data.json")
    @ResponseBody
    public ResultData date(String[] dataCodes,Integer step, String cycle ,
                                    Long xAxisStart, Long xAxisEnd, String operateType) throws Throwable {
        logger.debug("request : {}|{}|{}|{}|{}|{}", dataCodes, step, cycle, xAxisStart, xAxisEnd, operateType);
        try{

            Date nextEndDate = null;
            Date nextBeginDate = null;
            if("init".equals(operateType) || "refresh".equals(operateType) ||  "switch".equals(operateType)) {
                nextEndDate = new Date();
                nextBeginDate = calcBeginDate(nextEndDate, cycle, step);
            }else if("prev".equals(operateType)){
                nextEndDate =  new Date(xAxisStart * 1000 - 1);
                nextBeginDate = calcBeginDate(nextEndDate, cycle, step);
            }else if("next".equals(operateType)){
                nextBeginDate = new Date(xAxisEnd * 1000 + 1);
                nextEndDate = calcBeginDate(nextBeginDate, cycle, -step);
            }else {
                nextEndDate = new Date();
                nextBeginDate = calcBeginDate(nextEndDate, cycle, step);
            }

            String[] nextDateArrays  = calcDateArrays(nextBeginDate, nextEndDate, cycle);
            final List data = new ArrayList();
//            //DEMO
//            for (String dataCode : dataCodes) {
//                data.add(queryDataList(dataCode, nextDateArrays, "init".equals(operateType)));
//            }

            //DB
            String[] timeTags  = calcTimeTags(nextBeginDate, nextEndDate, cycle);
            for (String dataCode : dataCodes) {
                Double[] values = queryDataList(timeTags, dataCode, nextBeginDate, nextEndDate, nextDateArrays.length, cycle);
                data.add(queryDataList(dataCode, nextDateArrays, values, "init".equals(operateType)));
            }



            final Date finalNextBeginDate = nextBeginDate;
            final Date finalNextEndDate = nextEndDate;
            return ResultData.success(new HashMap<String,Object>(){{
                put("series", data);
                put("xAxisStart", finalNextBeginDate.getTime()/1000);
                put("xAxisEnd", finalNextEndDate.getTime()/1000);
            }});
        }catch (Exception e) {
            logger.error("error : ", e);
            return ResultData.error(ResultCode.ERROR);
        }finally {
            DataSourceContextHolder.clear();
        }
    }

    private Object queryDataList(final String dataCode, final String[] nextDateArrays, final Double[] nextDatas, boolean typeNeed) {
        final List data = new ArrayList();
        for (int i = 0; i < nextDateArrays.length; i++) {
            final int finalI = i;
            data.add(new HashMap(){{
                put("name", nextDateArrays[finalI]);
                put("value", new Object[]{nextDateArrays[finalI], nextDatas[finalI]});
            }});
        }

        HashMap result = new HashMap() {{
            put("name", dataCode);
            put("data", data);
        }};
        if(typeNeed) result.put("type", "line");
        return result;
    }

    private Double[] queryDataList(String[] timeTags, String dataCode, Date nextBeginDate, Date nextEndDate, int length, String cycle) throws Exception {
            Double[] values = new Double[length];
            Arrays.fill(values, 0d);

            String startRowKey = dataCode + "_" + formatDate(nextBeginDate, cycle, false);
            String endRowKey = dataCode + "_" + formatDate(nextEndDate, cycle, true);

            HBaseClient hBaseClient = HBaseClient.getInstance(null, null);
            String tableName = "manis_statistics_" + cycle;
            if("day".equals(cycle)) {
                tableName = "manis_statistics_date";
            }else if("year".equals(cycle)) {
                tableName = "manis_statistics_month";
            }else if("week".equals(cycle)) {
                tableName = "manis_statistics_date";
            }

        ResultScanner results = hBaseClient.scan(tableName, startRowKey, endRowKey);
            for (Result result : results) {
                String rowKey = new String(result.getRow());
//                System.out.println("================> " + rowKey);
                NavigableMap<byte[], byte[]> items = result.getFamilyMap(Bytes.toBytes("items"));
                if("year".equals(cycle)) {
                    BigDecimal sum = BigDecimal.ZERO;
                    for (byte[] bytes : items.keySet()) {
                        sum = sum.add(new BigDecimal(new String(items.get(bytes))));
                    }
                    int index = calcOffset(timeTags, (rowKey).substring((dataCode + "_").length()));
                    if(index >= 0){
                        if(values[index] != 0d) {
                            values[index] = sum.add(BigDecimal.valueOf(values[index])).doubleValue();
                        }else {
                            values[index] = sum.doubleValue();
                        }
                    }

                }
                for (byte[] bytes : items.keySet()) {
                    String tmpKey = new String(bytes);
                    Double tmpValue = Double.parseDouble(new String(items.get(bytes)));
                    int index = calcOffset(timeTags, (rowKey + tmpKey).substring((dataCode + "_").length()));
                    if(index < 0) {
                        index = -index - 2;
                    }
                    if(index < 0) {
                        logger.warn("index less than 0, timeTags = {}, searchTime = {}", timeTags,  (rowKey + tmpKey).substring((dataCode + "_").length()));
                    }else if(values[index] != 0d) {
                        values[index] = BigDecimal.valueOf(tmpValue).add(BigDecimal.valueOf(values[index])).doubleValue();
                    }else {
                        values[index] = tmpValue;
                    }
                }
            }
        return values;
    }

    private String[] calcTimeTags(Date beginDate, Date endDate, String cycle) {
        List<String> times = new ArrayList<String>();

        Calendar cursorCalender = Calendar.getInstance();
        cursorCalender.setTime(beginDate);

        while (!cursorCalender.getTime().after(endDate)) {
            if("minute".equals(cycle)) {
                times.add(DateUtils.getDate(cursorCalender.getTime(),"yyyyMMddHHmm"));
                cursorCalender.add(Calendar.MINUTE, 1);
            }else if("hour".equals(cycle)) {
                times.add(DateUtils.getDate(cursorCalender.getTime(),"yyyyMMddHH"));
                cursorCalender.add(Calendar.HOUR, 1);
            }else if("day".equals(cycle)) {
                times.add(DateUtils.getDate(cursorCalender.getTime(),"yyyyMMdd"));
                cursorCalender.add(Calendar.DATE, 1);
            }else if("week".equals(cycle)) {
                times.add(DateUtils.getDate(cursorCalender.getTime(),"yyyyMMdd"));
                cursorCalender.add(Calendar.DATE, 1 * 7);
            }else if("month".equals(cycle)) {
                times.add(DateUtils.getDate(cursorCalender.getTime(),"yyyyMM"));
                cursorCalender.add(Calendar.MONTH, 1);
            }else if("year".equals(cycle)) {
                times.add(DateUtils.getDate(cursorCalender.getTime(),"yyyy"));
                cursorCalender.add(Calendar.YEAR, 1);
            }else {
                times.add(DateUtils.getDate(cursorCalender.getTime(),"yyyyMMddHHmmss"));
                cursorCalender.add(Calendar.SECOND, 1);
            }

        }

        return times.toArray(new String[0]);
    }

    private int calcOffset(String[] timeTags,  String timeTag) {

        int index = Arrays.binarySearch(timeTags,timeTag);
        if(index >= 0 ) {
            return index;
        }

        return index;
    }

    private String formatDate(Date date, String cycle, boolean ceil) {
        Calendar cursorCalender = Calendar.getInstance();
        cursorCalender.setTime(date);
        if("minute".equals(cycle)) {
            if(ceil) cursorCalender.add(Calendar.HOUR, 1);
            return DateUtils.getDate(cursorCalender.getTime(), "yyyyMMddHH");
        }else if("hour".equals(cycle)) {
            if(ceil) cursorCalender.add(Calendar.DATE, 1);
            return DateUtils.getDate(cursorCalender.getTime(), "yyyyMMdd");
        }else if("day".equals(cycle)) {
            if(ceil) cursorCalender.add(Calendar.MONTH, 1);
            return DateUtils.getDate(cursorCalender.getTime(), "yyyyMM");
        }else if("week".equals(cycle)) {
            if(ceil) cursorCalender.add(Calendar.MONTH, 1);
            return DateUtils.getDate(cursorCalender.getTime(), "yyyyMM");
        }else if("month".equals(cycle)) {
            if(ceil) cursorCalender.add(Calendar.YEAR, 1);
            return DateUtils.getDate(cursorCalender.getTime(), "yyyy");
        }else if("year".equals(cycle)) {
            if(ceil) cursorCalender.add(Calendar.YEAR, 1);
            return DateUtils.getDate(cursorCalender.getTime(), "yyyy");
        }else {
            if(ceil) cursorCalender.add(Calendar.MINUTE, 1);
            return DateUtils.getDate(cursorCalender.getTime(), "yyyyMMddHHmm");
        }
    }

    private Object queryDataList(final String dataCode, String[] nextDateArrays, boolean typeNeed) {

        final List data = new ArrayList();
        String startRowKey = dataCode + "_"  + nextDateArrays[0];
        String enRowKey = dataCode + "_"  + nextDateArrays[nextDateArrays.length - 1];
        for (final String nextDateArray : nextDateArrays) {

            data.add(new HashMap(){{
                put("name", nextDateArray);
                put("value", new Object[]{nextDateArray, Math.floor(Math.random() * 100)});
            }});
        }

        HashMap result = new HashMap() {{
            put("name", dataCode);
            put("data", data);
        }};
        if(typeNeed) result.put("type", "line");
        return result;
    }

    private String[] calcDateArrays(Date beginDate, Date endDate, String cycle) {
        List<String> times = new ArrayList<String>();

        Calendar cursorCalender = Calendar.getInstance();
        cursorCalender.setTime(beginDate);

        while (!cursorCalender.getTime().after(endDate)) {
            if("minute".equals(cycle)) {
                times.add(DateUtils.getDate(cursorCalender.getTime(),"yyyy-MM-dd HH:mm"));
                cursorCalender.add(Calendar.MINUTE, 1);
            }else if("hour".equals(cycle)) {
                times.add(DateUtils.getDate(cursorCalender.getTime(),"yyyy-MM-dd HH:mm"));
                cursorCalender.add(Calendar.HOUR, 1);
            }else if("day".equals(cycle)) {
                times.add(DateUtils.getDate(cursorCalender.getTime(),"yyyy-MM-dd"));
                cursorCalender.add(Calendar.DATE, 1);
            }else if("week".equals(cycle)) {
                times.add(DateUtils.getDate(cursorCalender.getTime(),"yyyy-MM-dd"));
                cursorCalender.add(Calendar.DATE, 1 * 7);
            }else if("month".equals(cycle)) {
                times.add(DateUtils.getDate(cursorCalender.getTime(),"yyyy-MM"));
                cursorCalender.add(Calendar.MONTH, 1);
            }else if("year".equals(cycle)) {
                times.add(DateUtils.getDate(cursorCalender.getTime(),"yyyy-MM-dd HH:mm:ss"));
                cursorCalender.add(Calendar.YEAR, 1);
            }else {
                times.add(DateUtils.getDate(cursorCalender.getTime(),"yyyy-MM-dd HH:mm:ss"));
                cursorCalender.add(Calendar.SECOND, 1);
            }

        }

        return times.toArray(new String[0]);
    }

    private Date calcBeginDate(Date nextEndDate, String cycle, int step) {
        Calendar endCalender = Calendar.getInstance();
        endCalender.setTime(nextEndDate);
        if("minute".equals(cycle)) {
            endCalender.add(Calendar.MINUTE, -step);
        }else if("hour".equals(cycle)) {
            endCalender.add(Calendar.HOUR, -step);
        }else if("day".equals(cycle)) {
            endCalender.add(Calendar.DATE, -step);
        }else if("week".equals(cycle)) {
            endCalender.add(Calendar.DATE, -step * 7);
        }else if("month".equals(cycle)) {
            endCalender.add(Calendar.MONTH, -step);
        }else if("year".equals(cycle)) {
            endCalender.add(Calendar.YEAR, -step);
        }else {
            endCalender.add(Calendar.SECOND, -step);
        }
        return endCalender.getTime();
    }

    public static void main(String[] args) {
        List list = Lists.newArrayList("20170323141612","20170323141613","20170323141618","20170323151612","20170323151712");
        System.out.println(Arrays.binarySearch(list.toArray(new String[0]),"20170323141619"));
    }
}
