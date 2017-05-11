package com.hframework.common.util.message.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * User: zhangqh6
 * Date: 2016/1/2 13:46:46
 */
public class DefaultTreeImpl {

    private String id;
    private String pid;
    private String value;
    private String text;
    private boolean isOpen;
    private boolean checked;
    private String url;//暂缺

    private String call;
    private String img0;
    private String img1;
    private String img2;

    public DefaultTreeImpl() {
        super();
    }

    public DefaultTreeImpl(String id, String pid, String value, String text, boolean isOpen, boolean checked, String url, String call, String img0, String img1, String img2) {
        this.id = id;
        this.pid = pid;
        this.value = value;
        this.text = text;
        this.isOpen = isOpen;
        this.checked = checked;
        this.url = url;
        this.call = call;
        this.img0 = img0;
        this.img1 = img1;
        this.img2 = img2;
    }

    public String getId() {
        return id;
    }

    public String getPid() {
        return pid;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public boolean isChecked() {
        return checked;
    }

    public String getUrl() {
        return url;
    }

    public String getCall() {
        return call;
    }

    public String getImg0() {
        return img0;
    }

    public String getImg1() {
        return img1;
    }

    public String getImg2() {
        return img2;
    }

    public static String toXMLString(List<DefaultTreeImpl> treeList) {
        TreeParser<DefaultTreeImpl> treeParser = new TreeParser<DefaultTreeImpl>(treeList) {

            @Override
            public String getId(DefaultTreeImpl defaultTree) {
                return defaultTree.getId();
            }

            @Override
            public String getPid(DefaultTreeImpl defaultTree) {
                return defaultTree.getPid();
            }

            @Override
            public String getValue(DefaultTreeImpl defaultTree) {
                return defaultTree.getValue();
            }

            @Override
            public String getText(DefaultTreeImpl defaultTree) {
                return defaultTree.getText();
            }

            @Override
            public boolean isOpen(DefaultTreeImpl defaultTree) {
                return defaultTree.isOpen();
            }

            @Override
            public boolean checked(DefaultTreeImpl defaultTree) {
                return defaultTree.isChecked();
            }

            @Override
            public double getPri(DefaultTreeImpl defaultTree) {
                return -1;
            }

        };

        return treeParser.toXMLString();
    }

    public static void main(String[] args) {
        List<DefaultTreeImpl> treeList = new ArrayList<DefaultTreeImpl>();
        treeList.add(new DefaultTreeImpl("1", "-1","1", "哈哈1", true, false, null, null, null, null,null));
        treeList.add(new DefaultTreeImpl("11", "1","11", "哈哈11", true, true, null, null, null, null,null));
        treeList.add(new DefaultTreeImpl("12", "1","12", "哈哈12", true, true, null, null, null, null,null));
        treeList.add(new DefaultTreeImpl("111", "11","111", "哈哈111", false, true, null, null, null, null,null));

        System.out.println(toXMLString(treeList));
    }
}
