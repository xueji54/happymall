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
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServiceResponse.creatBysuccessMessage(forgetToken);
        }
        return ServiceResponse.creatByErrorMessage("问题的答案错误！");
    }

    public ServiceResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        if(StringUtils.isBlank(forgetToken)){
            return ServiceResponse.creatByErrorMessage("操作错误，请重新执行重置密码操作");
        }
        ServiceResponse validResult = checkValid(username,Const.USER_NAME);
        if(validResult.isSuccess()){
            return ServiceResponse.creatByErrorMessage("用户名不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        if(StringUtils.isBlank(token)){
            return ServiceResponse.creatByErrorMessage("您的身份信息无效或者过期，请重新申请");
        }
        if(StringUtils.equals(forgetToken,token)){
            String md5password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username,md5password);
            if(rowCount>0){
                return ServiceResponse.creatBysuccessMessage("修改密码成功");
            }else{
                return ServiceResponse.creatByErrorMessage("修改密码失败！token错误");
            }
        }
        return ServiceResponse.creatByErrorMessage("修改密码失败");
    }
    
    public ServiceResponse<String> resetPassword(String passwordOld,String passwordNew,User user){
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(resultCount == 0){
            ServiceResponse.creatByErrorMessage("密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount>0){
            return ServiceResponse.creatBysuccessMessage("密码修改成功！");
        }
        return ServiceResponse.creatByErrorMessage("修改密码失败，请重试");
    }

    public ServiceResponse<User> updateInformation(User user){
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount > 0){
            ServiceResponse.creatByErrorMessage("Email已存在！");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0){
            ServiceResponse.creatBysuccessMessage("更新个人信息成功！",updateUser);
        }
        return ServiceResponse.creatByErrorMessage("更新个人信息失败!");
    }

    public ServiceResponse<User> getInformation(Integer userid){
        User user = userMapper.selectByPrimaryKey(userid);
        if (user == null){
            return ServiceResponse.creatByErrorMessage("用户不存在！");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServiceResponse.creatBysuccessMessage(user);
    }
}
