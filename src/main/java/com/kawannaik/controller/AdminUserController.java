package com.kawannaik.controller;

import com.kawannaik.dto.response.ApiResponse;
import com.kawannaik.dto.response.UserProfileResponse;
import com.kawannaik.service.UserService;
import com.kawannaik.util.PaginationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "1. Pengguna (Admin)", description = "Manajemen data seluruh pengguna oleh administrator")
@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Daftar semua pengguna (Admin)", description = "Mengambil daftar seluruh pengguna terdaftar dengan opsi pagination")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserProfileResponse>>> getAllUsers(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        List<UserProfileResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(PaginationUtil.paginate(users, page, size, "Daftar pengguna berhasil diambil"));
    }

    @Operation(summary = "Detail pengguna (Admin)", description = "Mengambil detail profil pengguna tertentu berdasarkan ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserById(@PathVariable Long id) {
        UserProfileResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user, "Data pengguna berhasil diambil"));
    }

    @Operation(summary = "Hapus pengguna (Admin)", description = "Menghapus akun pengguna dari sistem")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Pengguna berhasil dihapus"));
    }
}
