package com.hframework.common.util.file;

import com.hframework.common.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    private static Logger logger = LoggerFactory.getLogger(FileUtils.class);
    /**
     * 转化为绝对路径文件名
     *
     * @param fileName 文件名
     * @return
     */
    public static String getAbsoluteFileName(String fileName) {
        if ((fileName.indexOf(":\\") != -1) || (fileName.indexOf("/") == 0)) {
            return fileName;
        }

        int index = fileName.indexOf("classpath:");
        if (index != -1) {
            return Thread.currentThread().getContextClassLoader().getResource("").getPath()
                    + fileName.substring("classpath:".length(), fileName.length());
        }
        return Thread.currentThread().getContextClassLoader().getResource("").getPath() + fileName;
    }

    /**
     *  文件类型匹配
     * @param file 文件
     * @param extension 文件类型名称
     * @return
     */
    public static boolean fileFilter(File file, String extension) {
        // 将文件名称转换为小写
        String lCaseFilename = file.getName().toLowerCase();
        return (file.isFile() && (lCaseFilename.indexOf(extension) > 0)) ? true : false;
    }

    /**
     * 创建目录
     * @param filePath
     */
    public static void createDir(String filePath) {
        File myFile = new File(filePath);
        if (!myFile.exists()) {
            myFile.mkdirs();
        }
    }
    /**
     * 创建文件，如果父目录不存在，则创建父目录后创建
     * @param filePath
     * */
    public static void createFile(String filePath){
         File file = new File(filePath);
         File parentDirectory = file.getParentFile();
        if(parentDirectory!=null&&!parentDirectory.exists()){
            parentDirectory.mkdirs();
        }
        if(!file.exists()){
            try{
                file.createNewFile();
            }catch (Exception e){
                logger.error("创建文件失败："+e.getMessage());
            }
        }
    }

    public static String readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName)),"UTF-8"));
        String retStr = "";
        String str = br.readLine();
        while(str != null) {
            retStr += str;
            str = br.readLine();
        }
        return retStr;
    }

    public static List<String> readFileToArray(String fileName) throws IOException {
        logger.debug("method params：{}", fileName);
        List<String> result = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName)), "UTF-8"));
        String str = br.readLine();
        while(str != null) {
            result.add(str);
            str = br.readLine();
        }
        logger.debug("method return：{}", result);
        return result;
    }

    public static void copyFile(File theFile,String filePath) throws  IOException{
        InputStream in=new FileInputStream(theFile);

        FileOutputStream out=new FileOutputStream(filePath);
        System.out.println(filePath);

        byte[] buffer= new byte[1024];
        int length=-1;
        while((length=in.read(buffer))!=-1){
            out.write(buffer,0,length);
        }
        out.flush();
        out.close();
        in.close();
    }

    public static  String saveFile(File srcFile,String destDir) throws FileNotFoundException, IOException
    {
        createDir(destDir);
        String destPath=destDir+File.separatorChar+srcFile.getName();
        copyFile(srcFile, destPath);
        return destPath;
    }

    public static void deleteFile(String filePath){
        File destFile=new File(filePath);
        if(destFile.exists()){
            destFile.delete();
        }
    }

    public static File[] getFileList(File directory) {

        if(directory.isDirectory()){

            File[] files=directory.listFiles();

            for (File file : files) {

                System.out.println(file.getName());

            }
            return files;
        }

        return null;
    }

    public static void writeFile(File filePath, String fileContent) {

        BufferedWriter bw=null;
        try {
            bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath)));

            String[] ss=fileContent.split("\n");
            for (String s : ss) {
                bw.write(s);
                bw.newLine();
            }
            bw.flush();
            System.out.println("文件创建成功");

        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(bw!=null){
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public static void writeFile(String filePath, String fileContent) {
        newFile(filePath);
        BufferedWriter bw=null;
        try {
            bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filePath)),"UTF-8"));

            String[] ss=fileContent.split("\n");
            for (String s : ss) {

//                for(String part :s.split("/n")){
                    bw.write(s);
                    bw.newLine();
//                }
            }
            bw.flush();
            System.out.println("文件创建成功:" + filePath);

        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(bw!=null){
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    /**
     * A方法追加文件：使用RandomAccessFile
     * @param fileName 文件名
     * @param content 追加的内容
     */
    public static void appendMethodA(String fileName,

                                     String content){
        try {
            // 打开一个随机访问文件流，按读写方式
            RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
            // 文件长度，字节数
            long fileLength = randomFile.length();
            //将写文件指针移到文件尾。
            randomFile.seek(fileLength);
            randomFile.writeBytes(content);
            randomFile.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    /**
     * B方法追加文件：使用FileWriter
     * @param fileName
     * @param content
     */
    public static void appendMethodB(String fileName, String content){
        try {
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void appendMethod(String fileName,
                                    String content,String dbName){
        File file = new File(fileName);
        String srcCont="";
        BufferedReader reader = null;
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            //一次读入一行，直到读入null为文件结束
            boolean flag=true;
            while ((tempString = reader.readLine()) != null){
                //显示行号
                if(("<!--"+dbName+"-->").equals(tempString.trim())){
                    flag=!flag;
                    continue;
                }
                if(flag){
                    srcCont+=tempString+"\n";
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        writeFile(fileName, srcCont.substring(0,srcCont.length()-10)+content+"</struts>");
    }

    /**
     * 获取某目录根节点下面的树形结构的所有文件以及文件夹
     * @param directory
     * @param pid
     * @param resultFiles
     */
    public static List<MyFile> getAllFilesFromParDirectory(File directory, String pid,
                                                           List<MyFile> resultFiles) {

        if(resultFiles == null){
            resultFiles = new ArrayList<MyFile>();
        }

        File[] subFiles = getFileList(directory);
        for (int i = 0; subFiles != null && i < subFiles.length; i++) {
            String id= CommonUtils.uuid();
            resultFiles.add(new MyFile(id,pid,subFiles[i].getName(),subFiles[i].getAbsolutePath().replace("\\", "/"),i+1));

            if(subFiles[i].isDirectory()==true){
                getAllFilesFromParDirectory(subFiles[i],id,resultFiles);
            }
        }

        return resultFiles;
    }

    /**
     * 获取某目录根节点下面的树形结构的所有文件以及文件夹
     * @param directory
     */
    public static List<MyFile> getAllFilesFromParDirectory(File directory,String[] unincludePackageNames,String[] fileTypeName) {
        return getAllFilesFromParDirectory(directory, "-1", null, unincludePackageNames,fileTypeName);
    }

    /**
     * 获取某目录根节点下面的树形结构的所有文件以及文件夹
     * @param directory
     * @param pid
     * @param resultFiles
     */
    public static List<MyFile> getAllFilesFromParDirectory(File directory, String pid,
                                                           List<MyFile> resultFiles,String[] unincludePackageNames,String[] fileTypeName) {

        if(resultFiles == null){
            resultFiles = new ArrayList<MyFile>();
        }

        File[] subFiles = getFileList(directory);
        for (int i = 0; subFiles != null && i < subFiles.length; i++) {
            String id=CommonUtils.uuid();
            String fullName = subFiles[i].getAbsolutePath();
            if(subFiles[i].isDirectory()==true){
                resultFiles.add(new MyFile(id,pid,subFiles[i].getName(),subFiles[i].getAbsolutePath().replace("\\", "/"),i+1));
                if(checkDictionaryNeedShow(unincludePackageNames,subFiles[i].getName())){
                    getAllFilesFromParDirectory(subFiles[i],id,resultFiles);
                }
            }else{
                if(checkFileNeedShow(fileTypeName,subFiles[i].getName())){
                    resultFiles.add(new MyFile(id,pid,subFiles[i].getName(),subFiles[i].getAbsolutePath().replace("\\", "/"),i+1));
                }
            }
        }

        for (MyFile myFile : resultFiles) {
            myFile.setShortname(myFile.getFullname().replace(directory.getAbsolutePath().replace("\\", "/"), "").substring(1));
        }

        return resultFiles;
    }

    public static void replaceFile(String filePath, String oldString, String newString) throws IOException {
        List<String> contentLines = readFileToArray(filePath);
        String target = "";
        for (String s : contentLines) {
            target += (s.replaceAll(oldString, newString) + "\n");
        }
        writeFile(filePath, target);
    }

    private static boolean checkFileNeedShow(String[] fileTypeName, String name) {

        if(fileTypeName == null ) {
            return true;
        }

        for (String string : fileTypeName) {
            if(name.endsWith(string)) {
                return true;
            }
        }

        return false;
    }

    private static boolean checkDictionaryNeedShow(
            String[] unincludePackageNames, String packageName) {

        if(unincludePackageNames == null ) {
            return true;
        }

        for (String unincludePackageName : unincludePackageNames) {
            if(packageName.equals(unincludePackageName)) {
                return false;
            }
        }

        return true;
    }

    public static void newFolder(String folderPath) {
        try {
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            if (!myFilePath.exists()) {
                myFilePath.mkdirs();
            }
        } catch (Exception e) {
            System.out.println("新建目录操作出错");
            e.printStackTrace();
        }
    }

    public static void newFile(String filePathAndName, String fileContent) {

        try {
            String filePath = filePathAndName;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            if (!myFilePath.exists()) {
                myFilePath.createNewFile();
            }
            FileWriter resultFile = new FileWriter(myFilePath);
            PrintWriter myFile = new PrintWriter(resultFile);
            String strContent = fileContent;
            myFile.println(strContent);
            resultFile.close();

        } catch (Exception e) {
            System.out.println("新建目录操作出错");
            e.printStackTrace();

        }

    }

    public static void newFile(String filePathAndName) {

        try {
            String filePath = filePathAndName;
            filePath = filePath.toString();

            newFolder(filePath.substring(0,filePath.lastIndexOf("/")));

            File myFilePath = new File(filePath);
            if (!myFilePath.exists()) {
                myFilePath.createNewFile();
            }

        } catch (Exception e) {
            System.out.println("新建目录操作出错");
            e.printStackTrace();

        }

    }

    public static void delFile(String filePathAndName) {
        try {
            String filePath = filePathAndName;
            filePath = filePath.toString();
            java.io.File myDelFile = new java.io.File(filePath);
            myDelFile.delete();

        } catch (Exception e) {
            System.out.println("删除文件操作出错");
            e.printStackTrace();

        }

    }

    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); // 删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); // 删除空文件夹

        } catch (Exception e) {
            System.out.println("删除文件夹操作出错");
            e.printStackTrace();

        }

    }

    public static void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);// 再删除空文件夹
            }
        }
    }

    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { // 文件存在时
                InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }


    public static void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath
                            + "/" + (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {// 如果是子文件夹
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();

        }

    }

    public static void moveFile(String oldPath, String newPath) {
        copyFile(oldPath, newPath);
        delFile(oldPath);

    }

    public static void moveFolder(String oldPath, String newPath) {
        copyFolder(oldPath, newPath);
        delFolder(oldPath);

    }

    public static void main(String[] args) throws IOException {

        String fileName = "/D:/my_workspace/chameleon//extension/src/main/template/webtemplate/extension/src/main/resources/spring/spring-config.xml";
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName)),"UTF-8"));
        String retStr = "";
        String str = br.readLine();
        while(str != null) {
            retStr += str;
            str = br.readLine();
        }
        System.out.println(retStr);
//        String fileName = "\"file:\\\\C:\\\\Users\\\\admin\\\\.m2\\\\repository\\\\com\\\\hframework\\\\hframe-webcore\\\\1.0-SNAPSHOT\\\\hframe-webcore-1.0-SNAPSHOT.jar!\\\\hframework\\\\template\\\\default\\\\page\\\\pagedescripter.xml\"";
//        InputStream is = ClassLoaderUtil.getResourceAsStream(fileName, FileUtils.class);
//        System.out.println(is);
//        List<MyFile> allFilesFromParDirectory = getAllFilesFromParDirectory(
//                new File("D:/my_workspace/hframe-trunk/hframe-reconciliation/src/main/resources"),
//                new String[]{"META-INF", "WEB-INF", "image", "js", "theme", "third", "design"}, new String[]{".xml"});
//        System.out.println(allFilesFromParDirectory);
    }

}
