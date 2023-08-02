package com.driver;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository = new OrderRepository();

    public void addOrder(Order order) {
        orderRepository.addOrder(order);
    }

    public void addDeliveryPartner(String partnerId) {
        DeliveryPartner partner = new DeliveryPartner(partnerId);
        orderRepository.addDeliveryPartner(partner);
    }

    public void assignOrderToPartner(String orderId, String partnerId) {
        orderRepository.assignOrderToPartner(orderId, partnerId);
    }

    public Order getOrderById(String orderId) {
        return orderRepository.getOrderById(orderId);
    }

    public DeliveryPartner getDeliveryPartnerById(String partnerId) {
        return orderRepository.getPartnerById(partnerId);
    }

    public int getOrderCountByPartnerId(String partnerId) {
        return orderRepository.getOrderCountByPartnerId(partnerId);
    }

    public List<Order> getOrdersByPartnerId(String partnerId) {
        return orderRepository.getOrdersByPartnerId(partnerId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.getAllOrders();
    }

    public int getCountOfUnassignedOrders() {
        return orderRepository.getCountOfUnassignedOrders();
    }

    public int getOrdersLeftAfterGivenTimeByPartnerId(String partnerId, String time) {
        int deliveryTimeMinutes = convertToMinutes(time);
        return orderRepository.getOrdersLeftAfterGivenTimeByPartnerId(partnerId, deliveryTimeMinutes);
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {
        return orderRepository.getLastDeliveryTimeByPartnerId(partnerId);
    }

    public void deleteDeliveryPartner(String partnerId) {
        orderRepository.deleteDeliveryPartner(partnerId);
    }

    public void deleteOrder(String orderId) {
        orderRepository.deleteOrder(orderId);
    }

    private int convertToMinutes(String deliveryTime) {
        String[] parts = deliveryTime.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }
}