package com.trade.utils;

import javax.servlet.http.Cookie;

public class HttpUtils {

    public static Cookie findCookie(String name, Cookie[] cookies){

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)){
                return cookie;
            }
        }

        return null;
    }

}
