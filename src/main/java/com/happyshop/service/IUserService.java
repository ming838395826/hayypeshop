package com.happyshop.service;

import com.happyshop.common.ServerResponse;
import com.happyshop.pojo.User;
import org.springframework.stereotype.Service;

/**
 * Created by ming on 2017/12/17.
 */

public interface IUserService {

    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

}
