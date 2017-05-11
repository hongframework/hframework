package com.hframework.common.client.ftp;

/**
 * Created by zhangquanhong on 2016/5/10.
 */

import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.parser.ConfigurableFTPFileEntryParserImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * <pre>
 * 解析IBM财务FTP服务器返回的一行信息
 * -rw-rw-rw-    1 chnnlftp nobody          145  6月22 16时56 finance_back_info_20100617150652.csv
 * 取得文件名，文件时间，文件类型，文件大小，文件所属用户。
 *
 * 本程序不具有复用性！！
 * </pre>
 */
public class FTPEntry4CnEnvParser extends ConfigurableFTPFileEntryParserImpl {

    private Class clazz = FTPEntry4CnEnvParser.class;
//    private Log log = LogFactory.getLog(clazz);

    /**
     * 解析FTP传回的文件信息
     */
    public FTPFile parseFTPEntry(String entry) {
//        log.debug("开始解析，内容为: " + entry);

        FTPFile file = new FTPFile();
        file.setRawListing(entry);

        String[] temp = entry.split("\\s+");
        if (temp.length != 8) {
            return null;
        }
        String fileType = temp[0].substring(0, 1);
        if ("d".equals(fileType)) {
            file.setType(FTPFile.DIRECTORY_TYPE);
        } else {
            file.setType(FTPFile.FILE_TYPE);
            file.setSize(Integer.valueOf(temp[4]));
        }
        file.setName(temp[7]);
        file.setUser(temp[3]);

        Calendar date = Calendar.getInstance();
        Date fileDate;
        // 返回【6月22 2010】形式的日期
        if(temp[6].matches("\\d{4}")){
            try {
                fileDate = new SimpleDateFormat("yyyyMM月dd")
                        .parse(temp[6] + temp[5]);
            } catch (ParseException e) {
                throw new RuntimeException("日期解析出错", e);
            }
            // 返回【6月22 16时56】形式的日期
        } else {
            int yyyy = date.get(Calendar.YEAR);
            try {
                fileDate = new SimpleDateFormat("yyyyMM月ddHH时mm")
                        .parse(yyyy + temp[5] + temp[6]);
            } catch (ParseException e) {
                throw new RuntimeException("日期解析出错", e);
            }
        }
        date.setTime(fileDate);
        file.setTimestamp(date);

        return file;
    }

    // =====================================================================
    // 本类只是特定解析一种FTP，没有考虑到使用正则表达式，匹配解析一类FTP
    // 核心方法为parseFTPEntry，以下方法没有实现。
    // =====================================================================
    public FTPEntry4CnEnvParser() {
        this("");
    }

    public FTPEntry4CnEnvParser(String regex) {
        super("");
    }

    protected FTPClientConfig getDefaultConfiguration() {
        return new FTPClientConfig(clazz.getPackage().getName()
                + clazz.getSimpleName(), "", "", "", "", "");
    }
}