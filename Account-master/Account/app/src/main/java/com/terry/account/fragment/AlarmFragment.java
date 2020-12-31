package com.terry.account.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.terry.account.R;
import com.terry.account.util.C;
import com.terry.account.util.SPUtils;
import com.terry.account.util.SingleToast;

import java.util.Calendar;


/**
 * 每日提醒页面
 *
 * @author sjy
 */
public class AlarmFragment extends Fragment {

    private View rootView;
    private TextView mTxTime;
    private Button mBtn;
    private Button mBtnCancel;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    private static AlarmFragment instance;

    public static AlarmFragment getInstance() {
        if (instance == null) instance = new AlarmFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_alarm, container, false);
        mTxTime = rootView.findViewById(R.id.tx_alarm_content);
        mBtn = rootView.findViewById(R.id.btn_alarm);
        mBtnCancel = rootView.findViewById(R.id.btn_alarm_cancel);

        String time = (String) SPUtils.getParam(getActivity(), C.SP.ALARM_TIME, "");
        if (TextUtils.isEmpty(time)) {
            mTxTime.setText("暂未设定");
        } else {
            mTxTime.setText(time);
        }

        //实例化闹钟管理器
        alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        //周期性闹钟的点计算事件
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取当前系统的时间
                Calendar calendar = Calendar.getInstance();
                int x = calendar.get(Calendar.HOUR_OF_DAY);
                int f = calendar.get(Calendar.MINUTE);
                //01.弹出时间对话框（选择时间）
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        //02.确定选择好的时间
                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.HOUR_OF_DAY, i);
                        c.set(Calendar.MINUTE, i1);
                        //04.时间一到，执行相对应的操作
                        Intent intent = new Intent();
                        intent.setAction("com.smile.account.alarm");
                        pendingIntent = PendingIntent.getBroadcast(getActivity(), 0x102, intent, 0);
                        //03.设置闹钟(单次)
                        alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                        //03.设置闹钟(周期)
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 60 * 60 * 24 * 1000, pendingIntent);

                        String time = i + ":" + i1;
                        SPUtils.setParam(getActivity(), C.SP.ALARM_TIME, time);
                        SingleToast.showToast(getContext(), "设置成功!", 2000);
                        mTxTime.setText(time);
                    }
                }, x, f, true);
                timePickerDialog.show();

            }
        });
        //结束周期性闹钟的点击事件
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setAction("com.smile.account.alarm");
                pendingIntent = PendingIntent.getBroadcast(getActivity(), 0x102, intent, 0);
                alarmManager.cancel(pendingIntent);
                SPUtils.setParam(getActivity(), C.SP.ALARM_TIME, "");
                mTxTime.setText("暂未设定");
                SingleToast.showToast(getContext(), "设置成功!", 2000);
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
