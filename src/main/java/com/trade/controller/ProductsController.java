package com.trade.controller;

import com.trade.ExceptionUtils;
import com.trade.exception.ServiceException;
import com.trade.model.Product;
import com.trade.service.dao.ProductService;
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


@Controller
@RequestMapping("/products")
public class ProductsController {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private ProductService productService;

    @GetMapping
    public ModelAndView getProductsList() {

        List<Product> products = null;

        // TODO implement pagination

        try {

            products = productService.findAll();

        } catch (ServiceException e) {
            logger.info("Error while reading all products");
            e.printStackTrace();
            return ExceptionUtils.getErrorPage("Error while reading all products");
        }

        return new ModelAndView("products", "products", products);
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

        logger.info("getting product picture of the user_id = "+productId);

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
