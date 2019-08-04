package cn.ztuo.ui.kline;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.SparseArray;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import cn.ztuo.R;
import cn.ztuo.ui.home.MainActivity;
import cn.ztuo.ui.mychart.CoupleChartGestureListener;
import cn.ztuo.ui.mychart.DataParse;
import cn.ztuo.ui.mychart.MinutesBean;
import cn.ztuo.ui.mychart.MyBottomMarkerView;
import cn.ztuo.ui.mychart.MyCombinedChartX;
import cn.ztuo.ui.mychart.MyHMarkerView;
import cn.ztuo.ui.mychart.MyLeftMarkerView;
import cn.ztuo.ui.mychart.MyUtils;
import cn.ztuo.ui.mychart.MyXAxis;
import cn.ztuo.ui.mychart.MyYAxis;
import cn.ztuo.base.BaseLazyFragment;
import cn.ztuo.entity.Currency;
import cn.ztuo.utils.WonderfulDateUtils;
import cn.ztuo.utils.WonderfulLogUtils;
import cn.ztuo.utils.WonderfulMathUtils;

import org.json.JSONArray;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import cn.ztuo.app.Injection;

/**
 * Created by Administrator on 2018/3/16.
 */

public class MinuteLineFragment extends BaseLazyFragment implements KlineContract.MinuteView {
    @BindView(R.id.tvKInfo)
    TextView tvKInfo;
    @BindView(R.id.tvMaInfo)
    TextView tvMaInfo;
    @BindView(R.id.minuteLineChart)
    MyCombinedChartX minuteLineChart;
    @BindView(R.id.kDataText)
    TextView mDataText;
    @BindView(R.id.kDataOne)
    TextView mDataOne;
    @BindView(R.id.kDataTwo)
    TextView mDataTwo;
    @BindView(R.id.kDataThree)
    TextView mDataThree;
    @BindView(R.id.kDataFour)
    TextView mDataFour;
    @BindView(R.id.kDataFive)
    TextView mDataFive;
    @BindView(R.id.volumeChart)
    MyCombinedChartX volumeChart;
    String color = "#66A7ADBC";
    String textColor = "#A7ADBC";
    String symbol = "BTC/USDT";
    protected MyXAxis xAxisPrice;
    protected MyYAxis axisRightPrice;
    protected MyYAxis axisLeftPrice;

    protected MyXAxis xAxisVolume;
    protected MyYAxis axisRightVolume;
    protected MyYAxis axisLeftVolume;

    protected SparseArray<String> stringSparseArray;
    protected DataParse mData;
    private Currency currency;
    private KlineContract.MinutePresenter presenter;

    public static MinuteLineFragment getInstance(String symbol) {
        MinuteLineFragment minuteLineFragment = new MinuteLineFragment();
        Bundle bundle = new Bundle();
        bundle.putString("symbol", symbol);
        minuteLineFragment.setArguments(bundle);
        return minuteLineFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_minute_line;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        symbol = getArguments().getString("symbol");
        new MinutePresenter(Injection.provideTasksRepository(getActivity()), this);
        initMinuteLineChart();
        initVolumeChart();
        initChartListener();
        //初始化图表
        initMinuteChart();
        initChartVolume();
        //设置X轴
        stringSparseArray = setXLabels();
        setShowLabels(stringSparseArray);
    }
    public void tcpNotify(Currency mCurrency){
//        Log.d("jiejie","MinMinMin");
        try{
            mDataFour.setText(String.valueOf(mCurrency.getHigh()));
            mDataFive.setText(String.valueOf(mCurrency.getLow()));
            mDataThree.setText(String.valueOf(mCurrency.getVolume()));
            mDataTwo.setText(String.valueOf(mCurrency.getChg()));
            mDataOne.setText(String.valueOf("$" + mCurrency.getClose() + "  ↑"));
            mDataText.setText("≈" + WonderfulMathUtils.getRundNumber(mCurrency.getClose() * MainActivity.rate * mCurrency.getBaseUsdRate(),
                    2, null) + "CNY");
        }catch (Exception e){}

    }
//    private Currency mCurrency ;
//    private void getCurrent(){
//        WonderfulOkhttpUtils.post().url(UrlFactory.getAllCurrencys()).build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Request request, Exception e) {
//
//                    }
//
//                    @Override
//                    public void onResponse(String response) {
//                        JsonObject object = new JsonParser().parse(response).getAsJsonObject();
//                        JsonArray array = object.getAsJsonArray("changeRank").getAsJsonArray();
//                        List<Currency>  objs = gson.fromJson(array, new TypeToken<List<Currency>>() {}.getType());
//                        setCurrentcy(objs);
//                    }
//                });
//    }
//
//    private void setCurrentcy(List<Currency> objs) {
//        if(objs == null || objs.size() == 0) return;
//        for(Currency currency : objs){
//            if(symbol.equals(currency.getSymbol())){
//                mCurrency = currency;
//            }
//        }
//        mDataFour.setText(String.valueOf(mCurrency.getHigh()));
//        mDataFive.setText(String.valueOf(mCurrency.getLow()));
//        mDataThree.setText(String.valueOf(mCurrency.getVolume()));
//        mDataTwo.setText(String.valueOf(mCurrency.getChg()));
//        mDataOne.setText(String.valueOf("$" + mCurrency.getClose() + "  ↑"));
//        Log.d("jiejie","mc" + mCurrency.getSymbol() + " ---" + mCurrency.getClose());
//    }

