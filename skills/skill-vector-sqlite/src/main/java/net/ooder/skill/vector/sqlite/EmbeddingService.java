package net.ooder.skill.vector.sqlite;

import java.util.List;

public interface EmbeddingService {
    
    double[] embed(String text);
    
    List<double[]> embedBatch(List<String> texts);
    
    int getDimension();
    
    String getModelName();
}
