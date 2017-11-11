package com.happymall.controller.portal;

import com.happymall.common.Const;
import com.happymall.common.ResponseCode;
import com.happymall.common.ServiceResponse;
import com.happymall.pojo.User;
import com.happymall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import static com.happymall.common.Const.CURRENT_USER;


@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;
    /**
     * 用户登录
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> login(String username, String password, HttpSession session){
        ServiceResponse<User> response = iUserService.login(username, password);
        if(response.isSuccess()){
            session.setAttribute(CURRENT_USER,response.getDate());
        }
        return response;
    }

    @RequestMapping(value = "logout.do",method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<String> logout(HttpSession session) {
        session.removeAttribute(Const.CURRENT_USER);
        return ServiceResponse.creatBySuccess();
    }

    @RequestMapping(value = "register.do",method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<String> register(User user){
        return iUserService.register(user);
    }

    @RequestMapping(value = "checkValid.do",method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<String> checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }

    @RequestMapping(value = "getUserInfo.do",method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<User> getUserInfo(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user != null){
            return ServiceResponse.creatBysuccessMessage(user);
        }
        return ServiceResponse.creatByErrorMessage("用户未登陆！");
    }

    @RequestMapping(value = "getQuestion.do",method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<String> forgetGetQuestion(String username){
        return iUserService.selectQusetion(username);
    }

    @RequestMapping(value = "checkAnswer.do",method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.forgetCheckAnswer(username,question,answer);
    }

    @RequestMapping(value = "forgetPassword.do",method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        return iUserService.forgetResetPassword(username,passwordNew,forgetToken);
    }
    
    @RequestMapping(value = "resetPassword.do",method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<String> ResetPassword(HttpSession session,String passwordOld,String passwordNew){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServiceResponse.creatByErrorMessage("未登录，请登录！");
        }
        else{
            return iUserService.resetPassword(passwordOld,passwordNew,user);
        }
    }

    @RequestMapping(value = "update_information.do",method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<User> update_information(HttpSession session,User user){
        User current_User = (User)session.getAttribute(Const.CURRENT_USER);
        if(current_User == null){
            return ServiceResponse.creatByErrorMessage("未登录，请登陆！");
        }
        user.setId(current_User.getId());
        ServiceResponse<User> response = iUserService.updateInformation(user);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getDate());
        }
        return response;
    }

    @RequestMapping(value = "get_information.do",method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<User> get_information(HttpSession session){
        User current_User = (User)session.getAttribute(Const.CURRENT_USER);
        if(current_User == null){
            return ServiceResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登陆，需要强制登陆statue = 10");
        }
        return iUserService.getInformation(current_User.getId());
    }
}