    private void initChartVolume() {
        try {
            volumeChart.setScaleEnabled(true);//启用图表缩放事件
            volumeChart.setDrawBorders(true);//是否绘制边线
            volumeChart.setBorderWidth(1);//边线宽度，单位dp
            volumeChart.setDragEnabled(true);//启用图表拖拽事件
            volumeChart.setScaleYEnabled(false);//启用Y轴上的缩放
            volumeChart.setBorderColor(Color.parseColor(color));//边线颜色
            volumeChart.setDescription("");//右下角对图表的描述信息
            volumeChart.setHardwareAccelerationEnabled(true);//是否开启硬件加速
            volumeChart.setMinOffset(0f);//设置上下内边距
            volumeChart.setExtraOffsets(0f, 0f, 0f, 5f);//图标周围格额外的偏移量

            Legend lineChartLegend = volumeChart.getLegend();
            lineChartLegend.setEnabled(false);//是否绘制 Legend 图例

            //x轴
            xAxisVolume = volumeChart.getXAxis();
            xAxisVolume.setEnabled(false);//是否绘制X轴的数据
//        xAxisVolume.setDrawLabels(false);
//        xAxisVolume.setDrawAxisLine(false);
//        xAxisVolume.setDrawGridLines(false);
//        xAxisVolume.enableGridDashedLine(10f, 10f, 0f);//绘制成虚线，只有在关闭硬件加速的情况下才能使用

            //左边y
            axisLeftVolume = volumeChart.getAxisLeft();
            axisLeftVolume.setAxisMinValue(0);//设置Y轴坐标最小为多少
            axisLeftVolume.setShowOnlyMinMax(true);//参考上面
            axisLeftVolume.setDrawLabels(false);//参考上面
            axisLeftVolume.setDrawGridLines(false);//参考上面
        /*轴不显示 避免和border冲突*/
            axisLeftVolume.setDrawAxisLine(false);//参考上面

            //右边y
            axisRightVolume = volumeChart.getAxisRight();
            axisRightVolume.setAxisMinValue(0);//参考上面
            axisRightVolume.setShowOnlyMinMax(true);//参考上面
            axisRightVolume.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);//参考上面
            axisRightVolume.setDrawLabels(true);//参考上面
            axisRightVolume.setDrawGridLines(true);//参考上面
            axisRightVolume.enableGridDashedLine(10f, 10f, 0f);//参考上面
            axisRightVolume.setDrawAxisLine(false);//参考上面

            //y轴样式
            this.axisRightVolume.setValueFormatter(new YAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, YAxis yAxis) {
                    DecimalFormat mFormat = new DecimalFormat("#0.00");
                    return mFormat.format(value);
                }
            });
        } catch (Exception ex) {

        }
    }

    private void setMarkerView(DataParse mData, MyCombinedChartX combinedChart) {
        MyLeftMarkerView leftMarkerView = new MyLeftMarkerView(getActivity(), R.layout.mymarkerview);
        MyHMarkerView hMarkerView = new MyHMarkerView(getActivity(), R.layout.mymarkerview_line);
        combinedChart.setMarker(leftMarkerView, hMarkerView, mData);
    }

    private void initChartListener() {
//         将K线控的滑动事件传递给交易量控件
        minuteLineChart.setOnChartGestureListener(new CoupleChartGestureListener(minuteLineChart, new Chart[]{volumeChart}));
        // 将交易量控件的滑动事件传递给K线控件
        volumeChart.setOnChartGestureListener(new CoupleChartGestureListener(volumeChart, new Chart[]{minuteLineChart}));
        minuteLineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                Highlight highlight = new Highlight(h.getXIndex(), h.getValue(), h.getDataIndex(), h.getDataSetIndex());
                float touchY = h.getTouchY() - minuteLineChart.getHeight();
                Highlight h1 = volumeChart.getHighlightByTouchPoint(h.getXIndex(), touchY);
                highlight.setTouchY(touchY);
                if (null == h1) {
                    highlight.setTouchYValue(0);
                } else {
                    highlight.setTouchYValue(h1.getTouchYValue());
                }
                volumeChart.highlightValues(new Highlight[]{highlight});

                updateText(e.getXIndex());
            }

            @Override
            public void onNothingSelected() {
                volumeChart.highlightValue(null);
            }
        });
        volumeChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                Highlight highlight = new Highlight(h.getXIndex(), h.getValue(), h.getDataIndex(), h.getDataSetIndex());
                float touchY = h.getTouchY() + minuteLineChart.getHeight();
                Highlight h1 = minuteLineChart.getHighlightByTouchPoint(h.getXIndex(), touchY);
                highlight.setTouchY(touchY);
                if (null == h1) {
                    highlight.setTouchYValue(0);
                } else {
                    highlight.setTouchYValue(h1.getTouchYValue());
                }
                minuteLineChart.highlightValues(new Highlight[]{highlight});
            }

            @Override
            public void onNothingSelected() {
                minuteLineChart.highlightValue(null);
            }
        });

    }

    /**
     * 设置文字
     */
    private void updateText(int index) {
        MinutesBean minutesBean = mData.getDatas().get(index);
        float change = (minutesBean.close - minutesBean.open) / minutesBean.open;
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMaximumFractionDigits(2);
        String changePercentage = nf.format(Double.valueOf(String.valueOf(change)));
        tvKInfo.setText("开 " + MyUtils.getDecimalFormatVol(minutesBean.open) + " 高  " + MyUtils.getDecimalFormatVol(minutesBean.high) + " 低 " +
                MyUtils.getDecimalFormatVol(minutesBean.low) + " 收 " + MyUtils.getDecimalFormatVol(minutesBean.close) + " 涨幅 " + changePercentage);
//        int newIndex = index;
//        if (newIndex < 4) {
//            tvMaInfo.setText("MA5:--  MA10:" + "--" + "  MA20:" + "--");
//        } else if (newIndex >= 4 && newIndex < 9) {
//            tvMaInfo.setText("MA5:" + kData.getMa5DataL().get(newIndex - 4).getVal() + "  MA10:" + "--" + "  MA20:" + "--");
//        } else if (newIndex >= 9 && newIndex < 19) {
//            tvMaInfo.setText("MA5:" + kData.getMa10DataL().get(newIndex - 4).getVal() + "  MA10:" +
//                    kData.getMa20DataL().get(newIndex - 9).getVal() + "  MA20:" + "--");
//        } else {
//            tvMaInfo.setText("MA5:" + kData.getMa5DataL().get(newIndex - 4).getVal() + "  MA10:" + kData.getMa10DataL().get(newIndex - 9).getVal() +
//                    "  MA20:" + kData.getMa20DataL().get(newIndex - 19).getVal());
//        }
    }

    private void initVolumeChart() {
        volumeChart.setScaleEnabled(false);//启用图表缩放事件
        volumeChart.setDrawBorders(true);//是否绘制边线
        volumeChart.setBorderWidth(1);//边线宽度，单位dp
        volumeChart.setDragEnabled(true);//启用图表拖拽事件
        volumeChart.setScaleYEnabled(false);//启用Y轴上的缩放
        volumeChart.setBorderColor(Color.parseColor(color));//边线颜色
        volumeChart.setDescription("");//右下角对图表的描述信息
        volumeChart.setHardwareAccelerationEnabled(true);//是否开启硬件加速
        volumeChart.setMinOffset(0f);//设置上下内边距
        volumeChart.setExtraOffsets(0f, 0f, 0f, 0f);//图标周围格额外的偏移量

        Legend lineChartLegend = volumeChart.getLegend();
        lineChartLegend.setEnabled(false);//是否绘制 Legend 图例

        //x轴
        xAxisVolume = volumeChart.getXAxis();
        xAxisVolume.setEnabled(false);//是否绘制X轴的数据
//        xAxisVolume.setDrawLabels(false);
//        xAxisVolume.setDrawAxisLine(false);
//        xAxisVolume.setDrawGridLines(false);
//        xAxisVolume.enableGridDashedLine(10f, 10f, 0f);//绘制成虚线，只有在关闭硬件加速的情况下才能使用

        //左边y
        axisLeftVolume = volumeChart.getAxisLeft();
        axisLeftVolume.setAxisMinValue(0);//设置Y轴坐标最小为多少
        axisLeftVolume.setShowOnlyMinMax(true);//参考上面
        axisLeftVolume.setDrawLabels(false);//参考上面
        axisLeftVolume.setDrawGridLines(false);//参考上面
        /*轴不显示 避免和border冲突*/
        axisLeftVolume.setDrawAxisLine(false);//参考上面

        //右边y
        axisRightVolume = volumeChart.getAxisRight();
        axisRightVolume.setAxisMinValue(0);//参考上面
        axisRightVolume.setShowOnlyMinMax(true);//参考上面
        axisRightVolume.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);//参考上面
        axisRightVolume.setDrawLabels(true);//参考上面
        axisRightVolume.setDrawGridLines(true);//参考上面
        axisRightVolume.enableGridDashedLine(10f, 10f, 0f);//参考上面
        axisRightVolume.setDrawAxisLine(false);//参考上面

        //y轴样式
        this.axisRightVolume.setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                DecimalFormat mFormat = new DecimalFormat("#0.00");
                return mFormat.format(value);
            }
        });
    }

    private void initMinuteLineChart() {
        minuteLineChart.setScaleEnabled(true);//启用图表缩放事件
        minuteLineChart.setDrawBorders(true);//是否绘制边线
        minuteLineChart.setBorderWidth(1);//边线宽度，单位dp
        minuteLineChart.setDragEnabled(true);//启用图表拖拽事件
        minuteLineChart.setScaleYEnabled(false);//启用Y轴上的缩放
        minuteLineChart.setBorderColor(Color.parseColor(color));//边线颜色
        minuteLineChart.setDescription("");//右下角对图表的描述信息
        minuteLineChart.setHardwareAccelerationEnabled(true);//是否不开启硬件加速
        minuteLineChart.setMinOffset(0f);//设置上下内边距
//        minuteLineChart.setMinOffsetLR(0f);//设置左右内边距
        minuteLineChart.setExtraOffsets(0f, 0f, 0f, 3f);//图标周围格额外的偏移量

        Legend lineChartLegend = minuteLineChart.getLegend();//主要控制左下方的图例的
        lineChartLegend.setEnabled(false);//是否绘制 Legend 图例

        //x轴
        xAxisPrice = minuteLineChart.getXAxis();//控制X轴的
        xAxisPrice.setDrawLabels(true);//是否显示X坐标轴上的刻度，默认是true
        xAxisPrice.setDrawAxisLine(false);//是否绘制坐标轴的线，即含有坐标的那条线，默认是true
        xAxisPrice.setDrawGridLines(false);//是否显示X坐标轴上的刻度竖线，默认是true
        xAxisPrice.setPosition(XAxis.XAxisPosition.BOTTOM);//把坐标轴放在上下 参数有：TOP, BOTTOM, BOTH_SIDED, TOP_INSIDE or BOTTOM_INSIDE.
        xAxisPrice.enableGridDashedLine(10f, 10f, 0f);//绘制成虚线，只有在关闭硬件加速的情况下才能使用
        xAxisPrice.setYOffset(7f);//设置X轴刻度在Y坐标上的偏移

        //左边y
        axisLeftPrice = minuteLineChart.getAxisLeft();
        axisLeftPrice.setLabelCount(5, false); //第一个参数是Y轴坐标的个数，第二个参数是 是否不均匀分布，true是不均匀分布
        axisLeftPrice.setDrawLabels(true);//是否显示Y坐标轴上的刻度，默认是true
        axisLeftPrice.setDrawGridLines(false);//是否显示Y坐标轴上的刻度竖线，默认是true
        /*轴不显示 避免和border冲突*/
        axisLeftPrice.setDrawAxisLine(true);//是否绘制坐标轴的线，即含有坐标的那条线，默认是true
        axisLeftPrice.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART); //参数是INSIDE_CHART(Y轴坐标在内部) 或 OUTSIDE_CHART(在外部（默认是这个）)
