package com.icalinks.mobile.ui.model;

public class UpgradeInfo {
    public String appName;// 应用名称
    public int apkSize;// apk文件大小(单位KB)
    public String chkCode;// md5验证码
    public String fileUrl;// 文件下载地址
    public String pubDate;// 2011-3-28
    public String summary;// 新版功能
    public int verCode;// 版本号
    public String verName;// 版本名
    public int upgType;// 升级类型(是否强制升级)
    
    
    public static final int UPGRADE_TYPE_TRUE=1;
    public static final int UPGRADE_TYPE_FALSE=0;
}
