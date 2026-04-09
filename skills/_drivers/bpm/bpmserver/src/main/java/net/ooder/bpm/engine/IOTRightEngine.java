/**
 * $RCSfile: IOTjava,v $
 * $Revision: 1.3 $
 * $Date: 2016/01/23 16:29:52 $
 * <p>
 * Copyright (C) 2008 itjds, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine;

import net.ooder.common.logging.Log;

import net.ooder.bpm.client.ActivityDefRight;
import net.ooder.bpm.client.ActivityInst;
import net.ooder.bpm.client.ActivityInstHistory;
import net.ooder.bpm.engine.database.expression.DbParticipantSelect;
import net.ooder.bpm.engine.database.expression.DbParticipantSelectManager;
import net.ooder.bpm.engine.database.right.*;
import net.ooder.bpm.engine.inter.*;
import net.ooder.bpm.engine.proxy.ActivityDefRightProxy;
import net.ooder.bpm.engine.query.BPMCondition;
import net.ooder.bpm.engine.query.BPMConditionKey;
import net.ooder.bpm.engine.query.right.ProcessStartRightFilter;
import net.ooder.bpm.enums.activitydef.ActivityDefPerformSequence;
import net.ooder.bpm.enums.activityinst.ActivityInstRightAtt;
import net.ooder.bpm.enums.activityinst.ActivityInstSuSpend;
import net.ooder.bpm.enums.activityinsthistory.ActivityInstHistoryAtt;
import net.ooder.bpm.enums.process.ProcessDefVersionAtt;
import net.ooder.bpm.enums.process.ProcessInstAtt;
import net.ooder.bpm.enums.process.ProcessInstStartType;
import net.ooder.bpm.enums.right.*;
import net.ooder.common.ReturnType;
import net.ooder.common.expression.ExpressionParser;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.Filter;
import net.ooder.annotation.Operator;
import net.ooder.config.ActivityDefImpl;
import net.ooder.annotation.AttributeInterpretClass;
import net.ooder.annotation.AttributeName;
import net.ooder.annotation.Attributetype;
import net.ooder.annotation.EsbBeanAnnotation;
import net.ooder.esb.config.manager.JDSExpressionParserManager;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.org.Org;
import net.ooder.org.OrgManager;
import net.ooder.org.Person;
import net.ooder.org.PersonNotFoundException;
import net.ooder.server.JDSClientService;
import net.ooder.server.JDSServer;
import net.ooder.server.OrgManagerFactory;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;


@EsbBeanAnnotation(expressionArr = "RightEvent()", id = "RightEngine")
public class IOTRightEngine implements RightEngine, Serializable {
    Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, IOTRightEngine.class);

    // process relevent manager
    public EIProcessDefManager processDefMgr = null;

    public EIProcessDefVersionManager processDefVerMgr = null;

    public EIActivityDefManager activityDefMgr = null;

    public EIRouteDefManager routeDefMgr = null;

    public EIProcessInstManager processInstMgr = null;

    public EIActivityInstManager activityInstMgr = null;

    public EIActivityInstHistoryManager activityInstHistoryMgr = null;

    public EIRouteInstManager routeInstMgr = null;

    // right relevent manager
    public DbActivityInstPersonManager actPersonMgr = null;

    public DbActivityHistoryPersonManager historyPersonMgr = null;

    public DbProcessDefVersionRightManager processRightMgr = null;

    public DbActivityDefRightManager activityRightMgr = null;

    public DbParticipantSelectManager participantMgr = null;

    // Workflw Engine
    public WorkflowEngine workflowEngine = null;

    // Org Manager
    public OrgManager orgManager = null;

    private String systemCode;

    public IOTRightEngine(String systemCode) {
        this.systemCode = systemCode;
        processDefMgr = EIProcessDefManager.getInstance();
        processDefVerMgr = EIProcessDefVersionManager.getInstance();
        activityDefMgr = EIActivityDefManager.getInstance();
        routeDefMgr = EIRouteDefManager.getInstance();
        processInstMgr = EIProcessInstManager.getInstance();
        activityInstMgr = EIActivityInstManager.getInstance();
        activityInstHistoryMgr = EIActivityInstHistoryManager.getInstance();
        routeInstMgr = EIRouteInstManager.getInstance();

        actPersonMgr = DbActivityInstPersonManager.getInstance();
        historyPersonMgr = DbActivityHistoryPersonManager.getInstance();
        processRightMgr = DbProcessDefVersionRightManager.getInstance();
        activityRightMgr = DbActivityDefRightManager.getInstance();
        participantMgr = DbParticipantSelectManager.getInstance();
        workflowEngine = WorkflowEngineImpl.getEngine(systemCode);

        orgManager = OrgManagerFactory.getOrgManager(JDSServer.getClusterClient().getSystem(systemCode).getConfigname());

    }

    private static IOTRightEngine engine = null;

    public static IOTRightEngine getEngine(String systemCode) {
        if (engine == null) {
            synchronized (IOTRightEngine.class) {
                if (engine == null) {
                    engine = new IOTRightEngine(systemCode);
                }
            }
        }
        return engine;
    }

    /**
     * @see com.ds.bpm.engine.RightEngine#getProcessDefListFilter(java.util.Map)
     */
    public Filter getProcessDefListFilter(Map<RightCtx, Object> ctx) throws BPMException {
        String userId = (String) ctx.get(RightCtx.USERID);
        ProcessStartRightFilter filter = new ProcessStartRightFilter(ctx, systemCode);
        return filter;
    }


    public Filter getProcessInstListFilter(RightConditionEnums condition, Map<RightCtx, Object> ctx) throws BPMException {
        String userId = (String) ctx.get(RightCtx.USERID);
        List userIds = (List) ctx.get("USERIDS");
        if (condition == null) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        sb.append(" ( SELECT PROCESSINST_ID ");

        switch (condition) {
            case CONDITION_WAITEDWORK:
                // 待办工作，所有待签收的公文
                sb.append(" FROM RT_ACTIVITY_PERSON WHERE PERSON_ID = ");
                sb.append("'").append(userId).append("'");
                sb.append(" AND RIGHT_GRP_CODE = ");
                sb.append("'").append(RightGroupEnums.PERFORMER.getType()).append("'");
                sb.append(" AND PERSON_ACTIVITY_STATE = ");
                sb.append("'").append(RightPerformStatus.WAITING.getType()).append("'");
                sb.append(" ) ");
                break;
            case CONDITION_MYWORK:
                // 我的工作，包括所有的待办工作和在办工作
                sb.append(" FROM RT_ACTIVITY_PERSON WHERE PERSON_ID = ");
                sb.append("'").append(userId).append("'");
                sb.append(" AND RIGHT_GRP_CODE = ");
                sb.append("'").append(RightGroupEnums.PERFORMER.getType()).append("'");
                sb.append(" AND PERSON_ACTIVITY_STATE IN ");
                sb.append("('").append(RightPerformStatus.WAITING.getType()).append("', ");
                sb.append("'").append(RightPerformStatus.CURRENT.getType()).append("') ");
                sb.append(" ) ");
                break;

            case CONDITION_COMPLETEDWORK:
                // 已办工作，所有已办完的公文
                sb.append(" FROM RT_ACTIVITY_PERSON WHERE PERSON_ID = ");
                sb.append("'").append(userId).append("'");
                sb.append(" AND ( ( RIGHT_GRP_CODE = ");
                sb.append("'").append(RightGroupEnums.PERFORMER.getType()).append("'");
                sb.append(" AND PERSON_ACTIVITY_STATE = ");
                sb.append("'").append(RightPerformStatus.FINISH.getType()).append("' ) ");
                sb.append(" OR RIGHT_GRP_CODE != ");
                sb.append("'").append(RightGroupEnums.PERFORMER.getType()).append("' )");
                sb.append(" ) ");
                break;

            case CONDITION_ALLWORK:
                if (userIds == null || userIds.size() == 0) {
                    sb.append(" FROM RT_ACTIVITY_PERSON WHERE PERSON_ID = ");
                    sb.append("'").append(userId).append("'");
                } else {
                    sb.append(" FROM RT_ACTIVITY_PERSON WHERE PERSON_ID IN ( ");
                    for (int k = 0; k < userIds.size(); k++) {
                        userId = (String) userIds.get(k);
                        if (k < userIds.size() - 1) {
                            sb.append("'").append(userId).append("',");
                        } else {
                            sb.append("'").append(userId).append("')");
                        }
                    }
                }
                sb.append(" ) ");

                break;
            case CONDITION_PPRCESSCOMPLETEDWORK:
                if (userIds == null || userIds.size() == 0) {
                    sb.append(" FROM RT_COMPLETEACTIVITY_PERSON WHERE PERSON_ID = ");
                    sb.append("'").append(userId).append("'");
                } else {
                    sb.append(" FROM RT_COMPLETEACTIVITY_PERSON WHERE PERSON_ID IN ( ");
                    for (int k = 0; k < userIds.size(); k++) {
                        userId = (String) userIds.get(k);
                        if (k < userIds.size() - 1) {
                            sb.append("'").append(userId).append("',");
                        } else {
                            sb.append("'").append(userId).append("')");
                        }
                    }
                }
                sb.append(" ) ");
                break;
            case CONDITION_ENDREAD:
                sb.append(" FROM RT_ACTIVITY_PERSON WHERE PERSON_ID = ");
                sb.append("'").append(userId).append("'");
                sb.append(" AND RIGHT_GRP_CODE = ");
                sb.append("'").append(RightGroupEnums.READER.getType()).append("'");
                sb.append(" AND PERSON_ACTIVITY_STATE = ");
                sb.append("'").append(RightPerformStatus.READ.getType()).append("'");
                sb.append(" ) ");

            case CONDITION_READ:
                sb.append(" FROM RT_ACTIVITY_PERSON WHERE PERSON_ID = ");
                sb.append("'").append(userId).append("'");
                sb.append(" AND RIGHT_GRP_CODE = ");
                sb.append("'").append(RightGroupEnums.READER.getType()).append("'");
                sb.append(" AND PERSON_ACTIVITY_STATE = ");
                sb.append("'").append(RightPerformStatus.READ.getType()).append("'");
                sb.append(" ) ");
                break;


/*************************************************************************/
            case CONDITION_WAITEDCOMMAND:
                // 待办工作，所有待签收的公文
                sb.append(" FROM RT_ACTIVITY_COMMAND WHERE COMMAND_ID = ");
                sb.append("'").append(userId).append("'");
                sb.append(" AND RIGHT_GRP_CODE = ");
                sb.append("'").append(RightGroupEnums.PERFORMER.getType()).append("'");
                sb.append(" AND COMMAND_ACTIVITY_STATE = ");
                sb.append("'").append(RightPerformStatus.WAITING.getType()).append("'");
                sb.append(" ) ");
                break;
            case CONDITION_MYCOMMAND:

                sb.append(" FROM RT_ACTIVITY_COMMAND WHERE COMMAND_ID = ");
                sb.append("'").append(userId).append("'");
                sb.append(" AND RIGHT_GRP_CODE = ");
                sb.append("'").append(RightGroupEnums.PERFORMER.getType()).append("'");
                sb.append(" AND COMMAND_ACTIVITY_STATE IN ");
                sb.append("('").append(RightPerformStatus.WAITING.getType()).append("', ");
                sb.append("'").append(RightPerformStatus.CURRENT.getType()).append("') ");
                sb.append(" ) ");
                break;

            case CONDITION_COMPLETEDCOMMAND:
                // 已办工作，所有已办完的公文
                sb.append(" FROM RT_ACTIVITY_COMMAND WHERE COMMAND_ID = ");
                sb.append("'").append(userId).append("'");
                sb.append(" AND ( ( RIGHT_GRP_CODE = ");
                sb.append("'").append(RightGroupEnums.PERFORMER.getType()).append("'");
                sb.append(" AND COMMAND_ACTIVITY_STATE = ");
                sb.append("'").append(RightPerformStatus.FINISH.getType()).append("' ) ");
                sb.append(" OR RIGHT_GRP_CODE != ");
                sb.append("'").append(RightGroupEnums.PERFORMER.getType()).append("' )");
                sb.append(" ) ");
                break;

            case CONDITION_ALLCOMMAND:
                if (userIds == null || userIds.size() == 0) {
                    sb.append(" FROM RT_ACTIVITY_COMMAND WHERE COMMAND_ID = ");
                    sb.append("'").append(userId).append("'");
                } else {
                    sb.append(" FROM RT_ACTIVITY_COMMAND WHERE COMMAND_ID IN ( ");
                    for (int k = 0; k < userIds.size(); k++) {
                        userId = (String) userIds.get(k);
                        if (k < userIds.size() - 1) {
                            sb.append("'").append(userId).append("',");
                        } else {
                            sb.append("'").append(userId).append("')");
                        }
                    }
                }
                sb.append(" ) ");


/*************************************************************************/

            case CONDITION_WAITEDEVENT:
                // 待办工作，所有待签收的公文
                sb.append(" FROM RT_ACTIVITY_EVENT WHERE EVENT_ID = ");
                sb.append("'").append(userId).append("'");
                sb.append(" AND RIGHT_GRP_CODE = ");
                sb.append("'").append(RightGroupEnums.PERFORMER.getType()).append("'");
                sb.append(" AND EVENT_ACTIVITY_STATE = ");
                sb.append("'").append(RightPerformStatus.WAITING.getType()).append("'");
                sb.append(" ) ");
                break;
            case CONDITION_MYEVENT:

                sb.append(" FROM RT_ACTIVITY_EVENT WHERE EVENT_ID = ");
                sb.append("'").append(userId).append("'");
                sb.append(" AND RIGHT_GRP_CODE = ");
                sb.append("'").append(RightGroupEnums.PERFORMER.getType()).append("'");
                sb.append(" AND EVENT_ACTIVITY_STATE IN ");
                sb.append("('").append(RightPerformStatus.WAITING.getType()).append("', ");
                sb.append("'").append(RightPerformStatus.CURRENT.getType()).append("') ");
                sb.append(" ) ");
                break;

            case CONDITION_COMPLETEDEVENT:
                // 已办工作，所有已办完的公文
                sb.append(" FROM RT_ACTIVITY_EVENT WHERE EVENT_ID = ");
                sb.append("'").append(userId).append("'");
                sb.append(" AND ( ( RIGHT_GRP_CODE = ");
                sb.append("'").append(RightGroupEnums.PERFORMER.getType()).append("'");
                sb.append(" AND EVENT_ACTIVITY_STATE = ");
                sb.append("'").append(RightPerformStatus.FINISH.getType()).append("' ) ");
                sb.append(" OR RIGHT_GRP_CODE != ");
                sb.append("'").append(RightGroupEnums.PERFORMER.getType()).append("' )");
                sb.append(" ) ");
                break;

            case CONDITION_ALLEVENT:
                if (userIds == null || userIds.size() == 0) {
                    sb.append(" FROM RT_ACTIVITY_EVENT WHERE EVENT_ID = ");
                    sb.append("'").append(userId).append("'");
                } else {
                    sb.append(" FROM RT_ACTIVITY_EVENT WHERE EVENT_ID IN ( ");
                    for (int k = 0; k < userIds.size(); k++) {
                        userId = (String) userIds.get(k);
                        if (k < userIds.size() - 1) {
                            sb.append("'").append(userId).append("',");
                        } else {
                            sb.append("'").append(userId).append("')");
                        }
                    }
                }
                sb.append(" ) ");


                break;


            default:
                throw new IllegalStateException("wrong right condition!");

        }

        BPMCondition c = new BPMCondition(BPMConditionKey.PROCESSINST_ID, Operator.IN, sb.toString());
        return c;
    }

    /**
     * @see com.ds.bpm.engine.RightEngine#getActivityInstListFilter(RightConditionEnums condition, java.util.Map)
     */
    public Filter getActivityInstListFilter(RightConditionEnums condition, Map<RightCtx, Object> ctx) throws BPMException {
        String userId = (String) ctx.get(RightCtx.USERID);
        List userIds = (List) ctx.get("USERIDS");
        if (condition == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(" ( SELECT ACTIVITYINST_ID ");

        switch (condition) {
            case CONDITION_WAITEDWORK:
                // 待办工作，所有待签收的公文
                sb.append(" FROM RT_ACTIVITY_PERSON WHERE PERSON_ID = ");
                sb.append("'").append(userId).append("'");
                sb.append(" AND RIGHT_GRP_CODE = ");
                sb.append("'").append(RightGroupEnums.PERFORMER.getType()).append("'");
                sb.append(" AND PERSON_ACTIVITY_STATE = ");
                sb.append("'").append(RightPerformStatus.WAITING.getType()).append("'");
                sb.append(" ) ");
                break;

            case CONDITION_OUTWORK:
                // 我发出去的
                sb.append(" FROM RT_ACTIVITY_PERSON WHERE PERSON_ID = ");
                sb.append("'").append(userId).append("'");
                sb.append(" AND RIGHT_GRP_CODE = ");
                sb.append("'").append(RightGroupEnums.SPONSOR.getType()).append("'");
                sb.append(" ) ");
                break;

            case CONDITION_INWORK:
                // 待办工作，所有待签收的公文
                sb.append(" FROM RT_ACTIVITY_PERSON WHERE PERSON_ID = ");
                sb.append("'").append(userId).append("'");
                sb.append(" AND RIGHT_GRP_CODE != ");
                sb.append("'").append(RightGroupEnums.SPONSOR.getType()).append("'");
                sb.append(" ) ");
                break;


            case CONDITION_MYWORK:
                if (userIds == null || userIds.size() == 0) {
                    sb.append(" FROM RT_ACTIVITY_PERSON WHERE PERSON_ID = ");
                    sb.append("'").append(userId).append("'");
                } else {
                    sb.append(" FROM RT_ACTIVITY_PERSON WHERE PERSON_ID IN ( ");
                    for (int k = 0; k < userIds.size(); k++) {
                        userId = (String) userIds.get(k);
                        if (k < userIds.size() - 1) {
                            sb.append("'").append(userId).append("',");
                        } else {
                            sb.append("'").append(userId).append("')");
                        }
                    }

                }

                sb.append(" ) ");
                break;


            case CONDITION_CURRENTWORK:
                if (userIds == null || userIds.size() == 0) {
                    sb.append(" FROM RT_ACTIVITY_PERSON WHERE PERSON_ID = ");
                    sb.append("'").append(userId).append("'");
                } else {
                    sb.append(" FROM RT_ACTIVITY_PERSON WHERE PERSON_ID IN ( ");
                    for (int k = 0; k < userIds.size(); k++) {
                        userId = (String) userIds.get(k);
                        if (k < userIds.size() - 1) {
                            sb.append("'").append(userId).append("',");
                        } else {
                            sb.append("'").append(userId).append("')");
                        }
                    }

                }

                sb.append(" ) ");
                break;

            case CONDITION_CURRENTWORK_NOTSTART:
                if (userIds == null || userIds.size() == 0) {
                    sb.append(" FROM RT_ACTIVITY_PERSON WHERE PERSON_ID = ");
                    sb.append("'").append(userId).append("'");
                } else {
                    sb.append(" FROM RT_ACTIVITY_PERSON WHERE PERSON_ID IN ( ");
                    for (int k = 0; k < userIds.size(); k++) {
                        userId = (String) userIds.get(k);
                        if (k < userIds.size() - 1) {
                            sb.append("'").append(userId).append("',");
                        } else {
                            sb.append("'").append(userId).append("')");
                        }
                    }

                }

                sb.append(" ) ");
                break;


            case CONDITION_MYWORKNOTREAD:
                // 我的工作，包括所有的待办工作和在办工作(不包含阅办)
                sb.append(" FROM RT_ACTIVITY_PERSON WHERE PERSON_ID = ");
                sb.append("'").append(userId).append("'");
                sb.append(" AND RIGHT_GRP_CODE IN ( ");
                sb.append("'").append(RightGroupEnums.PERFORMER.getType()).append("')");
                sb.append(" AND PERSON_ACTIVITY_STATE IN ");
                sb.append("('").append(RightPerformStatus.WAITING.getType()).append("', ");
                sb.append("'").append(RightPerformStatus.CURRENT.getType()).append("') ");
                sb.append(" ) ");
                break;

            case CONDITION_COMPLETEDWORK:

                // 已办工作，所有已办完的公文
                sb.append(" FROM RT_ACTIVITY_PERSON WHERE PERSON_ID = ");
                sb.append("'").append(userId).append("'");
                sb.append(" AND ((RIGHT_GRP_CODE IN ( ");
                sb.append("'").append(RightGroupEnums.PERFORMER.getType()).append("',");
                sb.append("'").append(RightGroupEnums.READER.getType()).append("')");
                sb.append(" AND PERSON_ACTIVITY_STATE = ");
                sb.append("'").append(RightPerformStatus.FINISH.getType()).append("' ) ");
                sb.append(" OR ( RIGHT_GRP_CODE != ");
                sb.append("'").append(RightGroupEnums.PERFORMER.getType()).append("' AND RIGHT_GRP_CODE !=");
                sb.append("'").append(RightGroupEnums.READER.getType());
                sb.append("') )) ");
                break;

            case CONDITION_ALLWORK:

                if (userIds == null || userIds.size() == 0) {
                    sb.append(" FROM RT_ACTIVITY_PERSON WHERE PERSON_ID = ");
                    sb.append("'").append(userId).append("'");
                } else {
                    sb.append(" FROM RT_ACTIVITY_PERSON WHERE PERSON_ID IN ( ");
                    for (int k = 0; k < userIds.size(); k++) {
                        userId = (String) userIds.get(k);
                        if (k < userIds.size() - 1) {
                            sb.append("'").append(userId).append("',");
                        } else {
                            sb.append("'").append(userId).append("')");
                        }
                    }

                }

                sb.append(" ) ");
                break;

            case CONDITION_ENDREAD:

                sb.append(" FROM RT_ACTIVITY_PERSON WHERE PERSON_ID = ");
                sb.append("'").append(userId).append("'");
                sb.append(" AND RIGHT_GRP_CODE = ");
                sb.append("'").append(RightGroupEnums.HISTORYREADER.getType()).append("'");
                sb.append(" AND PERSON_ACTIVITY_STATE = ");
                sb.append("'").append(RightPerformStatus.ENDREAD.getType()).append("'");
                sb.append(" ) ");
                break;

            case CONDITION_READ:

                sb.append(" FROM RT_ACTIVITY_PERSON WHERE PERSON_ID = ");
                sb.append("'").append(userId).append("'");
                sb.append(" AND RIGHT_GRP_CODE = ");
                sb.append("'").append(RightGroupEnums.READER.getType()).append("'");
                sb.append(" AND PERSON_ACTIVITY_STATE = ");
                sb.append("'").append(RightPerformStatus.READ.getType()).append("'");
                sb.append(" ) ");
                break;


/********************************************************************************************/
            case CONDITION_WAITEDCOMMAND:
                // 待办工作，所有待签收的公文
                sb.append(" FROM RT_ACTIVITY_COMMAND WHERE COMMAND_ID = ");
                sb.append("'").append(userId).append("'");
                sb.append(" AND RIGHT_GRP_CODE = ");
                sb.append("'").append(RightGroupEnums.PERFORMER.getType()).append("'");
                sb.append(" AND COMMAND_ACTIVITY_STATE = ");
                sb.append("'").append(RightPerformStatus.WAITING.getType()).append("'");
                sb.append(" ) ");
                break;
            case CONDITION_MYCOMMAND:
                break;


            case CONDITION_COMPLETEDCOMMAND:

                // 已办工作，所有已办完的公文
                sb.append(" FROM RT_ACTIVITY_COMMAND WHERE PERSON_ID = ");
                sb.append("'").append(userId).append("'");
                sb.append(" AND ((RIGHT_GRP_CODE IN ( ");
                sb.append("'").append(RightGroupEnums.PERFORMER.getType()).append("',");
                sb.append("'").append(RightGroupEnums.READER.getType()).append("')");
                sb.append(" AND COMMAND_ACTIVITY_STATE = ");
                sb.append("'").append(RightPerformStatus.FINISH.getType()).append("' ) ");
                sb.append(" OR ( RIGHT_GRP_CODE != ");
                sb.append("'").append(RightGroupEnums.PERFORMER.getType()).append("' AND RIGHT_GRP_CODE !=");
                sb.append("'").append(RightGroupEnums.READER.getType());
                sb.append("') )) ");
                break;

            case CONDITION_ALLCOMMAND:

                if (userIds == null || userIds.size() == 0) {
                    sb.append(" FROM RT_ACTIVITY_COMMAND WHERE COMMAND_ID = ");
                    sb.append("'").append(userId).append("'");
                } else {
                    sb.append(" FROM RT_ACTIVITY_COMMAND WHERE COMMAND_ID IN ( ");
                    for (int k = 0; k < userIds.size(); k++) {
                        userId = (String) userIds.get(k);
                        if (k < userIds.size() - 1) {
                            sb.append("'").append(userId).append("',");
                        } else {
                            sb.append("'").append(userId).append("')");
                        }
                    }

                }

                sb.append(" ) ");
                break;


/********************************************************************************************/
            case CONDITION_WAITEDEVENT:
                // 待办工作，所有待签收的公文
                sb.append(" FROM RT_ACTIVITY_EVENT WHERE ENENT_ID = ");
                sb.append("'").append(userId).append("'");
                sb.append(" AND RIGHT_GRP_CODE = ");
                sb.append("'").append(RightGroupEnums.PERFORMER.getType()).append("'");
                sb.append(" AND EVENT_ACTIVITY_STATE = ");
                sb.append("'").append(RightPerformStatus.WAITING.getType()).append("'");
                sb.append(" ) ");
                break;
            case CONDITION_MYEVENT:
                break;


            case CONDITION_COMPLETEDEVENT:

                // 已办工作，所有已办完的公文
                sb.append(" FROM RT_ACTIVITY_EVENT WHERE PERSON_ID = ");
                sb.append("'").append(userId).append("'");
                sb.append(" AND ((RIGHT_GRP_CODE IN ( ");
                sb.append("'").append(RightGroupEnums.PERFORMER.getType()).append("',");
                sb.append("'").append(RightGroupEnums.READER.getType()).append("')");
                sb.append(" AND EVENT_ACTIVITY_STATE = ");
                sb.append("'").append(RightPerformStatus.FINISH.getType()).append("' ) ");
                sb.append(" OR ( RIGHT_GRP_CODE != ");
                sb.append("'").append(RightGroupEnums.PERFORMER.getType()).append("' AND RIGHT_GRP_CODE !=");
                sb.append("'").append(RightGroupEnums.READER.getType());
                sb.append("') )) ");
                break;

            case CONDITION_ALLEVENT:

                if (userIds == null || userIds.size() == 0) {
                    sb.append(" FROM RT_ACTIVITY_EVENT WHERE EVENT_ID = ");
                    sb.append("'").append(userId).append("'");
                } else {
                    sb.append(" FROM RT_ACTIVITY_EVENT WHERE EVENT_ID IN ( ");
                    for (int k = 0; k < userIds.size(); k++) {
                        userId = (String) userIds.get(k);
                        if (k < userIds.size() - 1) {
                            sb.append("'").append(userId).append("',");
                        } else {
                            sb.append("'").append(userId).append("')");
                        }
                    }

                }

                sb.append(" ) ");
                break;
            default:
                throw new IllegalStateException("wrong right condition!");
        }

        BPMCondition c = new BPMCondition(BPMConditionKey.ACTIVITYINST_ID, Operator.IN, sb.toString());
        return c;
    }

    /**
     * 创建一个流程实例，在权限部分判断当前办理人是否有启动此流程得权限，如果没有则返回Failed表示不能进行此操作！
     */
    public ReturnType createProcessInst(String processInstId, Map<RightCtx, Object> ctx) throws BPMException {
        String userId = (String) ctx.get(RightCtx.USERID);
        EIProcessInst processInst = processInstMgr.loadByKey(processInstId);
        String versionId = processInst.getProcessDefVersionId();

        // 2。在扩展属性中写入流程启动人
        EIAttributeInst attInst = EIAttributeInstManager.getInstance().createAttributeInst();
        Person person = null;
        try {
            person = orgManager.getPersonByID(userId);
            attInst.setId(UUID.randomUUID().toString());
            attInst.setInterpretedValue(person);
            attInst.setInterpretClass(AttributeInterpretClass.PERSON.getType());
            attInst.setName(ProcessInstAtt.PROCESS_INSTANCE_STARTER.getType());
            attInst.setType(Attributetype.RIGHT.getType());

            processInst.setAttribute(Attributetype.RIGHT.getType(), attInst);
            processInstMgr.save(processInst);
        } catch (PersonNotFoundException e) {
            log.error("person not found : " + userId, e);
        }
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    /**
     * 创建一个流程实例，在权限部分判断当前办理人是否有启动此流程得权限，如果没有则返回Failed表示不能进行此操作！
     */
    public ReturnType createProcessInst(String processInstId, String initType, Map<RightCtx, Object> ctx) throws BPMException {
        // 对于自动启动流程不再写入流程启动人
        if (ProcessInstStartType.AUTO.getType().equalsIgnoreCase(initType)) {
            return new ReturnType(ReturnType.MAINCODE_SUCCESS);
        } else {
            return createProcessInst(processInstId, ctx);
        }
    }

    public ReturnType hasRightToStartProcess(String processInstId, Map<RightCtx, Object> ctx) throws BPMException {

        String userId = (String) ctx.get(RightCtx.USERID);
        EIProcessInst processInst = processInstMgr.loadByKey(processInstId);

        String versionId = processInst.getProcessDefVersionId();

        // get the process definition and first activity definition
        EIProcessDefVersion eiProcessDef = processDefVerMgr.loadByKey(versionId);
        EIActivityDef firstAct = this.workflowEngine.getFirstActivityDefInProcess(versionId);

        if (firstAct == null) {
            return new ReturnType(ReturnType.MAINCODE_FAIL);
        }


        if (!firstAct.getImplementation().equals(ActivityDefImpl.No)) {
            return new ReturnType(ReturnType.MAINCODE_SUCCESS);
        }


        List<Person> performers = getPerformerCandidate(firstAct.getActivityDefId(), ctx);
        if (performers == null || performers.size() == 0) {
            return new ReturnType(ReturnType.MAINCODE_SUCCESS);
        }
        for (Iterator it = performers.iterator(); it.hasNext(); ) {
            Person p = (Person) it.next();
            if (p.getID().equalsIgnoreCase(userId)) {
                return new ReturnType(ReturnType.MAINCODE_SUCCESS);
            }
        }
        return new ReturnType(ReturnType.MAINCODE_FAIL);
    }

    /**
     * 启动流程实例，保存办理人或启动人。 ctx中允许存放用户ID，或者用户对象的列表
     */
    public ReturnType startProcessInst(String processInstId, String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        // 如果ctx中保存了用户对象的列表则优先处理。
        List users = (List) ctx.get(RightCtx.USERS);
        if (users != null && !users.isEmpty()) {
            if (users.get(0) instanceof Person) {
                for (Iterator it = users.iterator(); it.hasNext(); ) {
                    Person user = (Person) it.next();
                    DbActivityInstPerson instPerson = actPersonMgr.createActivityInstPerson();
                    EIActivityInst eiActivityInst = activityInstMgr.loadByKey(activityInstId);

                    instPerson.setActivityInstId(eiActivityInst.getActivityInstId());
                    instPerson.setProcessInstId(eiActivityInst.getProcessInstId());
                    instPerson.setActivityInstPersonId(UUID.randomUUID().toString());
                    instPerson.setPersonId(user.getID());

                    instPerson.setPersonName("");
                    instPerson.setPersonActivityState(RightPerformStatus.WAITING);
                    instPerson.setRightGrpCode(RightGroupEnums.PERFORMER);
                    try {
                        actPersonMgr.save(instPerson);
                    } catch (SQLException e) {
                        throw new BPMException("save performer failed!", e);
                    }
                }
                return new ReturnType(ReturnType.MAINCODE_SUCCESS);
            } else {
                throw new BPMException("error performer type!");
            }
        }

        // 通常的处理
        String userId = (String) ctx.get(RightCtx.USERID);
        DbActivityInstPerson instPerson = actPersonMgr.createActivityInstPerson();
        EIActivityInst eiActivityInst = activityInstMgr.loadByKey(activityInstId);

        instPerson.setActivityInstId(eiActivityInst.getActivityInstId());
        instPerson.setProcessInstId(eiActivityInst.getProcessInstId());
        instPerson.setActivityInstPersonId(UUID.randomUUID().toString());
        instPerson.setPersonId(userId);

        instPerson.setPersonName("");
        instPerson.setPersonActivityState(RightPerformStatus.CURRENT);
        instPerson.setRightGrpCode(RightGroupEnums.PERFORMER);
        try {
            actPersonMgr.save(instPerson);
        } catch (SQLException e) {
            throw new BPMException("save performer failed!", e);
        }

        // 加入发起人组
        String personId = (String) ctx.get(RightCtx.USERID);
        DbActivityInstPerson currPerformer = actPersonMgr.createActivityInstPerson();
        currPerformer.setActivityInstPersonId(UUID.randomUUID().toString());
        currPerformer.setProcessInstId(processInstId);
        currPerformer.setActivityInstId(activityInstId);
        currPerformer.setPersonActivityState(RightPerformStatus.CURRENT);
        currPerformer.setPersonId(personId);
        currPerformer.setRightGrpCode(RightGroupEnums.SPONSOR);

        try {
            actPersonMgr.save(currPerformer);
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType startActivityInst(String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        // no operation now
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);

    }

    /**
     * @see com.ds.bpm.engine.RightEngine#saveActivityHistoryInst(String activityInstId, String activityInstHistoryId,
     * java.util.Map)
     */
    public ReturnType saveActivityHistoryInst(String activityInstId, String activityInstHistoryId, Map<RightCtx, Object> ctx) throws BPMException {
        String instId = activityInstId;
        String historyId = activityInstHistoryId;
        EIActivityInst srcActivityInst = activityInstMgr.loadByKey(instId);
        DbActivityDefRight actRightDef = activityRightMgr.loadByKey(srcActivityInst.getActivityDefId());
        RightGroupEnums movePerformerTo = actRightDef.getMovePerformerTo();

        if (movePerformerTo == null || movePerformerTo.equals("")) {
            movePerformerTo = RightGroupEnums.HISTORYPERFORMER;
        }
        RightGroupEnums moveReaderTo = actRightDef.getMoveReaderTo();
        if (moveReaderTo == null || moveReaderTo.equals("")) {
            moveReaderTo = RightGroupEnums.HISTORYREADER;
        }
        RightGroupEnums moveSponsorTo = actRightDef.getMoveSponsorTo();
        if (moveSponsorTo == null || moveSponsorTo.equals("")) {
            moveSponsorTo = RightGroupEnums.HISSPONSOR;
        }

        // process the performers
        try {
            String sqlGetPerformer = " WHERE ACTIVITYINST_ID = '" + instId + "' AND RIGHT_GRP_CODE = '" + RightGroupEnums.PERFORMER.getType() + "'";

            // 退回的时候会出现错误
            // add reader
            DbActivityInstPerson[] performers = actPersonMgr.loadByWhere(sqlGetPerformer);
            List list = new ArrayList();

            for (int i = 0; i < performers.length; i++) {
                DbActivityHistoryPerson historyPerson = historyPersonMgr.createActivityHistoryPerson(performers[i]);

                historyPerson.setActivityInstHistoryId(historyId);

                list.add(historyPerson);
                performers[i].setLastRightGrp(performers[i].getRightGrpCode());
                performers[i].setRightGrpCode(movePerformerTo);

            }
            actPersonMgr.save(performers);
            historyPersonMgr.save((DbActivityHistoryPerson[]) list.toArray(new DbActivityHistoryPerson[0]));

        } catch (SQLException e) {
            throw new BPMException("save the history performers failed!", e);
        }

        // process the readers
        try {
            String sqlGetReader = " WHERE ACTIVITYINST_ID = '" + instId + "' AND RIGHT_GRP_CODE = '" + RightGroupEnums.READER.getType() + "'";
            DbActivityInstPerson[] readers = actPersonMgr.loadByWhere(sqlGetReader);
            List list = new ArrayList();
            for (int i = 0; i < readers.length; i++) {
                DbActivityHistoryPerson historyPerson = historyPersonMgr.createActivityHistoryPerson(readers[i]);
                historyPerson.setActivityInstHistoryId(historyId);
                list.add(historyPerson);
                readers[i].setLastRightGrp(readers[i].getRightGrpCode());
                readers[i].setRightGrpCode(moveReaderTo);
            }
            actPersonMgr.save(readers);
            historyPersonMgr.save((DbActivityHistoryPerson[]) list.toArray(new DbActivityHistoryPerson[0]));

        } catch (SQLException e) {
            throw new BPMException("save the history readers failed!", e);
        }

        // 加入发起人组

        try {

            String sqlGetSponsor = " WHERE ACTIVITYINST_ID = '" + instId + "' AND RIGHT_GRP_CODE = '" + RightGroupEnums.SPONSOR.getType() + "'";

            // 删除原有活动发起人
            actPersonMgr.deleteByWhere(sqlGetSponsor);

            String personId = (String) ctx.get(RightCtx.USERID);
            DbActivityInstPerson currPerformer = actPersonMgr.createActivityInstPerson();
            currPerformer.setActivityInstPersonId(UUID.randomUUID().toString());
            currPerformer.setProcessInstId(srcActivityInst.getProcessInstId());
            currPerformer.setActivityInstId(activityInstId);
            currPerformer.setPersonActivityState(RightPerformStatus.FINISH);
            currPerformer.setPersonId(personId);
            currPerformer.setRightGrpCode(RightGroupEnums.SPONSOR);

            DbActivityHistoryPerson historyPerson = historyPersonMgr.createActivityHistoryPerson(currPerformer);
            historyPerson.setActivityInstHistoryId(historyId);

            historyPersonMgr.save(historyPerson);
            actPersonMgr.save(currPerformer);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);

    }


    public ReturnType splitActivityInst(String activityInstId, String[] subActivityInstIds, Map<RightCtx, Object> ctx) throws BPMException {
        EIActivityInst inst = activityInstMgr.loadByKey(activityInstId);

        for (int i = 0; i < subActivityInstIds.length; i++) {
            try {

                actPersonMgr.copy(activityInstId, subActivityInstIds[i]);

            } catch (SQLException e) {
                throw new BPMException("copy person from activityInst failOaed!", e);
            }
        }
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);

    }

    /**
     * 更换活动实例的当前办理人
     */
    public ReturnType changePerformer(String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        List<String> performers = new ArrayList((List<String>) ctx.get(RightCtx.PERFORMERS));
        EIActivityInst eiActivityInst = activityInstMgr.loadByKey(activityInstId);
        if (eiActivityInst == null) {
            throw new BPMException("The activity instance '" + activityInstId + "' not found!");
        }
        DbActivityDefRight activityDefRight = activityRightMgr.loadByKey(eiActivityInst.getActivityDefId());
        String processInstId = eiActivityInst.getProcessInstId();

        // 加入办理人
        try {
            // 先删除原来的办理人。
            actPersonMgr.deleteByWhere(" where ACTIVITYINST_ID='" + activityInstId + "'");
            for (Iterator it = performers.iterator(); it.hasNext(); ) {
                String performerId = (String) it.next();
                DbActivityInstPerson performer = actPersonMgr.createActivityInstPerson();
                performer.setActivityInstPersonId(UUID.randomUUID().toString());
                performer.setProcessInstId(processInstId);
                performer.setActivityInstId(activityInstId);
                performer.setPersonActivityState(RightPerformStatus.WAITING);
                performer.setPersonId(performerId);
                performer.setRightGrpCode(RightGroupEnums.PERFORMER);

                actPersonMgr.save(performer);
            }
        } catch (SQLException e) {
            throw new BPMException("save performer failed when process changePerformer!", e);
        }

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType routeTo(String activityInstId, String activityDefId, Map<RightCtx, Object> ctx) throws BPMException {
        List<String> performers = new ArrayList((List<String>) ctx.get(RightCtx.PERFORMERS));
        List readers = null;
        if (ctx.get(RightCtx.READERS) != null) {
            readers = new ArrayList((List) ctx.get(RightCtx.READERS));
        }
        EIActivityInst eiActivityInst = activityInstMgr.loadByKey(activityInstId);
        DbActivityDefRight activityDefRight = activityRightMgr.loadByKey(activityDefId);
        String processInstId = eiActivityInst.getProcessInstId();
        // 如果重发则将当前办理人加入到发送者组

        // 加入办理人
        try {
            for (Iterator it = performers.iterator(); it.hasNext(); ) {
                String performerId = (String) it.next();
                DbActivityInstPerson performer = actPersonMgr.createActivityInstPerson();
                performer.setActivityInstPersonId(UUID.randomUUID().toString());
                performer.setProcessInstId(processInstId);
                performer.setActivityInstId(activityInstId);
                performer.setPersonActivityState(RightPerformStatus.WAITING);
                performer.setPersonId(performerId);
                performer.setRightGrpCode(RightGroupEnums.PERFORMER);

                actPersonMgr.save(performer);
            }
        } catch (SQLException e) {
            throw new BPMException("save performer failed when process routeTo!", e);
        }

        if (readers != null) {
            // 加入读者
            try {
                for (Iterator it = readers.iterator(); it.hasNext(); ) {
                    String readerId = (String) it.next();
                    DbActivityInstPerson reader = actPersonMgr.createActivityInstPerson();
                    reader.setActivityInstPersonId(UUID.randomUUID().toString());
                    reader.setProcessInstId(processInstId);
                    reader.setActivityInstId(activityInstId);
                    reader.setPersonActivityState(RightPerformStatus.NULL);
                    reader.setPersonId(readerId);
                    reader.setRightGrpCode(RightGroupEnums.READER);

                    actPersonMgr.save(reader);
                }
            } catch (SQLException e) {
                throw new BPMException("save reader failed when process routeTo!", e);
            }
        }

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);

    }

    /**
     * 合并人员信息，将相同人员ID而且权限组相同的纪录合并在一起
     *
     * @param persons
     * @return
     */
    private DbActivityInstPerson[] combinePersons(DbActivityInstPerson[] persons) {
        Map result = new HashMap();
        for (int i = 0; i < persons.length; i++) {
            DbActivityInstPerson person = persons[i];
            String key = person.getPersonId() + "###" + person.getRightGrpCode();
            result.put(key, person);
        }
        return (DbActivityInstPerson[]) result.values().toArray(new DbActivityInstPerson[0]);
    }

    /**
     * 是否可以退回。
     *
     * @param activityInstId
     * @param ctx            上下文环境，包含权限引擎需要的信息
     * @return
     * @throws BPMException
     */
    public boolean canRouteBack(String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        return true;
    }

    /**
     * 权限中的退回操作 退回操作和路由类似，但是办理人和读者是从指定的历史活动中读取的
     *
     * @see com.ds.bpm.engine.RightEngine#routeBack(java.lang.String, java.lang.String, java.util.Map)
     */
    public ReturnType routeBack(String activityInstId, String activityInstHistoryId, Map<RightCtx, Object> ctx) throws BPMException {
        // 读取活动历史中的performers和readers
        String performersSql = " WHERE ACTIVITYINST_HISTORY_ID = '" + activityInstHistoryId + "' AND RIGHT_GRP_CODE = '" + RightGroupEnums.PERFORMER + "' ";
        String readersSql = " WHERE ACTIVITYINST_HISTORY_ID = '" + activityInstHistoryId + "' AND RIGHT_GRP_CODE = '" + RightGroupEnums.READER + "' ";
        DbActivityHistoryPerson[] historyPerformers = null;
        DbActivityHistoryPerson[] historyReaders = null;
        try {
            historyPerformers = historyPersonMgr.loadByWhere(performersSql);
            historyReaders = historyPersonMgr.loadByWhere(readersSql);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        EIActivityInstHistory history = activityInstHistoryMgr.loadByKey(activityInstHistoryId);
        if (history == null) {
            throw new BPMException("The history activity instance not found!");
        }

        // 将活动路由到指定的历史活动上面去
        EIActivityInst eiActivityInst = activityInstMgr.loadByKey(activityInstId);
        String processInstId = eiActivityInst.getProcessInstId();

        try {
            for (int i = 0; i < historyPerformers.length; i++) {
                DbActivityHistoryPerson historyPerformer = historyPerformers[i];
                String performerId = historyPerformer.getPersonId();
                DbActivityInstPerson performer = actPersonMgr.createActivityInstPerson();
                performer.setActivityInstPersonId(UUID.randomUUID().toString());
                performer.setProcessInstId(processInstId);
                performer.setActivityInstId(activityInstId);
                performer.setPersonActivityState(RightPerformStatus.WAITING);
                performer.setPersonId(performerId);
                performer.setRightGrpCode(RightGroupEnums.PERFORMER);

                actPersonMgr.save(performer);
            }
        } catch (SQLException e) {
            throw new BPMException("save performer failed when process routeTo!", e);
        }

        // 退回不考虑读者
        // try {
        // for (int i = 0; i < historyReaders.length; i++) {
        // DbActivityHistoryPerson historyReader = historyReaders[i];
        // String readerId = historyReader.getPersonId();
        // DbActivityInstPerson reader = actPersonMgr
        // .createActivityInstPerson();
        // reader.setActivityInstPersonId(UUID.randomUUID().toString());
        // reader.setProcessInstId(processInstId);
        // reader.setActivityInstId(activityInstId);
        // reader
        // .setPersonActivityState(RightPerformStatus.WAITING);
        // reader.setPersonId(readerId);
        // reader.setRightGrpCode(RightGroupEnums.READER);
        //
        // actPersonMgr.save(reader);
        // }
        // } catch (SQLException e) {
        // throw new BPMException("save reader failed when process routeTo!",
        // e);
        // }

        // 判断是否需要合并，如果需要则调用相关方法
        List activityInsts = workflowEngine.forecastCombinableActivityInsts(eiActivityInst.getActivityInstId(), history.getActivityDefId());
        if (activityInsts.size() != 0) {
            String suspendOrCombine = workflowEngine.forecastSuspendOrCombine(eiActivityInst.getActivityInstId(), history.getActivityDefId());
            if (suspendOrCombine.equals(ActivityInstSuSpend.SUSPEND.getType())) {
                // 挂起，不需要处理
            } else if (suspendOrCombine.equals(ActivityInstSuSpend.COMBINE.getType())) {
                // 合并，约定保留第一个活动实例，删除其他的，需要把人员信息合并倒第一个活动上
                String firstActivityInstId = null;
                // 取出所有的人员信息（所有活动实例上）
                StringBuffer sbSql = new StringBuffer();
                for (int i = 0; i < activityInsts.size(); i++) {
                    EIActivityInst actInst = (EIActivityInst) activityInsts.get(i);
                    if (i == 0) {
                        firstActivityInstId = actInst.getActivityInstId();
                    }
                    if (sbSql.length() != 0) {
                        sbSql.append(", ");
                    }
                    sbSql.append("'").append(actInst.getActivityInstId()).append("'");
                }
                sbSql.insert(0, " Where ACTIVITYINST_ID IN (");
                sbSql.append(")");
                DbActivityInstPerson[] persons = null;
                try {
                    persons = actPersonMgr.loadByWhere(sbSql.toString());
                    // 合并人员信息，去掉重复的内容
                    persons = combinePersons(persons);
                    // 删除所有人员信息
                    actPersonMgr.deleteByWhere(sbSql.toString());
                    // 重新添加合并后的人员信息。
                    for (int i = 0; i < persons.length; i++) {
                        DbActivityInstPerson person = actPersonMgr.createActivityInstPerson();
                        person.copy(persons[i]);
                        person.setActivityInstId(firstActivityInstId);
                        person.setLastRightGrp(RightGroupEnums.NULL);
                        persons[i] = person;
                    }
                    actPersonMgr.save(persons);
                } catch (SQLException e1) {
                    throw new BPMException("load the persons failed!", e1);
                }
            }
        }

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);

    }

    /**
     * 是否可以收回。<br>
     * 判断当前人是否是上一个活动节点的办理人
     *
     * @param activityInstId
     * @param ctx            上下文环境，包含权限引擎需要的信息
     * @return
     * @throws BPMException
     */
    public boolean canTakeBack(String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        String userId = (String) ctx.get(RightCtx.USERID);

        EIActivityInst inst = activityInstMgr.loadByKey(activityInstId);
        if (inst == null) {
            throw new BPMException("The activity instance not found!");
        }
        // 取得从上节点到该节点的路由实例
        List routeList = routeInstMgr.loadByWhere(" where TOACTIVITY_ID='" + activityInstId + "'");
        if (routeList == null || routeList.size() != 1) {
            return false;
        }
        EIRouteInst routeInst = (EIRouteInst) routeList.get(0);
        // 取得前一节点的历史活动ID
        String previousActivityInstHistoryId = routeInst.getFromActivityId();
        // 判断当前人是否是上一个活动节点的办理人
        String sql = " where ACTIVITYINST_HISTORY_ID = '" + previousActivityInstHistoryId + "'" + " and PERSON_ID = '" + userId + "'" + " and RIGHT_GRP_CODE = '" + RightGroupEnums.PERFORMER + "'";
        try {
            DbActivityHistoryPerson[] persons = historyPersonMgr.loadByWhere(sql);
            if (persons.length != 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new BPMException("load person from activityhistory failed!", e);
        }
    }

    /**
     * 权限中的收回操作<br>
     * 1.删除历史中的与权限相关信息<br>
     * 2.删除当前活动实例的办理人和读者<br>
     * 3.恢复活动实例中的办理人和读者<br>
     *
     * @see com.ds.bpm.engine.RightEngine#tackBack(java.lang.String, java.util.Map)
     */
    public ReturnType tackBack(String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        EIActivityInst inst = activityInstMgr.loadByKey(activityInstId);
        if (inst == null) {
            throw new BPMException("The activity instance not found!");
        }
        // 取得从上节点到该节点的路由实例
        List routeList = routeInstMgr.loadByWhere(" where TOACTIVITY_ID='" + activityInstId + "'");
        if (routeList == null || routeList.size() != 1) {
            throw new BPMException("can not found the corresponding route instance!");
        }
        EIRouteInst routeInst = (EIRouteInst) routeList.get(0);
        // 取得前一节点的历史活动ID
        String previousActivityInstHistoryId = routeInst.getFromActivityId();

        // 如果当前节点是分裂以后的就不能删除当前人员的办理信息 2013/10/1 lwz
        List routeHisList = routeInstMgr.loadByWhere(" where FROMACTIVITY_ID='" + previousActivityInstHistoryId + "'");
        if (routeHisList.size() == 1) {
            // 删除活动历史中的相关权限信息。
            String delHistorySql = " where ACTIVITYINST_HISTORY_ID = '" + previousActivityInstHistoryId + "'";
            try {
                historyPersonMgr.deleteByWhere(delHistorySql);
            } catch (SQLException e) {
                throw new BPMException("delete history person failed!", e);
            }
        }

        // 删除活动实例中的办理人和读者
        String delPersonSql = " where ACTIVITYINST_ID = '" + activityInstId + "' AND RIGHT_GRP_CODE IN ('" + RightGroupEnums.PERFORMER + "', '" + RightGroupEnums.READER + "') ";
        try {
            actPersonMgr.deleteByWhere(delPersonSql);
        } catch (SQLException e) {
            throw new BPMException("delete current performers and readers failed!", e);
        }

        String performSequence = (String) ctx.get(RightCtx.PERFORMSEQUENCE);

        // 如果不是多人办理可能还需要进行收回的恢复处理
        if (!performSequence.equals(ActivityDefPerformSequence.MEANWHILE)) {

            // 恢复活动实例中的办理人和读者
            String restorePersonSql = " where ACTIVITYINST_ID = '" + activityInstId + "' AND LAST_RIGHT_GRP IS NOT NULL ";

            // 恢复活动实例中的办理人和读者
            try {
                DbActivityInstPerson[] restorePersons = actPersonMgr.loadByWhere(restorePersonSql);
                for (int i = 0; i < restorePersons.length; i++) {
                    DbActivityInstPerson person = restorePersons[i];
                    person.setRightGrpCode(person.getLastRightGrp());
                    person.setPersonActivityState(RightPerformStatus.CURRENT);// 将状态设为已办理
                }
                actPersonMgr.save(restorePersons);
            } catch (SQLException e) {
                throw new BPMException("restore current performers and readers failed!", e);
            }

            // 判断是否需要合并 addby liwenzhang
            // 就是发给多人办理后其它人都办完了，到同步环节那头，同步环节都挂起，就剩一个人没有办然后这头取回这个人，同步环节还是挂起

            List<EIActivityInst> activityInsts = (List) ctx.get("combinableActivityInsts");

            String suspendOrCombine = (String) ctx.get("suspendOrCombine");

            if (activityInsts.size() != 0 && suspendOrCombine.equals(ActivityInstSuSpend.COMBINE.getType())) {
                this.combineActivityInsts(activityInsts, ctx);
            }
        }

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    /**
     * 查询是否能签收, 如果为单人办理，则只有一个人可以签收, 如果为多人顺序办理，则当前没有人处于CURRENT状态时可以签收, 如果为多人并行办理，则处于WAITING状态的人都可以签收,
     *
     * @param activityInstId
     * @param ctx            上下文环境，包含权限引擎需要的信息
     * @return
     */
    public boolean canSignReceive(String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        String userId = (String) ctx.get(RightCtx.USERID);
        EIActivityInst activityInst = activityInstMgr.loadByKey(activityInstId);
        DbActivityDefRight actDefRight = activityRightMgr.loadByKey(activityInst.getActivityDefId());
        if (actDefRight == null) {
            return false;
        }
        ActivityDefPerformSequence performSequence = actDefRight.getPerformSequence();
        // 查询当前人是否是办理人而且活动状态为等待办理
        String sql = " where ACTIVITYINST_ID = '" + activityInstId + "'" + " and PERSON_ID = '" + userId + "'" +
                // added by chenjie in 2004-03-09
                " and RIGHT_GRP_CODE = '" + RightGroupEnums.PERFORMER + "'" + " and PERSON_ACTIVITY_STATE = '" + RightPerformStatus.WAITING + "'";

        DbActivityInstPerson[] persons;
        try {
            if (performSequence.equals(ActivityDefPerformSequence.SEQUENCE)) {
                // 多人顺序办理,还需要检查当前没有人处于CURRENT状态
                String checkSql = " where ACTIVITYINST_ID = '" + activityInstId + "'" + " and RIGHT_GRP_CODE = '" + RightGroupEnums.PERFORMER + "'" + " and PERSON_ACTIVITY_STATE = '" + RightPerformStatus.CURRENT + "' ";
                persons = actPersonMgr.loadByWhere(checkSql);
                // 如果有人已经处于办理状态了，则不能签收
                if (persons.length != 0) {
                    return false;
                }
            }
            persons = actPersonMgr.loadByWhere(sql);
            if (persons.length == 0) {
                return false;
            } else {
                return true;
            }
        } catch (SQLException e) {
            throw new BPMException("load person from activityInst failed!", e);
        }
    }

    /**
     * 签收活动实例，启动该活动实例。 将当前办理人置为正在办理! 需要清空活动实例上面的对应人员的LAST_RIGHT_GRP内的内容, 如果为单人办理，则只有一个人可以签收，其他人置为已完成,
     * 如果为多人顺序办理，不改变其他人状态, 如果为多人并行办理，不改变其他人状态,
     *
     * @param activityInstId 活动实例的标识
     * @param ctx            上下文环境，包含权限引擎需要的信息
     * @return
     * @throws BPMException
     */
    public ReturnType signReceive(String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        String userId = (String) ctx.get(RightCtx.USERID);
        EIActivityInst activityInst = activityInstMgr.loadByKey(activityInstId);

        DbActivityDefRight actDefRight = activityRightMgr.loadByKey(activityInst.getActivityDefId());
        ActivityDefPerformSequence performSequence = actDefRight.getPerformSequence();

        // 将当前办理人置为正在办理
        // String sql= " where ACTIVITYINST_ID = '" + activityInstId + "' and
        // PERSON_ID = '" + userId + "' and PERSON_ACTIVITY_STATE = '" +
        // RightPerformStatus.WAITING + "'";
        // modified by chenjie.
        // 指定RIGHT_GRP_CODE为PERFORMER，否则可能取的不是PERFORMER，将会引起一系列的连锁反应。
        String sql = " where ACTIVITYINST_ID = '" + activityInstId + "' and RIGHT_GRP_CODE = '" + RightGroupEnums.PERFORMER + "' and PERSON_ID = '" + userId + "' and PERSON_ACTIVITY_STATE = '" + RightPerformStatus.WAITING
                + "'";
        DbActivityInstPerson[] persons;
        try {
            persons = actPersonMgr.loadByWhere(sql);
            if (persons.length == 0) {
                return new ReturnType(ReturnType.MAINCODE_FAIL);
            } else {
                persons[0].setPersonActivityState(RightPerformStatus.CURRENT);
                actPersonMgr.save(persons[0]);

                EIActivityInst actInst = activityInstMgr.loadByKey(activityInstId);
                List routeInsts = routeInstMgr.getRouteInsts(actInst);

                for (Iterator it = routeInsts.iterator(); it.hasNext(); ) {
                    EIRouteInst routeInst = (EIRouteInst) it.next();
                    DbActivityHistoryPerson historyPerson = historyPersonMgr.createActivityHistoryPerson(persons[0]);
                    historyPerson.setActivityInstHistoryId(routeInst.getFromActivityId());
                    historyPerson.setRightGrpCode(RightGroupEnums.READER);
                    historyPersonMgr.save(historyPerson);
                }
            }
        } catch (SQLException e) {
            throw new BPMException("load person from activityInst failed!", e);
        }

        // 单人办理，将其他人员置为读者
        if (performSequence.equals(ActivityDefPerformSequence.FIRST)) {
            String finishOtherSql = " where ACTIVITYINST_ID = '" + activityInstId + "'" + " and RIGHT_GRP_CODE = '" + RightGroupEnums.PERFORMER + "'" + " and PERSON_ACTIVITY_STATE = '" + RightPerformStatus.WAITING + "'";
            try {
                persons = actPersonMgr.loadByWhere(finishOtherSql);
                for (int i = 0; i < persons.length; i++) {
                    persons[i].setRightGrpCode(RightGroupEnums.READER);
                }
                actPersonMgr.save(persons);
            } catch (SQLException e) {
                throw new BPMException("load person from activityInst failed!", e);
            }
        }

        // 清除活动实例中的所有的LAST_RIGHT_GRP
        String restorePersonSql = " where ACTIVITYINST_ID = '" + activityInstId + "' AND LAST_RIGHT_GRP IS NOT NULL ";
        try {
            DbActivityInstPerson[] restorePersons = actPersonMgr.loadByWhere(restorePersonSql);
            for (int i = 0; i < restorePersons.length; i++) {
                DbActivityInstPerson person = restorePersons[i];
                person.setLastRightGrp(RightGroupEnums.NULL);
            }
            actPersonMgr.save(restorePersons);
        } catch (SQLException e) {
            throw new BPMException("restore current performers and readers failed!", e);
        }

        // 对于自动启动的活动，第一个活动办理人签收时，需要将此人作为拟稿人填入流程实例的流程启动人
        EIAttributeInst attInst = activityInst.getProcessInst().getAttribute(Attributetype.RIGHT + "." + ProcessInstAtt.PROCESS_INSTANCE_STARTER);
        // 没有拟稿人，此签收人会成为拟稿人
        if (attInst == null) {
            // 2。在扩展属性中写入流程启动人
            EIProcessInst processInst = activityInst.getProcessInst();
            attInst = EIAttributeInstManager.getInstance().createAttributeInst();
            Person person = null;
            try {
                person = orgManager.getPersonByID(userId);
                attInst.setId(UUID.randomUUID().toString());
                attInst.setInterpretedValue(person);
                attInst.setInterpretClass(AttributeInterpretClass.PERSON.getType());
                attInst.setName(ProcessInstAtt.PROCESS_INSTANCE_STARTER.getType());
                attInst.setType(Attributetype.RIGHT.getType());

                activityInst.getProcessInst().setAttribute(Attributetype.RIGHT.getType(), attInst);
                processInstMgr.save(processInst);
            } catch (PersonNotFoundException e) {
                log.error("person not found : " + userId, e);
            }
        }
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);

    }

    public ReturnType combineActivityInsts(List<EIActivityInst> activityInsts, Map<RightCtx, Object> ctx) throws BPMException {
        List<String> activityInstIds = new ArrayList<String>();
        for (int k = 0; k < activityInsts.size(); k++) {
            activityInstIds.add(activityInsts.get(k).getActivityInstId());
        }
        return combineActivityInsts(activityInstIds.toArray(new String[]{}), ctx);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ds.bpm.engine.RightEngine#combineActivityInsts(java.lang.String[], java.util.Map)
     */
    public ReturnType combineActivityInsts(String[] activityInstIds, Map<RightCtx, Object> ctx) throws BPMException {
        // 合并，约定保留第一个活动实例，删除其他的，需要把人员信息合并倒第一个活动上
        String firstActivityInstId = null;
        // 取出所有的人员信息（所有活动实例上）
        StringBuffer sbSql = new StringBuffer();
        for (int i = 0; i < activityInstIds.length; i++) {
            String activityInstId = activityInstIds[i];
            EIActivityInst actInst = (EIActivityInst) this.activityInstMgr.loadByKey(activityInstId);
            if (i == 0) {
                firstActivityInstId = actInst.getActivityInstId();
            }
            if (sbSql.length() != 0) {
                sbSql.append(", ");
            }
            sbSql.append("'").append(actInst.getActivityInstId()).append("'");
        }
        sbSql.insert(0, " Where ACTIVITYINST_ID IN (");
        sbSql.append(")");
        DbActivityInstPerson[] persons = null;
        try {
            persons = actPersonMgr.loadByWhere(sbSql.toString());
            // 合并人员信息，去掉重复的内容
            persons = combinePersons(persons);
            // 删除所有人员信息
            actPersonMgr.deleteByWhere(sbSql.toString());
            // 重新添加合并后的人员信息。
            for (int i = 0; i < persons.length; i++) {
                DbActivityInstPerson person = actPersonMgr.createActivityInstPerson();
                person.copy(persons[i]);
                person.setActivityInstId(firstActivityInstId);
                person.setLastRightGrp(RightGroupEnums.NULL);
                persons[i] = person;
            }
            actPersonMgr.save(persons);
        } catch (SQLException e1) {
            throw new BPMException("load the persons failed!", e1);
        }

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    /**
     * 活动挂起
     *
     * @see com.ds.bpm.engine.RightEngine#suspendActivityInst(java.lang.String, java.util.Map)
     */
    public ReturnType suspendActivityInst(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        // no operation
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);

    }

    /**
     * 继续活动实例
     *
     * @see com.ds.bpm.engine.RightEngine#resumeActivityInst(java.lang.String, java.util.Map)
     */
    public ReturnType resumeActivityInst(String activityInstID, Map<RightCtx, Object> ctx) throws BPMException {
        // no operation
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);

    }

    /**
     * 流程实例挂起
     *
     * @see com.ds.bpm.engine.RightEngine#suspendProcessInst(java.lang.String, java.util.Map)
     */
    public ReturnType suspendProcessInst(String processInstID, Map<RightCtx, Object> ctx) throws BPMException {
        // no operation
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);

    }

    /**
     * 继续流程实例
     *
     * @see com.ds.bpm.engine.RightEngine#resumeProcessInst(java.lang.String, java.util.Map)
     */
    public ReturnType resumeProcessInst(String processInstID, Map<RightCtx, Object> ctx) throws BPMException {
        // no operation
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);

    }

    /**
     * 中止流程实例
     *
     * @see com.ds.bpm.engine.RightEngine#abortProcessInst(java.lang.String, java.util.Map)
     */
    public ReturnType abortProcessInst(String processInstID, Map<RightCtx, Object> ctx) throws BPMException {
        // no operation
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);

    }

    /**
     * 流程实例完成
     *
     * @see com.ds.bpm.engine.RightEngine#completeProcessInst(java.lang.String, java.util.Map)
     */
    public ReturnType completeProcessInst(String processInstID, Map<RightCtx, Object> ctx) throws BPMException {

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);

    }

    /**
     * 删除流程实例
     *
     * @see com.ds.bpm.engine.RightEngine#deleteProcessInst(java.lang.String, java.util.Map)
     */
    public ReturnType deleteProcessInst(String processInstID, Map<RightCtx, Object> ctx) throws BPMException {
        // delete the person in table RT_ACTIVITY_PERSON and
        // RT_ACTIVITYHISTORY_PERSON
        String sqlDelActPerson = " WHERE PROCESSINST_ID = '" + processInstID + "'";
        String sqlDelHistoryPerson = " WHERE PROCESSINST_ID = '" + processInstID + "'";
        try {
            actPersonMgr.deleteByWhere(sqlDelActPerson);
            historyPersonMgr.deleteByWhere(sqlDelHistoryPerson);
        } catch (SQLException e) {
            throw new BPMException("delete person failed when delete process instance : " + processInstID, e);
        }
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);

    }

    @Override
    public ActivityDefRight getActivityDefRightAttribute(String activityDefId) throws BPMException {
        DbActivityDefRight right = activityRightMgr.loadByKey(activityDefId);
        ActivityDefRightProxy activityDefRight = null;
        if (right != null) {
            activityDefRight = new ActivityDefRightProxy(right, this.systemCode);
        }
        return activityDefRight;

    }

    /**
     * 取得活动办理人的候选人， 解析活动定义的公式来确定候选人！
     *
     * @param activityDefId 指定的活动ID
     * @param ctx           上下文环境，包括
     * @return
     * @throws BPMException
     */
    public List getPerformerCandidate(String activityDefId, Map<RightCtx, Object> ctx) throws BPMException {
        EIActivityDef activityDef = activityDefMgr.loadByKey(activityDefId);
        DbActivityDefRight rightDef = activityRightMgr.loadByKey(activityDefId);
        // 支持新的公式摸板
        EIAttributeDef attDef = rightDef.getPerformerSelectedAtt();
        return getParticipant(attDef, ctx);

        // String selectedId = rightDef.getPerformerSelectedId();

        // return getParticipant(selectedId, ctx);
    }

    /**
     * 取得活动的读者的候选人 解析活动定义的公式来确定候选人！
     *
     * @param activityDefId 指定的活动ID
     * @param ctx           上下文环境
     * @return
     * @throws BPMException
     */
    private List getReaderCandidate(String activityDefId, Map<RightCtx, Object> ctx) throws BPMException {
        EIActivityDef activityDef = activityDefMgr.loadByKey(activityDefId);
        DbActivityDefRight rightDef = activityRightMgr.loadByKey(activityDefId);
        // 支持新的公式摸板
        EIAttributeDef attDef = rightDef.getReaderSelectedAtt();
        return getParticipant(attDef, ctx);
        // String selectedId = rightDef.getReaderSelectedId();

        // return getParticipant(selectedId, ctx);
    }


    private Map<RightCtx, Object> fillInUserID(Map ctx) {
        Map result = ctx;
        if (result == null) {
            result = new HashMap();
        }

        result.put(RightCtx.USERID, this.getJdsClient().getConnectInfo().getUserID());
        return result;
    }

    /**
     * 支持新的公式摸板，可以有多个公式组合在一起，每个公式可以设置参数
     *
     * @param participantAttribute 公式定义的扩展属性
     * @param ctx
     * @return
     * @throws BPMException
     */
    public List<Person> getParticipant(EIAttributeDef participantAttribute, Map<RightCtx, Object> ctx) throws BPMException {
        if (participantAttribute == null) {
            return new ArrayList();
        }
        ctx = this.fillInUserID(ctx);

        String selectedId = participantAttribute.getValue();
        // 此属性不为空，说明是旧版本的公式系统，继续调用旧的公式系统执行
        if (selectedId != null && !selectedId.equals("")) {
            return getParticipant(selectedId, ctx);
        }
        List child = participantAttribute.getChildren();
        if (child.size() == 0) {
            return new ArrayList();
        }

        List result = new ArrayList();
        for (int i = 0; i < child.size(); i++) {
            EIAttributeDef formulaAtt = (EIAttributeDef) child.get(i);
            String formulaId = formulaAtt.getValue();
            Object o = null;
            if (formulaId.equalsIgnoreCase("CUSTOMFORMULA")) {
                // 高级公式
                EIAttributeDef customFormulaAtt = (EIAttributeDef) formulaAtt.getChild("CUSTOMFORMULA");
                String expression = customFormulaAtt.getValue();
                if (expression != null && !expression.equals("")) {
                    o = executeExpression(expression, ctx);
                }
            } else {
                // 执行新的公式摸板
                o = executeExpression(formulaAtt, ctx);
            }

            if (o == null) {
                continue;
            }

            if ((o instanceof Person) || o instanceof Org) {
                result.add(o);
            } else if (o instanceof Person[]) {
                Person[] persons = (Person[]) o;
                for (int j = 0; j < persons.length; j++) {
                    result.add(persons[j]);
                }
            } else if (o instanceof Org[]) {
                Org[] orgs = (Org[]) o;
                for (int j = 0; j < orgs.length; j++) {
                    result.add(orgs[j]);
                }
            }
        }
        result = combineParticipant(result);
        return result;
    }

    /**
     * @param src
     * @return
     */
    private List combineParticipant(List src) {
        List result = new ArrayList();
        for (int i = 0; i < src.size(); i++) {
            if (!result.contains(src.get(i))) {
                result.add(src.get(i));
            }
        }
        return result;
    }

    private List getParticipant(String participantSelectedId, Map<RightCtx, Object> ctx) throws BPMException {
        String selectedId = participantSelectedId;
        DbParticipantSelect selected;
        String expression = null;
        if (selectedId == null) {
            return new ArrayList();
        }
        if (!isUUID(selectedId)) {
            expression = selectedId;
        } else {
            try {
                selected = participantMgr.loadByKey(selectedId);
            } catch (SQLException e) {
                throw new BPMException("load participant " + selectedId + " failed", e);
            }
            if (selected == null) {
                return new ArrayList();
            }
            expression = selected.getFormula();
        }
        if (expression == null) {
            return new ArrayList();
        }
        List list;
        Object o = executeExpression(expression, ctx);
        if (o == null) {
            return new ArrayList();
        }
        list = new ArrayList();
        if ((o instanceof Person) || o instanceof Org) {
            list.add(o);
        } else if (o instanceof Person[]) {
            Person[] persons = (Person[]) o;
            for (int i = 0; i < persons.length; i++) {
                list.add(persons[i]);
            }
        } else if (o instanceof Org[]) {
            Org[] orgs = (Org[]) o;
            for (int i = 0; i < orgs.length; i++) {
                list.add(orgs[i]);
            }
        }
        return list;
    }

    private boolean isUUID(String uuid) {
        if (uuid.length() == 36) {
            if (uuid.charAt(8) == '-' && uuid.charAt(13) == '-') {
                return true;
            }
        }
        return false;
    }

    private final java.util.concurrent.locks.ReentrantLock expressionLock = new java.util.concurrent.locks.ReentrantLock();

    private Object executeExpression(EIAttributeDef formulaAtt, Map<RightCtx, Object> ctx) throws BPMException {
        expressionLock.lock();
        try {
            ExpressionParser parser = JDSExpressionParserManager.getExpressionParser(ctx);
            addCurrentActivityInst(parser, ctx);
            addCurrentProcessInst(parser, ctx);
            String selectedId = formulaAtt.getValue();
            DbParticipantSelect selected;
            try {
                selected = participantMgr.loadByKey(selectedId);
            } catch (SQLException e) {
                throw new BPMException("load participant " + selectedId + " failed", e);
            }
            if (selected == null) {
                return new ArrayList();
            }
            String expression = selected.getFormula();
            if (expression == null || expression.trim().isEmpty()) {
                return new ArrayList();
            }
            EIAttributeDef parameterAtt = (EIAttributeDef) formulaAtt.getChild(formulaAtt.getValue());
            if (parameterAtt != null && parameterAtt.getName() != null && !parameterAtt.getName().equals("") && parameterAtt.getValue() != null && !parameterAtt.getValue().equals("")) {
                StringTokenizer stParameter = new StringTokenizer(parameterAtt.getValue(), ";");
                while (stParameter.hasMoreTokens()) {
                    String parameterString = stParameter.nextToken();
                    int _index = parameterString.indexOf("=");
                    if (_index == -1) {
                        continue;
                    }
                    String parameterName = parameterString.substring(0, _index).trim();
                    String parameterValue = parameterString.substring(_index + 1).trim();
                    if (parameterName.isEmpty()) {
                        continue;
                    }
                    if (ctx.containsKey(parameterName)) {
                        parser.addVariableAsObject(parameterName, ctx.get(parameterName));
                    } else {
                        parser.addVariableAsObject(parameterName, parameterValue);
                    }
                }
            }
            boolean result = parser.parseExpression(expression);
            if (result == false) {
                log.warn("expression parse error: " + parser.getErrorInfo());
                return null;
            } else {
                Object o = parser.getValueAsObject();
                if (parser.hasError() == true) {
                    log.error(parser.getErrorInfo());
                }
                return o;
            }
        } finally {
            expressionLock.unlock();
        }
    }

    public List getParameter(String parameterName, String activityDefId) throws BPMException {

        DbActivityDefRight rightDef = activityRightMgr.loadByKey(activityDefId);
        // 支持新的公式摸板
        EIAttributeDef attDef = rightDef.getPerformerSelectedAtt();
        List child = attDef.getChildren();
        List<String> valueList = new ArrayList<String>();
        for (int i = 0; i < child.size(); i++) {
            EIAttributeDef formulaAtt = (EIAttributeDef) child.get(i);
            String formulaId = formulaAtt.getValue();
            EIAttributeDef parameterAtt = (EIAttributeDef) formulaAtt.getChild(formulaAtt.getValue());
            if (parameterAtt != null && parameterAtt.getName() != null && !parameterAtt.getName().equals("") && parameterAtt.getValue() != null && !parameterAtt.getValue().equals("")) {
                StringTokenizer stParameter = new StringTokenizer(parameterAtt.getValue(), ";");
                while (stParameter.hasMoreTokens()) {
                    String parameterString = stParameter.nextToken();
                    int _index = parameterString.indexOf("=");
                    if (_index == -1) {
                        continue;
                    }
                    String name = parameterString.substring(0, _index);
                    String parameterValue = parameterString.substring(_index + 1, parameterString.length());
                    if (parameterName.equals(name)) {
                        StringTokenizer st = new StringTokenizer(parameterValue, ":");

                        while (st.hasMoreTokens()) {
                            valueList.add(st.nextToken());
                        }
                    }

                }
            }
        }

        return valueList;
    }

    private Object executeExpression(String expression, Map<RightCtx, Object> ctx) {
        ExpressionParser parser = JDSExpressionParserManager.getExpressionParser(ctx);
        addCurrentActivityInst(parser, ctx);
        addCurrentProcessInst(parser, ctx);
        boolean result = parser.parseExpression(expression);
        if (result == false) {
            log.warn("expression parse error: " + parser.getErrorInfo());
            return null;
        } else {
            Object o = parser.getValueAsObject();
            if (parser.hasError() == true) {
                log.error(parser.getErrorInfo());
            }
            return o;
        }
    }

    /**
     * 向公式解析器加入当前活动实例
     *
     * @param parser
     * @param ctx
     */
    private void addCurrentActivityInst(ExpressionParser parser, Map<RightCtx, Object> ctx) {
        String activityInstId = (String) ctx.get(RightCtx.ACTIVITYINST_ID);
        if (activityInstId == null) {
            parser.addVariableAsObject(RightCtx.CURRENT_ACTIVITYINST.getType(), null);
            return;
        }

        try {
            EIActivityInst activityInst = (EIActivityInst) activityInstMgr.loadByKey(activityInstId);
            parser.addVariableAsObject(RightCtx.CURRENT_ACTIVITYINST.getType(), activityInst);
        } catch (BPMException e) {
            log.warn("load activiytInst failed!", e);
        }
    }

    /**
     * 向公式解析器加入当前流程实例
     *
     * @param parser
     * @param ctx
     */
    private void addCurrentProcessInst(ExpressionParser parser, Map<RightCtx, Object> ctx) {

        String processInstId = (String) ctx.get(RightCtx.PROCESSINST_ID);
        if (processInstId == null) {
            parser.addVariableAsObject(RightCtx.CURRENT_PROCESSINST.getType(), null);
            return;
        }

        try {
            EIProcessInst processInst = (EIProcessInst) processInstMgr.loadByKey(processInstId);
            parser.addVariableAsObject(RightCtx.CURRENT_PROCESSINST.getType(), processInst);
        } catch (BPMException e) {
            log.warn("load processInst failed!", e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ds.bpm.engine.RightEngine#getProcessDefVersionRightAttribute(java.lang.String, java.lang.String,
     * java.util.Map)
     */
    public Object getProcessDefVersionRightAttribute(String processDefVersionId, ProcessDefVersionAtt attName, Map<RightCtx, Object> ctx) throws BPMException {
        // TODO 流程定义权限属性，暂时没有
        return null;
    }


    /*
     * (non-Javadoc)
     *
     * @see com.ds.bpm.engine.RightEngine#getRouteDefRightAttribute(java.lang.String, java.lang.String,
     * java.util.Map)
     */
    public Object getRouteDefRightAttribute(String routeDefId, AttributeName attName, Map<RightCtx, Object> ctx) throws BPMException {
        // TODO 路由定义权限属性，暂时没有
        return null;
    }

    /**
     * 活动实例的权限属性
     *
     * @param activityInstId 活动实例的ID
     * @param group          属性名称
     * @param ctx
     * @return
     * @throws BPMException
     */
    public List<Person> getActivityInstRightAttribute(String activityInstId, ActivityInstRightAtt group, Map<RightCtx, Object> ctx) throws BPMException {

        String sql = " where ACTIVITYINST_ID = '" + activityInstId + "' " + " and RIGHT_GRP_CODE in ('" + group + "','" + group + "|" + RightGroupEnums.NORIGHT + "')";
        return getPersonsFromActivityPerson(sql);
    }

    private List<Person> getPersonsFromActivityPerson(String sql) throws BPMException {
        DbActivityInstPerson[] persons = null;
        try {
            persons = actPersonMgr.loadByWhere(sql);
        } catch (SQLException e) {
            throw new BPMException("load persons from activity failed!", e);
        }
        List personList = new ArrayList();
        for (int i = 0; i < persons.length; i++) {
            String personId = persons[i].getPersonId();
            Person person;
            try {
                person = orgManager.getPersonByID(personId);
                personList.add(person);
            } catch (PersonNotFoundException e) {
                log.warn("can't load person : " + personId, e);
            }
        }
        return personList;
    }

    /**
     * 活动实例历史的权限属性
     *
     * @param activityInstHistoryId 活动实例历史的ID
     * @param group                 属性名称
     * @param ctx
     * @return
     * @throws BPMException
     */
    public List<Person> getActivityInstHistoryRightAttribute(String activityInstHistoryId, ActivityInstHistoryAtt group, Map<RightCtx, Object> ctx) throws BPMException {

        String sql = " where ACTIVITYINST_HISTORY_ID = '" + activityInstHistoryId + "' " + " and RIGHT_GRP_CODE in ('" + group + "','" + group + "|" + RightGroupEnums.NORIGHT + "')";
        return getPersonsFromActivityHistoryPerson(sql);

    }

    private List<Person> getPersonsFromActivityHistoryPerson(String sql) throws BPMException {
        DbActivityHistoryPerson[] persons = null;
        try {
            persons = historyPersonMgr.loadByWhere(sql);
        } catch (SQLException e) {
            throw new BPMException("load persons from activityhistory failed!", e);
        }
        List personList = new ArrayList();
        for (int i = 0; i < persons.length; i++) {
            String personId = persons[i].getPersonId();
            Person person;
            try {
                person = orgManager.getPersonByID(personId);
                personList.add(person);
            } catch (PersonNotFoundException e) {
                log.warn("can't load person : " + personId, e);
            }
        }
        return personList;
    }

    /**
     * 判断当前人对活动实例的权限
     *
     * @param activityInstId
     * @param ctx            可以支持CTX参数为CTX_PERMISSION<br>
     *                       RightPermission.PERFORM<br>
     *                       RightPermission.READ<br>
     *                       RightPermission.ANY<br>
     * @return
     * @throws BPMException
     */
    public boolean queryPermissionToActivityInst(String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        String userId = (String) ctx.get(RightCtx.USERID);
        RightPermission permission = (RightPermission) ctx.get(RightCtx.PERMISSION);
        String sql = null;
        // 办理权限
        if (RightPermission.PERMISSION_PERFORM.equals(permission)) {
            sql = " where ACTIVITYINST_ID = '" + activityInstId + "'" + " and PERSON_ID = '" + userId + "'" + " and RIGHT_GRP_CODE in ('" + RightGroupEnums.PERFORMER.getType() + "','" + RightGroupEnums.SPONSOR.getType() + "')"
                    + " and PERSON_ACTIVITY_STATE = '" + RightPerformStatus.CURRENT.getType() + "'";
        } else if (RightPermission.PERMISSION_READ.equals(permission)) {
            sql = " where ACTIVITYINST_ID = '" + activityInstId + "'" + " and PERSON_ID = '" + userId + "'" + " and RIGHT_GRP_CODE = '" + RightGroupEnums.READER.getType() + "'";
        } else if (RightPermission.PERMISSION_ANY.equals(permission)) {
            sql = " where ACTIVITYINST_ID = '" + activityInstId + "'" + " and PERSON_ID = '" + userId + "'";
        }
        if (sql == null) {
            return false;
        }
        try {
            DbActivityInstPerson[] persons;
            persons = actPersonMgr.loadByWhere(sql);
            if (persons.length > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new BPMException("load activity persons failed!", e);
        }

    }

    /**
     * 得到当前人对活动实例的所有权限列表
     *
     * @param activityInstId
     * @param ctx
     * @return
     * @throws BPMException
     */
    public List<RightGroupEnums> queryAllPermissionToActivityInst(String activityInstId, Map<RightCtx, Object> ctx) throws BPMException {
        String userId = (String) ctx.get(RightCtx.USERID);

        String sql = null;
        // 办理权限
        sql = " where ACTIVITYINST_ID = '" + activityInstId + "'" + " and PERSON_ID = '" + userId + "'";
        Map rightGroup = new HashMap();
        try {
            DbActivityInstPerson[] persons;
            persons = actPersonMgr.loadByWhere(sql);
            for (int i = 0; i < persons.length; i++) {
                rightGroup.put(persons[i].getRightGrpCode(), persons[i].getRightGrpCode());
            }
            return new ArrayList(rightGroup.keySet());
        } catch (SQLException e) {
            throw new BPMException("load activity persons failed!", e);
        }

    }

    public ReturnType endRead(String activityInstId, String activityHistoryInstID, Map<RightCtx, Object> ctx) throws BPMException {

        String userId = (String) ctx.get(RightCtx.USERID);

        String sql = null;
        // 办理权限
        sql = " where ACTIVITYINST_ID = '" + activityInstId + "'" + " and PERSON_ID = '" + userId + "'" + " AND RIGHT_GRP_CODE = '" + RightGroupEnums.READER.getType() + "' AND PERSON_ACTIVITY_STATE='" + RightPerformStatus.READ.getType() + "'";

        DbActivityInstPerson[] persons = null;
        try {
            persons = actPersonMgr.loadByWhere(sql);
            if (persons != null && persons.length > 0) {
                DbActivityInstPerson person = persons[0];
                person.setRightGrpCode(RightGroupEnums.HISTORYREADER);
                person.setPersonActivityState(RightPerformStatus.ENDREAD);
                actPersonMgr.save(person);
                return saveActivityHistoryInst(activityInstId, activityHistoryInstID, ctx);
            }

        } catch (SQLException e) {
            throw new BPMException("ReadEnd activity persons failed!", e);

        }
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType copyTo(List<ActivityInst> activityInstList, List<String> readers) throws BPMException {
        try {
            for (int k = 0; k < readers.size(); k++) {
                String activityInstId = activityInstList.get(k).getActivityInstId();
                EIActivityInst eiActivityInst = activityInstMgr.loadByKey(activityInstId);
                String processInstId = eiActivityInst.getProcessInstId();
                String performerId = (String) readers.get(k);
                DbActivityInstPerson performer = actPersonMgr.createActivityInstPerson();
                performer.setActivityInstPersonId(UUID.randomUUID().toString());
                performer.setProcessInstId(processInstId);
                performer.setActivityInstId(activityInstId);
                performer.setPersonActivityState(RightPerformStatus.READ);
                performer.setPersonId(performerId);
                performer.setRightGrpCode(RightGroupEnums.READER);
                actPersonMgr.save(performer);
            }
        } catch (SQLException e) {
            throw new BPMException("save performer failed when process routeTo!", e);

        }
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType clearHistory(String activityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {
        String userId = (String) ctx.get(RightCtx.USERID);
        String sql = null;
        // 将活动历史权限删除
        sql = " where ACTIVITYINST_HISTORY_ID = '" + activityInstHistoryID + "'" + " and PERSON_ID = '" + userId + "'";

        try {
            historyPersonMgr.deleteByWhere(sql);

        } catch (SQLException e) {
            throw new BPMException("deleteHistory  persons failed!", e);

        }
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType deleteHistory(String activityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {
        String userId = (String) ctx.get(RightCtx.USERID);
        String rightType = (String) ctx.get(RightGroupEnums.NULL);
        String sql = null;
        if (rightType == null) {
            sql = " where ACTIVITYINST_HISTORY_ID = '" + activityInstHistoryID + "'" + " and PERSON_ID = '" + userId + "'  and RIGHT_GRP_CODE in ('" + RightGroupEnums.PERFORMER.getType() + "','" + RightGroupEnums.READER.getType() + "','"
                    + RightGroupEnums.HISTORYPERFORMER + "')";

        } else {
            // 将活动历史权限删除
            sql = " where ACTIVITYINST_HISTORY_ID = '" + activityInstHistoryID + "'" + " and PERSON_ID = '" + userId + "'  and RIGHT_GRP_CODE in ('" + rightType + "','HISTORYPERFORMER')";

        }

        DbActivityHistoryPerson[] persons = null;
        try {
            persons = historyPersonMgr.loadByWhere(sql);
            if (persons != null && persons.length > 0) {
                DbActivityHistoryPerson person = persons[0];
                person.setActivityInstHistoryId(activityInstHistoryID);
                person.setPersonId(userId);
                if (person.getRightGrpCodeStr().indexOf("|") == -1) {
                    person.setRightGrpCodeStr(person.getRightGrpCodeStr() + "|" + RightGroupEnums.NORIGHT);
                }

                historyPersonMgr.save(person);
            }
        } catch (SQLException e) {
            throw new BPMException("deleteHistory  persons failed!", e);

        }
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType restoreHistory(String activityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {
        String sql = null;
        String rightType = (String) ctx.get(RightGroupEnums.NULL);
        String userId = (String) ctx.get(RightCtx.USERID);

        if (rightType == null || rightType.equals("")) {
            sql = " where ACTIVITYINST_HISTORY_ID = '" + activityInstHistoryID + "'" + " and PERSON_ID = '" + userId + "'  and RIGHT_GRP_CODE in ('" + RightGroupEnums.PERFORMER + "|" + RightGroupEnums.NORIGHT + "','"
                    + RightGroupEnums.READER + "|" + RightGroupEnums.NORIGHT + "')";
        } else {
            sql = " where ACTIVITYINST_HISTORY_ID = '" + activityInstHistoryID + "'" + " and PERSON_ID = '" + userId + "'  and RIGHT_GRP_CODE in ('" + rightType + "|" + RightGroupEnums.NORIGHT + "')";

        }

        DbActivityHistoryPerson[] persons = null;
        try {
            persons = historyPersonMgr.loadByWhere(sql);
            if (persons != null && persons.length > 0) {
                DbActivityHistoryPerson person = persons[0];
                person.setActivityInstHistoryId(activityInstHistoryID);
                person.setPersonId(userId);

                if (person.getRightGrpCodeStr().indexOf("|") > -1) {
                    person.setRightGrpCodeStr(person.getRightGrpCodeStr().substring(0, person.getRightGrpCodeStr().indexOf("|")));
                }
                historyPersonMgr.save(person);
            }
        } catch (SQLException e) {
            throw new BPMException("restoreHistory  persons failed!", e);

        }
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType deletePersonTagToHistory(String activityInstHistoryID, ActivityInstHistoryAtt tagName, Map<RightCtx, Object> ctx) throws BPMException {
        String userId = (String) ctx.get(RightCtx.USERID);
        try {
            String sql = null;
            sql = " where ACTIVITYINST_HISTORY_ID = '" + activityInstHistoryID + "'" + " and PERSON_ID = '" + userId + "'  and RIGHT_GRP_CODE ='" + tagName.getType().toUpperCase() + "'";
            historyPersonMgr.deleteByWhere(sql);
        } catch (SQLException e) {
            throw new BPMException("addPersonToHistory   failed!", e);

        }
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType addPersonTagToHistory(String activityInstHistoryID, ActivityInstHistoryAtt tagName, Map<RightCtx, Object> ctx) throws BPMException {
        String userId = (String) ctx.get(RightCtx.USERID);
        try {
            ActivityInstHistory history = (ActivityInstHistory) this.activityInstHistoryMgr.loadByKey(activityInstHistoryID);
            DbActivityHistoryPerson person = historyPersonMgr.createActivityHistoryPerson();
            person.setProcessInstId(history.getProcessInstId());
            person.setActivityInstHistoryId(activityInstHistoryID);
            person.setPersonId(userId);
            person.setRightGrpCode(RightGroupEnums.fromType(tagName.getType()));
            historyPersonMgr.save(person);
        } catch (SQLException e) {
            throw new BPMException("addPersonToHistory   failed!", e);

        }
        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public Filter getActivityInstHistoryListFilter(RightConditionEnums condition, Map<RightCtx, Object> ctx) throws BPMException {
        BPMCondition c = null;
        String userId = (String) ctx.get(RightCtx.USERID);
        List userIds = (List) ctx.get("USERIDS");
        // String condition = (String) ctx.get(RightCtx.INSTANCE_CONDITION);
        if (condition == null) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        sb.append(" ( SELECT ACTIVITYINST_HISTORY_ID ");
        switch (condition) {
            case CONDITION_ALLWORK:
                sb.append(" FROM RT_ACTIVITYHISTORY_PERSON WHERE PERSON_ID = ");
                sb.append("'").append(userId).append("'");
                sb.append(" AND RIGHT_GRP_CODE in (");
                sb.append("'").append(RightGroupEnums.PERFORMER).append("',");
                sb.append("'").append(RightGroupEnums.HISTORYREADER).append("',");
                sb.append("'").append(RightGroupEnums.READER).append("',");
                sb.append("'").append(RightGroupEnums.PERFORMER).append("',");
                sb.append("'").append(RightGroupEnums.HISTORYPERFORMER).append("')");
                sb.append(" ) ");
                c = new BPMCondition(BPMConditionKey.ACTIVITYHISTORY_HISTORY_ID, Operator.IN, sb.toString());
                break;
            case CONDITION_DELETEWORK:
                sb.append(" FROM RT_ACTIVITYHISTORY_PERSON WHERE PERSON_ID = ");
                sb.append("'").append(userId).append("'");
                sb.append(" AND RIGHT_GRP_CODE in (");
                sb.append("'").append(RightGroupEnums.PERFORMER + "|" + RightGroupEnums.NORIGHT).append("',");
                sb.append("'").append(RightGroupEnums.SPONSOR + "|" + RightGroupEnums.NORIGHT).append("',");
                sb.append("'").append(RightGroupEnums.READER + "|" + RightGroupEnums.NORIGHT);
                sb.append("')");
                sb.append(" ) ");
                c = new BPMCondition(BPMConditionKey.ACTIVITYHISTORY_HISTORY_ID, Operator.IN, sb.toString());
                break;

            case CONDITION_JOINWORK:
                sb.append(" FROM RT_ACTIVITYHISTORY_PERSON WHERE PERSON_ID = ");
                sb.append("'").append(userId).append("'");
                sb.append(" AND RIGHT_GRP_CODE =");
                sb.append("'").append(RightGroupEnums.READER).append("'");
                sb.append(" ) ");
                c = new BPMCondition(BPMConditionKey.ACTIVITYHISTORY_HISTORY_ID, Operator.IN, sb.toString());
                break;

            case CONDITION_OUTWORK:
                sb.append(" FROM RT_ACTIVITYHISTORY_PERSON WHERE PERSON_ID = ");
                sb.append("'").append(userId).append("'");

                sb.append(" AND RIGHT_GRP_CODE in (");
                sb.append("'").append(RightGroupEnums.HISSPONSOR).append("',");
                sb.append("'").append(RightGroupEnums.SPONSOR)
                        // .append("',");
                        // sb.append("'").append(RightGroupEnums.PERFORMER)
                        .append("')");
                sb.append(" ) ");
                c = new BPMCondition(BPMConditionKey.ACTIVITYHISTORY_HISTORY_ID, Operator.IN, sb.toString());
                break;

            case CONDITION_INWORK:

                sb.append(" FROM RT_ACTIVITYHISTORY_PERSON WHERE PERSON_ID = ");
                sb.append("'").append(userId).append("'");
                sb.append(" AND RIGHT_GRP_CODE in (");
                // sb.append("'").append(RightGroupEnums.PERFORMER).append("',");
                sb.append("'").append(RightGroupEnums.READER);
                sb.append("')");
                // sb.append(" AND RIGHT_GRP_CODE in (");
                // sb.append("'").append(RightGroupEnums.SPONSOR);
                // sb.append("')");
                sb.append(" ) ");
                c = new BPMCondition(BPMConditionKey.ACTIVITYHISTORY_HISTORY_ID, Operator.IN, sb.toString());
                break;
            case CONDITION_PERFORMWORK:
                sb.append(" FROM RT_ACTIVITYHISTORY_PERSON WHERE PERSON_ID = ");
                sb.append("'").append(userId).append("'");
                sb.append(" AND RIGHT_GRP_CODE in (");
                sb.append("'").append(RightGroupEnums.PERFORMER).append("',");
                sb.append("'").append(RightGroupEnums.READER);
                sb.append("')");
                sb.append(" ) ");
                c = new BPMCondition(BPMConditionKey.ACTIVITYHISTORY_HISTORY_ID, Operator.IN, sb.toString());
                break;

        }

        return c;

    }

    public void copyActivityInstByHistory(String activityInstId, String activityHistoryInstId, Map<RightCtx, Object> ctx) throws BPMException {

        String personId = (String) ctx.get(RightCtx.USERID);
        DbActivityInstPerson currPerformer = actPersonMgr.createActivityInstPerson();
        currPerformer.setActivityInstPersonId(UUID.randomUUID().toString());
        currPerformer.setProcessInstId(this.activityInstHistoryMgr.loadByKey(activityHistoryInstId).getProcessInstId());
        currPerformer.setActivityInstId(activityInstId);
        currPerformer.setPersonActivityState(RightPerformStatus.CURRENT);
        currPerformer.setPersonId(personId);
        currPerformer.setRightGrpCode(RightGroupEnums.SPONSOR);
        try {
            actPersonMgr.save(currPerformer);
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }

    public ReturnType endTask(String activityInstID, String activityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {

        String userId = (String) ctx.get(RightCtx.USERID);

        String sql = null;
        // 办理权限
        sql = " where ACTIVITYINST_ID = '" + activityInstID + "'" + " and PERSON_ID = '" + userId + "'" + " AND RIGHT_GRP_CODE = '" + RightGroupEnums.PERFORMER + "' AND PERSON_ACTIVITY_STATE='" + RightPerformStatus.CURRENT
                + "'";

        DbActivityInstPerson[] persons = null;
        try {
            persons = actPersonMgr.loadByWhere(sql);
            if (persons != null && persons.length > 0) {
                DbActivityInstPerson person = persons[0];
                person.setRightGrpCode(RightGroupEnums.HISTORYREADER);
                person.setPersonActivityState(RightPerformStatus.FINISH);
                actPersonMgr.save(person);
                return saveActivityHistoryInst(activityInstID, activityInstHistoryID, ctx);
            }

        } catch (SQLException e) {
            throw new BPMException("ReadEnd activity persons failed!", e);

        }

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }

    public ReturnType abortedTask(String activityInstID, String activityInstHistoryID, Map<RightCtx, Object> ctx) throws BPMException {
        String userId = (String) ctx.get(RightCtx.USERID);

        String sql = null;
        // 办理权限
        sql = " where ACTIVITYINST_ID = '" + activityInstID + "'" + " and PERSON_ID = '" + userId + "'" + " AND RIGHT_GRP_CODE = '" + RightGroupEnums.PERFORMER + "' AND PERSON_ACTIVITY_STATE='" + RightPerformStatus.CURRENT
                + "'";

        DbActivityInstPerson[] persons = null;
        try {
            persons = actPersonMgr.loadByWhere(sql);
            if (persons != null && persons.length > 0) {
                DbActivityInstPerson person = persons[0];
                person.setRightGrpCode(RightGroupEnums.HISTORYREADER);
                person.setPersonActivityState(RightPerformStatus.FINISH);
                actPersonMgr.save(person);
                return saveActivityHistoryInst(activityInstID, activityInstHistoryID, ctx);
            }

        } catch (SQLException e) {
            throw new BPMException("ReadEnd activity persons failed!", e);

        }

        return new ReturnType(ReturnType.MAINCODE_SUCCESS);
    }


    public JDSClientService getJdsClient() {
        JDSClientService service = ((JDSClientService) EsbUtil.parExpression("$JDSC"));
        return service;
    }


}
