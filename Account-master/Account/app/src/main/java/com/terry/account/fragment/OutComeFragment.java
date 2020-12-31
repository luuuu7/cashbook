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
import com.terry.account.adapter.TypeAdapter;
import com.terry.account.base.BaseSwitchFragment;
import com.terry.account.bean.TypeBean;
import com.terry.account.db.DBProvider;
import com.terry.account.util.SingleToast;
import com.terry.account.view.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * 单日资产页面
 *
 * @author sjy
 */
public class OutComeFragment extends BaseSwitchFragment implements
        TypeAdapter.OnZichanClick {

    private static final String TAG = OutComeFragment.class.getSimpleName();

    private TypeAdapter mAdapter;
    private static OutComeFragment instance;
    private TabsActivity menuActivity;

    private View rootView;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton mFabActBtn;
    List<TypeBean> mAllList = new ArrayList<>();

    private static final String mComeType = "outcome";

    public static OutComeFragment getInstance() {
        if (instance == null) instance = new OutComeFragment();
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
     * 下拉刷新数据 查
     */
    private void doRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        mAllList.clear();

        //查询成功
        mAllList = DBProvider.getInstance(mAct).getTypeBeanFromDB(mApp.getCurrentUser(), mComeType);
        //更新到最新的列表
        mApp.mTypeBeanInList = mAllList;
        if (mAllList.size() == 0) {
            SingleToast.showToast(getContext(), "没有更多数据了", 2000);
        } else {
            //更新到最新的列表
            mAdapter = new TypeAdapter(mAct, mAllList, OutComeFragment.this);
            mRecyclerView.setAdapter(mAdapter);
        }
        //此次刷新结束
        mSwipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void OnClickType(final TypeBean info) {
        showHandleList(info);
    }

    /**
     * 展示操作选款
     *
     * @param info
     */
    private void showHandleList(final TypeBean info) {
        new MaterialDialog.Builder(mAct)
                .title(R.string.acount_handle_title)
                .items(R.array.zichan_handle)
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

    private void showAdd() {
        new MaterialDialog.Builder(mAct).title(R.string.add_outcome)
                .positiveText(android.R.string.ok)
                .inputRange(2, 10)
                .input(R.string.add_outcome_hint, R.string.input_empty, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                        boolean isExist = false;
                        for (TypeBean typeBean : mAllList) {
                            if (typeBean.getName().equals(input.toString())) {
                                isExist = true;
                            }
                        }
                        if (isExist) {
                            SingleToast.showToast(mAct, "该支出类型已存在，请重新输入!", 2000);
                            return;
                        }

                        TypeBean typeBean = new TypeBean();
                        typeBean.setName(input.toString());
                        typeBean.setType(mComeType);
                        typeBean.setUserName(mApp.getCurrentUser());
                        DBProvider.getInstance(mAct).insertTypeBean(typeBean);
                        SingleToast.showToast(getContext(), "已添加支出资产项", 2000);
                        doRefresh();
                    }
                })
                .positiveText(R.string.btn_confirm)
                .negativeText(R.string.btn_cancel).show();
    }

    /**
     * 删除资产
     */
    private void showDel(final TypeBean typeBean) {
        new MaterialDialog.Builder(mAct).content(R.string.dialog_del_title)
                .title(typeBean.getName())
                .positiveText(android.R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(final MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                        DBProvider.getInstance(mAct).delSingleTypeBean(typeBean);
                        SingleToast.showToast(getContext(), "删除成功", 2000);
                        doRefresh();
                    }
                }).negativeText(android.R.string.cancel).show();
    }

    private void showUpdate(final TypeBean typeBean) {
        new MaterialDialog.Builder(mAct).title(R.string.update_outcome)
                .positiveText(android.R.string.ok)
                .inputRange(2, 6)
                .input(getString(R.string.update_outcome_hint), typeBean.getName(), false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                        typeBean.setName(input.toString());

                        DBProvider.getInstance(mAct).updateTypeBean(typeBean.getId(), typeBean);
                        SingleToast.showToast(getContext(), "支出资产项修改成功", 2000);
                        doRefresh();

                    }
                })
                .positiveText(R.string.btn_confirm)
                .negativeText(R.string.btn_cancel).show();
    }


}
