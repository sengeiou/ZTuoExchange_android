package cn.ztuo.ui.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.ztuo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.ztuo.app.MyApplication;
import cn.ztuo.base.BaseDialogFragment;
import cn.ztuo.utils.WonderfulDpPxUtils;
import cn.ztuo.utils.WonderfulToastUtils;

/**
 * Created by Administrator on 2018/3/14.
 */

public class BBConfirmDialogFragment extends BaseDialogFragment {
    @BindView(R.id.tvPrice)
    TextView tvPrice;
    @BindView(R.id.ll1)
    LinearLayout ll1;
    @BindView(R.id.tvTotal)
    TextView tvTotal;
    @BindView(R.id.tvAmount)
    TextView tvAmount;
    @BindView(R.id.ll2)
    LinearLayout ll2;
    @BindView(R.id.tvType)
    TextView tvType;
    @BindView(R.id.tvConfirm)
    TextView tvConfirm;
    @BindView(R.id.tvCancle)
    TextView tvCancle;
    @BindView(R.id.rlContent)
    RelativeLayout rlContent;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    Unbinder unbinder;

    private String price;
    private String amount;
    private String total;
    private String type;

    public static BBConfirmDialogFragment getInstance(String price, String amount, String total, String type) {
        BBConfirmDialogFragment fragment = new BBConfirmDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("price", price);
        bundle.putString("amount", amount);
        bundle.putString("total", total);
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_fragment_exchange_bb;
    }

    @Override
    protected void initLayout() {
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.bottomDialog);
        // 一不小心进入深坑  满身荆棘的爬出坑  佛曰： 别学Android了，适配能整死你，看那个学Android的已经上吊了！
        window.setLayout(MyApplication.getApp().getmWidth(), WonderfulDpPxUtils.dip2px(getActivity(), 310));
    }

    private OperateCallback callback;

    public void setCallback(OperateCallback callback) {
        this.callback = callback;
    }

    @Override
    protected void initView() {
        tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ((OperateCallback) getTargetFragment()).confirm();
                } catch (Exception e) {
                    if (callback != null) {
                        callback.confirm();
                    }
                }
                dismiss();
            }
        });
    }

    @Override
    protected void fillWidget() {

        tvPrice.setText(getArguments().getString("price"));
        tvAmount.setText(getArguments().getString("amount"));
        tvTotal.setText(getArguments().getString("total"));
        if (getArguments().getString("type").equals("BUY")) {
            tvType.setText(WonderfulToastUtils.getString(R.string.text_buy));
            tvTitle.setText(WonderfulToastUtils.getString(R.string.dialog_two_title_buy));
        }
        else {
            tvType.setText(WonderfulToastUtils.getString(R.string.text_sale));
            tvTitle.setText(WonderfulToastUtils.getString(R.string.dialog_two_title_sell));
        }
    }

    @Override
    protected void loadData() {

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

    public interface OperateCallback {
        void confirm();
    }

}
