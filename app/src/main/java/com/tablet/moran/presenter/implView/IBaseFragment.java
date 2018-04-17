package com.tablet.moran.presenter.implView;

/**
 * Created by stone on 2016/4/26 0026.
 */
public interface IBaseFragment {
    void showProgressDialog();

    void hidProgressDialog();

    void showError(String error);
}
