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
import com.terry.account.util.SingleToast;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

public class ColumnChartActivity extends AppCompatActivity {

    GdApp mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_column_chart);

        mApp = (GdApp) getApplication();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String titleStr = getIntent().getStringExtra("title");
        getSupportActionBar().setTitle(titleStr);

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
     * A fragment containing a column chart.
     */
    public static class PlaceholderFragment extends Fragment {

        private ColumnChartView chart;
        private ColumnChartData data;

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
            View rootView = inflater.inflate(R.layout.fragment_column_chart, container, false);

            chart = (ColumnChartView) rootView.findViewById(R.id.chart);
            chart.setOnValueTouchListener(new ValueTouchListener());
            chart.setValueSelectionEnabled(true);

            generateData();

            return rootView;
        }


        private void generateData() {
            float numSubcolumns = 1;
            int numColumns = mAllList.size();

            List<Column> columns  = new ArrayList<>();
            List<SubcolumnValue> values;
            List<AxisValue> axisValues = new ArrayList<>();
            for (int i = 0; i < numColumns; i++) {
                values  = new ArrayList<>();
                AccountBean accountBean = mAllList.get(i);
                for (int j = 0; j < numSubcolumns; ++j) {
                    Float num;
                    String lable;
                    int color;
                    if(accountBean.getAccountType().equals("outcome")){
                        //num = -Integer.valueOf(accountBean.getMoney());
                        num = -Float.valueOf(accountBean.getMoney());
                        lable = accountBean.getType()+"\n"+(-num)+"元";
                        color = ChartUtils.COLOR_RED;
                    }else{
                        //num = Integer.valueOf(accountBean.getMoney());
                        num = Float.valueOf(accountBean.getMoney());
                        lable = accountBean.getType()+"\n"+num+"元";
                        color = ChartUtils.COLOR_GREEN;
                    }

                    SubcolumnValue subcolumnValue = new SubcolumnValue(num,color);
                    subcolumnValue.setLabel(lable);
                    values.add(subcolumnValue);

                }
                axisValues.add(new AxisValue(i).setLabel(accountBean.getTime()));
                Column column = new Column(values);
                column.setHasLabels(true);
                columns.add(column);
            }
            data = new ColumnChartData(columns);


            Axis dataAxis = new Axis(axisValues).setName("收支时间").setHasLines(true).setMaxLabelChars(numColumns)
                    .setTextColor(ChartUtils.COLOR_RED);
            data.setAxisXBottom(dataAxis);


            Axis axisY = new Axis().setHasLines(true);
            axisY.setName("收支金额");
            data.setAxisYLeft(axisY);
            chart.setColumnChartData(data);

        }


        private class ValueTouchListener implements ColumnChartOnValueSelectListener {

            @Override
            public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
                SingleToast.showToast(getActivity(),  String.valueOf(value.getLabelAsChars()),2000);
            }

            @Override
            public void onValueDeselected() {

            }

        }

    }
}
