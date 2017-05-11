package com.hframework.beans.tagbean;
import java.util.List;   
public class Pagination {   
    // 总共的数据量 ----------从数据中查询出来
    private int totle;  
    // 每页显示多少条   ---------页面默认多少条
    private int pageSize;   
    // 共有多少页   
    private int totlePage;   
    // 当前是第几页   ----------页面传入
    private int index;   
    // 数据   
    private List data;   
    // 连接路径   
    private String path;   
    public void setTotle(int totle) {   
        this.totle = totle;   
    }   
    public void setPageSize(int pageSize) {   
        this.pageSize = pageSize;   
    }   
    public void setIndex(int index) {   
        this.index = index;   
    }   
    public void setPath(String path) {   
        this.path = path;   
    }   
    public int getTotle() {   
        return totle;   
    }   
    public int getPageSize() {   
        return pageSize;   
    }   
    public int getTotlePage() {   
        return (this.totle + this.pageSize - 1) / this.pageSize;   
    }   
    public int getIndex() {   
        return index;   
    }   
    public List getData() {   
        return data;   
    }   
    public void setData(List data) {   
        this.data = data;   
    }   
    public String getPageDisplay() {   
        StringBuffer displayInfo = new StringBuffer();   
        if (index == 0 || pageSize == 0) {   
            displayInfo.append("没有分页的信息!");   
        } else {   
            displayInfo.append("<div class='pager'>");   
            displayInfo.append("共" + totle + "条记录每页<span style=\"color:#FF0000\" mce_style=\"color:#FF0000\">" + pageSize   
                    + "</span>条");   
            displayInfo.append("第<span style=\"color:#FF0000\" mce_style=\"color:#FF0000\">" + index   
                    + "</span>页/共"   
                    + this.getTotlePage() + "页");   
            // 判断如果当前是第一页 则“首页”和“第一页”失去链接   
            if (index == 1) {   
                displayInfo.append("  首页 ");   
                displayInfo.append("上一页 ");   
            } else {   //javascript:refreshList(222,2,10);
                displayInfo.append("  <a href=\"" + path   
                        + "index=1\" mce_href=\"" + path   
                        + "index=1\">首页</a> ");   
                displayInfo.append("<a href=\"" + path + "index=" + (index - 1)   
                        + "\" mce_href=\"" + path + "index=" + (index - 1)   
                        + "\">上一页</a> ");   
            }   
            if (index >= this.getTotlePage()) {   
                displayInfo.append("下一页 ");   
                displayInfo.append("最后一页 ");   
            } else {   
                displayInfo.append("<a href=\"" + path + "index=" + (index + 1)   
                        + "\" mce_href=\"" + path + "index=" + (index + 1)   
                        + "\">下一页</a> ");   
                displayInfo.append("<a href=\"" + path + "index="   
                        + this.getTotlePage() + "\" mce_href=\"" + path + "index="   
                        + this.getTotlePage() + "\">最后一页</a> ");   
            }   
            displayInfo.append("</div>");   
        }   
        return displayInfo.toString();   
    }   
    public String getPageDisplay(String tagId) {   
        StringBuffer displayInfo = new StringBuffer();   
        if (index == 0 || pageSize == 0) {   
            displayInfo.append("没有分页的信息!");   
        } else {   
        	displayInfo.append("<div style=\"float:left;\">共" + totle + "条记录,每页");   
        	displayInfo.append("   <span style=\"color: #FF0000\" mce_style=\"color:#FF0000\"> " + pageSize + " </span>条,第");   
        	displayInfo.append("   <span style=\"color: #FF0000\" mce_style=\"color:#FF0000\">" + index   
                    + "</span>页/共" + this.getTotlePage() + "页");
        	displayInfo.append("</div>");
        	
            // 判断如果当前是第一页 则“首页”和“第一页”失去链接   
            displayInfo.append("<div style=\"float:right;\">");   
            displayInfo.append("<nav>");
            displayInfo.append("  <ul class=\"pagination\"  style = \"margin:0px;\">");
            
			if (index == 1) {   
				displayInfo.append("    <li class=\"disabled\">");
	    		displayInfo.append("      <a href=\"javascript:refreshList('"+tagId+"',1,"+pageSize+");\" aria-label=\"Previous\">");
				displayInfo.append("        <span aria-hidden=\"true\">|&laquo;</span>");
				displayInfo.append("      </a>");
				displayInfo.append("    </li>");
				displayInfo.append("    <li class=\"disabled\">");
	    		displayInfo.append("      <a href=\"javascript:refreshList('"+tagId+"',"+(index-1)+","+pageSize+");\" aria-label=\"Previous\">");
				displayInfo.append("        <span aria-hidden=\"true\">&laquo;</span>");
				displayInfo.append("      </a>");
				displayInfo.append("    </li>");
            } else {   
            	displayInfo.append("    <li>");
	    		displayInfo.append("      <a href=\"javascript:refreshList('"+tagId+"',1,"+pageSize+");\" aria-label=\"Previous\">");
				displayInfo.append("        <span aria-hidden=\"true\">|&laquo;</span>");
				displayInfo.append("      </a>");
				displayInfo.append("    </li>");
				displayInfo.append("    <li>");
	    		displayInfo.append("      <a href=\"javascript:refreshList('"+tagId+"',"+(index-1)+","+pageSize+");\" aria-label=\"Previous\">");
				displayInfo.append("        <span aria-hidden=\"true\">&laquo;</span>");
				displayInfo.append("      </a>");
				displayInfo.append("    </li>");
            }   
			
            displayInfo.append("  </ul>");
            displayInfo.append("  <ul class=\"pagination\"  style = \"margin:0px;\">");

            int[] indexs = getIndexList(index,getTotlePage());
            
            for(int i = indexs[0];i<=indexs[1];i++) {
            	
            	if(i == index) {
            		displayInfo.append("    <li  class=\"disabled\"><a href=\"javascript:void(0);\" >"+i+"</a></li>");
            	}else{
            		displayInfo.append("    <li><a href=\"javascript:refreshList('"+tagId+"',"+i+","+pageSize+");\">"+i+"</a></li>");
            	}
            }

            displayInfo.append("  </ul>");
            displayInfo.append("  <ul class=\"pagination\"  style = \"margin:0px;\">");
			
            if (index == this.getTotlePage()) { 
            	displayInfo.append("    <li class=\"disabled\">");
    			displayInfo.append("      <a href=\"javascript:refreshList('"+tagId+"',"+(index+1)+","+pageSize+");\" aria-label=\"Next\">");
    			displayInfo.append("        <span aria-hidden=\"true\">&raquo;</span>");
    			displayInfo.append("      </a>");
    			displayInfo.append("   </li >");
    			
    			displayInfo.append("    <li class=\"disabled\">");
    			displayInfo.append("      <a href=\"javascript:refreshList('"+tagId+"',"+getTotlePage()+","+pageSize+");\" aria-label=\"Next\">");
    			displayInfo.append("        <span aria-hidden=\"true\">&raquo;|</span>");
    			displayInfo.append("      </a>");
    			displayInfo.append("   </li>");
            } else {   
            	displayInfo.append("    <li>");
    			displayInfo.append("      <a href=\"javascript:refreshList('"+tagId+"',"+(index+1)+","+pageSize+");\" aria-label=\"Next\">");
    			displayInfo.append("        <span aria-hidden=\"true\">&raquo;</span>");
    			displayInfo.append("      </a>");
    			displayInfo.append("   </li>");
    			
    			displayInfo.append("    <li>");
    			displayInfo.append("      <a href=\"javascript:refreshList('"+tagId+"',"+getTotlePage()+","+pageSize+");\" aria-label=\"Next\">");
    			displayInfo.append("        <span aria-hidden=\"true\">&raquo;|</span>");
    			displayInfo.append("      </a>");
    			displayInfo.append("   </li>");
            }   
			
			displayInfo.append("  </ul>");
			displayInfo.append("</nav>	");
			displayInfo.append("</div>");   
//          displayInfo.append("<div class='pager'>");   
//          displayInfo.append("共" + totle + "条记录每页<span style=\"color:#FF0000\" mce_style=\"color:#FF0000\">" + pageSize   
//                  + "</span>条");   
//          displayInfo.append("第<span style=\"color:#FF0000\" mce_style=\"color:#FF0000\">" + index   
//                  + "</span>页/共"   
//                  + this.getTotlePage() + "页");   
//            
//            if (index == 1) {   
//                displayInfo.append("  首页 ");   
//                displayInfo.append("上一页 ");   
//            } else {   
//                displayInfo.append("  <a href=\"javascript:refreshList('"+tagId+"',1,"+pageSize+");\">首页</a> ");   
//                displayInfo.append("  <a href=\"javascript:refreshList('"+tagId+"',"+(index-1)+","+pageSize+");\">上一页</a> ");   
//            }   
//            if (index >= this.getTotlePage()) {   
//                displayInfo.append("下一页 ");   
//                displayInfo.append("最后一页 ");   
//            } else {   
//                displayInfo.append("  <a href=\"javascript:refreshList('"+tagId+"',"+(index+1)+","+pageSize+");\">下一页</a> ");   
//                displayInfo.append("  <a href=\"javascript:refreshList('"+tagId+"',"+getTotlePage()+","+pageSize+");\">最后一页</a> ");   
//            }   
//            displayInfo.append("</div>");   
        }   
        return displayInfo.toString();   
    } 
    
    
    private int[] getIndexList(int index, int totalPages) {

    	int startNum = index<3?1:index-2;
    	
    	int leave = 2-(index-startNum);
    	
    	int endNum = index+2+leave<totalPages?index+2+leave:totalPages;
    	
    	return new int[] {startNum,endNum};
	}
	public static void main(String[] args) {
    	Pagination p=new Pagination();
    	p.setIndex(1);
    	p.setPageSize(10);
    	p.setTotle(100);
		System.out.println( p.getPageDisplay());
	}
}     
