package net.ooder.bpm.engine;

import net.ooder.bpm.config.SpringContextHolder;
import net.ooder.bpm.enums.agent.*;
import net.ooder.common.Filter;
import net.ooder.common.ReturnType;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;
import java.util.logging.Logger;

public class AgentEngineImpl implements AgentEngine {

    private static final Logger log = Logger.getLogger(AgentEngineImpl.class.getName());

    private volatile JdbcTemplate jdbcTemplate;

    public AgentEngineImpl() {}

    public AgentEngineImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private JdbcTemplate getJdbcTemplate() {
        if (getJdbcTemplate() == null) {
            jdbcTemplate = SpringContextHolder.getJdbcTemplate();
        }
        return jdbcTemplate;
    }

    private ReturnType success() { return new ReturnType(ReturnType.MAINCODE_SUCCESS); }
    private ReturnType fail() { return new ReturnType(ReturnType.MAINCODE_FAIL); }

    @Override
    public Filter getProcessDefListFilter(Map<AgentCtx, Object> ctx) throws BPMException { return null; }

    @Override
    public Filter getProcessInstListFilter(AgentConditionEnums condition, Map<AgentCtx, Object> ctx) throws BPMException { return null; }

    @Override
    public Filter getActivityInstListFilter(AgentConditionEnums condition, Map<AgentCtx, Object> ctx) throws BPMException { return null; }

    @Override
    public Filter getActivityInstHistoryListFilter(AgentConditionEnums condition, Map<AgentCtx, Object> ctx) throws BPMException { return null; }

    @Override
    public ReturnType createProcessInst(String processInstId, Map<AgentCtx, Object> ctx) throws BPMException {
        try {
            String agentId = ctx != null ? (String) ctx.get(AgentCtx.AGENT_ID) : null;
            if (agentId != null && getJdbcTemplate() != null) {
                getJdbcTemplate().update(
                    "INSERT INTO RT_ACTIVITY_AGENT (ACTIVITY_INST_ID, PROCESS_INST_ID, AGENT_ID, AGENT_GROUP_ENUMS, AGENT_STATUS, AGENT_TYPE) VALUES (?, ?, ?, ?, ?, ?)",
                    "init-" + UUID.randomUUID().toString().substring(0, 8),
                    processInstId, agentId,
                    AgentGroupEnums.SPONSOR.getType(),
                    AgentPerformStatus.CURRENT.getType(),
                    AgentType.LLM.getType()
                );
            }
            log.info("[AgentEngine] createProcessInst: processInstId=" + processInstId);
            return success();
        } catch (Exception e) {
            log.severe("[AgentEngine] createProcessInst failed: " + e.getMessage());
            return fail();
        }
    }

    @Override
    public ReturnType createProcessInst(String processInstId, String initType, Map<AgentCtx, Object> ctx) throws BPMException {
        return createProcessInst(processInstId, ctx);
    }

    @Override
    public ReturnType startProcessInst(String processInstId, String activityInstId, Map<AgentCtx, Object> ctx) throws BPMException {
        try {
            String agentId = ctx != null ? (String) ctx.get(AgentCtx.AGENT_ID) : null;
            if (agentId != null && activityInstId != null && getJdbcTemplate() != null) {
                getJdbcTemplate().update(
                    "INSERT INTO RT_ACTIVITY_AGENT (ACTIVITY_INST_ID, PROCESS_INST_ID, AGENT_ID, AGENT_GROUP_ENUMS, AGENT_STATUS, AGENT_TYPE) VALUES (?, ?, ?, ?, ?, ?)",
                    activityInstId, processInstId, agentId,
                    AgentGroupEnums.PERFORMER.getType(),
                    AgentPerformStatus.WAITING.getType(),
                    ctx.getOrDefault(AgentCtx.AGENT_TYPE, AgentType.LLM.getType())
                );
            }
            log.info("[AgentEngine] startProcessInst: processInstId=" + processInstId);
            return success();
        } catch (Exception e) {
            log.severe("[AgentEngine] startProcessInst failed: " + e.getMessage());
            return fail();
        }
    }

