/**
 * $RCSfile: EIRouteDef.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:59 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.inter;

import java.util.List;

import net.ooder.bpm.engine.BPMException;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 路由数据接口
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
public interface EIRouteDef {

    /**
     * Getter method for routedefId
     * 
     * @return the value of routedefId
     */
    public abstract String getRouteDefId();

    /**
     * Setter method for routedefId
     * 
     * @param newVal
     *            The new value to be assigned to routedefId
     */
    public abstract void setRouteDefId(String newVal);

    /**
     * Getter method for processdefId
     * 
     * @return the value of processdefId
     */
    public abstract String getProcessDefId();

    /**
     * Setter method for processdefId
     * 
     * @param newVal
     *            The new value to be assigned to processdefId
     */
    public abstract void setProcessDefId(String newVal);

    /**
     * Getter method for processdefVersionId
     * 
     * @return the value of processdefVersionId
     */
    public abstract String getProcessDefVersionId();

    /**
     * Setter method for processdefVersionId
     * 
     * @param newVal
     *            The new value to be assigned to processdefVersionId
     */
    public abstract void setProcessDefVersionId(String newVal);

    /**
     * Getter method for routename
     * 
     * @return the value of routename
     */
    public abstract String getName();

    /**
     * Setter method for routename
     * 
     * @param newVal
     *            The new value to be assigned to routename
     */
    public abstract void setName(String newVal);

    /**
     * Getter method for description
     * 
     * @return the value of description
     */
    public abstract String getDescription();

    /**
     * Setter method for description
     * 
     * @param newVal
     *            The new value to be assigned to description
     */
    public abstract void setDescription(String newVal);

    /**
     * Getter method for fromactivitydefId
     * 
     * @return the value of fromactivitydefId
     */
    public abstract String getFromActivityDefId();

    /**
     * Setter method for fromactivitydefId
     * 
     * @param newVal
     *            The new value to be assigned to fromactivitydefId
     */
    public abstract void setFromActivityDefId(String newVal);

    /**
     * Getter method for toactivitydefId
     * 
     * @return the value of toactivitydefId
     */
    public abstract String getToActivityDefId();

    /**
     * Setter method for toactivitydefId
     * 
     * @param newVal
     *            The new value to be assigned to toactivitydefId
     */
    public abstract void setToActivityDefId(String newVal);

    /**
     * Getter method for routeorder
     * 
     * @return the value of routeorder
     */
    public abstract int getRouteOrder();

    /**
     * Setter method for routeorder
     * 
     * @param newVal
     *            The new value to be assigned to routeorder
     */
    public abstract void setRouteOrder(int newVal);

    /**
     * Getter method for routedirection
     * 
     * @return the value of routedirection
     */
    public abstract String getRouteDirection();

    public abstract String getRouteConditionType();

    public abstract void setRouteConditionType(String newVal);

    /**
     * Setter method for routedirection
     * 
     * @param newVal
     *            The new value to be assigned to routedirection
     */
    public abstract void setRouteDirection(String newVal);

    /**
     * Getter method for routecondition
     * 
     * @return the value of routecondition
     */
    public abstract String getRouteCondition();

    /**
     * Setter method for routecondition
     * 
     * @param newVal
     *            The new value to be assigned to routecondition
     */
    public abstract void setRouteCondition(String newVal);

    /**
     * 取得扩展属性值，此值已经经过解释<br>
     * 当需要取得带层次关系的属性值时，属性名称在每层间加"."分割 <br>
     * 例如：<br>
     * "Form1.field1.readRight" - 取得最顶层属性中名称为form1的下的名称为field1的子属性下名称为value的值
     * 
     * @param name
     *            属性名称,"."隔开，不区分大小写
     * @return 属性值，此值已经使用AttributeInterpret接口实现类解释后的值
     */
    public Object getAttributeInterpretedValue(String name);

    /**
     * 取得扩展属性值,此值是未经解析的原值，即数据库中储存的值<br>
     * 当需要取得带层次关系的属性值时，属性名称在每层间加"."分割 <br>
     * 例如：<br>
     * "Form1.field1.readRight" - 取得最顶层属性中名称为form1的下的名称为field1的子属性下名称为value的值
     * 
     * @param name
     *            属性名称,"."隔开，不区分大小写
     * @return 属性值，此值是未经解析的原值
     */
    public String getAttributeValue(String name);

    /**
     * 取得所有的属性
     * 
     * @return
     */
    public List getAllAttribute();

    /**
     * 取得最顶层的属性（没有父属性的属性）
     * 
     * @return
     */
    public List getTopAttribute();

    /**
     * 取得当前路由上注册的所有路由监听器
     * 
     * @return
     */
    public List getListeners();

    public void setListeners(List list);

    /**
     * 清空活动定义内的所有扩展属性
     */
    public void clearAttribute();

    public void setAttribute(String name, EIAttributeDef attDef) throws BPMException;

    /**
     * 取得出发节点的活动定义
     * 
     * @return 活动定义
     */
    public abstract EIActivityDef getFromActivityDef() throws BPMException;

    /**
     * 取得到达节点的活动定义
     * 
     * @return 活动定义
     */
    public abstract EIActivityDef getToActivityDef() throws BPMException;

    /**
     * 取得当前活路由定义所使用的流程定义版本
     * 
     * @return 流程定义版本
     * @throws BPMException
     *             有异常发生
     */
    public EIProcessDefVersion getProcessDefVersion() throws BPMException;

    /**
     * 取得与该路由定义相对应的流程定义
     * 
     * @return 流程定义
     */
    public EIProcessDef getProcessDef() throws BPMException;
}

