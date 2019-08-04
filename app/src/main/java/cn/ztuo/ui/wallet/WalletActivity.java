package cn.ztuo.ui.wallet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;
import cn.ztuo.R;
import cn.ztuo.ui.login.LoginActivity;
import cn.ztuo.adapter.TextWatcher;
import cn.ztuo.adapter.WalletAdapter;
import cn.ztuo.app.MyApplication;
import cn.ztuo.base.BaseActivity;
import cn.ztuo.entity.Coin;
import cn.ztuo.entity.GccMatch;
import cn.ztuo.utils.SharedPreferenceInstance;
import cn.ztuo.utils.WonderfulCodeUtils;
import cn.ztuo.utils.WonderfulDpPxUtils;
import cn.ztuo.utils.WonderfulLogUtils;
import cn.ztuo.utils.WonderfulMathUtils;
import cn.ztuo.utils.WonderfulStringUtils;
import cn.ztuo.utils.WonderfulToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.ztuo.app.Injection;

public class WalletActivity extends BaseActivity implements WalletContract.View,WalletDialogFragment.OperateCallback{

    @BindView(R.id.ibBack)
    ImageButton ibBack;
    @BindView(R.id.ibDetail)
    TextView ibDetail;
    @BindView(R.id.llTitle)
    LinearLayout llTitle;
    @BindView(R.id.tvAmount)
    TextView tvAmount;
    @BindView(R.id.tvCnyAmount)
    TextView tvCnyAmount;
    @BindView(R.id.llAccount)
    LinearLayout llAccount;
    @BindView(R.id.rvWallet)
    RecyclerView rvWallet;
    @BindView(R.id.ivSee)
    ImageView ivSee;
    @BindView(R.id.llContainer)
    LinearLayout llContainer;
    @BindView(R.id.ivSearch)
    ImageView ivSearch;
    @BindView(R.id.evSearch)
    EditText evSearch;
    @BindView(R.id.cbHide)
    CheckBox cbHide;
    @BindView(R.id.view_back)
    View view_back;

