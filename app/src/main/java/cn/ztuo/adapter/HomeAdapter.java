package cn.ztuo.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import cn.ztuo.R;
import cn.ztuo.ui.home.MainActivity;
import cn.ztuo.app.MyApplication;
import cn.ztuo.entity.Currency;
import cn.ztuo.utils.WonderfulLogUtils;
import cn.ztuo.utils.WonderfulMathUtils;
import cn.ztuo.utils.WonderfulToastUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * author: wuzongjie
 * time  : 2018/4/16 0016 15:23
 * desc  : 首页涨幅榜适配器
 */

public class HomeAdapter extends BaseQuickAdapter<Currency, BaseViewHolder> {

    private boolean isLoad;

    public boolean isLoad() {
        return isLoad;
    }

    public void setLoad(boolean load) {
        isLoad = load;
    }

    public HomeAdapter(@Nullable List<Currency> data) {
        super(R.layout.adapter_home_layout, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Currency item) {
        if (isLoad) {
            helper.setText(R.id.item_home_money, "≈" + WonderfulMathUtils.getRundNumber(item.getClose()*item.getBaseUsdRate() * MainActivity.rate ,2,null)+" CNY");
        } else {
            helper.setText(R.id.item_home_money, "$" + WonderfulMathUtils.getRundNumber(item.getUsdRate(),2,null));
        }
        TextView view = helper.getView(R.id.item_home_position);

        if ((helper.getAdapterPosition() + 1)<=5){
            switch (helper.getAdapterPosition() + 1){
                case 1:
                    view.setBackgroundResource(R.color.color_green1);
                    break;
                case 2:
                    view.setBackgroundResource(R.color.color_green2);
                    break;
                case 3:
                    view.setBackgroundResource(R.color.color_green2);
                    break;
                case 4:
                    view.setBackgroundResource(R.color.color_green4);
                    break;
                case 5:
                    view.setBackgroundResource(R.color.color_green5);
                    break;
            }
        }else {
            view.setBackgroundResource(R.color.color_green3);
        }
        helper.setText(R.id.item_home_position, "" + (helper.getAdapterPosition() + 1));
        helper.setText(R.id.item_home_chg, (item.getChg() >= 0 ? "+" : "") + WonderfulMathUtils.getRundNumber(item.getChg() * 100, 2, "########0.") + "%");
        helper.setTextColor(R.id.item_home_close, item.getChg() >= 0 ? ContextCompat.getColor(MyApplication.getApp(),
                R.color.typeGreen) : ContextCompat.getColor(MyApplication.getApp(), R.color.typeRed));
        helper.getView(R.id.item_home_chg).setEnabled(item.getChg() >= 0);
        helper.setText(R.id.item_home_symbol, item.getSymbol());
        helper.setText(R.id.item_home_change, WonderfulToastUtils.getString(R.string.text_24_change) + WonderfulMathUtils.getRundNumber(Double.valueOf(item.getVolume().toString()),2,null));

//        helper.setText(R.id.item_home_close, WonderfulMathUtils.getRundNumber(item.getClose(),2,null));
        String format = new DecimalFormat("#0.00000000").format(item.getClose());
        BigDecimal bg = new BigDecimal(format);
        String v =  bg.setScale(8,BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
        helper.setText(R.id.item_home_close,v);

    }


}
