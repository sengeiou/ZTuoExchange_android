package cn.ztuo.ui.kline;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.barlibrary.ImmersionBar;
import cn.ztuo.R;
import cn.ztuo.ui.home.MainActivity;
import cn.ztuo.adapter.PagerAdapter;
import cn.ztuo.app.MyApplication;
import cn.ztuo.base.BaseFragment;
import cn.ztuo.base.BaseTransFragment;
import cn.ztuo.entity.Currency;
import cn.ztuo.entity.Favorite;
import cn.ztuo.app.UrlFactory;
import cn.ztuo.socket.ISocket;
import cn.ztuo.serivce.SocketMessage;
import cn.ztuo.serivce.SocketResponse;
import cn.ztuo.customview.intercept.WonderfulViewPager;
import cn.ztuo.utils.LoadDialog;
import cn.ztuo.utils.WonderfulToastUtils;
import cn.ztuo.utils.okhttp.StringCallback;
import cn.ztuo.utils.okhttp.WonderfulOkhttpUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import okhttp3.Request;

/**
 * Created by Administrator on 2018/2/1.
 */

public class KlineFragment extends BaseTransFragment {
    public static final String TAG = KlineFragment.class.getSimpleName();
    @BindView(R.id.ibBack)
    ImageButton ibBack;
    @BindView(R.id.ibMessage)
    ImageButton ibMessage;
    @BindView(R.id.llTitle)
    LinearLayout llTitle;
    @BindView(R.id.ivFullScreen)
    ImageView ivFullScreen;
    @BindView(R.id.vpKline)
    WonderfulViewPager vpKline;
    @BindView(R.id.tab)
    TabLayout tab;
    @BindArray(R.array.k_line_tab)
    String[] titles;
    @BindView(R.id.tvCurrencyName)
    TextView tvCurrencyName;
    @BindView(R.id.tvBuy)
    TextView tvBuy;
    @BindView(R.id.tvSell)
    TextView tvSell;
    @BindView(R.id.tv_collect)
    TextView mTvCollect; // 收藏的意思
    private String symbol;
    private List<BaseFragment> fragments = new ArrayList<>();