//        axisLeftPrice.setStartAtZero(false); //设置Y轴坐标是否从0开始
        axisLeftPrice.setShowOnlyMinMax(true); //参数如果为true Y轴坐标只显示最大值和最小值
        axisLeftPrice.enableGridDashedLine(10f, 10f, 0f); //虚线表示Y轴上的刻度竖线(float lineLength, float spaceLength, float phase)三个参数，1.线长，2.虚线间距，3.虚线开始坐标  当setDrawGridLines为true才有用

        //右边y
        axisRightPrice = minuteLineChart.getAxisRight();
        axisRightPrice.setLabelCount(5, false);//参考上面
        axisRightPrice.setDrawLabels(false);//参考上面
//        axisRightPrice.setStartAtZero(false);//参考上面
        axisRightPrice.setDrawGridLines(false);//参考上面
        axisRightPrice.setDrawAxisLine(true);//参考上面

        //y轴样式
        this.axisLeftPrice.setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                DecimalFormat mFormat = new DecimalFormat("#0.00");
                return mFormat.format(value);
            }
        });

    }

    private SparseArray<String> setXLabels() {
        SparseArray<String> xLabels = new SparseArray<>();
        xLabels.put(0, "00:00");
        xLabels.put(240, "04:00");
        xLabels.put(480, "08:00");
        xLabels.put(720, "12:00");
        xLabels.put(960, "16:00");
        xLabels.put(1200, "20:00");
        xLabels.put(1440, "24:00");
//        for (int i = 0; i < 1440; i++) {
//            int hh = i / 60;
//            int mm = 0;
//            String hhStr = "";
//            String mmStr = "";
//            if (hh == 0) {
//                mm = i % 60;
//            } else {
//                mm = i - 60 * hh;
//            }
//            if ((hh + "").length() == 1) hhStr = "0" + hh;
//            else hhStr = "" + hh;
//
//            if ((mm + "").length() == 1) mmStr = "0" + mm;
//            else mmStr = mm + "";
//            xLabels.put(i, hhStr + ":" + mmStr);
//        }
//        WonderfulLogUtils.logi("INFO", xLabels.toString());
        return xLabels;
    }

    public void setShowLabels(SparseArray<String> labels) {
        xAxisPrice.setXLabels(labels);
        xAxisVolume.setXLabels(labels);
    }

    private void initMinuteChart() {
        minuteLineChart.setScaleEnabled(true);//启用图表缩放事件
        minuteLineChart.setDrawBorders(true);//是否绘制边线
        minuteLineChart.setBorderWidth(1);//边线宽度，单位dp
        minuteLineChart.setDragEnabled(true);//启用图表拖拽事件
        minuteLineChart.setScaleYEnabled(false);//启用Y轴上的缩放
        minuteLineChart.setBorderColor(Color.parseColor(color));//边线颜色
        minuteLineChart.setDescription("");//右下角对图表的描述信息
        minuteLineChart.setHardwareAccelerationEnabled(true);//是否不开启硬件加速
        minuteLineChart.setMinOffset(0f);//设置上下内边距
//        minuteLineChart.setMinOffsetLR(0f);//设置左右内边距
        minuteLineChart.setExtraOffsets(0f, 0f, 0f, 3f);//图标周围格额外的偏移量

        Legend lineChartLegend = minuteLineChart.getLegend();//主要控制左下方的图例的
        lineChartLegend.setEnabled(false);//是否绘制 Legend 图例

        //x轴
        xAxisPrice = minuteLineChart.getXAxis();//控制X轴的
        xAxisPrice.setDrawLabels(true);//是否显示X坐标轴上的刻度，默认是true
        xAxisPrice.setDrawAxisLine(false);//是否绘制坐标轴的线，即含有坐标的那条线，默认是true
        xAxisPrice.setDrawGridLines(false);//是否显示X坐标轴上的刻度竖线，默认是true
        xAxisPrice.setPosition(XAxis.XAxisPosition.BOTTOM);//把坐标轴放在上下 参数有：TOP, BOTTOM, BOTH_SIDED, TOP_INSIDE or BOTTOM_INSIDE.
        xAxisPrice.enableGridDashedLine(10f, 10f, 0f);//绘制成虚线，只有在关闭硬件加速的情况下才能使用
        xAxisPrice.setYOffset(7f);//设置X轴刻度在Y坐标上的偏移

        //左边y
        axisLeftPrice = minuteLineChart.getAxisLeft();
        axisLeftPrice.setLabelCount(5, false); //第一个参数是Y轴坐标的个数，第二个参数是 是否不均匀分布，true是不均匀分布
        axisLeftPrice.setDrawLabels(true);//是否显示Y坐标轴上的刻度，默认是true
        axisLeftPrice.setDrawGridLines(false);//是否显示Y坐标轴上的刻度竖线，默认是true
        /*轴不显示 避免和border冲突*/
        axisLeftPrice.setDrawAxisLine(true);//是否绘制坐标轴的线，即含有坐标的那条线，默认是true
        axisLeftPrice.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART); //参数是INSIDE_CHART(Y轴坐标在内部) 或 OUTSIDE_CHART(在外部（默认是这个）)
