package cn.ztuo.adapter;

import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import cn.ztuo.R;
import cn.ztuo.entity.Currency;
import cn.ztuo.utils.WonderfulMathUtils;

import java.util.List;

/**
 * Created by Administrator on 2018/1/29.
 */

public class HomeTwoAdapter extends BaseQuickAdapter<Currency, BaseViewHolder> {
    private int type;//2 收藏  1 其它

    public HomeTwoAdapter(@LayoutRes int layoutResId, @Nullable List<Currency> data, int type) {
        super(layoutResId, data);
        this.type = type;
    }

    @Override
    protected void convert(BaseViewHolder helper, Currency item) {
        helper.setImageResource(R.id.ivCollect, item.isCollect() ? R.mipmap.icon_collect_yes : R.mipmap.icon_collect_no);
        helper.setTextColor(R.id.tvClose, Color.parseColor(item.getChg() >= 0 ? "#4BA64A" : "#EC5252"));
        if (type == 2) helper.setText(R.id.tvOther, item.getSymbol());
        else helper.setText(R.id.tvOther, item.getOtherCoin());
        helper.setText(R.id.tvClose, WonderfulMathUtils.getRundNumber(item.getClose(), 2, null))
                .setText(R.id.tvUpRose, (item.getChg() >= 0 ? "+" : "") + WonderfulMathUtils.getRundNumber(item.getChg() * 100, 2, null) + "%");
        helper.getView(R.id.tvUpRose).setEnabled(item.getChg() >= 0);
        helper.addOnClickListener(R.id.ivCollect);
    }

    public void notifyCollect(int position) {
        Currency currency = getData().get(position);
        currency.setCollect(!currency.isCollect());
        notifyDataSetChanged();
    }
}
