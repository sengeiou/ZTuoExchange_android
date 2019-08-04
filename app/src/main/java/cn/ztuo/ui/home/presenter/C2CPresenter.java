package cn.ztuo.ui.home.presenter;

import cn.ztuo.ui.home.MainContract;
import cn.ztuo.data.DataSource;
import cn.ztuo.entity.C2C;

/**
 * Created by Administrator on 2018/2/28.
 */

public class C2CPresenter implements MainContract.C2CPresenter {
    private MainContract.C2CView view;
    private DataSource dataRepository;

    public C2CPresenter(DataSource dataRepository, MainContract.C2CView view) {
        this.view = view;
        this.dataRepository = dataRepository;
        this.view.setPresenter(this);
    }

    @Override
    public void advertise(int pageNo, int pageSize, String advertiseType, String id) {
        dataRepository.advertise(pageNo, pageSize, advertiseType, id, new DataSource.DataCallback() {
            @Override
            public void onDataLoaded(Object obj) {
                view.advertiseSuccess((C2C) obj);
            }

            @Override
            public void onDataNotAvailable(Integer code, String toastMessage) {
                view.advertiseFail(code, toastMessage);

            }
        });
    }
}
