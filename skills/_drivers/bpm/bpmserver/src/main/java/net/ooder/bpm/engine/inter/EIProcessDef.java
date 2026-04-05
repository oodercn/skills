/**
 * $RCSfile: EIProcessDef.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:59 $
 * <p>
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.inter;

import java.util.List;

import net.ooder.bpm.engine.BPMException;

public interface EIProcessDef {

    /**
     * 返回流程ID 此属性为流程基本属性，与版本无关，
     *
     * @return 流程的UUID
     */

    public String getProcessDefId();

    public void setProcessDefId(String processDefId);

    /**
     * 流程定义的名称， 此属性为流程基本属性，与版本无关，
     *
     * @return 流程定义的名称
     */
    public String getName();

    public void setName(String name);

    /**
     * 流程定义的描述， 此属性为流程基本属性，与版本无关，
     *
     * @return 流程定义的描述
     */
    public String getDescription();

    public void setDescription(String description);

    /**
     * 流程定义的分类， 此属性为流程基本属性，与版本无关，
     *
     * @return 流程定义的分类
     */
    public String getClassification();

    public void setClassification(String classfication);


    public String getSystemCode();

    public void setSystemCode(String systemCode);

    /**
     * 流程的访问级别，也就对应着流程的类型 此属性为流程基本属性，与版本无关，
     *
     * @return 两种返回值：
     *         <li>ACCESS_PUBLIC: 可以独立启动
     *         <li>ACCESS_PRIVATE: 不可以独立启动，只能作为Subflow
     */
    public String getAccessLevel();

    public void setAccessLevel(String accessLevel);

    /**
     * 取得指定版本的流程定义
     *
     * @param version
     *            版本号
     * @return 如果不存在此版本，返回null
     */
    public EIProcessDefVersion getProcessDefVersion(int version) throws BPMException;

    /**
     * 返回此流程的所有版本的UUID
     *
     * @return List内保存的是UUID。如果没有则List为空
     */
    public List<String> getAllProcessDefVersionIds() throws BPMException;

    /**
     * 返回此流程的所有版本
     *
     * @return List内保存的是EIProcessDefVersion对象。如果没有则List为空
     */
    public List<EIProcessDefVersion> getAllProcessDefVersions() throws BPMException;

    /**
     * 返回当前激活的版本，如果没有激活的版本，则返回null
     *
     * @return 如果没有激活的版本，则返回null
     */
    public EIProcessDefVersion getActiveProcessDefVersion() throws BPMException;
}

