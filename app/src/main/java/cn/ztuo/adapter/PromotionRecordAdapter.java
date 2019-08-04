package cn.ztuo.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import cn.ztuo.R;
import cn.ztuo.entity.PromotionRecord;
import cn.ztuo.utils.WonderfulToastUtils;

import java.util.List;

/**
 * Created by Administrator on 2018/5/8 0008.
 */

public class PromotionRecordAdapter extends BaseQuickAdapter<PromotionRecord, BaseViewHolder> {

    public PromotionRecordAdapter(int layoutResId, @Nullable List<PromotionRecord> data) {
        super(layoutResId, data);
    }

//    @Override
//    public int addFooterView(View footer) {
//        return super.addFooterView(footer);
//    }

    @Override
    protected void convert(BaseViewHolder helper, PromotionRecord item) {
        helper.setText(R.id.tvRegistrationTime, item.getCreateTime())
                .setText(R.id.tvUserName,item.getUsername());
        switch (item.getLevel()) {
            case 0:
                helper.setText(R.id.RecommendationLevel, WonderfulToastUtils.getString(R.string.level1));
                break;
            case 1:
                helper.setText(R.id.RecommendationLevel, WonderfulToastUtils.getString(R.string.level2));
                break;
            case 2:
                helper.setText(R.id.RecommendationLevel, WonderfulToastUtils.getString(R.string.level3));
                break;
            case 3:
                helper.setText(R.id.RecommendationLevel, WonderfulToastUtils.getString(R.string.level4));
                break;
        }
    }


}
