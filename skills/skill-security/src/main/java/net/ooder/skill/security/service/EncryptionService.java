package net.ooder.skill.security.service;

public interface EncryptionService {
    
    String encrypt(String plainText);
    
    String decrypt(String cipherText);
    
    String generateKey();
    
    String hash(String plainText);
    
    boolean verifyHash(String plainText, String hash);
}
