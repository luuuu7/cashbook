package com.terry.account.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.zxing.maxicode.MaxiCodeReader;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.terry.account.R;
import com.terry.account.activity.PieChartActivity;
import com.terry.account.activity.TabsActivity;
import com.terry.account.adapter.AccountAdapter;
import com.terry.account.base.BaseSwitchFragment;
import com.terry.account.bean.AccountBean;
import com.terry.account.bean.PayBean;
import com.terry.account.bean.TypeBean;
import com.terry.account.db.DBProvider;
import com.terry.account.util.C;
import com.terry.account.util.LogTrace;
import com.terry.account.util.SingleToast;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * 单日资产页面
 *
 * @author sjy
 */
public class DayFragment extends BaseSwitchFragment implements
        CalendarDatePickerDialogFragment.OnDateSetListener, AccountAdapter.OnAccountClick {

    private static final String TAG = DayFragment.class.getSimpleName();

    private AccountAdapter mAdapter;
    private static DayFragment instance;
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

    private boolean isChoseQryTime = false;

    List<AccountBean> mAllList = new ArrayList<>();


    public static DayFragment getInstance() {
        instance = new DayFragment();
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
        int day = localCalendar.get(Calendar.DAY_OF_MONTH);

        if (day < 10) {
            if (month + 1 < 10) {
                qryTime = year + "-0" + (month + 1) + "-0" + day;
            } else {
                qryTime = year + "-" + (month + 1) + "-0" + day;
            }

        } else {

            if (month + 1 < 10) {
                qryTime = year + "-0" + (month + 1) + "-" + day;
            } else {
                qryTime = year + "-" + (month + 1) + "-" + day;
            }

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

    /**
     * 浮动按钮配置
     */
    private void initFloatBtn() {
        mFabActMenu = (FloatingActionMenu) rootView.findViewById(R.id.fab_normal);

        FloatingActionButton fbIncome = new FloatingActionButton(mAct);
        fbIncome.setButtonSize(FloatingActionButton.SIZE_MINI);
        fbIncome.setLabelText(getString(R.string.fl_menu_add_income));
        fbIncome.setImageDrawable(new IconicsDrawable(mAct, GoogleMaterial.Icon.gmd_note_add).color(Color.WHITE).actionBar());
        fbIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditView(0, null);
                mFabActMenu.close(true);
            }
        });
        mFabActMenu.addMenuButton(fbIncome);

        FloatingActionButton fbOutCome = new FloatingActionButton(mAct);
        fbOutCome.setButtonSize(FloatingActionButton.SIZE_MINI);
        fbOutCome.setLabelText(getString(R.string.fl_menu_add_outcome));
        fbOutCome.setImageDrawable(new IconicsDrawable(mAct, GoogleMaterial.Icon.gmd_library_add).color(Color.WHITE).actionBar());
        fbOutCome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditView(1, null);
                mFabActMenu.close(true);
            }
        });
        mFabActMenu.addMenuButton(fbOutCome);

        FloatingActionButton fbChoseDay = new FloatingActionButton(mAct);
        fbChoseDay.setButtonSize(FloatingActionButton.SIZE_MINI);
        fbChoseDay.setLabelText(getString(R.string.fl_menu_choseday));
        fbChoseDay.setImageDrawable(new IconicsDrawable(mAct, GoogleMaterial.Icon.gmd_today).color(Color.WHITE).actionBar());
        fbChoseDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择查看的日期
                isChoseQryTime = true;
                showDataChose();
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
                                for (AccountBean accountBean : mAllList) {
                                    if (accountBean.getAccountType().equals(C.COME_TYPE.IN)) {
                                        mApp.mAccBeanChartList.add(accountBean);
                                    }
                                }
                                Intent itChar = new Intent(mAct, PieChartActivity.class);
                                itChar.putExtra("title", qryTime + "收入统计图表");
                                startActivity(itChar);
                                break;
                            case 1://支出统计
                                mApp.mAccBeanChartList.clear();
                                for (AccountBean accountBean : mAllList) {
                                    if (accountBean.getAccountType().equals(C.COME_TYPE.OUT)) {
                                        mApp.mAccBeanChartList.add(accountBean);
                                    }
                                }
                                Intent itCharOut = new Intent(mAct, PieChartActivity.class);
                                itCharOut.putExtra("title", qryTime + "支出统计图表");
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
        //执行查询方法
        mAllList = DBProvider.getInstance(mAct).getAccountFromDB(mApp.getCurrentUser(), qryTime, mQryAccountType, mQryDetailType);
        mAdapter = new AccountAdapter(mAct, mAllList, DayFragment.this);
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
        String minTime = qryTime.substring(0, 4) + "-01-01";
        String maxTime = qryTime.substring(0, 4) + "-12-31";
        mApp.mAccBeanYearColumnList = DBProvider.getInstance(mAct).getAccountFromDBVague(mApp.getCurrentUser(), minTime, maxTime, mQryAccountType, mQryDetailType);
    }


    /**
     * 展示操作选款
     *
     * @param accountBean
     */
    private void showHandleList(final AccountBean accountBean) {
        new MaterialDialog.Builder(mAct)
                .title(R.string.acount_handle_title)
                .items(R.array.account_handle)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                //编辑
                                if (accountBean.getAccountType().equals(C.COME_TYPE.IN)) {
                                    showEditView(0, accountBean);
                                } else {
                                    showEditView(1, accountBean);
                                }
                                break;
                            case 1:
                                //删除
                                new MaterialDialog.Builder(mAct).content(R.string.dialog_del_title)
                                        .positiveText(android.R.string.ok)
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(final MaterialDialog dialog, DialogAction which) {
                                                dialog.dismiss();

                                                DBProvider.getInstance(mAct).delSingleAccount(accountBean);
                                                SingleToast.showToast(getContext(), "删除成功", 2000);
                                                doRefresh();
                                            }
                                        }).negativeText(android.R.string.cancel).show();
                                break;
                            case 2:
                                //查看账单备注
                                new MaterialDialog.Builder(mAct)
                                        .title("备注")
                                        .content(accountBean.getMark())
                                        .positiveText(android.R.string.ok)
                                        .show();
                                break;
                        }
                    }
                })
                .show();
    }

    boolean isTimeReady = true;
    boolean isTypeReady = false;
    boolean isFromTypeReady = false;

    String typeTitle = "";
    String fromTypeTitle = "";
    String zichanType = "";//资产类型
    String fromType = "";//资产账户类型（微信、支付宝、银行卡）
    String zichanTime = "";//资产时间
    String zichanMoney = "";//资产金额
    String zichanMark = "";//资产备注

    TextView txTimeInfo;
    TextView txTypeInfo;
    TextView txFromTypeInfo;

    MDButton positiveAction;

    /**
     * 新增或者修改某一账单
     *
     * @param type
     * @param accountBean
     */
    public void showEditView(final int type, final AccountBean accountBean) {

        isTimeReady = true;
        isTypeReady = false;
        isFromTypeReady = false;
        zichanMark = "";


        String title = "";
        String timeTitle = "";

        String timeHint = "";
        String typeHint = "";
        String fromTypeHint = "";
        String moneyTitle = "";
        String markTitle = getString(R.string.come_mark);

        String posText = "";
        String negText = "";

        if (type == 0) {
            //增加收入
            title = getString(R.string.fl_menu_add_income);
            timeTitle = getString(R.string.time_income);
            typeTitle = getString(R.string.type_income);
            fromTypeTitle = getString(R.string.from_type);
            moneyTitle = getString(R.string.money_income);
            timeHint = qryTime;
            typeHint = getString(R.string.type_income_hint);
        } else {
            //增加支出
            title = getString(R.string.fl_menu_add_outcome);
            timeTitle = getString(R.string.time_outcome);
            typeTitle = getString(R.string.type_outcome);
            fromTypeTitle = getString(R.string.from_type);
            moneyTitle = getString(R.string.money_outcome);
            timeHint = qryTime;
            typeHint = getString(R.string.type_outcome_hint);
        }
        fromTypeHint = getString(R.string.fromtype_hint);

        if (accountBean != null) {
            if (type == 0) {
                title = getString(R.string.modify_income);
            } else {
                title = getString(R.string.modify_outcome);
            }
        }

        if (accountBean != null) {
            posText = getString(R.string.dialog_update_sure);
            negText = getString(R.string.dialog_update_cancel);
        } else {
            posText = getString(R.string.dialog_add_sure);
            negText = getString(R.string.dialog_add_cancel);
        }

        MaterialDialog dialog = new MaterialDialog.Builder(mAct)
                .title(title)
                .customView(R.layout.dialog_customview, true)
                .positiveText(posText)
                .negativeText(negText)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                        if (accountBean != null) {

                            AccountBean newInfo = new AccountBean();
                            newInfo.setId(accountBean.getId());
                            newInfo.setUserName(accountBean.getUserName());
                            newInfo.setFromType(accountBean.getFromType());
                            newInfo.setAccountType(accountBean.getAccountType());
                            newInfo.setMoney(zichanMoney);
                            newInfo.setTime(zichanTime);
                            newInfo.setType(zichanType);
                            newInfo.setFromType(fromType);
                            newInfo.setMark(zichanMark);

                            DBProvider.getInstance(mAct).updateAccount(newInfo, accountBean.getId());
                            SingleToast.showToast(mAct, "更新完成", 2000);
                            doRefresh();

                        } else {
                            //确定添加
                            AccountBean info = new AccountBean();
                            info.setUserName(mApp.getCurrentUser());
                            info.setMoney(zichanMoney);
                            info.setTime(zichanTime);
                            info.setType(zichanType);
                            info.setFromType(fromType);
                            info.setMark(zichanMark);
                            if (type == 0) {
                                info.setAccountType(C.COME_TYPE.IN);
                            } else {
                                info.setAccountType(C.COME_TYPE.OUT);
                            }
                            DBProvider.getInstance(mAct).insertAccount(info);
                            SingleToast.showToast(mAct, "添加完成", 2000);
                            doRefresh();
                        }

                    }
                }).build();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);

        TextView txTime = (TextView) dialog.getCustomView().findViewById(R.id.tx_time);
        txTimeInfo = (TextView) dialog.getCustomView().findViewById(R.id.tx_time_info);
        TextView txType = (TextView) dialog.getCustomView().findViewById(R.id.tx_type);
        txTypeInfo = (TextView) dialog.getCustomView().findViewById(R.id.tx_type_info);

        TextView txFromType = (TextView) dialog.getCustomView().findViewById(R.id.tx_from_type);
        txFromTypeInfo = (TextView) dialog.getCustomView().findViewById(R.id.tx_tx_from_type_info);

        TextView txMoney = (TextView) dialog.getCustomView().findViewById(R.id.tx_money);
        TextView txMark = (TextView) dialog.getCustomView().findViewById(R.id.tx_mark);
        EditText moneyInput = (EditText) dialog.getCustomView().findViewById(R.id.ed_money);
        EditText markInput = (EditText) dialog.getCustomView().findViewById(R.id.ed_mark);

        txTime.setText(timeTitle);
        txType.setText(typeTitle);
        txFromType.setText(fromTypeTitle);
        txMoney.setText(moneyTitle);
        txMark.setText(markTitle);

        if (accountBean != null) {
            zichanMoney = accountBean.getMoney();
            zichanTime = accountBean.getTime();
            zichanType = accountBean.getType();
            fromType = accountBean.getFromType();
            txTimeInfo.setText(zichanTime);
            txTypeInfo.setText(zichanType);
            txFromTypeInfo.setText(fromType);
            moneyInput.setText(zichanMoney);
            markInput.setText(accountBean.getMark());
            isTimeReady = true;
            isTypeReady = true;
            positiveAction.setEnabled(true);
        } else {
            txTimeInfo.setText(timeHint);
            txTypeInfo.setText(typeHint);
            txFromTypeInfo.setText(fromTypeHint);
            isTimeReady = true;
            isTypeReady = false;
            zichanTime = qryTime;
            positiveAction.setEnabled(false);
        }

        txTimeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDataChose();
            }
        });
        txTypeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTypeChoice(typeTitle, type);
            }
        });

        txFromTypeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFromTypeChoice(fromTypeTitle);
            }
        });

        moneyInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                zichanMoney = s.toString();
                if (isTimeReady && isTypeReady && (zichanMoney.trim().length() > 0)) {
                    positiveAction.setEnabled(true);
                } else {
                    positiveAction.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        markInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                zichanMark = s.toString();
                if (isTimeReady && isTypeReady && (zichanMoney.trim().length() > 0)) {
                    positiveAction.setEnabled(true);
                } else {
                    positiveAction.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dialog.show();

    }


    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        LogTrace.d(TAG, "onDateSet", "dayOfMonth:" + dayOfMonth);
        if (isChoseQryTime) {
            isChoseQryTime = false;

          /*  if (dayOfMonth < 10) {
                qryTime = year + "-" + (monthOfYear + 1) + "-0"
                        + dayOfMonth;
            } else {
                qryTime = year + "-" + (monthOfYear + 1) + "-"
                        + dayOfMonth;
            }*/

            if (dayOfMonth < 10) {
                if (monthOfYear + 1 < 10) {
                    qryTime = year + "-0" + (monthOfYear + 1) + "-0" + dayOfMonth;
                } else {
                    qryTime = year + "-" + (monthOfYear + 1) + "-0" + dayOfMonth;
                }

            } else {

                if (monthOfYear + 1 < 10) {
                    qryTime = year + "-0" + (monthOfYear + 1) + "-" + dayOfMonth;
                } else {
                    qryTime = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                }

            }


            mTxTime.setText("查询时间:  " + qryTime);
            doRefresh();//重新刷新
        } else {
            isTimeReady = true;
            if (dayOfMonth < 10) {
                zichanTime = year + "-" + (monthOfYear + 1) + "-0"
                        + dayOfMonth;
            } else {
                zichanTime = year + "-" + (monthOfYear + 1) + "-"
                        + dayOfMonth;
            }
            txTimeInfo.setText(zichanTime);

        }
    }

    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";

    private void showDataChose() {
        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setThemeLight()
                .setOnDateSetListener(this);
        cdp.show(mAct.getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
    }

    @Override
    public void onResume() {
        // Example of reattaching to the fragment
        super.onResume();
        CalendarDatePickerDialogFragment calendarDatePickerDialogFragment = (CalendarDatePickerDialogFragment) mAct.getSupportFragmentManager()
                .findFragmentByTag(FRAG_TAG_DATE_PICKER);
        if (calendarDatePickerDialogFragment != null) {
            calendarDatePickerDialogFragment.setOnDateSetListener(this);
        }

     /*   if (isRefresh) {
        isRefresh = false;
        doRefresh();
    }*/
    }

    public static boolean isRefresh = false;

    /**
     * 选择某一类型
     *
     * @param title
     * @param type
     */
    public void showTypeChoice(final String title, final int type) {
        ArrayList<String> localArrayList = new ArrayList<String>();
        List<TypeBean> listZichan;
        if (type == 0) {
            listZichan = mApp.mTypeBeanInList;
        } else {
            listZichan = mApp.mTypeBeanOutList;
        }

        if (listZichan.size() == 0) {
            SingleToast.showToast(getContext(), "请先设置收入或者消费项目", 2000);
            return;
        }

        for (TypeBean info : listZichan) {
            localArrayList.add(info.getName());
        }
        new MaterialDialog.Builder(mAct)
                .title(title)
                .items(localArrayList)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        isTypeReady = true;
                        zichanType = text.toString();
                        txTypeInfo.setText(zichanType);
                        if (isTimeReady && isTypeReady && isFromTypeReady && (zichanMoney.trim().length() > 0)) {
                            positiveAction.setEnabled(true);
                        } else {
                            positiveAction.setEnabled(false);
                        }
                        return true; // allow selection
                    }
                })
                .negativeText(R.string.dialog_chose_cancel)
                .positiveText(R.string.dialog_chose_sure)
                .show();
    }


    /**
     * 选择某一类型
     *
     * @param title
     */
    public void showFromTypeChoice(String title) {

        ArrayList<String> localPayList = new ArrayList<String>();

        List<PayBean> listPay = DBProvider.getInstance(mAct).getPayBeanFromDB();
        for (PayBean info : listPay) {
            localPayList.add(info.getName());
        }

        new MaterialDialog.Builder(mAct)
                .title(title)
                .items(localPayList)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        isFromTypeReady = true;
                        fromType = text.toString();
                        txFromTypeInfo.setText(fromType);
                        if (isTimeReady && isTypeReady && isFromTypeReady && (zichanMoney.trim().length() > 0)) {
                            positiveAction.setEnabled(true);
                        } else {
                            positiveAction.setEnabled(false);
                        }
                        return true; // allow selection
                    }
                })
                .negativeText(R.string.dialog_chose_cancel)
                .positiveText(R.string.dialog_chose_sure)
                .show();
    }


    @Override
    public void OnClickAccount(AccountBean accountInfo) {
        showHandleList(accountInfo);
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
        //mTxTotal.setText("总计金额:  " + resBig.floatValue());
        mTxTotal.setText("收入金额:  " + addBig.floatValue()
                + "\n支出金额:  " + Math.abs(subBig.floatValue())
                + "\n总计金额:  " + resBig.floatValue());

        /*
         * Float类型保留两位小数处理
         *  保留结果四舍五入java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
         * df.format(你要格式化的数字);例：new java.text.DecimalFormat("#.00").format(3.1415926)
         * #.00表示两位小数 #.0000四位小数 以此类推...
         *
         * new DecimalFormat("0.00").format(-resBig.floatValue() - max)      保留结果四舍五入
         */
        Float max = mApp.mSelfDayMax.floatValue();
        if (max != -1 && max < -resBig.floatValue()) {
            new MaterialDialog.Builder(mAct).title("本日支出已超支")
                    .content("本日已超支" + (new DecimalFormat("0.00").format(-resBig.floatValue() - max)) + "元，请注意~")
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
                                mQryAccountType = "";
                                mQryDetailType = "";
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

}
