/**
 * $RCSfile: DbActivityDef.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:45 $
 * <p>
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.inter.*;
import net.ooder.common.cache.CacheSizes;
import net.ooder.common.cache.Cacheable;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;

/**
 * <p>
 * Title: JDS绯荤粺绠＄悊绯荤粺
 * </p>
 * <p>
 * Description: 娲诲姩瀹氫箟鎺ュ彛鏁版嵁搴撳疄鐜?
 * </p>
 * <p>
 * The java object mapped on the Relation database table BPM_ACTIVITYDEF
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 *
 * @author lxl
 * @version 1.0

 */
public class DbActivityDef implements EIActivityDef, Cacheable, Serializable {
    private static final Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, DbActivityDef.class);

    private String activityDefId;

    private boolean activitydefId_is_modified = false;
    private boolean activitydefId_is_initialized = false;

    private String processDefId;

    private boolean processdefId_is_modified = false;

    private boolean processdefId_is_initialized = false;

    private String processDefVersionId;

    private boolean processdefVersionId_is_modified = false;

    private boolean processdefVersionId_is_initialized = false;

    private String name;

    private boolean defname_is_modified = false;

    private boolean defname_is_initialized = false;

    private String description;

    private boolean description_is_modified = false;

    private boolean description_is_initialized = false;

    private String position;

    private boolean position_is_modified = false;

    private boolean position_is_initialized = false;

    private String implementation;

    private boolean implementation_is_modified = false;

    private boolean implementation_is_initialized = false;

    private String execClass;

    private boolean execclass_is_modified = false;

    private boolean execclass_is_initialized = false;

    private int limit;

    private boolean limit_is_modified = false;

    private boolean limit_is_initialized = false;

    private int alertTime;

    private boolean alerttime_is_modified = false;

    private boolean alerttime_is_initialized = false;

    private String durationUnit;

    private boolean durationunit_is_modified = false;

    private boolean durationunit_is_initialized = false;

    private String deadlineOperation;

    private boolean deadlineoperation_is_modified = false;

    private boolean deadlineoperation_is_initialized = false;

    private String canRouteBack;

    private boolean cantakeback_is_modified = false;

    private boolean cantakeback_is_initialized = false;

    private String takeBackMethod;

    private boolean takebackmethod_is_modified = false;

    private boolean takebackmethod_is_initialized = false;

    private String canSpecialSend;

    private boolean canspecialsend_is_modified = false;

    private boolean canspecialsend_is_initialized = false;

    private String join;

    private boolean join_is_modified = false;

    private boolean join_is_initialized = false;

    private String split;

    private boolean split_is_modified = false;

    private boolean split_is_initialized = false;

    private boolean _isNew = true;

    private boolean _isAttributeModified = true;

    Map attributeTopMap = null; // store the top level attribute

    Map attributeIdMap = null; // store all attribute in this activity
    // definition
    List listeners = null; // store all listeners in this activity definition

    /**
     */
    DbActivityDef() {
    }

    /**
     * Getter method for activityDefId
     *
     * @return the value of activityDefId
     */
    public String getActivityDefId() {
        return activityDefId;
    }

    /**
     * Setter method for activityDefId
     *
     * @param newVal
     *            The new value to be assigned to activityDefId
     */
    public void setActivityDefId(String newVal) {
        if ((newVal != null && newVal.equals(this.activityDefId) == true)
                || (newVal == null && this.activityDefId == null))
            return;
        this.activityDefId = newVal;
        activitydefId_is_modified = true;
        activitydefId_is_initialized = true;
    }

    /**
     * Determine if the activityDefId is modified or not
     *
     * @return true if the field has been modified, false if the field has not
     *         been modified
     */
    public boolean isActivitydefIdModified() {
        return activitydefId_is_modified;
    }

    /**
     * Determine if the activityDefId has been initialized or not
     *
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isActivitydefIdInitialized() {
        return activitydefId_is_initialized;
    }

    /**
     * Getter method for processDefId
     *
     * @return the value of processDefId
     */
    public String getProcessDefId() {
        return processDefId;
    }

    /**
     * Setter method for processDefId
     *
     * @param newVal
     *            The new value to be assigned to processDefId
     */
    public void setProcessDefId(String newVal) {
        if ((newVal != null && newVal.equals(this.processDefId) == true)
                || (newVal == null && this.processDefId == null))
            return;
        this.processDefId = newVal;
        processdefId_is_modified = true;
        processdefId_is_initialized = true;
    }

    /**
     * Determine if the processDefId is modified or not
     *
     * @return true if the field has been modified, false if the field has not
     *         been modified
     */
    public boolean isProcessdefIdModified() {
        return processdefId_is_modified;
    }

    /**
     * Determine if the processDefId has been initialized or not
     *
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isProcessdefIdInitialized() {
        return processdefId_is_initialized;
    }

    /**
     * Getter method for processDefVersionId
     *
     * @return the value of processDefVersionId
     */
    public String getProcessDefVersionId() {
        return processDefVersionId;
    }

    /**
     * Setter method for processDefVersionId
     *
     * @param newVal
     *            The new value to be assigned to processDefVersionId
     */
    public void setProcessDefVersionId(String newVal) {
        if ((newVal != null && newVal.equals(this.processDefVersionId) == true)
                || (newVal == null && this.processDefVersionId == null))
            return;
        this.processDefVersionId = newVal;
        processdefVersionId_is_modified = true;
        processdefVersionId_is_initialized = true;
    }

    /**
     * Determine if the processDefVersionId is modified or not
     *
     * @return true if the field has been modified, false if the field has not
     *         been modified
     */
    public boolean isProcessdefVersionIdModified() {
        return processdefVersionId_is_modified;
    }

    /**
     * Determine if the processDefVersionId has been initialized or not
     *
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isProcessdefVersionIdInitialized() {
        return processdefVersionId_is_initialized;
    }

    /**
     * Getter method for defName
     *
     * @return the value of defName
     */
    public String getName() {
        return name;
    }

    /**
     * Setter method for defName
     *
     * @param newVal
     *            The new value to be assigned to defName
     */
    public void setName(String newVal) {
        if ((newVal != null && newVal.equals(this.name) == true)
                || (newVal == null && this.name == null))
            return;
        this.name = newVal;
        defname_is_modified = true;
        defname_is_initialized = true;
    }

    /**
     * Determine if the defName is modified or not
     *
     * @return true if the field has been modified, false if the field has not
     *         been modified
     */
    public boolean isDefnameModified() {
        return defname_is_modified;
    }

    /**
     * Determine if the defName has been initialized or not
     *
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isDefnameInitialized() {
        return defname_is_initialized;
    }

    /**
     * Getter method for description
     *
     * @return the value of description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter method for description
     *
     * @param newVal
     *            The new value to be assigned to description
     */
    public void setDescription(String newVal) {
        if ((newVal != null && newVal.equals(this.description) == true)
                || (newVal == null && this.description == null))
            return;
        this.description = newVal;
        description_is_modified = true;
        description_is_initialized = true;
    }

    /**
     * Determine if the description is modified or not
     *
     * @return true if the field has been modified, false if the field has not
     *         been modified
     */
    public boolean isDescriptionModified() {
        return description_is_modified;
    }

    /**
     * Determine if the description has been initialized or not
     *
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isDescriptionInitialized() {
        return description_is_initialized;
    }

    /**
     * Getter method for position
     *
     * @return the value of position
     */
    public String getPosition() {
        return position;
    }

    /**
     * Setter method for position
     *
     * @param newVal
     *            The new value to be assigned to position
     */
    public void setPosition(String newVal) {
        if ((newVal != null && newVal.equals(this.position) == true)
                || (newVal == null && this.position == null))
            return;
        this.position = newVal;
        position_is_modified = true;
        position_is_initialized = true;
    }

    /**
     * Determine if the position is modified or not
     *
     * @return true if the field has been modified, false if the field has not
     *         been modified
     */
    public boolean isPositionModified() {
        return position_is_modified;
    }

    /**
     * Determine if the position has been initialized or not
     *
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isPositionInitialized() {
        return position_is_initialized;
    }

    /**
     * Getter method for implementation
     *
     * @return the value of implementation
     */
    public String getImplementation() {
        return implementation;
    }

    /**
     * Setter method for implementation
     *
     * @param newVal
     *            The new value to be assigned to implementation
     */
    public void setImplementation(String newVal) {
        if ((newVal != null && newVal.equals(this.implementation) == true)
                || (newVal == null && this.implementation == null))
            return;
        this.implementation = newVal;
        implementation_is_modified = true;
        implementation_is_initialized = true;
    }

    /**
     * Determine if the implementation is modified or not
     *
     * @return true if the field has been modified, false if the field has not
     *         been modified
     */
    public boolean isImplementationModified() {
        return implementation_is_modified;
    }

    /**
     * Determine if the implementation has been initialized or not
     *
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isImplementationInitialized() {
        return implementation_is_initialized;
    }

    /**
     * Getter method for execClass
     *
     * @return the value of execClass
     */
    public String getExecClass() {
        return execClass;
    }

    /**
     * Setter method for execClass
     *
     * @param newVal
     *            The new value to be assigned to execClass
     */
    public void setExecClass(String newVal) {
        if ((newVal != null && newVal.equals(this.execClass) == true)
                || (newVal == null && this.execClass == null))
            return;
        this.execClass = newVal;
        execclass_is_modified = true;
        execclass_is_initialized = true;
    }

    /**
     * Determine if the execClass is modified or not
     *
     * @return true if the field has been modified, false if the field has not
     *         been modified
     */
    public boolean isExecclassModified() {
        return execclass_is_modified;
    }

    /**
     * Determine if the execClass has been initialized or not
     *
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isExecclassInitialized() {
        return execclass_is_initialized;
    }

    /**
     * Getter method for limit
     *
     * @return the value of limit
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Setter method for limit
     *
     * @param newVal
     *            The new value to be assigned to limit
     */
    public void setLimit(int newVal) {
        if (newVal == this.limit)
            return;
        this.limit = newVal;
        limit_is_modified = true;
        limit_is_initialized = true;
    }

    /**
     * Determine if the limit is modified or not
     *
     * @return true if the field has been modified, false if the field has not
     *         been modified
     */
    public boolean isLimitModified() {
        return limit_is_modified;
    }

    /**
     * Determine if the limit has been initialized or not
     *
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isLimitInitialized() {
        return limit_is_initialized;
    }

    /**
     * Getter method for alertTime
     *
     * @return the value of alertTime
     */
    public int getAlertTime() {
        return alertTime;
    }

    /**
     * Setter method for alertTime
     *
     * @param newVal
     *            The new value to be assigned to alertTime
     */
    public void setAlertTime(int newVal) {
        if (newVal == this.alertTime)
            return;
        this.alertTime = newVal;
        alerttime_is_modified = true;
        alerttime_is_initialized = true;
    }

    /**
     * Determine if the alertTime is modified or not
     *
     * @return true if the field has been modified, false if the field has not
     *         been modified
     */
    public boolean isAlerttimeModified() {
        return alerttime_is_modified;
    }

    /**
     * Determine if the alertTime has been initialized or not
     *
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isAlerttimeInitialized() {
        return alerttime_is_initialized;
    }

    /**
     * Getter method for durationUnit
     *
     * @return the value of durationUnit
     */
    public String getDurationUnit() {
        return durationUnit;
    }

    /**
     * Setter method for durationUnit
     *
     * @param newVal
     *            The new value to be assigned to durationUnit
     */
    public void setDurationUnit(String newVal) {
        if ((newVal != null && newVal.equals(this.durationUnit) == true)
                || (newVal == null && this.durationUnit == null))
            return;
        this.durationUnit = newVal;
        durationunit_is_modified = true;
        durationunit_is_initialized = true;
    }

    /**
     * Determine if the durationUnit is modified or not
     *
     * @return true if the field has been modified, false if the field has not
     *         been modified
     */
    public boolean isDurationunitModified() {
        return durationunit_is_modified;
    }

    /**
     * Determine if the durationUnit has been initialized or not
     *
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isDurationunitInitialized() {
        return durationunit_is_initialized;
    }

    /**
     * Getter method for deadLineOperation
     *
     * @return the value of deadLineOperation
     */
    public String getDeadlineOperation() {
        return deadlineOperation;
    }

    /**
     * Setter method for deadLineOperation
     *
     * @param newVal
     *            The new value to be assigned to deadLineOperation
     */
    public void setDeadlineOperation(String newVal) {
        if ((newVal != null && newVal.equals(this.deadlineOperation) == true)
                || (newVal == null && this.deadlineOperation == null))
            return;
        this.deadlineOperation = newVal;
        deadlineoperation_is_modified = true;
        deadlineoperation_is_initialized = true;
    }

    /**
     * Determine if the deadLineOperation is modified or not
     *
     * @return true if the field has been modified, false if the field has not
     *         been modified
     */
    public boolean isDeadlineoperationModified() {
        return deadlineoperation_is_modified;
    }

    /**
     * Determine if the deadLineOperation has been initialized or not
     *
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isDeadlineoperationInitialized() {
        return deadlineoperation_is_initialized;
    }

    /**
     * Getter method for canRouteBack
     *
     * @return the value of canRouteBack
     */
    public String getCanRouteBack() {
        return canRouteBack;
    }

    /**
     * Setter method for canRouteBack
     *
     * @param newVal
     *            The new value to be assigned to canRouteBack
     */
    public void setCanRouteBack(String newVal) {
        if ((newVal != null && newVal.equals(this.canRouteBack) == true)
                || (newVal == null && this.canRouteBack == null))
            return;
        this.canRouteBack = newVal;
        cantakeback_is_modified = true;
        cantakeback_is_initialized = true;
    }

    /**
     * Determine if the canRouteBack is modified or not
     *
     * @return true if the field has been modified, false if the field has not
     *         been modified
     */
    public boolean isCantakebackModified() {
        return cantakeback_is_modified;
    }

    /**
     * Determine if the canRouteBack has been initialized or not
     *
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isCantakebackInitialized() {
        return cantakeback_is_initialized;
    }

    /**
     * Getter method for takeBackMethod
     *
     * @return the value of takeBackMethod
     */
    public String getRouteBackMethod() {
        return takeBackMethod;
    }

    /**
     * Setter method for takeBackMethod
     *
     * @param newVal
     *            The new value to be assigned to takeBackMethod
     */
    public void setRouteBackMethod(String newVal) {
        if ((newVal != null && newVal.equals(this.takeBackMethod) == true)
                || (newVal == null && this.takeBackMethod == null))
            return;
        this.takeBackMethod = newVal;
        takebackmethod_is_modified = true;
        takebackmethod_is_initialized = true;
    }

    /**
     * Determine if the takeBackMethod is modified or not
     *
     * @return true if the field has been modified, false if the field has not
     *         been modified
     */
    public boolean isTakebackmethodModified() {
        return takebackmethod_is_modified;
    }

    /**
     * Determine if the takeBackMethod has been initialized or not
     *
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isTakebackmethodInitialized() {
        return takebackmethod_is_initialized;
    }

    /**
     * Getter method for canSpecialSend
     *
     * @return the value of canSpecialSend
     */
    public String getCanSpecialSend() {
        return canSpecialSend;
    }

    /**
     * Setter method for canSpecialSend
     *
     * @param newVal
     *            The new value to be assigned to canSpecialSend
     */
    public void setCanSpecialSend(String newVal) {
        if ((newVal != null && newVal.equals(this.canSpecialSend) == true)
                || (newVal == null && this.canSpecialSend == null))
            return;
        this.canSpecialSend = newVal;
        canspecialsend_is_modified = true;
        canspecialsend_is_initialized = true;
    }

    /**
     * Determine if the canSpecialSend is modified or not
     *
     * @return true if the field has been modified, false if the field has not
     *         been modified
     */
    public boolean isCanspecialsendModified() {
        return canspecialsend_is_modified;
    }

    /**
     * Determine if the canSpecialSend has been initialized or not
     *
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isCanspecialsendInitialized() {
        return canspecialsend_is_initialized;
    }

    /**
     * Getter method for join
     *
     * @return the value of join
     */
    public String getJoin() {
        return join;
    }

    /**
     * Setter method for join
     *
     * @param newVal
     *            The new value to be assigned to join
     */
    public void setJoin(String newVal) {
        if ((newVal != null && newVal.equals(this.join) == true)
                || (newVal == null && this.join == null))
            return;
        this.join = newVal;
        join_is_modified = true;
        join_is_initialized = true;
    }

    /**
     * Determine if the join is modified or not
     *
     * @return true if the field has been modified, false if the field has not
     *         been modified
     */
    public boolean isJoinModified() {
        return join_is_modified;
    }

    /**
     * Determine if the join has been initialized or not
     *
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isJoinInitialized() {
        return join_is_initialized;
    }

    /**
     * Getter method for split
     *
     * @return the value of split
     */
    public String getSplit() {
        return split;
    }

    /**
     * Setter method for split
     *
     * @param newVal
     *            The new value to be assigned to split
     */
    public void setSplit(String newVal) {
        if ((newVal != null && newVal.equals(this.split) == true)
                || (newVal == null && this.split == null))
            return;
        this.split = newVal;
        split_is_modified = true;
        split_is_initialized = true;
    }

    /**
     * Determine if the split is modified or not
     *
     * @return true if the field has been modified, false if the field has not
     *         been modified
     */
    public boolean isSplitModified() {
        return split_is_modified;
    }

    /**
     * Determine if the split has been initialized or not
     *
     * @return true if the field has been initialized, false otherwise
     */
    public boolean isSplitInitialized() {
        return split_is_initialized;
    }

    /**
     * Determine if the current object is new or not
     *
     * @return true if the current object is new, false if the object is not new
     */
    public boolean isNew() {
        return _isNew;
    }

    /**
     * Specify to the object if he has to been set as new or not
     *
     * @param isNew
     *            the boolean value to be assigned to the isNew field
     */
    public void setIsNew(boolean isNew) {
        this._isNew = isNew;
    }

    /**
     * Determine if the object has been modified since the last time this method
     * was called or since the creation of the object
     *
     * @return true if the object has been modified, false if the object has not
     *         been modified
     */
    public boolean isModified() {
        return activitydefId_is_modified || processdefId_is_modified
                || processdefVersionId_is_modified || defname_is_modified
                || description_is_modified || position_is_modified
                || implementation_is_modified || execclass_is_modified
                || limit_is_modified || alerttime_is_modified
                || durationunit_is_modified || deadlineoperation_is_modified
                || cantakeback_is_modified || takebackmethod_is_modified
                || canspecialsend_is_modified || join_is_modified
                || split_is_modified;
    }

    /**
     * Reset object modification status to "not modified"
     */
    public void resetIsModified() {
        activitydefId_is_modified = false;
        processdefId_is_modified = false;
        processdefVersionId_is_modified = false;
        defname_is_modified = false;
        description_is_modified = false;
        position_is_modified = false;
        implementation_is_modified = false;
        execclass_is_modified = false;
        limit_is_modified = false;
        alerttime_is_modified = false;
        durationunit_is_modified = false;
        deadlineoperation_is_modified = false;
        cantakeback_is_modified = false;
        takebackmethod_is_modified = false;
        canspecialsend_is_modified = false;
        join_is_modified = false;
        split_is_modified = false;
        _isAttributeModified = false;
    }

    /**
     * Copy the passed bean into the current bean
     *
     * @param bean
     *            the bean to copy into the current bean
     */
    public void copy(DbActivityDef bean) {
        setActivityDefId(bean.getActivityDefId());
        setProcessDefId(bean.getProcessDefId());
        setProcessDefVersionId(bean.getProcessDefVersionId());
        setName(bean.getName());
        setDescription(bean.getDescription());
        setPosition(bean.getPosition());
        setImplementation(bean.getImplementation());
        setExecClass(bean.getExecClass());
        setLimit(bean.getLimit());
        setAlertTime(bean.getAlertTime());
        setDurationUnit(bean.getDurationUnit());
        setDeadlineOperation(bean.getDeadlineOperation());
        setCanRouteBack(bean.getCanRouteBack());
        setRouteBackMethod(bean.getRouteBackMethod());
        setCanSpecialSend(bean.getCanSpecialSend());
        setJoin(bean.getJoin());
        setSplit(bean.getSplit());
    }

    /**
     * Return the object string representation
     *
     * @return the object as a string
     */
    public String toString() {
        return "\n[BPM_ACTIVITYDEF] "
                + "\n - BPM_ACTIVITYDEF.ACTIVITYDEF_ID = "
                + (activitydefId_is_initialized ? ("["
                + activityDefId.toString() + "]") : "not initialized")
                + ""
                + "\n - BPM_ACTIVITYDEF.PROCESSDEF_ID = "
                + (processdefId_is_initialized ? ("[" + processDefId.toString() + "]")
                : "not initialized")
                + ""
                + "\n - BPM_ACTIVITYDEF.PROCESSDEF_VERSION_ID = "
                + (processdefVersionId_is_initialized ? ("["
                + processDefVersionId.toString() + "]")
                : "not initialized")
                + ""
                + "\n - BPM_ACTIVITYDEF.DEFNAME = "
                + (defname_is_initialized ? ("[" + name.toString() + "]")
                : "not initialized")
                + ""
                + "\n - BPM_ACTIVITYDEF.DESCRIPTION = "
                + (description_is_initialized ? ("[" + description.toString() + "]")
                : "not initialized")
                + ""
                + "\n - BPM_ACTIVITYDEF.POSITION = "
                + (position_is_initialized ? ("[" + position.toString() + "]")
                : "not initialized")
                + ""
                + "\n - BPM_ACTIVITYDEF.IMPLEMENTATION = "
                + (implementation_is_initialized ? ("["
                + implementation.toString() + "]") : "not initialized")
                + ""
                + "\n - BPM_ACTIVITYDEF.EXECCLASS = "
                + (execclass_is_initialized ? ("[" + execClass.toString() + "]")
                : "not initialized")
                + ""
                + "\n - BPM_ACTIVITYDEF.LIMIT = "
                + (limit_is_initialized ? ("[" + limit + "]")
                : "not initialized")
                + ""
                + "\n - BPM_ACTIVITYDEF.ALERTTIME = "
                + (alerttime_is_initialized ? ("[" + alertTime + "]")
                : "not initialized")
                + ""
                + "\n - BPM_ACTIVITYDEF.DURATIONUNIT = "
                + (durationunit_is_initialized ? ("[" + durationUnit.toString() + "]")
                : "not initialized")
                + ""
                + "\n - BPM_ACTIVITYDEF.DEADLINEOPERATION = "
                + (deadlineoperation_is_initialized ? ("["
                + deadlineOperation.toString() + "]")
                : "not initialized")
                + ""
                + "\n - BPM_ACTIVITYDEF.CANROUTEBACK = "
                + (cantakeback_is_initialized ? ("[" + canRouteBack.toString() + "]")
                : "not initialized")
                + ""
                + "\n - BPM_ACTIVITYDEF.TAKEBACKMETHOD = "
                + (takebackmethod_is_initialized ? ("["
                + takeBackMethod.toString() + "]") : "not initialized")
                + ""
                + "\n - BPM_ACTIVITYDEF.CANSPECIALSEND = "
                + (canspecialsend_is_initialized ? ("["
                + canSpecialSend.toString() + "]") : "not initialized")
                + ""
                + "\n - BPM_ACTIVITYDEF.JOIN = "
                + (join_is_initialized ? ("[" + join.toString() + "]")
                : "not initialized")
                + ""
                + "\n - BPM_ACTIVITYDEF.SPLIT = "
                + (split_is_initialized ? ("[" + split.toString() + "]")
                : "not initialized") + "";
    }

    /*
     * (non-Javadoc)
     *
     * @see net.ooder.bpm.engine.inter.EIActivityDef#getListeners()
     */
    public List getListeners() {
        if (listeners == null) {
            listeners = new ArrayList();
            DbActivityDefManager manager = (DbActivityDefManager) EIActivityDefManager
                    .getInstance();

            try {
                manager.loadListener(this);

            } catch (BPMException e) {
                log.error("load the listeners in activity definition "
                        + this.activityDefId + " failed!", e);
                return null;
            }
        }
        return new ArrayList(listeners);
    }

    public void setListeners(List list) {
        listeners = list;
    }

    /**
     * @return
     */
    public boolean isListenersModified() {
        if (listeners == null) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.ooder.bpm.engine.inter.EIActivityDef#getOutRouteIds()
     */
    public List<String> getOutRouteIds() throws BPMException {
        EIRouteDefManager manager = EIRouteDefManager.getInstance();

        List<EIRouteDef> routeDefs = manager.getOutRoutesFromActivity(this.getActivityDefId());
        List<String> routeIds = new ArrayList<String>();
        for (EIRouteDef eiRouteDef : routeDefs) {
            routeIds.add(eiRouteDef.getRouteDefId());
        }

        return routeIds;

    }

    /*
     * (non-Javadoc)
     *
     * @see net.ooder.bpm.engine.inter.EIActivityDef#getInRouteIds()
     */
    public List<String> getInRouteIds() throws BPMException {
        EIRouteDefManager manager = EIRouteDefManager.getInstance();
        List<EIRouteDef> routeDefs = manager.getInRoutesToActivity(this.getActivityDefId());
        List<String> routeIds = new ArrayList<String>();
        for (EIRouteDef eiRouteDef : routeDefs) {
            routeIds.add(eiRouteDef.getRouteDefId());
        }

        return routeIds;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.ooder.bpm.engine.inter.EIProcessDefVersion#getAttributeValue(java.lang.String)
     */
    public String getAttributeValue(String name) {
        EIAttributeDef attDef = getAttribute(name);
        if (attDef == null) {
            return null;
        }
        return attDef.getValue();
    }

    /*
     * (non-Javadoc)
     *
     * @see net.ooder.bpm.engine.inter.EIProcessDefVersion#getAttributeValue(java.lang.String)
     */
    public Object getAttributeInterpretedValue(String name) {
        EIAttributeDef attDef = getAttribute(name);
        if (attDef == null) {
            return null;
        }
        return attDef.getInterpretedValue();
    }

    /**
     * 鍙栧緱灞炴€у€硷紝褰撻渶瑕佸彇寰楀甫灞傛鍏崇郴鐨勫睘鎬у€兼椂锛屽睘鎬у悕绉板湪姣忓眰闂村姞"."鍒嗗壊 <br>
     * 渚嬪锛?br>
     * "Form1.field1.value" - 鍙栧緱鏈€椤跺眰灞炴€т腑鍚嶇О涓篺orm1鐨?
     *
     * @param name
     * @return
     */
    public EIAttributeDef getAttribute(String name) {
        if (name != null) {
            name = name.toUpperCase();
        }
        if (attributeIdMap == null && attributeTopMap == null) {
            attributeIdMap = new HashMap();
            attributeTopMap = new HashMap();
            DbActivityDefManager manager = (DbActivityDefManager) EIActivityDefManager
                    .getInstance();
            try {
                manager.loadAttribute(this);
            } catch (BPMException e) {
                log.error("load the attributes in activity definition "
                        + this.activityDefId + " failed!", e);
                return null;
            }
        }


        StringTokenizer st = new StringTokenizer(name, ".");
        DbAttributeDef subAtt = null;
        while (st.hasMoreTokens()) {
            String subname = st.nextToken();
            if (subAtt == null) { // top level
                subAtt = (DbAttributeDef) attributeTopMap.get(subname);
            } else {
                subAtt = (DbAttributeDef) subAtt.getChild(subname);
            }

            if (subAtt == null) {
                return null; // not found
            }
        }
        return subAtt;
    }

    /**
     * 鍙栧緱鏈€椤跺眰鐨勫睘鎬э紙娌℃湁鐖跺睘鎬х殑灞炴€э級
     *
     * @return
     */
    public List getTopAttribute() {
        if (attributeIdMap == null && attributeTopMap == null) {
            attributeIdMap = new HashMap();
            attributeTopMap = new HashMap();
            DbActivityDefManager manager = (DbActivityDefManager) EIActivityDefManager
                    .getInstance();
            try {
                manager.loadAttribute(this);
            } catch (BPMException e) {
                log.error("load the attributes in activity definition "
                        + this.activityDefId + " failed!", e);
                return new ArrayList();
            }
        }

        List list = new ArrayList(attributeTopMap.values());
        List result = new ArrayList();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            EIAttributeDef attr = (EIAttributeDef) it.next();
            result.addAll(attr.getChildren());
        }
        return result;

    }

    /*
     * (non-Javadoc)
     *
     * @see net.ooder.bpm.engine.inter.EIActivityDef#getAllAttribute()
     */
    public List getAllAttribute() {
        if (attributeIdMap == null && attributeTopMap == null) {
            attributeIdMap = new HashMap();
            attributeTopMap = new HashMap();
            DbActivityDefManager manager = (DbActivityDefManager) EIActivityDefManager
                    .getInstance();
            try {
                manager.loadAttribute(this);
            } catch (BPMException e) {
                log.error("load the attributes in activity definition "
                        + this.activityDefId + " failed!", e);
                return new ArrayList();
            }
        }

        // 修复：同时返回顶层属性和子属性
        // 顶层属性（如WORKFLOW）存储在attributeTopMap中
        // 子属性（如positionCoord）存储在attributeIdMap中
        // 保存时需要先保存顶层属性，再保存子属性（因为子属性有parentId引用）
        List result = new ArrayList();
        
        // 先添加顶层属性
        if (attributeTopMap != null) {
            for (Object att : attributeTopMap.values()) {
                result.add(att);
            }
        }
        
        // 再添加子属性
        if (attributeIdMap != null) {
            for (Object att : attributeIdMap.values()) {
                result.add(att);
            }
        }
        
        return result;
    }

    public void clearAttribute() {

        if (attributeIdMap == null && attributeTopMap == null) {
            // 涓嶈鍙栨暟鎹簱锛岀洿鎺ユ竻绌?
            attributeIdMap = new HashMap();
            attributeTopMap = new HashMap();
        }
        attributeIdMap.clear();
        attributeTopMap.clear();

        _isAttributeModified = true;
    }

    public void setAttribute(String parentName, EIAttributeDef attDef)
            throws BPMException {
        if (parentName != null) {
            //parentName = parentName.toUpperCase();
            //parentName = parentName;
        }
        // load first
        if (attributeIdMap == null && attributeTopMap == null) {
            attributeIdMap = new HashMap();
            attributeTopMap = new HashMap();
            DbActivityDefManager manager = (DbActivityDefManager) EIActivityDefManager
                    .getInstance();

            manager.loadAttribute(this);
        }

        EIAttributeDef parentAtt = null;

        if (parentName != null && !parentName.equals("")) {
            parentAtt = getAttribute(parentName);
            if (parentAtt == null) {
                if (parentName.indexOf(".") == -1) { // top level
                    parentAtt = new DbAttributeDef();
                    parentAtt.setName(parentName);
                    parentAtt.setType(parentName);
                    setAttribute(null, parentAtt);
                } else {
                    // error: parentAtt not in this activity definition!
                    throw new BPMException(
                            "parentAtt not in this activity definition! parentAtt:"
                                    + parentName + ", attName:"
                                    + attDef.getName());
                }
            }
            attDef.setType(parentAtt.getType());
        }

        if (parentAtt != null) {
            // sub attribute
            EIAttributeDef oldAtt = (EIAttributeDef) parentAtt.getChild(attDef
                    .getName());
            if (oldAtt != null) { // exist same name attribute in this tree
                attributeIdMap.remove(oldAtt.getId()); // remove it from all
                // attribute map
            }
            parentAtt.addChild(attDef); // change the new attribute definition!
            attributeIdMap.put(attDef.getId(), attDef);
        } else {
            // top level add to top map AND attributeIdMap
            // 修复：顶层属性也需要添加到attributeIdMap，否则saveAttribute不会保存它
            attributeTopMap.put(attDef.getName().toUpperCase(), attDef);
            attributeIdMap.put(attDef.getId(), attDef);
        }

        _isAttributeModified = true;
    }

    public boolean isAttributeModified() {
        if (attributeIdMap == null && attributeTopMap == null) {
            return false;
        }
        return activitydefId_is_modified || _isAttributeModified;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.ooder.common.cache.Cacheable#getCachedSize()
     */
    public int getCachedSize() {
        int size = 0;

        size += CacheSizes.sizeOfString(processDefId);
        size += CacheSizes.sizeOfString(activityDefId);
        size += CacheSizes.sizeOfString(description);
        size += CacheSizes.sizeOfInt(); // alertTime
        size += CacheSizes.sizeOfInt(); // limit
        size += CacheSizes.sizeOfString(canSpecialSend);
        size += CacheSizes.sizeOfString(canRouteBack);
        size += CacheSizes.sizeOfString(deadlineOperation);
        size += CacheSizes.sizeOfString(name);
        size += CacheSizes.sizeOfString(durationUnit);
        size += CacheSizes.sizeOfString(execClass);
        size += CacheSizes.sizeOfString(implementation);
        size += CacheSizes.sizeOfString(join);
        size += CacheSizes.sizeOfString(position);
        size += CacheSizes.sizeOfString(processDefVersionId);
        size += CacheSizes.sizeOfString(split);
        size += CacheSizes.sizeOfString(takeBackMethod);

        size += CacheSizes.sizeOfBoolean() * 13;
        return size;

    }

    public boolean equals(Object o) {
        String uuid;
        if (o instanceof java.lang.String) {
            uuid = (String) o;
        } else if (o instanceof DbActivityDef) {
            uuid = ((DbActivityDef) o).activityDefId;
        } else {
            return false;
        }
        return uuid.equalsIgnoreCase(this.processDefId);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.ooder.bpm.engine.inter.EIActivityDef#getProcessDef()
     */
    public EIProcessDef getProcessDef() throws BPMException {
        return EIProcessDefManager.getInstance().loadByKey(processDefId);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.ooder.bpm.engine.inter.EIActivityDef#getProcessDefVersion()
     */
    public EIProcessDefVersion getProcessDefVersion() throws BPMException {
        return EIProcessDefVersionManager.getInstance().loadByKey(
                processDefVersionId);
    }


}


