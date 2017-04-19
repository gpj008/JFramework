package com.me.guanpj.jdatabase.demo.model;

import java.io.Serializable;

/**
 * Created by Jie on 2017/4/19.
 */

public class Skill implements Serializable{
    private static final long serialVersionUID = 1L;

    private String name;
    private String desc;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
}
