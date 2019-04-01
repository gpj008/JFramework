package com.me.guanpj.jdatabase.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.me.guanpj.jdatabase.DatabaseManager;
import com.me.guanpj.jdatabase.demo.model.Company;
import com.me.guanpj.jdatabase.demo.model.Developer;
import com.me.guanpj.jdatabase.demo.model.Skill;
import com.me.guanpj.jdatabase.utility.Trace;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.mDbAddBtn).setOnClickListener(this);
        findViewById(R.id.mDbDeleteBtn).setOnClickListener(this);
        findViewById(R.id.mDbQueryBtn).setOnClickListener(this);
        myHook(findViewById(R.id.mDbAddBtn));
        DatabaseManager.init(getApplicationContext(), new DatabaseHelper(getApplicationContext()));
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

        //DBManager.getInstance().newOrUpdate(company);
        DatabaseManager.getInstance().getDao(Company.class).newOrUpdate(company);
    }

    public void queryCompanyById() {
        //Company company = DBManager.getInstance().queryById(Company.class, "001");
        Company company = DatabaseManager.getInstance().getDao(Company.class).queryById("001");
        if (company != null) {
            Trace.e(company.toString());
        }
    }

    public void deleteCompanyById() {
        Company company = new Company();
        company.setId("001");
        //DBManager.getInstance().delete(company);
        DatabaseManager.getInstance().getDao(Company.class).delete(company);
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

    private void hookOnClickListener(View view) {
        try {
            // 得到 View 的 ListenerInfo 对象
            Method getListenerInfo = View.class.getDeclaredMethod("getListenerInfo");
            getListenerInfo.setAccessible(true);
            Object listenerInfo = getListenerInfo.invoke(view);
            // 得到 原始的 OnClickListener 对象
            Class<?> listenerInfoClz = Class.forName("android.view.View$ListenerInfo");
            Field mOnClickListener = listenerInfoClz.getDeclaredField("mOnClickListener");
            mOnClickListener.setAccessible(true);
            View.OnClickListener originOnClickListener = (View.OnClickListener) mOnClickListener.get(listenerInfo);
            // 用自定义的 OnClickListener 替换原始的 OnClickListener
            View.OnClickListener hookedOnClickListener = new HookedOnClickListener(originOnClickListener);
            mOnClickListener.set(listenerInfo, hookedOnClickListener);
        } catch (Exception e) {
            Log.e("gpj","hook clickListener failed!");
        }
    }

    private void myHook(View view) {
        try {
            Method getListenerInfo = Class.forName("android.view.View").getDeclaredMethod("getListenerInfo");
            getListenerInfo.setAccessible(true);
            Object listener = getListenerInfo.invoke(view);

            Class<?> listenerInfoClz = Class.forName("android.view.View$ListenerInfo");
            Field onClickListener = listenerInfoClz.getDeclaredField("mOnClickListener");
            View.OnClickListener originalListener = (View.OnClickListener) onClickListener.get(listener);

            View.OnClickListener hookedListener = new HookedOnClickListener(originalListener);
            onClickListener.set(listener, hookedListener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class HookedOnClickListener implements View.OnClickListener {
        private View.OnClickListener origin;

        HookedOnClickListener(View.OnClickListener origin) {
            this.origin = origin;
        }

        @Override
        public void onClick(View v) {
            Log.e("gpj","Before click, do what you want to to.");
            if (origin != null) {
                origin.onClick(v);
            }
            Log.e("gpj","After click, do what you want to to.");
        }
    }
}
