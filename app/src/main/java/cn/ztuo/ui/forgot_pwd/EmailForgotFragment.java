package cn.ztuo.ui.forgot_pwd;

import android.content.Context;
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
import cn.ztuo.base.BaseTransFragment;
import cn.ztuo.entity.Captcha;
import cn.ztuo.utils.WonderfulCodeUtils;
import cn.ztuo.utils.WonderfulStringUtils;
import cn.ztuo.utils.WonderfulToastUtils;

import org.json.JSONObject;

import butterknife.BindView;
import cn.ztuo.app.Injection;

/**
 * Created by Administrator on 2018/2/2.
 */

public class EmailForgotFragment extends BaseTransFragment implements ForgotPwdContract.EmailView {
    public static final String TAG = EmailForgotFragment.class.getSimpleName();
    @BindView(R.id.ibBack)
    ImageButton ibBack;
    @BindView(R.id.tvChangeType)
    TextView tvChangeType;
    @BindView(R.id.llTitle)
    LinearLayout llTitle;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etCode)
    EditText etCode;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.etRePassword)
    EditText etRePassword;
    @BindView(R.id.tvSubmit)
    TextView tvSubmit;
    @BindView(R.id.tvBack)
    TextView tvBack;
    @BindView(R.id.tvGetCode)
    TextView tvGetCode;
    private CountDownTimer timer;
    private ForgotPwdContract.EmailPresenter presenter;
    private GT3GeetestUtilsBind gt3GeetestUtils;

    public static EmailForgotFragment getInstance() {
        EmailForgotFragment emailForgotFragment = new EmailForgotFragment();
        return emailForgotFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(getActivity() instanceof BaseForgotFragment.OperateCallback)) {
            throw new RuntimeException("fragment所在的Activity必须实现OperateCallback接口！");
        }
    }

    @Override
    public void onDestroyView() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        super.onDestroyView();
        gt3GeetestUtils.cancelUtils();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_email_forgot;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        gt3GeetestUtils = new GT3GeetestUtilsBind(getActivity());
        new EmailForgotPresenter(Injection.provideTasksRepository(getActivity().getApplicationContext()), this);
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
                ((BaseForgotFragment.OperateCallback) getActivity()).switchType(BaseForgotFragment.Type.PHONE);
            }
        });
        tvGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCode();
            }
        });
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    private void submit() {
        String account = etEmail.getText().toString();
        String code = etCode.getText().toString();
        String mode = "1";
        String password = etPassword.getText().toString();
        String passwordRe = etRePassword.getText().toString();

        if (WonderfulStringUtils.isEmpty(account, code, mode, password, passwordRe)) return;
        if (!password.equals(passwordRe)) {
            WonderfulToastUtils.showToast(WonderfulToastUtils.getString(R.string.pwd_diff));
            return;
        }
        presenter.forgotPwd(account, code, mode, password);
    }

    private void getCode() {
        String email = etEmail.getText().toString();
        if (WonderfulStringUtils.isEmpty(email) || !email.contains("@")) {
            WonderfulToastUtils.showToast(WonderfulToastUtils.getString(R.string.email_diff));
        }

        presenter.captch();
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
    public void setPresenter(ForgotPwdContract.EmailPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void emailForgotCodeSuccess(String obj) {
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
    public void emailForgotCodeFail(Integer code, String toastMessage) {
        WonderfulCodeUtils.checkedErrorCode(getmActivity(), code, toastMessage);
    }

    @Override
    public void forgotPwdSuccess(String obj) {
        WonderfulToastUtils.showToast(obj);
        finish();
    }

    @Override
    public void forgotPwdFail(Integer code, String toastMessage) {
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
                    presenter.emailForgotCode(email, challenge, validate, seccode);
                    tvGetCode.setEnabled(false);
                }
            }
        });
        gt3GeetestUtils.setDialogTouch(true);
    }

    @Override
    public void captchFail(Integer code, String toastMessage) {

    }

    @Override
    protected String getmTag() {
        return TAG;
    }
}
