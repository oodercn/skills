package net.ooder.vfs.engine;

import com.alibaba.fastjson.JSON;
import net.ooder.common.logging.Log;
import net.ooder.common.logging.LogFactory;
import net.ooder.vfs.VFSConstants;

import java.util.HashMap;
import java.util.Map;

public class VFSEngine {
    private Log log = LogFactory.getLog(VFSConstants.CONFIG_KEY, VFSEngine.class);

    public VFSRoManager vfsManager;

    public String systemCode;

    private static String SUCCESS = "1";
    private static String FAILED = "-1";

    private static Map<String, VFSEngine> engineMap = new HashMap<String, VFSEngine>();

    public static VFSEngine getEngine(String systemCode) {
        VFSEngine engine = engineMap.get(systemCode);
        if (engine == null) {
            synchronized (VFSEngine.class) {
                engine = new VFSEngine(systemCode);
                engineMap.put(systemCode, engine);
            }
        }
        return engine;
    }

    protected VFSEngine(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getSystemCode() {
        return systemCode;
    }
}
