package cn.ztuo.ui.kline;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.tifezh.kchartlib.chart.BaseKChartView;
import com.github.tifezh.kchartlib.chart.KChartView;
import com.github.tifezh.kchartlib.chart.MinuteChartView;
import com.github.tifezh.kchartlib.utils.ViewUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.barlibrary.ImmersionBar;
import cn.ztuo.R;
import cn.ztuo.ui.home.MainActivity;
import cn.ztuo.ui.mychart.DataParse;
import cn.ztuo.ui.mychart.KLineBean;
import cn.ztuo.ui.mychart.MinutesBean;
import cn.ztuo.adapter.MyPagerAdapter;
import cn.ztuo.adapter.PagerAdapter;
import cn.ztuo.app.GlobalConstant;
import cn.ztuo.app.MyApplication;
import cn.ztuo.base.BaseActivity;
import cn.ztuo.base.BaseFragment;
import cn.ztuo.entity.Currency;
import cn.ztuo.entity.Favorite;
import cn.ztuo.app.UrlFactory;
import cn.ztuo.socket.ISocket;
import cn.ztuo.serivce.SocketMessage;
import cn.ztuo.serivce.SocketResponse;

import cn.ztuo.customview.CustomViewPager;
import cn.ztuo.customview.MyViewPager;
import cn.ztuo.customview.intercept.WonderfulScrollView;
import cn.ztuo.data.DataHelper;
import cn.ztuo.adapter.KChartAdapter;
import cn.ztuo.entity.KLineEntity;
import cn.ztuo.entity.MinuteLineEntity;
import cn.ztuo.utils.LoadDialog;
import cn.ztuo.utils.WonderfulDateUtils;
import cn.ztuo.utils.WonderfulLogUtils;
import cn.ztuo.utils.WonderfulMathUtils;
import cn.ztuo.utils.WonderfulToastUtils;
import cn.ztuo.utils.okhttp.StringCallback;
import cn.ztuo.utils.okhttp.WonderfulOkhttpUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.OnClick;
import cn.ztuo.app.Injection;
import okhttp3.Request;

import static android.widget.RelativeLayout.CENTER_IN_PARENT;

public class KlineActivity extends BaseActivity implements KlineContract.View, View.OnClickListener {
    @BindView(R.id.tvCurrencyName)
    TextView tvCurrencyName;
    @BindView(R.id.llLandText)
    LinearLayout llLandText;
    @BindView(R.id.kDataText)
    TextView mDataText;
    @BindView(R.id.kDataOne)
    TextView mDataOne;
    @BindView(R.id.kCount)
    TextView kCount;
    @BindView(R.id.kUp)
    TextView kUp;
    @BindView(R.id.kLow)
    TextView kLow;
    @BindView(R.id.kLandDataText)
    TextView kLandDataText;
    @BindView(R.id.kLandDataOne)
    TextView kLandDataOne;
    @BindView(R.id.kLandCount)
    TextView kLandCount;
    @BindView(R.id.kLandUp)
    TextView kLandUp;
    @BindView(R.id.kLandLow)
    TextView kLandLow;
    @BindView(R.id.tab)
    LinearLayout tab;
    @BindView(R.id.llTitle)
    LinearLayout llTitle;
    @BindView(R.id.kLandRange)
    TextView kLandRange;
    @BindView(R.id.kRange)
    TextView kRange;
    @BindArray(R.array.k_line_tab)
    String[] titles;
    @BindView(R.id.viewPager)
    MyViewPager viewPager;
    @BindView(R.id.tv_collect)
    TextView mTvCollect; // 收藏的意思
    @BindView(R.id.ibBack)
    ImageButton ibBack;
    @BindView(R.id.tvMore)
    TextView tvMore;
    @BindView(R.id.llAllTab)
    LinearLayout llAllTab;
    @BindView(R.id.llVertical)
    LinearLayout llVertical;
    @BindView(R.id.llState)
    LinearLayout llState;
    @BindView(R.id.tvSell)
    TextView tvSell;
    @BindView(R.id.tvBuy)
    TextView tvBuy;
    @BindView(R.id.vpDepth)
    CustomViewPager depthPager;
    @BindView(R.id.llDepthTab)
    TabLayout depthTab;
    @BindView(R.id.scrollView)
    WonderfulScrollView scrollView;
    private KChartView kChartView;
    private MinuteChartView minuteChartView;
    private ArrayList<TextView> textViews;

