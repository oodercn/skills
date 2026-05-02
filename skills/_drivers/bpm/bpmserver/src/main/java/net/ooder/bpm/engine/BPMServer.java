
package net.ooder.bpm.engine;

import net.ooder.bpm.engine.event.BPMCoreEventControl;
import net.ooder.bpm.engine.inter.*;
import net.ooder.common.JDSException;
import net.ooder.common.util.ClassUtility;
import net.ooder.config.CApplication;
import net.ooder.config.JDSConfig;
import net.ooder.config.UserBean;
import net.ooder.context.JDSActionContext;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.server.JDSClientService;
import net.ooder.server.JDSServer;
import net.ooder.server.OrgManagerFactory;
import net.ooder.server.SubSystem;
import net.ooder.common.ConfigCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>
 * Title: JDS管理系统
 * </p>
 * <p>
 * Description: JDS服务器。主要用于处理引擎的启动及初始化配置取得工作流客户端服务接口实现等方法。
 * </p>
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * <p>
 * Company: www.justdos.net
 * </p>
 *
 * @author wenzhang li
 * @version 2.0
 */
public class BPMServer {

    private static final Logger logger = LoggerFactory.getLogger(BPMServer.class);

    // 工作流服务的单例引用
    private static BPMServer instance;

    // 应用与权限引擎映射
    private static Map<ConfigCode, RightEngine> rightEngines = new HashMap<ConfigCode, RightEngine>();

    // 应用与设备驱动引擎映射
    private static Map<ConfigCode, DeviceEngine> deviceEngines = new HashMap<ConfigCode, DeviceEngine>();

    private static Map<ConfigCode, EventEngine> eventEngines = new HashMap<ConfigCode, EventEngine>();

    private static Map<ConfigCode, ServiceEngine> serviceEngines = new HashMap<ConfigCode, ServiceEngine>();

    // 应用与数据引擎映射
    private static Map<ConfigCode, DataEngine> dataEngines = new HashMap<ConfigCode, DataEngine>();

    // 应用与数据引擎映射
    private static Map<ConfigCode, FileEngine> fileEngines = new HashMap<ConfigCode, FileEngine>();

    private static Map<ConfigCode, AgentEngine> agentEngines = new HashMap<ConfigCode, AgentEngine>();

    // 应用与存储引擎映射
    public static Map<JDSSessionHandle, Map<String, WorkflowClientService>> workflowServiceMap = new HashMap<JDSSessionHandle, Map<String, WorkflowClientService>>();

    private static Map<JDSSessionHandle, Map<String, AdminService>> adminServiceMap = new HashMap<JDSSessionHandle, Map<String, AdminService>>();

    private JDSServer jdsServcer;

    private Map<ConfigCode, CApplication> applicationMap;

    private BPMServer() throws JDSException {
        this.jdsServcer = JDSServer.getInstance();
        this.applicationMap = JDSServer.getClusterClient().getApplicationMap();
        this.init();

    }

    /**
     * 取得JDSServer服务器的单例实现
     *
     * @return
     * @throws JDSException
     */
    public static BPMServer getInstance() throws JDSException {

        if (instance == null) {
            if (instance == null) {
                instance = new BPMServer();
            }
        }

        return instance;
    }

    /**
     * 工作流引擎服务器初始化方法
     *
     * @throws JDSException
     */
    private void init() throws JDSException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        long startTime = System.currentTimeMillis();
        // Load core event listeners
        BPMCoreEventControl ec = BPMCoreEventControl.getInstance();

        for (int i = 0; i < ec.coreProcessEventListeners.size(); i++) {
            logger.info("Load ProcessEventListener: " + ec.coreProcessEventListeners.get(i).getClass().getName());
        }
        for (int i = 0; i < ec.coreActivityEventListeners.size(); i++) {
            logger.info("Load ActivityEventListener: " + ec.coreActivityEventListeners.get(i).getClass().getName());
        }

        loadManagers();


