package cn.ztuo.ui.bind_email;


import cn.ztuo.base.Contract;

/**
 * Created by Administrator on 2017/9/25.
 */

public interface BindEmailContract {
    interface View extends Contract.BaseView<Presenter> {

        void bindEmailSuccess(String obj);

        void bindEmailFail(Integer code, String toastMessage);

        void sendEmailCodeSuccess(String obj);

        void sendEmailCodeFail(Integer code, String toastMessage);
    }

    interface Presenter extends Contract.BasePresenter {
        void sendEmailCode(String token, String email);

        void bindEmail(String token, String phone, String code, String password);
    }
}
