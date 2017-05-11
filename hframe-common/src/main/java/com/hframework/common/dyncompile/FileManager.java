package com.hframework.common.dyncompile;

import javax.tools.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangquanhong on 2017/3/14.
 */
public class FileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
    protected final Map<String, Output> map = new HashMap<String, Output>();
    public FileManager(JavaCompiler compiler) {
        super(compiler.getStandardFileManager(null, null, null));  }  //取得文件管理器
    @Override
    public Output getJavaFileForOutput
            (Location location, String name, JavaFileObject.Kind kind, FileObject source) {
        Output mc = new Output(name, kind);   //与文件连接
        this.map.put(name, mc);
        return mc;
    }
    public Output getOutput(String name) {
        return map.get(name);
    }
}
