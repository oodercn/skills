package net.ooder.skill.org.dingding.org;

import net.ooder.common.ConfigCode;
import net.ooder.common.JDSException;
import net.ooder.msg.PersonPrivateGroup;
import net.ooder.msg.PersonPrivateGroupNotFoundException;
import net.ooder.annotation.RoleType;
import net.ooder.org.*;
import net.ooder.skill.org.dingding.client.DingdingApiClient;
import net.ooder.skill.org.dingding.model.DingdingDepartment;
import net.ooder.skill.org.dingding.model.DingdingUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DingdingOrgManager implements OrgManager {

    private static final Logger logger = LoggerFactory.getLogger(DingdingOrgManager.class);

    @Autowired
    private DingdingApiClient dingdingClient;

    private Map<String, Org> orgCache = new HashMap<>();
    private Map<String, Person> personCache = new HashMap<>();
    private ConfigCode configCode;

    @Override
    public void init(ConfigCode configCode) {
        this.configCode = configCode;
    }

    @Override
    public List<Org> getTopOrgs() {
        return getTopOrgs(null);
    }

    @Override
    public List<Org> getTopOrgs(String sysId) {
        try {
            List<DingdingDepartment> depts = dingdingClient.getDepartments(null);
            List<Org> orgs = new ArrayList<>();
            for (DingdingDepartment dept : depts) {
                orgs.add(convertToOrg(dept));
            }
            return orgs;
        } catch (Exception e) {
            logger.error("Failed to get top orgs", e);
            return new ArrayList<>();
        }
    }

    @Override
    public Org getOrgByID(String orgId) throws OrgNotFoundException {
        if (orgCache.containsKey(orgId)) {
            return orgCache.get(orgId);
        }
        try {
            DingdingDepartment dept = dingdingClient.getDepartment(orgId);
            if (dept == null) {
                throw new OrgNotFoundException("Organization not found: " + orgId);
            }
            Org org = convertToOrg(dept);
            orgCache.put(orgId, org);
            return org;
        } catch (Exception e) {
            throw new OrgNotFoundException("Failed to get organization: " + orgId, e);
        }
    }

    @Override
    public Person getPersonByID(String personId) throws PersonNotFoundException {
        if (personCache.containsKey(personId)) {
            return personCache.get(personId);
        }
        try {
            DingdingUser user = dingdingClient.getUser(personId);
            if (user == null) {
                throw new PersonNotFoundException("Person not found: " + personId);
            }
            Person person = convertToPerson(user);
            personCache.put(personId, person);
            return person;
        } catch (Exception e) {
            throw new PersonNotFoundException("Failed to get person: " + personId, e);
        }
    }

    @Override
    public Person getPersonByAccount(String account) throws PersonNotFoundException {
        try {
            DingdingUser user = dingdingClient.getUserByAccount(account);
            if (user == null) {
                throw new PersonNotFoundException("Person not found: " + account);
            }
            return convertToPerson(user);
        } catch (Exception e) {
            throw new PersonNotFoundException("Failed to get person by account: " + account, e);
        }
    }

    @Override
    public Person getPersonByMobile(String mobile) throws PersonNotFoundException {
        try {
            DingdingUser user = dingdingClient.getUserByMobile(mobile);
            if (user == null) {
                throw new PersonNotFoundException("Person not found by mobile: " + mobile);
            }
            return convertToPerson(user);
        } catch (Exception e) {
            throw new PersonNotFoundException("Failed to get person by mobile: " + mobile, e);
        }
    }

    @Override
    public Person getPersonByEmail(String email) throws PersonNotFoundException {
        try {
            DingdingUser user = dingdingClient.getUserByEmail(email);
            if (user == null) {
                throw new PersonNotFoundException("Person not found by email: " + email);
            }
            return convertToPerson(user);
        } catch (Exception e) {
            throw new PersonNotFoundException("Failed to get person by email: " + email, e);
        }
    }

    @Override
    public List<Person> getPersons() {
        try {
            List<DingdingUser> users = dingdingClient.getAllUsers();
            List<Person> persons = new ArrayList<>();
            for (DingdingUser user : users) {
                persons.add(convertToPerson(user));
            }
            return persons;
        } catch (Exception e) {
            logger.error("Failed to get all persons", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Person> getPersonsByOrgID(String orgId) {
        try {
            List<DingdingUser> users = dingdingClient.getUsersByDepartment(orgId);
            List<Person> persons = new ArrayList<>();
            for (DingdingUser user : users) {
                persons.add(convertToPerson(user));
            }
            return persons;
        } catch (Exception e) {
            logger.error("Failed to get persons by org id: {}", orgId, e);
            return new ArrayList<>();
        }
    }

    @Override
    public boolean verifyPerson(String account, String password) {
        try {
            return dingdingClient.verifyUser(account, password);
        } catch (Exception e) {
            logger.error("Failed to verify person: {}", account, e);
            return false;
        }
    }

    @Override
    public void reloadAll() {
        orgCache.clear();
        personCache.clear();
        logger.info("Dingding org cache cleared");
    }

    @Override
    public Person registerPerson(String account, String name, String password) {
        try {
            DingdingUser user = dingdingClient.getUserByAccount(account);
            if (user != null) {
                return convertToPerson(user);
            }
            return null;
        } catch (Exception e) {
            logger.error("Failed to register person: {}", account, e);
            return null;
        }
    }

    @Override
    public List<Role> getOrgLevels() {
        return new ArrayList<>();
    }

    @Override
    public List<Role> getOrgLevels(String sysId) {
        return new ArrayList<>();
    }

    @Override
    public List<Role> getOrgLevelsByNum(String levelNum) {
        return new ArrayList<>();
    }

    @Override
    public List<Role> getOrgLevelsByNum(String levelNum, String sysId) {
        return new ArrayList<>();
    }

    @Override
    public Role getOrgRoleByID(String orgRoleId) {
        return null;
    }

    @Override
    public Role getOrgRoleByName(String orgRoleName) {
        return null;
    }

    @Override
    public Role getOrgRoleByName(String orgRoleName, String sysId) {
        return null;
    }

    @Override
    public List<Org> getOrgsByOrgRoleID(String roleId) {
        return new ArrayList<>();
    }

    @Override
    public List<Org> getOrgsByOrgRoleName(String roleName) {
        return new ArrayList<>();
    }

    @Override
    public List<Org> getOrgsByOrgRoleName(String roleName, String sysId) {
        return new ArrayList<>();
    }

    @Override
    public boolean isSupportOrgLevel() {
        return false;
    }

    @Override
    public boolean isSupportRole() throws JDSException {
        return false;
    }

    @Override
    public boolean isSupportPersonDuty() throws JDSException {
        return false;
    }

    @Override
    public boolean isSupportPersonGroup() throws JDSException {
        return false;
    }

    @Override
    public boolean isSupportPersonLevel() throws JDSException {
        return false;
    }

    @Override
    public boolean isSupportPersonPosition() throws JDSException {
        return false;
    }

    @Override
    public boolean isSupportPersonRole() throws JDSException {
        return false;
    }

    @Override
    public boolean isSupportOrgRole() throws JDSException {
        return false;
    }

    @Override
    public PersonPrivateGroup getPrivateGroupById(String personGroupId) throws PersonPrivateGroupNotFoundException, JDSException {
        throw new PersonPrivateGroupNotFoundException("Private group not supported in Dingding");
    }

    @Override
    public Role getOrgLevelByID(String orgLevelId) throws RoleNotFoundException {
        throw new RoleNotFoundException("Org level not supported in Dingding");
    }

    @Override
    public Role getOrgLevelByName(String orgLevelName) throws RoleNotFoundException {
        throw new RoleNotFoundException("Org level not supported in Dingding");
    }

    @Override
    public Role getOrgLevelByName(String orgLevelName, String sysId) throws RoleNotFoundException {
        throw new RoleNotFoundException("Org level not supported in Dingding");
    }

    @Override
    public Role getRoleByID(String roleId) throws RoleNotFoundException {
        throw new RoleNotFoundException("Role not supported in Dingding");
    }

    @Override
    public Role getRoleByName(RoleType type, String roleName) throws RoleNotFoundException {
        throw new RoleNotFoundException("Role not supported in Dingding");
    }

    @Override
    public Role getRoleByName(RoleType type, String roleName, String sysId) throws RoleNotFoundException {
        throw new RoleNotFoundException("Role not supported in Dingding");
    }

    @Override
    public List<Role> getOrgRoles() {
        return new ArrayList<>();
    }

    @Override
    public List<Role> getOrgRoles(String sysId) {
        return new ArrayList<>();
    }

    @Override
    public List<Org> getOrgs() {
        return new ArrayList<>();
    }

    @Override
    public List<Org> getOrgs(String sysId) {
        return new ArrayList<>();
    }

    @Override
    public List<String> getOrgIds() {
        return new ArrayList<>();
    }

    @Override
    public List<String> getOrgIds(String sysId) {
        return new ArrayList<>();
    }

    @Override
    public List<Org> getOrgsByOrgLevelID(String levelId) {
        return new ArrayList<>();
    }

    @Override
    public List<Org> getOrgsByOrgLevelName(String levelName) {
        return new ArrayList<>();
    }

    @Override
    public List<Org> getOrgsByOrgLevelName(String levelName, String sysId) {
        return new ArrayList<>();
    }

    @Override
    public List<Org> getOrgsByRoleID(String roleId) {
        return new ArrayList<>();
    }

    @Override
    public List<Org> getOrgsByRoleName(String roleName) {
        return new ArrayList<>();
    }

    @Override
    public List<Org> getOrgsByRoleName(String roleName, String sysId) {
        return new ArrayList<>();
    }

    @Override
    public List<Role> getPersonDuties() {
        return new ArrayList<>();
    }

    @Override
    public List<Role> getPersonDuties(String sysId) {
        return new ArrayList<>();
    }

    @Override
    public List<Role> getPersonDutiesByNum(String personDutyNum) {
        return new ArrayList<>();
    }

    @Override
    public List<Role> getPersonDutiesByNum(String personDutyNum, String sysId) {
        return new ArrayList<>();
    }

    @Override
    public Role getPersonDutyByID(String personDutyId) throws RoleNotFoundException {
        throw new RoleNotFoundException("Person duty not supported in Dingding");
    }

    @Override
    public Role getPersonDutyByName(String personDutyName) throws RoleNotFoundException {
        throw new RoleNotFoundException("Person duty not supported in Dingding");
    }

    @Override
    public Role getPersonDutyByName(String personDutyName, String sysId) throws RoleNotFoundException {
        throw new RoleNotFoundException("Person duty not supported in Dingding");
    }

    @Override
    public Role getPersonGroupByID(String personGroupId) throws RoleNotFoundException {
        throw new RoleNotFoundException("Person group not supported in Dingding");
    }

    @Override
    public Role getPersonGroupByName(String personGroupName) throws RoleNotFoundException {
        throw new RoleNotFoundException("Person group not supported in Dingding");
    }

    @Override
    public Role getPersonGroupByName(String personGroupName, String sysId) throws RoleNotFoundException {
        throw new RoleNotFoundException("Person group not supported in Dingding");
    }

    @Override
    public List<Role> getPersonGroups() {
        return new ArrayList<>();
    }

    @Override
    public List<Role> getPersonGroups(String sysId) {
        return new ArrayList<>();
    }

    @Override
    public Role getPersonLevelByID(String personLevelId) throws RoleNotFoundException {
        throw new RoleNotFoundException("Person level not supported in Dingding");
    }

    @Override
    public Role getPersonLevelByName(String personLevelName) throws RoleNotFoundException {
        throw new RoleNotFoundException("Person level not supported in Dingding");
    }

    @Override
    public Role getPersonLevelByName(String personLevelName, String sysId) throws RoleNotFoundException {
        throw new RoleNotFoundException("Person level not supported in Dingding");
    }

    @Override
    public List<Role> getPersonLevels() {
        return new ArrayList<>();
    }

    @Override
    public List<Role> getPersonLevels(String sysId) {
        return new ArrayList<>();
    }

    @Override
    public List<Role> getPersonLevelsByNum(String personLevelNum) throws RoleNotFoundException {
        return new ArrayList<>();
    }

    @Override
    public List<Role> getPersonLevelsByNum(String personLevelNum, String sysId) throws RoleNotFoundException {
        return new ArrayList<>();
    }

    @Override
    public Role getPersonPositionByID(String personPositionId) throws RoleNotFoundException {
        throw new RoleNotFoundException("Person position not supported in Dingding");
    }

    @Override
    public Role getPersonPositionByName(String personPositionName) throws RoleNotFoundException {
        throw new RoleNotFoundException("Person position not supported in Dingding");
    }

    @Override
    public Role getPersonPositionByName(String personPositionName, String sysId) throws RoleNotFoundException {
        throw new RoleNotFoundException("Person position not supported in Dingding");
    }

    @Override
    public List<Role> getPersonPositions() {
        return new ArrayList<>();
    }

    @Override
    public List<Role> getPersonPositions(String sysId) {
        return new ArrayList<>();
    }

    @Override
    public Role getPersonRoleByID(String personRoleId) throws RoleNotFoundException {
        throw new RoleNotFoundException("Person role not supported in Dingding");
    }

    @Override
    public Role getPersonRoleByName(String personRoleName) throws RoleNotFoundException {
        throw new RoleNotFoundException("Person role not supported in Dingding");
    }

    @Override
    public Role getPersonRoleByName(String personRoleName, String sysId) throws RoleNotFoundException {
        throw new RoleNotFoundException("Person role not supported in Dingding");
    }

    @Override
    public List<Role> getPersonRoles() {
        return new ArrayList<>();
    }

    @Override
    public List<Role> getPersonRoles(String sysId) {
        return new ArrayList<>();
    }

    @Override
    public List<Role> getAllRoles() {
        return new ArrayList<>();
    }

    @Override
    public List<Role> getAllRoles(String sysId) {
        return new ArrayList<>();
    }

    @Override
    public List<Person> getPersons(String sysId) {
        return getPersons();
    }

    @Override
    public List<Person> getPersonsByPersonDutyID(String personDutyId) {
        return new ArrayList<>();
    }

    @Override
    public List<Person> getPersonsByPersonDutyName(String personDutyName) {
        return new ArrayList<>();
    }

    @Override
    public List<Person> getPersonsByPersonDutyName(String personDutyName, String sysId) {
        return new ArrayList<>();
    }

    @Override
    public List<Person> getPersonsByPersonGroupID(String personGroupId) {
        return new ArrayList<>();
    }

    @Override
    public List<Person> getPersonsByPersonGroupName(String personGroupName) {
        return new ArrayList<>();
    }

    @Override
    public List<Person> getPersonsByPersonGroupName(String personGroupName, String sysId) {
        return new ArrayList<>();
    }

    @Override
    public List<Person> getPersonsByPersonLevelID(String personLevelId) {
        return new ArrayList<>();
    }

    @Override
    public List<Person> getPersonsByPersonLevelName(String personLevelName) {
        return new ArrayList<>();
    }

    @Override
    public List<Person> getPersonsByPersonLevelName(String personLevelName, String sysId) {
        return new ArrayList<>();
    }

    @Override
    public List<Person> getPersonsByPersonPositionID(String personPositionId) {
        return new ArrayList<>();
    }

    @Override
    public List<Person> getPersonsByPersonPositionName(String personPositionName) {
        return new ArrayList<>();
    }

    @Override
    public List<Person> getPersonsByPersonPositionName(String personPositionName, String sysId) {
        return new ArrayList<>();
    }

    @Override
    public List<Person> getPersonsByPersonRoleID(String personRoleId) {
        return new ArrayList<>();
    }

    @Override
    public List<Person> getPersonsByPersonRoleName(String personRoleName) {
        return new ArrayList<>();
    }

    @Override
    public List<Person> getPersonsByPersonRoleName(String personRoleName, String sysId) {
        return new ArrayList<>();
    }

    private Org convertToOrg(DingdingDepartment dept) {
        DingdingOrg org = new DingdingOrg();
        org.setOrgId(dept.getDeptId());
        org.setName(dept.getName());
        org.setParentId(dept.getParentId());
        return org;
    }

    private Person convertToPerson(DingdingUser user) {
        DingdingPerson person = new DingdingPerson();
        person.setID(user.getUserid());
        person.setAccount(user.getUserid());
        person.setName(user.getName());
        person.setNickName(user.getName());
        person.setMobile(user.getMobile());
        person.setEmail(user.getEmail());
        person.setOrgId(user.getDeptId());
        return person;
    }
}