    private List<Coin> coins = new ArrayList<>();
    private List<Coin> removeCoins = new ArrayList<>();
    private List<Coin> keepCoins = new ArrayList<>();
    private List<Coin> zeroCoins = new ArrayList<>();
    private WalletAdapter adapter;
    double sumUsd = 0;
    double sumCny = 0;
    private WalletContract.Presenter presenter;
    private WalletDialogFragment walletDialogFragment;
    private PopupWindow popWnd;
    private List<String> lists_chong=new ArrayList<>();
    private List<String> lists_ti=new ArrayList<>();
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, WalletActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_wallet;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            displayLoadingPopup();
            finish();
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }
    @Override
    protected void initViews(Bundle savedInstanceState) {
        new WalletPresenter(Injection.provideTasksRepository(getApplicationContext()), this);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayLoadingPopup();
                finish();
            }
        });
        view_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayLoadingPopup();
                finish();
            }
        });

        ivSee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchSee();
            }
        });
        cbHide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                        hideZero();
                }else {
                    cancalHide();
                }
            }
        });
    }

    private void hideZero() {
        zeroCoins.clear();
        for (int i = 0; i < coins.size(); i++) {
            if (coins.get(i).getBalance() == 0) {
                zeroCoins.add(coins.get(i));
            }
        }
        WonderfulLogUtils.logi("WalletActivity  ","zeroCoins"+zeroCoins.size());
        coins.removeAll(zeroCoins);
        adapter.notifyDataSetChanged();
    }

    private void cancalHide() {
        WonderfulLogUtils.logi("WalletActivity  ","cancalHide");
        coins.addAll(zeroCoins);
        adapter.notifyDataSetChanged();
    }

    private void switchSee() {
        if (!"*****".equals(tvCnyAmount.getText())) {
            tvCnyAmount.setText("*****");
            tvAmount.setText("********");
            Drawable drawable = getResources().getDrawable(R.drawable.icon_eye_guan);
            ivSee.setImageDrawable(drawable);
            SharedPreferenceInstance.getInstance().saveMoneyShowtype(2);
        } else {
            tvAmount.setText(WonderfulMathUtils.getRundNumber(sumUsd, 6, null));
            tvCnyAmount.setText(WonderfulMathUtils.getRundNumber(sumCny, 2, null) + "CNY");
            Drawable drawable = getResources().getDrawable(R.drawable.icon_eye_open);
            ivSee.setImageDrawable(drawable);
            SharedPreferenceInstance.getInstance().saveMoneyShowtype(1);
        }
    }

    @Override
    protected ViewGroup getEmptyView() {
        return llContainer;
    }

    @Override
    protected void obtainData() {

    }

    @Override
    protected void fillWidget() {
        initRvWallet();
    }

    private void initRvWallet() {
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvWallet.setLayoutManager(manager);
        adapter = new WalletAdapter(R.layout.adapter_wallet, coins);
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        adapter.isFirstOnly(true);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (coins.get(position).getCoin().getCanRecharge() == 0 &&  coins.get(position).getCoin().getCanWithdraw() == 0) {
                    WonderfulToastUtils.showToast(WonderfulToastUtils.getString(R.string.notRaiseMoneyTip));
                }else {
                        walletDialogFragment = WalletDialogFragment.getInstance(coins.get(position), false,WalletActivity.this);
                        walletDialogFragment.show(getSupportFragmentManager(), "WDF");
                }
            }
        });
        rvWallet.setAdapter(adapter);
    }





    @Override
    protected void loadData() {
        ibDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View popupView = getLayoutInflater().inflate(R.layout.popupwindow_ctb, null);
                // ReleaseAdsActivity.actionStart(getActivity(), "SELL", null);
                LinearLayout my_chong=popupView.findViewById(R.id.my_chong);
                my_chong.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChongBiJLActivity.actionStart(WalletActivity.this,lists_chong);
                    }
                });
                LinearLayout my_ti=popupView.findViewById(R.id.my_ti);
                my_ti.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TiBiJLActivity.actionStart(WalletActivity.this,lists_ti);
                    }
                });
                PopupWindow window = new PopupWindow(popupView,WonderfulDpPxUtils.dip2px(WalletActivity.this, 120), WonderfulDpPxUtils.dip2px(WalletActivity.this, 80));
                window.setOutsideTouchable(true);
                window.setTouchable(true);
                window.setFocusable(true);
                window.setBackgroundDrawable(WalletActivity.this.getResources().getDrawable(R.drawable.my_bg));
                window.update();
                window.getContentView().measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
                int i = window.getWidth();
                int width = ibDetail.getWidth();
                WonderfulLogUtils.logi("miao",width+"便宜度");
                WonderfulLogUtils.logi("miao",i+"便宜度");
                window.showAsDropDown(ibDetail,-(i-width), 0);

            }
        });
        if (MyApplication.getApp().isLogin()) {
            ibDetail.setEnabled(true);
            presenter.myWallet(getToken());

        } else {
            ibDetail.setEnabled(false);
            showToLoginView();
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LoginActivity.RETURN_LOGIN:
                if (resultCode == Activity.RESULT_OK) {
                    hideToLoginView();
                    loadData();
                }
                break;
                default:
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        walletDialogFragment.dismiss();
    }

    @Override
    public void setPresenter(WalletContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void myWalletSuccess(List<Coin> obj) {
        if (evSearch==null){
            return;
        }
        if (obj == null) return;
        this.coins.clear();
        this.coins.addAll(obj);
        keepCoins.addAll(obj);
        lists_chong.clear();
        lists_ti.clear();
        for (int i=0;i<coins.size();i++){
            if (coins.get(i).getCoin().getCanRecharge() == 1){
                lists_chong.add(coins.get(i).getCoin().getUnit());
            }else if(coins.get(i).getCoin().getCanWithdraw() == 1){
                lists_ti.add(coins.get(i).getCoin().getUnit());
            }
        }
        adapter.notifyDataSetChanged();
        evSearch.addTextChangedListener(localChangeWatcher);
        calcuTotal();
    }

    private void calcuTotal() {
        sumUsd = 0;
        sumCny = 0;
        for (Coin coin : coins) {
            sumUsd += (coin.getBalance() * coin.getCoin().getUsdRate());
            sumCny += (coin.getBalance() * coin.getCoin().getCnyRate());
        }
        if (SharedPreferenceInstance.getInstance().getMoneyShowType() == 1) {
            tvAmount.setText(WonderfulMathUtils.getRundNumber(sumUsd, 6, null));
            tvCnyAmount.setText(WonderfulMathUtils.getRundNumber(sumCny, 2, null) + " CNY");
        } else if (SharedPreferenceInstance.getInstance().getMoneyShowType() == 2) {
            tvAmount.setText("********");
            tvCnyAmount.setText("*****");
        }
    }

    @Override
    public void myWalletFail(Integer code, String toastMessage) {
        WonderfulCodeUtils.checkedErrorCode(this, code, toastMessage);
    }

    @Override
    public void getCheckMatchSuccess(GccMatch obj) {
        showPopWindow(obj.getData());
    }

    private void showPopWindow(final double data) {
        View contentView = LayoutInflater.from(WalletActivity.this).inflate(R.layout.pop_gcc, null);
        popWnd = new PopupWindow(WalletActivity.this);
        popWnd.setContentView(contentView);
        popWnd.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popWnd.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popWnd.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 设置PopupWindow是否能响应外部点击事件
        popWnd.setOutsideTouchable(true);
        // 设置PopupWindow是否能响应点击事件
        popWnd.setTouchable(true);
        popWnd.setFocusable(true);
        popWnd.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        darkenBackground(0.4f);
        TextView confirm = contentView.findViewById(R.id.tvGccConfirm);
        TextView maxAmount = contentView.findViewById(R.id.tvMaxMatcchAmount);
        final EditText matchAmount = contentView.findViewById(R.id.evMatcchAmount);
       /* matchAmount.setFocusable(true);
        matchAmount.requestFocus();
        InputMethodManager imm = (InputMethodManager) matchAmount.getContext().getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);*/

        maxAmount.setText(String.valueOf(data));
        confirm.requestFocus();
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = matchAmount.getText().toString();
                if (!WonderfulStringUtils.isEmpty(amount)) {
                    if (Double.valueOf(amount) > 0 && Double.valueOf(amount) <= data) {
                        presenter.getStartMatch(getToken(),amount);
                    }
                }
            }
        });
        View rootview = LayoutInflater.from(WalletActivity.this).inflate(R.layout.activity_wallet, null);
        popWnd.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);
        popWnd.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                darkenBackground(1f);
            }
        });
    }

    /**
     * 改变背景颜色
     */
    private void darkenBackground(Float bgcolor){
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgcolor;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);

    }


    @Override
    public void getCheckMatchFail(Integer code, String toastMessage) {
        //showPopWindow(20);
        WonderfulCodeUtils.checkedErrorCode(this, code, toastMessage);
    }

    @Override
    public void getStartMatchSuccess(String obj) {
        popWnd.dismiss();
        WonderfulToastUtils.showToast(obj);
    }

    @Override
    public void getStartMatchFail(Integer code, String toastMessage) {
        popWnd.dismiss();
        WonderfulCodeUtils.checkedErrorCode(this, code, toastMessage);
    }

    private TextWatcher localChangeWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            removeCoins.clear();
            localCoinChange();
        }
    };

    private void localCoinChange() {
        String str = evSearch.getText().toString().toUpperCase();
        if (str.isEmpty()) {
            coins.clear();
            coins.addAll(keepCoins);
            adapter.notifyDataSetChanged();
        }else{
            for (int i = 0; i < coins.size(); i++) {
                if (!coins.get(i).getCoin().getUnit().contains(str)) {
                    removeCoins.add(coins.get(i));
                }
            }
            coins.removeAll(removeCoins);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void toMatch() {
        presenter.getCheckMatch(getToken());
    }
}
