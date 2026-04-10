/**
 * $RCSfile: DbActivityInstHistory.java,v $
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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import net.ooder.bpm.engine.BPMConstants;
import net.ooder.bpm.engine.BPMException;
import net.ooder.bpm.engine.inter.EIActivityDef;
import net.ooder.bpm.engine.inter.EIActivityDefManager;
import net.ooder.bpm.engine.inter.EIActivityInst;
import net.ooder.bpm.engine.inter.EIActivityInstHistory;
import net.ooder.bpm.engine.inter.EIActivityInstHistoryManager;
import net.ooder.bpm.engine.inter.EIActivityInstManager;
import net.ooder.bpm.engine.inter.EIAttributeInst;
import net.ooder.bpm.engine.inter.EIProcessInst;
import net.ooder.bpm.engine.inter.EIProcessInstManager;


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
 * Description:
 * </p>
 * <p>
 * Copyright: itjds Copyright (c) 2003
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author chenjie
 * @version 1.0
 */
public class DbActivityInstHistory implements EIActivityInstHistory, Cacheable,
		Serializable {
	private static final Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, DbActivityInstHistory.class);

	private String activityHistoryId;

	private boolean activityHistoryId_is_modified = false;

	private boolean activityHistoryId_is_initialized = false;

	private String processInstId;

	private boolean processInstId_is_modified = false;

	private boolean processInstId_is_initialized = false;

	private String activityDefId;

	private boolean activityDefId_is_modified = false;

	private boolean activityDefId_is_initialized = false;

	private String activityInstId;

	private boolean activityInstId_is_modified = false;

	private boolean activityInstId_is_initialized = false;

	private String urgencyType;

	private boolean urgencytype_is_modified = false;

	private boolean urgencytype_is_initialized = false;

	private Date arrivedTime;

	private boolean arrivedtime_is_modified = false;

	private boolean arrivedtime_is_initialized = false;

	private Date limitTime;

	private boolean limittime_is_modified = false;

	private boolean limittime_is_initialized = false;

	private Date startTime;

	private boolean starttime_is_modified = false;

	private boolean starttime_is_initialized = false;

	private Date endTime;

	private boolean endTime_is_modified = false;

	private boolean endTime_is_initialized = false;

	private String recieveState;

	private boolean recievestate_is_modified = false;

	private boolean recievestate_is_initialized = false;

	private String dealState;

	private boolean dealstate_is_modified = false;

	private boolean dealstate_is_initialized = false;

	private String runState;

	private boolean runstate_is_modified = false;

	private boolean runstate_is_initialized = false;

	private boolean _isNew = true;

	private boolean _isAttributeModified = true;

	Map attributeTopMap = null; // store the top level attribute

	Map attributeIdMap = null; // store all attribute in this activity
								// definition

	/**
	 */
	DbActivityInstHistory() {
	}

	/**
	 * Getter method for activityhistoryId
	 * 
	 * @return the value of activityhistoryId
	 */
	public String getActivityHistoryId() {
		return activityHistoryId;
	}

	/**
	 * Setter method for activityhistoryId
	 * 
	 * @param newVal
	 *            The new value to be assigned to activityhistoryId
	 */
	public void setActivityHistoryId(String newVal) {
		if ((newVal != null && newVal.equals(this.activityHistoryId) == true)
				|| (newVal == null && this.activityHistoryId == null))
			return;
		this.activityHistoryId = newVal;
		activityHistoryId_is_modified = true;
		activityHistoryId_is_initialized = true;
	}

	/**
	 * Determine if the activityhistoryId is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isActivityhistoryIdModified() {
		return activityHistoryId_is_modified;
	}

	/**
	 * Determine if the activityhistoryId has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isActivityhistoryIdInitialized() {
		return activityHistoryId_is_initialized;
	}

	/**
	 * Getter method for processinstId
	 * 
	 * @return the value of processinstId
	 */
	public String getProcessInstId() {
		return processInstId;
	}

	/**
	 * Setter method for processinstId
	 * 
	 * @param newVal
	 *            The new value to be assigned to processinstId
	 */
	public void setProcessInstId(String newVal) {
		if ((newVal != null && newVal.equals(this.processInstId) == true)
				|| (newVal == null && this.processInstId == null))
			return;
		this.processInstId = newVal;
		processInstId_is_modified = true;
		processInstId_is_initialized = true;
	}

	/**
	 * Determine if the processinstId is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isProcessinstIdModified() {
		return processInstId_is_modified;
	}

	/**
	 * Determine if the processinstId has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isProcessinstIdInitialized() {
		return processInstId_is_initialized;
	}

	/**
	 * Getter method for activitydefId
	 * 
	 * @return the value of activitydefId
	 */
	public String getActivityDefId() {
		return activityDefId;
	}

	/**
	 * Setter method for activitydefId
	 * 
	 * @param newVal
	 *            The new value to be assigned to activitydefId
	 */
	public void setActivityDefId(String newVal) {
		if ((newVal != null && newVal.equals(this.activityDefId) == true)
				|| (newVal == null && this.activityDefId == null))
			return;
		this.activityDefId = newVal;
		activityDefId_is_modified = true;
		activityDefId_is_initialized = true;
	}

	/**
	 * Determine if the activitydefId is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isActivitydefIdModified() {
		return activityDefId_is_modified;
	}

	/**
	 * Determine if the activitydefId has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isActivitydefIdInitialized() {
		return activityDefId_is_initialized;
	}

	/**
	 * Getter method for activityinstId
	 * 
	 * @return the value of activityinstId
	 */
	public String getActivityInstId() {
		return activityInstId;
	}

	/**
	 * Setter method for activityinstId
	 * 
	 * @param newVal
	 *            The new value to be assigned to activityinstId
	 */
	public void setActivityInstId(String newVal) {
		if ((newVal != null && newVal.equals(this.activityInstId) == true)
				|| (newVal == null && this.activityInstId == null))
			return;
		this.activityInstId = newVal;
		activityInstId_is_modified = true;
		activityInstId_is_initialized = true;
	}

	/**
	 * Determine if the activityinstId is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isActivityinstIdModified() {
		return activityInstId_is_modified;
	}

	/**
	 * Determine if the activityinstId has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isActivityinstIdInitialized() {
		return activityInstId_is_initialized;
	}

	/**
	 * Getter method for urgencyType
	 * 
	 * @return the value of urgencyType
	 */
	public String getUrgency() {
		return urgencyType;
	}

	/**
	 * Setter method for urgencyType
	 * 
	 * @param newVal
	 *            The new value to be assigned to urgencyType
	 */
	public void setUrgency(String newVal) {
		if ((newVal != null && newVal.equals(this.urgencyType) == true)
				|| (newVal == null && this.urgencyType == null))
			return;
		this.urgencyType = newVal;
		urgencytype_is_modified = true;
		urgencytype_is_initialized = true;
	}

	/**
	 * Determine if the urgencyType is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isUrgencytypeModified() {
		return urgencytype_is_modified;
	}

	/**
	 * Determine if the urgencyType has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isUrgencytypeInitialized() {
		return urgencytype_is_initialized;
	}

	/**
	 * Getter method for arrivedTime
	 * 
	 * @return the value of arrivedTime
	 */
	public Date getArrivedTime() {
		return arrivedTime;
	}

	/**
	 * Setter method for arrivedTime
	 * 
	 * @param newVal
	 *            The new value to be assigned to arrivedTime
	 */
	public void setArrivedTime(Date newVal) {
		if ((newVal != null && newVal.equals(this.arrivedTime) == true)
				|| (newVal == null && this.arrivedTime == null))
			return;
		this.arrivedTime = newVal;
		arrivedtime_is_modified = true;
		arrivedtime_is_initialized = true;
	}

	/**
	 * Determine if the arrivedTime is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isArrivedtimeModified() {
		return arrivedtime_is_modified;
	}

	/**
	 * Determine if the arrivedTime has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isArrivedtimeInitialized() {
		return arrivedtime_is_initialized;
	}

	/**
	 * Getter method for limitTime
	 * 
	 * @return the value of limitTime
	 */
	public Date getLimitTime() {
		return limitTime;
	}

	/**
	 * Setter method for limitTime
	 * 
	 * @param newVal
	 *            The new value to be assigned to limitTime
	 */
	public void setLimitTime(Date newVal) {
		if ((newVal != null && newVal.equals(this.limitTime) == true)
				|| (newVal == null && this.limitTime == null))
			return;
		this.limitTime = newVal;
		limittime_is_modified = true;
		limittime_is_initialized = true;
	}

	/**
	 * Determine if the limitTime is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isLimittimeModified() {
		return limittime_is_modified;
	}

	/**
	 * Determine if the limitTime has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isLimittimeInitialized() {
		return limittime_is_initialized;
	}

	/**
	 * Getter method for startTime
	 * 
	 * @return the value of startTime
	 */
	public Date getStartTime() {
		return startTime;
	}

	/**
	 * Setter method for startTime
	 * 
	 * @param newVal
	 *            The new value to be assigned to startTime
	 */
	public void setStartTime(Date newVal) {
		if ((newVal != null && newVal.equals(this.startTime) == true)
				|| (newVal == null && this.startTime == null))
			return;
		this.startTime = newVal;
		starttime_is_modified = true;
		starttime_is_initialized = true;
	}

	/**
	 * Determine if the startTime is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isStarttimeModified() {
		return starttime_is_modified;
	}

	/**
	 * Determine if the startTime has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isStarttimeInitialized() {
		return starttime_is_initialized;
	}

	/**
	 * Getter method for endTime
	 * 
	 * @return the value of endTime
	 */
	public Date getEndTime() {
		return endTime;
	}

	/**
	 * Setter method for endTime
	 * 
	 * @param newVal
	 *            The new value to be assigned to endTime
	 */
	public void setEndTime(Date newVal) {
		if ((newVal != null && newVal.equals(this.endTime) == true)
				|| (newVal == null && this.endTime == null))
			return;
		this.endTime = newVal;
		endTime_is_modified = true;
		endTime_is_initialized = true;
	}

	/**
	 * Determine if the endTime is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isEndTimeModified() {
		return endTime_is_modified;
	}

	/**
	 * Determine if the endTime has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isEndTimeInitialized() {
		return endTime_is_initialized;
	}

	/**
	 * Getter method for recieveState
	 * 
	 * @return the value of recieveState
	 */
	public String getReceiveMethod() {
		return recieveState;
	}

	/**
	 * Setter method for recieveState
	 * 
	 * @param newVal
	 *            The new value to be assigned to recieveState
	 */
	public void setReceiveMethod(String newVal) {
		if ((newVal != null && newVal.equals(this.recieveState) == true)
				|| (newVal == null && this.recieveState == null))
			return;
		this.recieveState = newVal;
		recievestate_is_modified = true;
		recievestate_is_initialized = true;
	}

	/**
	 * Determine if the recieveState is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isRecievestateModified() {
		return recievestate_is_modified;
	}

	/**
	 * Determine if the recieveState has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isRecievestateInitialized() {
		return recievestate_is_initialized;
	}

	/**
	 * Getter method for dealState
	 * 
	 * @return the value of dealState
	 */
	public String getDealMethod() {
		return dealState;
	}

	/**
	 * Setter method for dealState
	 * 
	 * @param newVal
	 *            The new value to be assigned to dealState
	 */
	public void setDealMethod(String newVal) {
		if ((newVal != null && newVal.equals(this.dealState) == true)
				|| (newVal == null && this.dealState == null))
			return;
		this.dealState = newVal;
		dealstate_is_modified = true;
		dealstate_is_initialized = true;
	}

	/**
	 * Determine if the dealState is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isDealstateModified() {
		return dealstate_is_modified;
	}

	/**
	 * Determine if the dealState has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isDealstateInitialized() {
		return dealstate_is_initialized;
	}

	/**
	 * Getter method for runState
	 * 
	 * @return the value of runState
	 */
	public String getRunStatus() {
		return runState;
	}

	/**
	 * Setter method for runState
	 * 
	 * @param newVal
	 *            The new value to be assigned to runState
	 */
	public void setRunStatus(String newVal) {
		if ((newVal != null && newVal.equals(this.runState) == true)
				|| (newVal == null && this.runState == null))
			return;
		this.runState = newVal;
		runstate_is_modified = true;
		runstate_is_initialized = true;
	}

	/**
	 * Determine if the runState is modified or not
	 * 
	 * @return true if the field has been modified, false if the field has not
	 *         been modified
	 */
	public boolean isRunstateModified() {
		return runstate_is_modified;
	}

	/**
	 * Determine if the runState has been initialized or not
	 * 
	 * @return true if the field has been initialized, false otherwise
	 */
	public boolean isRunstateInitialized() {
		return runstate_is_initialized;
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
		return activityHistoryId_is_modified || processInstId_is_modified
				|| activityDefId_is_modified || activityInstId_is_modified
				|| urgencytype_is_modified || arrivedtime_is_modified
				|| limittime_is_modified || starttime_is_modified
				|| endTime_is_modified || recievestate_is_modified
				|| dealstate_is_modified || runstate_is_modified;
	}

	/**
	 * @return
	 */
	public boolean isAttributeModified() {
		if (attributeIdMap == null && attributeTopMap == null) {
			return false;
		}
		return activityHistoryId_is_modified || _isAttributeModified;
	}

	/**
	 * Reset object modification status to "not modified"
	 */
	public void resetIsModified() {
		activityHistoryId_is_modified = false;
		processInstId_is_modified = false;
		activityDefId_is_modified = false;
		activityInstId_is_modified = false;
		urgencytype_is_modified = false;
		arrivedtime_is_modified = false;
		limittime_is_modified = false;
		starttime_is_modified = false;
		endTime_is_modified = false;
		recievestate_is_modified = false;
		dealstate_is_modified = false;
		runstate_is_modified = false;
	}

	/**
	 * Copy the passed bean into the current bean
	 * 
	 * @param bean
	 *            the bean to copy into the current bean
	 */
	public void copy(EIActivityInstHistory bean) {
		setActivityHistoryId(bean.getActivityHistoryId());
		setProcessInstId(bean.getProcessInstId());
		setActivityDefId(bean.getActivityDefId());
		setActivityInstId(bean.getActivityInstId());
		setUrgency(bean.getUrgency());
		setArrivedTime(bean.getArrivedTime());
		setLimitTime(bean.getLimitTime());
		setStartTime(bean.getStartTime());
		setEndTime(bean.getEndTime());
		setReceiveMethod(bean.getReceiveMethod());
		setDealMethod(bean.getDealMethod());
		setRunStatus(bean.getRunStatus());
	}

	/**
	 * 鍙栧緱灞炴€у€硷紝褰撻渶瑕佸彇寰楀甫灞傛鍏崇郴鐨勫睘鎬у€兼椂锛屽睘鎬у悕绉板湪姣忓眰闂村姞"."鍒嗗壊 <br>
	 * 渚嬪锛?br>
	 * "Form1.field1.value" - 鍙栧緱鏈€椤跺眰灞炴€т腑鍚嶇О涓篺orm1鐨?
	 * 
	 * @param name
	 * @return
	 */
	public EIAttributeInst getAttribute(String name) {
		if (name != null) {
			name = name.toUpperCase();
		}

		try {
			loadAttributes();
		} catch (BPMException e) {
			log.error("load activity instance history attribute error!");
			return null;
		}

		StringTokenizer st = new StringTokenizer(name, ".");
		DbAttributeInst subAtt = null;
		while (st.hasMoreTokens()) {
			String subname = st.nextToken();
			if (subAtt == null) { // top level
				subAtt = (DbAttributeInst) attributeTopMap.get(subname);
				
			} else {
				subAtt = (DbAttributeInst) subAtt.getChild(subname);
			}
		
			if (subAtt == null) {
				return null; // not found
			}
		}
		return subAtt;
	}
	
	
	

	/**
	 * 鍙栧緱鎵€鏈夌殑灞炴€х被鍨?
	 * 
	 * @return
	 */
	public List getTopAttribute() {
		try {
			loadAttributes();
		} catch (BPMException e) {
			log.error("load activity instance attribute error!");
			return new ArrayList(0);
		}

		List list = new ArrayList(attributeTopMap.values());
		List result = new ArrayList();
		for (Iterator it = list.iterator(); it.hasNext();) {
			EIAttributeInst attr = (EIAttributeInst) it.next();
			result.addAll(attr.getChildren());
		}
		return result;
	}


	/**
	 * 鍙栧緱娲诲姩涓殑鎵╁睍灞炴€у€硷紝姝ゅ€煎凡缁忕粡杩囪В閲?br>
	 * 褰撻渶瑕佸彇寰楀甫灞傛鍏崇郴鐨勫睘鎬у€兼椂锛屽睘鎬у悕绉板湪姣忓眰闂村姞"."鍒嗗壊 <br>
	 * 渚嬪锛?br>
	 * "Form1.field1.readRight" - 鍙栧緱鏈€椤跺眰灞炴€т腑鍚嶇О涓篺orm1鐨勪笅鐨勫悕绉颁负field1鐨勫瓙灞炴€т笅鍚嶇О涓簐alue鐨勫€?
	 * 
	 * @param name
	 *            灞炴€у悕绉?"."闅斿紑锛屼笉鍖哄垎澶у皬鍐?
	 * @return 灞炴€у€硷紝姝ゅ€煎凡缁忎娇鐢ˋttributeInterpret鎺ュ彛瀹炵幇绫昏В閲婂悗鐨勫€?
	 */
	public Object getAttributeInterpretedValue(String name) {
		EIAttributeInst attInst = getAttribute(name);
		if (attInst == null) {
			return null;
		}
		return attInst.getInterpretedValue();
	}

	/**
	 * 鍙栧緱娲诲姩涓殑鎵╁睍灞炴€у€?姝ゅ€兼槸鏈粡瑙ｆ瀽鐨勫師鍊硷紝鍗虫暟鎹簱涓偍瀛樼殑鍊?br>
	 * 褰撻渶瑕佸彇寰楀甫灞傛鍏崇郴鐨勫睘鎬у€兼椂锛屽睘鎬у悕绉板湪姣忓眰闂村姞"."鍒嗗壊 <br>
	 * 渚嬪锛?br>
	 * "Form1.field1.readRight" - 鍙栧緱鏈€椤跺眰灞炴€т腑鍚嶇О涓篺orm1鐨勪笅鐨勫悕绉颁负field1鐨勫瓙灞炴€т笅鍚嶇О涓簐alue鐨勫€?
	 * 
	 * @param name
	 *            灞炴€у悕绉?"."闅斿紑锛屼笉鍖哄垎澶у皬鍐?
	 * @return 灞炴€у€硷紝姝ゅ€兼槸鏈粡瑙ｆ瀽鐨勫師鍊?
	 */
	public String getAttributeValue(String name) {
		EIAttributeInst attInst = getAttribute(name);
		if (attInst == null) {
			return null;
		}
		return attInst.getValue();
	}

	/**
	 * 鍙栧緱鎵€鏈夋墿灞曞睘鎬у疄渚?
	 * 
	 * @return
	 */
	public List getAllAttribute() {
		try {
			loadAttributes();
		} catch (BPMException e) {
			log.error("load activity instance history attribute error!");
			return new ArrayList(0);
		}
		return new ArrayList(attributeIdMap.values());
	}

	public void setAttribute(String parentName, EIAttributeInst attr)
			throws BPMException {
		if (parentName != null) {
			parentName = parentName.toUpperCase();
		}
		loadAttributes();
		EIAttributeInst parentAtt = null;

		if (parentName != null && !parentName.equals("")) {
			parentAtt = getAttribute(parentName);
			if (parentAtt == null) {
				if (parentName.indexOf(".") == -1) { // top level
					parentAtt = new DbAttributeInst();
					parentAtt.setName(parentName);
					parentAtt.setType(parentName);
					setAttribute(null, parentAtt);
				} else {
					// error: parentAtt not in this activity definition!
					throw new BPMException(
							"parentAtt not in this activity definition! parentAtt:"
									+ parentName + ", attName:"
									+ attr.getName());
				}
			}
			attr.setType(parentAtt.getType());
		}

		if (parentAtt != null) {
			// sub attribute
			EIAttributeInst oldAtt = (EIAttributeInst) parentAtt.getChild(attr
					.getName());
			if (oldAtt != null) { // exist same name attribute in this tree
				// 鏇存柊灞炴€э紝骞舵墽琛屾暟鎹簱鍒犻櫎鎿嶄綔
				updateAttribute(oldAtt);
				return;
			}
			parentAtt.addChild(attr); // change the new attribute definition!
			attributeIdMap.put(attr.getId(), attr);
			// 鎵ц鏁版嵁搴撴彃鍏ユ搷浣?
			try {
				((DbActivityInstHistoryManager) EIActivityInstHistoryManager
						.getInstance()).addAttributeToDb(this,
						(DbAttributeInst) attr);
			} catch (BPMException e) {
				// 淇濆瓨澶辫触锛屽皢鏍囪缃负true
				_isAttributeModified = true;
			}
		} else {
			// top level add to top map
			attributeTopMap.put(attr.getName(), attr);
		}
	
	}

	/**
	 * Return the object string representation
	 * 
	 * @return the object as a string
	 */
	public String toString() {
		return "\n[BPM_ACTIVITYHISTORY] "
				+ "\n - BPM_ACTIVITYHISTORY.ACTIVITYHISTORY_ID = "
				+ (activityHistoryId_is_initialized ? ("["
						+ activityHistoryId.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_ACTIVITYHISTORY.PROCESSINST_ID = "
				+ (processInstId_is_initialized ? ("["
						+ processInstId.toString() + "]") : "not initialized")
				+ ""
				+ "\n - BPM_ACTIVITYHISTORY.ACTIVITYDEF_ID = "
				+ (activityDefId_is_initialized ? ("["
						+ activityDefId.toString() + "]") : "not initialized")
				+ ""
				+ "\n - BPM_ACTIVITYHISTORY.ACTIVITYINST_ID = "
				+ (activityInstId_is_initialized ? ("["
						+ activityInstId.toString() + "]") : "not initialized")
				+ ""
				+ "\n - BPM_ACTIVITYHISTORY.URGENCYTYPE = "
				+ (urgencytype_is_initialized ? ("[" + urgencyType.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_ACTIVITYHISTORY.ARRIVEDTIME = "
				+ (arrivedtime_is_initialized ? ("[" + arrivedTime.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_ACTIVITYHISTORY.LIMITTIME = "
				+ (limittime_is_initialized ? ("[" + limitTime.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_ACTIVITYHISTORY.STARTTIME = "
				+ (starttime_is_initialized ? ("[" + startTime.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_ACTIVITYHISTORY.EDNTIME = "
				+ (endTime_is_initialized ? ("[" + endTime.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_ACTIVITYHISTORY.RECEIVEMETHOD = "
				+ (recievestate_is_initialized ? ("[" + recieveState.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_ACTIVITYHISTORY.DEALMETHOD = "
				+ (dealstate_is_initialized ? ("[" + dealState.toString() + "]")
						: "not initialized")
				+ ""
				+ "\n - BPM_ACTIVITYHISTORY.RUNSTATUS = "
				+ (runstate_is_initialized ? ("[" + runState.toString() + "]")
						: "not initialized") + "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.common.cache.Cacheable#getCachedSize()
	 */
	public int getCachedSize() {
		int size = 0;
		size += CacheSizes.sizeOfString(activityDefId);
		size += CacheSizes.sizeOfString(activityInstId);
		size += CacheSizes.sizeOfString(processInstId);
		if (startTime != null) {
			size += CacheSizes.sizeOfDate(); // startTime
		}
		if (arrivedTime != null) {
			size += CacheSizes.sizeOfDate(); // arrivedTime
		}
		if (limitTime != null) {
			size += CacheSizes.sizeOfDate(); // limitTime
		}
		if (endTime != null) {
			size += CacheSizes.sizeOfDate(); // endTime
		}
		size += CacheSizes.sizeOfString(dealState);
		size += CacheSizes.sizeOfString(recieveState);
		size += CacheSizes.sizeOfString(runState);
		size += CacheSizes.sizeOfString(urgencyType);

		return size;
	}

	public boolean equals(Object o) {
		String uuid;
		if (o instanceof java.lang.String) {
			uuid = (String) o;
		} else if (o instanceof DbActivityInstHistory) {
			uuid = ((DbActivityInstHistory) o).activityHistoryId;
		} else {
			return false;
		}
		return uuid.equalsIgnoreCase(this.activityHistoryId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.inter.EIActivityInstHistory#getActivityDef()
	 */
	public EIActivityDef getActivityDef() throws BPMException {
		return EIActivityDefManager.getInstance().loadByKey(activityDefId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.inter.EIActivityInstHistory#getActivityDef()
	 */
	public EIActivityInst getActivityInst() throws BPMException {
		return EIActivityInstManager.getInstance().loadByKey(activityInstId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ooder.bpm.engine.inter.EIActivityInstHistory#getProcessInst()
	 */
	public EIProcessInst getProcessInst() throws BPMException {
		return EIProcessInstManager.getInstance().loadByKey(processInstId);
	}

	private void loadAttributes() throws BPMException {
		// load first
		if (attributeIdMap == null && attributeTopMap == null) {
			attributeIdMap = new HashMap();
			attributeTopMap = new HashMap();
			DbActivityInstHistoryManager manager = (DbActivityInstHistoryManager) EIActivityInstHistoryManager
					.getInstance();

			manager.loadAttribute(this);
		}
	}

	public void removeAttribute(EIAttributeInst attr) throws BPMException {
		List children = attr.getChildren();
		// 濡傛灉娌℃湁瀛愬睘鎬э紝鍒欑洿鎺ヤ粠map鍙婃暟鎹簱涓垹闄?
		if (children == null || children.size() == 0) {
			attributeIdMap.remove(attr.getId()); // remove it from all
													// attribute map
			try {
				// 鎵ц鏁版嵁搴撳垹闄ゆ搷浣?
				((DbActivityInstHistoryManager) EIActivityInstHistoryManager
						.getInstance())
						.removeAttributeFromDb((DbAttributeInst) attr);
			} catch (BPMException e) {
				// 淇濆瓨澶辫触锛屽皢鏍囪缃负true
				_isAttributeModified = true;
				throw new BPMException("", e);
			}
		} else {
			// 濡傛灉鏈夊瓙灞炴€э紝鍒欓€掑綊鍒犻櫎瀛愬睘鎬?
			for (Iterator iter = children.iterator(); iter.hasNext();) {
				removeAttribute((EIAttributeInst) iter.next());
			}
			// 鐒跺悗灏哸ttr鐨勫瓙灞炴€ф竻闄?
			attr.clearChild();
			// 鍐嶅垹闄よ嚜宸?
			removeAttribute(attr);
		}
	}

	/**
	 * @param attr
	 */
	private void updateAttribute(EIAttributeInst attr) throws BPMException {
		attributeIdMap.put(attr.getId(), attr);
		try {
			// 鎵ц鏁版嵁搴撳垹闄ゆ搷浣?
			((DbActivityInstHistoryManager) EIActivityInstHistoryManager
					.getInstance()).updateAttributeToDb(this,
					(DbAttributeInst) attr);
		} catch (BPMException e) {
			// 淇濆瓨澶辫触锛屽皢鏍囪缃负true
			_isAttributeModified = true;
			throw new BPMException("error occured while removing attribute.", e);
		}
	}
}