    public static KlineFragment getInstance(String symbol) {
        KlineFragment klineFragment = new KlineFragment();
        Bundle bundle = new Bundle();
        bundle.putString("symbol", symbol);
        klineFragment.setArguments(bundle);
        return klineFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        // 取消订阅
        EventBus.getDefault().post(new SocketMessage(0, ISocket.CMD.UNSUBSCRIBE_SYMBOL_THUMB, null));
        EventBus.getDefault().unregister(this);
    }
    private void startTCP(){
        // 开始订阅
        EventBus.getDefault().post(new SocketMessage(0, ISocket.CMD.SUBSCRIBE_SYMBOL_THUMB, null));
    }
    /**
     * socket 推送过来的信息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSocketMessage(SocketResponse response){
        if (response.getCmd() == ISocket.CMD.PUSH_SYMBOL_THUMB) {
            // 如果是盘口返回的信息
            try {
                Currency temp = new Gson().fromJson(response.getResponse(), Currency.class);
                if (temp == null) return;

                for (Currency currency : currenciesTwo) {
                    if (temp.getSymbol().equals(currency.getSymbol())) {
                        Currency.shallowClone(currency, temp);
                        break;
                    }
                }
                setCurrentcy(currenciesTwo);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private Currency mCurrency ;
    private List<Currency> currenciesTwo = new ArrayList<>();
    private void getCurrent(){
        WonderfulOkhttpUtils.post().url(UrlFactory.getAllCurrency()).build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {

                    }

                    @Override
                    public void onResponse(String response) {
//                        Log.d("jiejie", "222222" + response);
                        List<Currency> obj = new Gson().fromJson(response,new TypeToken<List<Currency>>() {
                        }.getType());
                        currenciesTwo.clear();
                        currenciesTwo.addAll(obj);
                        setCurrentcy(currenciesTwo);
                    }
                });
    }

    private void setCurrentcy(List<Currency> objs) {
        if(objs == null || objs.size() == 0) return;
        for(Currency currency : objs){
            if(symbol.equals(currency.getSymbol())){
                mCurrency = currency;
            }
        }
        if(one !=null) one.tcpNotify(mCurrency);
        if(two != null) two.tcpNotify(mCurrency);
        if(three != null) three.tcpNotify(mCurrency);
        if(four != null) four.tcpNotify(mCurrency);
        if(five != null) five.tcpNotify(mCurrency);
        if(six != null) six.tcpNotify(mCurrency);
        if (!isStart){
            isStart = true;
            startTCP();
        }
    }
    boolean isStart = false;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_kline1;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        symbol = getArguments().getString("symbol");
        tvCurrencyName.setText(symbol);
        getCurrent();
        addFragments();
        vpKline.setOffscreenPageLimit(4);
        List<String> titles = Arrays.asList(this.titles);
        vpKline.setAdapter(new PagerAdapter(getActivity().getSupportFragmentManager(), fragments, titles));
        tab.setupWithViewPager(vpKline);
        vpKline.setNotIntercept(false);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ivFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KlineLandActivity.actionStart(getActivity(), symbol);
            }
        });
        tvBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.actionStart(getActivity(), 1, symbol);
            }
        });
        tvSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.actionStart(getActivity(), 2, symbol);
            }
        });
        mTvCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.isAgain = true;
                if(isFace){ // 已经收藏 则删除
                    delete();
                }else {
                    getCollect();
                }
            }
        });
        isFace= addFace();
        if(isFace){ // 已经收藏
            //tv_collect
            mTvCollect.setText(WonderfulToastUtils.getString(R.string.text_collected));
        }else {
            mTvCollect.setText(WonderfulToastUtils.getString(R.string.text_add_favorite));
        }
    }
    private boolean isFace = false;
    private boolean addFace() {
        for (Favorite favorite : MainActivity.mFavorte) {
            if (symbol.equals(favorite.getSymbol())) return true;
        }
        return false;
    }
    private LoadDialog mDialog;
    private void showDialog(){
        if(mDialog == null) mDialog = new LoadDialog(getActivity());
        mDialog.show();
    }
    private void hideDialog(){
        if(mDialog != null && mDialog.isShowing()) mDialog.dismiss();
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
        WonderfulOkhttpUtils.post().url(UrlFactory.getAddUrl()).addHeader("x-auth-token", getmActivity().getToken())
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
                    } else {
                        WonderfulToastUtils.showToast(WonderfulToastUtils.getString(R.string.text_add_fail));
                    }
                } catch (JSONException e) {
                    WonderfulToastUtils.showToast(WonderfulToastUtils.getString(R.string.text_add_fail));
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideDialog();
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
        WonderfulOkhttpUtils.post().url(UrlFactory.getDeleteUrl()).addHeader("x-auth-token", getmActivity().getToken())
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
                    } else {
                        WonderfulToastUtils.showToast(WonderfulToastUtils.getString(R.string.text_cancel_fail));
                    }
                } catch (JSONException e) {
                    WonderfulToastUtils.showToast(WonderfulToastUtils.getString(R.string.text_cancel_fail));
                }
            }
        });
    }
    private MinuteLineFragment one;
    private KDataFragment two,three,four,five,six;
    private void addFragments() {
        if(one == null) one = MinuteLineFragment.getInstance(symbol);
        if(two == null) two = KDataFragment.getInstance(KDataFragment.Type.MIN1, symbol);
        if(three ==null) three = KDataFragment.getInstance(KDataFragment.Type.MIN5, symbol);
        if(four == null) four = KDataFragment.getInstance(KDataFragment.Type.MIN30, symbol);
        if(five == null) five = KDataFragment.getInstance(KDataFragment.Type.HOUR1, symbol);
        if(six == null) six = KDataFragment.getInstance(KDataFragment.Type.DAY, symbol);
//        fragments.add(KDataFragment.getInstance(KDataFragment.Type.LINE, symbol));
        fragments.add(one);
        fragments.add(two);
        fragments.add(three);
        fragments.add(four);
        fragments.add(five);
        fragments.add(six);
    }

    @Override
    protected void obtainData() {

    }

    @Override
    protected void fillWidget() {
        String[] str = symbol.split("/");
        tvSell.setText(WonderfulToastUtils.getString(R.string.text_sale) + str[0]);
        tvBuy.setText(WonderfulToastUtils.getString(R.string.text_buy) + str[0]);
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        if (!isSetTitle) {
            ImmersionBar.setTitleBar(getActivity(), llTitle);
            isSetTitle = true;
        }
    }

    @Override
    protected String getmTag() {
        return TAG;
    }
}
