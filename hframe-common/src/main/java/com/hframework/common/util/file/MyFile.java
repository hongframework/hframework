package com.hframework.common.util.file;

/**
 * 服务器中，指定路径的文件映射而成的文件对象
 * @author zhangqh6
 *
 */
public class MyFile {
	
	private String id;
	private String pid;
	
	private String filename;
	private String fullname;

	private String shortname;
	
	private long pri;
	
	
	public MyFile() {
		super();
	}

	public MyFile(String id, String pid, String fileName) {
		super();
		this.id = id;
		this.pid = pid;
		this.filename = fileName;
	}
	
	public MyFile(String id, String pid, String fileName,long pri) {
		super();
		this.id = id;
		this.pid = pid;
		this.filename = fileName;
		this.pri = pri;
	}
	
	public MyFile(String id, String pid, String fileName,String fullName,long pri) {
		super();
		this.id = id;
		this.pid = pid;
		this.filename = fileName;
		this.fullname = fullName;
		this.pri = pri;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public long getPri() {
		return pri;
	}

	public void setPri(long pri) {
		this.pri = pri;
	}


	public String getShortname() {
		return shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}
}
