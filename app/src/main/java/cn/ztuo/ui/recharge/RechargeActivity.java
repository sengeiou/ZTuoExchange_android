package cn.ztuo.ui.recharge;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.gyf.barlibrary.ImmersionBar;
import cn.ztuo.R;
import cn.ztuo.base.BaseActivity;
import cn.ztuo.entity.Coin;
import cn.ztuo.app.UrlFactory;
import cn.ztuo.utils.SharedPreferenceInstance;
import cn.ztuo.utils.WonderfulBitmapUtils;
import cn.ztuo.utils.WonderfulCommonUtils;
import cn.ztuo.utils.WonderfulLogUtils;
import cn.ztuo.utils.WonderfulStringUtils;
import cn.ztuo.utils.WonderfulToastUtils;
import cn.ztuo.utils.okhttp.StringCallback;
import cn.ztuo.utils.okhttp.WonderfulOkhttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import okhttp3.Request;

public class RechargeActivity extends BaseActivity {

    @BindView(R.id.ibBack)
    ImageButton ibBack;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvAddressText)
    TextView tvAddressText;
    @BindView(R.id.ivAddress)
    ImageView ivAddress;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.llAlbum)
    LinearLayout llAlbum;
    @BindView(R.id.llCopy)
    LinearLayout llCopy;
    @BindView(R.id.llTitle)
    LinearLayout llTitle;
    private Coin coin;
    private Bitmap saveBitmap;
    @BindView(R.id.view_back)
    View view_back;
    private int biaoshi=0;

    public static void actionStart(Context context, Coin coin) {
        Intent intent = new Intent(context, RechargeActivity.class);
        intent.putExtra("coin", coin);
        context.startActivity(intent);
    }

    @Override
    protected int getActivityLayoutId() {
        return R.layout.activity_recharge;
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
        llCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copy();
            }
        });
        llAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
        this.coin = (Coin) getIntent().getSerializableExtra("coin");

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (biaoshi==0){
            biaoshi=1;
            if (coin.getAddress()==null||"".equals(coin.getAddress())) {
                displayLoadingPopup();
                Timer timer=new Timer();
                TimerTask task=new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                huoqu();
                            }
                        });
                    }
                };
                timer.schedule(task,5000);

            }else {
                erciLoad();
            }
        }

    }


    private void save() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ATC/" + System.currentTimeMillis() + ".jpg");
        if (!file.getParentFile().exists()) {
            file.mkdirs();
        }
        if (!file.exists()) try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (saveBitmap != null) try {
            WonderfulBitmapUtils.saveBitmapToFile(saveBitmap, file, 100);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        WonderfulToastUtils.showToast("保存成功");
    }

    private void huoqu(){
        WonderfulOkhttpUtils.post().url(UrlFactory.getWalletUrl()).addHeader("x-auth-token", SharedPreferenceInstance.getInstance().getTOKEN()).build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                super.onError(request,e);
                WonderfulLogUtils.logi("获取所有钱包出错", "获取所有钱包出错：" + e.getMessage());
                hideLoadingPopup();
            }

            @Override
            public void onResponse(String response) {
                WonderfulLogUtils.logi("获取所有钱包回执：", "获取所有钱包回执：" + response.toString());
                hideLoadingPopup();
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.optInt("code") == 0) {
                        List<Coin> objs = gson.fromJson(object.getJSONArray("data").toString(), new TypeToken<List<Coin>>() {
                        }.getType());
                        for (int i=0;i<objs.size();i++){
                            Coin coin1=objs.get(i);
                            WonderfulLogUtils.logi("miao",coin1.getId()+"-----"+coin.getId());
                            if (coin.getId()==coin1.getId()){
                                WonderfulLogUtils.logi("miao",coin1.getAddress()+"-----");
                                coin.setAddress(coin1.getAddress());
                                erciLoad();
                                break;
                            }
                        }
                        if (coin.getAddress()==null||"".equals(coin.getAddress())){
                            WonderfulToastUtils.showToast("后台正在生成地址，请稍候退出我的资产界面重新进入。");
                        }

                    } else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        });
    }

    private void copy() {
        WonderfulCommonUtils.copyText(this, tvAddress.getText().toString());
        WonderfulToastUtils.showToast(R.string.copy_success);
    }

    @Override
    protected void obtainData() {

    }

    @Override
    protected void fillWidget() {

    }
    private void erciLoad(){
        if(tvTitle==null){
            return;
        }

        tvTitle.setText(coin.getCoin().getUnit() + WonderfulToastUtils.getString(R.string.chargeMoney));
        tvAddressText.setText(coin.getCoin().getUnit() + " 充币地址");
        tvAddress.setText(coin.getAddress());

        if (coin == null || coin.getAddress() == null) {

            tvAddress.setText(WonderfulToastUtils.getString(R.string.unChargeMoneyTip1));
            return;
        }


        ivAddress.post(new Runnable() {
            @Override
            public void run() {
                if (WonderfulStringUtils.isEmpty(coin.getAddress())) return;
                saveBitmap = createQRCode(coin.getAddress(), Math.min(ivAddress.getWidth(), ivAddress.getHeight()));
                ivAddress.setImageBitmap(saveBitmap);
            }
        });
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        if (!isSetTitle) {
            ImmersionBar.setTitleBar(this, llTitle);
            isSetTitle = true;
        }
    }

    public static Bitmap createQRCode(String text, int size) {
        try {
            Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            hints.put(EncodeHintType.MARGIN, 2);   //设置白边大小 取值为 0- 4 越大白边越大
            BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size, hints);
            int[] pixels = new int[size * size];
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * size + x] = 0xff000000;
                    } else {
                        pixels[y * size + x] = 0xffffffff;
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
