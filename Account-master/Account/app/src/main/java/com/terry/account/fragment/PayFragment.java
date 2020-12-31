package com.terry.account.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.clans.fab.FloatingActionButton;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.terry.account.R;
import com.terry.account.activity.TabsActivity;
import com.terry.account.adapter.PayAdapter;
import com.terry.account.base.BaseSwitchFragment;
import com.terry.account.bean.PayBean;
import com.terry.account.db.DBProvider;
import com.terry.account.util.SingleToast;
import com.terry.account.view.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;


/**
 * 支付设置页面
 *
 * @author sjy
 */
public class PayFragment extends BaseSwitchFragment implements
        PayAdapter.OnPayClick {

    private static final String TAG = PayFragment.class.getSimpleName();

    private PayAdapter mAdapter;
    private static PayFragment instance;
    private TabsActivity menuActivity;

    private View rootView;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton mFabActBtn;
    private List<PayBean> mAllList = new ArrayList<>();

    public static PayFragment getInstance() {
        if (instance == null) instance = new PayFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_somecome, container, false);
        initViews();
        menuActivity = (TabsActivity) mAct;
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    private void initViews() {
        initFloatBtn();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mAct));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setColor(getResources().getColor(R.color.colorPrimary));
        dividerItemDecoration.setSize(1);
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.theme_accent));

        //下拉刷新
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });

        doRefresh();
    }

    private void initFloatBtn() {
        mFabActBtn = (FloatingActionButton) rootView.findViewById(R.id.fab_normal);
        mFabActBtn.setButtonSize(FloatingActionButton.SIZE_NORMAL);
        mFabActBtn.setImageDrawable(new IconicsDrawable(mAct, GoogleMaterial.Icon.gmd_add).color(Color.WHITE).actionBar());
        mFabActBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //增加一则
                showAdd();
            }
        });
    }


    /**
     * 下拉刷新数据
     */
    private void doRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        mAllList.clear();
        //查询成功
        mAllList = DBProvider.getInstance(mAct).getPayBeanFromDB();
        //更新到最新的列表
        mApp.mPayBeanList = mAllList;
        mAdapter = new PayAdapter(mAct, mAllList, PayFragment.this);
        mRecyclerView.setAdapter(mAdapter);
        //此次刷新结束
        mSwipeRefreshLayout.setRefreshing(false);
    }


    /**
     * 展示操作选款
     *
     * @param info
     */
    private void showHandleList(final PayBean info) {
        new MaterialDialog.Builder(mAct)
                .title(R.string.acount_handle_title)
                .items(R.array.pay_handle)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                showDel(info);
                                break;
                            case 1:
                                showUpdate(info);
                                break;
                        }
                    }
                })
                .show();
    }



    /**
     * 增加支付账户
     */
    private void showAdd(){
        new MaterialDialog.Builder(mAct).title(R.string.add_pay)
                .positiveText(android.R.string.ok)
                .inputRange(2, 6)
                .input(R.string.add_pay_hint, R.string.input_empty, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        PayBean payBean = new PayBean();
                        payBean.setName(input.toString());
                        payBean.setUserName(mApp.getCurrentUser());
                        DBProvider.getInstance(mAct).insertPayBean(payBean);
                        SingleToast.showToast(getContext(),"已添加支付账户",2000);
                        doRefresh();
                    }
                })
                .positiveText(R.string.btn_confirm)
                .negativeText(R.string.btn_cancel).show();
    }

    /**
     * 删除资产
     */
    private void showDel(final PayBean info){
        new MaterialDialog.Builder(mAct).content(R.string.dialog_del_title)
                .title(info.getName())
                .positiveText(android.R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(final MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                        DBProvider.getInstance(mAct).delSinglePayBean(info);
                        SingleToast.showToast(getContext(),"删除成功",2000);
                        doRefresh();
                    }
                }).negativeText(android.R.string.cancel).show();
    }


    private void showUpdate(final PayBean info){
        new MaterialDialog.Builder(mAct).title(R.string.update_pay)
                .positiveText(android.R.string.ok)
                .inputRange(2, 6)
                .input(getString(R.string.update_pay_hint),  info.getName(), false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        info.setName(input.toString());
                        DBProvider.getInstance(mAct).updatePayBean(info.getId(),info);
                        SingleToast.showToast(getContext(),"支付账户修改成功",2000);
                        doRefresh();
                    }
                })
                .positiveText(R.string.btn_confirm)
                .negativeText(R.string.btn_cancel).show();
    }


    @Override
    public void OnClickType(PayBean payBean) {
        showHandleList(payBean);
    }
}
