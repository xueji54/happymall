package com.happymall.service;

import com.happymall.common.ServiceResponse;
import com.happymall.pojo.User;

public interface IUserService {
    ServiceResponse<User> login(String username, String password);
    ServiceResponse<String> register(User user);
    ServiceResponse<String> checkValid(String str,String type);
    ServiceResponse selectQusetion(String username);
    ServiceResponse forgetCheckAnswer(String username,String question,String answer);
    ServiceResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken);
    ServiceResponse<String>  resetPassword(String passwordOld,String passwordNew,User user);

}
