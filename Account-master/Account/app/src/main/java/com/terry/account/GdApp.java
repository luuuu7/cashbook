package com.terry.account;


import android.app.Application;

import com.terry.account.bean.AccountBean;
import com.terry.account.bean.PayBean;
import com.terry.account.bean.TypeBean;
import com.terry.account.bean.UserBean;
import com.terry.account.db.DBProvider;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 应用的入口 注册服务器信息
 *
 * @author sjy
 */
public class GdApp extends Application {

    private static final String TAG = GdApp.class.getSimpleName();

    private static String mSelfUserName;//当前用户名
    public static Float mSelfDayMax;//日限额

    public static Float mSelfMonthMax;//月限额

    public static Float mSelfYearMax;//年限额


    //展示的账单数据
    public List<AccountBean> mAccBeanChartList = new ArrayList<>();


    //展示的收入账单数据
    public List<AccountBean> mAccBeanInPieList = new ArrayList<>();
    //展示的支出账单数据
    public List<AccountBean> mAccBeanOutPieInList = new ArrayList<>();
    //展示的年度账单数据
    public List<AccountBean> mAccBeanYearColumnList = new ArrayList<>();


    //支付类型的缓存
    public List<PayBean> mPayBeanList = new ArrayList<>();

    //收入类型的缓存
    public List<TypeBean> mTypeBeanInList = new ArrayList<>();
    //消费类型的缓存
    public List<TypeBean> mTypeBeanOutList = new ArrayList<>();

    public List<UserBean> mUserList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public String getCurrentUser() {
        initTypeBeanList();
        return mSelfUserName;
    }


    public void setCurrentUser(UserBean userBean) {
        mSelfUserName = userBean.getName();
        mSelfDayMax = Float.valueOf(userBean.getDayMax());
        mSelfMonthMax = Float.valueOf(userBean.getMonthMax());
        mSelfYearMax= Float.valueOf(userBean.getYearMax());
        initTypeBeanList();
    }

    /**
     * 获取消费收入类型
     */
    public void initTypeBeanList() {
        mTypeBeanInList.clear();
        mTypeBeanOutList.clear();
        mTypeBeanInList = DBProvider.getInstance(this).getTypeBeanFromDB(mSelfUserName,"income");
        mTypeBeanOutList = DBProvider.getInstance(this).getTypeBeanFromDB(mSelfUserName,"outcome");

    }


}
