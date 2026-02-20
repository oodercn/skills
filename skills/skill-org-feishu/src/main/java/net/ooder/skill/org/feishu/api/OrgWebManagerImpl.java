package net.ooder.skill.org.feishu.api;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.common.org.service.OrgWebManager;
import net.ooder.config.ErrorResultModel;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import net.ooder.org.*;
import net.ooder.server.OrgManagerFactory;
import net.ooder.skill.org.feishu.org.FeishuOrgManager;
import net.ooder.web.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@EsbBeanAnnotation(id = "OrgWebManager", name = "飞书组织机构Web服务", expressionArr = "OrgWebManagerImpl()", version = 1, desc = "飞书组织机构Web服务")
public class OrgWebManagerImpl implements OrgWebManager {

    @Autowired
    private FeishuOrgManager feishuOrgManager;

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "/verifyPerson")
    public @ResponseBody ResultModel<Boolean> verifyPerson(String account, String password) {
        ResultModel<Boolean> result = new ResultModel<>();
        try {
            Boolean isverify = feishuOrgManager.verifyPerson(account, password);
            result.setData(isverify);
        } catch (Exception e) {
            result = new ErrorResultModel<>();
            ((ErrorResultModel<Boolean>) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "/getTopOrgs")
    public @ResponseBody ResultModel<List<Org>> getTopOrgs(String sysId) {
        ResultModel<List<Org>> result = new ResultModel<>();
        List<Org> orgList = feishuOrgManager.getTopOrgs(sysId);
        result = PageUtil.getDefaultPageList(orgList);
        return result;
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "/getOrgByID")
    public @ResponseBody ResultModel<Org> getOrgByID(String orgID) {
        ResultModel<Org> result = new ResultModel<>();
        try {
            Org org = feishuOrgManager.getOrgByID(orgID);
            result.setData(org);
        } catch (OrgNotFoundException e) {
            result = new ErrorResultModel<>();
            ((ErrorResultModel<Org>) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "/getPersonByAccount")
    public @ResponseBody ResultModel<Person> getPersonByAccount(String personAccount) {
        ResultModel<Person> result = new ResultModel<>();
        try {
            Person person = feishuOrgManager.getPersonByAccount(personAccount);
            result.setData(person);
        } catch (PersonNotFoundException e) {
            result = new ErrorResultModel<>();
            ((ErrorResultModel<Person>) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "/getPersonByID")
    public @ResponseBody ResultModel<Person> getPersonByID(String personId) {
        ResultModel<Person> result = new ResultModel<>();
        try {
            Person person = feishuOrgManager.getPersonByID(personId);
            result.setData(person);
        } catch (PersonNotFoundException e) {
            result = new ErrorResultModel<>();
            ((ErrorResultModel<Person>) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "/getPersonByMobile")
    public @ResponseBody ResultModel<Person> getPersonByMobile(String mobilenum) {
        ResultModel<Person> result = new ResultModel<>();
        try {
            Person person = feishuOrgManager.getPersonByMobile(mobilenum);
            result.setData(person);
        } catch (PersonNotFoundException e) {
            result = new ErrorResultModel<>();
            ((ErrorResultModel<Person>) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "/getPersonByEmail")
    public @ResponseBody ResultModel<Person> getPersonByEmail(String email) {
        ResultModel<Person> result = new ResultModel<>();
        try {
            Person person = feishuOrgManager.getPersonByEmail(email);
            result.setData(person);
        } catch (PersonNotFoundException e) {
            result = new ErrorResultModel<>();
            ((ErrorResultModel<Person>) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "/getPersons")
    public @ResponseBody ListResultModel<List<Person>> getPersons(String sysId) {
        ListResultModel<List<Person>> result = new ListResultModel<>();
        List<Person> personList = feishuOrgManager.getPersons();
        result = PageUtil.getDefaultPageList(personList);
        return result;
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "/getPersonsByOrgID")
    public @ResponseBody ListResultModel<List<Person>> getPersonsByOrgID(String orgId) {
        ListResultModel<List<Person>> result = new ListResultModel<>();
        List<Person> persons = feishuOrgManager.getPersonsByOrgID(orgId);
        result = PageUtil.getDefaultPageList(persons);
        return result;
    }

    @Override
    @RequestMapping(method = RequestMethod.POST, value = "/reLoadAll")
    public @ResponseBody ResultModel<Boolean> reLoadAll() {
        feishuOrgManager.reloadAll();
        return new ResultModel<>();
    }

    @Override
    public ListResultModel<List<Role>> getOrgRoles(String sysId) {
        return PageUtil.getDefaultPageList(new ArrayList<>());
    }

    @Override
    public ListResultModel<List<Org>> getOrgs(String sysId) {
        return PageUtil.getDefaultPageList(feishuOrgManager.getTopOrgs(sysId));
    }

    @Override
    public ListResultModel<List<String>> getOrgIds(String sysId) {
        ListResultModel<List<String>> result = new ListResultModel<>();
        List<String> orgIds = new ArrayList<>();
        for (Org org : feishuOrgManager.getTopOrgs(sysId)) {
            orgIds.add(org.getOrgId());
        }
        result.setData(orgIds);
        result.setSize(orgIds.size());
        return result;
    }

    @Override
    public ResultModel<Role> getOrgLevelByID(String orgLevelId) {
        return new ResultModel<>();
    }

    @Override
    public ResultModel<Role> getOrgLevelByName(String orgLevelName, String sysId) {
        return new ResultModel<>();
    }

    @Override
    public ListResultModel<List<Org>> getOrgsByOrgLevelID(String levelId) {
        return PageUtil.getDefaultPageList(new ArrayList<>());
    }

    @Override
    public ListResultModel<List<Org>> getOrgsByOrgLevelName(String levelName, String sysId) {
        return PageUtil.getDefaultPageList(new ArrayList<>());
    }

    @Override
    public ListResultModel<List<Org>> getOrgsByRoleID(String roleId) {
        return PageUtil.getDefaultPageList(new ArrayList<>());
    }

    @Override
    public ListResultModel<List<Org>> getOrgsByRoleName(String roleName, String sysId) {
        return PageUtil.getDefaultPageList(new ArrayList<>());
    }

    @Override
    public ListResultModel<List<Role>> getPersonDuties(String sysId) {
        return PageUtil.getDefaultPageList(new ArrayList<>());
    }

    @Override
    public ListResultModel<List<Role>> getPersonDutiesByNum(String personDutyNum, String sysId) {
        return PageUtil.getDefaultPageList(new ArrayList<>());
    }

    @Override
    public ResultModel<Role> getPersonDutyByID(String personDutyId) {
        return new ResultModel<>();
    }

    @Override
    public ResultModel<Role> getPersonDutyByName(String personDutyName, String sysId) {
        return new ResultModel<>();
    }

    @Override
    public ResultModel<Role> getPersonGroupByID(String personGroupId) {
        return new ResultModel<>();
    }

    @Override
    public ResultModel<Role> getPersonGroupByName(String personGroupName, String sysId) {
        return new ResultModel<>();
    }

    @Override
    public ListResultModel<List<Role>> getPersonGroups(String sysId) {
        return PageUtil.getDefaultPageList(new ArrayList<>());
    }

    @Override
    public ResultModel<Role> getPersonLevelByID(String personLevelId) {
        return new ResultModel<>();
    }

    @Override
    public ResultModel<Role> getPersonLevelByName(String personLevelName, String sysId) {
        return new ResultModel<>();
    }

    @Override
    public ListResultModel<List<Role>> getPersonLevels(String sysId) {
        return PageUtil.getDefaultPageList(new ArrayList<>());
    }

    @Override
    public ListResultModel<List<Role>> getPersonLevelsByNum(String personLevelNum, String sysId) {
        return PageUtil.getDefaultPageList(new ArrayList<>());
    }

    @Override
    public ResultModel<Role> getPersonPositionByID(String personPositionId) {
        return new ResultModel<>();
    }

    @Override
    public ResultModel<Role> getPersonPositionByName(String personPositionName, String sysId) {
        return new ResultModel<>();
    }

    @Override
    public ListResultModel<List<Role>> getPersonPositions(String sysId) {
        return PageUtil.getDefaultPageList(new ArrayList<>());
    }

    @Override
    public ResultModel<Role> getPersonRoleByID(String personRoleId) {
        return new ResultModel<>();
    }

    @Override
    public ResultModel<Role> getPersonRoleByName(String personRoleName, String sysId) {
        return new ResultModel<>();
    }

    @Override
    public ListResultModel<List<Role>> getPersonRoles(String sysId) {
        return PageUtil.getDefaultPageList(new ArrayList<>());
    }

    @Override
    public ListResultModel<List<Person>> getPersonsByPersonDutyID(String personDutyId) {
        return PageUtil.getDefaultPageList(new ArrayList<>());
    }

    @Override
    public ListResultModel<List<Person>> getPersonsByPersonDutyName(String personDutyName, String sysId) {
        return PageUtil.getDefaultPageList(new ArrayList<>());
    }

    @Override
    public ListResultModel<List<Person>> getPersonsByPersonGroupID(String personGroupId) {
        return PageUtil.getDefaultPageList(new ArrayList<>());
    }

    @Override
    public ListResultModel<List<Person>> getPersonsByPersonGroupName(String personGroupName, String sysId) {
        return PageUtil.getDefaultPageList(new ArrayList<>());
    }

    @Override
    public ListResultModel<List<Person>> getPersonsByPersonLevelID(String personLevelId) {
        return PageUtil.getDefaultPageList(new ArrayList<>());
    }

    @Override
    public ListResultModel<List<Person>> getPersonsByPersonLevelName(String personLevelName, String sysId) {
        return PageUtil.getDefaultPageList(new ArrayList<>());
    }

    @Override
    public ListResultModel<List<Person>> getPersonsByPersonPositionID(String personPositionId) {
        return PageUtil.getDefaultPageList(new ArrayList<>());
    }

    @Override
    public ListResultModel<List<Person>> getPersonsByPersonPositionName(String personPositionName, String sysId) {
        return PageUtil.getDefaultPageList(new ArrayList<>());
    }

    @Override
    public ListResultModel<List<Person>> getPersonsByPersonRoleID(String personRoleId) {
        return PageUtil.getDefaultPageList(new ArrayList<>());
    }

    @Override
    public ListResultModel<List<Person>> getPersonsByPersonRoleName(String personRoleName, String sysId) {
        return PageUtil.getDefaultPageList(new ArrayList<>());
    }

    @Override
    public ResultModel<Boolean> isSupportOrgLevel(String sysId) {
        ResultModel<Boolean> result = new ResultModel<>();
        result.setData(false);
        return result;
    }

    @Override
    public ResultModel<Boolean> isSupportRole(String sysId) {
        ResultModel<Boolean> result = new ResultModel<>();
        result.setData(false);
        return result;
    }

    @Override
    public ResultModel<Boolean> isSupportPersonDuty(String sysId) {
        ResultModel<Boolean> result = new ResultModel<>();
        result.setData(false);
        return result;
    }

    @Override
    public ResultModel<Boolean> isSupportPersonGroup(String sysId) {
        ResultModel<Boolean> result = new ResultModel<>();
        result.setData(false);
        return result;
    }

    @Override
    public ResultModel<Boolean> isSupportPersonLevel(String sysId) {
        ResultModel<Boolean> result = new ResultModel<>();
        result.setData(false);
        return result;
    }

    @Override
    public ResultModel<Boolean> isSupportPersonPosition(String sysId) {
        ResultModel<Boolean> result = new ResultModel<>();
        result.setData(false);
        return result;
    }

    @Override
    public ResultModel<Boolean> isSupportPersonRole(String sysId) {
        ResultModel<Boolean> result = new ResultModel<>();
        result.setData(false);
        return result;
    }

    @Override
    public ResultModel<Boolean> isSupportOrgRole(String sysId) {
        ResultModel<Boolean> result = new ResultModel<>();
        result.setData(false);
        return result;
    }

    @Override
    public ListResultModel<List<Org>> getOrgsByOrgRoleName(String roleName, String sysId) {
        return PageUtil.getDefaultPageList(new ArrayList<>());
    }

    @Override
    public ListResultModel<List<Org>> getOrgsByOrgRoleID(String roleId) {
        return PageUtil.getDefaultPageList(new ArrayList<>());
    }

    @Override
    public ResultModel<Role> getOrgRoleByName(String orgRoleName, String sysId) {
        return new ResultModel<>();
    }

    @Override
    public ResultModel<Role> getOrgRoleByID(String orgRoleId) {
        return new ResultModel<>();
    }

    @Override
    public ListResultModel<List<Role>> getOrgLevels(String sysId) {
        return PageUtil.getDefaultPageList(new ArrayList<>());
    }

    @Override
    public ListResultModel<List<Role>> getOrgLevelsByNum(String orgLevelNum, String sysId) {
        return PageUtil.getDefaultPageList(new ArrayList<>());
    }

    @Override
    public ResultModel<Role> getRoleByID(String roleId) {
        return new ResultModel<>();
    }

    @Override
    public ResultModel<Role> getRoleByName(net.ooder.annotation.RoleType type, String roleName, String sysId) {
        return new ResultModel<>();
    }

    @Override
    public ResultModel<Person> registerPerson(String accountName, String enName, String systemCode) {
        ResultModel<Person> result = new ResultModel<>();
        try {
            Person account = feishuOrgManager.getPersonByAccount(accountName);
            result.setData(account);
        } catch (PersonNotFoundException e) {
            result = new ErrorResultModel<>();
            ((ErrorResultModel<Person>) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ListResultModel<List<Role>> getAllRoles(String sysId) {
        return PageUtil.getDefaultPageList(new ArrayList<>());
    }

    @Override
    public ResultModel<net.ooder.msg.PersonPrivateGroup> getPrivateGroupById(String personGroupId) {
        return new ResultModel<>();
    }
}
