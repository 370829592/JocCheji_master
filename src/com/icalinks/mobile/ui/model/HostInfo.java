package com.icalinks.mobile.ui.model;

public class HostInfo {
    private String name;
    private String port;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPort() {
        return port;
    }

    public HostInfo() {
        super();
    }

    public HostInfo(String name, String port) {
        super();
        this.name = name;
        this.port = port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
