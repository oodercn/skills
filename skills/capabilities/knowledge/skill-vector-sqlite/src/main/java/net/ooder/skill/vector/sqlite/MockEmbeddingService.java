package net.ooder.skill.vector.sqlite;

import net.ooder.scene.skill.vector.EmbeddingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class MockEmbeddingService implements EmbeddingService {
    
    private static final Logger log = LoggerFactory.getLogger(MockEmbeddingService.class);
    
    @Value("${vector.sqlite.embeddingDimension:1536}")
    private int dimension;
    
    private Random random;
    
    @PostConstruct
    public void init() {
        this.random = new Random(42);
        log.info("MockEmbeddingService initialized with dimension: {}, using SDK EmbeddingService interface", dimension);
    }
    
    @Override
    public float[] embed(String text) {
        float[] embedding = new float[dimension];
        
        int hash = text.hashCode();
        random.setSeed(hash);
        
        for (int i = 0; i < dimension; i++) {
            embedding[i] = (float) random.nextGaussian();
        }
        
        normalize(embedding);
        
        return embedding;
    }
    
    @Override
    public List<float[]> embedBatch(List<String> texts) {
        List<float[]> embeddings = new ArrayList<>();
        for (String text : texts) {
            embeddings.add(embed(text));
        }
        return embeddings;
    }
    
    @Override
    public int getDimension() {
        return dimension;
    }
    
    @Override
    public String getModel() {
        return "mock-embedding";
    }
    
    private void normalize(float[] vector) {
        float norm = 0.0f;
        for (float v : vector) {
            norm += v * v;
        }
        norm = (float) Math.sqrt(norm);
        
        if (norm > 0) {
            for (int i = 0; i < vector.length; i++) {
                vector[i] /= norm;
            }
        }
    }
}
