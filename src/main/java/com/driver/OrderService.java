package com.driver;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final Map<String, Order> orders = new HashMap<>();
    private final Map<String, DeliveryPartner> deliveryPartners = new HashMap<>();
    private final Map<String, String> orderToPartnerMapping = new HashMap<>();

    public String addOrder(Order order) {
        orders.put(order.getId(), order);
        return "New order added successfully";
    }

    public String addPartner(String partnerId) {
        deliveryPartners.put(partnerId, new DeliveryPartner(partnerId));
        return "New delivery partner added successfully";
    }

    public String addOrderPartnerPair(String orderId, String partnerId) {
        if (!orders.containsKey(orderId) || !deliveryPartners.containsKey(partnerId)) {
            return "Invalid orderId or partnerId";
        }

        orderToPartnerMapping.put(orderId, partnerId);
        DeliveryPartner partner = deliveryPartners.get(partnerId);
        partner.setNumberOfOrders(partner.getNumberOfOrders() + 1);
        return "New order-partner pair added successfully";
    }

    public Order getOrderById(String orderId) {
        return orders.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        return deliveryPartners.get(partnerId);
    }

    public int getOrderCountByPartnerId(String partnerId) {
        return (int) orderToPartnerMapping.values().stream().filter(pid -> pid.equals(partnerId)).count();
    }

    public List<Order> getOrdersByPartnerId(String partnerId) {
        return orderToPartnerMapping.entrySet().stream()
                .filter(entry -> entry.getValue().equals(partnerId))
                .map(entry -> orders.get(entry.getKey()))
                .collect(Collectors.toList());
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orders.values());
    }

    public int getCountOfUnassignedOrders() {
        return (int) orders.keySet().stream().filter(orderId -> !orderToPartnerMapping.containsKey(orderId)).count();
    }

    public int getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {
        int givenTimeInMinutes = convertToMinutes(time);
        return (int) orderToPartnerMapping.entrySet().stream()
                .filter(entry -> entry.getValue().equals(partnerId))
                .filter(entry -> getOrderTimeInMinutes(entry.getKey()) > givenTimeInMinutes)
                .count();
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {
        int lastDeliveryTimeInMinutes = orderToPartnerMapping.entrySet().stream()
                .filter(entry -> entry.getValue().equals(partnerId))
                .map(entry -> getOrderTimeInMinutes(entry.getKey()))
                .max(Integer::compare)
                .orElse(0);

        return convertToTime(lastDeliveryTimeInMinutes);
    }

    public String deletePartnerById(String partnerId) {
        List<String> ordersToUnassign = orderToPartnerMapping.entrySet().stream()
                .filter(entry -> entry.getValue().equals(partnerId))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        ordersToUnassign.forEach(orderToPartnerMapping::remove);
        deliveryPartners.remove(partnerId);

        return partnerId + " removed successfully";
    }

    public String deleteOrderById(String orderId) {
        if (orderToPartnerMapping.containsKey(orderId)) {
            String partnerId = orderToPartnerMapping.remove(orderId);
            DeliveryPartner partner = deliveryPartners.get(partnerId);
            if (partner != null) {
                partner.setNumberOfOrders(partner.getNumberOfOrders() - 1);
            }
        }
        orders.remove(orderId);
        return orderId + " removed successfully";
    }

    private int convertToMinutes(String time) {
        String[] timeParts = time.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        return hours * 60 + minutes;
    }

    private String convertToTime(int minutes) {
        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;
        return String.format("%02d:%02d", hours, remainingMinutes);
    }

    private int getOrderTimeInMinutes(String orderId) {
        return orders.get(orderId).getDeliveryTime();
    }
}