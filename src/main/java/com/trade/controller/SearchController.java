package com.trade.controller;

import com.trade.dto.ProductDTO;
import com.trade.exception.ServiceException;
import com.trade.service.ProductSearchService;
import com.trade.utils.ErrorHandling;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class SearchController {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private ProductSearchService productSearchService;

    @GetMapping("/search")
    public ModelAndView findProduct(@RequestParam("search_phrase") String searchPhrase,
                                    @CookieValue("userID") long userId) {

        try {

            logger.info("starting searching for products");

            List<ProductDTO> productDTOS = productSearchService.findProducts(searchPhrase);

            System.out.println("size = "+productDTOS.size());

            ModelAndView modelAndView = new ModelAndView("search-results");
            modelAndView.addObject("searchPhrase", searchPhrase);
            modelAndView.addObject("productDTOS", productDTOS);
            return modelAndView;

        } catch (ServiceException e) {

            logger.error("searching process failed", e);
            return ErrorHandling.getErrorPage("error while search for products");
        }
    }

}
