/**
 * $RCSfile: DbParticipantSelect.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:26:04 $
 * <p>
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.database.expression;

import net.ooder.common.FormulaType;
import net.ooder.common.cache.CacheSizes;
import net.ooder.esb.config.manager.ExpressionParameter;
import net.ooder.esd.manager.formula.ParticipantSelect;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 工作流系统人员选择公式
 * </p>
 * <p>
 * The java object mapped on the Relation database table RT_PARTICIPANT_SELECT
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
public class DbParticipantSelect implements ParticipantSelect {

    private String participantSelectId;


    private boolean participantSelectId_is_modified = false;

    private boolean participantSelectId_is_initialized = false;

    private String selectName;

    private boolean selectname_is_modified = false;

    private boolean selectname_is_initialized = false;

    private FormulaType formulaType;

    private boolean type_is_modified = false;

    private boolean type_is_initialized = false;

    private String selectenName;

    private boolean selectenname_is_modified = false;

    private boolean selectenname_is_initialized = false;

    private String formula;

    private boolean formula_is_modified = false;

    private boolean formula_is_initialized = false;

    private String selectDesc;

    private boolean selectdesc_is_modified = false;

    private boolean selectdesc_is_initialized = false;

    private boolean _isNew = true;

    List<ExpressionParameter> parameterList = null;

    /**
     */
    DbParticipantSelect() {
    }

    /**
     * Getter method for participantSelectId
     *
     * @return the value of participantSelectId
     */
    public String getParticipantSelectId() {
        return participantSelectId;
    }

    /**
     * Setter method for participantSelectId
     *
     * @param newVal The new value to be assigned to participantSelectId
     */
    public void setParticipantSelectId(String newVal) {
        if ((newVal != null && newVal.equals(this.participantSelectId) == true) || (newVal == null && this.participantSelectId == null))
            return;
        this.participantSelectId = newVal;
        participantSelectId_is_modified = true;
        participantSelectId_is_initialized = true;
    }

    /**
     * Determine if the participantSelectId is modified or not
     *
     * @return true if the field has been modified, false if the field has not been modified
     */

    public boolean isParticipantSelectIdModified() {
        return participantSelectId_is_modified;
    }

    /**
     * Determine if the participantSelectId has been initialized or not
     *
     * @return true if the field has been initialized, false otherwise
     */

    public boolean isParticipantSelectIdInitialized() {
        return participantSelectId_is_initialized;
    }

    /**
     * Getter method for selectname
     *
     * @return the value of selectname
     */
    public String getSelectName() {
        return selectName;
    }

    /**
     * Setter method for selectname
     *
     * @param newVal The new value to be assigned to selectname
     */
    public void setSelectName(String newVal) {
        if ((newVal != null && newVal.equals(this.selectName) == true) || (newVal == null && this.selectName == null))
            return;
        this.selectName = newVal;
        selectname_is_modified = true;
        selectname_is_initialized = true;
    }

    /**
     * Getter method for selectname
     *
     * @return the value of selectname
     */

    public String getSelectenName() {
        return selectenName;
    }

    /**
     * Setter method for selectname
     *
     * @param newVal The new value to be assigned to selectname
     */
    public void setSelectenName(String newVal) {
        if ((newVal != null && newVal.equals(this.selectenName) == true) || (newVal == null && this.selectenName == null))
            return;
        this.selectenName = newVal;
        selectenname_is_modified = true;
        selectenname_is_initialized = true;
    }

    /**
     * Determine if the selectenname is modified or not
     *
     * @return true if the field has been modified, false if the field has not been modified
     */

    public boolean isSelectennameModified() {
        return selectenname_is_modified;
    }

    /**
     * Determine if the selectname is modified or not
     *
     * @return true if the field has been modified, false if the field has not been modified
     */

    public boolean isSelectnameModified() {
        return selectname_is_modified;
    }

    /**
     * Determine if the selectname has been initialized or not
     *
     * @return true if the field has been initialized, false otherwise
     */

    public boolean isSelectnameInitialized() {
        return selectname_is_initialized;
    }

    /**
     * Determine if the selectenname has been initialized or not
     *
     * @return true if the field has been initialized, false otherwise
     */

    public boolean isSelectennameInitialized() {
        return selectenname_is_initialized;
    }

    /**
     * Getter method for formula
     *
     * @return the value of formula
     */
    public String getFormula() {
        return formula;
    }

    /**
     * Setter method for formula
     *
     * @param newVal The new value to be assigned to formula
     */
    public void setFormula(String newVal) {
        if ((newVal != null && newVal.equals(this.formula) == true) || (newVal == null && this.formula == null))
            return;
        this.formula = newVal;
        formula_is_modified = true;
        formula_is_initialized = true;
    }

    /**
     * Determine if the formula is modified or not
     *
     * @return true if the field has been modified, false if the field has not been modified
     */

    public boolean isFormulaModified() {
        return formula_is_modified;
    }

    /**
     * Determine if the formula has been initialized or not
     *
     * @return true if the field has been initialized, false otherwise
     */

    public boolean isFormulaInitialized() {
        return formula_is_initialized;
    }

    /**
     * Getter method for type
     *
     * @return the value of type
     */
    public FormulaType getFormulaType() {
        return formulaType;
    }

    /**
     * Setter method for type
     *
     * @param newVal The new value to be assigned to type
     */
    public void setFormulaType(FormulaType newVal) {
        if ((newVal != null && newVal.equals(this.formulaType) == true) || (newVal == null && this.formulaType == null))
            return;
        this.formulaType = newVal;
        type_is_modified = true;
        type_is_initialized = true;
    }

    /**
     * Determine if the type is modified or not
     *
     * @return true if the field has been modified, false if the field has not been modified
     */

    public boolean isTypeModified() {
        return type_is_modified;
    }

    /**
     * Determine if the type has been initialized or not
     *
     * @return true if the field has been initialized, false otherwise
     */

    public boolean isTypeInitialized() {
        return type_is_initialized;
    }

    /**
     * Getter method for selectdesc
     *
     * @return the value of selectdesc
     */
    public String getSelectDesc() {
        return selectDesc;
    }

    /**
     * Setter method for selectdesc
     *
     * @param newVal The new value to be assigned to selectdesc
     */
    public void setSelectDesc(String newVal) {
        if ((newVal != null && newVal.equals(this.selectDesc) == true) || (newVal == null && this.selectDesc == null))
            return;
        this.selectDesc = newVal;
        selectdesc_is_modified = true;
        selectdesc_is_initialized = true;
    }

    /**
     * Determine if the selectdesc is modified or not
     *
     * @return true if the field has been modified, false if the field has not been modified
     */

    public boolean isSelectdescModified() {
        return selectdesc_is_modified;
    }

    /**
     * Determine if the selectdesc has been initialized or not
     *
     * @return true if the field has been initialized, false otherwise
     */

    public boolean isSelectdescInitialized() {
        return selectdesc_is_initialized;
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
     * @param isNew the boolean value to be assigned to the isNew field
     */
    public void setIsNew(boolean isNew) {
        this._isNew = isNew;
    }

    /**
     * Determine if the object has been modified since the last time this method was called or since the creation of the
     * object
     *
     * @return true if the object has been modified, false if the object has not been modified
     */

    public boolean isModified() {
        return participantSelectId_is_modified || selectname_is_modified || selectenname_is_modified || type_is_modified || formula_is_modified || selectdesc_is_modified;
    }

    /**
     * Reset object modification status to "not modified"
     */
    public void resetIsModified() {
        participantSelectId_is_modified = false;
        selectname_is_modified = false;
        selectenname_is_modified = false;
        type_is_modified = false;
        formula_is_modified = false;
        selectdesc_is_modified = false;
    }

    /**
     * Copy the passed bean into the current bean
     *
     * @param bean the bean to copy into the current bean
     */
    public void copy(DbParticipantSelect bean) {
        setParticipantSelectId(bean.getParticipantSelectId());
        setSelectName(bean.getSelectName());
        setSelectenName(bean.getSelectenName());
        setFormula(bean.getFormula());
        setFormulaType(bean.getFormulaType());
        setSelectDesc(bean.getSelectDesc());
    }

    public List<ExpressionParameter> getParameterList() {
        if (parameterList == null) {
            loadParameter();
        }
        return new ArrayList<ExpressionParameter>(parameterList);
    }

    public void setParameterList(List<ExpressionParameter> parameterList) {
        this.parameterList = new ArrayList<ExpressionParameter>(parameterList);
    }


    public DbExpressionParameter getParameter(String parameterId) {
        // add by chenjie.
        if (parameterList == null) {
            loadParameter();
        }
        for (int i = 0; i < this.parameterList.size(); i++) {
            DbExpressionParameter parameter = (DbExpressionParameter) this.parameterList.get(i);
            if (parameter.getParameterId().equalsIgnoreCase(parameterId)) {
                return parameter;
            }
        }
        return null;
    }


    public void saveParameter(DbExpressionParameter newParameter) {

        if (parameterList == null) {
            loadParameter();
        }

        if (newParameter != null && newParameter.getParameterId() != null) {
            DbExpressionParameter parameter = getParameter(newParameter.getParameterId());
            if (parameter != null) {
                parameterList.remove(parameter);
            }
            parameterList.add(newParameter);

        }

    }

    public void addParameter(DbExpressionParameter parameter) {
        if (parameterList == null) {
            loadParameter();
        }
        this.parameterList.add(parameter);
    }

    public void removeParameter(DbExpressionParameter parameter) {
        if (parameterList == null) {
            loadParameter();
        }
        this.parameterList.remove(parameter);
    }

    private void loadParameter() {
        if (parameterList == null) {
            parameterList = new ArrayList();
        } else {
            parameterList.clear();
        }
        DbParticipantSelectManager.getInstance().loadParameter(this);
    }

    /**
     * Return the object string representation
     *
     * @return the object as a string
     */
    public String toString() {
        return "\n[RT_PARTICIPANT_SELECT] " + "\n - RT_PARTICIPANT_SELECT.PARTICIPANT_SELECT_ID = " + (participantSelectId_is_initialized ? ("[" + participantSelectId.toString() + "]") : "not initialized") + ""
                + "\n - RT_PARTICIPANT_SELECT.SELECTNAME = " + (selectname_is_initialized ? ("[" + selectName.toString() + "]") : "not initialized") + "" + "\n - RT_PARTICIPANT_SELECT.SELECTENNAME = "
                + (selectenname_is_initialized ? ("[" + selectenName.toString() + "]") : "not initialized") + "" + "\n - RT_PARTICIPANT_SELECT.FORMULA = " + (formula_is_initialized ? ("[" + formula.toString() + "]") : "not initialized")
                + "" + "\n - RT_PARTICIPANT_SELECT.TYPE = " + (type_is_initialized ? ("[" + formulaType.toString() + "]") : "not initialized") + "" + "\n - RT_PARTICIPANT_SELECT.SELECTDESC = "
                + (selectdesc_is_initialized ? ("[" + selectDesc.toString() + "]") : "not initialized") + "";
    }

    /*
     * (non-Javadoc)
     *
     * @see net.ooder.common.cache.Cacheable#getCachedSize()
     */

    public int getCachedSize() {

        int size = 0;
        size += CacheSizes.sizeOfString(participantSelectId);
        size += CacheSizes.sizeOfString(selectName);
        size += CacheSizes.sizeOfString(selectenName);
        size += CacheSizes.sizeOfString(formula);
        size += CacheSizes.sizeOfString(formulaType.getName());
        size += CacheSizes.sizeOfString(selectDesc);

        size += CacheSizes.sizeOfBoolean() * 9;

        return size;
    }

}


