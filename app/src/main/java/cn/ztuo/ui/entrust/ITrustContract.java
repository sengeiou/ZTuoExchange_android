package cn.ztuo.ui.entrust;

import cn.ztuo.entity.EntrustHistory;

import java.util.List;

/**
 * author: wuzongjie
 * time  : 2018/4/18 0018 11:21
 * desc  :
 */

public class ITrustContract {
    interface View {

        void errorMes(int e, String meg);

        void getCurrentSuccess(List<EntrustHistory> entrusts);

        void getCancelSuccess(String success);

        void onDataNotAvailable(int code, String message);

        void getHistorySuccess(List<EntrustHistory> success);
    }

    interface Presenter {


        /**
         * 获取当前的委托
         */
        void getCurrentOrder(String token, int pageNo, int pageSize, String symbol,String type,String direction,String startTime,String endTime);

        /**
         * 获取历史委托
         */
        void getOrderHistory(String token, int pageNo, int pageSize, String symbol,String type,String direction,String startTime,String endTime);
        void onDetach();

        /**
         * 取消委托
         */
        void getCancelEntrust(String token, String orderId);
    }
}
