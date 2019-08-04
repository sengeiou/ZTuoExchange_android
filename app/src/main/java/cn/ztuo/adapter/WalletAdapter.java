package cn.ztuo.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import cn.ztuo.R;
import cn.ztuo.entity.Coin;
import cn.ztuo.customview.MyHorizontalScrollView;
import cn.ztuo.utils.WonderfulMathUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/2/5.
 */

public class WalletAdapter extends BaseQuickAdapter<Coin, BaseViewHolder> {

    List<MyHorizontalScrollView> scrollViews=new ArrayList<>();


    public WalletAdapter(@LayoutRes int layoutResId, @Nullable List<Coin> data) {
        super(layoutResId, data);

    }


    @Override
    protected void convert(final BaseViewHolder helper, Coin item) {

        //WonderfulMathUtils.getRundNumber(Double.valueOf(new BigDecimal(item.getToReleased()).toString()),8,null)
        helper.setText(R.id.tvCoinUnit, item.getCoin().getUnit()).setText(R.id.tvCanUse, WonderfulMathUtils.getRundNumber(Double.valueOf(new BigDecimal(item.getBalance()).toPlainString()),8,null) + "").setText(R.id.tv_suocang, WonderfulMathUtils.getRundNumber(Double.valueOf(new BigDecimal(item.getToReleased()).toPlainString()),8,null) + "")
                .setText(R.id.tvFrozon, WonderfulMathUtils.getRundNumber(Double.valueOf(new BigDecimal(item.getFrozenBalance()).toPlainString()),8,null) + "").addOnClickListener(R.id.tvRecharge).addOnClickListener(R.id.tvExtract);

    }

}
