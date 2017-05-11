package com.hframework.beans.controller;
import java.util.List;

/**
 * 分页
 */
public class Pagination {
    // 总记录数
    private int totalCount;
    // 页编码
    private int pageNo;
    // 页大小
    private int pageSize;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage(){
        return this.totalCount / this.pageSize +  (this.totalCount % this.pageSize > 0 ? 1 : 0);
    }

    public int getStartIndex(){
        return ( pageNo-1 ) * pageSize;
    }
    public int getEndIndex(){
        return pageSize;
    }

    public static class PagerUtils {
        public static  <T> List<T> page(List<T> list, int pageNo, int pageSize) {
            int beginIndex = pageSize == -1 ? 0 : ((pageNo-1)*pageSize > list.size() ? list.size() : (pageNo-1)*pageSize );
            int endIndex = pageSize == -1 ? list.size() : (pageNo*pageSize > list.size() ? list.size() : pageNo*pageSize);

            return list.subList(beginIndex, endIndex);
        }
    }
}
