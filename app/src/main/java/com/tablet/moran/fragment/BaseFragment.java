package com.tablet.moran.fragment;


import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewStub;

import com.tablet.moran.HHApplication;
import com.tablet.moran.config.Constant;
import com.tablet.moran.presenter.implPresenter.BasePresenterImpl;
import com.tablet.moran.tools.DialogUtils;
import com.tablet.moran.tools.PreferencesUtils;
import com.tablet.moran.tools.net.NetWorkUtil;

import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {

    private ConnectivityManager.NetworkCallback connectivityCallback;
    public boolean connected = false;
    private boolean monitoringConnectivity = false;
    private ConnectivityManager connectivityManager;
    private ViewStub viewStub;

    protected Dialog dialog;
    protected String token="";
    protected String userId;

    protected Unbinder unbinder;

    private BasePresenterImpl basePresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connected = NetWorkUtil.isNetworkConnected(getActivity());

        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        //屏幕方向只准竖屏
        if (!connected){
//            AppUtils.showToast(getActivity(), getResources().getString(R.string.net_dissconncted));
        }

        token = PreferencesUtils.getString(getActivity(), Constant.ACCESS_TOKEN);
        userId = PreferencesUtils.getString(getActivity(), Constant.USER_ID);
        dialog = DialogUtils.getProgressDialog(getActivity());

        if (!connected) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                connectivityCallback = new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(Network network) {

                        connected = true;

                        initDataSource();

                    }

                    @Override
                    public void onLost(Network network) {
                        connected = false;
                    }
                };
                //监听网络状态
                connectivityManager.registerNetworkCallback(
                        new NetworkRequest.Builder()
                                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                                .build(),
                        connectivityCallback);

                monitoringConnectivity = true;

            }
        }

    }

    //------------------------生命周期-----------------------
    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();

        //取消监听网络状态变化

        if (monitoringConnectivity) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                connectivityManager.unregisterNetworkCallback(connectivityCallback);
            }
            monitoringConnectivity = false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        dialog.dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(basePresenter != null) {
            basePresenter.unsubcrible();
        }
    }

    /**
     * 验证是否已经登陆
     * @return
     */
    public boolean goLogin(){
        if(!HHApplication.loginFlag){
//            startActivity(new Intent(getActivity(), WillLoginActivity.class));
            return true;
        }else
            return false;
    }

    protected void initDataSource() {

    }

    protected void initView(View view) {

    }

    protected void setListener() {

        if(viewStub != null && !connected) {

            final View view = viewStub.inflate();

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    connected = NetWorkUtil.isNetworkConnected(getActivity());

                    if (connected) {

                        view.setVisibility(View.GONE);

                        connected = true;

                        initDataSource();
                    }
                }
            });
        }
    }

}
