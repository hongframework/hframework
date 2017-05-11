package com.hframework.common.client.ftp;

import org.apache.commons.net.ftp.*;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhangquanhong on 2016/5/10.
 */
public class FtpClient {

    private static final Logger logger = LoggerFactory.getLogger(FtpClient.class);

    /**
     * 上传文件
     * @param ip  IP
     * @param port 端口
     * @param username 用户名
     * @param password 密码
     * @param path 远端文件目录
     * @param fileName 远端文件名
     * @param tempFile 本地临时文件
     * @return 上传结果true/fase
     */
    public static boolean upload(String ip, int port, String username,
                                 String password, String path, String fileName,File tempFile) {
        FtpHelper fu = new FtpHelper(ip, port, username, password);
        try {
            fu.connectFTPServer();
            return fu.uploadFile(tempFile.getAbsolutePath(), fileName, path);
        } catch (Exception e) {
            System.out.println("异常信息：" + e.getMessage());
        }finally {
            try {
                fu.closeFTPClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 下载文件
     * @param ip IP
     * @param port 端口
     * @param username 用户名
     * @param password 密码
     * @param path 远端文件目录
     * @param fileName 远端文件名
     * @param tempPath 本地临时文件路径
     * @return
     */
    public static boolean download(String ip, int port ,String username, String password, String path, String fileName , String tempPath) throws Exception {
        logger.debug("request：{}", ip, port, username ,password ,path, fileName, tempPath);
        FtpHelper fu = new FtpHelper(ip, port, username, password);
        try {
            fu.connectFTPServer();
            fu.downloadFile(path,fileName, tempPath);
            return true;
        } catch (Exception e) {
            logger.error("error",e);
            throw e;
        }finally {
            try {
                fu.closeFTPClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
//        upload("***", 21, "***", "***", new File("D:/ftp/test.txt"), "/ucfpay/pay", "test.txt");
        download("***", 21, "***", "***","/ucfpay/pay", "***","D:/ftp");

//        upload("10.18.6.15", 21, "ftp", "ftp", "/pub/upload", "20160105-Pay-M100000000.txt", new File("D:/ftp/20160105-Pay-M100000000.txt"));
//        download("10.18.6.15", 21, "ftp", "ftp", "/pub/upload", "20160105-Pay-M100000000.txt","D:/ftp");
    }

    public static class FtpHelper {

        private FTPClient ftp = null;
        /**
         * Ftp服务器
         */
        private String server;
        /**
         * 用户名
         */
        private String uname;
        /**
         * 密码
         */
        private String password;
        /**
         * 连接端口，默认21
         */
        private int port = 21;

        private Document document ;

        public FtpHelper(String server, int port, String uname,
                         String password){
            this.server = server;
            if (this.port > 0){
                this.port = port;
            }
            this.uname = uname;
            this.password = password;
            //初始化
            ftp = new FTPClient();
        }
        /**
         * 连接FTP服务器
         *
         * @return
         * @throws Exception
         */
        public FTPClient connectFTPServer() throws Exception {
            try {
                ftp.configure(getFTPClientConfig());
                ftp.connect(this.server, this.port);
                if (!ftp.login(this.uname, this.password)) {
                    ftp.logout();
                    ftp = null;
                    return ftp;
                }

                // 文件类型,默认是ASCII
                ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftp.setControlEncoding("GBK");
                // 设置被动模式
                ftp.enterLocalPassiveMode();
                ftp.setConnectTimeout(2000);
                ftp.setBufferSize(1024);
                // 响应信息
                int replyCode = ftp.getReplyCode();
                if ((!FTPReply.isPositiveCompletion(replyCode))) {
                    // 关闭Ftp连接
                    closeFTPClient();
                    // 释放空间
                    ftp = null;
                    throw new Exception("登录FTP服务器失败,请检查![Server:" + server + "、"
                            + "User:" + uname + "、" + "Password:" + password);
                } else {
                    return ftp;
                }
            } catch (Exception e) {
                ftp.disconnect();
                ftp = null;
                throw e;
            }
        }

        /**
         * 配置FTP连接参数
         *
         * @return
         * @throws Exception
         */
        public FTPClientConfig getFTPClientConfig() throws Exception {
            String systemKey = FTPClientConfig.SYST_UNIX;
            String serverLanguageCode = "zh";
            FTPClientConfig conf = new FTPClientConfig(systemKey);
            conf.setServerLanguageCode(serverLanguageCode);
            conf.setDefaultDateFormatStr("yyyy-MM-dd");
            return conf;
        }

        /**
         * 向FTP根目录上传文件
         *
         * @param localFile
         * @param newName
         *            新文件名
         * @throws Exception
         */
        public Boolean uploadFile(String localFile, String newName)
                throws Exception {
            InputStream input = null;
            boolean success = false;
            try {
                File file = null;
                if (checkFileExist(localFile)) {
                    file = new File(localFile);
                }
                input = new FileInputStream(file);
                success = ftp.storeFile(newName, input);
                if (!success) {
                    throw new Exception("文件上传失败!");
                }
            } catch (Exception e) {
                throw e;
            } finally {
                if (input != null) {
                    input.close();
                }
            }
            return success;
        }

        /**
         * 向FTP根目录上传文件
         *
         * @param input
         * @param newName
         *            新文件名
         * @throws Exception
         */
        public Boolean uploadFile(InputStream input, String newName)
                throws Exception {
            boolean success = false;
            try {
                success = ftp.storeFile(newName, input);
                if (!success) {
                    throw new Exception("文件上传失败!");
                }
            } catch (Exception e) {
                throw e;
            } finally {
                if (input != null) {
                    input.close();
                }
            }
            return success;
        }

        /**
         * 向FTP指定路径上传文件
         *
         * @param localFile
         * @param newName
         *            新文件名
         * @param remoteFoldPath
         * @throws Exception
         */
        public Boolean uploadFile(String localFile, String newName,
                                  String remoteFoldPath) throws Exception {

            InputStream input = null;
            boolean success = false;
            try {
                File file = null;
                if (checkFileExist(localFile)) {
                    file = new File(localFile);
                }
                input = new FileInputStream(file);

                // 改变当前路径到指定路径
                if (!this.changeDirectory(remoteFoldPath)) {
                    logger.warn("服务器路径不存在:{}", remoteFoldPath);
                    return false;
                }
                success = ftp.storeFile(newName, input);
                if (!success) {
                    logger.warn("文件上传失败:{}", newName);
                    throw new Exception("文件上传失败!");
                }
            } catch (Exception e) {
                throw e;
            } finally {
                if (input != null) {
                    input.close();
                }
            }
            return success;
        }

        /**
         * 向FTP指定路径上传文件
         *
         * @param input
         * @param newName
         *            新文件名
         * @param remoteFoldPath
         * @throws Exception
         */
        public Boolean uploadFile(InputStream input, String newName,
                                  String remoteFoldPath) throws Exception {
            boolean success = false;
            try {
                // 改变当前路径到指定路径
                if (!this.changeDirectory(remoteFoldPath)) {
                    System.out.println("服务器路径不存!");
                    return false;
                }
                success = ftp.storeFile(newName, input);
                if (!success) {
                    throw new Exception("文件上传失败!");
                }
            } catch (Exception e) {
                throw e;
            } finally {
                if (input != null) {
                    input.close();
                }
            }
            return success;
        }

        /**
         * 从FTP服务器下载文件
         *
         * @param remotePath
         *            FTP路径(不包含文件名)
         * @param fileName
         *            下载文件名
         * @param localPath
         *            本地路径
         */
        public Boolean downloadFile(String remotePath, String fileName,
                                    String localPath) throws Exception {

            BufferedOutputStream output = null;
            boolean success = false;
            try {
                // 检查本地路径
                this.checkFileExist(localPath);
                // 改变工作路径
                if (!this.changeDirectory(remotePath)) {
                    logger.warn("服务器路径不存在:{}",remotePath);
                    return false;
                }
                // 列出当前工作路径下的文件列表
                List<FTPFile> fileList = this.getFileList();
                if (fileList == null || fileList.size() == 0) {
                    logger.warn("服务器当前路径下不存在文件！");
                    return success;
                }
                for (FTPFile ftpfile : fileList) {
                    if (ftpfile.getName().equals(fileName)) {
                        File localFilePath = new File(localPath + File.separator
                                + ftpfile.getName());
                        output = new BufferedOutputStream(new FileOutputStream(
                                localFilePath));
                        success = ftp.retrieveFile(ftpfile.getName(), output);
                        break;
                    }
                    logger.warn("服务器无该文件：{}", remotePath, fileName);
                }
                if (!success) {
                    throw new Exception("文件下载失败!");
                }
            } catch (Exception e) {
                throw e;
            } finally {
                if (output != null) {
                    output.close();
                }
            }
            return success;
        }

        /**
         * 从FTP服务器获取文件流
         *
         * @param remoteFilePath
         * @return
         * @throws Exception
         */
        public InputStream downloadFile(String remoteFilePath) throws Exception {

            return ftp.retrieveFileStream(remoteFilePath);
        }

        /**
         * 获取FTP服务器上指定路径下的文件列表
         *
         * @param filePath
         * @return
         */
        public List<FTPFile> getFtpServerFileList(String remotePath)
                throws Exception {

            FTPListParseEngine engine = ftp.initiateListParsing(remotePath);
            List<FTPFile> ftpfiles = Arrays.asList(engine.getNext(25));

            return ftpfiles;
        }

        /**
         * 获取FTP服务器上[指定路径]下的文件列表
         *
         * @param path
         * @return
         * @throws Exception
         */
        public List<FTPFile> getFileList(String remotePath) throws Exception {

            List<FTPFile> ftpfiles = Arrays.asList(ftp.listFiles(remotePath));

            return ftpfiles;
        }

        /**
         * 获取FTP服务器[当前工作路径]下的文件列表
         *
         * @param path
         * @return
         * @throws Exception
         */
        public List<FTPFile> getFileList() throws Exception {

            List<FTPFile> ftpfiles = Arrays.asList(ftp.listFiles());

            return ftpfiles;
        }

        /**
         * 改变FTP服务器工作路径
         *
         * @param remoteFoldPath
         */
        public Boolean changeDirectory(String remoteFoldPath) throws Exception {

            return ftp.changeWorkingDirectory(remoteFoldPath);
        }

        /**
         * 删除文件
         *
         * @param remoteFilePath
         * @return
         * @throws Exception
         */
        public Boolean deleteFtpServerFile(String remoteFilePath) throws Exception {

            return ftp.deleteFile(remoteFilePath);
        }

        /**
         * 创建目录
         *
         * @param remoteFoldPath
         * @return
         */
        public boolean createFold(String remoteFoldPath) throws Exception {

            boolean flag = ftp.makeDirectory(remoteFoldPath);
            if (!flag) {
                throw new Exception("创建目录失败");
            }
            return false;
        }

        /**
         * 删除目录
         * @param remoteFoldPath
         * @return
         * @throws Exception
         */
        public boolean deleteFold(String remoteFoldPath) throws Exception {

            return ftp.removeDirectory(remoteFoldPath) ;
        }

        /**
         * 删除目录以及文件
         *
         * @param remoteFoldPath
         * @return
         */
        public boolean deleteFoldAndsubFiles(String remoteFoldPath)
                throws Exception {

            boolean success = false;
            List<FTPFile> list = this.getFileList(remoteFoldPath);
            if (list == null || list.size() == 0) {
                return deleteFold(remoteFoldPath);
            }
            for (FTPFile ftpFile : list) {

                String name = ftpFile.getName();
                if (ftpFile.isDirectory()) {
                    success = deleteFoldAndsubFiles(remoteFoldPath + "/" + name);
                    if (!success)
                        break;
                } else {
                    success = deleteFtpServerFile(remoteFoldPath + "/" + name);
                    if (!success)
                        break;
                }
            }
            if (!success)
                return false;
            success = deleteFold(remoteFoldPath);
            return success;
        }

        /**
         * 检查本地路径是否存在
         *
         * @param filePath
         * @return
         * @throws Exception
         */
        public boolean checkFileExist(String filePath) throws Exception {
            boolean flag = false;
            File file = new File(filePath);
            if (!file.exists()) {
                logger.warn("本地路径不存在:{}",filePath);
                throw new Exception("本地路径不存在,请检查!");
            } else {
                flag = true;
            }
            return flag;
        }

        /**
         * 创建XML文件
         * @return
         */
        public Element getCurrentElement(){
            document = DocumentHelper.createDocument();
            return document.addElement("root");
        }

        /**
         * 生成目录XML文件
         */
        public void createDirectoryXML(String remotePath,Element fatherElement) throws Exception{

            List<FTPFile> list = this.getFileList();
            for(FTPFile ftpfile:list){
                Element currentElement = fatherElement; //当前的目录节点
                String newRemotePath = remotePath+ftpfile.getName();
                if(ftpfile.isDirectory()){
                    Element dirElement = fatherElement.addElement("dir") ;
                    dirElement.addAttribute("name",ftpfile.getName());
                    currentElement = dirElement;
                    this.changeDirectory(newRemotePath); //从根目录开始
                    createDirectoryXML(newRemotePath,dirElement);
                }else{
                    Element fileElement = fatherElement.addElement("file");//文件节点
                    fileElement.setText(ftpfile.getName()) ;
                }
            }
        }

        /**
         * 保存xml
         */
        public void saveXML(){
            XMLWriter output = new XMLWriter();
            //输出格式化
            OutputFormat format = OutputFormat.createPrettyPrint();
            try {
                output = new XMLWriter(new FileWriter("src/com/shine/Ftp/config/dir.xml"), format);
                output.write(this.document);
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 关闭FTP连接
         *
         * @param ftp
         * @throws Exception
         */
        public void closeFTPClient(FTPClient ftp) throws Exception {

            try {
                if (ftp.isConnected())
                    ftp.logout();
                ftp.disconnect();
            } catch (Exception e) {
                throw new Exception("关闭FTP服务出错!");
            }
        }

        /**
         * 关闭FTP连接
         *
         * @throws Exception
         */
        public void closeFTPClient() throws Exception {

            this.closeFTPClient(this.ftp);
        }

        /**
         * Get Attribute Method
         *
         */
        public FTPClient getFtp() {
            return ftp;
        }

        public String getServer() {
            return server;
        }

        public String getUname() {
            return uname;
        }

        public String getPassword() {
            return password;
        }

        public int getPort() {
            return port;
        }

        /**
         * Set Attribute Method
         *
         */
        public void setFtp(FTPClient ftp) {
            this.ftp = ftp;
        }

        public void setServer(String server) {
            this.server = server;
        }

        public void setUname(String uname) {
            this.uname = uname;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }
}
