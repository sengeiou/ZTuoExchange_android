package cn.ztuo.ui.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geetest.sdk.Bind.GT3GeetestBindListener;
import com.geetest.sdk.Bind.GT3GeetestUtilsBind;
import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import cn.ztuo.R;
import cn.ztuo.ui.forgot_pwd.ForgotPwdActivity;
import cn.ztuo.ui.home.MainActivity;
import cn.ztuo.ui.signup.SignUpActivity;
import cn.ztuo.app.MyApplication;
import cn.ztuo.base.BaseActivity;
import cn.ztuo.entity.Captcha;
import cn.ztuo.entity.User;
import cn.ztuo.utils.SharedPreferenceInstance;
import cn.ztuo.utils.EncryUtils;
import cn.ztuo.utils.WonderfulCodeUtils;
import cn.ztuo.utils.WonderfulCommonUtils;
import cn.ztuo.utils.WonderfulLogUtils;
import cn.ztuo.utils.WonderfulStringUtils;
import cn.ztuo.utils.WonderfulToastUtils;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import cn.ztuo.app.Injection;

public class LoginActivity extends BaseActivity implements LoginContract.View {
    public static final int RETURN_LOGIN = 0;
    @BindView(R.id.ibBack)
    TextView ibBack;
    @BindView(R.id.ibRegist)
    TextView ibRegist;
    @BindView(R.id.llTitle)
    LinearLayout llTitle;
    @BindView(R.id.etUsername)
    EditText etUsername;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.tvLogin)
    TextView tvLogin;
    @BindView(R.id.tvForgetPas)
    TextView tvForgetPas;
    @BindView(R.id.tvToRegist)
    TextView tvToRegist;
    @BindView(R.id.yan)
    ImageView yan;
    private boolean isYan=false;
    private LoginContract.Presenter presenter;
    private GT3GeetestUtilsBind gt3GeetestUtils;
    private Handler handler = new Handler();

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        gt3GeetestUtils.cancelUtils();
        super.onDestroy();
    }

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        gt3GeetestUtils = new GT3GeetestUtilsBind(this);
        new LoginPresenter(Injection.provideTasksRepository(getApplicationContext()), this);

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvToRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpActivity.actionStart(LoginActivity.this);
            }
        });
        ibRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpActivity.actionStart(LoginActivity.this);
            }
        });
        tvForgetPas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotPwdActivity.actionStart(LoginActivity.this);
            }
        });
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        yan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isYan=!isYan;
                Drawable no = getResources().getDrawable(R.drawable.yan_no);
                Drawable yes = getResources().getDrawable(R.drawable.yan_yes);
                if (isYan){
                    //显示
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    yan.setImageDrawable(no);

                }else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    yan.setImageDrawable(yes);
                }
            }
        });
    }

    private void login() {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        if (WonderfulStringUtils.isEmpty(username, password)) {
            WonderfulToastUtils.showToast("请输入账号密码！");
            return;}
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
            ImmersionBar.setTitleBar(this, llTitle);
            isSetTitle = true;
        }
    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void loginFail(Integer code, String toastMessage) {
        gt3GeetestUtils.gt3Dismiss();
        WonderfulCodeUtils.checkedErrorCode(this, code, toastMessage);
    }

    @Override
    public void loginSuccess(User obj) {
        MyApplication.getApp().setCurrentUser(null);
        MainActivity.isAgain = true;
        String key = WonderfulCommonUtils.getSerialNumber() + etUsername.getText().toString() + etPassword.getText().toString();
        String md5Key = getMD5(key);
        SharedPreferenceInstance.getInstance().saveToken(EncryUtils.getInstance().encryptString(md5Key, MyApplication.getApp().getPackageName()));
        MyApplication.getApp().setLoginStatusChange(true);
        gt3GeetestUtils.gt3TestFinish();
        SharedPreferenceInstance.getInstance().saveLockPwd("");
        MyApplication.getApp().setCurrentUser(obj);
        SharedPreferenceInstance.getInstance().saveID(obj.getId());
        SharedPreferenceInstance.getInstance().saveTOKEN(obj.getToken());
        SharedPreferenceInstance.getInstance().saveaToken(EncryUtils.getInstance().decryptString(SharedPreferenceInstance.getInstance().getToken(), MyApplication.getApp().getPackageName()));
        setResult(RESULT_OK);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 600);
    }

    public String getMD5(String info) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes("UTF-8"));
            byte[] encryption = md5.digest();
            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                    strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
                } else {
                    strBuf.append(Integer.toHexString(0xff & encryption[i]));
                }
            }
            return strBuf.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    @Override
    public void captchSuccess(JSONObject obj) {
        gt3GeetestUtils.gtSetApi1Json(obj);
        gt3GeetestUtils.getGeetest(this, null, null, null, new GT3GeetestBindListener() {
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
                    WonderfulLogUtils.logi("LoginActivity","challenge  "+challenge+"   validate   "+validate+"   seccode   "+seccode);
                    String username = etUsername.getText().toString();
                    String password = etPassword.getText().toString();
                    presenter.login(username, password, challenge, validate, seccode);
                }
            }
        });
        gt3GeetestUtils.setDialogTouch(true);
    }

    @Override
    public void captchFail(Integer code, String toastMessage) {
        //do nothing
    }
}
