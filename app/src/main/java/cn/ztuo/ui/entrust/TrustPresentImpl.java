package cn.ztuo.ui.entrust;

import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import cn.ztuo.entity.EntrustHistory;
import cn.ztuo.app.UrlFactory;
import cn.ztuo.utils.okhttp.StringCallback;
import cn.ztuo.utils.okhttp.WonderfulOkhttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Request;

import static cn.ztuo.app.GlobalConstant.JSON_ERROR;
import static cn.ztuo.app.GlobalConstant.OKHTTP_ERROR;

/**
 * author: wuzongjie
 * time  : 2018/4/18 0018 11:22
 * desc  :
 */

public class TrustPresentImpl implements ITrustContract.Presenter {

    private ITrustContract.View view;

    public TrustPresentImpl(ITrustContract.View mView) {
        this.view = mView;
    }

    /**
     * 获取当前委托
     */
    @Override
    public void getCurrentOrder(String token, int pageNo, int pageSize, String symbol,String type,String direction,String startTime,String endTime) {
        WonderfulOkhttpUtils.post().url(UrlFactory.getEntrustUrl())
                .addHeader("x-auth-token", token)
                .addParams("pageNo", pageNo + "")
                .addParams("pageSize", pageSize + "")
                .addParams("type", type )
                .addParams("direction", direction )
                .addParams("startTime", startTime)
                .addParams("endTime", endTime)
                .addParams("symbol", symbol).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
//                Log.d("jiejie","-----" + e);
                if (view != null) view.errorMes(OKHTTP_ERROR, null);
            }

            @Override
            public void onResponse(String response) {
                Log.d("trust", "当前委托" + response);
                if (view != null) {
                    JSONObject object = null;
                    try {
                        object = new JSONObject(response);
                        view.errorMes(object.getInt("code"), object.optString("message"));
                    } catch (JSONException e) {
                        try {
                            List<EntrustHistory> objs = gson.fromJson(object.getJSONArray("content").toString(), new TypeToken<List<EntrustHistory>>() {
                            }.getType());
                            view.getCurrentSuccess(objs);
                        } catch (JSONException e1) {
                            view.errorMes(JSON_ERROR, null);
                        }
                    }
                }
            }
        });
    }

    /**
     * 取消某个委托
     */
    @Override
    public void getCancelEntrust(String token, String orderId) {
        WonderfulOkhttpUtils.post().url(UrlFactory.getCancleEntrustUrl() + orderId)
                .addHeader("x-auth-token", token).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                if (view != null) view.onDataNotAvailable(OKHTTP_ERROR, null);
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.optInt("code") == 0) {
                        view.getCancelSuccess(object.getString("message"));
                    } else {
                        view.onDataNotAvailable(object.getInt("code"), object.optString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    view.onDataNotAvailable(JSON_ERROR, null);
                }
            }
        });

    }

    /**
     * 获取历史的订单
     */
    @Override
    public void getOrderHistory(String token, final int pageNo, int pageSize, String symbol, String type, String direction, String startTime, String endTime) {
        WonderfulOkhttpUtils.post().url(UrlFactory.getHistoryEntrus())
                .addHeader("x-auth-token", token)
                .addParams("pageNo", pageNo + "")
                .addParams("pageSize", pageSize + "")
                .addParams("type", type )
                .addParams("direction", direction )
                .addParams("startTime", startTime)
                .addParams("endTime", endTime)
                .addParams("symbol", symbol).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                if (view != null) view.errorMes(OKHTTP_ERROR, null);
            }

            @Override
            public void onResponse(String response) {
                if (view == null) return;
                Log.d("trust", "-历史的订单--"+"pageNo--"+pageNo + response);
                JSONObject object = null;
                try {
                    object = new JSONObject(response);
                    view.onDataNotAvailable(object.getInt("code"), object.optString("message"));
                } catch (JSONException e) {
                    try {
                        JsonObject object1 = new JsonParser().parse(response).getAsJsonObject();
                        List<EntrustHistory> orders = gson.fromJson(object1.getAsJsonArray("content")
                                .getAsJsonArray(), new TypeToken<List<EntrustHistory>>() {
                        }.getType());
                        view.getHistorySuccess(orders);
                    } catch (Exception e1) {
                        view.onDataNotAvailable(JSON_ERROR, null);
                    }
                }
            }
        });
    }


    @Override
    public void onDetach() {

    }
}
