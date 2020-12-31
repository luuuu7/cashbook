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
import com.terry.account.adapter.UserAdapter;
import com.terry.account.base.BaseSwitchFragment;
import com.terry.account.bean.UserBean;
import com.terry.account.db.DBProvider;
import com.terry.account.util.SingleToast;
import com.terry.account.view.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;


/**
 * 用户管理页面
 *
 * @author sjy
 */
public class UserFragment extends BaseSwitchFragment implements
        UserAdapter.OnUserClick {

    private static final String TAG = UserFragment.class.getSimpleName();

    private UserAdapter mAdapter;
    private static UserFragment instance;
    private TabsActivity menuActivity;

    private View rootView;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton mFabActBtn;
    private List<UserBean> mAllList = new ArrayList<>();


    public static UserFragment getInstance() {
        if (instance == null) instance = new UserFragment();
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
        mAllList = DBProvider.getInstance(mAct).getUserFromDB();
        //更新到最新的列表
        mApp.mUserList = mAllList;
        mAdapter = new UserAdapter(mAct, mAllList, UserFragment.this);
        mRecyclerView.setAdapter(mAdapter);
        //此次刷新结束
        mSwipeRefreshLayout.setRefreshing(false);
    }


    /**
     * 展示操作选款
     *
     */
    private void showHandleList(final UserBean userBean) {
        new MaterialDialog.Builder(mAct)
                .title(R.string.acount_handle_title)
                .items(R.array.user_handle)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                showDel(userBean);
                                break;
                            case 1:
                                showUpdate(userBean);
                                break;
                        }
                    }
                })
                .show();
    }


    /**
     * 增加用户
     */
    private void showAdd(){
        new MaterialDialog.Builder(mAct).title("增加用户（默认密码123）")
                .positiveText(android.R.string.ok)
                .input("请输入账户名称", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        UserBean userBean = new UserBean();
                        userBean.setName(input.toString());
                        userBean.setPwd("123");
                        boolean isSuccess = DBProvider.getInstance(mAct).insertUser(userBean);
                        if(isSuccess){
                            SingleToast.showToast(getContext(),"已添加用户",2000);
                            doRefresh();
                        }else{
                            SingleToast.showToast(getContext(),"添加失败，请重新输入账户名称",2000);
                        }
                    }
                })
                .positiveText(R.string.btn_confirm)
                .negativeText(R.string.btn_cancel).show();
    }

    /**
     * 删除用户
     */
    private void showDel(final UserBean userBean){
        new MaterialDialog.Builder(mAct).content(R.string.dialog_del_title)
                .title(userBean.getName())
                .positiveText(android.R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(final MaterialDialog dialog, DialogAction which) {
                        dialog.dismiss();
                        DBProvider.getInstance(mAct).delSingleUser(userBean);
                        SingleToast.showToast(getContext(),"删除成功",2000);
                        doRefresh();
                    }
                }).negativeText(android.R.string.cancel).show();
    }


    private void showUpdate(final UserBean userBean){
        new MaterialDialog.Builder(mAct).title("修改"+userBean.getName()+"密码")
                .positiveText(android.R.string.ok)
                .input("",  userBean.getPwd(), false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        userBean.setPwd(input.toString());
                        DBProvider.getInstance(mAct).updateUserPwd(userBean.getName(),userBean.getPwd());
                        SingleToast.showToast(getContext(),"用户密码修改成功",2000);
                        doRefresh();
                    }
                })
                .positiveText(R.string.btn_confirm)
                .negativeText(R.string.btn_cancel).show();
    }


    @Override
    public void OnClickType(UserBean userBean) {
        showHandleList(userBean);
    }
}
