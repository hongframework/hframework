package com.hframework.common.util.message.bean;

import com.hframework.common.util.collect.CollectionUtils;
import com.hframework.common.util.collect.bean.Grouper;
import com.hframework.common.util.StringUtils;

import java.util.*;

/**
 * User: zhangqh6
 * Date: 2016/1/2 12:05:05
 */
public abstract class TreeParser<T> {

    //数据列表
    private List<T> data = null;

    public TreeParser(List<T> data) {
        this.data = data;
    }

    private Map<String, List<T>> tempMap = null;

    private T root = null;

//    public abstract void setData(List<T> tList);

    //设置树条目的ID标识
    public abstract String getId(T t);

    //设置树条目的父ID标识
    public abstract String getPid(T t);

    //设置树条目的隐藏值
    public abstract String getValue(T t);

    //设置树条目的显示文本
    public abstract String getText(T t);

    //设置树条目是否展开
    public abstract boolean isOpen(T t);

    //设置树条目是否选中
    public abstract boolean checked(T t);

    //设置优先级
    public abstract double getPri(T t);

    public String getCloseImg(T t) {
        return "books_close.gif";
    }

    public String geOpenDicImg(T t) {
        return "tombs.gif";
    }

    public String getLeafImg(T t) {
        return "tombs.gif";
    }

    public String toXMLString() {
        groupData();
        StringBuffer sb = new StringBuffer();

        parseNodeXML(root,sb);

        return  sb.toString();
    }

    protected  void groupData() {

        //按照pid对数据进行分组
        tempMap = CollectionUtils.group(data, new Grouper<String, T>() {
            public <K> K groupKey(T t) {
                return (K) getPid(t);
            }
        });


        //获取所有数据的ID集合，用户下一步获取root所用
        Set<String> idSet = new HashSet<String>();
        for (T t : data) {
            idSet.add(getId(t));
        }

        //获取root
        for (T t : data) {
            String pId = getPid(t);
            if(!idSet.contains(pId)) {
                root = t;
            }
        }
    }


    public String parseNodeXML(T node, StringBuffer sb){

        sb.append("<item");
        if(StringUtils.isNotBlank(getId(node))) {
            sb.append(" id=\""+getId(node)+"\"");
        }

        if(isOpen(node)) {
            sb.append(" open=\"1\"");
        }

        if(checked(node)) {
            sb.append(" checked=\"1\"");
        }

        if(StringUtils.isNotBlank(getText(node))) {
            sb.append(" text=\""+getText(node)+"\"");
        }

        if(StringUtils.isNotBlank(getCloseImg(node))) {
            sb.append(" im0=\""  + getCloseImg(node) + "\"");
        }

        if(StringUtils.isNotBlank(geOpenDicImg(node))) {
            sb.append(" im1=\""  + geOpenDicImg(node) + "\"");
        }

        if(StringUtils.isNotBlank(getLeafImg(node))) {
            sb.append(" im2=\""  + getLeafImg(node) + "\"");
        }


//        if(!isOpen(node)) {
//            sb.append(" open=\"0\"");
//        }
//        if(!checked(node)) {
//            sb.append(" select=\"" + (checked(node)?1:0) + "\"");
//        }





        sb.append(">");
//        sb.append("\n");
        //获取子树
        List<T> subNodeList = tempMap.get(getId(node));
        if(subNodeList != null) {
            T[] subNodes = (T[])subNodeList.toArray();
            //排序
            Arrays.sort(subNodes, new Comparator<T>() {
                public int compare(T o1, T o2) {
                    return getPri(o1) > getPri(o2)? 1 : -1;
                }
            });

            for (T subNode : subNodes) {
                parseNodeXML(subNode,sb);
            }
        }

        sb.append("</item>");
//        sb.append("\n");
//        if(img0!=null&&!"".equals(img0)){
//            resultStr+=" img0=\""+this.img0+"\"";
//        }
//        if(img1!=null&&!"".equals(img1)){
//            resultStr+=" img1=\""+this.img1+"\"";
//        }
//        if(img2!=null&&!"".equals(img2)){
//            resultStr+=" img2=\""+this.img2+"\"";
//        }
//        if(call!=null&&!"".equals(call)){
//            resultStr+=" call=\""+this.call+"\"";
//        }


//        if(url!=null&&!"".equals(url)){
//            resultStr+="\n   <userdata name=\"url\">"+url+"</userdata> \n  ";
//        }
//
//        if(treeItems!=null&&treeItems.size()>0){
//            for (TreeItem tItem : treeItems) {
//                resultStr+="\n   ";
//                resultStr+=tItem.toDhtmlTreeString();
//                resultStr+="\n";
//            }
//        }

        return sb.toString();
    }
}
