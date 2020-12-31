package com.terry.account.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import com.terry.account.GdApp;
import com.terry.account.R;
import com.terry.account.bean.AccountBean;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

public class PieChartActivity extends AppCompatActivity {

    GdApp mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String titleStr = getIntent().getStringExtra("title");
        getSupportActionBar().setTitle(titleStr);

        mApp = (GdApp) getApplication();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment().setList(mApp.mAccBeanChartList)).commit();
        }
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

    /**
     * A fragment containing a pie chart.
     */
    public static class PlaceholderFragment extends Fragment {

        private PieChartView chart;
        private PieChartData data;

        List<AccountBean> mAllList = new ArrayList<>();

        public PlaceholderFragment() {
        }

        public PlaceholderFragment setList(List<AccountBean> allList) {
            mAllList = allList;
            return this;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            setHasOptionsMenu(true);
            View rootView = inflater.inflate(R.layout.fragment_pie_chart, container, false);
            chart = (PieChartView) rootView.findViewById(R.id.chart);
            chart.setOnValueTouchListener(new ValueTouchListener());
            generateData();
            return rootView;
        }


        private void generateData() {
            int numColumns = mAllList.size();
            List<SliceValue> values = new ArrayList<>();

            float totalMoney = 0f;
            for (int i = 0; i < numColumns; ++i) {
                AccountBean accountBean = mAllList.get(i);
                totalMoney = totalMoney+Float.parseFloat(accountBean.getMoney());
            }

            for (int i = 0; i < numColumns; ++i) {
                AccountBean accountBean = mAllList.get(i);
                SliceValue sliceValue = new SliceValue(Float.parseFloat(accountBean.getMoney()), ChartUtils.pickColor());
                sliceValue.setLabel(accountBean.getType()+"\n"+ Float.parseFloat(accountBean.getMoney())/totalMoney*100+"%");
                values.add(sliceValue);
            }

            data = new PieChartData(values);
            data.setHasLabels(true);
            data.setHasCenterCircle(false);
            data.setCenterText1("");
            chart.setPieChartData(data);
        }



        private class ValueTouchListener implements PieChartOnValueSelectListener {

            @Override
            public void onValueSelected(int arcIndex, SliceValue value) {
//                Toast.makeText(getActivity(), "Selected: " + value, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onValueDeselected() {

            }

        }
    }
}
