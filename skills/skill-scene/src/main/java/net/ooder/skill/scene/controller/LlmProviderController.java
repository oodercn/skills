package net.ooder.skill.scene.controller;

import net.ooder.skill.scene.dto.llm.LlmProviderConfigDTO;
import net.ooder.skill.scene.model.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/llm")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LlmProviderController {

    private static final Logger log = LoggerFactory.getLogger(LlmProviderController.class);
    
    private Map<String, LlmProviderConfigDTO> providers = new HashMap<String, LlmProviderConfigDTO>();
    
    public LlmProviderController() {
        initMockData();
    }
    
    private void initMockData() {
        LlmProviderConfigDTO deepseek = new LlmProviderConfigDTO();
        deepseek.setProviderId("deepseek");
        deepseek.setName("DeepSeek");
        deepseek.setType("deepseek");
        deepseek.setEnabled(true);
        deepseek.setDefaultModel("deepseek-chat");
        deepseek.setTimeout(60000);
        deepseek.setMaxRetries(3);
        deepseek.setFunctionCallingEnabled(true);
        deepseek.setMaxIterations(5);
        deepseek.setCreateTime(System.currentTimeMillis() - 86400000L * 30);
        deepseek.setUpdateTime(System.currentTimeMillis() - 86400000L);
        
        List<LlmProviderConfigDTO.ModelConfigDTO> deepseekModels = new ArrayList<LlmProviderConfigDTO.ModelConfigDTO>();
        LlmProviderConfigDTO.ModelConfigDTO dsChat = new LlmProviderConfigDTO.ModelConfigDTO();
        dsChat.setModelId("deepseek-chat");
        dsChat.setDisplayName("DeepSeek Chat");
        dsChat.setMaxTokens(64000);
        dsChat.setTemperature(0.7);
        dsChat.setSupportsFunctionCalling(true);
        dsChat.setSupportsMultimodal(false);
        dsChat.setSupportsEmbedding(false);
        dsChat.setCostPer1kTokens(0.001);
        deepseekModels.add(dsChat);
        
        LlmProviderConfigDTO.ModelConfigDTO dsCoder = new LlmProviderConfigDTO.ModelConfigDTO();
        dsCoder.setModelId("deepseek-coder");
        dsCoder.setDisplayName("DeepSeek Coder");
        dsCoder.setMaxTokens(16000);
        dsCoder.setTemperature(0.3);
        dsCoder.setSupportsFunctionCalling(true);
        dsCoder.setSupportsMultimodal(false);
        dsCoder.setSupportsEmbedding(false);
        dsCoder.setCostPer1kTokens(0.001);
        deepseekModels.add(dsCoder);
        
        deepseek.setModels(deepseekModels);
        providers.put(deepseek.getProviderId(), deepseek);
        
        LlmProviderConfigDTO baidu = new LlmProviderConfigDTO();
        baidu.setProviderId("baidu");
        baidu.setName("百度文心");
        baidu.setType("baidu");
        baidu.setEnabled(true);
        baidu.setDefaultModel("ernie-bot-4");
        baidu.setTimeout(60000);
        baidu.setMaxRetries(3);
        baidu.setFunctionCallingEnabled(true);
        baidu.setMaxIterations(5);
        baidu.setCreateTime(System.currentTimeMillis() - 86400000L * 20);
        baidu.setUpdateTime(System.currentTimeMillis() - 86400000L * 2);
        
        List<LlmProviderConfigDTO.ModelConfigDTO> baiduModels = new ArrayList<LlmProviderConfigDTO.ModelConfigDTO>();
        LlmProviderConfigDTO.ModelConfigDTO ernie4 = new LlmProviderConfigDTO.ModelConfigDTO();
        ernie4.setModelId("ernie-bot-4");
        ernie4.setDisplayName("ERNIE Bot 4.0");
        ernie4.setMaxTokens(8000);
        ernie4.setTemperature(0.7);
        ernie4.setSupportsFunctionCalling(true);
        ernie4.setSupportsMultimodal(false);
        ernie4.setSupportsEmbedding(false);
        ernie4.setCostPer1kTokens(0.12);
        baiduModels.add(ernie4);
        
        LlmProviderConfigDTO.ModelConfigDTO ernieTurbo = new LlmProviderConfigDTO.ModelConfigDTO();
        ernieTurbo.setModelId("ernie-bot-turbo");
        ernieTurbo.setDisplayName("ERNIE Bot Turbo");
        ernieTurbo.setMaxTokens(4000);
        ernieTurbo.setTemperature(0.7);
        ernieTurbo.setSupportsFunctionCalling(false);
        ernieTurbo.setSupportsMultimodal(false);
        ernieTurbo.setSupportsEmbedding(false);
        ernieTurbo.setCostPer1kTokens(0.008);
        baiduModels.add(ernieTurbo);
        
        baidu.setModels(baiduModels);
        providers.put(baidu.getProviderId(), baidu);
        
        LlmProviderConfigDTO openai = new LlmProviderConfigDTO();
        openai.setProviderId("openai");
        openai.setName("OpenAI");
        openai.setType("openai");
        openai.setEnabled(false);
        openai.setDefaultModel("gpt-4-turbo");
        openai.setTimeout(60000);
        openai.setMaxRetries(3);
        openai.setFunctionCallingEnabled(true);
        openai.setMaxIterations(5);
        openai.setCreateTime(System.currentTimeMillis() - 86400000L * 10);
        openai.setUpdateTime(System.currentTimeMillis() - 86400000L * 5);
        
        List<LlmProviderConfigDTO.ModelConfigDTO> openaiModels = new ArrayList<LlmProviderConfigDTO.ModelConfigDTO>();
        LlmProviderConfigDTO.ModelConfigDTO gpt4 = new LlmProviderConfigDTO.ModelConfigDTO();
        gpt4.setModelId("gpt-4-turbo");
        gpt4.setDisplayName("GPT-4 Turbo");
        gpt4.setMaxTokens(128000);
        gpt4.setTemperature(0.7);
        gpt4.setSupportsFunctionCalling(true);
        gpt4.setSupportsMultimodal(true);
        gpt4.setSupportsEmbedding(false);
        gpt4.setCostPer1kTokens(0.01);
        openaiModels.add(gpt4);
        
        LlmProviderConfigDTO.ModelConfigDTO gpt35 = new LlmProviderConfigDTO.ModelConfigDTO();
        gpt35.setModelId("gpt-3.5-turbo");
        gpt35.setDisplayName("GPT-3.5 Turbo");
        gpt35.setMaxTokens(16000);
        gpt35.setTemperature(0.7);
        gpt35.setSupportsFunctionCalling(true);
        gpt35.setSupportsMultimodal(false);
        gpt35.setSupportsEmbedding(false);
        gpt35.setCostPer1kTokens(0.0005);
        openaiModels.add(gpt35);
        
        openai.setModels(openaiModels);
        providers.put(openai.getProviderId(), openai);
    }

    @GetMapping("/providers")
    public ResultModel<List<LlmProviderConfigDTO>> listProviders() {
        log.info("[listProviders] request start");
        return ResultModel.success(new ArrayList<LlmProviderConfigDTO>(providers.values()));
    }
    
    @GetMapping("/providers/{providerId}")
    public ResultModel<LlmProviderConfigDTO> getProvider(@PathVariable String providerId) {
        log.info("[getProvider] providerId: {}", providerId);
        LlmProviderConfigDTO provider = providers.get(providerId);
        if (provider == null) {
            return ResultModel.notFound("Provider not found: " + providerId);
        }
        return ResultModel.success(provider);
    }
    
    @PostMapping("/providers")
    public ResultModel<LlmProviderConfigDTO> createProvider(@RequestBody LlmProviderConfigDTO request) {
        log.info("[createProvider] name: {}", request.getName());
        
        String providerId = "provider-" + System.currentTimeMillis();
        request.setProviderId(providerId);
        request.setCreateTime(System.currentTimeMillis());
        request.setUpdateTime(System.currentTimeMillis());
        
        providers.put(providerId, request);
        
        return ResultModel.success(request);
    }
    
    @PutMapping("/providers/{providerId}")
    public ResultModel<LlmProviderConfigDTO> updateProvider(@PathVariable String providerId, @RequestBody LlmProviderConfigDTO request) {
        log.info("[updateProvider] providerId: {}", providerId);
        
        LlmProviderConfigDTO existing = providers.get(providerId);
        if (existing == null) {
            return ResultModel.notFound("Provider not found: " + providerId);
        }
        
        request.setProviderId(providerId);
        request.setUpdateTime(System.currentTimeMillis());
        request.setCreateTime(existing.getCreateTime());
        providers.put(providerId, request);
        
        return ResultModel.success(request);
    }
    
    @DeleteMapping("/providers/{providerId}")
    public ResultModel<Boolean> deleteProvider(@PathVariable String providerId) {
        log.info("[deleteProvider] providerId: {}", providerId);
        
        LlmProviderConfigDTO removed = providers.remove(providerId);
        if (removed == null) {
            return ResultModel.notFound("Provider not found: " + providerId);
        }
        
        return ResultModel.success(true);
    }
    
    @PostMapping("/providers/{providerId}/test")
    public ResultModel<Boolean> testProvider(@PathVariable String providerId) {
        log.info("[testProvider] providerId: {}", providerId);
        
        LlmProviderConfigDTO provider = providers.get(providerId);
        if (provider == null) {
            return ResultModel.notFound("Provider not found: " + providerId);
        }
        
        return ResultModel.success(true);
    }
    
    @GetMapping("/providers/{providerId}/models")
    public ResultModel<List<LlmProviderConfigDTO.ModelConfigDTO>> listModels(@PathVariable String providerId) {
        log.info("[listModels] providerId: {}", providerId);
        
        LlmProviderConfigDTO provider = providers.get(providerId);
        if (provider == null) {
            return ResultModel.notFound("Provider not found: " + providerId);
        }
        
        return ResultModel.success(provider.getModels());
    }
    
    @GetMapping("/default")
    public ResultModel<LlmProviderConfigDTO> getDefaultProvider() {
        log.info("[getDefaultProvider] request start");
        
        for (LlmProviderConfigDTO provider : providers.values()) {
            if (provider.isEnabled()) {
                return ResultModel.success(provider);
            }
        }
        
        return ResultModel.notFound("No default provider configured");
    }
}
