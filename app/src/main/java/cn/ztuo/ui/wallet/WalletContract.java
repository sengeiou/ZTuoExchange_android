package cn.ztuo.ui.wallet;


import cn.ztuo.base.Contract;
import cn.ztuo.entity.Coin;
import cn.ztuo.entity.GccMatch;

import java.util.List;

/**
 * Created by Administrator on 2017/9/25.
 */

public interface WalletContract {
    interface View extends Contract.BaseView<Presenter> {

        void myWalletSuccess(List<Coin> obj);

        void myWalletFail(Integer code, String toastMessage);

        void getCheckMatchSuccess(GccMatch obj);

        void getCheckMatchFail(Integer code, String toastMessage);

        void getStartMatchSuccess(String obj);

        void getStartMatchFail(Integer code, String toastMessage);
    }

    interface Presenter extends Contract.BasePresenter {

        void myWallet(String token);

        void getCheckMatch(String token);

        void getStartMatch(String token, String amount);
    }
}
