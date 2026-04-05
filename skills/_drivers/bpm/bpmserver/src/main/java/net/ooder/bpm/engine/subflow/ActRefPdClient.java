package net.ooder.bpm.engine.subflow;

/**
 * 获取子流程与活动的关系
 * wenzhang li
 */
import java.util.List;

import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.subflow.db.ActRefPd;

public interface ActRefPdClient {

    /**
     * 根据当前活动ID获取SUPFLOW实例
     * 
     * @param ActivityId
     * @return
     */
    public ActRefPd getActRefPdbyActivityId(String ActivityId);

  
    /**
     * 根据当前活动ID获取SUPFLOW的子流程版本ID 如果子流程为私有则版本应该是唯一的，版本的意义由父流程定义
     * 
     * @param activityId
     * @return
     */
    public String getSubProcessDefVersionId(String activityId);

    /**
     * 该流程内所有子流程及外流程的版本ID
     * 
     * @param versionId
     * @return
     * @throws DAOException
     * @throws BPMException
     */
    public List<String> getSubPorcessXPDL(String versionId) throws BPMException;

    /**
     * 删除该版本下所有子流程关系
     * 
     * @param versionId
     * @throws DAOException
     */
    public void delete(String versionId) throws BPMException;

}
