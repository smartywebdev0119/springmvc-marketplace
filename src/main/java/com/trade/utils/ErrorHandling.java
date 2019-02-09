package com.trade.utils;

import org.springframework.web.servlet.ModelAndView;

public class ErrorHandling {

    public static ModelAndView getDefaultErrorPage(){

        return new ModelAndView(
                "redirect:/error",
                "error_message",
                "something went wrong"
        );
    }

    public static ModelAndView getErrorPage(String message){

        return new ModelAndView(
                "redirect:/error",
                "error_message",
                message
        );

    }


}
