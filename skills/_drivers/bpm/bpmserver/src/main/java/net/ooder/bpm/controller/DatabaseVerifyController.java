package net.ooder.bpm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/db")
public class DatabaseVerifyController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/verify")
    public Map<String, Object> verifyDatabase() {
        Map<String, Object> result = new LinkedHashMap<>();
        
        result.put("processDefs", jdbcTemplate.queryForList("SELECT * FROM BPM_PROCESSDEF"));
        result.put("processDefVersions", jdbcTemplate.queryForList("SELECT * FROM BPM_PROCESSDEFVERSION"));
        result.put("activityDefs", jdbcTemplate.queryForList("SELECT * FROM BPM_ACTIVITYDEF"));
        result.put("routeDefs", jdbcTemplate.queryForList("SELECT * FROM BPM_ROUTEDEF"));
        result.put("users", jdbcTemplate.queryForList("SELECT * FROM BPM_USER"));
        result.put("processDefParticipants", jdbcTemplate.queryForList("SELECT * FROM BPM_PROCESSDEF_PARTICIPANT"));
        
        result.put("counts", Map.of(
            "processDefCount", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM BPM_PROCESSDEF", Integer.class),
            "versionCount", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM BPM_PROCESSDEFVERSION", Integer.class),
            "activityCount", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM BPM_ACTIVITYDEF", Integer.class),
            "routeCount", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM BPM_ROUTEDEF", Integer.class),
            "userCount", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM BPM_USER", Integer.class),
            "participantCount", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM BPM_PROCESSDEF_PARTICIPANT", Integer.class)
        ));
        
        return result;
    }

    @GetMapping("/raw/processdef")
    public List<Map<String, Object>> getRawProcessDef() {
        return jdbcTemplate.queryForList("SELECT * FROM BPM_PROCESSDEF");
    }

    @GetMapping("/raw/activitydef")
    public List<Map<String, Object>> getRawActivityDef() {
        return jdbcTemplate.queryForList("SELECT * FROM BPM_ACTIVITYDEF");
    }

    @GetMapping("/raw/routedef")
    public List<Map<String, Object>> getRawRouteDef() {
        return jdbcTemplate.queryForList("SELECT * FROM BPM_ROUTEDEF");
    }

    @GetMapping("/raw/users")
    public List<Map<String, Object>> getUsers() {
        return jdbcTemplate.queryForList("SELECT * FROM BPM_USER");
    }

    @GetMapping("/raw/participants")
    public List<Map<String, Object>> getProcessDefParticipants() {
        return jdbcTemplate.queryForList("SELECT * FROM BPM_PROCESSDEF_PARTICIPANT");
    }

    @GetMapping("/check/route-columns")
    public Map<String, Object> checkRouteColumns() {
        Map<String, Object> result = new LinkedHashMap<>();
        
        String sql = "PRAGMA table_info(BPM_ROUTEDEF)";
        List<Map<String, Object>> columns = jdbcTemplate.queryForList(sql);
        result.put("routeDefColumns", columns);
        
        List<Map<String, Object>> sampleData = jdbcTemplate.queryForList("SELECT * FROM BPM_ROUTEDEF LIMIT 1");
        result.put("sampleRouteDef", sampleData);
        
        return result;
    }

    @GetMapping("/check/history-columns")
    public Map<String, Object> checkHistoryColumns() {
        Map<String, Object> result = new LinkedHashMap<>();
        
        String sql = "PRAGMA table_info(BPM_ACTIVITYINSTHISTORY)";
        List<Map<String, Object>> columns = jdbcTemplate.queryForList(sql);
        result.put("historyColumns", columns);
        
        return result;
    }

    @GetMapping("/check/processinst-columns")
    public Map<String, Object> checkProcessInstColumns() {
        Map<String, Object> result = new LinkedHashMap<>();
        
        String sql = "PRAGMA table_info(BPM_PROCESSINST)";
        List<Map<String, Object>> columns = jdbcTemplate.queryForList(sql);
        result.put("processInstColumns", columns);
        
        return result;
    }

    @GetMapping("/check/activityinst-columns")
    public Map<String, Object> checkActivityInstColumns() {
        Map<String, Object> result = new LinkedHashMap<>();
        
        String sql = "PRAGMA table_info(BPM_ACTIVITYINST)";
        List<Map<String, Object>> columns = jdbcTemplate.queryForList(sql);
        result.put("activityInstColumns", columns);
        
        return result;
    }

    @GetMapping("/check/user-columns")
    public Map<String, Object> checkUserColumns() {
        Map<String, Object> result = new LinkedHashMap<>();
        
        String sql = "PRAGMA table_info(BPM_USER)";
        List<Map<String, Object>> columns = jdbcTemplate.queryForList(sql);
        result.put("userColumns", columns);
        
        return result;
    }

    @GetMapping("/check/participant-columns")
    public Map<String, Object> checkParticipantColumns() {
        Map<String, Object> result = new LinkedHashMap<>();
        
        String sql = "PRAGMA table_info(BPM_PROCESSDEF_PARTICIPANT)";
        List<Map<String, Object>> columns = jdbcTemplate.queryForList(sql);
        result.put("participantColumns", columns);
        
        return result;
    }
}
