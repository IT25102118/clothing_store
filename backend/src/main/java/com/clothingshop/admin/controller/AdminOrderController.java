package com.clothingshop.admin.controller;

import com.clothingshop.admin.dto.UpdateOrderStatusRequest;
import com.clothingshop.admin.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminService adminService;

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable Long id,
                                                  @Valid @RequestBody UpdateOrderStatusRequest request) {
        adminService.updateOrderStatus(id, request);
        return ResponseEntity.ok().build();
    }
}