    private ArrayList<View> views;
    private TextView selectedTextView;
    private KChartAdapter kChartAdapter;
    private int type;
    private String symbol = "BTC/USDT";
    private String resolution;
    private KlineContract.Presenter presenter;
    private Activity activity;
    private ArrayList<KLineBean> kLineDatas;     // K线图数据
    private Currency mCurrency;
    private List<Currency> currencies = new ArrayList<>();
    private boolean isStart = false;
    private Date startDate;
    private Date endDate;
    private ProgressBar mProgressBar;
    private boolean isFace = false;
    private LoadDialog mDialog;
    private boolean isPopClick;
    private TextView maView;
    private TextView bollView;
    private TextView macdView;
    private TextView kdjView;
    private TextView rsiView;
    private TextView hideChildView;
    private TextView hideMainView;
    private int childType = 0;
    private boolean isVertical;
    private boolean isFirstLoad = true;
    private List<BaseFragment> fragments = new ArrayList<>();
    private PagerAdapter adapter;
    private List<String> tabs;

    public static void actionStart(Context context, String symbol) {
        Intent intent = new Intent(context, KlineActivity.class);
        intent.putExtra("symbol", symbol);
        context.startActivity(intent);
    }

    @Override
    protected int getActivityLayoutId() {
        return R.layout.fragment_kline;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isVertical) {
                finish();
            } else {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 切换横竖屏
     *
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewPager.getLayoutParams();
        tab.removeAllViews();
        moreTabLayout.removeAllViews();
        textViews = new ArrayList<>();
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) { // 横屏
            isVertical = false;
            llState.setVisibility(View.GONE);
            llLandText.setVisibility(View.VISIBLE);
            llVertical.setVisibility(View.GONE);
            ibBack.setVisibility(View.GONE);
            depthTab.setVisibility(View.GONE);
            depthPager.setVisibility(View.GONE);
            params.height = LinearLayout.LayoutParams.MATCH_PARENT;
            viewPager.setLayoutParams(params);
            initTextView(6);
            intMoreTab(6);
            if (type == GlobalConstant.TAG_THIRTY_MINUTE) {
                isPopClick = false;
            }
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            isVertical = true;
            llState.setVisibility(View.VISIBLE);
            llLandText.setVisibility(View.INVISIBLE);
            llVertical.setVisibility(View.VISIBLE);
            ibBack.setVisibility(View.VISIBLE);
            depthTab.setVisibility(View.VISIBLE);
            depthPager.setVisibility(View.VISIBLE);
            params.height = ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 340, getResources().getDisplayMetrics()));
            viewPager.setLayoutParams(params);
            initTextView(5);
            intMoreTab(5);
            if (type == GlobalConstant.TAG_THIRTY_MINUTE) {
                isPopClick = true;
            }
        }
        for (int i = 0; i < views.size(); i++) {
            View view = views.get(i);
            KChartView kChartView = view.findViewById(R.id.kchart_view);
            MinuteChartView minuteChartView = view.findViewById(R.id.minuteChartView);
            if (i != 0) {
                if (isVertical) {
                    kChartView.setGridRows(4);
                    kChartView.setGridColumns(4);
                } else {
                    kChartView.setGridRows(3);
                    kChartView.setGridColumns(8);
                }
            }
        }
        setPagerView();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().post(new SocketMessage(0, ISocket.CMD.UNSUBSCRIBE_SYMBOL_THUMB, null)); //  取消订阅
        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void initViews(Bundle savedInstanceState) {
        tvCurrencyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
    protected void fillWidget() {

    }


    @Override
    protected void obtainData() {
        isVertical = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
        activity = this;
        new KlinePresenter(Injection.provideTasksRepository(activity.getApplicationContext()), this);
        textViews = new ArrayList<>();
        views = new ArrayList<>();

        List<String> titles = Arrays.asList(this.titles);
        if (titles != null) {
            initViewpager(titles);
            initTextView(5);
            initPopWindow(5);
        }
        isFace = addFace();
        if (isFace) { // 已经收藏
            mTvCollect.setText(WonderfulToastUtils.getString(R.string.text_collected));
        } else {
            mTvCollect.setText(WonderfulToastUtils.getString(R.string.text_add_favorite));
        }
        symbol = getIntent().getStringExtra("symbol");
        tvCurrencyName.setText(symbol);
        if (symbol != null) {
            String[] s = symbol.split("/");
            tvBuy.setText(String.valueOf(WonderfulToastUtils.getString(R.string.text_buy) + s[0]));
            tvSell.setText(String.valueOf(WonderfulToastUtils.getString(R.string.text_sale) + s[0]));
        }
        selectedTextView = textViews.get(2);
        Drawable home_zhang_no = getResources().getDrawable(
                R.drawable.tab);
        selectedTextView.setCompoundDrawablesWithIntrinsicBounds(null,
                null, null, home_zhang_no);
        type = (int) selectedTextView.getTag();
        viewPager.setCurrentItem(2);
        initDepthData();
        getCurrent();
    }

    /**
     * 初始化深度图数据
     */
    private void initDepthData() {
        fragments.add(DepthFragment.getInstance(symbol));
        fragments.add(VolumeFragment.getInstance(symbol));
        String[] tabArray = getResources().getStringArray(R.array.k_line_depth);
        tabs = new ArrayList<>();
        for (int i = 0; i < tabArray.length; i++) {
            tabs.add(tabArray[i]);
        }
        depthPager.setAdapter(adapter = new PagerAdapter(getSupportFragmentManager(), fragments, tabs));
        depthTab.setTabMode(TabLayout.MODE_FIXED);
        depthTab.setupWithViewPager(depthPager);
//        depthPager.setOffscreenPageLimit(fragments.size() - 1);
        depthPager.setCurrentItem(0);
    }


    @OnClick({R.id.ibBack, R.id.ivFullScreen, R.id.tvSell, R.id.tvBuy, R.id.tv_collect, R.id.tvMore, R.id.tvIndex})
    void setListener(View view) {
        switch (view.getId()) {
            case R.id.ibBack:
                finish();
                return;
            case R.id.ivFullScreen:
                if (isVertical) {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                }
                return;
            case R.id.tvSell:
                MainActivity.actionStart(activity, 2, symbol);
                return;
            case R.id.tvBuy:
                MainActivity.actionStart(activity, 1, symbol);
                return;
            case R.id.tv_collect:
                MainActivity.isAgain = true;
                if (isFace) { // 已经收藏 则删除
                    delete();
                } else {
                    getCollect();
                }
                return;
            case R.id.tvMore:
                moreTabLayout.setVisibility(View.VISIBLE);
                indexLayout.setVisibility(View.GONE);
                break;
            case R.id.tvIndex:
                moreTabLayout.setVisibility(View.GONE);
                indexLayout.setVisibility(View.VISIBLE);
                break;
                default:
        }
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            popupWindow.showAsDropDown(llAllTab);
        }
    }

    /**
     * 删除收藏
     */
    private void delete() {
        if (!MyApplication.getApp().isLogin()) {
            WonderfulToastUtils.showToast(WonderfulToastUtils.getString(R.string.text_xian_login));
            return;
        }
        showDialog();
        WonderfulOkhttpUtils.post().url(UrlFactory.getDeleteUrl()).addHeader("x-auth-token", getToken())
                .addParams("symbol", symbol).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                hideDialog();
                WonderfulToastUtils.showToast(WonderfulToastUtils.getString(R.string.text_cancel_fail));
            }

            @Override
            public void onResponse(String response) {
                hideDialog();
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.optInt("code") == 0) {
                        WonderfulToastUtils.showToast(WonderfulToastUtils.getString(R.string.text_cancel_success));
                        mTvCollect.setText(WonderfulToastUtils.getString(R.string.text_add_favorite));
                        isFace = false;
                        Drawable yisoucang = getResources().getDrawable(
                                R.drawable.icon_collect_no);
                        mTvCollect.setCompoundDrawablesWithIntrinsicBounds(null,
                                yisoucang, null, null);
                    } else {
                        WonderfulToastUtils.showToast(WonderfulToastUtils.getString(R.string.text_cancel_fail));
                    }
                } catch (JSONException e) {
                    WonderfulToastUtils.showToast(WonderfulToastUtils.getString(R.string.text_cancel_fail));
                }
            }
        });
    }

    /**
     * 添加收藏
     */
    private void getCollect() {
        if (!MyApplication.getApp().isLogin()) {
            WonderfulToastUtils.showToast(WonderfulToastUtils.getString(R.string.text_xian_login));
            return;
        }
        showDialog();
        WonderfulOkhttpUtils.post().url(UrlFactory.getAddUrl()).addHeader("x-auth-token", getToken())
                .addParams("symbol", symbol).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                hideDialog();
                WonderfulToastUtils.showToast(WonderfulToastUtils.getString(R.string.text_add_fail));
            }

            @Override
            public void onResponse(String response) {
                hideDialog();
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.optInt("code") == 0) {
                        WonderfulToastUtils.showToast(WonderfulToastUtils.getString(R.string.text_add_success));
                        isFace = true;
                        mTvCollect.setText(WonderfulToastUtils.getString(R.string.text_collected));
                        Drawable yisoucang = getResources().getDrawable(
                                R.drawable.icon_collect_yes);
                        mTvCollect.setCompoundDrawablesWithIntrinsicBounds(null,
                                yisoucang, null, null);
                    } else {
                        WonderfulToastUtils.showToast(WonderfulToastUtils.getString(R.string.text_add_fail));
                    }
                } catch (JSONException e) {
                    WonderfulToastUtils.showToast(WonderfulToastUtils.getString(R.string.text_add_fail));
                }
            }
        });
    }


    private PopupWindow popupWindow;
    private LinearLayout moreTabLayout;
    private LinearLayout indexLayout;

    /**
     * 初始化popwindow
     *
     * @param count
     */
    private void initPopWindow(int count) {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.layout_kline_popwindow, null);
        initPopChidView(contentView);
        intMoreTab(count);
        popupWindow = new PopupWindow(activity);
        popupWindow.setContentView(contentView);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
    }

    /**
     * 设置more显示内容
     *
     * @param count
     */
    private void intMoreTab(int count) {
        List<String> titles = Arrays.asList(this.titles);
        for (int i = count; i < titles.size(); i++) {
            TextView textView = (TextView) LayoutInflater.from(activity).inflate(R.layout.textview_pop, null);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            textView.setLayoutParams(layoutParams);
            textView.setPadding(ViewUtil.Dp2Px(activity, 20), 0, 0, 0);
            textView.setText(titles.get(i));
            textView.setTag(i);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isPopClick = true;
                    selectedTextView = (TextView) view;
                    int selectedTag = (int) selectedTextView.getTag();
                    type = selectedTag;
                    viewPager.setCurrentItem(selectedTag);
                    popupWindow.dismiss();
                }
            });
            moreTabLayout.addView(textView);
            textViews.add(textView);

        }
    }

    /**
     * 设置tab栏显示内容
     *
     * @param count
     */
    private void initTextView(int count) {
        List<String> titles = Arrays.asList(this.titles);
        for (int i = 0; i < titles.size(); i++) {
            if (i < count) {
                TextView textView = (TextView) LayoutInflater.from(activity).inflate(R.layout.textview_pop, null);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams.weight = 1;
                textView.setLayoutParams(layoutParams);
                textView.setText(titles.get(i));
                textView.setTag(i);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isPopClick = false;
                        selectedTextView = (TextView) view;
                        int selectedTag = (int) selectedTextView.getTag();
                        type = selectedTag;
                        viewPager.setCurrentItem(selectedTag);
                    }
                });
                textViews.add(textView);
                tab.addView(textView);
            }
        }
    }

    /**
     * 初始化popwindow里的控件
     *
     * @param contentView
     */
    private void initPopChidView(View contentView) {
        moreTabLayout = contentView.findViewById(R.id.tabPop);
        indexLayout = contentView.findViewById(R.id.llIndex);
        maView = contentView.findViewById(R.id.tvMA);
        maView.setSelected(true);
        maView.setOnClickListener(this);
        bollView = contentView.findViewById(R.id.tvBOLL);
        bollView.setOnClickListener(this);
        macdView = contentView.findViewById(R.id.tvMACD);
        kdjView = contentView.findViewById(R.id.tvKDJ);
        rsiView = contentView.findViewById(R.id.tvRSI);
        hideMainView = contentView.findViewById(R.id.tvMainHide);
        hideMainView.setOnClickListener(this);
        macdView = contentView.findViewById(R.id.tvMACD);
        macdView.setSelected(true);
        macdView.setOnClickListener(this);
        kdjView = contentView.findViewById(R.id.tvKDJ);
        kdjView.setOnClickListener(this);
        rsiView = contentView.findViewById(R.id.tvRSI);
        rsiView.setOnClickListener(this);
        hideChildView = contentView.findViewById(R.id.tvChildHide);
        hideChildView.setSelected(false);
        hideChildView.setOnClickListener(this);
    }

    /**
     * socket 推送过来的信息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSocketMessage(SocketResponse response) {
        if (response.getCmd() == ISocket.CMD.PUSH_SYMBOL_THUMB) {    // 如果是盘口返回的信息
            try {
                Currency temp = new Gson().fromJson(response.getResponse(), Currency.class);
                for (Currency currency : currencies) {
                    if (temp.getSymbol().equals(currency.getSymbol())) {
                        Currency.shallowClone(currency, temp);
                        break;
                    }
                }
                setCurrentcy(currencies);
            } catch (Exception e) {
                e.printStackTrace();
                WonderfulToastUtils.showToast("解析出错");
            }
        }
    }

    /**
     * 加载数据
     */
    @Override
    protected void loadData() {

    }

    private void loadData2() {
        if (type != GlobalConstant.TAG_DIVIDE_TIME)
            kChartView.showLoading();
        else
            mProgressBar.setVisibility(View.VISIBLE);
        Long to = System.currentTimeMillis();
        endDate = WonderfulDateUtils.getDate("HH:mm", to);
        Long from = to;
        WonderfulLogUtils.logi("miao", "type==" + type);
        switch (type) {
            case GlobalConstant.TAG_DIVIDE_TIME:
                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY) - 1;
                c.set(Calendar.HOUR_OF_DAY, hour);
                String strDate = WonderfulDateUtils.getFormatTime("HH:mm", c.getTime());
                startDate = WonderfulDateUtils.getDateTransformString(strDate, "HH:mm");
                resolution = 1 + "";
                String str = WonderfulDateUtils.getFormatTime(null, c.getTime());
                from = WonderfulDateUtils.getTimeMillis(null, str);
                break;
            case GlobalConstant.TAG_ONE_MINUTE:
                from = to - 24L * 60 * 60 * 1000;//前一天数据
                resolution = 1 + "";
                break;
            case GlobalConstant.TAG_FIVE_MINUTE:
                from = to - 2 * 24L * 60 * 60 * 1000;//前两天数据
                resolution = 5 + "";
                break;
            case GlobalConstant.TAG_THIRTY_MINUTE:
                from = to - 12 * 24L * 60 * 60 * 1000; //前12天数据
                resolution = 30 + "";
                break;
            case GlobalConstant.TAG_AN_HOUR:
                from = to - 24 * 24L * 60 * 60 * 1000;//前 24天数据
                resolution = 1 + "H";
                break;
            case GlobalConstant.TAG_DAY:
                from = to - 60 * 24L * 60 * 60 * 1000; //前60天数据
                resolution = 1 + "D";
                break;
            case GlobalConstant.TAG_WEEK:
                from = to - 730 * 24L * 60 * 60 * 1000; //前两年数据
                resolution = 1 + "W";
                break;
            case GlobalConstant.TAG_MONTH:
                from = to - 1095 * 24L * 60 * 60 * 1000; //前三年数据
                resolution = 1 + "M";
                break;
            default:
        }
        presenter.KData(symbol, from, to, resolution);
    }

    /**
     * 头部显示内容
     *
     * @param objs
     */
    private void setCurrentcy(List<Currency> objs) {
        try {
            for (Currency currency : objs) {
                if (symbol.equals(currency.getSymbol())) {
                    mCurrency = currency;
                    break;
                }
            }
            String strUp = String.valueOf(mCurrency.getHigh());
            String strLow = String.valueOf(mCurrency.getLow());
            String strCount = String.valueOf(mCurrency.getVolume());
            Double douChg = mCurrency.getChg();
            String strRang = WonderfulMathUtils.getRundNumber(mCurrency.getChg() * 100, 2, "########0.") + "%";
            String strDataText = "≈" + WonderfulMathUtils.getRundNumber(mCurrency.getClose() * MainActivity.rate * mCurrency.getBaseUsdRate(),
                    2, null) + "CNY";
            String strDataOne = String.valueOf(mCurrency.getClose());


            BigDecimal bg3 = new BigDecimal(strUp);
            String v3 = bg3.setScale(8, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
            kUp.setText(v3);

            BigDecimal bg2 = new BigDecimal(strLow);
            String v2 = bg2.setScale(8, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
            kLow.setText(v2);
            kCount.setText(strCount);
            kRange.setText(strRang);

            BigDecimal bg1 = new BigDecimal(strDataOne);
            String v1 = bg1.setScale(8, BigDecimal.ROUND_DOWN).stripTrailingZeros().toPlainString();
            mDataOne.setText(v1);
            mDataText.setText(strDataText);
            if (douChg < 0) {
                mDataOne.setTextColor(getResources().getColor(R.color.typeRed));
                kRange.setTextColor(getResources().getColor(R.color.typeRed));
                kLandRange.setTextColor(getResources().getColor(R.color.typeRed));
                kLandDataOne.setTextColor(getResources().getColor(R.color.typeRed));
            } else {
                mDataOne.setTextColor(getResources().getColor(R.color.typeGreen));
                kRange.setTextColor(getResources().getColor(R.color.typeGreen));
                kLandRange.setTextColor(getResources().getColor(R.color.typeGreen));
                kLandDataOne.setTextColor(getResources().getColor(R.color.typeGreen));
            }
            kLandUp.setText(strUp);
            kLandLow.setText(strLow);
            kLandCount.setText(strCount);
            kLandRange.setText(strRang);
            kLandDataOne.setText(strDataOne);
            kLandDataText.setText(strDataText);
            if (!isStart) {
                isStart = true;
                startTCP();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean addFace() {
        for (Favorite favorite : MainActivity.mFavorte) {
            if (symbol.equals(favorite.getSymbol())) return true;
        }
        return false;
    }

    private void startTCP() {
        EventBus.getDefault().post(new SocketMessage(0, ISocket.CMD.SUBSCRIBE_SYMBOL_THUMB, null)); // 开始订阅
    }

    /**
     * 获取头部信息
     */
    private void getCurrent() {
        WonderfulOkhttpUtils.post().url(UrlFactory.getAllCurrency()).build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {
                        List<Currency> obj = new Gson().fromJson(response, new TypeToken<List<Currency>>() {
                        }.getType());
                        currencies.clear();
                        currencies.addAll(obj);
                        setCurrentcy(currencies);
                    }
                });
    }


    /**
     * 初始化viewpager
     *
     * @param titles
     */
    private void initViewpager(List<String> titles) {
        for (int i = 0; i < titles.size(); i++) {
            View view = LayoutInflater.from(activity).inflate(R.layout.layout_kchartview, null);
            if (i == 0) {
                minuteChartView = view.findViewById(R.id.minuteChartView);
                minuteChartView.setVisibility(View.VISIBLE);
                RelativeLayout mLayout = view.findViewById(R.id.mLayout);
                mProgressBar = new ProgressBar(activity);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewUtil.Dp2Px(activity, 50), ViewUtil.Dp2Px(activity, 50));
                lp.addRule(CENTER_IN_PARENT);
                mLayout.addView(mProgressBar, lp);
            } else {
                KChartView kChartView = view.findViewById(R.id.kchart_view);
                initKchartView(kChartView);
                kChartView.setVisibility(View.VISIBLE);
                kChartView.setAdapter(new KChartAdapter());
            }
            views.add(view);
        }
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(views);
        viewPager.setAdapter(myPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setPagerView();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    /**
     * 设置kchartview
     *
     * @param kChartView
     */
    private void initKchartView(KChartView kChartView) {
        kChartView.setCandleSolid(true);
        kChartView.setGridRows(4);
        kChartView.setGridColumns(4);
        kChartView.setOnSelectedChangedListener(new BaseKChartView.OnSelectedChangedListener() {
            @Override
            public void onSelectedChanged(BaseKChartView view, Object point, int index) {
                KLineEntity data = (KLineEntity) point;
                WonderfulLogUtils.logi("onSelectedChanged", "index:" + index + " closePrice:" + data.getClosePrice());
            }
        });
    }

    /**
     * viewpager和textview的点击事件
     */
    private void setPagerView() {
        for (int j = 0; j < textViews.size(); j++) {
            textViews.get(j).setSelected(false);
            textViews.get(j).setCompoundDrawablesWithIntrinsicBounds(null,
                    null, null, null);
            int tag = (int) textViews.get(j).getTag();
            if (tag == type) {
                if (isPopClick) {
                    tvMore.setText(selectedTextView.getText());
                    tvMore.setSelected(true);
                } else {
                    tvMore.setText(getString(R.string.more));
                    tvMore.setSelected(false);
                    textViews.get(j).setSelected(true);
                    Drawable home_zhang_no1 = getResources().getDrawable(
                            R.drawable.tab);
                    textViews.get(j).setCompoundDrawablesWithIntrinsicBounds(null,
                            null, null, home_zhang_no1);
                }
                View view = views.get(j);
                if (type != GlobalConstant.TAG_DIVIDE_TIME) {
                    kChartView = view.findViewById(R.id.kchart_view);
                    kChartView.setMAandBOLL(maView.isSelected(), bollView.isSelected());
                    kChartView.setChidType(childType);
                    kChartAdapter = (KChartAdapter) kChartView.getAdapter();
                    if (kChartAdapter.getDatas() == null || kChartAdapter.getDatas().size() == 0) {
                        loadData2();
                    }
                } else {
                    minuteChartView.setMAandBOLL(maView.isSelected(), bollView.isSelected());
                    if (isFirstLoad)
                        loadData2();
                }
            } else if (!isPopClick) {
                tvMore.setSelected(false);
            }
        }
    }


    @Override
    public void setPresenter(KlineContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void KDataFail(Integer code, String toastMessage) {

    }

    @Override
    public void KDataSuccess(JSONArray obj) {
        DataParse kData = new DataParse();
        switch (type) {
            case GlobalConstant.TAG_DIVIDE_TIME: // 分时图
                mProgressBar.setVisibility(View.GONE);
                try {
                    kData.parseMinutes(obj, (float) mCurrency.getLastDayClose());
                    ArrayList<MinutesBean> objList = kData.getDatas();
                    if (objList != null && objList.size() > 0) {
                        ArrayList<MinuteLineEntity> minuteLineEntities = new ArrayList<>();
                        for (int i = 0; i < objList.size(); i++) {
                            MinuteLineEntity minuteLineEntity = new MinuteLineEntity();
                            MinutesBean minutesBean = objList.get(i);
                            minuteLineEntity.setAvg(minutesBean.getAvprice()); // 成交价
                            minuteLineEntity.setPrice(minutesBean.getCjprice());
                            minuteLineEntity.setTime(WonderfulDateUtils.getDateTransformString(minutesBean.getTime(), "HH:mm"));
                            minuteLineEntity.setVolume(minutesBean.getCjnum());
                            minuteLineEntity.setClose(minutesBean.getClose());
                            minuteLineEntities.add(minuteLineEntity);
                        }
                        if (isFirstLoad) { // 避免界面重绘
                            DataHelper.calculateMA30andBOLL(minuteLineEntities);
                            minuteChartView.initData(minuteLineEntities,
                                    startDate,
                                    endDate,
                                    null,
                                    null,
                                    (float) mCurrency.getLow(), maView.isSelected());
                            isFirstLoad = false;
                        }
                    }
                } catch (Exception e) {
                    WonderfulToastUtils.showToast(getString(R.string.parse_error));
                }
                break;
            default:
                try {
                    kData.parseKLine(obj, type);
                    kLineDatas = kData.getKLineDatas();
                    if (kLineDatas != null && kLineDatas.size() > 0) {
                        ArrayList<KLineEntity> kLineEntities = new ArrayList<>();
                        kLineEntities.clear();
                        for (int i = 0; i < kLineDatas.size(); i++) {
                            KLineEntity lineEntity = new KLineEntity();
                            KLineBean kLineBean = kLineDatas.get(i);
                            lineEntity.setDate(kLineBean.getDate());
                            lineEntity.setOpen(kLineBean.getOpen());
                            lineEntity.setClose(kLineBean.getClose());
                            lineEntity.setHigh(kLineBean.getHigh());
                            lineEntity.setLow(kLineBean.getLow());
                            lineEntity.setVolume(kLineBean.getVol());
                            kLineEntities.add(lineEntity);
                        }
                        WonderfulLogUtils.logi("miao", kLineDatas.get(0).getClose() + "--" + kLineDatas.get(0).getHigh() + "--" + kLineDatas.get(0).getLow() + "--" + kLineDatas.get(0).getOpen() + "--" + kLineDatas.get(0).getVol());
                        WonderfulLogUtils.logi("miao", kLineEntities.size() + "--");

                        kChartAdapter.addFooterData(DataHelper.getALL(activity, kLineEntities));


//                        kChartView.startAnimation();
                        kChartView.refreshEnd();
                    } else {
                        kChartView.refreshEnd();

                    }
                } catch (Exception e) {
                    WonderfulToastUtils.showToast(getString(R.string.parse_error));
                }

                break;
        }
    }


    @Override
    public void allCurrencySuccess(List<Currency> obj) {

    }

    @Override
    public void allCurrencyFail(Integer code, String toastMessage) {

    }

    private void showDialog() {
        if (mDialog == null) mDialog = new LoadDialog(activity);
        mDialog.show();
    }

    private void hideDialog() {
        if (mDialog != null && mDialog.isShowing()) mDialog.dismiss();
    }

    /**
     * 副图的点击事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvMA:
            case R.id.tvBOLL:
            case R.id.tvMainHide:
                if (view.getId() == R.id.tvMA) {
                    maView.setSelected(true);
                    bollView.setSelected(false);
                    hideMainView.setSelected(false);
                } else if (view.getId() == R.id.tvBOLL) {
                    maView.setSelected(false);
                    bollView.setSelected(true);
                    hideMainView.setSelected(false);
                } else {
                    maView.setSelected(false);
                    bollView.setSelected(false);
                    hideMainView.setSelected(true);
                }
                if (type == GlobalConstant.TAG_DIVIDE_TIME) {
                    minuteChartView.setMAandBOLL(maView.isSelected(), bollView.isSelected());
                } else {
                    kChartView.setMAandBOLL(maView.isSelected(), bollView.isSelected());
                }
                popupWindow.dismiss();
                break;
            case R.id.tvMACD:
            case R.id.tvRSI:
            case R.id.tvKDJ:
            case R.id.tvChildHide:
                if (view.getId() == R.id.tvMACD) {
                    childType = 0;
                    macdView.setSelected(true);
                    rsiView.setSelected(false);
                    kdjView.setSelected(false);
                    hideChildView.setSelected(false);
                } else if (view.getId() == R.id.tvKDJ) {
                    childType = 1;
                    macdView.setSelected(false);
                    rsiView.setSelected(false);
                    kdjView.setSelected(true);
                    hideChildView.setSelected(false);
                } else if (view.getId() == R.id.tvRSI) {
                    childType = 2;
                    macdView.setSelected(false);
                    rsiView.setSelected(true);
                    kdjView.setSelected(false);
                    hideChildView.setSelected(false);
                } else {
                    childType = -1;
                    macdView.setSelected(false);
                    rsiView.setSelected(false);
                    kdjView.setSelected(false);
                    hideChildView.setSelected(true);
                }
                if (type == GlobalConstant.TAG_DIVIDE_TIME) {
                } else {
                    kChartView.setChidType(childType);
                }
                popupWindow.dismiss();
                break;
            default:
        }
    }
}
