package cn.ztuo.ui.message_detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;
import cn.ztuo.R;
import cn.ztuo.base.BaseActivity;
import cn.ztuo.entity.Message;
import cn.ztuo.utils.WonderfulCodeUtils;
import cn.ztuo.utils.WonderfulLogUtils;

import butterknife.BindView;
import cn.ztuo.app.Injection;

public class MessageDetailActivity extends BaseActivity implements MessageDetailContract.View {

    @BindView(R.id.ibBack)
    ImageButton ibBack;
    @BindView(R.id.ibDetail)
    TextView ibDetail;
    @BindView(R.id.llTitle)
    LinearLayout llTitle;
    @BindView(R.id.wb)
    WebView wb;
    @BindView(R.id.text)
    TextView text;
    @BindView(R.id.text_time)
    TextView text_time;
    @BindView(R.id.view_back)
    View view_back;
    private MessageDetailContract.Presenter presenter;
    private String id;
    public static void actionStart(Context context, String id) {
        Intent intent = new Intent(context, MessageDetailActivity.class);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_message_detail;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        new MessageDetailPresenter(Injection.provideTasksRepository(getApplicationContext()), this);
        initWb();
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
    }

    private void initWb() {
        wb.getSettings().setJavaScriptEnabled(true);
        wb.getSettings().setSupportZoom(false);
        wb.getSettings().setBuiltInZoomControls(false);
        wb.getSettings().setUseWideViewPort(true);
        wb.getSettings().setLoadWithOverviewMode(true);
        wb.getSettings().setDefaultFontSize(16);
        wb.setWebViewClient(new WebViewClient());
        wb.setBackgroundColor(getResources().getColor(R.color.primaryBackDark));
    }

    @Override
    protected void obtainData() {
        id = getIntent().getStringExtra("id");
        WonderfulLogUtils.logi("miao", id);
    }

    @Override
    protected void fillWidget() {

    }

    @Override
    protected void loadData() {
        presenter.messageDetail(id);
    }

    @Override
    public void setPresenter(MessageDetailContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        if (!isSetTitle) {
            isSetTitle = true;
            ImmersionBar.setTitleBar(this, llTitle);
        }
    }

    @Override
    public void messageDetailSuccess(Message obj) {
        if (obj == null) return;
        if (text == null) {
            return;
        }
        text.setText(obj.getTitle());
        text_time.setText(obj.getCreateTime() );
        wb.getSettings().setSupportZoom(true);
        wb.getSettings().setTextSize(WebSettings.TextSize.LARGEST);
        wb.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.loadUrl("javascript:function modifyTextColor(){" +
                        "document.getElementsByTagName('body')[0].style.webkitTextFillColor='#E6E6E6'" +
                        "};modifyTextColor();");
            }
        });
        wb.loadDataWithBaseURL(null, obj.getContent(), "text/html", "utf-8", null);

    }

    @Override
    public void messageDetailFail(Integer code, String toastMessage) {
        WonderfulCodeUtils.checkedErrorCode(this, code, toastMessage);
    }






}
