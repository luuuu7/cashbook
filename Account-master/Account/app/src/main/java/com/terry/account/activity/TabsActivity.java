package com.terry.account.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.terry.account.GdApp;
import com.terry.account.R;
import com.terry.account.bean.AccountBean;
import com.terry.account.db.DBProvider;
import com.terry.account.fragment.AboutFragment;
import com.terry.account.fragment.AlarmFragment;
import com.terry.account.fragment.ChartNullFragment;
import com.terry.account.fragment.ColumnChartFragment;
import com.terry.account.fragment.DayFragment;
import com.terry.account.fragment.InComeFragment;
import com.terry.account.fragment.InTableFragment;
import com.terry.account.fragment.LimitFragment;
import com.terry.account.fragment.MonthFragment;
import com.terry.account.fragment.OutComeFragment;
import com.terry.account.fragment.OutTableFragment;
import com.terry.account.fragment.PayFragment;
import com.terry.account.fragment.PieChartFragment;
import com.terry.account.fragment.YearFragment;
import com.terry.account.util.C;
import com.terry.account.util.SPUtils;
import com.terry.account.util.SingleToast;
import com.terry.account.zxing.android.CaptureActivity;

import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class TabsActivity extends AppCompatActivity {

    private static int sBottomNavigationViewID = 0;
    private static boolean isExit = false;      //定义一个变量来标识是否退出
    Timer timer = new Timer();
    TimerTask task;

    GdApp mApp;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    sBottomNavigationViewID = 0;
                    getSupportActionBar().setTitle(R.string.drawer_item_day);
                    changeFragment(DayFragment.getInstance());
                    return true;
                case R.id.navigation_chart:
                    sBottomNavigationViewID = 1;
                    if (mApp.mAccBeanInPieList.isEmpty()) {
                        getSupportActionBar().setTitle(R.string.title_chart_null);
                        changeFragment(ChartNullFragment.getInstance());
                    } else {
                        getSupportActionBar().setTitle(R.string.drawer_chart_pie_in);
                        changeFragment(new PieChartFragment().setList(mApp.mAccBeanInPieList));
                    }
                    return true;
                case R.id.navigation_set:
                    sBottomNavigationViewID = 2;
                    getSupportActionBar().setTitle(R.string.drawer_item_income);
                    changeFragment(InComeFragment.getInstance());
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        mApp = (GdApp) getApplication();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (savedInstanceState == null) {
            getSupportActionBar().setTitle(R.string.drawer_item_day);
            changeFragment(DayFragment.getInstance());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        switch (sBottomNavigationViewID) {
            case 0:
                getMenuInflater().inflate(R.menu.bill, menu);
                break;
            case 1:
                getMenuInflater().inflate(R.menu.chart, menu);
                break;
            case 2:
                getMenuInflater().inflate(R.menu.setting, menu);
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.drawer_item_day:
                getSupportActionBar().setTitle(R.string.drawer_item_day);
                changeFragment(DayFragment.getInstance());
                break;
            case R.id.drawer_item_month:
                getSupportActionBar().setTitle(R.string.drawer_item_month);
                changeFragment(MonthFragment.getInstance());
                break;
            case R.id.drawer_item_year:
                getSupportActionBar().setTitle(R.string.drawer_item_year);
                changeFragment(YearFragment.getInstance());
                break;
            case R.id.drawer_item_income:
                getSupportActionBar().setTitle(R.string.drawer_item_income);
                changeFragment(InComeFragment.getInstance());
                break;
            case R.id.drawer_item_outcome:
                getSupportActionBar().setTitle(R.string.drawer_item_outcome);
                changeFragment(OutComeFragment.getInstance());
                break;
            case R.id.drawer_item_max:
                getSupportActionBar().setTitle(R.string.drawer_item_max);
                changeFragment(LimitFragment.getInstance());
                break;
            case R.id.drawer_item_user:
                getSupportActionBar().setTitle(R.string.drawer_item_user);
                changeFragment(PayFragment.getInstance());
                break;
            case R.id.drawer_item_alarm:
                getSupportActionBar().setTitle(R.string.drawer_item_alarm);
                changeFragment(AlarmFragment.getInstance());
                break;
            case R.id.drawer_chart_pie_in:
                getSupportActionBar().setTitle(R.string.drawer_chart_pie_in);
                changeFragment(new PieChartFragment().setList(mApp.mAccBeanInPieList));
                break;
            case R.id.drawer_chart_pie_out:
                getSupportActionBar().setTitle(R.string.drawer_chart_pie_out);
                changeFragment(new PieChartFragment().setList(mApp.mAccBeanOutPieInList));
                break;
            case R.id.drawer_chart_column_year:
                getSupportActionBar().setTitle(R.string.drawer_chart_column_year);
                changeFragment(new ColumnChartFragment().setList(mApp.mAccBeanYearColumnList));
                break;
            case R.id.drawer_table_year_in:
                getSupportActionBar().setTitle(R.string.drawer_table_year_in);
                changeFragment(InTableFragment.getInstance());
                break;
            case R.id.drawer_table_year_out:
                getSupportActionBar().setTitle(R.string.drawer_table_year_out);
                changeFragment(OutTableFragment.getInstance());
                break;
            case R.id.drawer_item_about:
                getSupportActionBar().setTitle(R.string.drawer_item_about);
                changeFragment(AboutFragment.getInstance());
                break;
            case R.id.drawer_item_scan:
                //动态权限申请
                if (ContextCompat.checkSelfPermission(TabsActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(TabsActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
                } else {
                    goScan();
                }
                break;
            case R.id.loginout:
                showExitDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //3秒内双击返回键退出应用
    @Override
    public void onBackPressed() {
        if (isExit == false) {
            isExit = true;
            Toast.makeText(this, R.string.exit_again, Toast.LENGTH_SHORT).show();
            task = new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            };
            timer.schedule(task, 3000);
        } else {
            finish();
            System.exit(0);
        }
    }

    /**
     * 切换Fragment
     *
     * @param targetFragment
     */
    private void changeFragment(Fragment targetFragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_container, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }


    public void showExitDialog() {
        new MaterialDialog.Builder(this)
                .content(R.string.oyt_app_sure)
                .positiveText(android.R.string.ok).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(MaterialDialog dialog, DialogAction which) {
                SPUtils.setParam(TabsActivity.this,
                        C.SP.remember_login, false);
                finish();
            }
        }).negativeText(android.R.string.cancel).show();
    }


    private static final String DECODED_CONTENT_KEY = "codedContent";
    private static final String DECODED_BITMAP_KEY = "codedBitmap";
    private static final int REQUEST_CODE_SCAN = 0x0000;

    /**
     * 跳转到扫码界面扫码
     */
    private void goScan() {
        Intent intent = new Intent(TabsActivity.this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    goScan();
                } else {
                    Toast.makeText(this, "你拒绝了权限申请，可能无法打开相机扫码哟！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                //返回的文本内容
                String content = data.getStringExtra(DECODED_CONTENT_KEY);
                //返回的BitMap图像
                Bitmap bitmap = data.getParcelableExtra(DECODED_BITMAP_KEY);

                  /*  new MaterialDialog.Builder(this)
                            .title("扫描成功!")
                            .content("你扫描到的内容是：" + content)
                            .positiveText("我已知悉")
                            .show();*/

                //模拟支出
                String zichanTime = "";
                Calendar localCalendar = Calendar.getInstance();
                int year = localCalendar.get(Calendar.YEAR);
                int month = localCalendar.get(Calendar.MONTH);
                int day = localCalendar.get(Calendar.DAY_OF_MONTH);
                if (day < 10) {
                    if (month + 1 < 10) {
                        zichanTime = year + "-0" + (month + 1) + "-0" + day;
                    } else {
                        zichanTime = year + "-" + (month + 1) + "-0" + day;
                    }
                } else {
                    if (month + 1 < 10) {
                        zichanTime = year + "-0" + (month + 1) + "-" + day;
                    } else {
                        zichanTime = year + "-" + (month + 1) + "-" + day;
                    }
                }
                String[] strs = new String[]{"日常购物", "餐饮开销", "娱乐开销", "其他花费"};
                String[] fromTypes = new String[]{"支付宝", "微信"};
                final AccountBean info = new AccountBean();
                info.setUserName(mApp.getCurrentUser());
                info.setMoney(String.valueOf(new Random().nextInt(1000)));
                info.setTime(zichanTime);
                info.setType(strs[new Random().nextInt(4)]);
                info.setFromType(fromTypes[new Random().nextInt(2)]);
                info.setMark("");
                info.setAccountType(C.COME_TYPE.OUT);

                new MaterialDialog.Builder(this)
                        .title("扫描成功!")
                        .content("你扫描到的内容是：" + content + "\n"
                                + "支出类别:" + info.getType() + "\n"
                                + "支出金额:" + info.getMoney())
                        .positiveText("支出")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                DBProvider.getInstance(TabsActivity.this).insertAccount(info);
                                SingleToast.showToast(TabsActivity.this, "扫码支付成功!", 2000);
                                DayFragment.isRefresh = true;
                                getSupportActionBar().setTitle(R.string.drawer_item_day);
                                changeFragment(DayFragment.getInstance());
                            }
                        })
                        .negativeText("取消")
                        .show();

            }
        }
    }
}
