package com.trade.controller;

import com.trade.dto.UserDTO;
import com.trade.exception.ServiceException;
import com.trade.model.User;
import com.trade.model.converter.UserToDTOConverter;
import com.trade.service.dao.UserService;
import com.trade.utils.ErrorHandling;
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
    private UserToDTOConverter converter;

    @GetMapping
    public ModelAndView getProfile(@CookieValue("userID") long userID) {

        try {

            logger.info("userID = " + userID);

            User user = userService.findById(userID);
            UserDTO userDTO = converter.convert(user);

            logger.info("ProfileController.getProfile");
            logger.info(userDTO);

            return new ModelAndView("profile", "user", userDTO);

        } catch (ServiceException e) {
            logger.error("not managed to find user with id = " + userID, e);
            return ErrorHandling.getErrorPage("not managed to find user with id");
        }


    }

    @PostMapping("/picture")
    public ModelAndView uploadUserPicture(@CookieValue("userID") long thisUserID,
                                          @RequestParam("user_image") MultipartFile file) {


        try {
            logger.info("user with id = "+thisUserID+" uploading picture to the profile");

            User user = userService.findById(thisUserID);
            Blob userImage = new SerialBlob(file.getBytes());
            user.setImage(userImage);

            logger.info("user picture uploaded for user id = "+thisUserID);

            try {

                userService.updateImage(user);
                logger.info("user picture updated for user id = "+thisUserID);

            } catch (ServiceException e) {

                String errorMessage = "error while updating user's image";

                logger.info(errorMessage, e);

                return ErrorHandling.getErrorPage(errorMessage);
            }

            return new ModelAndView(
                    "redirect:/profile",
                    "user",
                    converter.convert(user)
            );

        } catch (SQLException e) {

            logger.info("not managed to read user with id = "+thisUserID, e);
            return ErrorHandling.getErrorPage("not managed to read user");

        } catch (IOException e) {

            logger.info("not managed to read picture as bytes", e);
            return ErrorHandling.getErrorPage("not managed to read picture as bytes");

        } catch (ServiceException e) {

            logger.info("user not found", e);
            return ErrorHandling.getErrorPage("user not found");

        }


    }



}
