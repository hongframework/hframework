package com.hframework.common.client.hbase;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangquanhong on 2017/3/16.
 */
public class HBaseClient {
    private static final Logger logger = LoggerFactory.getLogger(HBaseClient.class);


    private static Map<String, HBaseClient> hBaseClients = new HashMap<String, HBaseClient>();

    private Configuration hBaseConfig;

    private Map<String, Table> tableCache = new HashMap<String, Table>();


    private HBaseClient(String zklist, String zkport) {
        hBaseConfig = getConfiguration(zklist, zkport);
    }

    private static Configuration getConfiguration(String zklist, String zkport) {
        Configuration config = HBaseConfiguration.create();
        if(StringUtils.isNotBlank(zklist)) {
            config.set("hbase.zookeeper.quorum", zklist);
        }

        if(StringUtils.isNotBlank(zkport)) {
            config.set("hbase.zookeeper.property.clientPort", zkport);
        }


        logger.info("zklist = {}, zkport = {}, config = {}", zklist, zkport, config);
        return config;
    }

    public Table getTable(String tableName) throws Exception, IOException {

        if(!tableCache.containsKey(tableName)) {
            synchronized (this) {
                if(!tableCache.containsKey(tableName)) {
                    logger.info("tableName = {}", tableName);
//		Connection connection = ConnectionFactory.createConnection(config, User.create(UserGroupInformation.createRemoteUser("ngadmin")));
                    Connection connection = ConnectionFactory.createConnection(hBaseConfig);
                    logger.info("connection = {}", connection);
//		if (connection.getAdmin().tableExists((TableName.valueOf(DEFAULT_TABLE)))) {
//			logger.warn("不存在表：{}", DEFAULT_TABLE);
//		}
                    tableCache.put(tableName,  connection.getTable(TableName.valueOf(tableName)));
                }
            }
        }
        return tableCache.get(tableName);
    }

    public String getRowKey(String... parts) {
        StringBuffer sb = new StringBuffer();
        for (String part : parts) {
            if(sb.length() > 0) {
                sb.append("_");
            }
            sb.append(part);
        }

        return sb.toString();
//
//        String hash = Utils.sha256Encode(sb.toString());
//        String prefix = StringUtils.substring(hash, 0, 2);
//
//        return prefix + "_" + sb.toString();
    }


    public Result get(String tableName, String rowKey) throws Exception {
        return get(tableName, rowKey, null);
    }

    public Result get(String tableName, String rowKey, String family) throws Exception {

        Get get = new Get(Bytes.toBytes(rowKey));
        if (family != null) {
            get.addFamily(family.getBytes());
        }
        return getTable(tableName).get(get);
    }
    public void delete(String tableName, String rowKey) throws Exception {
        delete(tableName, rowKey, null);
    }
    public void delete(String tableName, String rowKey, String family) throws Exception {

        Delete delete = new Delete(Bytes.toBytes(rowKey));
        if (family != null) {
            delete.addFamily(family.getBytes());
        }
        getTable(tableName).delete(delete);
    }

    public ResultScanner scan(String tableName, String prefixFilter) throws Exception {

        Scan scan = new Scan();
        scan.setRowPrefixFilter(Bytes.toBytes(prefixFilter));
        ResultScanner rs = getTable(tableName).getScanner(scan);
        return rs;
    }

    public ResultScanner scan(String tableName, String prefixFilter, long maxResultSize) throws Exception {

        Scan scan = new Scan();
        scan.setRowPrefixFilter(Bytes.toBytes(prefixFilter));
        scan.setMaxResultSize(maxResultSize);
        ResultScanner rs = getTable(tableName).getScanner(scan);
        return rs;
    }

    public ResultScanner scan(String tableName, long maxResultSize) throws Exception {

        Scan scan = new Scan();
        scan.setMaxResultSize(maxResultSize);
        ResultScanner rs = getTable(tableName).getScanner(scan);
        return rs;
    }


