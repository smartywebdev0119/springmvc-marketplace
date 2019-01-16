package com.trade;

import org.springframework.web.servlet.ModelAndView;

public class ExceptionUtils {

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
