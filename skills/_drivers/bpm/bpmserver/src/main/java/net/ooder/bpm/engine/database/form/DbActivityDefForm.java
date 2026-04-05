/**
 * $RCSfile: DbActivityDefRight.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:26:04 $
 * <p>
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.database.form;

import net.ooder.bpm.engine.inter.EIAttributeDef;
import net.ooder.common.cache.CacheSizes;
import net.ooder.common.cache.Cacheable;

import java.io.Serializable;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 默认权限模型使用的活动权限定义数据封装类
 * </p>
 * <p>
 * 此类数据来自于活动定义的扩展属性
 * </p>
 * <p>
 * Copyright: Copyright (c) 20
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 *
 * @author lxl
 * @version 1.0
 */
public class DbActivityDefForm implements Cacheable, Serializable {

    public static final String CANINSTEADSIGN_DEFAULT = "DEFAULT"; // 全流程默认值

    private String activityDefId = null;

    private String escomSelectedId = null;

    private String actionSelectedId = null;

    private String tableSelectedId = null;


    private EIAttributeDef escomSelectedAtt = null;

    private EIAttributeDef actionSelectedAtt = null;

    private EIAttributeDef tableSelectedAtt = null;

    DbActivityDefForm() {

    }

    public String getActivityDefId() {
        return activityDefId;
    }

    public String getEscomSelectedId() {
        return escomSelectedId;
    }

    public void setEscomSelectedId(String escomSelectedId) {
        this.escomSelectedId = escomSelectedId;
    }

    public String getActionSelectedId() {
        return actionSelectedId;
    }

    public void setActionSelectedId(String actionSelectedId) {
        this.actionSelectedId = actionSelectedId;
    }

    public String getTableSelectedId() {
        return tableSelectedId;
    }

    public void setTableSelectedId(String tableSelectedId) {
        this.tableSelectedId = tableSelectedId;
    }

    public EIAttributeDef getEscomSelectedAtt() {
        return escomSelectedAtt;
    }

    public void setEscomSelectedAtt(EIAttributeDef escomSelectedAtt) {
        this.escomSelectedAtt = escomSelectedAtt;
    }

    public EIAttributeDef getActionSelectedAtt() {
        return actionSelectedAtt;
    }

    public void setActionSelectedAtt(EIAttributeDef actionSelectedAtt) {
        this.actionSelectedAtt = actionSelectedAtt;
    }

    public EIAttributeDef getTableSelectedAtt() {
        return tableSelectedAtt;
    }

    public void setTableSelectedAtt(EIAttributeDef tableSelectedAtt) {
        this.tableSelectedAtt = tableSelectedAtt;
    }

    public void setActivityDefId(String activityDefId) {
        this.activityDefId = activityDefId;
    }


    /*
     * (non-Javadoc)
     *
     * @see net.ooder.common.cache.Cacheable#getCachedSize()
     */
    public int getCachedSize() {

        int size = 0;

        size += CacheSizes.sizeOfString(activityDefId);
        size += CacheSizes.sizeOfString(escomSelectedId);
        size += CacheSizes.sizeOfString(actionSelectedId);
        size += CacheSizes.sizeOfString(tableSelectedId);

        return size;
    }


}


