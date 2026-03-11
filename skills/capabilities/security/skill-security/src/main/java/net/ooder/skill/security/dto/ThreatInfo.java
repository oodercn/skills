package net.ooder.skill.security.dto;

import lombok.Data;

/**
 * Threat Information DTO
 * 
 * <p>Represents a security threat detected in the system.</p>
 * 
 * <h3>Threat Types:</h3>
 * <ul>
 *   <li>{@code malware} - Malware detection</li>
 *   <li>{@code intrusion} - Intrusion attempt</li>
 *   <li>{@code ddos} - DDoS attack</li>
 *   <li>{@code phishing} - Phishing attempt</li>
 *   <li>{@code vulnerability} - Vulnerability detected</li>
 * </ul>
 * 
 * <h3>Severity Levels:</h3>
 * <ul>
 *   <li>{@code low} - Low risk threat</li>
 *   <li>{@code medium} - Medium risk threat</li>
 *   <li>{@code high} - High risk threat</li>
 *   <li>{@code critical} - Critical threat requiring immediate action</li>
 * </ul>
 * 
 * @author Ooder Team
 * @version 2.3
 * @since 2026-02-27
 */
@Data
public class ThreatInfo {

    /** Unique threat identifier */
    private String threatId;

    /** Threat type: malware, intrusion, ddos, phishing, vulnerability */
    private String type;

    /** Severity level: low, medium, high, critical */
    private String severity;

    /** Source of the threat (IP, domain, user, etc.) */
    private String source;

    /** Target of the threat */
    private String target;

    /** Threat status: active, investigating, resolved, false_positive */
    private String status;

    /** Detailed description of the threat */
    private String description;

    /** Resolution description (if resolved) */
    private String resolution;

    /** Timestamp when threat was detected */
    private long detectedAt;

    /** Timestamp when threat was resolved */
    private long resolvedAt;

    /** Additional metadata */
    private java.util.Map<String, Object> metadata;
}
