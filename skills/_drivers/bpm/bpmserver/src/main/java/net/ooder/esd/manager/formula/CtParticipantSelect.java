package net.ooder.esd.manager.formula;

import net.ooder.common.FormulaType;
import net.ooder.esb.config.manager.ExpressionParameter;
import net.ooder.esb.config.manager.ServiceBean;

import java.util.List;

public class CtParticipantSelect implements ParticipantSelect {

    private String selectName;
    private FormulaType formulaType;
    private String selectenName;
    private String formula;
    private String selectDesc;
    private String participantSelectId;
    private List<ExpressionParameter> parameterList;

    public CtParticipantSelect() {
    }

    public CtParticipantSelect(ParticipantSelect select) {
        this.participantSelectId = select.getParticipantSelectId();
        this.parameterList = select.getParameterList();
        this.selectName = select.getSelectName();
        this.formulaType = select.getFormulaType();
        this.formula = select.getFormula();
        this.selectDesc = select.getSelectDesc();
    }

    public CtParticipantSelect(ServiceBean select) {
    }

    @Override
    public void setFormula(String formula) {
        this.formula = formula;
    }

    @Override
    public FormulaType getFormulaType() {
        return formulaType;
    }

    @Override
    public void setFormulaType(FormulaType formulaType) {
        this.formulaType = formulaType;
    }

    @Override
    public String getSelectenName() {
        return selectenName;
    }

    @Override
    public void setSelectenName(String selectenName) {
        this.selectenName = selectenName;
    }

    @Override
    public String getSelectName() {
        return selectName;
    }

    @Override
    public void setSelectName(String selectName) {
        this.selectName = selectName;
    }

    @Override
    public String getParticipantSelectId() {
        return participantSelectId;
    }

    @Override
    public void setParticipantSelectId(String participantSelectId) {
        this.participantSelectId = participantSelectId;
    }

    @Override
    public List<ExpressionParameter> getParameterList() {
        return parameterList;
    }

    @Override
    public void setParameterList(List<ExpressionParameter> parameterList) {
        this.parameterList = parameterList;
    }

    @Override
    public void setSelectDesc(String selectDesc) {
        this.selectDesc = selectDesc;
    }

    @Override
    public String getFormula() {
        return formula;
    }

    @Override
    public String getSelectDesc() {
        return selectDesc;
    }
}
