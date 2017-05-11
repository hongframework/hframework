package com.hframework.common.dyncompile;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.net.URI;

/**
 * Created by zhangquanhong on 2017/3/14.
 */
public class Output extends SimpleJavaFileObject {
    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    public Output(String name, Kind kind) {
        super(URI.create("dynamic:///" + name.replace('.', '/') + kind.extension), kind); } //URI
    byte[] toByteArray() {  //外部调用，生成Class
        return this.baos.toByteArray();
    }
    @Override
    public ByteArrayOutputStream openOutputStream() {
        return this.baos;
    }
}
