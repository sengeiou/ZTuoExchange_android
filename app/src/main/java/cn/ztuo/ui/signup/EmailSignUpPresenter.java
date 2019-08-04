package cn.ztuo.ui.signup;


import cn.ztuo.data.DataSource;

import org.json.JSONObject;

/**
 * Created by Administrator on 2017/9/25.
 */

public class EmailSignUpPresenter implements SignUpContract.EmailPresenter {
    private final DataSource dataRepository;
    private final SignUpContract.EmailView view;

    public EmailSignUpPresenter(DataSource dataRepository, SignUpContract.EmailView view) {
        this.dataRepository = dataRepository;
        this.view = view;
        view.setPresenter(this);
    }


    @Override
    public void signUpByEmail(String email, String username, String password, String country, String challenge, String validate, String seccode,String tuijian2) {
        view.displayLoadingPopup();
        dataRepository.signUpByEmail(email, username, password, country,challenge,validate,seccode,tuijian2, new DataSource.DataCallback() {
            @Override
            public void onDataLoaded(Object obj) {
                view.hideLoadingPopup();
                view.signUpByEmailSuccess((String) obj);
            }

            @Override
            public void onDataNotAvailable(Integer code, String toastMessage) {
                view.hideLoadingPopup();
                view.signUpByEmailFail(code, toastMessage);
            }
        });
    }

    @Override
    public void captch() {
        view.displayLoadingPopup();
        dataRepository.captch(new DataSource.DataCallback() {
            @Override
            public void onDataLoaded(Object obj) {
                view.hideLoadingPopup();
                view.captchSuccess((JSONObject) obj);
            }

            @Override
            public void onDataNotAvailable(Integer code, String toastMessage) {
                view.hideLoadingPopup();
                view.captchFail(code, toastMessage);
            }
        });
    }

}
