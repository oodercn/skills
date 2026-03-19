package net.ooder.skill.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 閫氱敤杩斿洖缁撴灉绫? * 涓存椂鏇夸唬agent-sdk鐨凴esult绫伙紝鐩村埌SDK鍙戝竷
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 鏄惁鎴愬姛
     */
    private boolean success;

    /**
     * 杩斿洖鏁版嵁
     */
    private T data;

    /**
     * 閿欒鐮?     */
    private Integer code;

    /**
     * 閿欒娑堟伅
     */
    private String message;

    public Result() {}

    public Result(boolean success, T data, Integer code, String message) {
        this.success = success;
        this.data = data;
        this.code = code;
        this.message = message;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(true, data, 200, "success");
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(false, null, 500, message);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(false, null, code, message);
    }
}
