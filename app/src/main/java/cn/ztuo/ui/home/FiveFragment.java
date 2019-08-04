package cn.ztuo.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gyf.barlibrary.ImmersionBar;
import cn.ztuo.R;
import cn.ztuo.ui.bind_account.BindAccountActivity;
import cn.ztuo.ui.entrust.TrustListActivity;
import cn.ztuo.ui.login.LoginActivity;
import cn.ztuo.ui.my_ads.AdsActivity;
import cn.ztuo.ui.my_order.MyOrderActivity;
import cn.ztuo.ui.myinfo.MyInfoActivity;
import cn.ztuo.ui.setting.SettingActivity;
import cn.ztuo.ui.wallet.WalletActivity;
import cn.ztuo.ui.wallet_detail.WalletDetailActivity;
import cn.ztuo.app.MyApplication;
import cn.ztuo.base.BaseTransFragment;
import cn.ztuo.entity.Coin;
import cn.ztuo.entity.SafeSetting;
import cn.ztuo.entity.User;
import cn.ztuo.app.UrlFactory;
import cn.ztuo.utils.SharedPreferenceInstance;
import cn.ztuo.customview.intercept.WonderfulScrollView;
import cn.ztuo.utils.WonderfulStringUtils;
import cn.ztuo.utils.WonderfulToastUtils;
import cn.ztuo.utils.okhttp.StringCallback;
import cn.ztuo.utils.okhttp.WonderfulOkhttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Request;

/**
 * Created by Administrator on 2018/1/29.
 */

public class FiveFragment extends BaseTransFragment implements MainContract.FiveView {
    public static final String TAG = FiveFragment.class.getSimpleName();
    @BindView(R.id.llTop)
    LinearLayout llTop;
    @BindView(R.id.llAccount)
    LinearLayout llAccount;
    @BindView(R.id.llOrder)
    LinearLayout llOrder;
    @BindView(R.id.llAds)
    LinearLayout llAds;
    @BindView(R.id.line_top)
    LinearLayout line_top;
    @BindView(R.id.line_zican)
    LinearLayout line_zican;
    @BindView(R.id.line_bibi)
    LinearLayout line_bibi;
    @BindView(R.id.llSafe)
    ImageView llSafe;
    @BindView(R.id.llSettings)
    ImageView llSettings;
    @BindView(R.id.llMyinfo)
    LinearLayout llMyinfo;
    @BindView(R.id.tvNickName)
    TextView tvNickName;
    @BindView(R.id.tvAccount)
    TextView tvAccount;

