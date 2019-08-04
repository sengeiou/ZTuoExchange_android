package cn.ztuo.ui.home.presenter;

import cn.ztuo.ui.home.MainContract;
import cn.ztuo.data.DataSource;

/**
 * Created by Administrator on 2018/2/24.
 */

public class ThreePresenter implements MainContract.ThreePresenter {
    private MainContract.ThreeView view;
    private DataSource dataRepository;

    public ThreePresenter(DataSource dataRepository, MainContract.ThreeView view) {
        this.view = view;
        this.dataRepository = dataRepository;
        this.view.setPresenter(this);
    }

}