    public ResultScanner scan(String tableName, String startRowKey, String endRowKey) throws Exception {

        Scan scan = new Scan();
        scan.setStartRow(Bytes.toBytes(startRowKey));
        scan.setStopRow(Bytes.toBytes(endRowKey));
        ResultScanner rs = getTable(tableName).getScanner(scan);
//        for(Result result : rs){
//            OrderInfo orderInfo = new OrderInfo();
//            orderInfo.setId(Bytes.toString(result.getValue(Bytes.toBytes("f"), Bytes.toBytes("id"))));
//            orderInfo.setHistoryId(Bytes.toString(result.getValue(Bytes.toBytes("f"), Bytes.toBytes("historyId"))));
//            orderInfo.setOrderId(Bytes.toLong(result.getValue(Bytes.toBytes("f"), Bytes.toBytes("orderId"))));
//            orderInfo.setOrderDirection(Bytes.toString(result.getValue(Bytes.toBytes("f"), Bytes.toBytes("orderDirection"))));
//            list.add(orderInfo);
//        }
        return rs;
    }

    public PutContainer table(String tableName) throws Exception {
        return new PutContainer(getTable(tableName));
    }

    public static String resultGet(Result result, String family, String qualifier) {
        if(result.containsColumn(Bytes.toBytes(family),Bytes.toBytes(qualifier))) {
            return new String(result.getFamilyMap(Bytes.toBytes(family)).get(Bytes.toBytes(qualifier)));
        }else {
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
//        String zklist = "node1,node2,node3";
//        String zkport = "2183";
        HBaseClient hBaseClient = HBaseClient.getInstance(null, null);

        ResultScanner manis_statistics_runtime = hBaseClient.scan("manis_statistics_runtime", "user_newly_increased_");
        Map<String, BigDecimal> results = new HashMap<String, BigDecimal>();
        for (Result result : manis_statistics_runtime) {
            String rowkey = new String(result.getRow());
            String keyInfo = rowkey.substring(0, rowkey.lastIndexOf("_"));
            keyInfo = keyInfo.substring(0, keyInfo.lastIndexOf("_"));
            if(!results.containsKey(keyInfo)) {
                results.put(keyInfo, BigDecimal.ZERO);
            }
            results.put(keyInfo, results.get(keyInfo).add(new BigDecimal(resultGet(result, "items", "0")))) ;
            System.out.println(new String(result.getRow()) + "=>" + resultGet(result, "items", "0"));
        }
        for (String s : results.keySet()) {
            System.out.println(s + " => " + results.get(s).toPlainString());
        }


        Result result = hBaseClient.get("manis_statistics_hour", "00_1039_21170311");
        System.out.println(resultGet(result, "infos", "name"));
        hBaseClient.table("manis_statistics_hour")
                .rowKey("00_1039_21170311")
                .column("infos", "testDouble", 1.30d)
                .column("infos", "name", "这是一个测试信息").put();

        result = hBaseClient.get("manis_statistics_hour", "00_1039_21170311");
        System.out.println(resultGet(result, "infos", "name"));
        System.out.println(resultGet(result, "infos", "testDouble"));
        hBaseClient.delete("manis_statistics_hour", "00_1039_21170311");
//        hBaseClient.table("lcs")
//                .rowKey("lcs_234234324324")
//                .family("data")
//                .column("rate", 1)
//                .column("name", "234").put();

    }

    public static HBaseClient getInstance(String zklist, String zkport) {
        if(!hBaseClients.containsKey(zklist + "|" + zkport)) {
            synchronized (HBaseClient.class) {
                if(!hBaseClients.containsKey(zklist + "|" + zkport)) {
                    hBaseClients.put(zklist + "|" + zkport, new HBaseClient(zklist, zkport));
                }
            }
        }
        return hBaseClients.get(zklist + "|" + zkport);
    }

    public static class PutContainer{
        private Put put ;
        private Table table;

        private String family;

        public PutContainer(){
        }

        public PutContainer(Table table){
            this.table = table;
        }

        public PutContainer(Table table, Put put){
            this.table = table;
            this.put = put;
        }

        public PutContainer table(Table table){
            this.table = table;
            return this;
        }

        public PutContainer rowKey(String rowKey){
            this.put =  new Put(Bytes.toBytes(rowKey));
            return this;
        }

        public PutContainer family(String family){
            this.family = family;
            return this;
        }

        public PutContainer column( String qualifier, Object value) {
            put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier),Bytes.toBytes(String.valueOf(value)));
            return this;
        }


        public PutContainer column(String family, String qualifier, Object value) {
            put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier),Bytes.toBytes(String.valueOf(value)));
            return this;
        }

        public PutContainer addColumn(String family, String qualifier, Object value) {
            put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier),Bytes.toBytes(String.valueOf(value)));
            return this;
        }

        public void put() throws IOException {
            table.put(put);
        }

    }
}
