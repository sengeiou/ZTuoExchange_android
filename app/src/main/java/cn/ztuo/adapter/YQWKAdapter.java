package cn.ztuo.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.ztuo.R;
import cn.ztuo.base.CustomHorizontalScrollView;
import cn.ztuo.base.LinAdapter;
import cn.ztuo.entity.XiangQiangBean;
import cn.ztuo.entity.YQWKBean;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2018/7/3.
 */
public class YQWKAdapter extends LinAdapter<YQWKBean> {
    private List<YQWKBean> beanss;
    /**
     * LinAdapter通用的构造方法
     *
     * @param context 传入的上下文
     * @param beans   要显示的数据源封装好的列表
     */
    public YQWKAdapter(Activity context, List<YQWKBean> beans) {
        super(context, beans);
        beanss=beans;
    }

    @Override
    protected View LgetView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_yqwk, parent, false);
        }
        YQWKBean bean=beanss.get(position);
        LinearLayout line_bj=ViewHolders.get(convertView,R.id.line_bj);
        if (position%2==0){
            line_bj.setBackgroundColor(Color.parseColor("#f5f5f5"));
        }else {
            line_bj.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        CustomHorizontalScrollView CustomHorizontalScrollView=ViewHolders.get(convertView,R.id.scrollView);
        CustomHorizontalScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        TextView text_star=ViewHolders.get(convertView,R.id.text_star);
        text_star.setText(bean.starTime);
        TextView text_name=ViewHolders.get(convertView,R.id.text_name);
        text_name.setText(bean.nname);
        TextView text_iphone=ViewHolders.get(convertView,R.id.text_iphone);
        text_iphone.setText(bean.iphone);
        TextView text_id=ViewHolders.get(convertView,R.id.text_id);
        text_id.setText(bean.dingdanID);
        TextView text_dui=ViewHolders.get(convertView,R.id.text_dui);
        text_dui.setText(bean.jiaoyiDui);
        TextView text_fangxiang=ViewHolders.get(convertView,R.id.text_fangxiang);
        text_fangxiang.setText(bean.fangxiang);
        TextView text_shouxufei=ViewHolders.get(convertView,R.id.text_shouxufei);
        text_shouxufei.setText(bean.shouxufei);

        TextView text_zong=ViewHolders.get(convertView,R.id.text_wbshuliang);
        text_zong.setText(new BigDecimal(String.valueOf(bean.wkBHB)).toString() + "");

        TextView text_time=ViewHolders.get(convertView,R.id.text_time);
        text_time.setText(bean.endTime);



        return convertView;
    }
}
