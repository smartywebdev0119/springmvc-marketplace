package com.trade.interceptor;

import com.trade.service.HeaderMenuUpdateService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class updates data for the top navigation menu
 * at the end on each request
 */
public class HeaderMenuInterceptor implements HandlerInterceptor {

    private final Logger logger = Logger.getLogger(getClass());

    @Autowired
    private HeaderMenuUpdateService menuUpdateService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o, ModelAndView modelAndView) throws Exception {

        logger.info("update header");

        if (modelAndView != null) {

            menuUpdateService.update(modelAndView, request, response);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
