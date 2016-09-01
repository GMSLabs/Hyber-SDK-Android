package com.hyber;

interface IHyberAuthorizationListener {

    void onLoginProcess();

    void onAuthorized();

    void onAuthorizationUpdating();

    void onAutorizationUpdated();

    void onLogoutProcess();

    void onUnauthorized();

}
