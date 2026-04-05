/**
 * $RCSfile: DbActivityDefFormManager.java,v $
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
import net.ooder.bpm.engine.inter.EIActivityDef;
import net.ooder.bpm.engine.inter.EIActivityDefManager;
import net.ooder.bpm.engine.inter.EIAttributeDef;
import net.ooder.bpm.engine.inter.EIAttributeDefManager;
import net.ooder.common.FormulaType;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.CommonYesNoEnum;
import net.ooder.annotation.AttributeInterpretClass;
import net.ooder.annotation.AttributeName;
import net.ooder.annotation.Attributetype;

import java.io.Serializable;
import java.util.UUID;

public class DbActivityDefFormManager implements Serializable {

    private static final Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, DbActivityDefFormManager.class);

    private static DbActivityDefFormManager instance = new DbActivityDefFormManager();

    public static DbActivityDefFormManager getInstance() {
        return instance;
    }


    public DbActivityDefFormManager() {

    }



    public DbActivityDefForm createActivityDefForm() {
        return new DbActivityDefForm();
    }

    public DbActivityDefForm loadByKey(String activityDefId) throws BPMException {
        DbActivityDefForm rightDef = null;
        EIActivityDefManager manager = EIActivityDefManager.getInstance();
        EIActivityDef activityDef = manager.loadByKey(activityDefId);
        if (activityDef == null) {
            return null;
        }
        rightDef = createActivityDefForm();
        rightDef.setActivityDefId(activityDefId);
        rightDef.setTableSelectedId(activityDef.getAttributeValue(FormulaType.ActionSelectedID.getBaseType().getType() + "." + FormulaType.TableSelectedID.getType()));
        rightDef.setTableSelectedAtt(activityDef.getAttribute(FormulaType.TableSelectedID.getBaseType().getType() + "." +  FormulaType.TableSelectedID.getType()));
        rightDef.setEscomSelectedId(activityDef.getAttributeValue(FormulaType.ESDCOM.getBaseType().getType() + "." + FormulaType.ESDCOM.getType()));
        rightDef.setEscomSelectedAtt(activityDef.getAttribute(FormulaType.ESDCOM.getBaseType().getType() + "." +  FormulaType.ESDCOM.getType()));
        rightDef.setActionSelectedId(activityDef.getAttributeValue( FormulaType.ActionSelectedID.getBaseType().getType() + "." + FormulaType.ActionSelectedID));
        rightDef.setActionSelectedAtt(activityDef.getAttribute(FormulaType.ActionSelectedID.getBaseType().getType() + "." + FormulaType.ActionSelectedID));
        return rightDef;
    }

    private EIAttributeDef createRightAttribute(AttributeName name, String value) {
        EIAttributeDefManager attriuteDefManager = EIAttributeDefManager.getInstance();

        EIAttributeDef eiAtt = attriuteDefManager.createAttributeDef();
        eiAtt.setId(UUID.randomUUID().toString());
        eiAtt.setInterpretClass(AttributeInterpretClass.STRING.getType());
        eiAtt.setCanInstantiate(CommonYesNoEnum.NO.getType());
        eiAtt.setName(name.getType());
        eiAtt.setValue(value);
        eiAtt.setType(Attributetype.PAGE.getType());

        return eiAtt;
    }


}


