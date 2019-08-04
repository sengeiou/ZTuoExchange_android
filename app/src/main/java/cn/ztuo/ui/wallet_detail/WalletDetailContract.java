package cn.ztuo.ui.wallet_detail;


import cn.ztuo.base.Contract;
import cn.ztuo.entity.WalletDetail;
import cn.ztuo.entity.WalletDetailNew;

import java.util.List;

/**
 * Created by Administrator on 2017/9/25.
 */

public interface WalletDetailContract {
    interface View extends Contract.BaseView<Presenter> {

        void allTransactionSuccess(WalletDetailNew obj);

        void allTransactionFail(Integer code, String toastMessage);
    }

    interface Presenter extends Contract.BasePresenter {

        void allTransaction(String token, int pageNo, int pageSize,int memberId,String startTime,String endTime,String symbol,String type);
    }
}
