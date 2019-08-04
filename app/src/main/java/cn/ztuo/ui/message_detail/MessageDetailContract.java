package cn.ztuo.ui.message_detail;


import cn.ztuo.base.Contract;
import cn.ztuo.entity.Message;

/**
 * Created by Administrator on 2017/9/25.
 */

public interface MessageDetailContract {
    interface View extends Contract.BaseView<Presenter> {

        void messageDetailSuccess(Message obj);

        void messageDetailFail(Integer code, String toastMessage);
    }

    interface Presenter extends Contract.BasePresenter {

        void messageDetail(String id);
    }
}
