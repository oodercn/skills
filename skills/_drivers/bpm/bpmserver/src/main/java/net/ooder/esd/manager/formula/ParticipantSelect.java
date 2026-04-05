package net.ooder.esd.manager.formula;

import net.ooder.annotation.MethodChinaName;
import net.ooder.common.FormulaType;
import net.ooder.esb.config.manager.ExpressionParameter;

import java.util.List;

public interface ParticipantSelect {

    public String getParticipantSelectId();

    public void setParticipantSelectId(String participantSelectId);

    @MethodChinaName(cname = "公式名称")
    public String getSelectName();

    public void setSelectName(String selectName);

    @MethodChinaName(cname = "类型")
    public FormulaType getFormulaType();

    public void setFormulaType(FormulaType formulaType);

    public String getSelectenName();

    public void setSelectenName(String selectenName);

    @MethodChinaName(cname = "公式")
    public String getFormula();

    public void setFormula(String formula);

    @MethodChinaName(cname = "描述")
    public String getSelectDesc();

    public void setSelectDesc(String selectDesc);

    @MethodChinaName(cname = "公式参数")
    public List<ExpressionParameter> getParameterList();

    public void setParameterList(List<ExpressionParameter> parameterList);
}
