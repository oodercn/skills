package net.ooder.spi.facade;

import net.ooder.spi.database.DataSourceProvider;
import net.ooder.spi.document.DocumentParser;
import net.ooder.spi.im.ImService;
import net.ooder.spi.rag.RagEnhanceDriver;
import net.ooder.spi.vector.VectorStoreProvider;
import net.ooder.spi.workflow.WorkflowDriver;
import java.util.List;
import java.util.Optional;

public class SpiServices {

    private static SpiServices instance;

    private ImService imService;
    private RagEnhanceDriver ragEnhanceDriver;
    private WorkflowDriver workflowDriver;
    private DataSourceProvider dataSourceProvider;
    private VectorStoreProvider vectorStoreProvider;
    private List<DocumentParser> documentParsers;

    public static synchronized void init(SpiServices services) {
        instance = services;
    }

    public static SpiServices getInstance() {
        return instance;
    }

    public static ImService getImService() {
        return instance != null ? instance.imService : null;
    }

    public static RagEnhanceDriver getRagEnhanceDriver() {
        return instance != null ? instance.ragEnhanceDriver : null;
    }

    public static WorkflowDriver getWorkflowDriver() {
        return instance != null ? instance.workflowDriver : null;
    }
    
    public static DataSourceProvider getDataSourceProvider() {
        return instance != null ? instance.dataSourceProvider : null;
    }
    
    public static VectorStoreProvider getVectorStoreProvider() {
        return instance != null ? instance.vectorStoreProvider : null;
    }
    
    public static List<DocumentParser> getDocumentParsers() {
        return instance != null ? instance.documentParsers : null;
    }
    
    public static DocumentParser getDocumentParser(String mimeType) {
        if (instance == null || instance.documentParsers == null) {
            return null;
        }
        return instance.documentParsers.stream()
            .filter(p -> p.supports(mimeType))
            .findFirst()
            .orElse(null);
    }

    public static Optional<ImService> im() { return Optional.ofNullable(getImService()); }
    public static Optional<RagEnhanceDriver> rag() { return Optional.ofNullable(getRagEnhanceDriver()); }
    public static Optional<WorkflowDriver> workflow() { return Optional.ofNullable(getWorkflowDriver()); }
    public static Optional<DataSourceProvider> dataSource() { return Optional.ofNullable(getDataSourceProvider()); }
    public static Optional<VectorStoreProvider> vectorStore() { return Optional.ofNullable(getVectorStoreProvider()); }
    public static Optional<DocumentParser> documentParser(String mimeType) { return Optional.ofNullable(getDocumentParser(mimeType)); }

    public void setImService(ImService imService) { this.imService = imService; }
    public void setRagEnhanceDriver(RagEnhanceDriver ragEnhanceDriver) { this.ragEnhanceDriver = ragEnhanceDriver; }
    public void setWorkflowDriver(WorkflowDriver workflowDriver) { this.workflowDriver = workflowDriver; }
    public void setDataSourceProvider(DataSourceProvider dataSourceProvider) { this.dataSourceProvider = dataSourceProvider; }
    public void setVectorStoreProvider(VectorStoreProvider vectorStoreProvider) { this.vectorStoreProvider = vectorStoreProvider; }
    public void setDocumentParsers(List<DocumentParser> documentParsers) { this.documentParsers = documentParsers; }
}
