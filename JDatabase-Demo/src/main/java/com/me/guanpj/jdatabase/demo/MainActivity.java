package com.me.guanpj.jdatabase.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.me.guanpj.jdatabase.DBManager;
import com.me.guanpj.jdatabase.demo.model.Company;
import com.me.guanpj.jdatabase.demo.model.Developer;
import com.me.guanpj.jdatabase.demo.model.Skill;
import com.me.guanpj.jdatabase.utility.Trace;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.mDbAddBtn).setOnClickListener(this);
        findViewById(R.id.mDbDeleteBtn).setOnClickListener(this);
        findViewById(R.id.mDbQueryBtn).setOnClickListener(this);
        DBManager.init(getApplicationContext(), new DatabaseHelper(getApplicationContext()));
    }

    public void add() {
        Developer developer = new Developer();
        developer.setId("00001");
        developer.setName("Jie");
        developer.setAge(17);

        Skill skill1 = new Skill();
        skill1.setName("coding");
        skill1.setDesc("android");

        Skill skill2 = new Skill();
        skill2.setName("sport");
        skill2.setDesc("basketball");
        ArrayList<Skill> skills = new ArrayList<Skill>();
        skills.add(skill1);
        skills.add(skill2);
        developer.setSkills(skills);

        Company company = new Company();
        company.setId("001");
        company.setName("gpj");

        developer.setCompany(company);

        DBManager.getInstance().newOrUpdate(developer);
    }

    public void queryCompanyById() {
        Developer developer = DBManager.getInstance().queryById(Developer.class, "00001");
        if (developer != null) {
            Trace.e(developer.toString());
        }
    }

    public void deleteCompanyById() {
        Developer developer = new Developer();
        developer.setId("00001");
        DBManager.getInstance().delete(developer);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mDbAddBtn:
                add();
                break;
            case R.id.mDbQueryBtn:
                queryCompanyById();
                break;
            case R.id.mDbDeleteBtn:
                deleteCompanyById();
                break;
        }
    }
}
