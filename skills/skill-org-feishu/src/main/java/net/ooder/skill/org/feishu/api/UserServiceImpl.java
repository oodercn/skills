package net.ooder.skill.org.feishu.api;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.common.JDSException;
import net.ooder.common.org.service.UserService;
import net.ooder.config.ErrorResultModel;
import net.ooder.config.ResultModel;
import net.ooder.jds.core.User;
import net.ooder.org.OrgManager;
import net.ooder.org.Person;
import net.ooder.org.PersonNotFoundException;
import net.ooder.server.OrgManagerFactory;
import net.ooder.skill.org.feishu.org.FeishuOrgManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@EsbBeanAnnotation(id = "UserService", name = "飞书账号服务", expressionArr = "UserServiceImpl()", desc = "飞书账号服务")
public class UserServiceImpl implements UserService {

    @Autowired
    private FeishuOrgManager feishuOrgManager;

    @Autowired
    private UserInnerServiceImpl userInnerService;

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "/Login")
    public @ResponseBody ResultModel<User> login(@RequestBody User user) {
        ResultModel<User> userStatusInfo = new ResultModel<>();
        try {
            boolean login = feishuOrgManager.verifyPerson(user.getAccount(), user.getPassword());
            if (login) {
                Person person = feishuOrgManager.getPersonByAccount(user.getAccount());
                user.setId(person.getID());
                user.setName(person.getName());
                user.setEmail(person.getEmail());
                user.setPhone(person.getMobile());
                userStatusInfo.setData(user);
            } else {
                userStatusInfo = new ErrorResultModel<>();
                ((ErrorResultModel<User>) userStatusInfo).setErrdes("Invalid credentials");
            }
        } catch (Exception e) {
            userStatusInfo = new ErrorResultModel<>();
            ((ErrorResultModel<User>) userStatusInfo).setErrdes(e.getMessage());
        }
        return userStatusInfo;
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "/Logout")
    public @ResponseBody ResultModel<User> logout() {
        ResultModel<User> userStatusInfo = new ResultModel<>();
        try {
            userInnerService.logout();
        } catch (JDSException e) {
            userStatusInfo = new ErrorResultModel<>();
            ((ErrorResultModel<User>) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel<User>) userStatusInfo).setErrdes(e.getMessage());
        }
        return userStatusInfo;
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "/Register")
    public @ResponseBody ResultModel<User> register(@RequestBody User user) {
        ResultModel<User> userStatusInfo = new ResultModel<>();
        try {
            user = userInnerService.register(user);
            userStatusInfo.setData(user);
        } catch (JDSException e) {
            userStatusInfo = new ErrorResultModel<>();
            ((ErrorResultModel<User>) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel<User>) userStatusInfo).setErrdes(e.getMessage());
        }
        return userStatusInfo;
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "/SendCode")
    public @ResponseBody ResultModel sendCode(String mobile) {
        ResultModel<User> userStatusInfo = new ResultModel<>();
        User user = new User();
        user.setAccount(mobile);
        try {
            userInnerService.sendCode(user);
            userStatusInfo.setData(user);
        } catch (JDSException e) {
            userStatusInfo = new ErrorResultModel<>();
            ((ErrorResultModel<User>) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel<User>) userStatusInfo).setErrdes(e.getMessage());
        }
        return userStatusInfo;
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "/UpdateUserInfo")
    public @ResponseBody ResultModel updateUserInfo(@RequestBody User user) {
        ResultModel<User> userStatusInfo = new ResultModel<>();
        try {
            userInnerService.saveUser(user);
            userStatusInfo.setData(user);
        } catch (JDSException e) {
            userStatusInfo = new ErrorResultModel<>();
            ((ErrorResultModel<User>) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel<User>) userStatusInfo).setErrdes(e.getMessage());
        }
        return userStatusInfo;
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "/RestPw")
    public @ResponseBody ResultModel<User> restPw(String account, String newpassword, String code) {
        ResultModel<User> userStatusInfo = new ResultModel<>();
        User user = new User();
        user.setAccount(account);
        user.setNewPassword(newpassword);
        try {
            userInnerService.modifyPwd(user, code);
            userStatusInfo.setData(user);
        } catch (JDSException e) {
            userStatusInfo = new ErrorResultModel<>();
            ((ErrorResultModel<User>) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel<User>) userStatusInfo).setErrdes(e.getMessage());
        }
        return userStatusInfo;
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "/UpdatePassword")
    public @ResponseBody ResultModel<User> updatePassword(String oldpassword, String newpassword, String userId) {
        ResultModel<User> userStatusInfo = new ResultModel<>();
        User user = new User();
        user.setId(userId);
        user.setNewPassword(newpassword);
        user.setPassword(oldpassword);
        try {
            userInnerService.modifyPwd(user, "");
            userStatusInfo.setData(user);
        } catch (JDSException e) {
            userStatusInfo = new ErrorResultModel<>();
            ((ErrorResultModel<User>) userStatusInfo).setErrcode(e.getErrorCode());
            ((ErrorResultModel<User>) userStatusInfo).setErrdes(e.getMessage());
        }
        return userStatusInfo;
    }
}
