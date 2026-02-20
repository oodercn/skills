package net.ooder.skill.org.dingding.api;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.common.JDSException;
import net.ooder.common.org.service.UserInnerService;
import net.ooder.engine.ConnectInfo;
import net.ooder.jds.core.User;
import net.ooder.org.Person;
import net.ooder.org.PersonNotFoundException;
import net.ooder.skill.org.dingding.org.DingdingOrgManager;
import org.springframework.beans.factory.annotation.Autowired;

@EsbBeanAnnotation(id = "UserInnerService", name = "钉钉账号内部服务", expressionArr = "UserInnerServiceImpl()", desc = "钉钉账号内部服务")
public class UserInnerServiceImpl implements UserInnerService {

    @Autowired
    private DingdingOrgManager dingdingOrgManager;

    @Override
    public void logout() throws JDSException {
    }

    @Override
    public ConnectInfo login(String userName, String password, String sessionid) throws JDSException {
        try {
            Person person = dingdingOrgManager.getPersonByAccount(userName.toLowerCase());
            boolean login = dingdingOrgManager.verifyPerson(userName.toLowerCase(), password);
            if (login) {
                return new ConnectInfo(person.getID(), person.getAccount(), person.getPassword());
            } else {
                throw new JDSException("Invalid credentials", JDSException.NOTLOGINEDERROR);
            }
        } catch (PersonNotFoundException e) {
            throw new JDSException("User not found: " + userName, JDSException.NOTLOGINEDERROR);
        }
    }

    @Override
    public User saveUser(User user) throws JDSException {
        return user;
    }

    @Override
    public User getUserByAccount(String account) throws JDSException {
        try {
            Person person = dingdingOrgManager.getPersonByAccount(account);
            User user = new User();
            user.setId(person.getID());
            user.setAccount(person.getAccount());
            user.setName(person.getName());
            user.setEmail(person.getEmail());
            user.setPhone(person.getMobile());
            return user;
        } catch (PersonNotFoundException e) {
            throw new JDSException("User not found: " + account, JDSException.NOTLOGINEDERROR);
        }
    }

    @Override
    public User getUserById(String Id) throws JDSException {
        try {
            Person person = dingdingOrgManager.getPersonByID(Id);
            User user = new User();
            user.setId(person.getID());
            user.setAccount(person.getAccount());
            user.setName(person.getName());
            user.setEmail(person.getEmail());
            user.setPhone(person.getMobile());
            return user;
        } catch (PersonNotFoundException e) {
            throw new JDSException("User not found: " + Id, JDSException.NOTLOGINEDERROR);
        }
    }

    @Override
    public User register(User user) throws JDSException {
        return user;
    }

    @Override
    public void modifyPwd(User user, String code) throws JDSException {
    }

    @Override
    public String sendCode(User userData) throws JDSException {
        return sendCode(userData.getPhone());
    }

    @Override
    public String sendCode(String phonenum) throws JDSException {
        return createRandom(true, 4);
    }

    public static String createRandom(boolean numberFlag, int length) {
        String retStr = "";
        String strTable = numberFlag ? "1234567890" : "1234567890abcdefghijkmnpqrstuvwxyz";
        int len = strTable.length();
        boolean bDone = true;
        do {
            retStr = "";
            int count = 0;
            for (int i = 0; i < length; i++) {
                double dblR = Math.random() * len;
                int intR = (int) Math.floor(dblR);
                char c = strTable.charAt(intR);
                if (('0' <= c) && (c <= '9')) {
                    count++;
                }
                retStr += strTable.charAt(intR);
            }
            if (count >= 2) {
                bDone = false;
            }
        } while (bDone);
        return retStr;
    }
}
