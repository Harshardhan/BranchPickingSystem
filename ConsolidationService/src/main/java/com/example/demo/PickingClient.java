package com.example.demo;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "picking-service") // This must match the service name registered with Eureka
public interface PickingClient {
	
    @PostMapping("/api/pickings")
    PickingClient createPicking(@RequestBody PickingClient request);

    @GetMapping("/api/pickings/{orderId}")
    PickingClient getPickingByOrderId(@PathVariable Long orderId);


}
