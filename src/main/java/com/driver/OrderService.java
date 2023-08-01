package com.driver;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private Map<String, Order> ordersMap = new HashMap<>();
    private Map<String, DeliveryPartner> partnersMap = new HashMap<>();
    private Map<String, String> assignedOrdersMap = new HashMap<>();

    public String addOrder(Order order) {
        if (order != null && !ordersMap.containsKey(order.getId())) {
            ordersMap.put(order.getId(), order);
            return "New order added successfully";
        }
        return "Order ID already exists or the order is null";
    }

    public String addPartner(String partnerId) {
        if (partnerId != null && !partnersMap.containsKey(partnerId)) {
            partnersMap.put(partnerId, new DeliveryPartner(partnerId));
            return "New delivery partner added successfully";
        }
        return "Delivery Partner ID already exists or the ID is null";
    }

    public String addOrderPartnerPair(String orderId, String partnerId) {
        if (ordersMap.containsKey(orderId) && partnersMap.containsKey(partnerId)) {
            assignedOrdersMap.put(orderId, partnerId);
            partnersMap.get(partnerId).incrementNumberOfOrders();
            return "New order-partner pair added successfully";
        }
        return "Order ID or Partner ID not found";
    }

    public Order getOrderById(String orderId) {
        return ordersMap.getOrDefault(orderId, null);
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        return partnersMap.getOrDefault(partnerId, null);
    }

    public int getOrderCountByPartnerId(String partnerId) {
        return (int) assignedOrdersMap.values().stream().filter(id -> id.equals(partnerId)).count();
    }

    public List<Order> getOrdersByPartnerId(String partnerId) {
        return assignedOrdersMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(partnerId))
                .map(entry -> ordersMap.get(entry.getKey()))
                .collect(Collectors.toList());
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(ordersMap.values());
    }

    public int getCountOfUnassignedOrders() {
        return (int) ordersMap.values().stream().filter(order -> !assignedOrdersMap.containsKey(order.getId())).count();
    }

    public int getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {
        String[] timeParts = time.split(":");
        int hh = Integer.parseInt(timeParts[0]);
        int mm = Integer.parseInt(timeParts[1]);
        int givenTime = hh * 60 + mm;

        return (int) assignedOrdersMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(partnerId))
                .map(entry -> ordersMap.get(entry.getKey()))
                .filter(order -> order.getDeliveryTime() > givenTime)
                .count();
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {
        List<Order> partnerOrders = getOrdersByPartnerId(partnerId);
        if (partnerOrders.isEmpty()) {
            return "No orders for the given partner";
        }
        int maxDeliveryTime = partnerOrders.stream().mapToInt(Order::getDeliveryTime).max().getAsInt();
        int hh = maxDeliveryTime / 60;
        int mm = maxDeliveryTime % 60;
        return String.format("%02d:%02d", hh, mm);
    }

    public String deletePartnerById(String partnerId) {
        if (partnersMap.containsKey(partnerId)) {
            partnersMap.remove(partnerId);
            assignedOrdersMap.entrySet().removeIf(entry -> entry.getValue().equals(partnerId));
            return partnerId + " removed successfully";
        }
        return "Partner ID not found";
    }

    public String deleteOrderById(String orderId) {
        if (ordersMap.containsKey(orderId)) {
            String partnerId = assignedOrdersMap.get(orderId);
            if (partnerId != null) {
                partnersMap.get(partnerId).decrementNumberOfOrders();
                assignedOrdersMap.remove(orderId);
            }
            ordersMap.remove(orderId);
            return orderId + " removed successfully";
        }
        return "Order ID not found";
    }
}