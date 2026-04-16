package net.ooder.skill.share.service;

import net.ooder.skill.share.model.ShareRecord;
import net.ooder.skill.share.model.DelegateRecord;
import net.ooder.skill.share.dto.ShareCapabilityRequest;
import net.ooder.skill.share.dto.DelegateCapabilityRequest;

import java.util.List;

public interface CapabilityShareService {
    
    ShareRecord shareCapability(ShareCapabilityRequest request);
    
    DelegateRecord delegateCapability(DelegateCapabilityRequest request);
    
    List<ShareRecord> getSharedCapabilities(String userId);
    
    List<DelegateRecord> getDelegatedCapabilities(String userId);
    
    List<ShareRecord> getReceivedCapabilities(String userId);
    
    ShareRecord getShareDetail(String shareId);
    
    ShareRecord getShareByCode(String shareCode);
    
    boolean validateShare(String shareCode, String password);
    
    boolean acceptShare(String shareId, String userId);
    
    boolean rejectShare(String shareId, String userId, String reason);
    
    boolean cancelShare(String shareId, String userId);
    
    boolean cancelDelegate(String delegateId, String userId);
}
