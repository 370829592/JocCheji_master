package com.icalinks.mobile.ui.model;

/**
 * 保养记录详细信息
 * 
 * @author guogzhao
 * 
 */

public class InfoRecordInfo {
    /**
     * 保养时间
     */
    private String date;

    /**
     * 保养内容
     */
    private String content;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTuition() {
        return tuition;
    }

    public void setTuition(String tuition) {
        this.tuition = tuition;
    }

    public String getNumofkm() {
        return numofkm;
    }

    public void setNumofkm(String numofkm) {
        this.numofkm = numofkm;
    }

    /**
     * 本次保养消费
     */
    private String tuition;
    /**
     * 保养公里数
     */
    private String numofkm;

}
