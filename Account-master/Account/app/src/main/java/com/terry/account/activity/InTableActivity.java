package com.terry.account.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.utils.DensityUtils;
import com.terry.account.GdApp;
import com.terry.account.R;
import com.terry.account.bean.AccountBean;
import com.terry.account.bean.InTableBean;
import com.terry.account.util.C;

import java.util.ArrayList;
import java.util.List;

public class InTableActivity extends AppCompatActivity {

    private SmartTable<InTableBean> table;

    private  GdApp mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotation);
        FontStyle.setDefaultTextSize(DensityUtils.sp2px(this, 15));

        mApp = (GdApp) getApplication();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("一年支出报表");


        final List<InTableBean> inTableBeans = new ArrayList<>();


        for(AccountBean accountBean: mApp.mAccBeanYearColumnList){
            if(accountBean.getFromType().equals(C.COME_TYPE.IN)){
                inTableBeans.add(InTableBean.getInTableBean(accountBean));
            }

        }
        table = (SmartTable<InTableBean>) findViewById(R.id.table);
        table.setData(inTableBeans);
        table.getConfig().setShowTableTitle(true);
        table.getConfig().setShowXSequence(true);
        table.getConfig().setShowYSequence(true);
        table.setZoom(true, 2, 0.2f);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
