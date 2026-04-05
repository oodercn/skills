
package net.ooder.bpm.engine.database.expression;

import net.ooder.annotation.FormulaParams;
import net.ooder.esb.config.manager.ExpressionParameter;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: itjds Copyright (c) 2003-2018
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 *
 * @author wenzhang
 * @version 3.0
 */
public class DbExpressionParameter<T> implements ExpressionParameter {
    private String parameterId;

    private String parameterValue;

    private String participantSelectId;

    private String parameterCode;

    private String parameterName;

    private String parameterenName;

    private String parameterDesc;

    private FormulaParams parameterType;

    private Boolean single = false;

    /**
     * @return Returns the parameterCode.
     */
    public String getParameterCode() {
        return parameterCode;
    }

    /**
     * @param parameterCode The parameterCode to set.
     */
    public void setParameterCode(String parameterCode) {
        this.parameterCode = parameterCode;
    }

    /**
     * @return Returns the parameterDesc.
     */
    public String getParameterDesc() {
        return parameterDesc;
    }

    /**
     * @param parameterDesc The parameterDesc to set.
     */
    public void setParameterDesc(String parameterDesc) {
        this.parameterDesc = parameterDesc;
    }



    public Boolean getSingle() {
        return single;
    }


    public void setSingle(Boolean single) {
        this.single = single;
    }

    /**
     * @return Returns the parameterId.
     */
    public String getParameterId() {
        return parameterId;
    }

    /**
     * @param parameterId The parameterId to set.
     */
    public void setParameterId(String parameterId) {
        this.parameterId = parameterId;
    }

    /**
     * @return Returns the parameterName.
     */
    public String getParameterName() {
        return parameterName;
    }

    /**
     * @param parameterName The parameterName to set.
     */
    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    /**
     * @return Returns the parameterType.
     */
    public FormulaParams getParameterType() {
        return parameterType;
    }

    @Override
    public String getValue() {
        return parameterValue;
    }

    @Override
    public void setValue(String value) {

        this.parameterValue = value;
    }

    /**
     * @param parameterType The parameterType to set.
     */
    public void setParameterType(FormulaParams parameterType) {
        this.parameterType = parameterType;
    }

    @Override
    public String getParameterValue() {
        return parameterValue;
    }

    @Override
    public void setParameterValue(String parameterValue) {

        this.parameterValue = parameterValue;
    }

    /**
     * @return Returns the participantId.
     */
    public String getParticipantSelectId() {
        return participantSelectId;
    }

    /**
     * @param participantSelectId The participantId to set.
     */
    public void setParticipantSelectId(String participantSelectId) {
        this.participantSelectId = participantSelectId;

    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof DbExpressionParameter) {
            DbExpressionParameter parameter = (DbExpressionParameter) o;
            if (this.parameterCode == null) {
                return false;
            } else {
                return this.parameterCode.equalsIgnoreCase(parameter.getParameterCode());
            }
        } else if (o instanceof String) {
            String id = (String) o;
            if (this.parameterCode == null) {
                return false;
            } else {
                return this.parameterCode.equalsIgnoreCase(id);
            }
        } else {
            return false;
        }
    }

    public String getParameterenName() {
        return parameterenName;
    }

    public void setParameterenName(String parameterenName) {
        this.parameterenName = parameterenName;
    }
}


