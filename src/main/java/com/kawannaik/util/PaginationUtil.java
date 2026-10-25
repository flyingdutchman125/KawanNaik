package com.kawannaik.util;

import com.kawannaik.dto.response.ApiResponse;

import java.util.Collections;
import java.util.List;

/**
 * Utility untuk mempermudah pagination dan sorting pada data list respon API.
 */
public final class PaginationUtil {

    private PaginationUtil() {
    }

    /**
     * Memotong list sesuai parameter page dan size jika diberikan.
     * Jika page/size bernilai null, mengembalikan list penuh tanpa metadata pagination (backward compatible).
     */
    public static <T> ApiResponse<List<T>> paginate(List<T> fullList, Integer page, Integer size, String defaultMessage) {
        if (fullList == null) {
            fullList = Collections.emptyList();
        }

        if (page == null || size == null || size <= 0) {
            return ApiResponse.success(fullList, defaultMessage);
        }

        long totalElements = fullList.size();
        int fromIndex = Math.max(0, page * size);

        if (fromIndex >= totalElements) {
            return ApiResponse.success(Collections.emptyList(), defaultMessage, page, size, totalElements);
        }

        int toIndex = Math.min(fromIndex + size, fullList.size());
        List<T> pageList = fullList.subList(fromIndex, toIndex);

        return ApiResponse.success(pageList, defaultMessage, page, size, totalElements);
    }
}
