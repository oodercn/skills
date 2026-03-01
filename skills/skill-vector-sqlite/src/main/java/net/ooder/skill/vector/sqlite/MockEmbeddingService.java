package net.ooder.skill.vector.sqlite;

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
        log.info("MockEmbeddingService initialized with dimension: {}", dimension);
    }
    
    @Override
    public double[] embed(String text) {
        double[] embedding = new double[dimension];
        
        int hash = text.hashCode();
        random.setSeed(hash);
        
        for (int i = 0; i < dimension; i++) {
            embedding[i] = random.nextGaussian();
        }
        
        normalize(embedding);
        
        return embedding;
    }
    
    @Override
    public List<double[]> embedBatch(List<String> texts) {
        List<double[]> embeddings = new ArrayList<>();
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
    public String getModelName() {
        return "mock-embedding";
    }
    
    private void normalize(double[] vector) {
        double norm = 0.0;
        for (double v : vector) {
            norm += v * v;
        }
        norm = Math.sqrt(norm);
        
        if (norm > 0) {
            for (int i = 0; i < vector.length; i++) {
                vector[i] /= norm;
            }
        }
    }
}
