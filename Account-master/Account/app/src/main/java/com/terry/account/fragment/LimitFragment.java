package com.terry.account.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.terry.account.GdApp;
import com.terry.account.R;
import com.terry.account.base.BaseSwitchFragment;
import com.terry.account.db.DBProvider;
import com.terry.account.util.SingleToast;

import java.lang.reflect.Array;
import java.text.DecimalFormat;

import static com.terry.account.GdApp.mSelfDayMax;
import static java.lang.String.valueOf;


/**
 * 消费限额设置页面
 *
 * @author sjy
 */
public class LimitFragment extends BaseSwitchFragment implements
        View.OnClickListener {

    private View rootView;

    private TextView mTxUserName;
    private EditText mEdDay;
    private EditText mEdMonth;
    private EditText mEdYear;
    private Button mBtnSet;

    private static LimitFragment instance;

    public static LimitFragment getInstance() {
        if (instance == null) instance = new LimitFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_limit, container, false);
        mEdDay = rootView.findViewById(R.id.ed_day);
        mEdMonth = rootView.findViewById(R.id.ed_month);
        mEdYear = rootView.findViewById(R.id.ed_year);
        mTxUserName = rootView.findViewById(R.id.tx_user);
        mBtnSet = rootView.findViewById(R.id.btn_set);
        mBtnSet.setOnClickListener(this);


        mTxUserName.setText(mApp.getCurrentUser() + "支付限额：");
        mEdDay.setText("" + mSelfDayMax);
        mEdMonth.setText("" + GdApp.mSelfMonthMax);
        mEdYear.setText("" + GdApp.mSelfYearMax);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_set:
                /*
                * 源代码
                * 输入的仅为int类型，不能输入并保存小数
                * */
				/*mApp.mSelfDayMax = Integer.parseInt(mEdDay.getEditableText().toString());
				mApp.mSelfMonthMax = Integer.parseInt(mEdMonth.getEditableText().toString());
				mApp.mSelfYearMax = Integer.parseInt(mEdYear.getEditableText().toString());
				DBProvider.getInstance(mAct).updateUserInfo(mApp.getCurrentUser(),mApp.mSelfDayMax,mApp.mSelfMonthMax,mApp.mSelfYearMax);
				SingleToast.showToast(mAct,"限额修改成功",2000);*/

				/*
				* 预算限额输入非空验证
				* 换为Float类型，可使用小数。并在DayFragment、MonthFragment、YearFragment中进行保留两位小数的处理
				* 此处为Float类型，不使用if...else...语句判断（会报错闪退），因此使用try... catch....
				* */
				try{
                    mApp.mSelfDayMax = Float.valueOf(mEdDay.getEditableText().toString());
                }catch (NumberFormatException e){
                    showHint(mAct
                            .getString(R.string.error_day_max_null));
                    return;
                }
				try{
				    mApp.mSelfMonthMax = Float.valueOf(mEdMonth.getEditableText().toString());
                }catch (NumberFormatException e){
                    showHint(mAct
                            .getString(R.string.error_month_max_null));
                    return;
                }
				try{
				    mApp.mSelfYearMax = Float.valueOf(mEdYear.getEditableText().toString());
                }catch (NumberFormatException e){
                    showHint(mAct
                            .getString(R.string.error_year_max_null));
                    return;
                }

                if (DBProvider.getInstance(mAct).updateUserInfo(mApp.getCurrentUser(), mSelfDayMax, GdApp.mSelfMonthMax, GdApp.mSelfYearMax)) {
                    SingleToast.showToast(mAct, "限额修改成功", 2000);
                } else {
                    SingleToast.showToast(mAct, "限额修改失败", 2000);
                }
                break;
            default:
                break;
        }

    }

}
