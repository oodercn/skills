package net.ooder.skill.scene.network.service;

import net.ooder.skill.scene.network.dto.LinkDTO;
import net.ooder.skill.scene.network.dto.NetworkTopologyDTO;

import java.util.List;

public interface NetworkService {
    
    NetworkTopologyDTO getTopology();
    
    List<LinkDTO> listLinks();
    
    LinkDTO getLink(String linkId);
    
    List<LinkDTO> searchLinks(String keyword);
    
    boolean reconnectLink(String linkId);
    
    boolean disconnectLink(String linkId);
    
    int getBindingCount(String linkId);
}
