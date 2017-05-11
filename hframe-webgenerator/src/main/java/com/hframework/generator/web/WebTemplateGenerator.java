package com.hframework.generator.web;

import com.hframework.common.util.file.FileUtils;
import com.hframework.common.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * Created by zhangquanhong on 2016/12/28.
 */
public class WebTemplateGenerator {

    private static final String WEB_TEMPLATE_FILES_NAME = "template-files";

    private static final String PREFIX_DIR_COPY_KEY = "hframe.dir.copy.";
    private static final String PREFIX_DIR_CREATE_KEY = "hframe.dir.create.";
    private static final String PREFIX_FILE_COPY_KEY = "hframe.file.copy.";
    private static final String PREFIX_FILE_CLEAN_KEY = "hframe.file.clean.";

    private static final String[] ordered= {PREFIX_DIR_COPY_KEY,PREFIX_DIR_CREATE_KEY,PREFIX_FILE_COPY_KEY,PREFIX_FILE_CLEAN_KEY};
    private static Map<String, String> configs = new TreeMap(new Comparator() {
        public int compare(Object o1, Object o2) {
            int distinct = getIndex(o1) - getIndex(o2);
            return distinct == 0 ? o1.hashCode() -o2.hashCode() : distinct;
        }

        private int getIndex(Object o1) {
            for (int i = 0; i < ordered.length; i++) {
                if(((String)o1).startsWith(ordered[i])) return i;
            }
            return -1;
        }
    });

    private static String parentRootDir = null;
    private static String childRootDir = null;
    private static String childMavenArtifactId = null;

    private static Set<String> pomFileSet = new HashSet<String>();


    public static void generate() throws IOException {
        loadProperties();

        parentRootDir = configs.remove("hframe.globle.parent.root.dir");
        childRootDir = configs.remove("hframe.globle.child.root.dir");
        childMavenArtifactId = configs.remove("hframe.globle.child.maven.artifactId");

        fileGenerator();
        mavenArtifactIdReplace();

    }

    private static void mavenArtifactIdReplace() throws IOException {
        for (String pomFilePath : pomFileSet) {
            List<String> content = FileUtils.readFileToArray(pomFilePath);
            String target = "";
            for (String s : content) {
                target += (s.replace("<artifactId>hframe-trunk</artifactId>", "<artifactId>" + childMavenArtifactId + "</artifactId>") + "\n");
            }
            FileUtils.writeFile(pomFilePath, target);
        }
    }

    private static void fileGenerator() {
        for (Map.Entry<String, String> entry : configs.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            String originBasePath = parentRootDir ;
            String targetBasePath = childRootDir;

            String moduleName = getModuleName(key);
            originBasePath += ("\\" + moduleName);
            if(!"child-program-files".equals(moduleName)) {
                 targetBasePath += ( "\\" + moduleName);
            }

            String[] files = null;
            if(StringUtils.isNotBlank(value)) {
                files = StringUtils.split(value, ";");
            }
            if(files == null || files.length == 0) continue;
            for (String file : files) {
                String originFullPath = originBasePath + "\\" + file;
                String targetFullPath = targetBasePath + "\\" + file;
                if("pom.xml".equals(file)) {
                    pomFileSet.add(targetFullPath);
                }
                if(key.startsWith(PREFIX_DIR_COPY_KEY)) {
                    doDirCopy(originFullPath, targetFullPath);
                }else if(key.startsWith(PREFIX_DIR_CREATE_KEY)) {
                    doDirCreate(targetFullPath);
                }else if(key.startsWith(PREFIX_FILE_COPY_KEY)) {
                    doFileCopy(originFullPath, targetFullPath);
                }else if(key.startsWith(PREFIX_FILE_CLEAN_KEY)) {
                    doFileClean(targetFullPath);
                }
            }

        }
    }

    private static void doDirCreate(String targetFullPath) {
        FileUtils.createDir(targetFullPath);
    }

    private static void doFileClean( String targetFullPath) {
        FileUtils.createFile(targetFullPath);
    }

    private static void doFileCopy(String originFullPath, String targetFullPath) {
        FileUtils.createFile(targetFullPath);
        FileUtils.copyFile(originFullPath, targetFullPath);
    }



    private static void doDirCopy(String originFullPath, String targetFullPath) {
        FileUtils.copyFolder(originFullPath, targetFullPath);
    }

    private static String getModuleName(String key) {
        String categoryName ;
        if(key.startsWith(PREFIX_DIR_COPY_KEY)) {
            categoryName = key.substring(PREFIX_DIR_COPY_KEY.length());
        }else if(key.startsWith(PREFIX_DIR_CREATE_KEY)) {
            categoryName = key.substring(PREFIX_DIR_CREATE_KEY.length());
        }else if(key.startsWith(PREFIX_FILE_COPY_KEY)) {
            categoryName = key.substring(PREFIX_FILE_COPY_KEY.length());
        }else if(key.startsWith(PREFIX_FILE_CLEAN_KEY)) {
            categoryName = key.substring(PREFIX_FILE_CLEAN_KEY.length());
        }else {
            throw new RuntimeException("不支持改配置项目：" + key);
        }
        return categoryName;
    }


    public static void loadProperties() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(WEB_TEMPLATE_FILES_NAME);
        Enumeration<String> keys = resourceBundle.getKeys();
        while(keys.hasMoreElements()) {
            String element = keys.nextElement();
            String elementValue = resourceBundle.getString(element);
            configs.put(element, elementValue);
        }
    }

    public static void main(String[] args) throws IOException {
        generate();
    }
}
