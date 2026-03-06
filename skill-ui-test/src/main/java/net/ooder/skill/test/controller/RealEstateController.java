package net.ooder.skill.test.controller;

import net.ooder.skill.test.dto.PropertyDTO;
import net.ooder.skill.test.dto.ValuationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/real-estate")
public class RealEstateController {
    
    private static final Logger log = LoggerFactory.getLogger(RealEstateController.class);
    
    private final Map<String, Map<String, Object>> propertyStore = new ConcurrentHashMap<>();
    
    public RealEstateController() {
        initMockData();
    }
    
    private void initMockData() {
        createProperty("prop-001", "精装修三室两厅 南北通透 近地铁", "阳光花园", "朝阳区", "3室2厅", 125, 580, "available");
        createProperty("prop-002", "学区房 两室一厅 精装修", "学府雅苑", "海淀区", "2室1厅", 89, 420, "reserved");
        createProperty("prop-003", "豪华装修四室 南向采光好", "望京新城", "朝阳区", "4室2厅", 168, 980, "available");
    }
    
    private void createProperty(String id, String title, String community, String district, String roomType, double area, double price, String status) {
        Map<String, Object> property = new HashMap<>();
        property.put("id", id);
        property.put("title", title);
        property.put("community", community);
        property.put("district", district);
        property.put("roomType", roomType);
        property.put("area", area);
        property.put("price", price);
        property.put("status", status);
        property.put("createdAt", System.currentTimeMillis() - (long)(Math.random() * 86400000 * 7));
        property.put("updatedAt", System.currentTimeMillis());
        propertyStore.put(id, property);
    }
    
