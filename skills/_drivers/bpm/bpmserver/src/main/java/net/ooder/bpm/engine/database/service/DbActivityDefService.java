/**
 * $RCSfile: DbActivityDefRight.java,v $
 * $Revision: 1.1 $
 * $Date: 2014/07/08 00:26:04 $
 *
 * Copyright (C) 2003 itjds, Inc. All rights reserved.
 *
 * This software is the proprietary information of itjds, Inc.
 * Use is subject to license terms.
 */
package net.ooder.bpm.engine.database.service;

import net.ooder.annotation.HttpMethod;
import net.ooder.annotation.RequestType;
import net.ooder.annotation.ResponseType;
import net.ooder.bpm.engine.inter.EIAttributeDef;

import net.ooder.common.cache.CacheSizes;
import net.ooder.common.cache.Cacheable;

import java.io.Serializable;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 默认设备模型使用的活动设备任务定义数据封装类
 * </p>
 * <p>
 * 此类数据来自于活动定义的扩展属�?
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author lwz
 * @version 1.0
 */
public class DbActivityDefService implements Cacheable, Serializable {

  

    private String activityDefId = null;
  

    private String url ;

    private RequestType requestType = RequestType.JSON;

    private ResponseType responseType = ResponseType.JSON;
    
    private HttpMethod method=HttpMethod.POST;

    private String  serviceParams ;
    
  
    private String serviceSelectedID = null;

    

    private EIAttributeDef serviceSelectedAtt = null;

    DbActivityDefService() {
	
    }


    /**
     * @return Returns the activityDefId.
     */
    public String getActivityDefId() {
	return activityDefId;
    }

    public String getUrl() {
        return url;
    }


    public void setUrl(String url) {
        this.url = url;
    }


    public RequestType getRequestType() {
        return requestType;
    }


    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }


    public ResponseType getResponseType() {
        return responseType;
    }


    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }


    public HttpMethod getMethod() {
        return method;
    }


    public void setMethod(HttpMethod method) {
        this.method = method;
    }


    public String getServiceParams() {
        return serviceParams;
    }


    public void setServiceParams(String serviceParams) {
        this.serviceParams = serviceParams;
    }


    public String getServiceSelectedID() {
        return serviceSelectedID;
    }


    public void setServiceSelectedID(String serviceSelectedID) {
        this.serviceSelectedID = serviceSelectedID;
    }


    public EIAttributeDef getServiceSelectedAtt() {
        return serviceSelectedAtt;
    }


    public void setServiceSelectedAtt(EIAttributeDef serviceSelectedAtt) {
        this.serviceSelectedAtt = serviceSelectedAtt;
    }


    /**
     * @param activityDefId
     *            The activityDefId to set.
     */
    public void setActivityDefId(String activityDefId) {
	this.activityDefId = activityDefId;
    }

   



    /*
     * (non-Javadoc)
     * 
     * @see net.ooder.common.cache.Cacheable#getCachedSize()
     */
    public int getCachedSize() {

	int size = 0;

	size += CacheSizes.sizeOfString(activityDefId);
	size += CacheSizes.sizeOfString(requestType.getType());
	size += CacheSizes.sizeOfString(responseType.getType());
	size += CacheSizes.sizeOfString(method.getType());
	
	size += CacheSizes.sizeOfString(serviceParams);
	size += CacheSizes.sizeOfString(serviceSelectedID);

	return size;
    }

}


