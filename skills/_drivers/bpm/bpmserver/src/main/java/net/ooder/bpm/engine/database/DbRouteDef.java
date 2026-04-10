/**
 * $RCSfile: DbRouteDef.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:25:45 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.inter.EIActivityDef;
import net.ooder.bpm.engine.inter.EIActivityDefManager;
import net.ooder.bpm.engine.inter.EIAttributeDef;
import net.ooder.bpm.engine.inter.EIProcessDef;
import net.ooder.bpm.engine.inter.EIProcessDefManager;
import net.ooder.bpm.engine.inter.EIProcessDefVersion;
import net.ooder.bpm.engine.inter.EIProcessDefVersionManager;
import net.ooder.bpm.engine.inter.EIRouteDef;
import net.ooder.bpm.engine.inter.EIRouteDefManager;
import net.ooder.common.cache.CacheSizes;
import net.ooder.common.cache.Cacheable;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;

/**
 * <p>
 * Title: JDS绯荤粺绠＄悊绯荤粺
 * </p>
 * <p>
 * Description: 璺敱瀹氫箟鎺ュ彛鏁版嵁搴撳疄鐜?
 * </p>
 * <p>
 * The java object mapped on the Relation database table BPM_ROUTEDEF
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
public class DbRouteDef implements EIRouteDef, Cacheable, Serializable {

	private static final Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, DbRouteDef.class);

	private String routeDefId;

	private boolean routedefId_is_modified = false;

	private boolean routedefId_is_initialized = false;

	private String processDefId;

	private boolean processdefId_is_modified = false;

	private boolean processdefId_is_initialized = false;

	private String processDefVersionId;

	private boolean processdefVersionId_is_modified = false;

	private boolean processdefVersionId_is_initialized = false;

	private String name;

	private boolean routename_is_modified = false;

	private boolean routename_is_initialized = false;

	private String description;

	private boolean description_is_modified = false;

	private boolean description_is_initialized = false;

	private String fromActivityDefId;

	private boolean fromactivitydefId_is_modified = false;

	private boolean fromactivitydefId_is_initialized = false;

	private String toActivityDefId;

	private boolean toactivitydefId_is_modified = false;

	private boolean toactivitydefId_is_initialized = false;

	private int routeOrder;

	private boolean routeorder_is_modified = false;

	private boolean routeorder_is_initialized = false;

	private String routeDirection;

	private boolean routedirection_is_modified = false;

	private boolean routedirection_is_initialized = false;

	private String routeCondition;

	private boolean routecondition_is_modified = false;

	private boolean routecondition_is_initialized = false;
	
	
	private String routeConditionType;

	private boolean routeConditionType_is_modified = false;

	private boolean routeConditionType_is_initialized = false;


	private boolean _isNew = true;

	private boolean _isAttributeModified = true;

	Map attributeTopMap = null; // store the top level attribute

	Map attributeIdMap = null; // store all attribute in this definition

	List listeners = null; // store all listeners in this route definition
	/**
	 */
	DbRouteDef() {
	}

	/**
	 * Getter method for routedefId
	 * 
	 * @return the value of routedefId
	 */
	public String getRouteDefId() {
		return routeDefId;
	}

	/**
	 * Setter method for routedefId
	 * 
	 * @param newVal
	 *            The new value to be assigned to routedefId
	 */
	public void setRouteDefId(String newVal) {
		if ((newVal != null && newVal.equals(this.routeDefId) == true)
				|| (newVal == null && this.routeDefId == null))
			return;
		this.routeDefId = newVal;
		routedefId_is_modified = true;
		routedefId_is_initialized = true;
	}

	/**
	 * Determine if the routedefId is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isRoutedefIdModified() {
		return routedefId_is_modified;
	}

	/**
	 * Determine if the routedefId has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isRoutedefIdInitialized() {
		return routedefId_is_initialized;
	}

	/**
	 * Getter method for processdefId
	 * 
	 * @return the value of processdefId
	 */
	public String getProcessDefId() {
		return processDefId;
	}

	/**
	 * Setter method for processdefId
	 * 
	 * @param newVal
	 *            The new value to be assigned to processdefId
	 */
	public void setProcessDefId(String newVal) {
		if ((newVal != null && newVal.equals(this.processDefId) == true)
				|| (newVal == null && this.processDefId == null))
			return;
		this.processDefId = newVal;
		processdefId_is_modified = true;
		processdefId_is_initialized = true;
	}

	/**
	 * Determine if the processdefId is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isProcessdefIdModified() {
		return processdefId_is_modified;
	}

	/**
	 * Determine if the processdefId has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isProcessdefIdInitialized() {
		return processdefId_is_initialized;
	}

	/**
	 * Getter method for processdefVersionId
	 * 
	 * @return the value of processdefVersionId
	 */
	public String getProcessDefVersionId() {
		return processDefVersionId;
	}

	/**
	 * Setter method for processdefVersionId
	 * 
	 * @param newVal
	 *            The new value to be assigned to processdefVersionId
	 */
	public void setProcessDefVersionId(String newVal) {
		if ((newVal != null && newVal.equals(this.processDefVersionId) == true)
				|| (newVal == null && this.processDefVersionId == null))
			return;
		this.processDefVersionId = newVal;
		processdefVersionId_is_modified = true;
		processdefVersionId_is_initialized = true;
	}

	/**
	 * Determine if the processdefVersionId is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isProcessdefVersionIdModified() {
		return processdefVersionId_is_modified;
	}

	/**
	 * Determine if the processdefVersionId has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isProcessdefVersionIdInitialized() {
		return processdefVersionId_is_initialized;
	}

	/**
	 * Getter method for routename
	 * 
	 * @return the value of routename
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter method for routename
	 * 
	 * @param newVal
	 *            The new value to be assigned to routename
	 */
	public void setName(String newVal) {
		if ((newVal != null && newVal.equals(this.name) == true)
				|| (newVal == null && this.name == null))
			return;
		this.name = newVal;
		routename_is_modified = true;
		routename_is_initialized = true;
	}

	/**
	 * Determine if the routename is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isRoutenameModified() {
		return routename_is_modified;
	}

	/**
	 * Determine if the routename has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isRoutenameInitialized() {
		return routename_is_initialized;
	}

	/**
	 * Getter method for description
	 * 
	 * @return the value of description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Setter method for description
	 * 
	 * @param newVal
	 *            The new value to be assigned to description
	 */
	public void setDescription(String newVal) {
		if ((newVal != null && newVal.equals(this.description) == true)
				|| (newVal == null && this.description == null))
			return;
		this.description = newVal;
		description_is_modified = true;
		description_is_initialized = true;
	}

	/**
	 * Determine if the description is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isDescriptionModified() {
		return description_is_modified;
	}

	/**
	 * Determine if the description has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isDescriptionInitialized() {
		return description_is_initialized;
	}

	/**
	 * Getter method for fromactivitydefId
	 * 
	 * @return the value of fromactivitydefId
	 */
	public String getFromActivityDefId() {
		return fromActivityDefId;
	}

	/**
	 * Setter method for fromactivitydefId
	 * 
	 * @param newVal
	 *            The new value to be assigned to fromactivitydefId
	 */
	public void setFromActivityDefId(String newVal) {
		if ((newVal != null && newVal.equals(this.fromActivityDefId) == true)
				|| (newVal == null && this.fromActivityDefId == null))
			return;
		this.fromActivityDefId = newVal;
		fromactivitydefId_is_modified = true;
		fromactivitydefId_is_initialized = true;
	}

	/**
	 * Determine if the fromactivitydefId is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isFromactivitydefIdModified() {
		return fromactivitydefId_is_modified;
	}

	/**
	 * Determine if the fromactivitydefId has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isFromactivitydefIdInitialized() {
		return fromactivitydefId_is_initialized;
	}

	/**
	 * Getter method for toactivitydefId
	 * 
	 * @return the value of toactivitydefId
	 */
	public String getToActivityDefId() {
		return toActivityDefId;
	}

	/**
	 * Setter method for toactivitydefId
	 * 
	 * @param newVal
	 *            The new value to be assigned to toactivitydefId
	 */
	public void setToActivityDefId(String newVal) {
		if ((newVal != null && newVal.equals(this.toActivityDefId) == true)
				|| (newVal == null && this.toActivityDefId == null))
			return;
		this.toActivityDefId = newVal;
		toactivitydefId_is_modified = true;
		toactivitydefId_is_initialized = true;
	}

	/**
	 * Determine if the toactivitydefId is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isToactivitydefIdModified() {
		return toactivitydefId_is_modified;
	}

	/**
	 * Determine if the toactivitydefId has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isToactivitydefIdInitialized() {
		return toactivitydefId_is_initialized;
	}

	/**
	 * Getter method for routeorder
	 * 
	 * @return the value of routeorder
	 */
	public int getRouteOrder() {
		return routeOrder;
	}

	/**
	 * Setter method for routeorder
	 * 
	 * @param newVal
	 *            The new value to be assigned to routeorder
	 */
	public void setRouteOrder(int newVal) {
		if (newVal == routeOrder)
			return;
		this.routeOrder = newVal;
		routeorder_is_modified = true;
		routeorder_is_initialized = true;
	}

	/**
	 * Determine if the routeorder is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isRouteorderModified() {
		return routeorder_is_modified;
	}

	/**
	 * Determine if the routeorder has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isRouteorderInitialized() {
		return routeorder_is_initialized;
	}

	/**
	 * Getter method for routedirection
	 * 
	 * @return the value of routedirection
	 */
	public String getRouteDirection() {
		return routeDirection;
	}

	/**
	 * Setter method for routedirection
	 * 
	 * @param newVal
	 *            The new value to be assigned to routedirection
	 */
	public void setRouteDirection(String newVal) {
		if ((newVal != null && newVal.equals(this.routeDirection) == true)
				|| (newVal == null && this.routeDirection == null))
			return;
		this.routeDirection = newVal;
		routedirection_is_modified = true;
		routedirection_is_initialized = true;
	}

	/**
	 * Determine if the routedirection is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isRoutedirectionModified() {
		return routedirection_is_modified;
	}

	/**
	 * Determine if the routedirection has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isRoutedirectionInitialized() {
		return routedirection_is_initialized;
	}

	
	public String getRouteCondition() {
		return routeCondition;
	}
	

	
	public String getRouteConditionType() {
		return routeConditionType;
	}
	

	public void setRouteConditionType(String newVal) {
		if ((newVal != null && newVal.equals(this.routeConditionType) == true)
				|| (newVal == null && this.routeConditionType == null))
			return;
		this.routeConditionType = newVal;
		routeConditionType_is_modified = true;
		routeConditionType_is_initialized = true;
	}
	
	/**
	 * Determine if the routecondition is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isRouteconditionTypeModified() {
		return routeConditionType_is_modified;
	}

	/**
	 * Determine if the routecondition has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isRouteconditionTypeInitialized() {
		return routeConditionType_is_initialized;
	}



	/**
	 * Setter method for routecondition
	 * 
	 * @param newVal
	 *            The new value to be assigned to routecondition
	 */
	public void setRouteCondition(String newVal) {
		if ((newVal != null && newVal.equals(this.routeCondition) == true)
				|| (newVal == null && this.routeCondition == null))
			return;
		this.routeCondition = newVal;
		routecondition_is_modified = true;
		routecondition_is_initialized = true;
	}

	/**
	 * Determine if the routecondition is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isRouteconditionModified() {
		return routecondition_is_modified;
	}

	/**
	 * Determine if the routecondition has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isRouteconditionInitialized() {
		return routecondition_is_initialized;
	}

	/**
	 * Determine if the current object is new or not
	 * 
	 * @return true if the current object is new, false if the object is not new
	 */
	public boolean isNew() {
		return _isNew;
	}

	/**
	 * Specify to the object if he has to been set as new or not
	 * 
	 * @param isNew
	 *            the boolean value to be assigned to the isNew field
	 */
	public void setIsNew(boolean isNew) {
		this._isNew = isNew;
	}

	/**
	 * Determine if the object has been modified since the last time this method
	 * was called or since the creation of the object
	 * 
	 * @return true if the object has been modified, false if the object has not
	 *         been modified
	 */
	public boolean isModified() {
		return routedefId_is_modified || processdefId_is_modified
				|| processdefVersionId_is_modified || routename_is_modified
				|| description_is_modified || fromactivitydefId_is_modified
				|| toactivitydefId_is_modified || routeorder_is_modified
				|| routeConditionType_is_modified
				|| routedirection_is_modified || routecondition_is_modified;
	}

	/**
	 * Reset object modification status to "not modified"
	 */
	public void resetIsModified() {
		routedefId_is_modified = false;
		processdefId_is_modified = false;
		processdefVersionId_is_modified = false;
		routename_is_modified = false;
		description_is_modified = false;
		fromactivitydefId_is_modified = false;
		toactivitydefId_is_modified = false;
		routeorder_is_modified = false;
		routedirection_is_modified = false;
		routecondition_is_modified = false;
		routeConditionType_is_modified = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.inter.EIProcessDefVersion#getAttributeValue(java.lang.String)
	 */
	public String getAttributeValue(String name) {
		EIAttributeDef attDef = getAttribute(name);
		if (attDef == null) {
			return null;
		}
		return attDef.getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.inter.EIProcessDefVersion#getAttributeValue(java.lang.String)
	 */
	public Object getAttributeInterpretedValue(String name) {
		EIAttributeDef attDef = getAttribute(name);
		if (attDef == null) {
			return null;
		}
		return attDef.getInterpretedValue();
	}

	/**
	 * 鍙栧緱灞炴€у€硷紝褰撻渶瑕佸彇寰楀甫灞傛鍏崇郴鐨勫睘鎬у€兼椂锛屽睘鎬у悕绉板湪姣忓眰闂村姞"."鍒嗗壊 <br>
	 * 渚嬪锛?br>
	 * "Form1.field1.value" - 鍙栧緱鏈€椤跺眰灞炴€т腑鍚嶇О涓篺orm1鐨?
	 * 
	 * @param name
	 * @return
	 */
	public EIAttributeDef getAttribute(String name) {
		if (name != null) {
			name = name.toUpperCase();
		}

		if (attributeIdMap == null && attributeTopMap == null) {
			attributeIdMap = new HashMap();
			attributeTopMap = new HashMap();
			DbRouteDefManager manager = (DbRouteDefManager) EIRouteDefManager
					.getInstance();
			try {
				manager.loadAttribute(this);
			} catch (BPMException e) {
				log.error("load the attributes in route definition "
						+ this.routeDefId + " failed!", e);
				return null;
			}
		}

		StringTokenizer st = new StringTokenizer(name, ".");
		DbAttributeDef subAtt = null;
		while (st.hasMoreTokens()) {
			String subname = st.nextToken();
			if (subAtt == null) { // top level
				subAtt = (DbAttributeDef) attributeTopMap.get(subname);
			} else {
				subAtt = (DbAttributeDef) subAtt.getChild(subname);
			}

			if (subAtt == null) {
				return null; // not found
			}
		}
		return subAtt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.inter.EIActivityDef#getAllAttribute()
	 */
	public List getAllAttribute() {

		if (attributeIdMap == null && attributeTopMap == null) {
			attributeIdMap = new HashMap();
			attributeTopMap = new HashMap();
			DbRouteDefManager manager = (DbRouteDefManager) EIRouteDefManager
					.getInstance();
			try {
				manager.loadAttribute(this);
			} catch (BPMException e) {
				log.error("load the attributes in route definition "
						+ this.routeDefId + " failed!", e);
				return new ArrayList();
			}
		}

		return new ArrayList(attributeIdMap.values());
	}

	/**
	 * 鍙栧緱鏈€椤跺眰鐨勫睘鎬э紙娌℃湁鐖跺睘鎬х殑灞炴€э級
	 * 
	 * @return
	 */
	public List getTopAttribute() {
		if (attributeIdMap == null && attributeTopMap == null) {
			attributeIdMap = new HashMap();
			attributeTopMap = new HashMap();
			DbRouteDefManager manager = (DbRouteDefManager) EIRouteDefManager
					.getInstance();
			try {
				manager.loadAttribute(this);
			} catch (BPMException e) {
				log.error("load the attributes in route definition "
						+ this.routeDefId + " failed!", e);
				return new ArrayList();
			}
		}

		List list = new ArrayList(attributeTopMap.values());
		List result = new ArrayList();
		for (Iterator it = list.iterator(); it.hasNext();) {
			EIAttributeDef attr = (EIAttributeDef) it.next();
			result.addAll(attr.getChildren());
		}
		return result;

	}

	public void clearAttribute() {

		if (attributeIdMap == null && attributeTopMap == null) {
			// 涓嶈鍙栨暟鎹簱锛岀洿鎺ユ竻绌?
			attributeIdMap = new HashMap();
			attributeTopMap = new HashMap();
		}
		attributeIdMap.clear();
		attributeTopMap.clear();

		_isAttributeModified = true;
	}

	public void setAttribute(String parentName, EIAttributeDef attDef)
			throws BPMException {
		if (parentName != null) {
			parentName = parentName.toUpperCase();
		}
		// load first
		if (attributeIdMap == null && attributeTopMap == null) {
			attributeIdMap = new HashMap();
			attributeTopMap = new HashMap();
			DbRouteDefManager manager = (DbRouteDefManager) EIRouteDefManager
					.getInstance();

			manager.loadAttribute(this);
		}

		EIAttributeDef parentAtt = null;

		if (parentName != null && !parentName.equals("")) {
			parentAtt = getAttribute(parentName);
			if (parentAtt == null) {
				if (parentName.indexOf(".") == -1) { // top level
					parentAtt = new DbAttributeDef();
					parentAtt.setName(parentName);
					parentAtt.setType(parentName);
					setAttribute(null, parentAtt);
				} else {
					// error: parentAtt not in this activity definition!
					throw new BPMException(
							"parentAtt not in this route definition! parentAtt:"
									+ parentName + ", attName:"
									+ attDef.getName());
				}
			}
			attDef.setType(parentAtt.getType());
		}

		if (parentAtt != null) {
			// sub attribute
			EIAttributeDef oldAtt = (EIAttributeDef) parentAtt.getChild(attDef
					.getName());
			if (oldAtt != null) { // exist same name attribute in this tree
				attributeIdMap.remove(oldAtt.getId()); // remove it from all
														// attribute map
			}
			parentAtt.addChild(attDef); // change the new attribute definition!
			attributeIdMap.put(attDef.getId(), attDef);
		} else {
			// top level add to top map
			// EIAttributeDef oldAtt = (EIAttributeDef)
			// attributeTopMap.get(attDef.getName());
			// if(oldAtt != null) { //exist same name attribute in this tree
			// attributeIdMap.remove(oldAtt.getId()); //remove it from all
			// attribute map
			// }
			attributeTopMap.put(attDef.getName(), attDef);
		}

		_isAttributeModified = true;
	}

	public boolean isAttributeModified() {
		if (attributeIdMap == null && attributeTopMap == null) {
			return false;
		}
		return routedefId_is_modified || _isAttributeModified;
	}

	/**
	 * Copy the passed bean into the current bean
	 * 
	 * @param bean
	 *            the bean to copy into the current bean
	 */
	public void copy(DbRouteDef bean) {
		setRouteDefId(bean.getRouteDefId());
		setProcessDefId(bean.getProcessDefId());
		setProcessDefVersionId(bean.getProcessDefVersionId());
		setName(bean.getName());
		setDescription(bean.getDescription());
		setFromActivityDefId(bean.getFromActivityDefId());
		setToActivityDefId(bean.getToActivityDefId());
		setRouteOrder(bean.getRouteOrder());
		setRouteDirection(bean.getRouteDirection());
		setRouteCondition(bean.getRouteCondition());
	}

	/**
	 * Return the object string representation
	 * 
	 * @return the object as a string
	 */
	public String toString() {
		return "\n[BPM_ROUTEDEF] "
				+ "\n - BPM_ROUTEDEF.ROUTEDEF_ID = "
				+ (routedefId_is_initialized ? ("[" + routeDefId.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_ROUTEDEF.PROCESSDEF_ID = "
				+ (processdefId_is_initialized ? ("[" + processDefId.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_ROUTEDEF.PROCESSDEF_VERSION_ID = "
				+ (processdefVersionId_is_initialized ? ("["
						+ processDefVersionId.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_ROUTEDEF.ROUTENAME = "
				+ (routename_is_initialized ? ("[" + name.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_ROUTEDEF.DESCRIPTION = "
				+ (description_is_initialized ? ("[" + description.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_ROUTEDEF.FROMACTIVITYDEF_ID = "
				+ (fromactivitydefId_is_initialized ? ("["
						+ fromActivityDefId.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_ROUTEDEF.TOACTIVITYDEF_ID = "
				+ (toactivitydefId_is_initialized ? ("["
						+ toActivityDefId.toString() + "]") : "not initialized")
				+ ""
				+ "\n - BPM_ROUTEDEF.ROUTEORDER = "
				+ (routeorder_is_initialized ? ("[" + routeOrder + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_ROUTEDEF.ROUTEDIRECTION = "
				+ (routedirection_is_initialized ? ("["
						+ routeDirection.toString() + "]") : "not initialized")
				+ ""
				+ "\n - BPM_ROUTEDEF.ROUTECONDITION = "
				+ (routecondition_is_initialized ? ("["
						+ routeCondition.toString() + "]") : "not initialized")
				+ ""
				+ "\n - BPM_ROUTEDEF.ROUTECONDITIONTYPE = "
				+ (routeConditionType_is_initialized ? ("["
						+ routeConditionType.toString() + "]") : "not initialized")
				+ "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.common.cache.Cacheable#getCachedSize()
	 */
	public int getCachedSize() {

		int size = 0;
		size += CacheSizes.sizeOfString(routeDefId);
		size += CacheSizes.sizeOfString(processDefVersionId);
		size += CacheSizes.sizeOfString(name);
		size += CacheSizes.sizeOfString(description);
		size += CacheSizes.sizeOfString(fromActivityDefId);
		size += CacheSizes.sizeOfString(toActivityDefId);
		size += CacheSizes.sizeOfInt(); // routeOrder
		size += CacheSizes.sizeOfString(routeDirection);
		size += CacheSizes.sizeOfString(routeCondition);
		size += CacheSizes.sizeOfString(routeConditionType);

		return size;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.inter.EIRouteDef#getFromActivityDef()
	 */
	public EIActivityDef getFromActivityDef() throws BPMException {
		return EIActivityDefManager.getInstance().loadByKey(fromActivityDefId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.inter.EIRouteDef#getProcessDef()
	 */
	public EIProcessDef getProcessDef() throws BPMException {
		return EIProcessDefManager.getInstance().loadByKey(processDefId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.inter.EIRouteDef#getProcessDefVersion()
	 */
	public EIProcessDefVersion getProcessDefVersion() throws BPMException {
		return EIProcessDefVersionManager.getInstance().loadByKey(
				processDefVersionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.inter.EIRouteDef#getToActivityDef()
	 */
	public EIActivityDef getToActivityDef() throws BPMException {
		return EIActivityDefManager.getInstance().loadByKey(toActivityDefId);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.inter.EIActivityDef#getListeners()
	 */
	public List getListeners() {
		if (listeners == null) {
			listeners = new ArrayList();
			DbRouteDefManager manager = (DbRouteDefManager) EIRouteDefManager
					.getInstance();
			
			try {
				manager.loadListener(this);
				
			} catch (BPMException e) {
				log.error("load the listeners in route definition "
						+ this.routeDefId + " failed!", e);
				return null;
			}
		}
		return new ArrayList(listeners);
	}

	public void setListeners(List list) {
		listeners = list;
	}

	/**
	 * @return
	 */
	public boolean isListenersModified() {
		if (listeners == null) {
			return false;
		}
		return true;
	}

	

}


