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
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final Logger logger = Logger.getLogger(this.getClass());


    @Autowired
    private UserService userService;

    @Autowired
    private UserModelToDTOConverter converter;

    @GetMapping
    public ModelAndView getProfile(@CookieValue("userID") long userID) {

        logger.info("userID = " + userID);

        UserDTO userDTO;

        try {

            User user = userService.findById(userID);
            userDTO = converter.convert(user);

        } catch (ServiceException e) {
            logger.info("not managed to find user with id = " + userID);
            e.printStackTrace();
            return ExceptionUtils.getErrorPage("not managed to find user with id");
        }

        logger.info("ProfileController.getProfile");
        logger.info(userDTO);

        return new ModelAndView("profile", "user", userDTO);
    }

    @PostMapping("/picture")
    public ModelAndView uploadUserPicture(@CookieValue("userID") long thisUserID,
                                          @RequestParam("user_image") MultipartFile file) {

        logger.info("user with id = "+thisUserID+" uploading picture to the profile");

        User user = null;

        try {

            user = userService.findById(thisUserID);
            Blob userImage = new SerialBlob(file.getBytes());
            user.setImage(userImage);

            logger.info("user picture uploaded for user id = "+thisUserID);

        } catch (SQLException e) {

            logger.info("not managed to read user with id = "+thisUserID);
            e.printStackTrace();
            return ExceptionUtils.getErrorPage("not managed to read user");

        } catch (IOException e) {

            logger.info("not managed to read picture as bytes");
            e.printStackTrace();
            return ExceptionUtils.getErrorPage("not managed to read picture as bytes");

        } catch (ServiceException e) {

            logger.info("user not found");
            e.printStackTrace();
            return ExceptionUtils.getErrorPage("user not found");

        }

        try {

            userService.updateImage(user);
            logger.info("user picture updated for user id = "+thisUserID);

        } catch (ServiceException e) {

            String errorMessage = "error while updating user's image";

            logger.info(errorMessage);
            e.printStackTrace();
            return ExceptionUtils.getErrorPage(errorMessage);
        }

        return new ModelAndView(
                "redirect:/profile",
                "user",
                converter.convert(user)
        );
    }



}
