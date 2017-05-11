package com.hframework.beans.tagbean;

import java.util.ArrayList;
import java.util.List;

/**
 * 一个树形组件的项目内容
 * @author zhangqh6
 *
 */
public class TreeItem {

	private String id;
	private String pid;
	private String iid;
	private String value;
	private String text;
	private String open;
	private String select;
	private String url;//暂缺
	
	private String call;
	private String img0;
	private String img1;
	private String img2;
	
	private List<TreeItem> treeItems;
	
	public TreeItem() {
		super();
	}
	public TreeItem(String id,String pid,String iid, String value, String text, String open,
			String select, String url, String call, String img0, String img1,
			String img2) {
		super();
		this.id = id;
		this.pid = pid;
		this.iid = iid;
		this.value = value;
		this.text = text;
		this.open = open;
		this.select = select;
		this.url = url;
		this.call = call;
		this.img0 = img0;
		this.img1 = img1;
		this.img2 = img2;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getOpen() {
		return open;
	}
	public void setOpen(String open) {
		this.open = open;
	}
	public String getSelect() {
		return select;
	}
	public void setSelect(String select) {
		this.select = select;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getCall() {
		return call;
	}
	public void setCall(String call) {
		this.call = call;
	}
	public String getImg0() {
		return img0;
	}
	public void setImg0(String img0) {
		this.img0 = img0;
	}
	public String getImg1() {
		return img1;
	}
	public void setImg1(String img1) {
		this.img1 = img1;
	}
	public String getImg2() {
		return img2;
	}
	public void setImg2(String img2) {
		this.img2 = img2;
	}
	
	

	
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getIid() {
		return iid;
	}
	public void setIid(String iid) {
		this.iid = iid;
	}
	public List<TreeItem> getTreeItems() {
		return treeItems;
	}
	public void setTreeItems(List<TreeItem> treeItems) {
		this.treeItems = treeItems;
	}
	public void addItem(TreeItem treeItem){
		if(treeItems==null){
			treeItems=new ArrayList<TreeItem>();
		}
		treeItems.add(treeItem);
	}
	
	public String toDhtmlTreeString(){
		String resultStr="<item";
		if(text!=null&&!"".equals(text)){
			resultStr+=" text=\""+this.text+"\"";
		}
		
		if(iid!=null&&!"".equals(iid)){
			resultStr+=" id=\""+this.iid+"\"";
		}
		if(open!=null&&!"".equals(open)){
			resultStr+=" open=\""+this.open+"\"";
		}
		if(img0!=null&&!"".equals(img0)){
			resultStr+=" img0=\""+this.img0+"\"";
		}
		if(img1!=null&&!"".equals(img1)){
			resultStr+=" img1=\""+this.img1+"\"";
		}
		if(img2!=null&&!"".equals(img2)){
			resultStr+=" img2=\""+this.img2+"\"";
		}
		if(call!=null&&!"".equals(call)){
			resultStr+=" call=\""+this.call+"\"";
		}

		if(select!=null&&!"".equals(select)){
			resultStr+=" select=\""+this.select+"\"";
		}
		resultStr+=">";
		
		if(url!=null&&!"".equals(url)){
			resultStr+="\n   <userdata name=\"url\">"+url+"</userdata> \n  ";
		}
		
		if(treeItems!=null&&treeItems.size()>0){
			for (TreeItem tItem : treeItems) {
				resultStr+="\n   ";
				resultStr+=tItem.toDhtmlTreeString();
				resultStr+="\n";
			}
		}
		resultStr+="</item>";

		return resultStr;
	}
	
//	public static void main(String[] args) {
//		TreeItem tItem=new TreeItem("id","pid","iid","value","哈哈",null,null,null,null,null,null,null);
//		tItem.addItem(new TreeItem("111","111","111","111","哈哈",null,null,null,null,null,null,null));
//		System.out.println(tItem.toDhtmlTreeString());
//
//	}

	public static void main(String[] args) {
		TreeItem tItem=new TreeItem("id","pid","iid","value","哈哈",null,null,null,null,null,null,null);
		tItem.addItem(new TreeItem("111","111","111","111","哈哈",null,null,null,null,null,null,null));
		System.out.println(tItem.toDhtmlTreeString());

	}
}
