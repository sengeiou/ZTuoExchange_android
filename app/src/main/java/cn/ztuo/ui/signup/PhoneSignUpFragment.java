package cn.ztuo.ui.signup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geetest.sdk.Bind.GT3GeetestUtilsBind;
import com.gyf.barlibrary.ImmersionBar;

import cn.ztuo.R;
import cn.ztuo.ui.country.CountryActivity;
import cn.ztuo.ui.login.LoginActivity;
import cn.ztuo.ui.message_detail.MessageHelpActivity;
import cn.ztuo.app.GlobalConstant;
import cn.ztuo.base.BaseTransFragment;
import cn.ztuo.entity.Country;
import cn.ztuo.utils.WonderfulCodeUtils;
import cn.ztuo.utils.WonderfulLogUtils;
import cn.ztuo.utils.WonderfulStringUtils;
import cn.ztuo.utils.WonderfulToastUtils;

import com.tencent.captchasdk.TCaptchaDialog;
import com.tencent.captchasdk.TCaptchaVerifyListener;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import cn.ztuo.app.Injection;

/**
 * Created by Administrator on 2018/2/2.
 */

public class PhoneSignUpFragment extends BaseTransFragment implements SignUpContract.PhoneView {

    public static final String TAG = PhoneSignUpFragment.class.getSimpleName();
    public static String token = "";
    @BindView(R.id.ibBack)
    ImageButton ibBack;
    @BindView(R.id.tvChangeType)
    TextView tvChangeType;
    @BindView(R.id.llTitle)
    LinearLayout llTitle;
    @BindView(R.id.etUsername)
    EditText etUsername;
    @BindView(R.id.etCode)
    EditText etCode;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.etRePassword)
    EditText etRePassword;
    @BindView(R.id.tvBack)
    TextView tvBack;
    @BindView(R.id.tvCountry)
    TextView tvCountry;
    @BindView(R.id.tvGetCode)
    TextView tvGetCode;
    @BindView(R.id.tvSignUp)
    TextView tvSignUp;
    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.tvToRegist)
    TextView tvToRegist;
    @BindView(R.id.checkbox)
    CheckBox checkbox;
    @BindView(R.id.text_yonghu)
    TextView text_yonghu;
    @BindView(R.id.yan)
    ImageView yan;
    private boolean isYan = false;
    private boolean isYan1 = false;

    @BindView(R.id.yan1)
    ImageView yan1;
    @BindView(R.id.tuijian)
    EditText tuijian;
    private Country country;
    private CountDownTimer timer;
    private SignUpContract.PhonePresenter presenter;
    private GT3GeetestUtilsBind gt3GeetestUtils;

    private String challenge;
    private String validate;
    private String seccode;

    private TCaptchaDialog dialog;
    private TCaptchaVerifyListener listener = new TCaptchaVerifyListener() {
        @Override
        public void onVerifyCallback(JSONObject jsonObject) {
            int ret = 0;
            try {
                ret = jsonObject.getInt("ret");
                if (ret == 0) {
                    //验证成功回调
                    //jsonObject.getInt("ticket")为验证码票据
                    //jsonObject.getString("appid")为appid
                    //jsonObject.getString("randstr")为随机串
                    WonderfulLogUtils.logi("miao", countryStr + "----" + phone);
                    presenter.phoneCode(phone, countryStr);
                    challenge = jsonObject.getString("ticket");
                    validate = jsonObject.getString("randstr");
                    seccode = "";
                    tvGetCode.setEnabled(false);
                } else if (ret == -1001) {
                    //验证码首个TCaptcha.js加载错误，业务可以根据需要重试
                    //jsonObject.getString("info")为错误信息
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                } else {
                    //验证失败回调，一般为用户关闭验证码弹框
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(getActivity() instanceof BaseSignUpFragment.OperateCallback)) {
            throw new RuntimeException("The Activity which fragment is located must implement the OperateCallback interface!");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        gt3GeetestUtils.cancelUtils();
    }

    public static PhoneSignUpFragment getInstance() {
        PhoneSignUpFragment phoneSignUpFragment = new PhoneSignUpFragment();
        return phoneSignUpFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_phone_sign_up;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        gt3GeetestUtils = new GT3GeetestUtilsBind(getActivity());
        new PhoneSignUpPresenter(Injection.provideTasksRepository(getActivity().getApplicationContext()), this);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvChangeType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseSignUpFragment.OperateCallback) getActivity()).switchType(BaseSignUpFragment.Type.EMAIL);
            }
        });
        tvGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCode();
            }
        });
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpByPhone();

            }
        });
        tvCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountryActivity.actionStart(PhoneSignUpFragment.this);
            }
        });
        tvToRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.actionStart(getActivity());
            }
        });
        text_yonghu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageHelpActivity.actionStart(getmActivity(), GlobalConstant.USER_AGREEMENT_ID);
            }
        });
        yan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isYan = !isYan;
                Drawable no = getResources().getDrawable(R.drawable.yan_no);
                Drawable yes = getResources().getDrawable(R.drawable.yan_yes);
                if (isYan) {
                    //显示
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    yan.setImageDrawable(no);

                } else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    yan.setImageDrawable(yes);
                }
            }
        });
        yan1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isYan1 = !isYan1;
                Drawable no = getResources().getDrawable(R.drawable.yan_no);
                Drawable yes = getResources().getDrawable(R.drawable.yan_yes);
                if (isYan1) {
                    //显示
                    etRePassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    yan1.setImageDrawable(no);

                } else {
                    etRePassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    yan1.setImageDrawable(yes);
                }
            }
        });
    }

    private void signUpByPhone() {
        String phone = etPhone.getText().toString();
        String username = etUsername.getText().toString();
        String code = etCode.getText().toString();
        String password = etPassword.getText().toString();
        String rePassword = etRePassword.getText().toString();
        String country = "";
        String tuijianma = tuijian.getText().toString();
        if (this.country == null) country = "中国";
        else country = this.country.getZhName();
        if (WonderfulStringUtils.isEmpty(phone, code, username, password, rePassword, country)) {
            WonderfulToastUtils.showToast(WonderfulToastUtils.getString(R.string.Incomplete_information));
            return;
        }
        if (!checkbox.isChecked()) {
            WonderfulToastUtils.showToast(WonderfulToastUtils.getString(R.string.xieyi));
            return;
        }
        if (!password.equals(rePassword)) {
            WonderfulToastUtils.showToast(R.string.pwd_diff);
            return;
        }
        WonderfulLogUtils.logi("miao", challenge + "-----" + validate);
        presenter.signUpByPhone(phone, username, password, country, code, tuijianma, challenge, validate, seccode);
//        presenter.signUpByPhone(phone, username, password, country, code,tuijianma,challenge,validate,seccode);
        /*if (WonderfulStringUtils.isStandard(password,rePassword)) {
            presenter.signUpByPhone(phone, username, password, country, code);
        }else WonderfulToastUtils.showToast(R.string.pwd_diff);*/
    }

    private String countryStr;
    private String phone;

    private void getCode() {
        countryStr = "";
        phone = "";
        if (country == null) countryStr = "中国";
        else countryStr = country.getZhName();
        phone = etPhone.getText().toString();
        if (WonderfulStringUtils.isEmpty(phone) || phone.length() < 11) {
            WonderfulToastUtils.showToast(R.string.phone_not_correct);
        }
//        presenter.captch();
        /**
         @param context，上下文
         @param appid，业务申请接入验证码时分配的appid
         @param listener，验证码验证结果回调
         @param jsonString，业务自定义参数
         */
        dialog = new TCaptchaDialog(getmActivity(), "2040846200", listener, null);
        dialog.show();
    }

    @Override
    protected void obtainData() {

    }

    @Override
    protected void fillWidget() {

    }

    @Override
    protected void loadData() {
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        if (!isSetTitle) {
            ImmersionBar.setTitleBar(getActivity(), llTitle);
            isSetTitle = true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CountryActivity.RETURN_COUNTRY) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                country = (Country) data.getSerializableExtra("country");
                tvCountry.setText(country.getZhName());
            }
        }
    }

    @Override
    public void setPresenter(SignUpContract.PhonePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void phoneCodeSuccess(String obj) {
        gt3GeetestUtils.gt3TestFinish();
        WonderfulToastUtils.showToast(obj);
        fillCodeView(90 * 1000);
    }

    private void fillCodeView(long time) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvGetCode.setText(getActivity().getResources().getString(R.string.re_send) + "（" + millisUntilFinished / 1000 + "）");
            }

            @Override
            public void onFinish() {
                tvGetCode.setText(R.string.send_code);
                tvGetCode.setEnabled(true);
                timer.cancel();
                timer = null;
            }
        };
        timer.start();
    }

    @Override
    public void phoneCodeFail(Integer code, String toastMessage) {
        gt3GeetestUtils.gt3Dismiss();
        tvGetCode.setEnabled(true);
        WonderfulCodeUtils.checkedErrorCode(getmActivity(), code, toastMessage);
    }

    @Override
    public void signUpByPhoneSuccess(String obj) {
        WonderfulToastUtils.showToast(obj);
        finish();

    }

    @Override
    public void signUpByPhoneFail(Integer code, String toastMessage) {
        WonderfulCodeUtils.checkedErrorCode(getmActivity(), code, toastMessage);
    }

    @Override
    public void captchSuccess(JSONObject obj) {

//        gt3GeetestUtils.gtSetApi1Json(obj);
//        gt3GeetestUtils.getGeetest(getActivity(), UrlFactory.getCaptchaUrl(), null, null, new GT3GeetestBindListener() {
//            @Override
//            public boolean gt3SetIsCustom() {
//                return true;
//            }
//
//            @Override
//            public void gt3GetDialogResult(boolean status, String result) {
//
//                if (status) {
//                    Captcha captcha = new Gson().fromJson(result, Captcha.class);
//                    if (captcha == null) return;
//                     challenge = captcha.getchallenge();
//                     validate = captcha.getvalidate();
//                     seccode = captcha.getseccode();
//                    presenter.phoneCode(phone, countryStr, challenge, validate, seccode);
//                    tvGetCode.setEnabled(false);
//
//                }
//            }
//        });
//        gt3GeetestUtils.setDialogTouch(true);

    }

    @Override
    public void captchFail(Integer code, String toastMessage) {
        // do nothing

    }

    @Override
    protected String getmTag() {
        return TAG;
    }
}
