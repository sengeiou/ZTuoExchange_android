package cn.ztuo.ui.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import cn.ztuo.R;
import cn.ztuo.ui.message_detail.MessageHelpActivity;
import cn.ztuo.adapter.GongGaoAdapter;
import cn.ztuo.base.BaseActivity;
import cn.ztuo.base.MyListView;
import cn.ztuo.entity.Message;
import cn.ztuo.app.UrlFactory;
import cn.ztuo.utils.WonderfulLogUtils;
import cn.ztuo.utils.okhttp.StringCallback;
import cn.ztuo.utils.okhttp.WonderfulOkhttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Request;

/**
 * Created by Administrator on 2018/8/9.
 */
public class HelpActivity extends BaseActivity {
    @BindView(R.id.llTitle)
    LinearLayout llTitle;
    private GongGaoAdapter adapter;
    private GongGaoAdapter adapter2;
    @BindView(R.id.listview_xinshou)
    MyListView listview_xinshou;
    @BindView(R.id.ibBack)
    ImageButton ibBack;
    @BindView(R.id.listview_changjian)
    MyListView listview_changjian;
    @BindView(R.id.text_gengduo1)
    TextView text_gengduo1;
    @BindView(R.id.text_gengduo2)
    TextView text_gengduo2;
    @BindView(R.id.view_back)
    View view_back;
    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_help;
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, HelpActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void initViews(Bundle savedInstanceState) {
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        view_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listview_xinshou.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MessageHelpActivity.actionStart(HelpActivity.this, messageList.get(position).getId());
            }
        });
        listview_changjian.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WonderfulLogUtils.logi("miao",messageList_chang.get(position).getId());
                MessageHelpActivity.actionStart(HelpActivity.this, messageList_chang.get(position).getId());
            }
        });
        text_gengduo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelpXinShouActivity.actionStart(HelpActivity.this);
            }
        });
        text_gengduo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelpChangjianActivity.actionStart(HelpActivity.this);
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
        getMessage();
    }
    private void getMessage() {
        WonderfulOkhttpUtils.post().url(UrlFactory.getHelp())
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {
                WonderfulLogUtils.logi("miao",response);
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray jsonArray = object.optJSONArray("data");
                    if (jsonArray.length()>0){
                        JSONObject jsonObject = jsonArray.optJSONObject(0);
                        JSONArray jsonArray1 = jsonObject.optJSONArray("content");
                        if (jsonArray1.length()>0){
                            messageList.clear();
                            for (int i=0;i<jsonArray1.length();i++){
                                if (i==3){
                                    break;
                                }
                                JSONObject jsonObject1 = jsonArray1.optJSONObject(i);
                                Message bean=new Message();
                                bean.setContent(jsonObject1.optString("content"));
                                bean.setCreateTime(jsonObject1.optString("createTime"));
                                bean.setIsTop(jsonObject1.optString("isTop"));
                                bean.setId(jsonObject1.optString("id"));
                                bean.setTitle(jsonObject1.optString("title"));
                                messageList.add(bean);
                            }

                            adapter=new GongGaoAdapter(HelpActivity.this,messageList);
                        listview_xinshou.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        }
                        JSONObject jsonObject2 = jsonArray.optJSONObject(1);
                        JSONArray jsonArray2 = jsonObject2.optJSONArray("content");
                        if (jsonArray2.length()>0){
                            messageList_chang.clear();
                            for (int i=0;i<jsonArray2.length();i++){
                                if (i==3){
                                    break;
                                }
                                JSONObject jsonObject1 = jsonArray2.optJSONObject(i);
                                Message bean=new Message();
                                bean.setContent(jsonObject1.optString("content"));
                                bean.setCreateTime(jsonObject1.optString("createTime"));
                                bean.setIsTop(jsonObject1.optString("isTop"));
                                bean.setId(jsonObject1.optString("id"));
                                bean.setTitle(jsonObject1.optString("title"));
                                messageList_chang.add(bean);
                            }
                            adapter2=new GongGaoAdapter(HelpActivity.this,messageList_chang);
                            listview_changjian.setAdapter(adapter2);
                            adapter2.notifyDataSetChanged();

                        }

                    }
//                        adapter=new GongGaoAdapter(HelpActivity.this,messageList);
//
//                        listview_xinshou.setAdapter(adapter);
//                        adapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
//        marqueeView.setOnItemClickListener(new MarqueeView.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position, TextView textView) {
//                MessageDetailActivity.actionStart(getActivity(), messageList.get(infoss.get(position)).getId());
//            }
//        });
    }

    private List<Message> messageList = new ArrayList<>();
    private List<Message> messageList_chang = new ArrayList<>();

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        if (!isSetTitle) {
            ImmersionBar.setTitleBar(HelpActivity.this, llTitle);
            isSetTitle = true;
        }
    }
}
