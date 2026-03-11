package net.ooder.skill.security.service.impl;

import net.ooder.skill.security.service.EncryptionService;
import net.ooder.sdk.service.security.crypto.KeyManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class EncryptionServiceImpl implements EncryptionService {
    
    private static final Logger log = LoggerFactory.getLogger(EncryptionServiceImpl.class);
    private static final String DEFAULT_KEY_ID = "skill-security-default";
    
    private KeyManager keyManager;
    private String masterKeyId;
    
    @PostConstruct
    public void init() {
        this.keyManager = new KeyManager();
        
        String masterKey = System.getenv("OODER_MASTER_KEY");
        if (masterKey != null && !masterKey.isEmpty()) {
            keyManager.importKey(DEFAULT_KEY_ID, masterKey);
            this.masterKeyId = DEFAULT_KEY_ID;
            log.info("EncryptionService initialized with custom master key");
        } else {
            this.masterKeyId = DEFAULT_KEY_ID;
            keyManager.generateKey(DEFAULT_KEY_ID);
            log.warn("OODER_MASTER_KEY not set, generated new key for development");
        }
    }
    
    @Override
    public String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        
        try {
            byte[] plainBytes = plainText.getBytes(StandardCharsets.UTF_8);
            byte[] encrypted = keyManager.encrypt(masterKeyId, plainBytes);
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.error("Encryption failed", e);
            throw new RuntimeException("Encryption failed", e);
        }
    }
    
    @Override
    public String decrypt(String cipherText) {
        if (cipherText == null || cipherText.isEmpty()) {
            return cipherText;
        }
        
        try {
            byte[] cipherBytes = Base64.getDecoder().decode(cipherText);
            byte[] decrypted = keyManager.decrypt(masterKeyId, cipherBytes);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Decryption failed", e);
            throw new RuntimeException("Decryption failed", e);
        }
    }
    
    @Override
    public String generateKey() {
        String newKeyId = "key-" + System.currentTimeMillis();
        return keyManager.generateKey(newKeyId);
    }
    
    @Override
    public String hash(String plainText) {
        if (plainText == null) {
            return null;
        }
        
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("Hashing failed", e);
            throw new RuntimeException("Hashing failed", e);
        }
    }
    
    @Override
    public boolean verifyHash(String plainText, String hash) {
        if (plainText == null || hash == null) {
            return false;
        }
        return hash.equals(hash(plainText));
    }
    
    public void importKey(String keyId, String keyData) {
        keyManager.importKey(keyId, keyData);
        log.info("Imported key: {}", keyId);
    }
    
    public String exportKey(String keyId) {
        return keyManager.exportKey(keyId);
    }
    
    public void deleteKey(String keyId) {
        keyManager.deleteKey(keyId);
        log.info("Deleted key: {}", keyId);
    }
    
    public boolean hasKey(String keyId) {
        return keyManager.hasKey(keyId);
    }
}
