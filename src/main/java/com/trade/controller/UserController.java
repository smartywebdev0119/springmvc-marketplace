package com.trade.controller;

import com.trade.utils.ExceptionUtils;
import com.trade.dto.UserDTO;
import com.trade.exception.ServiceException;
import com.trade.model.User;
import com.trade.model.converter.UserModelToDTOConverter;
import com.trade.service.dao.UserService;
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

@Controller
@RequestMapping("/user")
public class UserController {

    private final Logger logger = Logger.getLogger(getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private UserModelToDTOConverter converter;


    @GetMapping("/{id}")
    public ModelAndView getUserPage(@PathVariable("id") long userID) {

        logger.info("userID = " + userID + " opening the profile");

        UserDTO userDTO;

        try {

            User user = userService.findById(userID);
            userDTO = converter.convert(user);

        } catch (ServiceException e) {

            logger.info("no managed to find user with id = " + userID);
            e.printStackTrace();
            return ExceptionUtils.getDefaultErrorPage();
        }

        logger.info("UserController.getUserPage");
        logger.info(userDTO);

        return new ModelAndView("user", "user", userDTO);
    }

    @GetMapping("/picture/{user_id}")
    public void getUserPicture(@PathVariable("user_id") long userID,
                               HttpServletResponse response) {

        logger.info("getting picture of the user_id = " + userID);

        try {

            User user = userService.findById(userID);

            if (user.getImage() != null) {

                logger.info("user has a picture");

                InputStream binaryStream = user.getImage().getBinaryStream();
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

            }

        } catch (SQLException e) {
            logger.info("no managed to find user with id = " + userID);
            e.printStackTrace();

        } catch (ServiceException e) {
            logger.info("user not found");
            e.printStackTrace();

        } catch (IOException e) {
            logger.info("error while converting Blob image to array of bytes");
            e.printStackTrace();
        }

    }


}
