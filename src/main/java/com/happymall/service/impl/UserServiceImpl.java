package com.happymall.service.impl;

import com.happymall.common.Const;
import com.happymall.common.ServiceResponse;
import com.happymall.common.TokenCache;
import com.happymall.dao.UserMapper;
import com.happymall.pojo.User;
import com.happymall.service.IUserService;
import com.happymall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;


    @Override
    public ServiceResponse<User> login(String username, String password) {
        int userCount = userMapper.checkUserName(username);
        if (userCount == 0){
            return ServiceResponse.creatByErrorMessage("用户名不存在");
        }

        //todo MD5加密登陆
        String MD5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username,MD5Password);
            if(user == null){
                return ServiceResponse.creatByErrorMessage("密码错误");
            }

            user.setPassword(StringUtils.EMPTY);
            return ServiceResponse.creatBysuccessMessage("登陆成功",user);
    }


    public ServiceResponse<String> register(User user){

        ServiceResponse validResponse = checkValid(user.getUsername(),Const.USER_NAME);
        if(!validResponse.isSuccess()){
            return validResponse;
        }

        validResponse = checkValid(user.getEmail(),Const.E_MAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if(resultCount == 0){
            return ServiceResponse.creatByErrorMessage("注册失败!");
        }
        return ServiceResponse.creatBysuccessMessage("注册成功！");
    }

    public ServiceResponse<String> checkValid(String str, String type) {
        if(StringUtils.isNotBlank(type)){
            if(Const.USER_NAME.equals(type)){
                int resultCount = userMapper.checkUserName(str);
                if(resultCount > 0){
                    return ServiceResponse.creatByErrorMessage("用户名已存在！");
                }
                }
            if(Const.E_MAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount > 0){
                    return ServiceResponse.creatByErrorMessage("邮箱已存在！");
                }
            }else{
                return ServiceResponse.creatByErrorMessage("参数错误！");
            }
        }
        return ServiceResponse.creatBysuccessMessage("校验成功！");
    }

    public ServiceResponse selectQusetion(String username){
       ServiceResponse validResult = checkValid(username,Const.USER_NAME);
       if(validResult.isSuccess()){
           return ServiceResponse.creatByErrorMessage("用户名不存在");
       }
        String question = userMapper.selectQuestiongByUsername(username);
       if(StringUtils.isNotBlank(question)){
           return ServiceResponse.creatBysuccessMessage(question);
       }
       return ServiceResponse.creatByErrorMessage("密码问题不存在");
    }

    public ServiceResponse<String> forgetCheckAnswer(String username,String question,String answer){
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if(resultCount > 0){
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey("Token_"+username,forgetToken);
            return ServiceResponse.creatBysuccessMessage(forgetToken);
        }
        return ServiceResponse.creatByErrorMessage("问题的答案错误！");
    }
}