        try {
            initSubSystem();
        } catch (JDSException e) {
            e.printStackTrace();
        }
    }

    public void initSubSystem() throws JDSException {

        Iterator<ConfigCode> it = applicationMap.keySet().iterator();
        for (; it.hasNext(); ) {
            ConfigCode code = it.next();
            CApplication appConfig = applicationMap.get(code);

            if ((appConfig != null) && (appConfig.getName() != null) && appConfig.getConfigCode().equals(UserBean.getInstance().getConfigName().getType())) {

                if (appConfig.getRightEngine() != null) {
                    String rightEngineStr = appConfig.getRightEngine().getImplementation();
                    try {
                        Class clazz = (Class) ClassUtility.loadClass(rightEngineStr);
                        Object[] parms = new Object[1];
                        parms[0] = JDSActionContext.getActionContext().getSystemCode();

                        Constructor constructor = clazz.getConstructor(new Class[]{String.class});
                        RightEngine rightEngine = (RightEngine) constructor.newInstance(parms);

                        rightEngines.put(ConfigCode.fromType(appConfig.getConfigCode()), rightEngine);
                    } catch (ClassNotFoundException cnfe) {
                        throw new JDSException("RightEngine class '" + rightEngineStr + "' not found.", cnfe, JDSException.LOADRIGHTENGINEERROR);
                    } catch (IllegalAccessException iae) {
                        throw new JDSException("", iae, JDSException.LOADRIGHTENGINEERROR);
                    } catch (InstantiationException ie) {
                        throw new JDSException("", ie, JDSException.LOADRIGHTENGINEERROR);
                    } catch (ClassCastException cce) {
                        throw new JDSException("RightEngine must implement net.ooder.bpm.engine.RightEngine interface.", cce, JDSException.LOADRIGHTENGINEERROR);
                    } catch (NoSuchMethodException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (SecurityException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    RightEngine rightEngine = new IOTRightEngine(appConfig.getConfigCode());

                    rightEngines.put(ConfigCode.fromType(appConfig.getConfigCode()), rightEngine);

                    // throw new JDSException(
                    // "RightEngine for application not configured.",
                    // JDSException.LOADRIGHTENGINEERROR);
                }

                if (appConfig.getFileEngine() != null) {
                    String fileEngineStr = appConfig.getFileEngine().getImplementation();
                    try {

                        Class clazz = (Class) ClassUtility.loadClass(fileEngineStr);
                        Object[] parms = new Object[1];
                        parms[0] = JDSActionContext.getActionContext().getSystemCode();
                        Constructor constructor = clazz.getConstructor(new Class[]{String.class});
                        FileEngine fileEngine = (FileEngine) constructor.newInstance(parms);

                        fileEngines.put(ConfigCode.fromType(appConfig.getConfigCode()), fileEngine);
                    } catch (ClassNotFoundException cnfe) {
                        throw new JDSException("FileEngine class '" + fileEngineStr + "' not found.", cnfe, JDSException.LOADFILEENGINEERROR);
                    } catch (IllegalAccessException iae) {
                        throw new JDSException("", iae, JDSException.LOADFILEENGINEERROR);
                    } catch (InstantiationException ie) {
                        throw new JDSException("", ie, JDSException.LOADFILEENGINEERROR);
                    } catch (ClassCastException cce) {
                        throw new JDSException("FileEngine must implement net.ooder.bpm.engine.FileEngine interface.", cce, JDSException.LOADFILEENGINEERROR);
                    } catch (NoSuchMethodException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (SecurityException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                } else {
                    FileEngine fileEngine = new DefaultFileEngine(appConfig.getConfigCode());
                    fileEngine.setSystemCode(JDSActionContext.getActionContext().getSystemCode());
                    fileEngines.put(ConfigCode.fromType(JDSActionContext.getActionContext().getSystemCode()), fileEngine);
                }

                if (appConfig.getDataEngine() != null) {
                    String dataEngineStr = appConfig.getDataEngine().getImplementation();
                    try {

                        Class clazz = (Class) ClassUtility.loadClass(dataEngineStr);

                        if (clazz.getClass().isAssignableFrom(DataEngine.class)) {
                            Object[] parms = new Object[1];
                            parms[0] = appConfig.getConfigCode();
                            Constructor constructor = clazz.getConstructor(new Class[]{String.class});
                            DataEngine dataEngine = (DataEngine) constructor.newInstance(parms);
                            dataEngine.setSystemCode(JDSActionContext.getActionContext().getSystemCode());
                            dataEngines.put(ConfigCode.fromType(JDSActionContext.getActionContext().getSystemCode()), dataEngine);
                        }

                    } catch (ClassNotFoundException cnfe) {
                        throw new JDSException("dataEngine class '" + dataEngineStr + "' not found.", cnfe, JDSException.LOADRIGHTENGINEERROR);
                    } catch (IllegalAccessException iae) {
                        throw new JDSException("", iae, JDSException.LOADRIGHTENGINEERROR);
                    } catch (InstantiationException ie) {
                        throw new JDSException("", ie, JDSException.LOADRIGHTENGINEERROR);
                    } catch (ClassCastException cce) {
                        throw new JDSException("dataEngine must implement net.ooder.bpm.engine.DataEngine interface.", cce, JDSException.LOADRIGHTENGINEERROR);
                    } catch (NoSuchMethodException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (SecurityException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                // else
                // throw new JDSException("dataEngine for application not configured.",
                // JDSException.LOADRIGHTENGINEERROR);

                AgentEngine agentEngine = new AgentEngineImpl();
                agentEngines.put(ConfigCode.fromType(JDSActionContext.getActionContext().getSystemCode()), agentEngine);
            }
        }

    }

    private void loadManagers() {

        String processDefManagerStr = JDSConfig.getValue("ProcessDefManager.class");
        if (processDefManagerStr != null) {
            try {
                EIProcessDefManager processDefManager = (EIProcessDefManager) (ClassUtility.loadClass(processDefManagerStr).newInstance());
                EIProcessDefManager.setInstance(processDefManager);
            } catch (Exception e) {
                logger.error("Fail to load configed ProcessDefManager: " + processDefManagerStr);
            }
        }
        logger.info("Load ProcessDefManager: " + EIProcessDefManager.getInstance().getClass().getName());

        String processDefVersionManagerStr = JDSConfig.getValue("ProcessDefVersionManager.class");
        if (processDefVersionManagerStr != null) {
            try {
                EIProcessDefVersionManager processDefVersionManager = (EIProcessDefVersionManager) (ClassUtility.loadClass(processDefVersionManagerStr).newInstance());
                EIProcessDefVersionManager.setInstance(processDefVersionManager);
            } catch (Exception e) {
                logger.error("Fail to load configed ProcessDefVersionManager: " + processDefVersionManagerStr);
            }
        }
        logger.info("Load ProcessDefVersionManager: " + EIProcessDefVersionManager.getInstance().getClass().getName());

        String processInstManagerStr = JDSConfig.getValue("ProcessInstManager.class");
        if (processInstManagerStr != null) {
            try {
                EIProcessInstManager processInstManager = (EIProcessInstManager) (ClassUtility.loadClass(processInstManagerStr).newInstance());
                EIProcessInstManager.setInstance(processInstManager);
            } catch (Exception e) {
                logger.error("Fail to load configed ProcessInstManager: " + processInstManagerStr);
            }
        }
        logger.info("Load ProcessInstManager: " + EIProcessInstManager.getInstance().getClass().getName());

        String activityDefManagerStr = JDSConfig.getValue("ActivityDefManager.class");
        if (activityDefManagerStr != null) {
            try {
                EIActivityDefManager activityDefManager = (EIActivityDefManager) (ClassUtility.loadClass(activityDefManagerStr).newInstance());
                EIActivityDefManager.setInstance(activityDefManager);
            } catch (Exception e) {
                logger.error("Fail to load configed ActivityDefManager: " + activityDefManagerStr);
            }
        }
        logger.info("Load ActivityDefManager: " + EIActivityDefManager.getInstance().getClass().getName());

        String activityInstManagerStr = JDSConfig.getValue("ActivityInstManager.class");
        if (activityInstManagerStr != null) {
            try {
                EIActivityInstManager activityInstManager = (EIActivityInstManager) (ClassUtility.loadClass(activityInstManagerStr).newInstance());
                EIActivityInstManager.setInstance(activityInstManager);
            } catch (Exception e) {
                logger.error("Fail to load configed ActivityInstManager: " + activityInstManagerStr);
            }
        }
        logger.info("Load ActivityInstManager: " + EIActivityInstManager.getInstance().getClass().getName());

        String activityInstHistoryManagerStr = JDSConfig.getValue("ActivityInstHistoryManager.class");
        if (activityInstHistoryManagerStr != null) {
            try {
                EIActivityInstHistoryManager activityInstHistoryManager = (EIActivityInstHistoryManager) (ClassUtility.loadClass(activityInstHistoryManagerStr).newInstance());
                EIActivityInstHistoryManager.setInstance(activityInstHistoryManager);
            } catch (Exception e) {
                logger.error("Fail to load configed ActivityInstHistoryManager: " + activityInstHistoryManagerStr);
            }
        }
        logger.info("Load ActivityInstHistoryManager: " + EIActivityInstHistoryManager.getInstance().getClass().getName());

        String attributeDefManagerStr = JDSConfig.getValue("AttributeDefManager.class");
        if (attributeDefManagerStr != null) {
            try {
                EIAttributeDefManager attributeDefManager = (EIAttributeDefManager) (ClassUtility.loadClass(attributeDefManagerStr).newInstance());
                EIAttributeDefManager.setInstance(attributeDefManager);
            } catch (Exception e) {
                logger.error("Fail to load configed AttributeDefManager: " + attributeDefManagerStr);
            }
        }
        logger.info("Load AttributeDefManager: " + EIAttributeDefManager.getInstance().getClass().getName());

        String attributeInstManagerStr = JDSConfig.getValue("AttributeInstManager.class");
        if (attributeInstManagerStr != null) {
            try {
                EIAttributeInstManager attributeInstManager = (EIAttributeInstManager) (ClassUtility.loadClass(attributeInstManagerStr).newInstance());
                EIAttributeInstManager.setInstance(attributeInstManager);
            } catch (Exception e) {
                logger.error("Fail to load configed AttributeInstManager: " + attributeInstManagerStr);
            }
        }
        logger.info("Load AttributeInstManager: " + EIAttributeInstManager.getInstance().getClass().getName());

        String routeDefManagerStr = JDSConfig.getValue("RouteDefManager.class");
        if (routeDefManagerStr != null) {
            try {
                EIRouteDefManager routeDefManager = (EIRouteDefManager) (ClassUtility.loadClass(routeDefManagerStr).newInstance());
                EIRouteDefManager.setInstance(routeDefManager);
            } catch (Exception e) {
                logger.error("Fail to load configed RouteDefManager: " + routeDefManagerStr);
            }
        }
        logger.info("Load RouteDefManager: " + EIRouteDefManager.getInstance().getClass().getName());

        String routeInstManagerStr = JDSConfig.getValue("RouteInstManager.class");
        if (attributeInstManagerStr != null) {
            try {
                EIRouteInstManager routeInstManager = (EIRouteInstManager) (ClassUtility.loadClass(routeInstManagerStr).newInstance());
                EIRouteInstManager.setInstance(routeInstManager);
            } catch (Exception e) {
                logger.error("Fail to load configed RouteInstManager: " + routeInstManagerStr);
            }
        }
        logger.info("Load RouteInstManager: " + EIRouteInstManager.getInstance().getClass().getName());

    }

    /**
     * 根据客户端服务取得管理服务对象，该AdminService已自动连接（connect），如果未找到该应用相应的AdminService配置，则返回null。
     *
     * @param client 客户端服务
     * @return
     * @throws JDSException
     */
    public AdminService getAdminService(WorkflowClientService client) throws JDSException {
        if (client == null) {
            throw new JDSException("Session invalid error!", JDSException.NOTLOGINEDERROR);
        }

        JDSSessionHandle sessionHandle = client.getSessionHandle();
        String systemCode = client.getSystemCode();
        AdminService admin = null;
        Map admins = (Map) adminServiceMap.get(sessionHandle);
        if (admins == null) {
            admins = new HashMap();
            adminServiceMap.put(sessionHandle, admins);
        }
        // 如果已经有该AdminService则直接返回之。
        if ((admin = (AdminService) admins.get(systemCode)) != null) {
            return admin;
        } else { // 否则，新建一个，然后放入该sessionHandle所对应的adminServiceMap中
            CApplication app = applicationMap.get(systemCode);
            if (app == null) {
                throw new JDSException("The application config for '" + systemCode + "' not found!");
            }
            String adminServiceClassName = null;
            if (app.getAdminService() != null) {
                adminServiceClassName = app.getAdminService().getImplementation();
            }
            // 如果没有配置该应用的AdminService，则返回空。
            if (adminServiceClassName == null || adminServiceClassName.equals("")) {
                return null;
            }
            try {
                admin = (AdminService) ClassUtility.loadClass(adminServiceClassName).newInstance();
            } catch (ClassNotFoundException cnfe) {
                throw new JDSException("AdminService class '" + adminServiceClassName + "' not found.", cnfe, JDSException.LOADADMINSERVICEERROR);
            } catch (IllegalAccessException iae) {
                throw new JDSException("", iae, JDSException.LOADADMINSERVICEERROR);
            } catch (InstantiationException ie) {
                throw new JDSException("", ie, JDSException.LOADADMINSERVICEERROR);
            } catch (ClassCastException cce) {
                throw new JDSException("AdminService must implement net.ooder.bpm.engine.AdminService interface.", cce, JDSException.LOADADMINSERVICEERROR);
            }

            // if (admin == null) {
            // throw new JDSException("Admin service not found!",
            // JDSException.LOADADMINSERVICEERROR);
            // }
            if (admin != null) {
                admin.setClientService(client);
                admin.setRightEngine((RightEngine) rightEngines.get(client.getSystemCode()));
                admin.setWorkflowEngine(WorkflowEngineImpl.getEngine(client.getSystemCode()));
                admin.setWorkflowServer(this);

                admin.setDataEngine((DataEngine) dataEngines.get(client.getSystemCode()));
                admin.setConnInfo(client.getConnectInfo());
                admin.setSessionHandle(sessionHandle);
                admin.setSystemCode(systemCode);

                admins.put(systemCode, admin);
            }
            return admin;
        }
    }

    public WorkflowClientService getWorkflowService(JDSSessionHandle sessionHandle, String systemCode) throws JDSException {

        SubSystem system = JDSServer.getClusterClient().getSystem(systemCode);

        JDSClientService client = jdsServcer.getJDSClientService(sessionHandle, system.getConfigname());
        if (client == null) {
            throw new JDSException("Session invalid error!", JDSException.NOTLOGINEDERROR);
        }

        return getWorkflowService(client);
    }

    // =====================================工作流服务器载入配置相关方法

    /**
     * 根据系统代码取得相应的权限引擎。初始载入时将读取配置文件中的 'bpm.RightEngine'，并生成相应的系统代码和权限引擎实现的映射。
     *
     * @param systemCode 系统代码（例如：sp, CMS等等）
     * @return 权限引擎实现
     * @throws JDSException
     */
    public static RightEngine getRigthEngine(String systemCode) throws JDSException {


        if (!JDSServer.started) {
            throw new JDSException("JDSServer not started!", JDSException.SERVERNOTSTARTEDERROR);
        }


        RightEngine engine = (RightEngine) rightEngines.get(systemCode);
        if (engine == null) {
            engine = new IOTRightEngine(systemCode);
            // throw new JDSException("RightEngine for system code '" + systemCode + "' not found. Please check the configuration file 'BPM_config.xml'.", JDSException.LOADRIGHTENGINEERROR);
        }
        return engine;
    }

    // =====================================工作流服务器载入配置相关方法

    /**
     * 根据系统代码取得相应的设备引擎。初始载入时将读取配置文件中的 'bpm.EventEngine'，并生成相应的系统代码和设备引擎实现的映射。
     *
     * @param systemCode 系统代码（例如：iot, CMS等等）
     * @return 权限引擎实现
     * @throws JDSException
     */
    public static DeviceEngine getDeviceEngine(String systemCode) throws JDSException {
        if (!JDSServer.started) {
            throw new JDSException("JDSServer not started!", JDSException.SERVERNOTSTARTEDERROR);
        }
        DeviceEngine engine = (DeviceEngine) deviceEngines.get(systemCode);
        if (engine == null) {
            engine = new IOTDeviceEngine(systemCode);
            deviceEngines.put(ConfigCode.fromType(systemCode), engine);

            // throw new JDSException(
            // "DeviceEngine for system code '"
            // + systemCode
            // + "' not found. Please check the configuration file 'BPM_config.xml'.",
            // JDSException.LOADRIGHTENGINEERROR);
        }
        return engine;
    }

    // =====================================工作流服务器载入配置相关方法

    /**
     * 根据系统代码取得相应的设备引擎。初始载入时将读取配置文件中的 'bpm.EventEngine'，并生成相应的系统代码和设备引擎实现的映射。
     *
     * @param systemCode 系统代码（例如：iot, CMS等等）
     * @return 权限引擎实现
     * @throws JDSException
     */
    public static EventEngine getEventEngine(String systemCode) throws JDSException {
        if (!JDSServer.started) {
            throw new JDSException("JDSServer not started!", JDSException.SERVERNOTSTARTEDERROR);
        }
        EventEngine engine = (EventEngine) eventEngines.get(systemCode);
        if (engine == null) {
            engine = new IOTEventEngine(systemCode);
            eventEngines.put(ConfigCode.fromType(systemCode), engine);

        }
        return engine;
    }

    /**
     * 根据系统代码取得相应的设备引擎。初始载入时将读取配置文件中的 'bpm.EventEngine'，并生成相应的系统代码和设备引擎实现的映射。
     *
     * @param systemCode 系统代码（例如：iot, CMS等等）
     * @return 权限引擎实现
     * @throws JDSException
     */
    public static ServiceEngine getServiceEngine(String systemCode) throws JDSException {
        if (!JDSServer.started) {
            throw new JDSException("JDSServer not started!", JDSException.SERVERNOTSTARTEDERROR);
        }
        ServiceEngine engine = (ServiceEngine) serviceEngines.get(systemCode);
        if (engine == null) {
            engine = new IOTServiceEngine(systemCode);
            serviceEngines.put(ConfigCode.fromType(systemCode), engine);
        }
        return engine;
    }

    /**
     * 根据系统代码取得相应的数据引擎。初始载入时将读取配置文件中的 'bpm.DataEngine'，并生成相应的系统代码和数据引擎实现的映射。
     *
     * @param systemCode 系统代码（例如：sp, CMS等等）
     * @return 数据引擎实现
     * @throws JDSException
     */
    public static DataEngine getDataEngine(String systemCode) throws JDSException {
        if (!JDSServer.started) {
            throw new JDSException("JDSServer not started!", JDSException.SERVERNOTSTARTEDERROR);
        }
        DataEngine engine = (DataEngine) dataEngines.get(systemCode);

        if (engine == null) {
            try {
                engine = new VfsDataEngine(systemCode);
            } catch (BPMException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // throw new JDSException(
            // "dateEngine for system code '"
            // + systemCode
            // + "' not found. Please check the configuration file 'BPM_config.xml'.",
            // JDSException.LOADRIGHTENGINEERROR);
        }
        return engine;
    }

    /**
     * 根据系统代码取得相应的大数据引擎。初始载入时将读取配置文件中的 'bpm.VFSEngine'，并生成相应的系统代码和文件引擎实现的映射。
     *
     * @param systemCode 系统代码（例如：sp, CMS等等）
     * @return 数据引擎实现
     * @throws JDSException
     */
    public static FileEngine getFileEngine(String systemCode) throws JDSException {
        if (!JDSServer.started) {
            throw new JDSException("JDSServer not started!", JDSException.SERVERNOTSTARTEDERROR);
        }

        FileEngine engine = (FileEngine) fileEngines.get(systemCode);

        if (engine == null) {
            engine = new VFSFileEngine(systemCode);

            // throw new JDSException("VFSEngine for system code '" + systemCode + "' not found. Please check the configuration file 'BPM_config.xml'.", JDSException.LOADRIGHTENGINEERROR);
        }

        return engine;
    }

    public static AgentEngine getAgentEngine(String systemCode) throws JDSException {
        if (!JDSServer.started) {
            throw new JDSException("JDSServer not started!", JDSException.SERVERNOTSTARTEDERROR);
        }

        AgentEngine engine = (AgentEngine) agentEngines.get(systemCode);

        if (engine == null) {
            engine = new AgentEngineImpl();
            agentEngines.put(ConfigCode.fromType(systemCode), engine);
        }

        return engine;
    }

    public static BPMCoreEventControl getEventControl() throws JDSException {
        if (!JDSServer.started) {
            throw new JDSException("JDSServer not started!", JDSException.SERVERNOTSTARTEDERROR);
        }
        return BPMCoreEventControl.getInstance();
    }

    /**
     * 根据客户端服务取得管理服务对象，该VFSClientService已自动连接（connect），如果未找到该应用相应的AdminService配置，则返回null。
     *
     * @param client 客户端服务
     * @return
     * @throws JDSException
     */
    public WorkflowClientService getWorkflowService(JDSClientService client) throws JDSException {
        if (client == null) {
            throw new JDSException("Session invalid error!", JDSException.NOTLOGINEDERROR);
        }

        JDSSessionHandle sessionHandle = client.getSessionHandle();
        String systemCode = client.getSystemCode();
        WorkflowClientService workflowService = null;
        Map vfsClients = (Map) workflowServiceMap.get(sessionHandle);
        if (vfsClients == null) {
            vfsClients = new HashMap();
            workflowServiceMap.put(sessionHandle, vfsClients);
        }
        SubSystem system = JDSServer.getClusterClient().getSystem(systemCode);
        // 如果已经有该AdminService则直接返回之。
        if ((workflowService = (WorkflowClientService) vfsClients.get(systemCode)) != null) {
            return workflowService;
        } else { // 否则，新建一个，然后放入该sessionHandle所对应的adminServiceMap中
            CApplication app = applicationMap.get(system.getConfigname());
            if (app == null) {
                throw new JDSException("The application config for '" + systemCode + "' not found!");
            }
            String workflowServiceClassName = null;
            if (app.getWorkflowService() != null) {
                workflowServiceClassName = app.getWorkflowService().getImplementation();
            }
            // 如果没有配置该应用的，则返回空。
            if (workflowServiceClassName == null || workflowServiceClassName.equals("")) {
                workflowService = new WorkflowClientServiceImpl(client);
                // return null;
            } else {

                try {
                    workflowService = (WorkflowClientService) ClassUtility.loadClass(workflowServiceClassName).newInstance();
                } catch (ClassNotFoundException cnfe) {
                    throw new JDSException("workflowService class '" + workflowServiceClassName + "' not found.", cnfe, JDSException.LOADADMINSERVICEERROR);
                } catch (IllegalAccessException iae) {
                    throw new JDSException("", iae, JDSException.LOADADMINSERVICEERROR);
                } catch (InstantiationException ie) {
                    throw new JDSException("", ie, JDSException.LOADADMINSERVICEERROR);
                } catch (ClassCastException cce) {
                    throw new JDSException(workflowServiceClassName + " must implement net.ooder.bpm.engine.workflowService interface.", cce, JDSException.LOADADMINSERVICEERROR);
                }
            }

            if (workflowService != null) {
                // workflowService.setSystemCode(systemCode);
                workflowService.setOrgManager(OrgManagerFactory.getOrgManager(client.getConfigCode()));
                vfsClients.put(systemCode, workflowService);
            }
            return workflowService;
        }
    }

}


