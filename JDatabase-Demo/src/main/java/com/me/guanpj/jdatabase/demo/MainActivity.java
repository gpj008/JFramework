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
        Developer developer1 = new Developer();
        developer1.setId("00001");
        developer1.setName("Jie");
        developer1.setAge(17);

        Skill skill1 = new Skill();
        skill1.setName("coding");
        skill1.setDesc("android");

        Skill skill2 = new Skill();
        skill2.setName("sport");
        skill2.setDesc("basketball");
        ArrayList<Skill> skills1 = new ArrayList<Skill>();
        skills1.add(skill1);
        skills1.add(skill2);
        developer1.setSkills(skills1);

        Developer developer2 = new Developer();
        developer2.setId("00002");
        developer2.setName("guanpj");
        developer2.setAge(18);

        Skill skill3 = new Skill();
        skill3.setName("coding");
        skill3.setDesc("java");

        ArrayList<Skill> skills2 = new ArrayList<Skill>();
        skills2.add(skill3);
        developer2.setSkills(skills2);

        ArrayList<Developer> developers = new ArrayList<>();
        developers.add(developer1);
        developers.add(developer2);

        Company company = new Company();
        company.setId("001");
        company.setName("gpj");
        company.setUrl("www.guanpj.me.com");
        company.setTel("10086");
        company.setAddress("Shenzhen");
        company.setDevelopers(developers);

        DBManager.getInstance().newOrUpdate(company);
    }

    public void queryCompanyById() {
        Company company = DBManager.getInstance().queryById(Company.class, "001");
        if (company != null) {
            Trace.e(company.toString());
        }
    }

    public void deleteCompanyById() {
        Company company = new Company();
        company.setId("001");
        DBManager.getInstance().delete(company);
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
