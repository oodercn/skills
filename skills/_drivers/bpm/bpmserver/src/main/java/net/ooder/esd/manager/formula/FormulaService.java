package net.ooder.esd.manager.formula;

import net.ooder.common.FormulaType;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import net.ooder.esb.config.manager.ExpressionParameter;

import java.util.List;

public interface FormulaService {

    ResultModel<ParticipantSelect> getParticipantSelect(String selectedId);

    ResultModel<Boolean> addParticipantSelect(ParticipantSelect participantSelect);

    ResultModel<Boolean> delParticipantSelect(String participantSelectId);

    public ResultModel<Boolean> addFormulaParameters(ExpressionParameter parameter);

    public ResultModel<Boolean> delParameters(String parameterId);

    public ListResultModel<List<ExpressionParameter>> getFormulaParameters();

    public ResultModel<ExpressionParameter> getFormulaParameter(String parameterId);
}
