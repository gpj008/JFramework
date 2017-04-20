package com.me.guanpj.jdatabase.demo.model;

import com.me.guanpj.jdatabase.annotation.Column;
import com.me.guanpj.jdatabase.annotation.Table;
import com.me.guanpj.jdatabase.utility.JsonUtil;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Jie on 2017/4/19.
 */

@Table(name = "developer")
public class Developer implements Serializable{
    @Column(id = true, name = "developer_id")
    private String id;
    @Column(name = "developer_name")
    private String name;
    @Column
    private int age;
    @Column(type = Column.ColumnType.SERIALIZABLE)
    private ArrayList<Skill> skills;
    @Column(type = Column.ColumnType.TONE, autofresh = true)
    private Company company;

   public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public ArrayList<Skill> getSkills() {
        return skills;
    }

    public void setSkills(ArrayList<Skill> skills) {
        this.skills = skills;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "id:" + id + ",name:" + name + ",age:" + age + ",skills:" + JsonUtil.toJson(skills) + ",company:" + (company != null ? company
                .toString() : "null");
    }
}
