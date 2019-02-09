package com.trade.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/error")
public class ErrorController {

    private static final String DEFAULT_MESSAGE = "something went wrong";

    @GetMapping
    public ModelAndView getErrorPage(
            @RequestParam(value = "error_message", required = false) String message){

        ModelAndView error = new ModelAndView("error");

        if (message != null){

            error.addObject("error_message", message);

        } else {

            error.addObject("error_message", DEFAULT_MESSAGE);
        }

        return error;
    }

}