    @Override
    public List<String> getAgentCandidate(String activityDefId, Map<AgentCtx, Object> ctx) throws BPMException {
        try {
            ActivityDefAgent agentDef = getActivityDefAgentAttribute(activityDefId);
            if (agentDef == null || agentDef.getAgentSelectedId() == null) return Collections.emptyList();
            String selectedId = agentDef.getAgentSelectedId();
            if (selectedId.startsWith("$")) return Collections.singletonList(selectedId);
            return Arrays.asList(selectedId.split(","));
        } catch (Exception e) {
            log.warning("[AgentEngine] getAgentCandidate failed: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public ReturnType startActivityInst(String activityInstId, Map<AgentCtx, Object> ctx) throws BPMException { return success(); }

    @Override
    public ReturnType hasAgentToStartProcess(String processInstId, Map<AgentCtx, Object> ctx) throws BPMException { return success(); }

    @Override
    public ReturnType saveActivityHistoryInst(String activityInstId, String activityInstHistoryId, Map<AgentCtx, Object> ctx) throws BPMException {
        try {
            if (getJdbcTemplate() == null) return success();
            List<Map<String, Object>> agents = getJdbcTemplate().queryForList(
                "SELECT * FROM RT_ACTIVITY_AGENT WHERE ACTIVITY_INST_ID = ?", activityInstId
            );
            for (Map<String, Object> agent : agents) {
                getJdbcTemplate().update(
                    "INSERT INTO RT_ACTIVITYHISTORY_AGENT (ACTIVITY_HISTORY_ID, PROCESS_INST_ID, AGENT_ID, AGENT_GROUP_ENUMS, AGENT_STATUS, AGENT_TYPE, END_TIME) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)",
                    activityInstHistoryId, agent.get("PROCESS_INST_ID"), agent.get("AGENT_ID"),
                    agent.get("AGENT_GROUP_ENUMS"), AgentPerformStatus.FINISH.getType(), agent.get("AGENT_TYPE")
                );
            }
            getJdbcTemplate().update("DELETE FROM RT_ACTIVITY_AGENT WHERE ACTIVITY_INST_ID = ?", activityInstId);
            log.info("[AgentEngine] saveActivityHistoryInst: activityInstId=" + activityInstId);
            return success();
        } catch (Exception e) {
            log.severe("[AgentEngine] saveActivityHistoryInst failed: " + e.getMessage());
            return fail();
        }
    }

    @Override
    public ReturnType splitActivityInst(String activityInstId, String[] subActivityInstIds, Map<AgentCtx, Object> ctx) throws BPMException {
        try {
            if (getJdbcTemplate() == null) return success();
            List<Map<String, Object>> agents = getJdbcTemplate().queryForList(
                "SELECT * FROM RT_ACTIVITY_AGENT WHERE ACTIVITY_INST_ID = ?", activityInstId
            );
            for (String subId : subActivityInstIds) {
                for (Map<String, Object> agent : agents) {
                    getJdbcTemplate().update(
                        "INSERT INTO RT_ACTIVITY_AGENT (ACTIVITY_INST_ID, PROCESS_INST_ID, AGENT_ID, AGENT_GROUP_ENUMS, AGENT_STATUS, AGENT_TYPE) VALUES (?, ?, ?, ?, ?, ?)",
                        subId, agent.get("PROCESS_INST_ID"), agent.get("AGENT_ID"),
                        agent.get("AGENT_GROUP_ENUMS"), AgentPerformStatus.WAITING.getType(), agent.get("AGENT_TYPE")
                    );
                }
            }
            log.info("[AgentEngine] splitActivityInst: subCount=" + subActivityInstIds.length);
            return success();
        } catch (Exception e) {
            log.severe("[AgentEngine] splitActivityInst failed: " + e.getMessage());
            return fail();
        }
    }

    @Override
    public ReturnType changeAgent(String activityInstId, Map<AgentCtx, Object> ctx) throws BPMException {
        try {
            String newAgentId = ctx != null ? (String) ctx.get(AgentCtx.AGENT_ID) : null;
            if (newAgentId != null && getJdbcTemplate() != null) {
                getJdbcTemplate().update(
                    "UPDATE RT_ACTIVITY_AGENT SET AGENT_ID = ? WHERE ACTIVITY_INST_ID = ? AND AGENT_GROUP_ENUMS = ?",
                    newAgentId, activityInstId, AgentGroupEnums.PERFORMER.getType()
                );
            }
            return success();
        } catch (Exception e) {
            return fail();
        }
    }

    @Override
    public ReturnType routeTo(String activityInstId, String activityDefId, Map<AgentCtx, Object> ctx) throws BPMException {
        try {
            if (getJdbcTemplate() == null) return success();
            List<String> candidates = getAgentCandidate(activityDefId, ctx);
            if (!candidates.isEmpty()) {
                String processInstId = ctx != null ? (String) ctx.get(AgentCtx.PROCESSINST_ID) : null;
                String agentType = ctx != null ? (String) ctx.getOrDefault(AgentCtx.AGENT_TYPE, AgentType.LLM.getType()) : AgentType.LLM.getType();
                getJdbcTemplate().update("DELETE FROM RT_ACTIVITY_AGENT WHERE ACTIVITY_INST_ID = ?", activityInstId);
                for (String candidateId : candidates) {
                    getJdbcTemplate().update(
                        "INSERT INTO RT_ACTIVITY_AGENT (ACTIVITY_INST_ID, PROCESS_INST_ID, AGENT_ID, AGENT_GROUP_ENUMS, AGENT_STATUS, AGENT_TYPE) VALUES (?, ?, ?, ?, ?, ?)",
                        activityInstId, processInstId, candidateId,
                        AgentGroupEnums.PERFORMER.getType(), AgentPerformStatus.WAITING.getType(), agentType
                    );
                }
            }
            log.info("[AgentEngine] routeTo: activityInstId=" + activityInstId);
            return success();
        } catch (Exception e) {
            log.severe("[AgentEngine] routeTo failed: " + e.getMessage());
            return fail();
        }
    }

    @Override
    public boolean canRouteBack(String activityInstId, Map<AgentCtx, Object> ctx) throws BPMException { return true; }

    @Override
    public ReturnType routeBack(String activityInstId, String activityInstHistoryId, Map<AgentCtx, Object> ctx) throws BPMException {
        try {
            if (getJdbcTemplate() == null) return success();
            if (activityInstHistoryId != null) {
                List<Map<String, Object>> historyAgents = getJdbcTemplate().queryForList(
                    "SELECT * FROM RT_ACTIVITYHISTORY_AGENT WHERE ACTIVITY_HISTORY_ID = ?", activityInstHistoryId
                );
                getJdbcTemplate().update("DELETE FROM RT_ACTIVITY_AGENT WHERE ACTIVITY_INST_ID = ?", activityInstId);
                for (Map<String, Object> ha : historyAgents) {
                    getJdbcTemplate().update(
                        "INSERT INTO RT_ACTIVITY_AGENT (ACTIVITY_INST_ID, PROCESS_INST_ID, AGENT_ID, AGENT_GROUP_ENUMS, AGENT_STATUS, AGENT_TYPE) VALUES (?, ?, ?, ?, ?, ?)",
                        activityInstId, ha.get("PROCESS_INST_ID"), ha.get("AGENT_ID"),
                        ha.get("AGENT_GROUP_ENUMS"), AgentPerformStatus.WAITING.getType(), ha.get("AGENT_TYPE")
                    );
                }
            }
            return success();
        } catch (Exception e) {
            return fail();
        }
    }

    @Override
    public boolean canTakeBack(String activityInstId, Map<AgentCtx, Object> ctx) throws BPMException { return true; }

    @Override
    public ReturnType tackBack(String activityInstId, Map<AgentCtx, Object> ctx) throws BPMException {
        return routeBack(activityInstId, null, ctx);
    }

    @Override
    public boolean canSignReceive(String activityInstID, Map<AgentCtx, Object> ctx) throws BPMException {
        try {
            if (getJdbcTemplate() == null) return false;
            List<Map<String, Object>> agents = getJdbcTemplate().queryForList(
                "SELECT * FROM RT_ACTIVITY_AGENT WHERE ACTIVITY_INST_ID = ? AND AGENT_GROUP_ENUMS = ? AND AGENT_STATUS = ?",
                activityInstID, AgentGroupEnums.PERFORMER.getType(), AgentPerformStatus.WAITING.getType()
            );
            return !agents.isEmpty();
        } catch (Exception e) { return false; }
    }

    @Override
    public ReturnType signReceive(String activityInstID, Map<AgentCtx, Object> ctx) throws BPMException {
        try {
            if (getJdbcTemplate() != null) {
                getJdbcTemplate().update(
                    "UPDATE RT_ACTIVITY_AGENT SET AGENT_STATUS = ? WHERE ACTIVITY_INST_ID = ? AND AGENT_GROUP_ENUMS = ?",
                    AgentPerformStatus.CURRENT.getType(), activityInstID, AgentGroupEnums.PERFORMER.getType()
                );
            }
            log.info("[AgentEngine] signReceive: activityInstId=" + activityInstID);
            return success();
        } catch (Exception e) { return fail(); }
    }

    @Override
    public ReturnType combineActivityInsts(String[] activityInstIds, Map<AgentCtx, Object> ctx) throws BPMException {
        try {
            if (getJdbcTemplate() == null || activityInstIds == null || activityInstIds.length <= 1) return success();
            for (int i = 1; i < activityInstIds.length; i++) {
                getJdbcTemplate().update("DELETE FROM RT_ACTIVITY_AGENT WHERE ACTIVITY_INST_ID = ?", activityInstIds[i]);
            }
            return success();
        } catch (Exception e) { return fail(); }
    }

    @Override
    public ReturnType suspendActivityInst(String activityInstID, Map<AgentCtx, Object> ctx) throws BPMException { return success(); }

    @Override
    public ReturnType resumeActivityInst(String activityInstID, Map<AgentCtx, Object> ctx) throws BPMException { return success(); }

    @Override
    public ReturnType suspendProcessInst(String processInstID, Map<AgentCtx, Object> ctx) throws BPMException { return success(); }

    @Override
    public ReturnType resumeProcessInst(String processInstID, Map<AgentCtx, Object> ctx) throws BPMException { return success(); }

    @Override
    public ReturnType abortProcessInst(String processInstID, Map<AgentCtx, Object> ctx) throws BPMException {
        try {
            if (getJdbcTemplate() != null) getJdbcTemplate().update("DELETE FROM RT_ACTIVITY_AGENT WHERE PROCESS_INST_ID = ?", processInstID);
            return success();
        } catch (Exception e) { return fail(); }
    }

    @Override
    public ReturnType completeProcessInst(String processInstID, Map<AgentCtx, Object> ctx) throws BPMException {
        try {
            if (getJdbcTemplate() != null) getJdbcTemplate().update("DELETE FROM RT_ACTIVITY_AGENT WHERE PROCESS_INST_ID = ?", processInstID);
            return success();
        } catch (Exception e) { return fail(); }
    }

    @Override
    public ReturnType deleteProcessInst(String processInstID, Map<AgentCtx, Object> ctx) throws BPMException {
        try {
            if (getJdbcTemplate() != null) {
                getJdbcTemplate().update("DELETE FROM RT_ACTIVITY_AGENT WHERE PROCESS_INST_ID = ?", processInstID);
                getJdbcTemplate().update("DELETE FROM RT_ACTIVITYHISTORY_AGENT WHERE PROCESS_INST_ID = ?", processInstID);
            }
            return success();
        } catch (Exception e) { return fail(); }
    }

    @Override
    public ReturnType endTask(String activityInstID, String activityInstHistoryID, Map<AgentCtx, Object> ctx) throws BPMException {
        return saveActivityHistoryInst(activityInstID, activityInstHistoryID, ctx);
    }

    @Override
    public ReturnType abortedTask(String activityInstID, String activityInstHistoryID, Map<AgentCtx, Object> ctx) throws BPMException {
        try {
            if (getJdbcTemplate() == null) return success();
            List<Map<String, Object>> agents = getJdbcTemplate().queryForList(
                "SELECT * FROM RT_ACTIVITY_AGENT WHERE ACTIVITY_INST_ID = ?", activityInstID
            );
            for (Map<String, Object> agent : agents) {
                getJdbcTemplate().update(
                    "INSERT INTO RT_ACTIVITYHISTORY_AGENT (ACTIVITY_HISTORY_ID, PROCESS_INST_ID, AGENT_ID, AGENT_GROUP_ENUMS, AGENT_STATUS, AGENT_TYPE, END_TIME) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)",
                    activityInstHistoryID, agent.get("PROCESS_INST_ID"), agent.get("AGENT_ID"),
                    agent.get("AGENT_GROUP_ENUMS"), AgentPerformStatus.ERROR.getType(), agent.get("AGENT_TYPE")
                );
            }
            getJdbcTemplate().update("DELETE FROM RT_ACTIVITY_AGENT WHERE ACTIVITY_INST_ID = ?", activityInstID);
            return success();
        } catch (Exception e) { return fail(); }
    }

    @Override
    public ActivityDefAgent getActivityDefAgentAttribute(String activityDefId) throws BPMException {
        try {
            if (getJdbcTemplate() == null) return null;
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(
                "SELECT * FROM BPM_ACTIVITYDEF_AGENT WHERE ACTIVITYDEF_ID = ?", activityDefId
            );
            if (rows.isEmpty()) return null;
            Map<String, Object> row = rows.get(0);
            ActivityDefAgent def = new ActivityDefAgent();
            def.setActivityDefId(activityDefId);
            def.setAgentSelectedId((String) row.get("AGENT_SELECTED_ID"));
            def.setAgentType((String) row.get("AGENT_TYPE"));
            def.setPerformType((String) row.get("PERFORM_TYPE"));
            def.setPerformSequence((String) row.get("PERFORM_SEQUENCE"));
            def.setCanRouteBack((String) row.get("CAN_ROUTE_BACK"));
            def.setRouteBackMethod((String) row.get("ROUTE_BACK_METHOD"));
            def.setCanTakeBack((String) row.get("CAN_TAKE_BACK"));
            def.setCoordinatorId((String) row.get("COORDINATOR_ID"));
            def.setLlmProvider((String) row.get("LLM_PROVIDER"));
            def.setLlmModel((String) row.get("LLM_MODEL"));
            def.setSystemPrompt((String) row.get("SYSTEM_PROMPT"));
            def.setTemperature(row.get("TEMPERATURE") != null ? ((Number) row.get("TEMPERATURE")).doubleValue() : 0.7);
            def.setMaxTokens(row.get("MAX_TOKENS") != null ? ((Number) row.get("MAX_TOKENS")).intValue() : 4096);
            def.setMcpTools((String) row.get("MCP_TOOLS"));
            def.setCapabilities((String) row.get("CAPABILITIES"));
            def.setExtendedAttributes((String) row.get("EXTENDED_ATTRIBUTES"));
            return def;
        } catch (Exception e) {
            log.warning("[AgentEngine] getActivityDefAgentAttribute failed: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<String> getActivityInstAgentAttribute(String activityInstId, AgentGroupEnums attName, Map<AgentCtx, Object> ctx) throws BPMException {
        try {
            if (getJdbcTemplate() == null) return Collections.emptyList();
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(
                "SELECT AGENT_ID FROM RT_ACTIVITY_AGENT WHERE ACTIVITY_INST_ID = ? AND AGENT_GROUP_ENUMS = ?",
                activityInstId, attName.getType()
            );
            List<String> result = new ArrayList<>();
            for (Map<String, Object> row : rows) result.add((String) row.get("AGENT_ID"));
            return result;
        } catch (Exception e) { return Collections.emptyList(); }
    }

    @Override
    public List<String> getActivityInstHistoryAgentAttribute(String activityInstHistoryId, AgentGroupEnums attName, Map<AgentCtx, Object> ctx) throws BPMException {
        try {
            if (getJdbcTemplate() == null) return Collections.emptyList();
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(
                "SELECT AGENT_ID FROM RT_ACTIVITYHISTORY_AGENT WHERE ACTIVITY_HISTORY_ID = ? AND AGENT_GROUP_ENUMS = ?",
                activityInstHistoryId, attName.getType()
            );
            List<String> result = new ArrayList<>();
            for (Map<String, Object> row : rows) result.add((String) row.get("AGENT_ID"));
            return result;
        } catch (Exception e) { return Collections.emptyList(); }
    }

    @Override
    public boolean queryPermissionToActivityInst(String activityInstId, Map<AgentCtx, Object> ctx) throws BPMException {
        try {
            String agentId = ctx != null ? (String) ctx.get(AgentCtx.AGENT_ID) : null;
            if (agentId == null || getJdbcTemplate() == null) return false;
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(
                "SELECT * FROM RT_ACTIVITY_AGENT WHERE ACTIVITY_INST_ID = ? AND AGENT_ID = ?", activityInstId, agentId
            );
            return !rows.isEmpty();
        } catch (Exception e) { return false; }
    }

    @Override
    public List<AgentGroupEnums> queryAllPermissionToActivityInst(String activityInstId, Map<AgentCtx, Object> ctx) throws BPMException {
        try {
            String agentId = ctx != null ? (String) ctx.get(AgentCtx.AGENT_ID) : null;
            if (agentId == null || getJdbcTemplate() == null) return Collections.emptyList();
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(
                "SELECT AGENT_GROUP_ENUMS FROM RT_ACTIVITY_AGENT WHERE ACTIVITY_INST_ID = ? AND AGENT_ID = ?", activityInstId, agentId
            );
            List<AgentGroupEnums> result = new ArrayList<>();
            for (Map<String, Object> row : rows) result.add(AgentGroupEnums.fromType((String) row.get("AGENT_GROUP_ENUMS")));
            return result;
        } catch (Exception e) { return Collections.emptyList(); }
    }

    @Override
    public void copyActivityInstByHistory(String activityInstId, String activityHistoryInstId, Map<AgentCtx, Object> ctx) throws BPMException {
        try {
            if (getJdbcTemplate() == null) return;
            List<Map<String, Object>> historyAgents = getJdbcTemplate().queryForList(
                "SELECT * FROM RT_ACTIVITYHISTORY_AGENT WHERE ACTIVITY_HISTORY_ID = ?", activityHistoryInstId
            );
            for (Map<String, Object> ha : historyAgents) {
                getJdbcTemplate().update(
                    "INSERT INTO RT_ACTIVITY_AGENT (ACTIVITY_INST_ID, PROCESS_INST_ID, AGENT_ID, AGENT_GROUP_ENUMS, AGENT_STATUS, AGENT_TYPE) VALUES (?, ?, ?, ?, ?, ?)",
                    activityInstId, ha.get("PROCESS_INST_ID"), ha.get("AGENT_ID"),
                    ha.get("AGENT_GROUP_ENUMS"), AgentPerformStatus.WAITING.getType(), ha.get("AGENT_TYPE")
                );
            }
        } catch (Exception e) {
            log.warning("[AgentEngine] copyActivityInstByHistory failed: " + e.getMessage());
        }
    }

    @Override
    public ReturnType invokeAgent(String activityInstId, Map<AgentCtx, Object> ctx) throws BPMException {
        try {
            if (getJdbcTemplate() != null) {
                getJdbcTemplate().update(
                    "UPDATE RT_ACTIVITY_AGENT SET AGENT_STATUS = ? WHERE ACTIVITY_INST_ID = ? AND AGENT_GROUP_ENUMS = ?",
                    AgentPerformStatus.CURRENT.getType(), activityInstId, AgentGroupEnums.PERFORMER.getType()
                );
            }
            log.info("[AgentEngine] invokeAgent: activityInstId=" + activityInstId);
            return success();
        } catch (Exception e) {
            log.severe("[AgentEngine] invokeAgent failed: " + e.getMessage());
            return fail();
        }
    }

    @Override
    public void terminateAgent(String activityInstId) throws BPMException {
        try {
            if (getJdbcTemplate() != null) {
                getJdbcTemplate().update(
                    "UPDATE RT_ACTIVITY_AGENT SET AGENT_STATUS = ? WHERE ACTIVITY_INST_ID = ?",
                    AgentPerformStatus.ERROR.getType(), activityInstId
                );
            }
        } catch (Exception e) {
            log.warning("[AgentEngine] terminateAgent failed: " + e.getMessage());
        }
    }

    @Override
    public String getAgentStatus(String activityInstId) throws BPMException {
        try {
            if (getJdbcTemplate() == null) return null;
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(
                "SELECT AGENT_STATUS FROM RT_ACTIVITY_AGENT WHERE ACTIVITY_INST_ID = ? AND AGENT_GROUP_ENUMS = ?",
                activityInstId, AgentGroupEnums.PERFORMER.getType()
            );
            return rows.isEmpty() ? null : (String) rows.get(0).get("AGENT_STATUS");
        } catch (Exception e) { return null; }
    }

    @Override
    public ReturnType escalateToCoordinator(String activityInstId, Map<AgentCtx, Object> ctx) throws BPMException {
        try {
            String coordinatorId = null;
            if (ctx != null) coordinatorId = (String) ctx.get(AgentCtx.COORDINATOR);
            if (coordinatorId != null && getJdbcTemplate() != null) {
                String processInstId = ctx != null ? (String) ctx.get(AgentCtx.PROCESSINST_ID) : null;
                getJdbcTemplate().update(
                    "INSERT INTO RT_ACTIVITY_AGENT (ACTIVITY_INST_ID, PROCESS_INST_ID, AGENT_ID, AGENT_GROUP_ENUMS, AGENT_STATUS, AGENT_TYPE) VALUES (?, ?, ?, ?, ?, ?)",
                    activityInstId, processInstId, coordinatorId,
                    AgentGroupEnums.COORDINATOR.getType(), AgentPerformStatus.WAITING.getType(), AgentType.COORDINATOR.getType()
                );
            }
            log.info("[AgentEngine] escalateToCoordinator: activityInstId=" + activityInstId);
            return success();
        } catch (Exception e) { return fail(); }
    }

    @Override
    public ReturnType delegateToAgent(String activityInstId, String targetAgentId, Map<AgentCtx, Object> ctx) throws BPMException {
        try {
            if (getJdbcTemplate() != null) {
                String processInstId = ctx != null ? (String) ctx.get(AgentCtx.PROCESSINST_ID) : null;
                getJdbcTemplate().update(
                    "UPDATE RT_ACTIVITY_AGENT SET AGENT_STATUS = ? WHERE ACTIVITY_INST_ID = ? AND AGENT_GROUP_ENUMS = ?",
                    AgentPerformStatus.FINISH.getType(), activityInstId, AgentGroupEnums.PERFORMER.getType()
                );
                getJdbcTemplate().update(
                    "INSERT INTO RT_ACTIVITY_AGENT (ACTIVITY_INST_ID, PROCESS_INST_ID, AGENT_ID, AGENT_GROUP_ENUMS, AGENT_STATUS, AGENT_TYPE) VALUES (?, ?, ?, ?, ?, ?)",
                    activityInstId, processInstId, targetAgentId,
                    AgentGroupEnums.PERFORMER.getType(), AgentPerformStatus.WAITING.getType(),
                    ctx != null ? (String) ctx.getOrDefault(AgentCtx.AGENT_TYPE, AgentType.LLM.getType()) : AgentType.LLM.getType()
                );
            }
            log.info("[AgentEngine] delegateToAgent: targetAgentId=" + targetAgentId);
            return success();
        } catch (Exception e) { return fail(); }
    }
}
