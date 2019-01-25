package com.trade.controller;

import com.trade.dto.ProductDTO;
import com.trade.exception.ServiceException;
import com.trade.model.Product;
import com.trade.model.converter.ProductModelToDTOConverter;
import com.trade.service.PaginationService;
import com.trade.service.dao.ProductService;
import com.trade.utils.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

import static com.trade.utils.ConstantsUtils.NUMBER_OF_PRODUCTS_ON_PAGE;


@Controller
@RequestMapping("/products")
public class ProductsController {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private ProductService productService;

    @Autowired
    private PaginationService paginationService;

    @Autowired
    private ProductModelToDTOConverter productModelToDTOConverter;

    private static final int FIRST_PAGE = 1;

    @GetMapping
    public ModelAndView getProductsList() {


        return new ModelAndView("redirect:/products/page/" + FIRST_PAGE);
    }

    @GetMapping("/page/{page_number}")
    public ModelAndView getProductsByPage(@PathVariable("page_number") Integer pageNumber) {

        try {

            if (pageNumber <= 0) {
                return new ModelAndView("redirect:/products/page/" + FIRST_PAGE);
            }

            List<Product> productsOnPage = productService.findByPage(pageNumber);
            List<ProductDTO> productDTOsOnPageList = productModelToDTOConverter.convert(productsOnPage);

            final int totalProductsNumber = productService.findTotalProductsNumber();
            final int numberOfPages = (int) Math.ceil(totalProductsNumber / (NUMBER_OF_PRODUCTS_ON_PAGE * 1.0));

            if (pageNumber > numberOfPages) {
                return new ModelAndView("redirect:/products/page/" + FIRST_PAGE);
            }

            List<Integer> pageNumbers =
                    paginationService.calcPageNumbersForPagination(pageNumber, numberOfPages);

            ModelAndView modelAndView = new ModelAndView("products");
            modelAndView.addObject("products", productDTOsOnPageList);
            modelAndView.addObject("number_of_pages", numberOfPages);
            modelAndView.addObject("current_page", pageNumber);

            modelAndView.addObject("page_numbers", pageNumbers);

            return modelAndView;

        } catch (ServiceException e) {

            logger.error("not managed to read products from table by page");
            e.printStackTrace();

            return ExceptionUtils.getErrorPage("not managed to get products by page");
        }

    }

    @GetMapping("/{id}")
    public ModelAndView getProductPage(@PathVariable("id") long id) {

        Product product = null;
        try {

            product = productService.findById(id);

        } catch (ServiceException e) {

            logger.info("Error while reading product with id =" + id);
            e.printStackTrace();
            return ExceptionUtils.getErrorPage("Error while reading product with id = " + id);
        }

        return new ModelAndView("product", "product", product);
    }


    @GetMapping("/picture/{product_id}")
    public void getProductPicture(@PathVariable("product_id") long productId,
                                  HttpServletResponse response) {

        logger.info("getting product picture of the user_id = " + productId);

        try {

            Product product = productService.findById(productId);

            if (product.getImage() != null) {

                logger.info("user has a picture");

                try {

                    InputStream binaryStream = product.getImage().getBinaryStream();
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                    int nRead;
                    byte[] data = new byte[64];

                    while ((nRead = binaryStream.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }
                    byte[] pictureAsBytes = buffer.toByteArray();

                    response.setContentType("image/jpeg");
                    response.setContentLength(pictureAsBytes.length);
                    response.getOutputStream().write(pictureAsBytes);

                    logger.info("picture is sent with success");

                } catch (IOException e) {
                    logger.info("error while converting Blob image to array of bytes");
                    e.printStackTrace();
                }

            }

        } catch (SQLException | ServiceException e) {
            logger.info("not managed to find product with id = " + productId);
            e.printStackTrace();
            ExceptionUtils.getErrorPage("not managed to find product");
        }

    }

}
