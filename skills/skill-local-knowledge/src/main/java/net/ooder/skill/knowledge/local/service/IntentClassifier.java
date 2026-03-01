package net.ooder.skill.knowledge.local.service;

import net.ooder.skill.knowledge.local.model.IntentClassification;

import java.util.Map;

public interface IntentClassifier {
    
    IntentClassification classify(String text);
    
    IntentClassification classifyWithContext(String text, Map<String, Object> context);
}
