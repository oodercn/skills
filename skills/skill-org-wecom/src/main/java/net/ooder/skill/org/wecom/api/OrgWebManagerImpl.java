package net.ooder.skill.org.wecom.api;

import net.ooder.org.Org;
import net.ooder.org.Person;
import net.ooder.skill.org.wecom.org.WeComOrgManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/org")
public class OrgWebManagerImpl {

    @Autowired
    private WeComOrgManager orgManager;

    @GetMapping("/tree")
    public List<Org> getOrgTree() {
        return orgManager.getTopOrgs();
    }

    @GetMapping("/department/{id}")
    public Org getDepartment(@PathVariable String id) {
        try {
            return orgManager.getOrgByID(id);
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping("/person/{id}")
    public Person getPerson(@PathVariable String id) {
        try {
            return orgManager.getPersonByID(id);
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping("/persons")
    public List<Person> getPersons() {
        return orgManager.getPersons();
    }

    @GetMapping("/persons/byOrg/{orgId}")
    public List<Person> getPersonsByOrg(@PathVariable String orgId) {
        return orgManager.getPersonsByOrgID(orgId);
    }

    @PostMapping("/reload")
    public String reload() {
        orgManager.reloadAll();
        return "OK";
    }
}
