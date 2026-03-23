package net.ooder.mvp.skill.scene.spi;

import net.ooder.mvp.skill.scene.spi.message.Message;
import net.ooder.mvp.skill.scene.spi.message.SceneNotification;
import net.ooder.mvp.skill.scene.spi.message.SendMessageResult;

import java.util.List;

public interface MessageService {
    
    SendMessageResult sendMessage(Message message);
    
    List<SendMessageResult> batchSendMessages(List<Message> messages);
    
    SendMessageResult sendSceneNotification(
        String sceneId, 
        List<String> userIds, 
        SceneNotification notification
    );
}
