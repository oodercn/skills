package net.ooder.bpm.engine.proxy;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.bpm.client.ActivityDefForm;
import net.ooder.bpm.client.AttributeDef;
import net.ooder.bpm.engine.BPMServer;
import net.ooder.bpm.engine.IOTRightEngine;
import net.ooder.bpm.engine.database.expression.DbParticipantSelectManager;
import net.ooder.bpm.engine.database.form.DbActivityDefForm;
import net.ooder.common.JDSException;
import net.ooder.esb.config.manager.ExpressionParameter;
import net.ooder.esd.manager.formula.CtParticipantSelect;
import net.ooder.esd.manager.formula.ParticipantSelect;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ActivityDefFormProxy implements ActivityDefForm {

    private final String systemCode;
    @JSONField(serialize = false)
    private DbActivityDefForm dbActivityDefForm;

    @JSONField(serialize = false)
    private IOTRightEngine rightEngine;


    public ActivityDefFormProxy(DbActivityDefForm dbActivityDefForm, String systemCode) {
        this.systemCode = systemCode;
        try {
            this.rightEngine = (IOTRightEngine) BPMServer.getRigthEngine(systemCode);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        this.dbActivityDefForm = dbActivityDefForm;

    }

    private List<ParticipantSelect> getParticipantSelectList(AttributeDef participantAttribute) {
        List<ParticipantSelect> participantSelects = new ArrayList<ParticipantSelect>();

        String selectedId = participantAttribute.getValue();


        if (participantAttribute == null) {
            return new ArrayList();
        }


        // 此属性不为空，说明是旧版本的公式系统，继续调用旧的公式系统执行
        if (selectedId != null && !selectedId.equals("")) {
            ParticipantSelect participantSelect = null;
            try {
                participantSelect = DbParticipantSelectManager.getInstance().loadByKey(selectedId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (participantSelect != null) {
                participantSelects.add(new CtParticipantSelect(participantSelect));
            }
        } else {
            List child = participantAttribute.getChildren();
            if (child.size() == 0) {
                return new ArrayList();
            }

            for (int i = 0; i < child.size(); i++) {
                AttributeDef formulaAtt = (AttributeDef) child.get(i);
                String formulaId = formulaAtt.getValue();
                ParticipantSelect participantSelect = null;
                if (formulaId.equalsIgnoreCase("CUSTOMFORMULA")) {
                    // 高级公式
                    participantSelect = new CtParticipantSelect();
                    AttributeDef customFormulaAtt = (AttributeDef) formulaAtt.getChild("CUSTOMFORMULA");
                    participantSelect.setFormula(formulaId);
                    participantSelect.setSelectName("高級公式");
                    String expression = customFormulaAtt.getValue();
                    if (expression != null && !expression.equals("")) {
                        participantSelect.setFormula(expression);
                    }
                } else {
                    try {
                        participantSelect = new CtParticipantSelect(DbParticipantSelectManager.getInstance().loadByKey(formulaId));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    // 取得参数以及参数的值
                    AttributeDef parameterAtt = (AttributeDef) formulaAtt.getChild(formulaAtt.getValue());

                    if (parameterAtt != null && parameterAtt.getName() != null && !parameterAtt.getName().equals("") && parameterAtt.getValue() != null && !parameterAtt.getValue().equals("")) {
                        StringTokenizer stParameter = new StringTokenizer(parameterAtt.getValue(), ";");
                        List<ExpressionParameter> parameterList = participantSelect.getParameterList();

                        while (stParameter.hasMoreTokens()) {
                            String parameterString = stParameter.nextToken();
                            int _index = parameterString.indexOf("=");
                            if (_index == -1) {
                                continue;
                            }
                            String parameterName = parameterString.substring(0, _index);
                            String parameterValue = parameterString.substring(_index + 1, parameterString.length());
                            for (ExpressionParameter expressionParameter : parameterList) {
                                if (expressionParameter.getParameterCode().equals(parameterName)) {
                                    expressionParameter.setValue(parameterValue);
                                }

                            }

                        }
                        participantSelect.setParameterList(parameterList);
                    }
                    // 执行公式
                }

                if (participantSelect != null) {
                    participantSelects.add(participantSelect);
                }

            }

        }
        return participantSelects;

    }

    @Override
    public List<ParticipantSelect> getEscomSelectedAtt() {
        AttributeDefProxy attributeDef = null;
        List<ParticipantSelect> participantSelects = new ArrayList<ParticipantSelect>();

        if (dbActivityDefForm.getEscomSelectedAtt() != null) {
            AttributeDef participantAttribute = new AttributeDefProxy(dbActivityDefForm.getEscomSelectedAtt(), systemCode);
            participantSelects = getParticipantSelectList(participantAttribute);

        }

        return participantSelects;
    }


    @Override
    public List<ParticipantSelect> getActionSelectedAtt() {
        AttributeDefProxy attributeDef = null;
        List<ParticipantSelect> participantSelects = new ArrayList<ParticipantSelect>();

        if (dbActivityDefForm.getActionSelectedAtt() != null) {
            AttributeDef participantAttribute = new AttributeDefProxy(dbActivityDefForm.getActionSelectedAtt(), systemCode);
            participantSelects = getParticipantSelectList(participantAttribute);

        }

        return participantSelects;
    }

    @Override
    public List<ParticipantSelect> getTableSelectedAtt() {
        AttributeDefProxy attributeDef = null;
        List<ParticipantSelect> participantSelects = new ArrayList<ParticipantSelect>();

        if (dbActivityDefForm.getTableSelectedAtt() != null) {
            AttributeDef participantAttribute = new AttributeDefProxy(dbActivityDefForm.getTableSelectedAtt(), systemCode);
            participantSelects = getParticipantSelectList(participantAttribute);

        }

        return participantSelects;

    }


}
