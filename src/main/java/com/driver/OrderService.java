package com.driver;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    private Map<String, Order> ordersMap = new HashMap<>();
    private Map<String, DeliveryPartner> partnersMap = new HashMap<>();
    private Map<String, String> orderPartnerMap = new HashMap<>();

    public void addOrder(Order order) {
        ordersMap.put(order.getId(), order);
    }

    public void addPartner(String partnerId) {
        if (!partnersMap.containsKey(partnerId)) {
            partnersMap.put(partnerId, new DeliveryPartner(partnerId));
        }
    }

    public void assignOrderToPartner(String orderId, String partnerId) {
        if (ordersMap.containsKey(orderId) && partnersMap.containsKey(partnerId)) {
            orderPartnerMap.put(orderId, partnerId);
            DeliveryPartner partner = partnersMap.get(partnerId);
            partner.incrementNumberOfOrders();
        }
    }

    public Order getOrderById(String orderId) {
        return ordersMap.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        return partnersMap.get(partnerId);
    }

    public int getOrderCountByPartnerId(String partnerId) {
        int count = 0;
        for (String orderId : orderPartnerMap.keySet()) {
            if (orderPartnerMap.get(orderId).equals(partnerId)) {
                count++;
            }
        }
        return count;
    }

    public List<Order> getOrdersByPartnerId(String partnerId) {
        List<Order> orders = new ArrayList<>();
        for (String orderId : orderPartnerMap.keySet()) {
            if (orderPartnerMap.get(orderId).equals(partnerId)) {
                orders.add(ordersMap.get(orderId));
            }
        }
        return orders;
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(ordersMap.values());
    }

    public int getCountOfUnassignedOrders() {
        int count = 0;
        for (String orderId : ordersMap.keySet()) {
            if (!orderPartnerMap.containsKey(orderId)) {
                count++;
            }
        }
        return count;
    }

    public int getCountOfOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {
        String[] timeParts = time.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        int count = 0;
        for (String orderId : orderPartnerMap.keySet()) {
            String currentPartnerId = orderPartnerMap.get(orderId);
            if (currentPartnerId.equals(partnerId)) {
                Order order = ordersMap.get(orderId);
                int orderHour = order.getDeliveryTime() / 60;
                int orderMinute = order.getDeliveryTime() % 60;
                if (orderHour > hour || (orderHour == hour && orderMinute > minute)) {
                    count++;
                }
            }
        }
        return count;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {
        String lastDeliveryTime = "N/A";
        int lastDeliveryTimeValue = -1;

        for (String orderId : orderPartnerMap.keySet()) {
            if (orderPartnerMap.get(orderId).equals(partnerId)) {
                int orderDeliveryTime = ordersMap.get(orderId).getDeliveryTime();
                if (orderDeliveryTime > lastDeliveryTimeValue) {
                    lastDeliveryTimeValue = orderDeliveryTime;
                    lastDeliveryTime = String.format("%02d:%02d", orderDeliveryTime / 60, orderDeliveryTime % 60);
                }
            }
        }
        return lastDeliveryTime;
    }

    public void deletePartnerById(String partnerId) {
        if (partnersMap.containsKey(partnerId)) {
            DeliveryPartner partner = partnersMap.get(partnerId);
            int numberOfOrders = partner.getNumberOfOrders();
            if (numberOfOrders > 0) {
                for (String orderId : orderPartnerMap.keySet()) {
                    if (orderPartnerMap.get(orderId).equals(partnerId)) {
                        orderPartnerMap.remove(orderId);
                    }
                }
            }
            partnersMap.remove(partnerId);
        }
    }

    public void deleteOrderById(String orderId) {
        if (ordersMap.containsKey(orderId)) {
            if (orderPartnerMap.containsKey(orderId)) {
                String partnerId = orderPartnerMap.get(orderId);
                DeliveryPartner partner = partnersMap.get(partnerId);
                partner.decrementNumberOfOrders();
                orderPartnerMap.remove(orderId);
            }
            ordersMap.remove(orderId);
        }
    }
}