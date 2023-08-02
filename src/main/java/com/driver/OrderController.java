package com.driver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/add-order")
    public ResponseEntity<String> addOrder(@RequestBody Order order) {
        // Add the order using the OrderService and return appropriate response
        orderService.addOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body("New order added successfully");
    }

    @PostMapping("/add-partner/{partnerId}")
    public ResponseEntity<String> addPartner(@PathVariable String partnerId) {
        // Add the delivery partner using the OrderService and return appropriate response
        orderService.addDeliveryPartner(partnerId);
        return ResponseEntity.status(HttpStatus.CREATED).body("New delivery partner added successfully");
    }

    @PutMapping("/add-order-partner-pair")
    public ResponseEntity<String> addOrderPartnerPair(@RequestParam String orderId, @RequestParam String partnerId) {
        // Assign the order to the delivery partner using the OrderService and return appropriate response
        orderService.assignOrderToPartner(orderId, partnerId);
        return ResponseEntity.status(HttpStatus.CREATED).body("New order-partner pair added successfully");
    }

    @GetMapping("/get-order-by-id/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable String orderId) {
        // Get the order by orderId using the OrderService and return appropriate response
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/get-partner-by-id/{partnerId}")
    public ResponseEntity<DeliveryPartner> getPartnerById(@PathVariable String partnerId) {
        // Get the delivery partner by partnerId using the OrderService and return appropriate response
        DeliveryPartner partner = orderService.getDeliveryPartnerById(partnerId);
        return ResponseEntity.ok(partner);
    }

    @GetMapping("/get-order-count-by-partner-id/{partnerId}")
    public ResponseEntity<Integer> getOrderCountByPartnerId(@PathVariable String partnerId) {
        // Get the order count for a delivery partner using the OrderService and return appropriate response
        int orderCount = orderService.getOrderCountByPartnerId(partnerId);
        return ResponseEntity.ok(orderCount);
    }

    @GetMapping("/get-orders-by-partner-id/{partnerId}")
    public ResponseEntity<List<Order>> getOrdersByPartnerId(@PathVariable String partnerId) {
        // Get the list of orders for a delivery partner using the OrderService and return appropriate response
        List<Order> orders = orderService.getOrdersByPartnerId(partnerId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/get-all-orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        // Get all orders using the OrderService and return appropriate response
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/get-count-of-unassigned-orders")
    public ResponseEntity<Integer> getCountOfUnassignedOrders() {
        // Get the count of unassigned orders using the OrderService and return appropriate response
        int unassignedOrderCount = orderService.getCountOfUnassignedOrders();
        return ResponseEntity.ok(unassignedOrderCount);
    }

    @GetMapping("/get-count-of-orders-left-after-given-time/{partnerId}")
    public ResponseEntity<Integer> getOrdersLeftAfterGivenTimeByPartnerId(@PathVariable String partnerId,
                                                                          @RequestParam String time) {
        // Get the count of orders left after a given time for a delivery partner using the OrderService and return appropriate response
        int ordersLeftAfterTime = orderService.getOrdersLeftAfterGivenTimeByPartnerId(partnerId, time);
        return ResponseEntity.ok(ordersLeftAfterTime);
    }

    @GetMapping("/get-last-delivery-time/{partnerId}")
    public ResponseEntity<String> getLastDeliveryTimeByPartnerId(@PathVariable String partnerId) {
        // Get the time of the last delivery for a delivery partner using the OrderService and return appropriate response
        String lastDeliveryTime = orderService.getLastDeliveryTimeByPartnerId(partnerId);
        return ResponseEntity.ok(lastDeliveryTime);
    }

    @DeleteMapping("/delete-partner-by-id/{partnerId}")
    public ResponseEntity<String> deletePartnerById(@PathVariable String partnerId) {
        // Delete the delivery partner and unassign their orders using the OrderService and return appropriate response
        orderService.deleteDeliveryPartner(partnerId);
        return ResponseEntity.ok(partnerId + " removed successfully");
    }

    @DeleteMapping("/delete-order-by-id/{orderId}")
    public ResponseEntity<String> deleteOrderById(@PathVariable String orderId) {
        // Delete an order and unassign it from the delivery partner using the OrderService and return appropriate response
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok(orderId + " removed successfully");
    }
}