    @GetMapping("/properties")
    public ResponseEntity<Map<String, Object>> getProperties(
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String roomType,
            @RequestParam(required = false) String status) {
        
        log.info("[getProperties] district: {}, roomType: {}, status: {}", district, roomType, status);
        
        List<Map<String, Object>> properties = new ArrayList<>(propertyStore.values());
        
        if (district != null && !district.isEmpty()) {
            properties = properties.stream()
                .filter(p -> district.equals(p.get("district")))
                .collect(Collectors.toList());
        }
        
        if (roomType != null && !roomType.isEmpty()) {
            properties = properties.stream()
                .filter(p -> p.get("roomType") != null && p.get("roomType").toString().contains(roomType))
                .collect(Collectors.toList());
        }
        
        if (status != null && !status.isEmpty()) {
            properties = properties.stream()
                .filter(p -> status.equals(p.get("status")))
                .collect(Collectors.toList());
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("properties", properties);
        data.put("total", properties.size());
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", data);
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/properties/{id}")
    public ResponseEntity<Map<String, Object>> getProperty(@PathVariable String id) {
        log.info("[getProperty] id: {}", id);
        
        Map<String, Object> property = propertyStore.get(id);
        
        Map<String, Object> result = new HashMap<>();
        if (property != null) {
            result.put("status", "success");
            result.put("data", property);
        } else {
            result.put("status", "error");
            result.put("message", "Property not found");
        }
        
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/properties")
    public ResponseEntity<Map<String, Object>> createProperty(@RequestBody PropertyDTO property) {
        log.info("[createProperty] property: {}", property);
        
        String id = "prop-" + System.currentTimeMillis();
        property.setId(id);
        
        Map<String, Object> propertyMap = convertToMap(property);
        propertyMap.put("createdAt", System.currentTimeMillis());
        propertyMap.put("updatedAt", System.currentTimeMillis());
        
        if (property.getStatus() == null) {
            propertyMap.put("status", "available");
        }
        
        propertyStore.put(id, propertyMap);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", propertyMap);
        
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/properties/{id}")
    public ResponseEntity<Map<String, Object>> updateProperty(@PathVariable String id, @RequestBody PropertyDTO property) {
        log.info("[updateProperty] id: {}, property: {}", id, property);
        
        Map<String, Object> existing = propertyStore.get(id);
        
        if (existing == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("status", "error");
            result.put("message", "Property not found");
            return ResponseEntity.ok(result);
        }
        
        property.setId(id);
        Map<String, Object> propertyMap = convertToMap(property);
        propertyMap.put("createdAt", existing.get("createdAt"));
        propertyMap.put("updatedAt", System.currentTimeMillis());
        
        propertyStore.put(id, propertyMap);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", propertyMap);
        
        return ResponseEntity.ok(result);
    }
    
    private Map<String, Object> convertToMap(PropertyDTO dto) {
        Map<String, Object> map = new HashMap<>();
        if (dto.getId() != null) map.put("id", dto.getId());
        if (dto.getTitle() != null) map.put("title", dto.getTitle());
        if (dto.getDistrict() != null) map.put("district", dto.getDistrict());
        if (dto.getCommunity() != null) map.put("community", dto.getCommunity());
        if (dto.getRoomType() != null) map.put("roomType", dto.getRoomType());
        if (dto.getArea() != null) map.put("area", dto.getArea());
        if (dto.getInnerArea() != null) map.put("innerArea", dto.getInnerArea());
        if (dto.getFloor() != null) map.put("floor", dto.getFloor());
        if (dto.getTotalFloor() != null) map.put("totalFloor", dto.getTotalFloor());
        if (dto.getOrientation() != null) map.put("orientation", dto.getOrientation());
        if (dto.getDecoration() != null) map.put("decoration", dto.getDecoration());
        if (dto.getBuildYear() != null) map.put("buildYear", dto.getBuildYear());
        if (dto.getPropertyType() != null) map.put("propertyType", dto.getPropertyType());
        if (dto.getPrice() != null) map.put("price", dto.getPrice());
        if (dto.getAddress() != null) map.put("address", dto.getAddress());
        if (dto.getDescription() != null) map.put("description", dto.getDescription());
        if (dto.getOwnerName() != null) map.put("ownerName", dto.getOwnerName());
        if (dto.getOwnerPhone() != null) map.put("ownerPhone", dto.getOwnerPhone());
        if (dto.getStatus() != null) map.put("status", dto.getStatus());
        if (dto.getImages() != null) map.put("images", dto.getImages());
        return map;
    }
    
    @DeleteMapping("/properties/{id}")
    public ResponseEntity<Map<String, Object>> deleteProperty(@PathVariable String id) {
        log.info("[deleteProperty] id: {}", id);
        
        Map<String, Object> removed = propertyStore.remove(id);
        
        Map<String, Object> result = new HashMap<>();
        if (removed != null) {
            result.put("status", "success");
            result.put("message", "Property deleted");
        } else {
            result.put("status", "error");
            result.put("message", "Property not found");
        }
        
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/valuation")
    public ResponseEntity<Map<String, Object>> getValuation(@RequestBody ValuationRequest request) {
        log.info("[getValuation] request: {}", request);
        
        double area = request.getArea() != null ? request.getArea() : 100;
        String district = request.getDistrict();
        
        double basePrice = 40000;
        
        if (district != null) {
            switch (district) {
                case "朝阳区":
                case "海淀区":
                    basePrice = 65000;
                    break;
                case "东城区":
                case "西城区":
                    basePrice = 80000;
                    break;
                case "丰台区":
                case "石景山区":
                    basePrice = 50000;
                    break;
                case "通州区":
                case "大兴区":
                    basePrice = 35000;
                    break;
                default:
                    basePrice = 30000;
            }
        }
        
        double variation = 0.9 + Math.random() * 0.2;
        double valuation = Math.round(area * basePrice * variation / 10000);
        
        Map<String, Object> valuationData = new HashMap<>();
        valuationData.put("valuation", valuation);
        valuationData.put("unitPrice", Math.round(basePrice * variation));
        valuationData.put("confidence", 0.85 + Math.random() * 0.1);
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", valuationData);
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/districts")
    public ResponseEntity<Map<String, Object>> getDistricts() {
        log.info("[getDistricts]");
        
        List<String> districts = Arrays.asList(
            "朝阳区", "海淀区", "东城区", "西城区", 
            "丰台区", "石景山区", "通州区", "顺义区", 
            "大兴区", "昌平区"
        );
        
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("data", districts);
        
        return ResponseEntity.ok(result);
    }
}
