package com.hframework.common.util.cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by zhangquanhong on 2016/8/25.
 */
public class ShellExecutor {

    public static void exeCmd(String commandStr) {
        BufferedReader br = null;
        try {
            Process p = Runtime.getRuntime().exec(commandStr);
            br = new BufferedReader(new InputStreamReader(p.getInputStream(),"GBK"));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            System.out.println(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
            if (br != null)
            {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        String commandStr = "ping www.taobao.com";
        //String commandStr = "ipconfig";
//        exeCmd(commandStr);
        exeCmd("D:\\my_workspace\\hframe-shop\\build\\compile.bat");
    }
}
