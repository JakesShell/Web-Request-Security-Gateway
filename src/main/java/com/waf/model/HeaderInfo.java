package com.waf.model;

public class HeaderInfo {
    private final String name;
    private final String value;
    private final String purpose;
    private final String status;

    public HeaderInfo(String name, String value, String purpose, String status) {
        this.name = name;
        this.value = value;
        this.purpose = purpose;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getStatus() {
        return status;
    }
}
