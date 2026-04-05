/**
 * $RCSfile: EIProcessDefVersion.java,v $
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

import net.ooder.bpm.client.Listener;
import net.ooder.bpm.engine.BPMException;


/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 流程定义版本数据接口
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
public interface EIProcessDefVersion {

	/**
	 * 返回流程ID 此属性为流程基本属性，与版本无关，
	 * 
	 * @return 流程的UUID
	 */
	public String getProcessDefId();

	public void setProcessDefId(String processDefId);

	/**
	 * 返回当前版本的UUID
	 * 
	 * @return 版本的UUID
	 */
	public String getProcessDefVersionId();

	public void setProcessDefVersionId(String processDefVersionId);

	/**
	 * 返回当前流程定义的版本号
	 * 
	 * @return
	 */
	public int getVersion();

	public void setVersion(int version);

	/**
	 * 返回当前版本的状态： 此属性为流程基本属性，与版本无关，
	 * 
	 * @return 返回的状态有三个值：
	 *         <li> VERSION_UNDER_REVISION - 修订中
	 *         <li> VERSION_RELEASED - 已发布
	 *         <li> VERSION_UNDER_TEST - 测试中
	 */
	public String getPublicationStatus();

	public void setPublicationStatus(String publicationStatus);

	/**
	 * 流程定义的名称， 此属性为流程基本属性，与版本无关，
	 * 
	 * @return 流程定义的名称
	 */
	public String getProcessDefName();

	/**
	 * 流程定义的描述， 此属性为流程基本属性，与版本无关，
	 * 
	 * @return 流程定义的描述
	 */
	public String getDescription();

	public abstract void setDescription(String newVal);

	/**
	 * 流程定义的分类， 此属性为流程基本属性，与版本无关，
	 * 
	 * @return 流程定义的分类
	 */
	public String getClassification();

	public abstract String getDefDescription();

	/**
	 * 流程所属的应用系统，如：OA,CMS等 此属性为流程基本属性，与版本无关，
	 * 
	 * @return 字符串，应用系统的代码，如："SP","CMS"
	 */
	public String getSystemCode();

	/**
	 * 流程的访问级别，也就对应着流程的类型 此属性为流程基本属性，与版本无关，
	 * 
	 * @return 两种返回值：
	 *         <li> ACCESS_PUBLIC: 可以独立启动
	 *         <li> ACCESS_PRIVATE: 不可以独立启动，只能作为Subflow
	 */
	public String getAccessLevel();

	/**
	 * 取得流程版本的激活时间，如果在冻结状态，则返回null
	 * 
	 * @return
	 */
	public java.util.Date getActiveTime();

	public void setActiveTime(java.util.Date activeTime);

	/**
	 * 取得流程版本的冻结时间，如果在激活状态，则返回null
	 * 
	 * @return
	 */
	public java.util.Date getFreezeTime();

	public void setFreezeTime(java.util.Date freezeTime);

	/**
	 * 取得流程版本创建人的Id
	 * 
	 * @return
	 */
	public String getCreatorId();

	public void setCreatorId(String id);

	/**
	 * 取得流程版本创建人的姓名
	 * 
	 * @return
	 */
	public String getCreatorName();

	public void setCreatorName(String name);

	/**
	 * 取得流程版本的创建时间
	 * 
	 * @return
	 */
	public java.util.Date getCreated();

	public void setCreated(java.util.Date created);

	/**
	 * 取得流程版本最后修改人的Id
	 * 
	 * @return
	 */
	public String getModifierId();

	public void setModifierId(String id);

	/**
	 * 取得流程版本最后修改人的姓名
	 * 
	 * @return
	 */
	public String getModifierName();

	public void setModifierName(String name);

	/**
	 * 取得流程版本的最后修改
	 * 
	 * @return
	 */
	public java.util.Date getModifyTime();

	public void setModifyTime(java.util.Date modifyTime);

	/**
	 * 取得流程版本的完成期限 单位从getDurationUnit()取得
	 * 
	 * @return
	 */
	public int getLimit();

	public void setLimit(int limit);

	/**
	 * 取得时间的单位
	 * 
	 * @return
	 * <li>Y:年
	 * <li>M:月
	 * <li>D:日
	 * <li>H:时
	 * <li>m:分
	 * <li>s:秒
	 * <li>W:工作日
	 */
	public String getDurationUnit();

	public void setDurationUnit(String durationUnit);

	/**
	 * 取得当前流程版本所注册的流程监听器类
	 * 
	 * @return
	 */
	public List<Listener> getListeners();

	public void setListeners(List<Listener> list);

	/**
	 * 取得当前版本中包含所有活动的对象
	 * 
	 * @return 返回的List是只读
	 */
	public List<EIActivityDef> getAllActivityDefs();

	/**
	 * 取得当前版本中包含所有路由的对象
	 * 
	 * @return 返回的List是只读
	 */
	public List<EIRouteDef> getAllRouteDefs() ;

	/**
	 * 取得最顶层的属性（没有父属性的属性）
	 * 
	 * @return
	 */
	public List<EIAttribute> getTopAttribute();

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

	public List<EIAttribute> getAllAttribute();

	/**
	 * 清空活动定义内的所有扩展属性
	 */
	public void clearAttribute();

	public void setAttribute(String name, EIAttributeDef attDef)
			throws BPMException;

}

