package com.trade.model.converter;

import com.trade.dto.UserDTO;
import com.trade.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserToDTOConverter {

    private static final Long DEFAULT_USER_IMAGE_ID = 0L;

    public UserDTO convert(User user) {

        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setName(user.getName());
        userDTO.setSurname(user.getSurname());
        userDTO.setRole(user.getRole());

        if (user.getImage() != null) {

            userDTO.setImageId(user.getId());

        } else {

            userDTO.setImageId(DEFAULT_USER_IMAGE_ID);
        }

        return userDTO;
    }

    public List<UserDTO> convert(List<User> users) {

        List<UserDTO> userDTOList = new ArrayList<>();

        for (User user : users) {
            userDTOList.add(convert(user));
        }

        return userDTOList;
    }


}
