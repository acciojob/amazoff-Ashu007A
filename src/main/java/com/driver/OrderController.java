package com.driver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final Map<String, Order> orderMap = new HashMap<>();
    private final Map<String, DeliveryPartner> partnerMap = new HashMap<>();
    private final Map<String, String> orderPartnerMap = new HashMap<>();
    private ConcurrentMap<String, Order> orders = new ConcurrentHashMap<>();
    private ConcurrentMap<String, DeliveryPartner> partners = new ConcurrentHashMap<>();

    @PostMapping("/add-order")
    public ResponseEntity<String> addOrder(@RequestBody Order order) {
        orderMap.put(order.getId(), order);
        return new ResponseEntity<>("New order added successfully", HttpStatus.CREATED);
    }

    @PostMapping("/add-partner/{partnerId}")
    public ResponseEntity<String> addPartner(@PathVariable String partnerId) {
        partnerMap.put(partnerId, new DeliveryPartner(partnerId));
        return new ResponseEntity<>("New delivery partner added successfully", HttpStatus.CREATED);
    }

    @PutMapping("/add-order-partner-pair")
    public ResponseEntity<String> addOrderPartnerPair(@RequestParam String orderId, @RequestParam String partnerId) {
        orderPartnerMap.put(orderId, partnerId);
        if (partnerMap.containsKey(partnerId)) {
            DeliveryPartner partner = partnerMap.get(partnerId);
            partner.setNumberOfOrders(partner.getNumberOfOrders() + 1);
        }
        return new ResponseEntity<>("New order-partner pair added successfully", HttpStatus.CREATED);
    }

    @GetMapping("/get-order-by-id/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable String orderId) {
        Order order = orderMap.get(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @GetMapping("/get-partner-by-id/{partnerId}")
    public ResponseEntity<DeliveryPartner> getPartnerById(@PathVariable String partnerId) {
        DeliveryPartner deliveryPartner = partnerMap.get(partnerId);
        return new ResponseEntity<>(deliveryPartner, HttpStatus.OK);
    }

    @GetMapping("/get-order-count-by-partner-id/{partnerId}")
    public ResponseEntity<Integer> getOrderCountByPartnerId(@PathVariable String partnerId) {
        if (partnerMap.containsKey(partnerId)) {
            DeliveryPartner partner = partnerMap.get(partnerId);
            return new ResponseEntity<>(partner.getNumberOfOrders(), HttpStatus.OK);
        }
        return new ResponseEntity<>(0, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/get-orders-by-partner-id/{partnerId}")
    public ResponseEntity<List<String>> getOrdersByPartnerId(@PathVariable String partnerId) {
        List<String> orders = new ArrayList<>();
        for (Map.Entry<String, String> entry : orderPartnerMap.entrySet()) {
            if (entry.getValue().equals(partnerId)) {
                orders.add(entry.getKey());
            }
        }
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/get-all-orders")
    public ResponseEntity<List<String>> getAllOrders() {
        List<String> orders = new ArrayList<>(orderMap.keySet());
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/get-count-of-unassigned-orders")
    public ResponseEntity<Integer> getCountOfUnassignedOrders() {
        int count = 0;
        for (Order order : orderMap.values()) {
            if (!orderPartnerMap.containsKey(order.getId())) {
                count++;
            }
        }
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

//    @GetMapping("/get-count-of-orders-left-after-given-time/{partnerId}")
//    public ResponseEntity<Integer> getOrdersLeftAfterGivenTimeByPartnerId(@PathVariable String partnerId, @RequestParam String time) {
//        int targetTime = convertDeliveryTimeToMinutes(time);
//        int count = 0;
//        for (Map.Entry<String, String> entry : orderPartnerMap.entrySet()) {
//            if (entry.getValue().equals(partnerId)) {
//                Order order = orderMap.get(entry.getKey());
//                if (order.getDeliveryTime() > targetTime) {
//                    count++;
//                }
//            }
//        }
//        return new ResponseEntity<>(count, HttpStatus.OK);
//    }

    @GetMapping("/get-count-of-orders-left-after-given-time/{time}/{partnerId}")
    public ResponseEntity<Integer> getCountOfOrdersLeftAfterGivenTimeByPartnerId(
            @PathVariable @DateTimeFormat(pattern = "HH:mm") LocalTime time,
            @PathVariable String partnerId) {

        DeliveryPartner partner = partners.get(partnerId);
        if (partner == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        int countOfOrders = 0;
        for (Order order : orders.values()) {
            if (order.getDeliveryTime().isAfter(time) && order.getAssignedPartnerId().equals(partnerId)) {
                countOfOrders++;
            }
        }

        return new ResponseEntity<>(countOfOrders, HttpStatus.OK);
    }

    @GetMapping("/get-last-delivery-time/{partnerId}")
    public ResponseEntity<String> getLastDeliveryTimeByPartnerId(@PathVariable String partnerId) {
        int lastDeliveryTime = Integer.MIN_VALUE;
        for (Map.Entry<String, String> entry : orderPartnerMap.entrySet()) {
            if (entry.getValue().equals(partnerId)) {
                Order order = orderMap.get(entry.getKey());
                if (order.getDeliveryTime() > lastDeliveryTime) {
                    lastDeliveryTime = order.getDeliveryTime();
                }
            }
        }
        String time = convertMinutesToDeliveryTime(lastDeliveryTime);
        return new ResponseEntity<>(time, HttpStatus.OK);
    }

    private int convertDeliveryTimeToMinutes(String deliveryTime) {
        String[] timeParts = deliveryTime.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        return hours * 60 + minutes;
    }

    private String convertMinutesToDeliveryTime(int minutes) {
        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;
        return String.format("%02d:%02d", hours, remainingMinutes);
    }

    @DeleteMapping("/delete-partner-by-id/{partnerId}")
    public ResponseEntity<String> deletePartnerById(@PathVariable String partnerId) {
        if (partnerMap.containsKey(partnerId)) {
            partnerMap.remove(partnerId);
            // Remove assigned orders from the partner
            for (Map.Entry<String, String> entry : orderPartnerMap.entrySet()) {
                if (entry.getValue().equals(partnerId)) {
                    orderPartnerMap.remove(entry.getKey());
                }
            }
            return new ResponseEntity<>(partnerId + " removed successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Partner not found", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/delete-order-by-id/{orderId}")
    public ResponseEntity<String> deleteOrderById(@PathVariable String orderId) {
        if (orderMap.containsKey(orderId)) {
            // Remove the order from partner assignments
            if (orderPartnerMap.containsKey(orderId)) {
                String partnerId = orderPartnerMap.get(orderId);
                DeliveryPartner partner = partnerMap.get(partnerId);
                if (partner != null) {
                    partner.setNumberOfOrders(partner.getNumberOfOrders() - 1);
                }
                orderPartnerMap.remove(orderId);
            }
            orderMap.remove(orderId);
            return new ResponseEntity<>(orderId + " removed successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Order not found", HttpStatus.NOT_FOUND);
    }
}