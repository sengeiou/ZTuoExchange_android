package cn.ztuo.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import cn.ztuo.R;
import cn.ztuo.app.MyApplication;
import cn.ztuo.entity.EntrustHistory;
import cn.ztuo.utils.WonderfulDateUtils;
import cn.ztuo.utils.WonderfulMathUtils;
import cn.ztuo.utils.WonderfulToastUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/1/30.
 */

public class EntrustAdapter extends BaseQuickAdapter<EntrustHistory, BaseViewHolder> {
    public EntrustAdapter(@LayoutRes int layoutResId, @Nullable List<EntrustHistory> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, EntrustHistory item) {
        if ("BUY".equals(item.getDirection())) {
            helper.setText(R.id.tvType, WonderfulToastUtils.getString(R.string.item_buy)).setTextColor(R.id.tvType, ContextCompat.getColor(MyApplication.getApp(), R.color.typeGreen));
        } else
            helper.setText(R.id.tvType, WonderfulToastUtils.getString(R.string.item_sell)).setTextColor(R.id.tvType, ContextCompat.getColor(MyApplication.getApp(), R.color.typeRed));
        String[] times = WonderfulDateUtils.getFormatTime(null, new Date(item.getTime())).split(" ");
        helper.setText(R.id.tvTime, times[0] + "\n" + times[1]);
        helper.setText(R.id.tvPrice, item.getPrice() + "\n" + item.getBaseSymbol());
        helper.setText(R.id.tvCount, item.getAmount() + "\n" + item.getCoinSymbol());
        helper.setText(R.id.tvTotal, (WonderfulMathUtils.getRundNumber(item.getPrice() * item.getAmount(), 2, null)) + "\n" + item.getBaseSymbol());
        helper.setText(R.id.tvDoneCount, item.getTurnover() + "\n" + item.getCoinSymbol());
        helper.setText(R.id.tvNotCount, item.getTradedAmount() + "\n" + item.getCoinSymbol());
    }
}

