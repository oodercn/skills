package net.ooder.skill.workflow.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.ooder.skill.bpm.engine.WorkflowClientService;
import net.ooder.skill.bpm.model.ActivityInst;
import net.ooder.skill.bpm.model.ProcessDef;
import net.ooder.skill.bpm.model.ProcessInst;
import net.ooder.skill.bpm.service.ProcessDefService;
import net.ooder.skill.bpm.service.ProcessInstService;
import net.ooder.skill.workflow.dto.*;

public class WorkflowService {

    private final ProcessDefService processDefService;
    private final ProcessInstService processInstService;
    private final WorkflowClientService workflowClientService;

    public WorkflowService(
            ProcessDefService processDefService,
            ProcessInstService processInstService,
            WorkflowClientService workflowClientService) {
        this.processDefService = processDefService;
        this.processInstService = processInstService;
        this.workflowClientService = workflowClientService;
    }

    public List<ProcessDefDTO> listProcessDefs(String category) {
        List<ProcessDef> defs = processDefService.listProcessDefsByCategory(category);
        return defs.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<ProcessDefDTO> listPublishedProcessDefs() {
        List<ProcessDef> defs = processDefService.listPublishedProcessDefs();
        return defs.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public ProcessDefDTO getProcessDef(String id) {
        ProcessDef def = processDefService.getProcessDef(id);
        return def != null ? convertToDTO(def) : null;
    }

    public ProcessDefDTO createProcessDef(ProcessDefDTO dto) {
        ProcessDef def = convertToEntity(dto);
        def = processDefService.createProcessDef(def);
        return convertToDTO(def);
    }

    public ProcessDefDTO updateProcessDef(ProcessDefDTO dto) {
        ProcessDef def = convertToEntity(dto);
        def = processDefService.updateProcessDef(def);
        return def != null ? convertToDTO(def) : null;
    }

    public boolean deleteProcessDef(String id) {
        return processDefService.deleteProcessDef(id);
    }

    public ProcessDefDTO publishProcessDef(String id) {
        ProcessDef def = processDefService.publishProcessDef(id);
        return def != null ? convertToDTO(def) : null;
    }

    public ProcessDefDTO unpublishProcessDef(String id) {
        ProcessDef def = processDefService.unpublishProcessDef(id);
        return def != null ? convertToDTO(def) : null;
    }

    public List<ProcessInstDTO> listProcessInsts(String status) {
        List<ProcessInst> insts;
        if ("RUNNING".equals(status)) {
            insts = processInstService.listRunningProcessInsts();
        } else if ("COMPLETED".equals(status)) {
            insts = processInstService.listCompletedProcessInsts();
        } else {
            insts = processInstService.listProcessInsts();
        }
        return insts.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<ProcessInstDTO> listMyProcessInsts(String userId) {
        return processInstService.listPendingActivitiesByUser(userId).stream()
                .map(a -> processInstService.getProcessInst(a.getProcessInstId()))
                .filter(p -> p != null)
                .distinct()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProcessInstDTO> listProcessInstsStartedBy(String userId) {
        List<ProcessInst> insts = processInstService.listProcessInstsByStarter(userId);
        return insts.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public ProcessInstDTO getProcessInst(String id) {
        ProcessInst inst = processInstService.getProcessInst(id);
        return inst != null ? convertToDTO(inst) : null;
    }

    public ProcessInstDTO startProcess(StartProcessRequest request, String userId) {
        ProcessInst inst = workflowClientService.startProcess(
                request.getProcessDefId(),
                request.getVariables(),
                userId);
        if (request.getName() != null) {
            inst.setName(request.getName());
        }
        if (request.getSubject() != null) {
            inst.setSubject(request.getSubject());
        }
        if (request.getPriority() != null) {
            inst.setPriority(request.getPriority());
        }
        if (request.getFormData() != null) {
            inst.getFormData().putAll(request.getFormData());
        }
        inst = processInstService.createProcessInst(inst);
        return convertToDTO(inst);
    }

    public ProcessInstDTO suspendProcess(String id) {
        workflowClientService.suspendProcess(id);
        ProcessInst inst = processInstService.getProcessInst(id);
        if (inst != null) {
            inst.setStatus("SUSPENDED");
            inst = processInstService.updateProcessInst(inst);
        }
        return inst != null ? convertToDTO(inst) : null;
    }

    public ProcessInstDTO resumeProcess(String id) {
        workflowClientService.resumeProcess(id);
        ProcessInst inst = processInstService.getProcessInst(id);
        if (inst != null) {
            inst.setStatus("RUNNING");
            inst = processInstService.updateProcessInst(inst);
        }
        return inst != null ? convertToDTO(inst) : null;
    }

    public ProcessInstDTO terminateProcess(String id, String reason) {
        workflowClientService.terminateProcess(id, reason);
        ProcessInst inst = processInstService.terminateProcessInst(id, reason);
        return inst != null ? convertToDTO(inst) : null;
    }

    public Map<String, Object> getProcessVariables(String id) {
        ProcessInst inst = processInstService.getProcessInst(id);
        return inst != null ? inst.getProcessVariables() : null;
    }

    public void setProcessVariables(String id, Map<String, Object> variables) {
        ProcessInst inst = processInstService.getProcessInst(id);
        if (inst != null) {
            inst.getProcessVariables().putAll(variables);
            processInstService.updateProcessInst(inst);
        }
    }

    public List<TaskDTO> listMyTasks(String userId) {
        List<ActivityInst> activities = processInstService.listPendingActivitiesByUser(userId);
        return activities.stream().map(this::convertToTaskDTO).collect(Collectors.toList());
    }

    public List<TaskDTO> listMyCompletedTasks(String userId) {
        List<ActivityInst> activities = processInstService.listCompletedActivitiesByUser(userId);
        return activities.stream().map(this::convertToTaskDTO).collect(Collectors.toList());
    }

    public TaskDTO getTask(String id) {
        ActivityInst inst = processInstService.getActivityInst(id);
        return inst != null ? convertToTaskDTO(inst) : null;
    }

    public TaskDTO claimTask(String id, String userId) {
        ActivityInst inst = processInstService.claimActivityInst(id, userId);
        return inst != null ? convertToTaskDTO(inst) : null;
    }

    public TaskDTO releaseTask(String id, String userId) {
        ActivityInst inst = processInstService.getActivityInst(id);
        if (inst != null && userId.equals(inst.getAssigneeId())) {
            inst.setAssigneeId(null);
            inst.setClaimTime(null);
            inst = processInstService.claimActivityInst(id, null);
        }
        return inst != null ? convertToTaskDTO(inst) : null;
    }

    public TaskDTO completeTask(CompleteTaskRequest request) {
        ActivityInst inst = processInstService.completeActivityInst(
                request.getTaskId(),
                request.getUserId(),
                request.getResult());
        if (inst != null && request.getOpinion() != null) {
            inst.setOpinion(request.getOpinion());
        }
        if (inst != null && request.getFormData() != null) {
            inst.getFormData().putAll(request.getFormData());
        }
        return inst != null ? convertToTaskDTO(inst) : null;
    }

    public List<TaskDTO> listTasksByProcess(String processInstId) {
        List<ActivityInst> activities = processInstService.listActivityInstsByProcess(processInstId);
        return activities.stream().map(this::convertToTaskDTO).collect(Collectors.toList());
    }

    private ProcessDefDTO convertToDTO(ProcessDef def) {
        ProcessDefDTO dto = new ProcessDefDTO();
        dto.setId(def.getId());
        dto.setName(def.getName());
        dto.setDisplayName(def.getDisplayName());
        dto.setDescription(def.getDescription());
        dto.setCategory(def.getCategory());
        dto.setVersion(def.getVersion());
        dto.setStatus(def.getStatus());
        dto.setXpdlContent(def.getXpdlContent());
        dto.setFormDef(def.getFormDef());
        dto.setExtendedAttributes(def.getExtendedAttributes());
        dto.setCreatorId(def.getCreatorId());
        dto.setCreatorName(def.getCreatorName());
        dto.setCreateTime(def.getCreateTime());
        dto.setUpdateTime(def.getUpdateTime());
        dto.setPublishTime(def.getPublishTime());
        return dto;
    }

    private ProcessDef convertToEntity(ProcessDefDTO dto) {
        ProcessDef def = new ProcessDef();
        def.setId(dto.getId());
        def.setName(dto.getName());
        def.setDisplayName(dto.getDisplayName());
        def.setDescription(dto.getDescription());
        def.setCategory(dto.getCategory());
        def.setVersion(dto.getVersion());
        def.setStatus(dto.getStatus());
        def.setXpdlContent(dto.getXpdlContent());
        def.setFormDef(dto.getFormDef());
        def.setExtendedAttributes(dto.getExtendedAttributes());
        def.setCreatorId(dto.getCreatorId());
        def.setCreatorName(dto.getCreatorName());
        return def;
    }

    private ProcessInstDTO convertToDTO(ProcessInst inst) {
        ProcessInstDTO dto = new ProcessInstDTO();
        dto.setId(inst.getId());
        dto.setProcessDefId(inst.getProcessDefId());
        dto.setProcessDefName(inst.getProcessDefName());
        dto.setProcessDefVersion(inst.getProcessDefVersion());
        dto.setName(inst.getName());
        dto.setSubject(inst.getSubject());
        dto.setStatus(inst.getStatus());
        dto.setPriority(inst.getPriority());
        dto.setStarterId(inst.getStarterId());
        dto.setStarterName(inst.getStarterName());
        dto.setStartTime(inst.getStartTime());
        dto.setEndTime(inst.getEndTime());
        dto.setDeadline(inst.getDeadline());
        dto.setProcessVariables(inst.getProcessVariables());
        dto.setFormData(inst.getFormData());
        dto.setCurrentActivityId(inst.getCurrentActivityId());
        dto.setCurrentActivityName(inst.getCurrentActivityName());
        dto.setCreateTime(inst.getCreateTime());
        dto.setUpdateTime(inst.getUpdateTime());
        return dto;
    }

    private TaskDTO convertToTaskDTO(ActivityInst inst) {
        TaskDTO dto = new TaskDTO();
        dto.setId(inst.getId());
        dto.setProcessInstId(inst.getProcessInstId());
        dto.setActivityDefId(inst.getActivityDefId());
        dto.setActivityDefName(inst.getActivityDefName());
        dto.setName(inst.getName());
        dto.setType(inst.getType());
        dto.setStatus(inst.getStatus());
        dto.setPerformerId(inst.getPerformerId());
        dto.setPerformerName(inst.getPerformerName());
        dto.setAssigneeId(inst.getAssigneeId());
        dto.setAssigneeName(inst.getAssigneeName());
        dto.setStartTime(inst.getStartTime());
        dto.setDeadline(inst.getDeadline());
        dto.setClaimTime(inst.getClaimTime());
        dto.setFormData(inst.getFormData());
        dto.setOpinion(inst.getOpinion());
        dto.setResult(inst.getResult());
        dto.setCreateTime(inst.getCreateTime());
        return dto;
    }
}
