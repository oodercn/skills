
package net.ooder.bpm.bpd;

import net.ooder.common.logging.Log;


import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.database.expression.DbExpressionParameter;
import net.ooder.bpm.engine.database.expression.DbParticipantSelect;
import net.ooder.bpm.engine.database.expression.DbParticipantSelectManager;
import net.ooder.common.FormulaType;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.config.ErrorListResultModel;
import net.ooder.config.ErrorResultModel;
import net.ooder.config.ListResultModel;
import net.ooder.config.ResultModel;
import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.esb.config.manager.ExpressionParameter;
import net.ooder.esd.manager.formula.FormulaService;
import net.ooder.esd.manager.formula.ParticipantSelect;
import net.ooder.web.util.PageUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@EsbBeanAnnotation(id = "FormulaService", name = "表达式服务", expressionArr = "FormulaServiceImpl()", desc = "表达式服务")
public class FormulaServiceImpl implements FormulaService {
    private static final Log logger = LogFactory.getLog(BPMConstants.CONFIG_KEY, FormulaServiceImpl.class);


    public ListResultModel<List<ParticipantSelect>> getFormulas(FormulaType type) {
        ListResultModel<List<ParticipantSelect>> result = new ListResultModel<List<ParticipantSelect>>();
        try {
            String where = "where 1=1";
            if (type != null) {
                if (type != null) {
                    where = " where TYPE in ('" + type.getType() + "','" + type.getBaseType().getType() + "')";
                } else {
                    where = " where TYPE='" + type.getType() + "'";
                }
            }
            ParticipantSelect[] dbPS = (DbParticipantSelect[]) DbParticipantSelectManager.getInstance().loadByWhere(where).toArray(new DbParticipantSelect[0]);

            List<ParticipantSelect> psList = Arrays.asList(dbPS);
            //  logger.info("getFormulas size="+psList.size());
            // result = PageUtil.getDefaultPageList(psList);
            result.setSize(psList.size());

            result.setData(psList);


        } catch (Exception ex) {
            logger.error("", ex);
            result = new ErrorListResultModel<List<ParticipantSelect>>();
            ((ErrorListResultModel) result).setErrdes(ex.getMessage());
        }
        return result;
    }

    @Override
    public ListResultModel<List<ExpressionParameter>> getFormulaParameters() {
        ListResultModel<List<ExpressionParameter>> result = new ListResultModel<List<ExpressionParameter>>();
        try {
            DbParticipantSelect[] dbPS = DbParticipantSelectManager.getInstance().loadAll();
            List<ExpressionParameter> params = new ArrayList<ExpressionParameter>();

            if (dbPS != null && dbPS.length > 0) {
                for (int i = 0; i < dbPS.length; i++) {
                    DbParticipantSelect obj = dbPS[i];
                    List<ExpressionParameter> list = obj.getParameterList();
                    params.addAll(list);
                }
            }
            result.setData(params);

            // result = PageUtil.getDefaultPageList(params);

        } catch (SQLException e) {
            logger.error("", e);
            result = new ErrorListResultModel<List<ExpressionParameter>>();
            ((ErrorListResultModel) result).setErrdes(e.getMessage());
        }

        return result;
    }

    @Override
    public ResultModel<ExpressionParameter> getFormulaParameter(String parameterId) {
        ResultModel<ExpressionParameter> result = new ResultModel<ExpressionParameter>();
        ExpressionParameter parameter = DbParticipantSelectManager.getInstance().loadParameterById(parameterId);
        result.setData(parameter);
        return result;
    }

    @Override
    public ResultModel<ParticipantSelect> getParticipantSelect(String selectedId) {
        ResultModel<ParticipantSelect> result = new ResultModel<ParticipantSelect>();
        try {
            ParticipantSelect select = DbParticipantSelectManager.getInstance().loadByKey(selectedId);
            result.setData(select);
        } catch (Exception ex) {

            result = new ErrorResultModel<ParticipantSelect>();
            ((ErrorResultModel) result).setErrdes(ex.getMessage());
        }
        return result;
    }


    @Override
    public ResultModel<Boolean> addParticipantSelect(ParticipantSelect participantSelect) {

        ResultModel<Boolean> result = new ResultModel<Boolean>();
        try {
            DbParticipantSelect select = null;
            String selectId = participantSelect.getParticipantSelectId();

            if (selectId == null || selectId.equals("")) {
                select = DbParticipantSelectManager.getInstance().createParticipantSelect();
                select.setParticipantSelectId(UUID.randomUUID().toString());
            } else if (DbParticipantSelectManager.getInstance().loadByKey(selectId) == null) {
                select = DbParticipantSelectManager.getInstance().createParticipantSelect();
                select.setParticipantSelectId(selectId);
            } else {
                select = DbParticipantSelectManager.getInstance().loadByKey(selectId);
            }

            select.setFormula(participantSelect.getFormula());
            select.setSelectDesc(participantSelect.getSelectDesc());
            select.setSelectName(participantSelect.getSelectName());
            FormulaType type = participantSelect.getFormulaType();
            select.setFormulaType(type == null ? FormulaType.UNKNOW : type);
            select.setSelectenName(participantSelect.getSelectenName());
            DbParticipantSelectManager.getInstance().save(select);

        } catch (Exception ex) {
            ex.printStackTrace();
            result = new ErrorResultModel<Boolean>();
            ((ErrorResultModel) result).setErrdes(ex.getMessage());
        }
        return result;
    }


