/**
 * $RCSfile: ProcessDefProxy.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:45 $
 * <p>
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.proxy;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.bpm.client.ProcessDef;
import net.ooder.bpm.client.ProcessDefVersion;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.inter.EIProcessDef;
import net.ooder.bpm.engine.inter.EIProcessDefVersion;
import net.ooder.bpm.enums.process.ProcessDefAccess;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 流程定义客户端接口的代理实现
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 *
 * @author wenzhang li
 * @version 1.0
 */
public class ProcessDefProxy implements ProcessDef, Serializable {

    @JSONField(serialize = false)
    private EIProcessDef eiProcessDef;
    private String systemCode;

    /**
     * @param eiProcessDef
     */
    public ProcessDefProxy(EIProcessDef eiProcessDef, String systemCode) {
        super();
        this.systemCode = systemCode;
        this.eiProcessDef = eiProcessDef;
    }

    /*
     * @see com.ds.bpm.client.ProcessDef#getProcessDefId()
     */
    public String getProcessDefId() {
        return eiProcessDef.getProcessDefId();
    }

    /*
     * @see com.ds.bpm.client.ProcessDef#getName()
     */
    public String getName() {
        return eiProcessDef.getName();
    }

    /*
     * @see com.ds.bpm.client.ProcessDef#getDescription()
     */
    public String getDescription() {
        return eiProcessDef.getDescription();
    }

    /*
     * @see com.ds.bpm.client.ProcessDef#getClassification()
     */
    public String getClassification() {
        return eiProcessDef.getClassification();
    }

    /*
     * @see com.ds.bpm.client.ProcessDef#getSystemCode()
     */
    public String getSystemCode() {
        return eiProcessDef.getSystemCode();
    }

    /*
     * @see com.ds.bpm.client.ProcessDef#getAccessLevel()
     */
    public ProcessDefAccess getAccessLevel() {
        return ProcessDefAccess.fromType(eiProcessDef.getAccessLevel());
    }

    /*
     * @see com.ds.bpm.client.ProcessDef#getProcessDefVersion(int)
     */

    @JSONField(serialize = false)
    public ProcessDefVersion getProcessDefVersion(int version) throws BPMException {
        EIProcessDefVersion eiProcessDefVersion = eiProcessDef.getProcessDefVersion(version);
        return new ProcessDefVersionProxy(eiProcessDefVersion, systemCode);
    }

    /*
     * @see com.ds.bpm.client.ProcessDef#getAllProcessDefVersionIds()
     */

    public List<String> getAllProcessDefVersionIds() {
        try {
            return eiProcessDef.getAllProcessDefVersionIds();
        } catch (BPMException e) {
            return new ArrayList<String>();
        }
    }

    /*
     * @see com.ds.bpm.client.ProcessDef#getAllProcessDefVersions()
     */

    public List<ProcessDefVersion> getAllProcessDefVersions() throws BPMException {
        List<EIProcessDefVersion> allProcessDefVersionsList = eiProcessDef.getAllProcessDefVersions();
        return new WorkflowListProxy(allProcessDefVersionsList, systemCode);
    }

    /*
     * @see com.ds.bpm.client.ProcessDef#getActiveProcessDefVersion()
     */

    @JSONField(serialize = false)
    public ProcessDefVersion getActiveProcessDefVersion() throws BPMException {
        EIProcessDefVersion eiProcessDefVersion = eiProcessDef.getActiveProcessDefVersion();

        if (eiProcessDefVersion==null){
           // throw new BPMException("流程未激活");
            return null;
        }

        return new ProcessDefVersionProxy(eiProcessDefVersion, systemCode);
    }


}
