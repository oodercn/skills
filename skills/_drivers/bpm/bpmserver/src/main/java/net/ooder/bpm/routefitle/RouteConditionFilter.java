/**
 * $RCSfile: RouteConditionFilter.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:26:17 $
 * <p>
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 * <p>
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.routefitle;

import java.util.Map;

import net.ooder.bpm.client.RouteDef;
import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.query.RouteDefFilter;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;
import net.ooder.context.JDSActionContext;
import net.ooder.context.JDSContext;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 执行路由条件的过滤器，此过滤器将解释路由上面的条件， 并根据条件公式执行结果判断路由是否有效
 * </p>
 * <p>
 * Copyright: itjds Copyright (c) 2008
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 *
 * @author wenzhangli
 * @version 2.0
 */
public class RouteConditionFilter extends RouteDefFilter {

    static Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, RouteConditionFilter.class);

    Map ctx = null;

    public RouteConditionFilter(Map ctx) {
        this.ctx = ctx;
    }

    /**
     * 解释路由条件公式
     *
     * @see net.ooder.bpm.engine.query.RouteDefFilter#filterRouteDef(net.ooder.bpm.client.RouteDef)
     */
    public boolean filterRouteDef(RouteDef obj) {

        JDSContext context = JDSActionContext.getActionContext();
        String expression = obj.getRouteCondition();
        if (expression == null || expression.equals("")) {
            return true;
        }
        Object result = true;
        try {

            result = context.Par(expression, boolean.class);
            // result = ESBPar.pare(expression, contextRoot, null, context);
        } catch (Exception e) {
            log.warn("Route Condition parser error : ");
            return false;
        }

        if (result instanceof Boolean) {
            return ((Boolean) result).booleanValue();
        } else {
            return true;
        }

    }

}