    @Override
    public ResultModel<Boolean> delParticipantSelect(String selectedId) {
        ResultModel<Boolean> result = new ResultModel<Boolean>();
        try {

            List<String> ids = new ArrayList<>();
            if (selectedId.indexOf(";") > -1) {
                ids = Arrays.asList(selectedId.split(";"));
            } else {
                ids.add(selectedId);
            }
            for (String id : ids) {
                DbParticipantSelectManager.getInstance().deleteByKey(id);
            }


        } catch (SQLException e) {
            result = new ErrorResultModel<Boolean>();
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }


    @Override
    public ResultModel<Boolean> addFormulaParameters(ExpressionParameter parameter) {
        ResultModel<Boolean> result = new ResultModel<Boolean>();
        try {
            DbParticipantSelect select = DbParticipantSelectManager.getInstance().loadByKey(parameter.getParticipantSelectId());
            if (select != null) {
                DbExpressionParameter dbparameter = new DbExpressionParameter();

                String parameterId = parameter.getParameterId();
                if (parameterId == null || parameterId.equals("")) {
                    parameterId = UUID.randomUUID().toString();
                }
                dbparameter.setParameterId(parameterId);
                dbparameter.setParameterCode(parameter.getParameterCode());
                dbparameter.setParameterName(parameter.getParameterName());
                dbparameter.setParameterenName(parameter.getParameterenName());
                dbparameter.setParameterDesc(parameter.getParameterDesc());
                dbparameter.setParameterValue(parameter.getParameterValue());
                dbparameter.setParticipantSelectId(parameter.getParticipantSelectId());
                dbparameter.setParameterType(parameter.getParameterType());
                dbparameter.setSingle(parameter.getSingle());
                select.saveParameter(dbparameter);
                DbParticipantSelectManager.getInstance().save(select);

            }

        } catch (SQLException e) {
            result = new ErrorResultModel<Boolean>();
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    @Override
    public ResultModel<Boolean> delParameters(String parameterId) {
        ResultModel<Boolean> result = new ResultModel<Boolean>();
        try {
            List<String> ids = new ArrayList<>();
            if (parameterId.indexOf(";") > -1) {
                ids = Arrays.asList(parameterId.split(";"));
            } else {
                ids.add(parameterId);
            }

            for (String id : ids) {

                DbExpressionParameter dbparameter = DbParticipantSelectManager.getInstance().loadParameterById(id);
                if (dbparameter != null) {
                    DbParticipantSelect select = DbParticipantSelectManager.getInstance().loadByKey(dbparameter.getParticipantSelectId());

                    if (dbparameter != null) {
                        select.removeParameter(dbparameter);
                    }
                    DbParticipantSelectManager.getInstance().save(select);
                }
            }


        } catch (SQLException e) {
            result = new ErrorResultModel<Boolean>();
            ((ErrorResultModel) result).setErrdes(e.getMessage());
        }
        return result;
    }

    public ListResultModel<List<ParticipantSelect>> formulaSearch(FormulaType type, String forumla, String name) {
        ListResultModel<List<ParticipantSelect>> result = new ListResultModel<List<ParticipantSelect>>();
        try {
            String where = "";
            if (name != null) {
                where = "where SELECTNAME like '%" + name + "%'";
            }

            if (forumla != null) {
                if (where.startsWith("where")) {
                    where = where + " and FORMULA like '%" + forumla + "%'";
                } else {
                    where = " where FORMULA like '%" + forumla + "%'";
                }

            }

            if (type != null) {
                if (where.startsWith("where")) {
                    if (type != null) {
                        where = where + " and TYPE in ('" + type.getType() + "','" + type.getBaseType().getType() + "')";
                    } else {
                        where = where + " and TYPE='" + type.getType() + "'";
                    }
                } else {
                    if (type != null) {
                        where = " where TYPE in ('" + type.getType() + "','" + type.getBaseType().getType() + "')";
                    } else {
                        where = " where TYPE='" + type.getType() + "'";
                    }
                }
            }
            if (where.equals("")) {
                where = "where 1=1 ";
            }
            ParticipantSelect[] dbPS = (DbParticipantSelect[]) DbParticipantSelectManager.getInstance().loadByWhere(where).toArray(new DbParticipantSelect[0]);
            List<ParticipantSelect> psList = Arrays.asList(dbPS);

            result = PageUtil.getDefaultPageList(psList);
            result.setSize(psList.size());


        } catch (Exception ex) {
            logger.error("", ex);
            result = new ErrorListResultModel<List<ParticipantSelect>>();
            ((ErrorListResultModel) result).setErrdes(ex.getMessage());
        }
        return result;
    }


}
