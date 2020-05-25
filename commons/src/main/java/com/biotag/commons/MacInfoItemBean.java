package com.biotag.commons;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class MacInfoItemBean {
    @Id
    public long id;
    public String mac;
    public String date;

    public MacInfoItemBean() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
