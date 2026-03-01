package net.ooder.skill.security.service.impl;

import net.ooder.skill.security.service.EncryptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class EncryptionServiceImpl implements EncryptionService {
    
    private static final Logger log = LoggerFactory.getLogger(EncryptionServiceImpl.class);
    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 256;
    
    private SecureRandom secureRandom;
    private String masterKey;
    
    @PostConstruct
    public void init() {
        this.secureRandom = new SecureRandom();
        this.masterKey = System.getenv("OODER_MASTER_KEY");
        if (this.masterKey == null || this.masterKey.isEmpty()) {
            this.masterKey = "default-master-key-for-development";
            log.warn("OODER_MASTER_KEY not set, using default key for development");
        }
        log.info("EncryptionService initialized");
    }
    
    @Override
    public String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        
        try {
            byte[] iv = new byte[16];
            secureRandom.nextBytes(iv);
            
            byte[] keyBytes = deriveKey(masterKey);
            
            byte[] plainBytes = plainText.getBytes(StandardCharsets.UTF_8);
            byte[] encrypted = xorEncrypt(plainBytes, keyBytes, iv);
            
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);
            
            return Base64.getEncoder().encodeToString(combined);
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
            byte[] combined = Base64.getDecoder().decode(cipherText);
            
            byte[] iv = new byte[16];
            byte[] encrypted = new byte[combined.length - 16];
            System.arraycopy(combined, 0, iv, 0, 16);
            System.arraycopy(combined, 16, encrypted, 0, encrypted.length);
            
            byte[] keyBytes = deriveKey(masterKey);
            
            byte[] decrypted = xorEncrypt(encrypted, keyBytes, iv);
            
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Decryption failed", e);
            throw new RuntimeException("Decryption failed", e);
        }
    }
    
    @Override
    public String generateKey() {
        byte[] key = new byte[32];
        secureRandom.nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
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
    
    private byte[] deriveKey(String masterKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(masterKey.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Key derivation failed", e);
        }
    }
    
    private byte[] xorEncrypt(byte[] data, byte[] key, byte[] iv) {
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) (data[i] ^ key[i % key.length] ^ iv[i % iv.length]);
        }
        return result;
    }
}
