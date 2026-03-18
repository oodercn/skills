package net.ooder.skill.market.dto;

import lombok.Data;
import java.util.List;

@Data
public class SearchRequest {
    private String keyword;
    private String category;
    private List<String> tags;
    private Integer page;
    private Integer pageSize;
}
