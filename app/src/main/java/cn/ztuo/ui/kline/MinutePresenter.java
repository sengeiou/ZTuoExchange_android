package cn.ztuo.ui.kline;

import cn.ztuo.data.DataSource;
import cn.ztuo.entity.Currency;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by Administrator on 2018/3/19.
 */

public class MinutePresenter implements KlineContract.MinutePresenter {
    private DataSource dataRepository;
    private KlineContract.MinuteView view;

    public MinutePresenter(DataSource dataRepository, KlineContract.MinuteView view) {
        this.dataRepository = dataRepository;
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void allCurrency() {
        dataRepository.allCurrency(new DataSource.DataCallback() {
            @Override
            public void onDataLoaded(Object obj) {
                view.allCurrencySuccess((List<Currency>) obj);
            }

            @Override
            public void onDataNotAvailable(Integer code, String toastMessage) {
                view.allCurrencyFail(code, toastMessage);

            }
        });
    }

    @Override
    public void KData(String symbol, long from, Long to, String resolution) {
        dataRepository.KData(symbol, from, to, resolution, new DataSource.DataCallback() {
            @Override
            public void onDataLoaded(Object obj) {
                view.KDataSuccess((JSONArray) obj);
            }

            @Override
            public void onDataNotAvailable(Integer code, String toastMessage) {
                view.KDataFail(code, toastMessage);

            }
        });
    }
}
