package cn.ztuo.ui.home;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import cn.ztuo.R;
import cn.ztuo.ui.home.presenter.EntrustPresenter;
import cn.ztuo.adapter.EntrustAdapter;
import cn.ztuo.app.MyApplication;
import cn.ztuo.base.BaseLazyFragment;
import cn.ztuo.ui.dialog.EntrustOperateDialogFragment;
import cn.ztuo.entity.Currency;
import cn.ztuo.entity.EntrustHistory;
import cn.ztuo.utils.WonderfulCodeUtils;
import cn.ztuo.utils.WonderfulToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.ztuo.app.Injection;

/**
 * Created by Administrator on 2018/1/30.
 */

public class EntrustFragment extends BaseLazyFragment implements MainContract.EntrustView, EntrustOperateDialogFragment.OperateCallback {
    @BindView(R.id.rvEntrsut)
    RecyclerView rvEntrsut;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;
    private List<EntrustHistory> entrusts = new ArrayList<>();
    private EntrustAdapter adapter;
    private MainContract.EntrustPresenter presenter;
    private int pageNo = 0;
    private int pageSize = 20;
    private Currency currency;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_entrust;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        new EntrustPresenter(Injection.provideTasksRepository(getActivity().getApplicationContext()), this);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    private void refresh() {
        adapter.setEnableLoadMore(false);
        if (MyApplication.getApp().isLogin()) presenter.entrust(getmActivity().getToken(), pageSize, pageNo = 0, currency.getSymbol());
        else refreshLayout.setRefreshing(false);
    }

    @Override
    protected void obtainData() {
    }

    private void initRvEntrust() {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvEntrsut.setLayoutManager(manager);
        adapter = new EntrustAdapter(R.layout.adapter_entrust, entrusts);
        adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                return showBottomFragment((EntrustHistory) adapter.getData().get(position));
            }
        });
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMOre();
            }
        }, rvEntrsut);
        rvEntrsut.setAdapter(adapter);
    }

    private void loadMOre() {
        if (MyApplication.getApp().isLogin()) {
            refreshLayout.setEnabled(false);
            presenter.entrust(getmActivity().getToken(), pageSize, ++pageNo, currency.getSymbol());
        }
    }

    private boolean showBottomFragment(EntrustHistory entrust) {
        EntrustOperateDialogFragment entrustOperateFragment = EntrustOperateDialogFragment.getInstance(entrust);
        entrustOperateFragment.setTargetFragment(this, Integer.MAX_VALUE);
        entrustOperateFragment.show(getParentFragment().getChildFragmentManager(), "bottom");
        return true;
    }

    @Override
    protected void fillWidget() {
        initRvEntrust();
        if (!MyApplication.getApp().isLogin()) {
            refreshLayout.setEnabled(false);
            adapter.setEnableLoadMore(false);
        }
    }

    @Override
    protected void loadData() {
        if (MyApplication.getApp().isLogin() && currency != null)
            presenter.entrust(getmActivity().getToken(), pageSize, pageNo, currency.getSymbol());
    }

    @Override
    protected boolean isImmersionBarEnabled() {
        return false;
    }

    @Override
    public void setPresenter(MainContract.EntrustPresenter presenter) {
        this.presenter = presenter;
    }

    public void resetSymbol(Currency currency) {
        this.currency = currency;
        loadData();
    }

    public void setViewContent(Currency currency) {
        this.currency = currency;
    }

    @Override
    public void entrustSuccess(List<EntrustHistory> obj) {
        refreshLayout.setEnabled(true);
        refreshLayout.setRefreshing(false);
        adapter.setEnableLoadMore(true);
        adapter.loadMoreComplete();
        if (obj == null) return;
        if (pageNo == 0) {
            this.entrusts.clear();
            if (obj.size() == 0) adapter.loadMoreEnd();
            else this.entrusts.addAll(obj);
        } else {
            if (obj.size() != 0) this.entrusts.addAll(obj);
            else adapter.loadMoreEnd();
        }
        adapter.disableLoadMoreIfNotFullPage();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void entrustFail(Integer code, String toastMessage) {
        refreshLayout.setEnabled(true);
        refreshLayout.setRefreshing(false);
        adapter.setEnableLoadMore(true);
        adapter.loadMoreComplete();
    }

    @Override
    public void cancleEntrustSuccess(String obj) {
        WonderfulToastUtils.showToast(obj);
        loadData();
    }

    @Override
    public void cancleEntrustFail(Integer code, String toastMessage) {
        WonderfulCodeUtils.checkedErrorCode(getmActivity(), code, toastMessage);
    }

    @Override
    public void cancleOrder(String orderId) {
        presenter.cancleEntrust(getmActivity().getToken(), orderId);
    }


}
