package com.trade.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class MainPageController {

    private final Logger logger = Logger.getLogger(this.getClass());

    @GetMapping
    public ModelAndView getMainPage(){

        logger.info("get main page");

        return new ModelAndView("main");
    }

}
