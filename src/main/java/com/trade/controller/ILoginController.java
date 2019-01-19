package com.trade.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.*;

import javax.servlet.http.*;

public interface ILoginController {

    String getLoginPage();

    ModelAndView login(@RequestParam("username") String username,
                              @RequestParam("password") String password,
                              HttpServletRequest request,
                              HttpServletResponse response);

    ModelAndView logout(@CookieValue("userID") long userID);
}
