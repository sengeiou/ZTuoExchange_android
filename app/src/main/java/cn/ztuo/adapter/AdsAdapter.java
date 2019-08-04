package cn.ztuo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import cn.ztuo.R;
import cn.ztuo.app.MyApplication;
import cn.ztuo.entity.Ads;
import cn.ztuo.utils.WonderfulLogUtils;
import cn.ztuo.utils.WonderfulMathUtils;
import cn.ztuo.utils.WonderfulToastUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2018/2/5.
 */

public class AdsAdapter extends BaseQuickAdapter<Ads, BaseViewHolder> {

    private String username;
    private String avatar;
    private Context context;

    public AdsAdapter(@LayoutRes int layoutResId, @Nullable List<Ads> data, String username, String avatar, Context context) {
        super(layoutResId, data);
        this.username = username;
        this.avatar = avatar;
        this.context = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, Ads item) {
        helper.setText(R.id.tvName, username)
                .setText(R.id.tvStatus, item.getStatus() == 0 ? WonderfulToastUtils.getString(R.string.grounding) : WonderfulToastUtils.getString(R.string.shelved))
                .setText(R.id.tvType, item.getAdvertiseType() == 0 ? WonderfulToastUtils.getString(R.string.text_buy_one) : WonderfulToastUtils.getString(R.string.text_sell_one))
                .setText(R.id.tvPrice,new BigDecimal(item.getNumber()-item.getRemainAmount()).setScale(8,BigDecimal.ROUND_DOWN).toPlainString() + item.getCoin().getUnit())
                .setText(R.id.tvNumber,WonderfulToastUtils.getString(R.string.number)+" "+new BigDecimal(item.getRemainAmount()).setScale(8,BigDecimal.ROUND_DOWN).toPlainString()+item.getCoin().getUnit())
                .setText(R.id.tvLimit, WonderfulToastUtils.getString(R.string.limit) + item.getMinLimit() + "~" + item.getMaxLimit() + "CNY");
//        helper.setBackgroundColor(R.id.tvType, item.getAdvertiseType() ==  0 ?
//                ContextCompat.getColor(context,
//                R.color.typeGreen) : ContextCompat.getColor(context, R.color.typeRed));
//        WonderfulLogUtils.logi("miao",new BigDecimal(item.getNumber()).setScale(8,BigDecimal.ROUND_DOWN).toPlainString()+"科学");
        Glide.with(context).load(avatar).asBitmap().placeholder(R.mipmap.icon_default_header).centerCrop().into
                (new BitmapImageViewTarget((ImageView) helper.getView(R.id.ivHeader)) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                ((ImageView)helper.getView(R.id.ivHeader)).setImageDrawable(circularBitmapDrawable);
            }
        });
    }
}