    @BindView(R.id.ivHeader)
    ImageView ivHeader;
    public static double sumUsd = 0;
    double sumCny = 0;
    @BindView(R.id.scrollView)
    WonderfulScrollView scrollView;
    @BindView(R.id.llEntrust)
    LinearLayout llEntrust;
    Unbinder unbinder;
    private MainContract.FivePresenter presenter;
    private SafeSetting safeSetting;
    private PopupWindow loadingPopup;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_five;
    }

    /**
     * 初始化加载dialog
     */
    private void initLoadingPopup() {
        View loadingView = getLayoutInflater().inflate(R.layout.pop_loading, null);
        loadingPopup = new PopupWindow(loadingView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        loadingPopup.setFocusable(true);
        loadingPopup.setClippingEnabled(false);
        loadingPopup.setBackgroundDrawable(new ColorDrawable());
    }

    /**
     * 显示加载框
     */
    @Override
    public void displayLoadingPopup() {
        loadingPopup.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    /**
     * 隐藏加载框
     */
    @Override
    public void hideLoadingPopup() {
        if (loadingPopup != null) {
            loadingPopup.dismiss();
        }

    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        biaoshi = -1;
        initLoadingPopup();


        line_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.getApp().isLogin()) {
                    displayLoadingPopup();
                    accountClick();
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginActivity.class), LoginActivity.RETURN_LOGIN);
                }
            }
        });
        llAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.getApp().isLogin()) {
                    displayLoadingPopup();
                    WalletActivity.actionStart(getActivity());
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginActivity.class), LoginActivity.RETURN_LOGIN);
                }

            }
        });

        line_zican.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.getApp().isLogin()) {
                    displayLoadingPopup();
                    WalletDetailActivity.actionStart(getmActivity());
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginActivity.class), LoginActivity.RETURN_LOGIN);
                }

            }
        });


        line_bibi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.getApp().isLogin()) {
                    displayLoadingPopup();
                    TrustListActivity.show(getActivity());
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginActivity.class), LoginActivity.RETURN_LOGIN);
                }

            }
        });
        llOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.getApp().isLogin()) {
                    displayLoadingPopup();
                    MyOrderActivity.actionStart(getActivity(), 0);
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginActivity.class), LoginActivity.RETURN_LOGIN);
                }


            }
        });
        llAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.getApp().isLogin()) {
                    if (MyApplication.realVerified == 1) {
                        WonderfulOkhttpUtils.get().url(UrlFactory.getShangjia())
                                .addHeader("x-auth-token", SharedPreferenceInstance.getInstance().getTOKEN())
                                .build().execute(new StringCallback() {
                            @Override
                            public void onError(Request request, Exception e) {

                            }

                            @Override
                            public void onResponse(String response) {
                                Log.i("miao", "商家认证" + response);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    int code = jsonObject.optInt("code");
                                    if (code == 0) {
                                        JSONObject jsonObject1 = jsonObject.optJSONObject("data");
                                        int certifiedBusinessStatus = jsonObject1.optInt("certifiedBusinessStatus");
                                        if (certifiedBusinessStatus == 2) {
                                            displayLoadingPopup();
                                            AdsActivity.actionStart(getActivity(), MyApplication.getApp().getCurrentUser().getUsername(), MyApplication.getApp().getCurrentUser().getAvatar());
                                        } else {
                                            WonderfulToastUtils.showToast(WonderfulToastUtils.getString(R.string.shangjia));
                                        }
                                    } else {
                                        WonderfulToastUtils.showToast(WonderfulToastUtils.getString(R.string.unknown_error));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginActivity.class), LoginActivity.RETURN_LOGIN);
                }
            }
        });
        llSafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toLoginOrCenter();
            }
        });
        llSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.getApp().isLogin()) {
                    displayLoadingPopup();
                    SettingActivity.actionStart(getActivity());
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginActivity.class), LoginActivity.RETURN_LOGIN);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideLoadingPopup();

    }

    @Override
    public void onStart() {
        super.onStart();
        hideLoadingPopup();
    }


    private void toLoginOrCenter() {
        if (MyApplication.getApp().isLogin()) {
            MyInfoActivity.actionStart(getActivity());
        } else {
            startActivityForResult(new Intent(getActivity(), LoginActivity.class), LoginActivity.RETURN_LOGIN);
        }
    }

    @Override
    protected void obtainData() {
    }

    @Override
    protected void fillWidget() {
    }

    @Override
    protected void loadData() {
        if (MyApplication.getApp().isLogin()) {
            loginingViewText();
        } else {
            notLoginViewText();
        }
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        if (!isSetTitle) {
            ImmersionBar.setTitleBar(getActivity(), llMyinfo);
            isSetTitle = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LoginActivity.RETURN_LOGIN:
                if (resultCode == Activity.RESULT_OK && getUserVisibleHint() && MyApplication.getApp().isLogin()) {
                    loginingViewText();
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    notLoginViewText();
                }
                break;
            default:
        }
    }

    private void notLoginViewText() {
        try {
            sumCny = 0.00;
            sumUsd = 0.000000;
            tvNickName.setText(WonderfulToastUtils.getString(R.string.not_logged_in));
            Glide.with(getActivity().getApplicationContext()).load(R.mipmap.icon_default_header).into(ivHeader);
        } catch (Exception e) {

        }

    }

    private void loginingViewText() {
        try {
            presenter.myWallet(getmActivity().getToken());
            presenter.safeSetting(getmActivity().getToken());
            User user = MyApplication.getApp().getCurrentUser();
            tvNickName.setText(user.getUsername());
            if (!WonderfulStringUtils.isEmpty(user.getAvatar())) {
                Glide.with(getActivity().getApplicationContext()).load(user.getAvatar()).into(ivHeader);
            } else {
                Glide.with(getActivity().getApplicationContext()).load(R.mipmap.icon_default_header).into(ivHeader);
            }
        } catch (Exception e) {

        }

    }

    @Override
    public void setPresenter(MainContract.FivePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void myWalletSuccess(List<Coin> obj) {
        if (obj == null) {
            return;
        }
        calcuTotal(obj);
    }

    private void calcuTotal(List<Coin> coins) {
        sumUsd = 0;
        sumCny = 0;
        for (Coin coin : coins) {
            sumUsd += (coin.getBalance() * coin.getCoin().getUsdRate());
            sumCny += (coin.getBalance() * coin.getCoin().getCnyRate());
        }
//        if (SharedPreferenceInstance.getInstance().getMoneyShowType() == 1) {
//            if (WonderfulMathUtils.getRundNumber(sumUsd, 6, null)==null||"null".equals(WonderfulMathUtils.getRundNumber(sumUsd, 6, null))){
//                tvAmount.setText("0.000000");
//            }else {
//                tvAmount.setText(WonderfulMathUtils.getRundNumber(sumUsd, 6, null));
//            }
////            tvAmount.setText(WonderfulMathUtils.getRundNumber(sumUsd, 6, null));
//            tvCnyAmount.setText(WonderfulMathUtils.getRundNumber(sumCny, 2, null) + " CNY");
//        } else if (SharedPreferenceInstance.getInstance().getMoneyShowType() == 2) {
//            tvAmount.setText("********");
//            tvCnyAmount.setText("*****");
//        }
    }

    @Override
    public void myWalletFail(Integer code, String toastMessage) {
//        WonderfulCodeUtils.checkedErrorCode(getmActivity(), code, toastMessage);
        biaoshi = 1;
//        SharedPreferenceInstance.getInstance().saveIsNeedShowLock(false);
//        SharedPreferenceInstance.getInstance().saveLockPwd("");
        MyApplication.getApp().setCurrentUser(null);
        notLoginViewText();
        if (code == 4000) {
//            MyApplication.getApp().loginAgain(getmActivity());
            SharedPreferenceInstance.getInstance().saveaToken("");
            SharedPreferenceInstance.getInstance().saveTOKEN("");
        }


    }

    private void accountClick() {
        if (safeSetting == null) {
            return;
        }
        hideLoadingPopup();
        if (safeSetting.getRealVerified() == 1 && safeSetting.getFundsVerified() == 1) {
            BindAccountActivity.actionStart(getmActivity());
        } else {
            WonderfulToastUtils.showToast(WonderfulToastUtils.getString(R.string.password_realname));
        }


    }

    private int biaoshi = -1;

    @Override
    public void safeSettingSuccess(SafeSetting obj) {
        if (obj == null) {
            return;
        }
        this.safeSetting = obj;
        MyApplication.number = safeSetting.getMobilePhone();

//        if (tvIdCredit==null){
//            return;
//        }
//        if (safeSetting.getRealVerified() == 1) {
//            tvIdCredit.setEnabled(false);
//            tvIdCredit.setText(R.string.verification);
//        } else if (safeSetting.getRealAuditing() == 1) {
//            tvIdCredit.setEnabled(false);
//            tvIdCredit.setText(R.string.creditting);
//        } else {
//            tvIdCredit.setEnabled(true);
//            tvIdCredit.setText(R.string.unverified);
//        }
    }

    @Override
    public void safeSettingFail(Integer code, String toastMessage) {
        if (code == 4000) {
            MyApplication.getApp().setCurrentUser(null);
            SharedPreferenceInstance.getInstance().saveaToken("");
            SharedPreferenceInstance.getInstance().saveTOKEN("");
            notLoginViewText();
        }
        //do nothing
    }

    @Override
    protected String getmTag() {
        return TAG;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
