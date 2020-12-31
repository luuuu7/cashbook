package com.terry.account.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.codetroopers.betterpickers.expirationpicker.ExpirationPickerBuilder;
import com.codetroopers.betterpickers.expirationpicker.ExpirationPickerDialogFragment;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.terry.account.R;
import com.terry.account.activity.PieChartActivity;
import com.terry.account.activity.TabsActivity;
import com.terry.account.adapter.AccountAdapter;
import com.terry.account.base.BaseSwitchFragment;
import com.terry.account.bean.AccountBean;
import com.terry.account.bean.TypeBean;
import com.terry.account.db.DBProvider;
import com.terry.account.util.C;
import com.terry.account.util.SingleToast;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * 月度资产页面
 *
 * @author sjy
 */
public class MonthFragment extends BaseSwitchFragment implements AccountAdapter.OnAccountClick
        , ExpirationPickerDialogFragment.ExpirationPickerDialogHandler {

    private static final String TAG = MonthFragment.class.getSimpleName();

    private AccountAdapter mAdapter;
    private static MonthFragment instance;
    private TabsActivity menuActivity;

    private View rootView;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionMenu mFabActMenu;

    private TextView mTxTime;
    private TextView mTxTotal;
    private TextView mTxQryType;

    private String qryTime;
    private String mQryAccountType = "";//支出还是收入
    private String mQryDetailType = "";//支出或者收入的具体项

    List<AccountBean> mAllList = new ArrayList<>();


    public static MonthFragment getInstance() {
        if (instance == null) instance = new MonthFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_day, container, false);
        initViews();
        menuActivity = (TabsActivity) mAct;

        Calendar localCalendar = Calendar.getInstance();
        int year = localCalendar.get(Calendar.YEAR);
        int month = localCalendar.get(Calendar.MONTH);
        if(month+1<10){
            qryTime = year + "-0" + (month + 1);
        }else{
            qryTime = year + "-" + (month + 1);
        }
        mTxTime.setText("查询时间:  " + qryTime);
        mTxQryType.setText("账单类型:所有");

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        doRefresh();
    }


    private void initViews() {
        initFloatBtn();

        mTxTime = (TextView) rootView.findViewById(R.id.tx_qry_time);
        mTxTotal = (TextView) rootView.findViewById(R.id.tx_total);
        mTxQryType = (TextView) rootView.findViewById(R.id.tx_type);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mAct));

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.theme_accent));

        //下拉刷新
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });

    }

    private void initFloatBtn() {
        mFabActMenu = (FloatingActionMenu) rootView.findViewById(R.id.fab_normal);

        FloatingActionButton fbChoseDay = new FloatingActionButton(mAct);
        fbChoseDay.setButtonSize(FloatingActionButton.SIZE_MINI);
        fbChoseDay.setLabelText(getString(R.string.fl_menu_chosemonth));
        fbChoseDay.setImageDrawable(new IconicsDrawable(mAct, GoogleMaterial.Icon.gmd_view_day).color(Color.WHITE).actionBar());
        fbChoseDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择查看的月份
                showMonthChose();
                mFabActMenu.close(true);
            }
        });
        mFabActMenu.addMenuButton(fbChoseDay);

        FloatingActionButton sortFb = new FloatingActionButton(mAct);
        sortFb.setButtonSize(FloatingActionButton.SIZE_MINI);
        sortFb.setLabelText(getString(R.string.fl_menu_sort));
        sortFb.setImageDrawable(new IconicsDrawable(mAct, GoogleMaterial.Icon.gmd_sort).color(Color.WHITE).actionBar());
        sortFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择查看的大类
                showSortFirstList();
                mFabActMenu.close(true);
            }
        });
        mFabActMenu.addMenuButton(sortFb);


        FloatingActionButton fbChart = new FloatingActionButton(mAct);
        fbChart.setButtonSize(FloatingActionButton.SIZE_MINI);
        fbChart.setLabelText(getString(R.string.fl_menu_chart));
        fbChart.setImageDrawable(new IconicsDrawable(mAct, GoogleMaterial.Icon.gmd_star).color(Color.WHITE).actionBar());
        fbChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查看图表
                mFabActMenu.close(false);
                if (mAllList.isEmpty()) {
                    SingleToast.showToast(mAct, "请先添加数据", 2000);
                } else {
                    showChartList();
                }
            }
        });
        mFabActMenu.addMenuButton(fbChart);
        mFabActMenu.setClosedOnTouchOutside(true);
    }


    /**
     * 展示图表选择操作选项
     */
    private void showChartList() {
        new MaterialDialog.Builder(mAct)
                .title(R.string.acount_handle_title)
                .items(R.array.chart_handle)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        dialog.dismiss();
                        switch (which) {
                            case 0://收入统计
                                mApp.mAccBeanChartList.clear();
                                for(AccountBean accountBean:mAllList){
                                    if(accountBean.getAccountType().equals(C.COME_TYPE.IN)){
                                        mApp.mAccBeanChartList.add(accountBean);
                                    }
                                }
                                Intent itChar = new Intent(mAct, PieChartActivity.class);
                                itChar.putExtra("title",qryTime+"收入统计图表");
                                startActivity(itChar);
                                break;
                            case 1://支出统计
                                mApp.mAccBeanChartList.clear();
                                for(AccountBean accountBean:mAllList){
                                    if(accountBean.getAccountType().equals(C.COME_TYPE.OUT)){
                                        mApp.mAccBeanChartList.add(accountBean);
                                    }
                                }
                                Intent itCharOut = new Intent(mAct, PieChartActivity.class);
                                itCharOut.putExtra("title",qryTime+"支出统计图表");
                                startActivity(itCharOut);
                                break;
                        }
                    }
                })
                .show();
    }


    /**
     * 下拉刷新数据
     */
    private void doRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        String minTime = qryTime+"-01";
        String maxTime = qryTime+"-31";
        mAllList =  DBProvider.getInstance(mAct).getAccountFromDBVague(mApp.getCurrentUser(),minTime,maxTime,mQryAccountType,mQryDetailType);
        mAdapter = new AccountAdapter(mAct, mAllList, MonthFragment.this);
        mRecyclerView.setAdapter(mAdapter);
        //此次刷新结束
        mSwipeRefreshLayout.setRefreshing(false);
        calculateSum();

        //用于第二TAB页展示的柱状图
        mApp.mAccBeanInPieList.clear();
        mApp.mAccBeanOutPieInList.clear();
        mApp.mAccBeanInPieList = DBProvider.getInstance(mAct).getAccountFromDB(mApp.getCurrentUser(), qryTime, C.COME_TYPE.IN, mQryDetailType);
        mApp.mAccBeanOutPieInList = DBProvider.getInstance(mAct).getAccountFromDB(mApp.getCurrentUser(), qryTime, C.COME_TYPE.OUT, mQryDetailType);

        //查询年度账单
        String minTimeYear = qryTime.substring(0,4)+"-01-01";
        String maxTimeYear = qryTime.substring(0,4)+"-12-31";
        mApp.mAccBeanYearColumnList =  DBProvider.getInstance(mAct).getAccountFromDBVague(mApp.getCurrentUser(),minTimeYear,maxTimeYear,mQryAccountType,mQryDetailType);
    }


    @Override
    public void OnClickAccount(AccountBean accountInfo) {
        SingleToast.showToast(mAct, "请不要试图做假账哈", 2000);
    }

    /**
     * 计算金额
     */
    private void calculateSum() {
        if (mAllList == null || mAllList.size() == 0) {
            mTxTotal.setText("总计金额:  无");
            return;
        }

        BigDecimal resBig = BigDecimal.valueOf(0);
        BigDecimal addBig = BigDecimal.valueOf(0);
        BigDecimal subBig = BigDecimal.valueOf(0);
        for (AccountBean info : mAllList) {
            if (info.getAccountType().equals("income")) {
                if (TextUtils.isEmpty(info.getMoney())) {
                    addBig = addBig.add(new BigDecimal(0));
                } else {
                    addBig = addBig.add(new BigDecimal(info.getMoney()));
                }
            } else {
                if (TextUtils.isEmpty(info.getMoney())) {
                    subBig = subBig.subtract(new BigDecimal(0));
                } else {
                    subBig = subBig.subtract(new BigDecimal(info.getMoney()));
                }
            }
        }
        resBig = addBig.add(subBig);
//        mTxTotal.setText("总计金额:  " + resBig.floatValue());
        mTxTotal.setText("收入金额:  " + addBig.floatValue()
                + "\n支出金额:  " + Math.abs(subBig.floatValue())
                + "\n总计金额:  " + resBig.floatValue());

        /*
         * Float类型保留两位小数处理
         * 保留结果四舍五入java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
         * df.format(你要格式化的数字);例：new java.text.DecimalFormat("#.00").format(3.1415926)
         * #.00表示两位小数 #.0000四位小数 以此类推...
         *
         * new DecimalFormat("0.00").format(-resBig.floatValue() - max)      保留结果四舍五入
         */
        Float max = Float.valueOf(mApp.mSelfMonthMax);
        if (max != -1 && max < -resBig.floatValue()) {
            new MaterialDialog.Builder(mAct).title(R.string.month_max_alert)
                    .content("本月度已超支" + ((new DecimalFormat("0.00").format(-resBig.floatValue() - max))) + "元，请注意~")
                    .positiveText(R.string.btn_confirm)
                    .negativeText(R.string.btn_cancel).show();

        }


    }

    /**
     * 分大类查看
     */
    private void showSortFirstList() {
        new MaterialDialog.Builder(mAct)
                .title(R.string.fl_menu_sort)
                .items(R.array.sort_array)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            case 0:
                                mQryDetailType = "";
                                mQryAccountType = "";
                                mTxQryType.setText("账单类型:所有");
                                doRefresh();
                                break;
                            case 1:
                                showSortSecList(0);
                                break;
                            case 2:
                                showSortSecList(1);
                                break;
                        }
                    }
                })
                .show();
    }


    /**
     * 分具体小类查看
     *
     * @param type 0:收入 1：支出
     */
    private void showSortSecList(final int type) {
        final ArrayList<String> localArrayList = new ArrayList<String>();
        List<TypeBean> listZichan;
        if (type == 0) {
            listZichan = mApp.mTypeBeanInList;
            localArrayList.add("所有收入");
        } else {
            listZichan = mApp.mTypeBeanOutList;
            localArrayList.add("所有支出");
        }

        for (TypeBean info : listZichan) {
            localArrayList.add(info.getName());
        }

        new MaterialDialog.Builder(mAct)
                .title(R.string.fl_menu_sort)
                .items(localArrayList)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (type == 0) {
                            if (which == 0) {
                                mQryAccountType = C.COME_TYPE.IN;
                                mQryDetailType = "";
                                mTxQryType.setText("账单类型:" + " 收入");
                            } else {
                                mQryAccountType = C.COME_TYPE.IN;
                                mQryDetailType = localArrayList.get(which);
                                mTxQryType.setText("账单类型:" + " 收入 " + mQryDetailType);
                            }
                            doRefresh();
                        } else {
                            if (which == 0) {
                                mQryAccountType = C.COME_TYPE.OUT;
                                mQryDetailType = "";
                                mTxQryType.setText("账单类型:" + " 支出");
                            } else {
                                mQryAccountType = C.COME_TYPE.OUT;
                                mQryDetailType = localArrayList.get(which);
                                mTxQryType.setText("账单类型:" + " 支出  " + mQryDetailType);
                            }
                            doRefresh();
                        }
                    }
                })
                .show();
    }


    @Override
    public void onDialogExpirationSet(int reference, int year, int monthOfYear) {
//        qryTime = year + "-" + (monthOfYear);
        if(monthOfYear<10){
            qryTime = year + "-0" + monthOfYear;
        }else{
            qryTime = year + "-" + monthOfYear;
        }

        mTxTime.setText("查询时间:  " + qryTime);
        doRefresh();//重新刷新
    }


    private void showMonthChose() {
        ExpirationPickerBuilder epb = new ExpirationPickerBuilder()
                .setFragmentManager(mAct.getSupportFragmentManager())
                .addExpirationPickerDialogHandler(this)
                .setStyleResId(R.style.BetterPickersDialogFragment_Light)
                .setMinYear(2000);
        epb.show();
    }




    @Override
    public void onResume() {
        super.onResume();
        if (isRefresh) {
            isRefresh = false;
            doRefresh();
        }
    }

    public static boolean isRefresh = false;

}
