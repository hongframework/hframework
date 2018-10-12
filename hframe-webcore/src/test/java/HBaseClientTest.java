import com.hframework.common.client.hbase.HBaseClient;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class HBaseClientTest {

    public static void main(String[] args) throws Exception {
//        String zklist = "node1,node2,node3";
//        String zkport = "2183";
        System.out.println(System.getenv("HADOOP_HOME"));
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

    public static String resultGet(Result result, String family, String qualifier) {
        if(result.containsColumn(Bytes.toBytes(family),Bytes.toBytes(qualifier))) {
            return new String(result.getFamilyMap(Bytes.toBytes(family)).get(Bytes.toBytes(qualifier)));
        }else {
            return null;
        }
    }
}


