package com.driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderRepository {

    private Map<String, Order> orders = new HashMap<>();
    private Map<String, DeliveryPartner> partners = new HashMap<>();

    public void addOrder(Order order) {
        orders.put(order.getId(), order);
    }

    public void addDeliveryPartner(DeliveryPartner partner) {
        partners.put(partner.getId(), partner);
    }

    public Order getOrderById(String orderId) {
        return orders.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId) {
        return partners.get(partnerId);
    }

    public void assignOrderToPartner(String orderId, String partnerId) {
        Order order = orders.get(orderId);
        DeliveryPartner partner = partners.get(partnerId);

        if (order != null && partner != null) {
            partner.incrementNumberOfOrders();
            orders.put(orderId, order);
            partners.put(partnerId, partner);
        }
    }

    public List<Order> getOrdersByPartnerId(String partnerId) {
        List<Order> partnerOrders = new ArrayList<>();
        for (Order order : orders.values()) {
            if (order.getId() != null && order.getId().equals(partnerId)) {
                partnerOrders.add(order);
            }
        }
        return partnerOrders;
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orders.values());
    }

    public int getCountOfUnassignedOrders() {
        int count = 0;
        for (Order order : orders.values()) {
            if (order.getId() == null) {
                count++;
            }
        }
        return count;
    }

    public int getOrderCountByPartnerId(String partnerId) {
        int count = 0;
        for (Order order : orders.values()) {
            if (order.getId() != null && order.getId().equals(partnerId)) {
                count++;
            }
        }
        return count;
    }

    public int getOrdersLeftAfterGivenTimeByPartnerId(String partnerId, int deliveryTimeMinutes) {
        int count = 0;
        for (Order order : orders.values()) {
            if (order.getId() != null && order.getId().equals(partnerId) &&
                    order.getDeliveryTimeMinutes() > deliveryTimeMinutes) {
                count++;
            }
        }
        return count;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {
        int maxDeliveryTime = -1;
        for (Order order : orders.values()) {
            if (order.getId() != null && order.getId().equals(partnerId) &&
                    order.getDeliveryTimeMinutes() > maxDeliveryTime) {
                maxDeliveryTime = order.getDeliveryTimeMinutes();
            }
        }
        return convertMinutesToTime(maxDeliveryTime);
    }

    public void deleteOrder(String orderId) {
        Order order = orders.get(orderId);
        if (order != null) {
            String partnerId = order.getId();
            if (partnerId != null) {
                DeliveryPartner partner = partners.get(partnerId);
                if (partner != null) {
                    partner.decrementNumberOfOrders();
                    partners.put(partnerId, partner);
                }
            }
            orders.remove(orderId);
        }
    }

    public void deleteDeliveryPartner(String partnerId) {
        DeliveryPartner partner = partners.get(partnerId);
        if (partner != null) {
            for (Order order : orders.values()) {
                if (order.getId() != null && order.getId().equals(partnerId)) {
                    order.setId(null);
                }
            }
            partners.remove(partnerId);
        }
    }

    private String convertMinutesToTime(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        return String.format("%02d:%02d", hours, mins);
    }
}