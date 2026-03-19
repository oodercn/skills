package net.ooder.skill.network.p2p;

public interface P2PEventListener {
    
    void onEvent(P2PEventType eventType, Object data);
}
