package com.hframework.smartsql.client;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

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

    public Result[] gets(String tableName, List<String> rowKeys) throws Exception {
        return gets(tableName, rowKeys);
    }

    public Result[] gets(String tableName, List<String> rowKeys, String family) throws Exception {
        List<Get> list = new ArrayList<Get>();
        for (String rowKey : rowKeys) {
            Get get = new Get(Bytes.toBytes(rowKey));
            if (family != null) {
                get.addFamily(family.getBytes());
            }
        }
        return getTable(tableName).get(list);
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

    public Putter putter() throws Exception {
        return new Putter(this);
    }

    public Scanner scanner() throws Exception {
        return new Scanner(this);
    }

    public Putter table(String tableName) throws Exception {
        return new Putter(getTable(tableName));
    }

    public static String resultGet(Result result, String family, String qualifier) {
        if(result.containsColumn(Bytes.toBytes(family),Bytes.toBytes(qualifier))) {
            return new String(result.getFamilyMap(Bytes.toBytes(family)).get(Bytes.toBytes(qualifier)));
        }else {
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        String zklist = "zqh,wzk,zzy";
        String zkport = "2181";
        HBaseClient hBaseClient = HBaseClient.getInstance(zklist, zkport);

        Set<String> keys = hBaseClient
                .scanner()
                .table("manis_statistics_hour")
                .rowPrefix("user_newly_increased_")
                .prefixRowsMatchAny("user_newly_increased_2017","user_newly_increased_0")
                .addResultColumn("items", "17")
                .addResultColumn("items", "14")
                .scanRowKeys();
        System.out.println(Arrays.toString(keys.toArray(new String[0])));

        ResultScanner datas = hBaseClient
                .scanner()
                .table("manis_statistics_hour")
                .rowPrefix("user_newly_increased_")
//                .startRow("user_newly_increased_20170320")
//                .stopRow("user_newly_increased_20170405")
//                .regexRow("user_newly_increased_2017.*4")
//                .regexRowsMatchAny("user_newly_increased_2017.{3}4.*", "user_newly_increased_2017.{3}0.*")
                .prefixRowsMatchAny("user_newly_increased_2017","user_newly_increased_0")
                .addResultColumn("items", "17")
                .addResultColumn("items", "14")
//                .resultSize(100L)
                .scan();

        Integer count = 0;

        for (Result data : datas) {
            count ++;
            String row = new String(data.getRow());
            for (Cell cell : data.rawCells()) {
                System.out.println("row : " + row + "; info : " + cell.toString() + "; value : " + new String(cell.getValue()));
            }
        }

//        for (Result data : datas) {
//            count ++;
//            String row = new String(data.getRow());
//            NavigableMap<byte[], NavigableMap<byte[], byte[]>> noVersionMap = data.getNoVersionMap();
//            for (Map.Entry<byte[], NavigableMap<byte[], byte[]>> navigableMapEntry : noVersionMap.entrySet()) {
//                String family = new String(navigableMapEntry.getKey());
//                NavigableMap<byte[], byte[]> columns = navigableMapEntry.getValue();
//                for (Map.Entry<byte[], byte[]> entry : columns.entrySet()) {
//                    System.out.println("row : " + row + "; family : " + family + "; column : " + new String(entry.getKey()) + "; value : " + new String(entry.getValue()));
//                }
//            }
//        }
        System.out.println("count => " + count);

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
        hBaseClient.putter().table("manis_statistics_hour")
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

    public static class Scanner {
        private HBaseClient hBaseClient;
        private Table table;
        private Scan scan;
        public Scanner(HBaseClient hBaseClient) {
            this.hBaseClient = hBaseClient;
            this.scan = new Scan();
        }

        public Scanner table(String tableName) throws Exception {
            this.table = hBaseClient.getTable(tableName);
            return this;
        }

        public Scanner rowPrefix(String prefixFilter) throws Exception {
            scan.setRowPrefixFilter(Bytes.toBytes(prefixFilter));
            return this;
        }

        public Scanner resultSize(long maxResultSize) throws Exception {
            scan.setMaxResultSize(maxResultSize);
            return this;
        }
        public Scanner startRow(String startRowKey) throws Exception {
            scan.setStartRow(Bytes.toBytes(startRowKey));
            return this;
        }
        public Scanner stopRow(String endRowKey) throws Exception {
            scan.setStopRow(Bytes.toBytes(endRowKey));
            return this;
        }
        public Scanner regexRow(String regex) throws Exception {
            RegexStringComparator comparator = new RegexStringComparator(regex);
            Filter filter = new RowFilter(CompareFilter.CompareOp.EQUAL, comparator);
            scan.setFilter(filter);
            return this;
        }

        public Scanner regexRowsMatchAll(String... regexs) throws Exception {
            return regexRows(true, regexs);
        }
        public Scanner regexRowsMatchAny(String... regexs) throws Exception {
            return regexRows(false, regexs);
        }

        public Scanner prefixRowsMatchAll(String... prefixs) throws Exception {
            return prefixRows(true, prefixs);
        }
        public Scanner prefixRowsMatchAny(String... prefixs) throws Exception {
            return prefixRows(false, prefixs);
        }


        private Scanner prefixRows(boolean matchAll, String... prefixs) throws Exception {
            List<Filter> filters = new ArrayList<Filter>();
            for (String prefix : prefixs) {
                BinaryPrefixComparator comparator = new BinaryPrefixComparator(Bytes.toBytes(prefix));
                Filter filter = new RowFilter(CompareFilter.CompareOp.EQUAL, comparator);
                filters.add(filter);
            }
            FilterList fl = new FilterList(matchAll? FilterList.Operator.MUST_PASS_ALL : FilterList.Operator.MUST_PASS_ONE, filters);
            scan.setFilter(fl);
            return this;
        }

        private Scanner regexRows(boolean matchAll, String... regexs) throws Exception {
            List<Filter> filters = new ArrayList<Filter>();
            for (String regex : regexs) {
                RegexStringComparator comparator = new RegexStringComparator(regex);
                Filter filter = new RowFilter(CompareFilter.CompareOp.EQUAL, comparator);
                filters.add(filter);
            }
            FilterList fl = new FilterList(matchAll? FilterList.Operator.MUST_PASS_ALL : FilterList.Operator.MUST_PASS_ONE, filters);
            scan.setFilter(fl);
            return this;
        }


        public Scanner addResultColumn(String family, String qualifier) throws Exception {
            scan.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier));
            return this;
        }
        public Scanner addResultColumns(String family, String qualifiers[]) throws Exception {
            for (String qualifier : qualifiers) {
                scan.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier));
            }
            return this;
        }

        public Scanner addResultColumns(Map<String, List<String>> columnMaps) throws Exception {
            for (Map.Entry<String, List<String>> entry : columnMaps.entrySet()) {
                String family = entry.getKey();
                List<String> qualifiers = entry.getValue();
                for (String qualifier : qualifiers) {
                    scan.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier));
                }
            }
            return this;
        }


        public ScanResult scan() throws IOException {
            ResultScanner rs = table.getScanner(scan);
            return new ScanResult(rs);
        }

        public Set<String> scanRowKeys() throws IOException {
            FilterList fl = new FilterList(FilterList.Operator.MUST_PASS_ALL, Lists.newArrayList(scan.getFilter(), new KeyOnlyFilter()));
            scan.setFilter(fl);
            Set<String> keys = new HashSet<String>();
            ScanResult rows = scan();
            for (Result row : rows) {
                keys.add(new String(row.getRow()));
            }
            return keys;
        }
    }

    public static class ScanResult implements ResultScanner{

        private ResultScanner resultScanner;

        public ScanResult(ResultScanner resultScanner) {
            this.resultScanner = resultScanner;
        }

        public Result next() throws IOException {
            return resultScanner.next();
        }

        public Result[] next(int nbRows) throws IOException {
            return resultScanner.next(nbRows);
        }

        public void close() {
            resultScanner.close();
        }

        public Iterator<Result> iterator() {
            return resultScanner.iterator();
        }
    }

    public static class Putter {
        private HBaseClient hBaseClient;
        private Put put ;
        private Table table;

        private String family;

        public Putter(){
        }

        public Putter(Table table){
            this.table = table;
        }

        public Putter(Table table, Put put){
            this.table = table;
            this.put = put;
        }

        public Putter(HBaseClient hBaseClient) {
            this.hBaseClient = hBaseClient;
        }

        public Putter table(String tableName) throws Exception {
            this.table = hBaseClient.getTable(tableName);
            return this;
        }

        public Putter table(Table table){
            this.table = table;
            return this;
        }

        public Putter rowKey(String rowKey){
            this.put =  new Put(Bytes.toBytes(rowKey));
            return this;
        }

        public Putter family(String family){
            this.family = family;
            return this;
        }

        public Putter column( String qualifier, Object value) {
            put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier),Bytes.toBytes(String.valueOf(value)));
            return this;
        }


        public Putter column(String family, String qualifier, Object value) {
            put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier),Bytes.toBytes(String.valueOf(value)));
            return this;
        }

        public Putter addColumn(String family, String qualifier, Object value) {
            put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier),Bytes.toBytes(String.valueOf(value)));
            return this;
        }

        public void put() throws IOException {
            table.put(put);
        }

    }
}
