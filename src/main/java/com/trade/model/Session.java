package com.trade.model;

/**
 *
 * For keeping log of user's logins and logouts
 *
 */
public class Session {

    private long id;
    private long userId;
    private String sessionToken;
    private String loginDateTime;
    private String logoutDateTime;
    private boolean isExpired;

    public Session() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public boolean isExpired() {
        return isExpired;
    }

    public void setExpired(boolean expired) {
        isExpired = expired;
    }

    public String getLoginDateTime() {
        return loginDateTime;
    }

    public void setLoginDateTime(String loginDateTime) {
        this.loginDateTime = loginDateTime;
    }

    public String getLogoutDateTime() {
        return logoutDateTime;
    }

    public void setLogoutDateTime(String logoutDateTime) {
        this.logoutDateTime = logoutDateTime;
    }
}
