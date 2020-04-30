package com.icloud.basecommon.model;


import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 查询参数
 */
public class Query extends LinkedHashMap<String, Object> {
	private static final long serialVersionUID = 1L;
	//当前页码
    private int pageNum = 1;
    //每页条数
    private int pageSize = 10;

    public Query(Map<String, Object> params){
        this.putAll(params);
        //分页参数
        if(params.get("pageNum")!=null) {
            this.pageNum = Integer.parseInt(params.get("pageNum").toString());
        }
        if(params.get("pageNo")!=null) {
            this.pageNum = Integer.parseInt(params.get("pageNo").toString());
        }
        if(params.get("pageSize")!=null) {
            this.pageSize = Integer.parseInt(params.get("pageSize").toString());
        }
        this.remove("pageNo");
        this.remove("pageNum");
        this.remove("pageSize");
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
