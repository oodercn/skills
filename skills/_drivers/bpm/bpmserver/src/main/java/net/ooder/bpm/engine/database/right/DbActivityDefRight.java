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
package net.ooder.bpm.engine.database.right;

import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.inter.EIAttributeDef;
import net.ooder.bpm.enums.activitydef.ActivityDefPerformSequence;
import net.ooder.bpm.enums.activitydef.ActivityDefPerformtype;
import net.ooder.bpm.enums.activitydef.ActivityDefSpecialSendScope;
import net.ooder.bpm.enums.right.RightGroupEnums;
import net.ooder.common.CommonYesNoEnum;
import net.ooder.common.cache.CacheSizes;
import net.ooder.common.cache.Cacheable;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;

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
public class DbActivityDefRight implements Cacheable, Serializable {

    public static final String CANINSTEADSIGN_DEFAULT = "DEFAULT"; // 全流程默认值
    private static final Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, DbActivityDefRight.class);


    private String activityDefId = null;

    private ActivityDefPerformtype performType = ActivityDefPerformtype.SINGLE;

    private ActivityDefPerformSequence performSequence = ActivityDefPerformSequence.FIRST;

    private ActivityDefSpecialSendScope specialSendScope = null;

    private CommonYesNoEnum canInsteadSign = null;

    private CommonYesNoEnum canTakeBack = null;

    private CommonYesNoEnum canReSend = null;

    private String insteadSignSelectedId = null;

    private String performerSelectedId = null;

    private String readerSelectedId = null;

    private RightGroupEnums movePerformerTo = null;

    private RightGroupEnums moveSponsorTo = null;

    private RightGroupEnums moveReaderTo = null;

    private String surrogateId = null;

    private String surrogateName = null;


    private EIAttributeDef performerSelectedAtt = null;

    private EIAttributeDef readerSelectedAtt = null;

    private EIAttributeDef insteadSignSelectedAtt = null;

    DbActivityDefRight() {
        loadFromDb();
    }

    void loadFromDb() {

    }

    /**
     * @return Returns the activityDefId.
     */
    public String getActivityDefId() {
        return activityDefId;
    }

    /**
     * @param activityDefId
     *            The activityDefId to set.
     */
    public void setActivityDefId(String activityDefId) {
        this.activityDefId = activityDefId;
    }

    /**
     * @return Returns the canInsteadSign.
     */
    public CommonYesNoEnum getCanInsteadSign() {
        return canInsteadSign;
    }

    /**
     * @param canInsteadSign
     *            The canInsteadSign to set.
     */
    public void setCanInsteadSign(String canInsteadSign) {
        this.canInsteadSign = CommonYesNoEnum.fromType(canInsteadSign);
    }

    /**
     * @return Returns the doSequence.
     */
    public ActivityDefPerformSequence getPerformSequence() {
        if (performSequence == null) {
            performSequence = ActivityDefPerformSequence.FIRST;
        }
        return performSequence;
    }

    /**
     * @param doSequence
     *            The doSequence to set.
     */
    public void setPerformSequence(String doSequence) {
        this.performSequence = ActivityDefPerformSequence.fromType(doSequence);
    }

    /**
     * @return Returns the doType.
     */
    public ActivityDefPerformtype getPerformType() {
        if (performType == null) {
            performType = ActivityDefPerformtype.SINGLE;
        }
        return performType;
    }

    /**
     * @param doType
     *            The doType to set.
     */
    public void setPerformType(String doType) {

        log.info("dotype=" + doType);
        this.performType = ActivityDefPerformtype.fromType(doType);
        log.info("performType=" + this.performType);
    }

    /**
     * @return Returns the insteadSignSelected.
     */
    public String getInsteadSignSelectedId() {
        return insteadSignSelectedId;
    }

    /**
     * @param insteadSignSelected
     *            The insteadSignSelected to set.
     */
    public void setInsteadSignSelectedId(String insteadSignSelected) {
        this.insteadSignSelectedId = insteadSignSelected;
    }

    public EIAttributeDef getInsteadSignSelectedAtt() {
        return insteadSignSelectedAtt;
    }

    /**
     * @param insteadSignSelectedAtt
     *            The insteadSignSelectedAtt to set.
     */
    public void setInsteadSignSelectedAtt(EIAttributeDef insteadSignSelectedAtt) {
        this.insteadSignSelectedAtt = insteadSignSelectedAtt;
    }

    /**
     * @return Returns the movePerformerTo.
     */
    public RightGroupEnums getMovePerformerTo() {
        return movePerformerTo;
    }

    /**
     * @param movePerformerTo
     *            The movePerformerTo to set.
     */
    public void setMovePerformerTo(RightGroupEnums movePerformerTo) {
        this.movePerformerTo = movePerformerTo;
    }

    /**
     * @return Returns the moveReaderTo.
     */
    public RightGroupEnums getMoveReaderTo() {
        return moveReaderTo;
    }

    /**
     * @param moveReaderTo
     *            The moveReaderTo to set.
     */
    public void setMoveReaderTo(RightGroupEnums moveReaderTo) {
        this.moveReaderTo = moveReaderTo;
    }

    /**
     * @return Returns the performerSelectedId.
     */
    public String getPerformerSelectedId() {
        return performerSelectedId;
    }

    /**
     * @param performerSelectedId
     *            The performerSelectedId to set.
     */
    public void setPerformerSelectedId(String performerSelectedId) {
        this.performerSelectedId = performerSelectedId;
    }

    public EIAttributeDef getPerformerSelectedAtt() {
        return performerSelectedAtt;
    }

    /**
     * @param performerSelectedAtt
     *            The performerSelectedAtt to set.
     */
    public void setPerformerSelectedAtt(EIAttributeDef performerSelectedAtt) {
        this.performerSelectedAtt = performerSelectedAtt;
    }

    /**
     * @return Returns the readerSelectedId.
     */
    public String getReaderSelectedId() {
        return readerSelectedId;
    }

    /**
     * @param readerSelectedId
     *            The readerSelectedId to set.
     */
    public void setReaderSelectedId(String readerSelectedId) {
        this.readerSelectedId = readerSelectedId;
    }

    public EIAttributeDef getReaderSelectedAtt() {
        return readerSelectedAtt;
    }

    /**
     * @param readerSelectedAtt
     *            The readerSelectedAtt to set.
     */
    public void setReaderSelectedAtt(EIAttributeDef readerSelectedAtt) {
        this.readerSelectedAtt = readerSelectedAtt;
    }

    /**
     * @return Returns the specialSendScope.
     */
    public ActivityDefSpecialSendScope getSpecialSendScope() {
        return specialSendScope;
    }

    /**
     * @param specialSendScope
     *            The specialSendScope to set.
     */
    public void setSpecialSendScope(ActivityDefSpecialSendScope specialSendScope) {
        this.specialSendScope = specialSendScope;
    }

    /**
     * @return Returns the surrogateId.
     */
    public String getSurrogateId() {
        return surrogateId;
    }

    /**
     * @param surrogateId
     *            The surrogateId to set.
     */
    public void setSurrogateId(String surrogateId) {
        this.surrogateId = surrogateId;
    }

    /**
     * @return Returns the surrogateName.
     */
    public String getSurrogateName() {
        return surrogateName;
    }

    /**
     * @param surrogateName
     *            The surrogateName to set.
     */
    public void setSurrogateName(String surrogateName) {
        this.surrogateName = surrogateName;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.ooder.common.cache.Cacheable#getCachedSize()
     */
    public int getCachedSize() {

        int size = 0;

        size += CacheSizes.sizeOfString(activityDefId);
        size += CacheSizes.sizeOfString(performType.getType());
        size += CacheSizes.sizeOfString(performSequence.getType());
        size += CacheSizes.sizeOfString(specialSendScope.getType());
        size += CacheSizes.sizeOfString(canInsteadSign.getType());
        size += CacheSizes.sizeOfString(insteadSignSelectedId);
        size += CacheSizes.sizeOfString(performerSelectedId);
        size += CacheSizes.sizeOfString(readerSelectedId);
        size += CacheSizes.sizeOfString(movePerformerTo.getType());
        size += CacheSizes.sizeOfString(moveReaderTo.getType());
        size += CacheSizes.sizeOfString(moveSponsorTo.getType());
        size += CacheSizes.sizeOfString(surrogateId);
        size += CacheSizes.sizeOfString(surrogateName);
        size += CacheSizes.sizeOfString(this.canReSend.getType());
        size += CacheSizes.sizeOfString(this.canTakeBack.getType());
        return size;
    }

    public CommonYesNoEnum getCanReSend() {
        return canReSend;
    }

    public void setCanReSend(String canReSend) {
        this.canReSend = CommonYesNoEnum.fromType(canReSend);
    }

    public CommonYesNoEnum getCanTakeBack() {
        return canTakeBack;
    }

    public void setCanTakeBack(String canTakeBack) {
        this.canTakeBack = CommonYesNoEnum.fromType(canTakeBack);
    }

    public RightGroupEnums getMoveSponsorTo() {
        return moveSponsorTo;
    }

    public void setMoveSponsorTo(RightGroupEnums moveSponsorTo) {
        this.moveSponsorTo = moveSponsorTo;
    }


}


