package cn.ztuo.ui.signup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geetest.sdk.Bind.GT3GeetestBindListener;
import com.geetest.sdk.Bind.GT3GeetestUtilsBind;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import cn.ztuo.R;
import cn.ztuo.ui.country.CountryActivity;
import cn.ztuo.base.BaseTransFragment;
import cn.ztuo.entity.Captcha;
import cn.ztuo.entity.Country;
import cn.ztuo.utils.WonderfulCodeUtils;
import cn.ztuo.utils.WonderfulStringUtils;
import cn.ztuo.utils.WonderfulToastUtils;

import org.json.JSONObject;

import butterknife.BindView;
import cn.ztuo.app.Injection;

/**
 * Created by Administrator on 2018/2/2.
 */

public class EmailSignUpFragment extends BaseTransFragment implements SignUpContract.EmailView {
    public static final String TAG = EmailSignUpFragment.class.getSimpleName();
    @BindView(R.id.ibBack)
    ImageButton ibBack;
    @BindView(R.id.tvChangeType)
    TextView tvChangeType;
    @BindView(R.id.llTitle)
    LinearLayout llTitle;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etUserName)
    EditText etUserName;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.etRePassword)
    EditText etRePassword;
    @BindView(R.id.tvSignUp)
    TextView tvSignUp;
    @BindView(R.id.tvBack)
    TextView tvBack;
    @BindView(R.id.tvGetCode)
    TextView tvGetCode;
    @BindView(R.id.tvCountry)
    TextView tvCountry;
    @BindView(R.id.tuijian)
    EditText tuijian;
    private Country country;
    private CountDownTimer timer;
    private SignUpContract.EmailPresenter presenter;
    private GT3GeetestUtilsBind gt3GeetestUtils;

    public static EmailSignUpFragment getInstance() {
        EmailSignUpFragment emailSignUpFragment = new EmailSignUpFragment();
        return emailSignUpFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(getActivity() instanceof BaseSignUpFragment.OperateCallback)) {
            throw new RuntimeException("The Activity which this fragment is located must implement the OperateCallback interface!");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        gt3GeetestUtils.cancelUtils();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CountryActivity.RETURN_COUNTRY) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                country = (Country) data.getSerializableExtra("country");
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_email_sign_up;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        gt3GeetestUtils = new GT3GeetestUtilsBind(getActivity());
        new EmailSignUpPresenter(Injection.provideTasksRepository(getActivity().getApplicationContext()), this);
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
                ((BaseSignUpFragment.OperateCallback) getActivity()).switchType(BaseSignUpFragment.Type.PHONE);
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
                signUpByEmail();
            }
        });
        tvCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountryActivity.actionStart(EmailSignUpFragment.this);
            }
        });
    }

    private String countryStr;

    private void signUpByEmail() {
        String email = etEmail.getText().toString();
        String username = etUserName.getText().toString();
        String password = etPassword.getText().toString();
        String rePassword = etRePassword.getText().toString();
        String tuijian2 = tuijian.getText().toString();
        if (country == null) countryStr = "中国";
        else countryStr = country.getZhName();
        if (WonderfulStringUtils.isEmpty(email, username, password, rePassword,countryStr) || !email.contains("@")) return;
        if (!password.equals(rePassword)) {
            WonderfulToastUtils.showToast(R.string.pwd_diff);
            return;
        }else presenter.captch();
    }

    private void getCode() {
        String phone = etEmail.getText().toString();
        if (WonderfulStringUtils.isEmpty(phone) || !phone.contains("@")) {
            WonderfulToastUtils.showToast(R.string.email_not_correct);
            gt3GeetestUtils.gt3Dismiss();
        }
//        presenter.emailCode(phone);
        tvGetCode.setEnabled(false);
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
    public void setPresenter(SignUpContract.EmailPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void signUpByEmailSuccess(String obj) {
        gt3GeetestUtils.gt3TestFinish();
        WonderfulToastUtils.showToast(obj);
        finish();
    }

    @Override
    public void signUpByEmailFail(Integer code, String toastMessage) {
        gt3GeetestUtils.gt3Dismiss();
        WonderfulCodeUtils.checkedErrorCode(getmActivity(), code, toastMessage);
    }

    @Override
    public void captchSuccess(JSONObject obj) {
        gt3GeetestUtils.gtSetApi1Json(obj);
        gt3GeetestUtils.getGeetest(getActivity(), null, null, null, new GT3GeetestBindListener() {
            @Override
            public boolean gt3SetIsCustom() {
                return true;
            }

            @Override
            public void gt3GetDialogResult(boolean status, String result) {
                if (status) {
                    Captcha captcha = new Gson().fromJson(result, Captcha.class);
                    if (captcha == null) return;
                    String challenge = captcha.getGeetest_challenge();
                    String validate = captcha.getGeetest_validate();
                    String seccode = captcha.getGeetest_seccode();
                    String email = etEmail.getText().toString();
                    String username = etUserName.getText().toString();
                    String password = etPassword.getText().toString();
                    String rePassword = etRePassword.getText().toString();
                    String tuijian2 = tuijian.getText().toString();
                    if (password.equals(rePassword)) {
                        presenter.signUpByEmail(email, username, password, countryStr, challenge, validate, seccode,tuijian2);
                        tvGetCode.setEnabled(false);
                    }else {
                        WonderfulToastUtils.showToast(WonderfulToastUtils.getString(R.string.pwd_diff));
                        return;
                    }

                }
            }
        });
        gt3GeetestUtils.setDialogTouch(true);
    }

    @Override
    public void captchFail(Integer code, String toastMessage) {
        gt3GeetestUtils.gt3Dismiss();
    }

    @Override
    protected String getmTag() {
        return TAG;
    }
}
