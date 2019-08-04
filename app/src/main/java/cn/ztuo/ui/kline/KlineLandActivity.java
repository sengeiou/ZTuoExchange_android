package cn.ztuo.ui.kline;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import cn.ztuo.R;
import cn.ztuo.adapter.PagerAdapter;
import cn.ztuo.base.BaseActivity;
import cn.ztuo.base.BaseFragment;
import cn.ztuo.customview.intercept.WonderfulViewPager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;

public class KlineLandActivity extends BaseActivity {
    private String symbol;
    @BindView(R.id.ibBack)
    ImageButton ibBack;
    @BindView(R.id.vpKline)
    WonderfulViewPager vpKline;
    @BindView(R.id.tab)
    TabLayout tab;
    @BindArray(R.array.k_line_tab)
    String[] titles;
    private List<BaseFragment> fragments = new ArrayList<>();

    public static void actionStart(Context context, String symbol) {
        Intent intent = new Intent(context, KlineLandActivity.class);
        intent.putExtra("symbol", symbol);
        context.startActivity(intent);
    }

    @Override
    protected void setFlag() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
    }

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_kline_land;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        symbol = getIntent().getStringExtra("symbol");
        addFragments();
        vpKline.setOffscreenPageLimit(4);
        List<String> titles = Arrays.asList(this.titles);
        vpKline.setAdapter(new PagerAdapter(getSupportFragmentManager(), fragments, titles));
        tab.setupWithViewPager(vpKline);
        vpKline.setNotIntercept(false);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void addFragments() {
        fragments.add(MinuteLineFragment.getInstance(symbol));
        fragments.add(KDataFragment.getInstance(KDataFragment.Type.MIN1, symbol));
        fragments.add(KDataFragment.getInstance(KDataFragment.Type.MIN5, symbol));
        fragments.add(KDataFragment.getInstance(KDataFragment.Type.MIN30, symbol));
        fragments.add(KDataFragment.getInstance(KDataFragment.Type.HOUR1, symbol));
        fragments.add(KDataFragment.getInstance(KDataFragment.Type.DAY, symbol));
    }

    @Override
    protected void obtainData() {

    }

    @Override
    protected void fillWidget() {

    }

    @Override
    protected void loadData() {

    }

    @Override
    protected boolean isImmersionBarEnabled() {
        return false;
    }
}
