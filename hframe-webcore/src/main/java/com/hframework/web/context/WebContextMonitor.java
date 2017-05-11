package com.hframework.web.context;

import com.google.common.collect.MapMaker;
import net.sf.ehcache.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangquanhong on 2016/10/19.
 */
public class WebContextMonitor {
    private static final Logger logger               = LoggerFactory.getLogger(WebContextMonitor.class);
    // 扫描周期，单位秒
    private long                             scanIntervalInSecond = 5;

    private Map<Class,String> rootConfMap   = new MapMaker().makeMap();

    private Map<Class,Map<String,FileInfo>> lastFiles   = new MapMaker().makeMap();

    private WebContext webContext;

    public WebContextMonitor(WebContext webContext) {
        this.webContext = webContext;

    }


    private ScheduledExecutorService executor  = Executors.newScheduledThreadPool(1,
            new NamedThreadFactory("web-context-flush"));

    public void start() {
        init();
        executor.scheduleWithFixedDelay(new Runnable() {

            public void run() {
                try {
                    scan();
                } catch (Throwable e) {
                    logger.error("scan failed", e);
                }
            }

        }, scanIntervalInSecond, scanIntervalInSecond, TimeUnit.SECONDS);
    }

    public void stop() {
        executor.shutdownNow();
        lastFiles.clear();
    }

    private void init() {
        for (Map.Entry<Class, String> entry : rootConfMap.entrySet()) {
            Class config = entry.getKey();
            String rootConf = entry.getValue();
            File rootdir = new File(rootConf);
            if (!rootdir.exists()) {
                return;
            }
            lastFiles.put(config, new HashMap<String, FileInfo>());
            File[] files = rootdir.listFiles(new FileFilter() {

                public boolean accept(File pathname) {
                    String filename = pathname.getName();
                    return filename.endsWith(".xml");
                }
            });


            for (File file : files) {
                lastFiles.get(config).put(file.getAbsolutePath(), new FileInfo(file.getName(), file.lastModified()));
            }
        }
    }


    private void scan() {
        for (Map.Entry<Class, String> entry : rootConfMap.entrySet()) {
            Class config = entry.getKey();
            String rootConf = entry.getValue();
            File rootdir = new File(rootConf);
            if (!rootdir.exists()) {
                return;
            }

            File[] files = rootdir.listFiles(new FileFilter() {

                public boolean accept(File pathname) {
                    String filename = pathname.getName();
                    return filename.endsWith(".xml");
                }
            });

            List<File> diffFile = new ArrayList<File>();
            for (File file : files) {
                if(!lastFiles.get(config).containsKey(file.getAbsolutePath())) {
                    diffFile.add(file);
                }else {
                    FileInfo lastFile = lastFiles.get(config).get(file.getAbsolutePath());
                    boolean hasChanged = file.lastModified() != lastFile.getLastModified();
                    // 通知变化
                    if (hasChanged) {
                        diffFile.add(file);
                    }
                }
            }

            try {
                webContext.overrideContext(diffFile, config);
                for (File file : diffFile) {
                    lastFiles.get(config).put(file.getAbsolutePath(), new FileInfo(file.getName(), file.lastModified()));
                }
            }catch (Exception e) {
            }

        }
    }

    public Map<Class, String> getRootConfMap() {
        return rootConfMap;
    }

    public void addRootConfMap(Class conf, String rootPath) {
        rootConfMap.put(conf, rootPath);
    }

    public static class FileInfo {

        private String name;
        private long   lastModified = 0;

        public FileInfo(String name, long lastModified){
            this.name = name;
            this.lastModified = lastModified;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getLastModified() {
            return lastModified;
        }

        public void setLastModified(long lastModified) {
            this.lastModified = lastModified;
        }

    }

}
