package cn.ztuo.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import cn.ztuo.R;
import cn.ztuo.entity.Order;
import cn.ztuo.utils.WonderfulLogUtils;
import cn.ztuo.utils.WonderfulToastUtils;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

/**
 * Created by Administrator on 2018/2/5.
 */

public class OrderAdapter extends BaseQuickAdapter<Order, BaseViewHolder> {

    private Context context;

    public OrderAdapter(Context context, @LayoutRes int layoutResId, @Nullable List<Order> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, Order item) {
        helper.setText(R.id.tvName, item.getName()).setText(R.id.tvType, (item.getType() == 0 ? WonderfulToastUtils.getString(R.string.text_buy) : WonderfulToastUtils.getString(R.string.text_sell_two)) + item.getUnit())
                .setBackgroundColor(R.id.tvType, item.getType() == 0 ? context.getResources().getColor(R.color.typeGreen) : context.getResources().getColor(R.color.typeRed)).setText(R.id.tvCount, item.getAmount() + item.getUnit()).setText(R.id.tvTotal, item.getMoney() + "CNY");
        if (item.getAvatar() == null || "".equals(item.getAvatar())) {
            Glide.with(context).load(R.mipmap.icon_default_header)
                    .diskCacheStrategy(DiskCacheStrategy.NONE).into((ImageView) helper.getView(R.id.ivHeader));
        } else {
//            ImageOptions options = new ImageOptions.Builder().setUseMemCache(false).build();
//            x.image().bind((ImageView) helper.getView(R.id.ivHeader), item.getAvatar(),options);
            WonderfulLogUtils.logi("miao1",item.getAvatar()+"----"+helper.getPosition());
            Glide.with(context).load(item.getAvatar())
                    .diskCacheStrategy(DiskCacheStrategy.NONE).into((ImageView) helper.getView(R.id.ivHeader));
        }
    }
}
