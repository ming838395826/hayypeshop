package com.happyshop.common;

import java.util.List;

/**
 * Created by Administrator on 2018/1/23.
 */
public class EasyUiResponse {

    private int total;

    private List rows;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }
}
