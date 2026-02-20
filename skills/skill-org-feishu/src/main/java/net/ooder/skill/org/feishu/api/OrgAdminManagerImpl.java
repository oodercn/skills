package net.ooder.skill.org.feishu.api;

import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.annotation.MethodChinaName;
import net.ooder.annotation.Operator;
import net.ooder.common.org.CtOrg;
import net.ooder.common.org.CtPerson;
import net.ooder.common.org.CtRole;
import net.ooder.common.org.service.OrgAdminService;
import net.ooder.config.ErrorResultModel;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import net.ooder.org.Org;
import net.ooder.org.Person;
import net.ooder.org.PersonNotFoundException;
import net.ooder.org.Role;
import net.ooder.org.query.OrgCondition;
import net.ooder.server.OrgManagerFactory;
import net.ooder.skill.org.feishu.org.FeishuOrgManager;
import net.ooder.web.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@EsbBeanAnnotation(id = "OrgAdminService", name = "飞书组织机构管理服务", expressionArr = "OrgAdminManagerImpl()", desc = "飞书组织机构管理服务")
public class OrgAdminManagerImpl implements OrgAdminService {

    @Autowired
    private FeishuOrgManager feishuOrgManager;

    @Override
    public ListResultModel<List<Org>> findOrgs(OrgCondition condition) {
        ListResultModel<List<Org>> resultModel = new ListResultModel<>();
        List<Org> orgs = feishuOrgManager.getTopOrgs();
        return PageUtil.getDefaultPageList(orgs);
    }

    @Override
    public ListResultModel<List<Org>> getAllTopOrgs() {
        ListResultModel<List<Org>> resultModel = new ListResultModel<>();
        List<Org> orgs = feishuOrgManager.getTopOrgs();
        return PageUtil.getDefaultPageList(orgs);
    }

    @Override
    public ListResultModel<List<Person>> findPersons(OrgCondition condition) {
        ListResultModel<List<Person>> resultModel = new ListResultModel<>();
        List<Person> persons = feishuOrgManager.getPersons();
        return PageUtil.getDefaultPageList(persons);
    }

    @Override
    public ListResultModel<List<Role>> findRoles(OrgCondition condition) {
        ListResultModel<List<Role>> resultModel = new ListResultModel<>();
        List<Role> roles = new ArrayList<>();
        return PageUtil.getDefaultPageList(roles);
    }

    @Override
    public ResultModel<Boolean> savePerson(CtPerson person) {
        ResultModel<Boolean> resultModel = new ResultModel<>();
        try {
            resultModel.setData(true);
        } catch (Exception e) {
            resultModel = new ErrorResultModel<>();
            ((ErrorResultModel<Boolean>) resultModel).setErrdes(e.getMessage());
        }
        return resultModel;
    }

    @Override
    public ResultModel<Boolean> addPerson2Org(String personId, String orgId) {
        ResultModel<Boolean> resultModel = new ResultModel<>();
        try {
            resultModel.setData(true);
        } catch (Exception e) {
            resultModel = new ErrorResultModel<>();
            ((ErrorResultModel<Boolean>) resultModel).setErrdes(e.getMessage());
        }
        return resultModel;
    }

    @Override
    public ResultModel<Boolean> saveOrg(CtOrg org) {
        ResultModel<Boolean> resultModel = new ResultModel<>();
        try {
            resultModel.setData(true);
        } catch (Exception e) {
            resultModel = new ErrorResultModel<>();
            ((ErrorResultModel<Boolean>) resultModel).setErrdes(e.getMessage());
        }
        return resultModel;
    }

    @Override
    public ResultModel<Boolean> saveRole(CtRole role) {
        ResultModel<Boolean> resultModel = new ResultModel<>();
        try {
            resultModel.setData(true);
        } catch (Exception e) {
            resultModel = new ErrorResultModel<>();
            ((ErrorResultModel<Boolean>) resultModel).setErrdes(e.getMessage());
        }
        return resultModel;
    }

    @Override
    public ResultModel<Boolean> addPerson2Role(String personId, String roleId) {
        ResultModel<Boolean> resultModel = new ResultModel<>();
        try {
            resultModel.setData(true);
        } catch (Exception e) {
            resultModel = new ErrorResultModel<>();
            ((ErrorResultModel<Boolean>) resultModel).setErrdes(e.getMessage());
        }
        return resultModel;
    }

    @Override
    public ResultModel<Boolean> removePerson2Role(String personId, String roleId) {
        ResultModel<Boolean> resultModel = new ResultModel<>();
        try {
            resultModel.setData(true);
        } catch (Exception e) {
            resultModel = new ErrorResultModel<>();
            ((ErrorResultModel<Boolean>) resultModel).setErrdes(e.getMessage());
        }
        return resultModel;
    }

    @Override
    public ResultModel<Boolean> addOrg2Role(String orgId, String roleId) {
        ResultModel<Boolean> resultModel = new ResultModel<>();
        try {
            resultModel.setData(true);
        } catch (Exception e) {
            resultModel = new ErrorResultModel<>();
            ((ErrorResultModel<Boolean>) resultModel).setErrdes(e.getMessage());
        }
        return resultModel;
    }

    @Override
    public ResultModel<Boolean> removeOrg2Role(String orgId, String roleId) {
        ResultModel<Boolean> resultModel = new ResultModel<>();
        try {
            resultModel.setData(true);
        } catch (Exception e) {
            resultModel = new ErrorResultModel<>();
            ((ErrorResultModel<Boolean>) resultModel).setErrdes(e.getMessage());
        }
        return resultModel;
    }

    @Override
    public ResultModel<Boolean> removePerson2Org(String personId, String orgId) {
        ResultModel<Boolean> resultModel = new ResultModel<>();
        try {
            resultModel.setData(true);
        } catch (Exception e) {
            resultModel = new ErrorResultModel<>();
            ((ErrorResultModel<Boolean>) resultModel).setErrdes(e.getMessage());
        }
        return resultModel;
    }

    @Override
    public ResultModel<Boolean> delOrg(String orgId) {
        ResultModel<Boolean> resultModel = new ResultModel<>();
        try {
            resultModel.setData(true);
        } catch (Exception e) {
            resultModel = new ErrorResultModel<>();
            ((ErrorResultModel<Boolean>) resultModel).setErrdes(e.getMessage());
        }
        return resultModel;
    }

    @Override
    public ResultModel<Boolean> delRole(String roleId) {
        ResultModel<Boolean> resultModel = new ResultModel<>();
        try {
            resultModel.setData(true);
        } catch (Exception e) {
            resultModel = new ErrorResultModel<>();
            ((ErrorResultModel<Boolean>) resultModel).setErrdes(e.getMessage());
        }
        return resultModel;
    }

    @Override
    public ResultModel<Boolean> delPerson(String personId) {
        ResultModel<Boolean> resultModel = new ResultModel<>();
        try {
            resultModel.setData(true);
        } catch (Exception e) {
            resultModel = new ErrorResultModel<>();
            ((ErrorResultModel<Boolean>) resultModel).setErrdes(e.getMessage());
        }
        return resultModel;
    }
}
