/**
 * $RCSfile: DbActivityDefRightManager.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:26:04 $
 * <p>
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.database.form;

import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.inter.EIProcessDefVersion;
import net.ooder.bpm.engine.inter.EIProcessDefVersionManager;
import net.ooder.bpm.enums.form.FormNameEnum;
import net.ooder.bpm.enums.form.LockEnum;
import net.ooder.bpm.enums.form.MarkEnum;
import net.ooder.bpm.enums.process.FormRightEnums;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.CommonYesNoEnum;
import net.ooder.annotation.Attributetype;

import java.io.Serializable;
import java.util.Arrays;

public class DbProcessDefFormManager implements Serializable {

    private static final Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, DbProcessDefFormManager.class);

    private static DbProcessDefFormManager instance = new DbProcessDefFormManager();

    public static DbProcessDefFormManager getInstance() {
        return instance;
    }


    public DbProcessDefFormManager() {
    }


    public DbProcessDefForm createProcessDefForm(String processDefVersionId) {
        return new DbProcessDefForm(processDefVersionId);
    }


    public DbProcessDefForm loadByKey(String processDefVersionId) throws BPMException {
        DbProcessDefForm formDef = null;
        EIProcessDefVersionManager manager = EIProcessDefVersionManager.getInstance();
        EIProcessDefVersion processDefVersion = manager.loadByKey(processDefVersionId);
        if (processDefVersion == null) {
            return null;
        }

        formDef = createProcessDefForm(processDefVersionId);

        formDef.setAutoSave(CommonYesNoEnum.fromType(processDefVersion.getAttributeValue(Attributetype.PAGE.getType() + "." + FormRightEnums.AutoSave.getType())));
        formDef.setNoSqlType(CommonYesNoEnum.fromType(processDefVersion.getAttributeValue(Attributetype.PAGE.getType() + "." + FormRightEnums.NoSql.getType())));

        formDef.setLock(LockEnum.fromType(processDefVersion.getAttributeValue(Attributetype.PAGE.getType() + "." + FormRightEnums.Lock.getType())));
        formDef.setMark(MarkEnum.fromType(processDefVersion.getAttributeValue(Attributetype.PAGE.getType() + "." + FormRightEnums.Mark.getType())));

        Object mdforms = processDefVersion.getAttributeInterpretedValue(Attributetype.PAGE.getType() + "." + FormNameEnum.FORM.getType());
        if (mdforms != null) {
            formDef.setFormsNames(Arrays.asList(mdforms.toString().split(";")));
        }
        Object tableNames = processDefVersion.getAttributeInterpretedValue(Attributetype.DB.getType() + "." + FormNameEnum.TABLE.getType());
        if (tableNames != null) {
            formDef.setTableNames(Arrays.asList(tableNames.toString().split(";")));
        }
        return formDef;
    }

}


