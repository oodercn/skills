package net.ooder.vfs.engine;

import net.ooder.common.JDSConstants;
import net.ooder.common.JDSException;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.common.util.ClassUtility;
import net.ooder.config.CApplication;
import net.ooder.engine.JDSSessionHandle;
import net.ooder.server.JDSClientService;
import net.ooder.server.JDSServer;
import net.ooder.common.ConfigCode;
import net.ooder.vfs.engine.event.VFSEventControl;
import net.ooder.vfs.service.VFSClient;
import net.ooder.vfs.service.impl.VFSClientImpl;

import java.util.HashMap;
import java.util.Map;

public class VFSServer {

    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, VFSServer.class);

    private static VFSServer instance;

    public static Map<JDSSessionHandle, Map<String, VFSClient>> vfsServiceMap = new HashMap<JDSSessionHandle, Map<String, VFSClient>>();

    private JDSServer jdsServer;

    private Map<ConfigCode, CApplication> applicationMap;

    private VFSRoManager roVfsManager;

    private VFSServer() throws JDSException {
        this.jdsServer = JDSServer.getInstance();
        this.applicationMap = JDSServer.getClusterClient().getApplicationMap();
        this.init();

    }

    public static VFSServer getInstance() throws JDSException {

        if (instance == null) {
            if (instance == null) {
                instance = new VFSServer();
            }
        }

        return instance;
    }

    private void init() throws JDSException {
        logger.info("VFSServer starting ...");
        VFSEventControl ec = VFSEventControl.getInstance();
        this.roVfsManager = VFSRoManager.getInstance();
        roVfsManager.init();
        initSubSystem();
        logger.info("VFSServer end ...");
    }

    public void initSubSystem() throws JDSException {

    }

    public VFSClient getVFSService(JDSSessionHandle sessionHandle, ConfigCode configCode) throws JDSException {
        JDSClientService client = jdsServer.getJDSClientService(sessionHandle, configCode);

        if (client == null) {
            throw new JDSException("Session invalid error!", JDSException.NOTLOGINEDERROR);
        }

        return getVFSService(client);
    }


    public static VFSEventControl getEventControl() throws JDSException {
        if (!JDSServer.started) {
            throw new JDSException("JDSServer not started!", JDSException.SERVERNOTSTARTEDERROR);
        }
        return VFSEventControl.getInstance();
    }

    public VFSClient getVFSService(JDSClientService client) throws JDSException {
        if (client == null) {
            throw new JDSException("Session invalid error!", JDSException.NOTLOGINEDERROR);
        }

        JDSSessionHandle sessionHandle = client.getSessionHandle();
        String systemCode = client.getSystemCode();
        VFSClient vfsService = null;
        Map vfsClients = (Map) vfsServiceMap.get(sessionHandle);
        if (vfsClients == null) {
            vfsClients = new HashMap();
            vfsServiceMap.put(sessionHandle, vfsClients);
        }
        if ((vfsService = (VFSClient) vfsClients.get(systemCode)) != null) {
            return vfsService;
        } else {
            CApplication app = applicationMap.get(systemCode);


            if (app != null) {
                String vfsServiceClassName = null;
                if (app.getVfsService() != null) {
                    vfsServiceClassName = app.getVfsService().getImplementation();
                }
                if (vfsServiceClassName != null && !vfsServiceClassName.equals("")) {
                    try {
                        vfsService = (VFSClient) ClassUtility.loadClass(vfsServiceClassName).newInstance();
                    } catch (ClassNotFoundException cnfe) {
                        throw new JDSException("vfsService class '" + vfsServiceClassName + "' not found.", cnfe, JDSException.LOADADMINSERVICEERROR);
                    } catch (IllegalAccessException iae) {
                        throw new JDSException("", iae, JDSException.LOADADMINSERVICEERROR);
                    } catch (InstantiationException ie) {
                        throw new JDSException("", ie, JDSException.LOADADMINSERVICEERROR);
                    } catch (ClassCastException cce) {
                        throw new JDSException("AdminService must implement net.ooder.bpm.engine.AdminService interface.", cce,
                                JDSException.LOADADMINSERVICEERROR);
                    }
                }
            }


            if (vfsService == null) {
                vfsService = new VFSClientImpl();
            }

            vfsService.setSystemCode(systemCode);
            vfsService.setClientService(client);
            vfsService.connect(client.getConnectInfo());
            vfsClients.put(systemCode, vfsService);

            return vfsService;
        }
    }

}
