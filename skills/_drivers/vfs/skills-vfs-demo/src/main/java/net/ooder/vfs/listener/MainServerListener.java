package net.ooder.vfs.listener;

import net.ooder.common.JDSConstants;
import net.ooder.common.JDSException;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.config.JDSConfig.Config;
import net.ooder.engine.event.EIServerAdapter;
import net.ooder.engine.event.EIServerEvent;
import net.ooder.esb.config.manager.EsbBeanFactory;
import net.ooder.esb.config.manager.ExpressionTempBean;
import net.ooder.esb.config.manager.ServiceBean;
import net.ooder.vfs.engine.VFSServer;
import net.ooder.vfs.sync.SyncFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainServerListener extends EIServerAdapter {

    private static final Log logger = LogFactory.getLog(JDSConstants.CONFIG_KEY, MainServerListener.class);

    @Override
    public void serverStopped(EIServerEvent event) throws JDSException {
        VFSServer.getInstance();
        try {

            try {
                SyncFactory.getInstance().push(Paths.get(Config.applicationHome().getAbsolutePath()),"root/JDSHome/");
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void serverStarted(EIServerEvent event) throws JDSException {

        logger.info("serverStarted .... loadStaticAllData");

        List<? extends ServiceBean> list = EsbBeanFactory.getInstance().loadAllServiceBean();
        for (int k = 0; k < list.size(); k++) {
            if (!(list.get(k) instanceof ExpressionTempBean)) {
                continue;
            }
            ExpressionTempBean bean = (ExpressionTempBean) list.get(k);
            logger.info(bean.getName() + bean.getExpressionArr());
        }
        logger.info("end .... loadStaticAllData");


    }
}
