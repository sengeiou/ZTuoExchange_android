package cn.ztuo.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import cn.ztuo.R;
import cn.ztuo.entity.BankName;

import java.util.List;

/**
 * Created by Administrator on 2018/3/1.
 */

public class BankNameAdapter extends BaseQuickAdapter<BankName, BaseViewHolder> {
    public BankNameAdapter(int layoutResId, @Nullable List<BankName> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BankName item) {
        helper.setText(R.id.tvCoinName, item.getBankName());
        helper.setVisible(R.id.ivSellect, item.isSelected());
    }

}

