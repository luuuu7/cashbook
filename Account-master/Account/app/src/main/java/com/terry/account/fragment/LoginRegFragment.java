package com.terry.account.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.terry.account.R;
import com.terry.account.activity.LoginActivity;
import com.terry.account.base.BaseSwitchFragment;
import com.terry.account.bean.UserBean;
import com.terry.account.db.DBProvider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 注册界面
 *
 * @author sjy
 */
public class LoginRegFragment extends BaseSwitchFragment implements
        OnClickListener {

    private Button mBtnReg;
    private EditText mEtUserName;
    private EditText mEtUserPassword;
    private EditText mEtUserPasswordAgain;
    private TextView mTxBack;
    private ProgressDialog progressDialog;
    private String userName;
    private String userType = "0";
    private ToggleButton regToggleOne;
    private ToggleButton regToggleTwo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_register, container, false);
        mBtnReg = (Button) rootView.findViewById(R.id.button_i_need_register);
        mEtUserName = (EditText) rootView.findViewById(R.id.editText_register_username);
        mEtUserPassword = (EditText) rootView.findViewById(R.id.editText_register_userPassword);
        mEtUserPasswordAgain = (EditText) rootView.findViewById(R.id.editText_register_userPassword_again);
        mTxBack = (TextView) rootView.findViewById(R.id.rl_top_btn_back);
        regToggleOne = (ToggleButton) rootView.findViewById(R.id.register_toggle_one);
        regToggleTwo = (ToggleButton) rootView.findViewById(R.id.register_toggle_two);

        mBtnReg.setOnClickListener(this);
        mTxBack.setOnClickListener(this);

        /*
        * 密码显示/隐藏
        * */
        regToggleOne.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    mEtUserPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    mEtUserPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                mEtUserPassword.setSelection(mEtUserPassword.length());
            }
        });
        regToggleTwo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    mEtUserPasswordAgain.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    mEtUserPasswordAgain.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                mEtUserPasswordAgain.setSelection(mEtUserPasswordAgain.length());
            }
        });

        return rootView;
    }

    // 开始注册
    public void register() {
        final String password = mEtUserPassword.getText().toString();
        userName = mEtUserName.getText().toString();

        UserBean user = new UserBean();
        user.setName(userName);
        user.setPwd(password);
        boolean isSuccess = DBProvider.getInstance(mAct).insertUser(user);
        if (isSuccess) {
            showRegisterSuccess();
        } else {
            showHint("注册失败,请重试");
        }
    }

    private void progressDialogDismiss() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    private void progressDialogShow() {
        progressDialog = ProgressDialog
                .show(mAct,
                        mAct.getResources().getText(
                                R.string.dialog_message_title),
                        mAct.getResources().getText(
                                R.string.dialog_text_wait), true, false);
    }

    private void showRegisterSuccess() {
        new AlertDialog.Builder(mAct)
                .setTitle(
                        mAct.getResources().getString(
                                R.string.dialog_message_title))
                .setMessage(
                        mAct.getResources().getString(
                                R.string.success_register_success))
                .setNegativeButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                mFragmentCallBack.fragmentCallBack(
                                        LoginActivity.JUMP_2_LOGIN, null);
                            }
                        }).show();
    }

    // 进行注册前的格式验证
    private void doRegVerfy() {
        if (mEtUserName.getText().toString().isEmpty()) {
            showHint(mAct.getString(R.string.error_register_user_name_null));
            return;
        }

        if (mEtUserPassword.getText().toString()
                .equals(mEtUserPasswordAgain.getText().toString())) {
            if (!mEtUserPassword.getText().toString().isEmpty()) {
                register();
            } else{
                showHint(mAct
                        .getString(R.string.error_register_password_null));
            }
        } else if (mEtUserPassword.getText().toString().length() < 6){
            showHint(mAct
                    .getString(R.string.error_password_length_short));
        }else if (mEtUserPasswordAgain.getText().toString().isEmpty()){
            showHint(mAct.getString(R.string.error_registerRe_password_null));
        }else {
            showHint(mAct
                    .getString(R.string.error_register_password_not_equals));
        }
    }

    private boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;
        String expression = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}d{1}-?d{8}$)|"
                + "(^0[3-9] {1}d{2}-?d{7,8}$)|"
                + "(^0[1,2]{1}d{1}-?d{8}-(d{1,4})$)|"
                + "(^0[3-9]{1}d{2}-? d{7,8}-(d{1,4})$))";
        CharSequence inputStr = phoneNumber;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_i_need_register:
                doRegVerfy();
                break;
            case R.id.rl_top_btn_back:
                mFragmentCallBack
                        .fragmentCallBack(LoginActivity.JUMP_2_LOGIN, null);
                break;
            default:
                break;
        }
    }

    public void doFragmentBackPressed() {
        mFragmentCallBack.fragmentCallBack(LoginActivity.JUMP_2_LOGIN, null);
    }

    ;
}
