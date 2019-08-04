package cn.ztuo.ui.kline;


import cn.ztuo.data.DataSource;
import cn.ztuo.entity.Currency;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by Administrator on 2018/1/17.
 */

public class KlinePresenter implements KlineContract.Presenter {
    private DataSource dataRepository;
    private KlineContract.View view;

    public KlinePresenter(DataSource dataRepository, KlineContract.View view) {
        this.dataRepository = dataRepository;
        this.view = view;
        view.setPresenter(this);
    }


    @Override
    public void KData(String symbol, Long from, Long to, String resolution) {
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

    @Override
    public void allCurrency() {
        dataRepository.allCurrency(new DataSource.DataCallback() {
            @Override
            public void onDataLoaded(Object obj) {
                view.hideLoadingPopup();
                view.allCurrencySuccess((List<Currency>) obj);
            }

            @Override
            public void onDataNotAvailable(Integer code, String toastMessage) {
                view.hideLoadingPopup();
                view.allCurrencyFail(code, toastMessage);

            }
        });
    }
}
