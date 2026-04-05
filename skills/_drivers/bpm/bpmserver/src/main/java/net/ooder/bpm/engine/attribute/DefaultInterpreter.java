/*
 * Created on 2003-12-15
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package net.ooder.bpm.engine.attribute;

import net.ooder.bpm.client.attribute.AttributeInterpreter;
import net.ooder.common.util.StringUtility;
import net.ooder.org.Org;
import net.ooder.org.OrgNotFoundException;
import net.ooder.org.Person;
import net.ooder.org.PersonNotFoundException;
import net.ooder.server.OrgManagerFactory;

import java.math.BigDecimal;

/**
 * <p>
 * Title: JDS系统管理系统
 * </p>
 * <p>
 * Description: 流程定义管理器接口数据库实现
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
public final class DefaultInterpreter implements AttributeInterpreter {

    String defaultClass = null;

    public DefaultInterpreter(String defaultClass) {
        this.defaultClass = defaultClass;
    }

    /**
     * @see net.ooder.bpm.client.attribute.AttributeInterpreter#interpret(java.lang.String)
     */
    public final Object interpret(String value) {
        if (defaultClass.equals("NUMBER")) {
            return new BigDecimal(value);
        } else if (defaultClass.equals("STRING")) {
            return value;
        } else if (defaultClass.equals("SINGLELIST")) {
            return value;
        } else if (defaultClass.equals("MULTILIST")) {
            return StringUtility.split(value, ",");
        } else if (defaultClass.equals("BOOLEAN")) {
            return Boolean.valueOf(value);
        } else if (defaultClass.equals("PERSON")) {
            Person person = null;
            try {
                person = OrgManagerFactory.getOrgManager().getPersonByID(value);
            } catch (PersonNotFoundException e) {
                e.printStackTrace();
                return null;
            }
            return person;
        } else if (defaultClass.equals("DEPARTMENT")) {
            Org org = null;
            try {
                org = OrgManagerFactory.getOrgManager().getOrgByID(value);
            } catch (OrgNotFoundException e) {
                e.printStackTrace();
                return null;
            }

            return org;
        }
        return value;
    }

    /*
     * (non-Javadoc)
     *
     * @see net.ooder.bpm.client.attribute.AttributeInterpreter#instantiate(java.lang.Object)
     */
    public final String instantiate(Object obj) {
        if (defaultClass.equals("NUMBER")) {
            return obj.toString();
        } else if (defaultClass.equals("STRING")) {
            return (String) obj;
        } else if (defaultClass.equals("SINGLELIST")) {
            return (String) obj;
        } else if (defaultClass.equals("MULTILIST")) {
            String[] strs = (String[]) obj;
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < strs.length; i++) {
                sb.append(strs[i]);
            }
            return sb.toString();
        } else if (defaultClass.equals("BOOLEAN")) {
            return obj.toString();
        } else if (defaultClass.equals("PERSON")) {
            Person person = (Person) obj;
            return person.getID();
        } else if (defaultClass.equals("DEPARTMENT")) {
            Org org = (Org) obj;
            return org.getOrgId();
        }
        return obj.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see net.ooder.bpm.client.attribute.AttributeInterpreter#interpretFromInstance(java.lang.String)
     */
    public Object interpretFromInstance(String value) {
        if (defaultClass.equals("NUMBER")) {
            return new BigDecimal(value);
        } else if (defaultClass.equals("STRING")) {
            return value;
        } else if (defaultClass.equals("SINGLELIST")) {
            return value;
        } else if (defaultClass.equals("MULTILIST")) {
            return StringUtility.split(value, ",");
        } else if (defaultClass.equals("BOOLEAN")) {
            return Boolean.valueOf(value);
        } else if (defaultClass.equals("PERSON")) {
            Person person = null;
            try {
                person = OrgManagerFactory.getOrgManager().getPersonByID(value);
            } catch (PersonNotFoundException e) {

                return null;
            }
            return person;
        } else if (defaultClass.equals("DEPARTMENT")) {
            Org org = null;
            try {
                org = OrgManagerFactory.getOrgManager().getOrgByID(value);
            } catch (OrgNotFoundException e) {
                e.printStackTrace();
                return null;
            }

            return org;
        }
        return value;
    }

}


