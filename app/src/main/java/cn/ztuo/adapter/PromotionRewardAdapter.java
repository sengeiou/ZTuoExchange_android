package cn.ztuo.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import cn.ztuo.R;
import cn.ztuo.entity.PromotionReward;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by Administrator on 2018/5/8 0008.
 */

public class PromotionRewardAdapter extends BaseQuickAdapter<PromotionReward, BaseViewHolder> {
    public PromotionRewardAdapter(int layoutResId, @Nullable List<PromotionReward> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PromotionReward item) {
        helper.setText(R.id.tvReleaseTime, item.getCreateTime())
                .setText(R.id.tvCurrency,item.getSymbol())
                .setText(R.id.tvAmount, NumberFormat.getInstance().format(item.getAmount()))
                .setText(R.id.tvRemarks,item.getRemark());
    }

}
