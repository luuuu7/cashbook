
package com.terry.account.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.terry.account.R;
import com.terry.account.activity.LoginActivity;
import com.terry.account.activity.TabsActivity;
import com.terry.account.base.BaseSwitchFragment;
import com.terry.account.db.DBProvider;
import com.terry.account.util.C;
import com.terry.account.util.SPUtils;
import com.terry.account.util.SingleToast;


/**
 * 登录界面
 *
 * @author sjy
 */
public class LoginFragment extends BaseSwitchFragment implements OnClickListener {

    private Button mBtnLogin;
    private TextView mBtnReg;
    private TextView mBtnFrgpwd;
    private EditText mEtUserName;
    private EditText mEtPwd;
    private ProgressDialog mPrgDialog;
    private ToggleButton loginToggle;
    private CheckBox mCkRemberPwd;
    private CheckBox autoLogin;

    private int back = 0;// 判断按几次back

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container,
                false);

        mBtnLogin = (Button) rootView.findViewById(R.id.button_login);
        mBtnReg = (TextView) rootView.findViewById(R.id.button_register);
        mBtnFrgpwd = (TextView) rootView
                .findViewById(R.id.login_button_reset_password);
        mEtUserName = (EditText) rootView
                .findViewById(R.id.editText_userName);
        mEtPwd = (EditText) rootView
                .findViewById(R.id.editText_userPassword);
        mCkRemberPwd = (CheckBox) rootView.findViewById(R.id.cb_rememberpwd);
        loginToggle = (ToggleButton) rootView.findViewById(R.id.loginToggle);
        autoLogin = (CheckBox) rootView.findViewById(R.id.auto_login);

        /*
         * 密码显示/隐藏
         * */
        loginToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    mEtPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mEtPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                mEtPwd.setSelection(mEtPwd.length());
            }
        });

        /*
         * 记住密码
         * */
        mCkRemberPwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                SPUtils.setParam(mAct, C.SP.remember_login, arg1);
            }
        });

        Boolean isRemember = (Boolean) SPUtils.getParam(mAct,
                C.SP.remember_login, true);
        mCkRemberPwd.setChecked(isRemember);
        if (isRemember) {
            String userAccount = (String) SPUtils.getParam(mAct,
                    C.SP.account, "");
            String userPassword = (String) SPUtils.getParam(mAct, C.SP.pwd,
                    "");

            if (!TextUtils.isEmpty(userAccount)
                    && !TextUtils.isEmpty(userPassword)) {
                mEtUserName.setText(userAccount);
                mEtPwd.setText(userPassword);
            }
        }
        mBtnLogin.setOnClickListener(this);
        mBtnReg.setOnClickListener(this);
        mBtnFrgpwd.setOnClickListener(this);
        mAct.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        return rootView;

    }


    // 隐藏进度框
    private void progressDialogDismiss() {
        if (mPrgDialog != null)
            mPrgDialog.dismiss();
    }

    // 展示进度框
    private void progressDialogShow() {
        mPrgDialog = ProgressDialog
                .show(mAct,
                        mAct.getResources().getText(
                                R.string.dialog_message_title),
                        mAct.getResources().getText(
                                R.string.dialog_text_wait), true, false);
    }

    // 显示登陆错误对话框
    private void showLoginError() {
        new AlertDialog.Builder(mAct)
                .setTitle(
                        mAct.getResources().getString(
                                R.string.dialog_error_title))
                .setMessage(
                        mAct.getResources().getString(
                                R.string.error_login_error))
                .setNegativeButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }

    // 显示密码为空对话框
    private void showUserPasswordEmptyError() {
        new AlertDialog.Builder(mAct)
                .setTitle(
                        mAct.getResources().getString(
                                R.string.dialog_error_title))
                .setMessage(
                        mAct.getResources().getString(
                                R.string.error_register_password_null))
                .setNegativeButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }

    // 显示用户名为空对话框
    private void showUserNameEmptyError() {
        new AlertDialog.Builder(mAct)
                .setTitle(
                        mAct.getResources().getString(
                                R.string.dialog_error_title))
                .setMessage(
                        mAct.getResources().getString(
                                R.string.error_register_user_name_null))
                .setNegativeButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }

    public void doFragmentBackPressed() {
        back++;
        switch (back) {
            case 1:
                SingleToast.showToast(mAct, R.string.exit_again, 3000);
                // 3秒内都可以退出应用
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        back = 0;
                    }
                }, 3000);
                break;
            case 2:
                back = 0;
                if (TextUtils.isEmpty(mEtUserName.getText())
                        || TextUtils.isEmpty(mEtPwd.getText())) {
                    SPUtils.setParam(mApp, C.SP.remember_login, false);
                }
                mAct.finish();
                android.os.Process.killProcess(android.os.Process.myPid());// 关闭进程
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_login:
                String username = mEtUserName.getText().toString();
                if (username.isEmpty()) {
                    showUserNameEmptyError();
                    return;
                }
                String pwd = mEtPwd.getText().toString();
                if (pwd.isEmpty()) {
                    showUserPasswordEmptyError();
                    return;
                }
                progressDialogShow();


                if (DBProvider.getInstance(mAct).isUserExist(username, pwd)) {
                    //验证成功
                    mApp.setCurrentUser(DBProvider.getInstance(mAct).getSingleUserFromDB(username, pwd));
                    //记住密码
                    if (mCkRemberPwd.isChecked()) {
                        SPUtils.setParam(mAct, C.SP.account,
                                mEtUserName.getText().toString());
                        SPUtils.setParam(mAct, C.SP.pwd,
                                mEtPwd.getText().toString());
                    } else {
                        SPUtils.setParam(mAct, C.SP.account, "");
                        SPUtils.setParam(mAct, C.SP.pwd, "");
                    }
                    progressDialogDismiss();
                    //进入主界面
                    Intent mainIntent = new Intent(mAct,
                            TabsActivity.class);
                    startActivity(mainIntent);
                    mAct.finish();
                } else {
                    // 提示错误
                    progressDialogDismiss();
                    showLoginError();
                }
                break;
            case R.id.button_register:
                mFragmentCallBack.fragmentCallBack(LoginActivity.JUMP_2_REG, null);
                break;
            case R.id.login_button_reset_password:
                mFragmentCallBack.fragmentCallBack(LoginActivity.JUMP_2_RESET, null);
                break;
            default:
                break;
        }

    }


}