//        axisLeftPrice.setStartAtZero(false); //设置Y轴坐标是否从0开始
        axisLeftPrice.setShowOnlyMinMax(true); //参数如果为true Y轴坐标只显示最大值和最小值
        axisLeftPrice.enableGridDashedLine(10f, 10f, 0f); //虚线表示Y轴上的刻度竖线(float lineLength, float spaceLength, float phase)三个参数，1.线长，2.虚线间距，3.虚线开始坐标  当setDrawGridLines为true才有用

        //右边y
        axisRightPrice = minuteLineChart.getAxisRight();
        axisRightPrice.setLabelCount(5, false);//参考上面
        axisRightPrice.setDrawLabels(false);//参考上面
//        axisRightPrice.setStartAtZero(false);//参考上面
        axisRightPrice.setDrawGridLines(false);//参考上面
        axisRightPrice.setDrawAxisLine(true);//参考上面

        //y轴样式
        this.axisLeftPrice.setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                DecimalFormat mFormat = new DecimalFormat("#0.00");
                return mFormat.format(value);
            }
        });
    }

    public String[] getMinutesCount() {
        return new String[1440];
    }

    private void setMarkerViewButtom(DataParse mData, MyCombinedChartX combinedChart) {
        try {
            MyLeftMarkerView leftMarkerView = new MyLeftMarkerView(getActivity(), R.layout.mymarkerview);
            MyHMarkerView hMarkerView = new MyHMarkerView(getActivity(), R.layout.mymarkerview_line);
            MyBottomMarkerView bottomMarkerView = new MyBottomMarkerView(getActivity(), R.layout.mymarkerview);
            combinedChart.setMarker(leftMarkerView, bottomMarkerView, hMarkerView, mData);
        } catch (Exception ex) {
            //do nothing
        }
    }

    @Override
    protected void obtainData() {

    }

    @Override
    protected void fillWidget() {

    }

    @Override
    protected void loadData() {
        presenter.allCurrency();
    }

    @Override
    protected boolean isImmersionBarEnabled() {
        return false;
    }

    @NonNull
    private LineDataSet setLine(int type, String[] xVals, ArrayList<Entry> lineEntries) {
        LineDataSet lineDataSetMa = new LineDataSet(lineEntries, "ma" + type);
        lineDataSetMa.setHighlightEnabled(false);
        lineDataSetMa.setDrawValues(false);
        if (type == 0) {
//            lineDataSetMa.setDrawFilled(true);
            lineDataSetMa.setAxisDependency(YAxis.AxisDependency.LEFT);
            lineDataSetMa.setColor(Color.parseColor("#6198F7"));
            lineDataSetMa.setDrawFilled(true);
            lineDataSetMa.setFillAlpha(50);
        } else if (type == 1) {
            lineDataSetMa.setAxisDependency(YAxis.AxisDependency.RIGHT);
            lineDataSetMa.setColor(Color.parseColor("#5C3F7D"));
        } else {
            lineDataSetMa.setAxisDependency(YAxis.AxisDependency.RIGHT);
            lineDataSetMa.setColor(getResources().getColor(R.color.transparent));
        }
        lineDataSetMa.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSetMa.setLineWidth(1f);

        lineDataSetMa.setDrawCircles(false);
        lineDataSetMa.setAxisDependency(YAxis.AxisDependency.LEFT);

        return lineDataSetMa;
    }

    @Override
    public void setPresenter(KlineContract.MinutePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void allCurrencySuccess(List<Currency> obj) {
        if (obj == null) return;
        for (Currency currency : obj) {
            if (symbol.equals(currency.getSymbol())) {
                this.currency = currency;
                break;
            }
        }
        Long to = System.currentTimeMillis();
        Date date = new Date();
        String str = WonderfulDateUtils.getFormatTime(null, date);
        String strFrom = str.split(" ")[0] + " 00:00:00";
        long from = WonderfulDateUtils.getTimeMillis(null, strFrom);
        WonderfulLogUtils.loge("INFO", "str  " + str + "****" + "from  " + from + "******" + "to  " + to);
        String resolution = 1 + "";
        presenter.KData(symbol, from, to, resolution);
    }

    @Override
    public void allCurrencyFail(Integer code, String toastMessage) {
        //do nothing
    }

    @Override
    public void KDataSuccess(JSONArray obj) {
        mData = new DataParse();
        mData.parseMinutes(obj, (float) currency.getLastDayClose());
        initChartMinutesData(minuteLineChart);
        initChartVolumeData(volumeChart);
    }

    /**
     * 设置 分时图数据
     */
    private void initChartMinutesData(MyCombinedChartX combinedChartX) {
        try {
            setMarkerViewButtom(mData, combinedChartX);
            if (mData.getDatas().size() == 0) {
                combinedChartX.setNoDataText("暂无数据");
                return;
            }
            //设置y左右两轴最大最小值
            combinedChartX.getAxisLeft().setAxisMinValue(mData.getMin());
            combinedChartX.getAxisLeft().setAxisMaxValue(mData.getMax());
            combinedChartX.getAxisRight().setAxisMinValue(mData.getPercentMin());
            combinedChartX.getAxisRight().setAxisMaxValue(mData.getPercentMax());

            //基准线
            LimitLine ll = new LimitLine(0);
            ll.setLineWidth(1f);
            ll.setLineColor(Color.parseColor("#aaaaaa"));
            ll.enableDashedLine(10f, 5f, 0f);
            ll.setLineWidth(1);
            combinedChartX.getAxisRight().addLimitLine(ll);
            combinedChartX.getAxisRight().setBaseValue(0);

            ArrayList<Entry> lineCJEntries = new ArrayList<>();
            ArrayList<Entry> lineJJEntries = new ArrayList<>();

            List<BarEntry> barEntries = new ArrayList<>();
            for (int i = 0, j = 0; i < mData.getDatas().size(); i++, j++) {
                MinutesBean t = mData.getDatas().get(j);

                if (t == null) {
                    lineCJEntries.add(new Entry(Float.NaN, i));
                    lineJJEntries.add(new Entry(Float.NaN, i));
                    continue;
                }
                if (!TextUtils.isEmpty(stringSparseArray.get(i)) &&
                        stringSparseArray.get(i).contains("/")) {
                    i++;
                }
                lineCJEntries.add(new Entry(mData.getDatas().get(i).cjprice, i));
                lineJJEntries.add(new Entry(mData.getDatas().get(i).avprice, i));

                barEntries.add(new BarEntry(mData.getDatas().get(i).cjprice, i));
            }

            ArrayList<ILineDataSet> sets = new ArrayList<>();
            sets.add(setLine(0, getMinutesCount(), lineCJEntries));
//        sets.add(setLine(1, getMinutesCount(), lineJJEntries));
        /*注老版本LineData参数可以为空，最新版本会报错，修改进入ChartData加入if判断*/
            LineData lineData = new LineData(getMinutesCount(), sets);
            lineData.setHighlightEnabled(false);


            //需要添加一个假的bar，才能用使用自定义的高亮
            BarDataSet set = new BarDataSet(barEntries, "");
            set.setHighlightEnabled(true);
            set.setHighLightAlpha(255);
            set.setHighLightColor(Color.parseColor("#aaaaaa"));
            set.setDrawValues(false);
            set.setColor(getResources().getColor(R.color.transparent));

            BarData barData = new BarData(getMinutesCount(), set);
            barData.setHighlightEnabled(true);

            CombinedData combinedData = new CombinedData(getMinutesCount());
            combinedData.setData(barData);
            combinedData.setData(lineData);
            combinedChartX.setData(combinedData);

            combinedChartX.invalidate();//刷新图
        } catch (Exception ex) {

        }
    }

    /**
     * 设置成交量图数据
     */
    private void initChartVolumeData(MyCombinedChartX combinedChartX) {
        try {
            setMarkerView(mData, combinedChartX);
            combinedChartX.getAxisLeft().setAxisMaxValue(mData.getVolmax()); /*单位*/
            combinedChartX.getAxisRight().setAxisMaxValue(mData.getVolmax());
//        String unit = MyUtils.getVolUnit(mData.getVolmax());
//        String wan = getString(R.string.wan_unit);
//        String yi = getString(R.string.yi_unit);
//        int u = 1;
//        if (wan.equals(unit)) {
//            u = 4;
//        } else if (yi.equals(unit)) {
//            u = 8;
//        }
//        /*次方*/
//        combinedChartX.getAxisRight().setValueFormatter(new VolFormatter((int) Math.pow(10, u)));
            ArrayList<Entry> lineJJEntries = new ArrayList<>();
            ArrayList<BarEntry> barEntries = new ArrayList<>();
            for (int i = 0, j = 0; i < mData.getDatas().size(); i++, j++) {
                MinutesBean t = mData.getDatas().get(j);
                if (t == null) {
                    barEntries.add(new BarEntry(Float.NaN, i));
                    continue;
                }
                if (!TextUtils.isEmpty(stringSparseArray.get(i)) &&
                        stringSparseArray.get(i).contains("/")) {
                    i++;
                }
                lineJJEntries.add(new Entry(0, i));
//            barEntries.add(new BarEntry(mData.getDatas().get(i).cjnum, i));
                barEntries.add(new BarEntry(i, t.high, t.low, t.open, t.close, t.cjnum));
            }
            BarDataSet barDataSet = new BarDataSet(barEntries, "成交量");
//        barDataSet.setBarSpacePercent(20); //bar空隙，可以控制树状图的大小，空隙越大，树状图越窄
            barDataSet.setHighLightColor(Color.parseColor("#aaaaaa"));// 设置点击某个点时，横竖两条线的颜色，就是高亮线的颜色
            barDataSet.setHighLightAlpha(255);//设置高亮线的透明度
            barDataSet.setDrawValues(false);//是否在线上绘制数值
            barDataSet.setHighlightEnabled(true);//是否启用高亮线
//        barDataSet.setColor(getResources().getColor(R.color.increasing_color));//设置树状图颜色
            List<Integer> list = new ArrayList<>();
            list.add(getResources().getColor(R.color.typeGreen));
            list.add(getResources().getColor(R.color.typeRed));
            barDataSet.setColors(list);//可以给树状图设置多个颜色，判断条件在BarChartRenderer 类的140行以下修改了判断条件
            barDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);//设置这些值对应哪条轴
            BarData barData = new BarData(getMinutesCount(), barDataSet);

            ArrayList<ILineDataSet> sets = new ArrayList<>();
            sets.add(setLine(2, getMinutesCount(), lineJJEntries));
        /*注老版本LineData参数可以为空，最新版本会报错，修改进入ChartData加入if判断*/
            LineData lineData = new LineData(getMinutesCount(), sets);
            lineData.setHighlightEnabled(false);

            CombinedData combinedData = new CombinedData(getMinutesCount());
            combinedData.setData(barData);
            combinedData.setData(lineData);
            combinedChartX.setData(combinedData);

            combinedChartX.invalidate();
        } catch (Exception ex) {

        }
    }

    @Override
    public void KDataFail(Integer code, String toastMessage) {

    }
}
