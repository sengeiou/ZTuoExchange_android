package cn.ztuo.ui.home.presenter;

import cn.ztuo.ui.home.MainContract;
import cn.ztuo.data.DataSource;
import cn.ztuo.entity.BannerEntity;

import java.util.List;

/**
 * Created by Administrator on 2018/2/24.
 */

public class OnePresenter implements MainContract.OnePresenter {
    private MainContract.OneView view;
    private DataSource dataRepository;

    public OnePresenter(DataSource dataRepository, MainContract.OneView view) {
        this.view = view;
        this.dataRepository = dataRepository;
        this.view.setPresenter(this);
    }

    @Override
    public void banners(String sysAdvertiseLocation) {
        dataRepository.banners(sysAdvertiseLocation, new DataSource.DataCallback() {
            @Override
            public void onDataLoaded(Object obj) {
                view.hideLoadingPopup();
                view.bannersSuccess((List<BannerEntity>) obj);
            }

            @Override
            public void onDataNotAvailable(Integer code, String toastMessage) {
                view.hideLoadingPopup();
                view.bannersFail(code, toastMessage);

            }
        });
    }
}
