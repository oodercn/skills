package net.ooder.vfs.store;

import net.ooder.esb.config.manager.EsbBeanFactory;
import net.ooder.jds.core.esb.EsbUtil;
import net.ooder.vfs.store.manager.FileObjectManager;

public class VFStoreFactory {
    public static final String THREAD_LOCK = "Thread Lock";

    static VFStoreFactory instance;
    private final FileObjectManager fileObjectManager;


    public static VFStoreFactory getInstance() {
        if (instance == null) {
            synchronized (THREAD_LOCK) {
                if (instance == null) {
                    instance = new VFStoreFactory();
                }
            }
        }
        return instance;
    }

    VFStoreFactory() {
        this.fileObjectManager = EsbUtil.parExpression(FileObjectManager.class);
        fileObjectManager.loadAll(1000);
    }


}
