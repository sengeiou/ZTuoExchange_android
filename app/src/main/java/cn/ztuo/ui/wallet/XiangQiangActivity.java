package cn.ztuo.ui.wallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.gyf.barlibrary.ImmersionBar;
import cn.ztuo.R;
import cn.ztuo.adapter.XiangQiangAdapter;
import cn.ztuo.base.BaseActivity;
import cn.ztuo.base.LinListView;
import cn.ztuo.entity.XiangQiangBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/7/4.
 */
public class XiangQiangActivity extends BaseActivity {
    @BindView(R.id.ibBack)
    ImageButton ibBack;
    @BindView(R.id.llTitle)
    LinearLayout llTitle;
    @BindView(R.id.listview_1)
    LinListView listview_1;
    private XiangQiangAdapter adapter;
    private List<XiangQiangBean> lists=new ArrayList<>();

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_chakan;
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, XiangQiangActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        if (!isSetTitle) {
            ImmersionBar.setTitleBar(this, llTitle);
            isSetTitle = true;
        }
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void obtainData() {

    }

    @Override
    protected void fillWidget() {

    }

    @Override
    protected void loadData() {
        lists.clear();
        for (int i=0;i<21;i++){
            XiangQiangBean bean=new XiangQiangBean();
            bean.starTime="2018-06-25 14:00:03";
            bean.jiaoyidui="BTC/USDT";
            bean.fangxiang="限价卖出";
            bean.jiage="100.01USDT";
            bean.weituoliang="10000BHB";
            bean.yichengjiao="10000BHB";
            bean.shouxufei="100.01USDT";
            lists.add(bean);
        }
        adapter=new XiangQiangAdapter(XiangQiangActivity.this,lists);
        listview_1.setEveryPageItemCount(100);
        listview_1.setAdapter(adapter);
        listview_1.setOnRefreshListener(new LinListView.OnRefreshListener() {
            @Override
            public void onLoadMore() {

            }

            @Override
            public void onRefresh() {
                listview_1.stopFreshing();
            }
        });



    }
}
