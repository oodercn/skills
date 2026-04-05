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

import net.ooder.bpm.enums.form.LockEnum;
import net.ooder.bpm.enums.form.MarkEnum;
import net.ooder.common.CommonYesNoEnum;
import net.ooder.common.cache.Cacheable;

import java.io.Serializable;
import java.util.List;

public class DbProcessDefForm implements Cacheable, Serializable {


    private CommonYesNoEnum autoSave = null;

    private CommonYesNoEnum noSqlType = null;

    private String processDefVersionId = null;

    private MarkEnum mark = MarkEnum.ProcessInst;

    private LockEnum lock = LockEnum.Last;

    private List<String> tableNames;

    private List<String> formsNames;

    DbProcessDefForm(String processDefVersionId) {
        this.processDefVersionId = processDefVersionId;
    }

    public String getProcessDefVersionId() {
        return processDefVersionId;
    }

    public void setProcessDefVersionId(String processDefVersionId) {
        this.processDefVersionId = processDefVersionId;
    }

    public MarkEnum getMark() {
        return mark;
    }

    public void setMark(MarkEnum mark) {
        this.mark = mark;
    }

    public LockEnum getLock() {
        return lock;
    }

    public void setLock(LockEnum lock) {
        this.lock = lock;
    }

    public CommonYesNoEnum getAutoSave() {
        return autoSave;
    }

    public void setAutoSave(CommonYesNoEnum autoSave) {
        this.autoSave = autoSave;
    }

    public CommonYesNoEnum getNoSqlType() {
        return noSqlType;
    }

    public void setNoSqlType(CommonYesNoEnum noSqlType) {
        this.noSqlType = noSqlType;
    }

    public List<String> getTableNames() {
        return tableNames;
    }

    public void setTableNames(List<String> tableNames) {
        this.tableNames = tableNames;
    }

    public List<String> getFormsNames() {
        return formsNames;
    }

    public void setFormsNames(List<String> formsNames) {
        this.formsNames = formsNames;
    }

    public int getCachedSize() {

        int size = 0;


        return size;
    }


}


