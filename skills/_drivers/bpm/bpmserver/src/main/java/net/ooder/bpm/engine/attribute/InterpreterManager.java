/*
 * Created on 2003-12-15
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package net.ooder.bpm.engine.attribute;

import java.util.HashMap;
import java.util.Map;

import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.logging.Log;
import net.ooder.common.util.ClassUtility;
import net.ooder.bpm.client.attribute.AttributeInterpreter;
import net.ooder.bpm.engine.BPMConstants;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 流程定义管理器接口数据库实现
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 * 
 * @author wenzhangli
 * @version 2.0
 */
public class InterpreterManager {

    private static Log log = LogFactory.getLog(BPMConstants.CONFIG_KEY, InterpreterManager.class);

    private static InterpreterManager instance = null;

    private Map interpreters = new HashMap();

    public static InterpreterManager getInstance() {
	if (instance == null) {
	    synchronized (InterpreterManager.class) {
		if (instance == null) {
		    instance = new InterpreterManager();
		}
	    }
	}
	return instance;
    }

    public InterpreterManager() {

	interpreters.put("NUMBER", new DefaultInterpreter("NUMBER"));
	interpreters.put("STRING", new DefaultInterpreter("STRING"));
	interpreters.put("SINGLELIST", new DefaultInterpreter("SINGLELIST"));
	interpreters.put("MULTILIST", new DefaultInterpreter("MULTILIST"));
	interpreters.put("BOOLEAN", new DefaultInterpreter("BOOLEAN"));
	interpreters.put("PERSON", new DefaultInterpreter("PERSON"));
	interpreters.put("DEPARTMENT", new DefaultInterpreter("DEPARTMENT"));
	interpreters.put("", new DefaultInterpreter("STRING"));
	interpreters.put(null, new DefaultInterpreter("STRING"));
    }

    public AttributeInterpreter getInterpreter(String implClass) {
	AttributeInterpreter interpreter = (AttributeInterpreter) interpreters.get(implClass);
	if (interpreter == null) {
	    Class c = null;
	    Object o = null;
	    try {
		c = ClassUtility.loadClass(implClass);
	    } catch (ClassNotFoundException e) {
		log.error("can't load AttributeInterpreter class " + implClass, e);
		return null;
	    }
	    try {
		o = c.newInstance();
	    } catch (InstantiationException e1) {
		log.error("can't not instantiate the AttributeInterpreter class " + implClass, e1);
		return null;
	    } catch (IllegalAccessException e1) {
		log.error("can't access the the AttributeInterpreter class " + implClass, e1);
		return null;
	    }
	    if (!(o instanceof AttributeInterpreter)) {
		log.error("the class " + implClass + " is not the instance of AttributeInterpreter!");
		return null;
	    }
	    interpreter = (AttributeInterpreter) o;
	}
	return interpreter;
    }

}


