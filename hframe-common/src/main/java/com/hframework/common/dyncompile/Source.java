package com.hframework.common.dyncompile;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

/**
 * Created by zhangquanhong on 2017/3/14.
 */
public class Source extends SimpleJavaFileObject {
    private final String content;
    public Source(String name, Kind kind, String content) {
        super(URI.create("dynamic:///" + name.replace('.', '/') + kind.extension), kind);
        this.content = content;
    }
    @Override
    public CharSequence getCharContent(boolean ignore) {
        return this.content;
    }
}