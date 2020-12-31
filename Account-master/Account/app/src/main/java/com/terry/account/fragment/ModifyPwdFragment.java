package com.terry.account.fragment;

import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.terry.account.R;
import com.terry.account.activity.LoginActivity;
import com.terry.account.base.BaseSwitchFragment;
import com.terry.account.db.DBProvider;
import com.terry.account.util.SingleToast;


/**
 * 修改密码页面
 *
 * @author sjy
 */
public class ModifyPwdFragment extends BaseSwitchFragment implements
        View.OnClickListener {

    private View rootView;

    private TextView mTxBack;
    private EditText mEtUserName;
    private EditText mEdOld;
    private EditText mEdNew;
    private EditText mEdNewR;
    private Button mBtnModify;
    private ToggleButton modifyToggleOne;
    private ToggleButton modifyToggleTwo;
    private ToggleButton modifyToggleThree;

    private static ModifyPwdFragment instance;

    /*public static ModifyPwdFragment getInstance() {
        if (instance == null) instance = new ModifyPwdFragment();
        return instance;
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_modify_pwd, container, false);
        mTxBack = (TextView) rootView.findViewById(R.id.rl_top_btn_back);
        mEdOld = (EditText) rootView.findViewById(R.id.et_pwd_old);
        mEdNew = (EditText) rootView.findViewById(R.id.et_pwd_new);
        mEdNewR = (EditText) rootView.findViewById(R.id.et_pwd_new_repeat);
        mEtUserName = (EditText) rootView.findViewById(R.id.editText_register_username);
        mBtnModify = (Button) rootView.findViewById(R.id.btn_pwd_modify);
        modifyToggleOne = (ToggleButton) rootView.findViewById(R.id.modify_toggle_one);
        modifyToggleTwo = (ToggleButton) rootView.findViewById(R.id.modify_toggle_two);
        modifyToggleThree = (ToggleButton) rootView.findViewById(R.id.modify_toggle_three);
        mBtnModify.setOnClickListener(this);
        mTxBack.setOnClickListener(this);

        /*
         * 密码显示/隐藏
         * */
        modifyToggleOne.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheacked) {
                if (isCheacked) {
                    mEdOld.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mEdOld.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                mEdOld.setSelection(mEdOld.length());
            }
        });
        modifyToggleTwo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    mEdNew.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mEdNew.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                mEdNew.setSelection(mEdNew.length());
            }
        });
        modifyToggleThree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    mEdNewR.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    mEdNewR.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                mEdNewR.setSelection(mEdNewR.length());
            }
        });

        return rootView;

    }

    //密码修改前的格式验证
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_top_btn_back:
                mFragmentCallBack.fragmentCallBack(LoginActivity.JUMP_2_LOGIN, null);
                break;
            case R.id.btn_pwd_modify:
                String userName = mEtUserName.getText().toString();
                String oldPwd = mEdOld.getEditableText().toString();
                String newPwd = mEdNew.getEditableText().toString();
                String newPwdR = mEdNewR.getEditableText().toString();
                if (mEtUserName.getText().toString().isEmpty()) {
                    showHint(mAct
                            .getString(R.string.error_register_user_name_null));
                    return;
                }
                if (mEdOld.getText().toString().isEmpty()) {
                    showHint(mAct
                            .getString(R.string.error_modify_old_password_null));
                    return;
                }
                if (mEdNew.getText().toString().isEmpty()) {
                    showHint(mAct
                            .getString(R.string.error_modify_new_password_null));
                } else if (mEdNew.getText().toString().length() < 6) {
                    showHint(mAct
                            .getString(R.string.error_password_length_short));
                } else if (mEdNewR.getText().toString().isEmpty()) {
                    showHint(mAct
                            .getString(R.string.error_registerRe_password_null));
                } else {
                    if (!newPwd.equals(newPwdR)) {
                        //SingleToast.showToast(mAct,"两次输入密码不一致，请重新输入",2000);
                        SingleToast.showToast(mAct, R.string.error_register_password_not_equals, 2000);
                    } else {
                        if (DBProvider.getInstance(mAct).updateUserPwd(userName, newPwd)) {
                            SingleToast.showToast(mAct, "密码修改成功", 2000);
                            mEdOld.setText("");
                            mEdNew.setText("");
                            mEdNewR.setText("");
                        } else {
                            SingleToast.showToast(mAct, "密码修改失败", 2000);
                        }
                    }
                }
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
