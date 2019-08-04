package cn.ztuo.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import cn.ztuo.R;
import cn.ztuo.entity.Address;
import cn.ztuo.utils.WonderfulStringUtils;

import java.util.List;

/**
 * Created by Administrator on 2018/3/8.
 */

public class AddressAdapter extends BaseQuickAdapter<Address, BaseViewHolder> {
    public AddressAdapter(int layoutResId, @Nullable List<Address> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Address item) {
        helper.setText(R.id.tvRemark, WonderfulStringUtils.isEmpty(item.getRemark()) ? "无备注" : item.getRemark()).setText(R.id.tvAddress, item.getAddress());
    }
}
