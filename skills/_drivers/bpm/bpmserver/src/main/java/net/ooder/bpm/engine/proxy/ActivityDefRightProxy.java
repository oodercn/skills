package net.ooder.bpm.engine.proxy;

import com.alibaba.fastjson.annotation.JSONField;
import net.ooder.bpm.client.ActivityDefRight;
import net.ooder.bpm.client.AttributeDef;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.BPMServer;
import net.ooder.bpm.engine.IOTRightEngine;
import net.ooder.bpm.engine.database.expression.DbParticipantSelectManager;
import net.ooder.bpm.engine.database.right.DbActivityDefRight;
import net.ooder.bpm.enums.activitydef.ActivityDefPerformSequence;
import net.ooder.bpm.enums.activitydef.ActivityDefPerformtype;
import net.ooder.bpm.enums.activitydef.ActivityDefSpecialSendScope;
import net.ooder.bpm.enums.right.RightGroupEnums;
import net.ooder.common.JDSException;
import net.ooder.common.CommonYesNoEnum;
import net.ooder.esb.config.manager.ExpressionParameter;
import net.ooder.esd.manager.formula.CtParticipantSelect;
import net.ooder.esd.manager.formula.ParticipantSelect;
import net.ooder.org.Person;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ActivityDefRightProxy implements ActivityDefRight {

    private final String systemCode;
    @JSONField(serialize = false)
    
    private DbActivityDefRight dbActivityDefRight;
    @JSONField(serialize = false)
    
    private IOTRightEngine rightEngine;


    public ActivityDefRightProxy(DbActivityDefRight dbActivityDefRight, String systemCode) {
        this.systemCode = systemCode;
        try {
            this.rightEngine = (IOTRightEngine) BPMServer.getRigthEngine(systemCode);
        } catch (JDSException e) {
            e.printStackTrace();
        }
        this.dbActivityDefRight = dbActivityDefRight;

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
                                    expressionParameter.setParameterValue(parameterValue);
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
    public List<ParticipantSelect> getPerformerSelectedAtt() {
        AttributeDefProxy attributeDef = null;
        List<ParticipantSelect> participantSelects = new ArrayList<ParticipantSelect>();

        if (dbActivityDefRight.getPerformerSelectedAtt() != null) {
            AttributeDef participantAttribute = new AttributeDefProxy(dbActivityDefRight.getPerformerSelectedAtt(), systemCode);
            participantSelects = getParticipantSelectList(participantAttribute);

        }

        return participantSelects;
    }

    @Override
    public ActivityDefPerformtype getPerformType() {
        return dbActivityDefRight.getPerformType();
    }

    @Override
    public ActivityDefPerformSequence getPerformSequence() {
        return dbActivityDefRight.getPerformSequence();
    }

    @Override
    @JSONField(serialize = false)
    
    public List<Person> getPerFormPersons() throws BPMException {
        return this.rightEngine.getParticipant(dbActivityDefRight.getPerformerSelectedAtt(), null);
    }

    @Override
    @JSONField(serialize = false)
    
    public List<Person> getReaderPersons() throws BPMException {
        return this.rightEngine.getParticipant(dbActivityDefRight.getReaderSelectedAtt(), null);

    }

    @Override
    
    public List<Person> getInsteadSignPersons() throws BPMException {
        return this.rightEngine.getParticipant(dbActivityDefRight.getInsteadSignSelectedAtt(), null);
    }

    @Override
    public RightGroupEnums getMovePerformerTo() {
        return dbActivityDefRight.getMovePerformerTo();
    }

    @Override
    public Boolean isCanInsteadSign() {
        return dbActivityDefRight.getCanInsteadSign().equals(CommonYesNoEnum.YES) ? true : false;
    }

    @Override
    public Boolean isCanTakeBack() {
        return dbActivityDefRight.getCanTakeBack().equals(CommonYesNoEnum.YES) ? true : false;
    }

    @Override
    public RightGroupEnums getMoveReaderTo() {
        return dbActivityDefRight.getMoveReaderTo();
    }

    @Override
    public List<ParticipantSelect> getReaderSelectedAtt() {
        AttributeDefProxy attributeDef = null;
        List<ParticipantSelect> participantSelects = new ArrayList<ParticipantSelect>();
        if (dbActivityDefRight.getReaderSelectedAtt() != null) {
            AttributeDef participantAttribute = new AttributeDefProxy(dbActivityDefRight.getReaderSelectedAtt(), systemCode);
            participantSelects = getParticipantSelectList(participantAttribute);

        }
        return participantSelects;
    }

    @Override
    public List<ParticipantSelect> getInsteadSignAtt() {
        AttributeDefProxy attributeDef = null;
        List<ParticipantSelect> participantSelects = new ArrayList<ParticipantSelect>();
        if (dbActivityDefRight.getInsteadSignSelectedAtt() != null) {
            AttributeDef participantAttribute = new AttributeDefProxy(dbActivityDefRight.getInsteadSignSelectedAtt(), systemCode);
            participantSelects = getParticipantSelectList(participantAttribute);

        }
        return participantSelects;

    }

    @Override
    public Boolean isCanReSend() {
        return dbActivityDefRight.getCanReSend() == null ? true : (dbActivityDefRight.equals(CommonYesNoEnum.YES) ? true : false);
    }

    @Override
    public ActivityDefSpecialSendScope getSpecialSendScope() {
        return dbActivityDefRight.getSpecialSendScope();
    }


}
