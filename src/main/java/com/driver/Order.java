package com.driver;

public class Order {

    private String orderId;
    private int deliveryTime;

    public Order(String orderId, String deliveryTime) {
        this.orderId = orderId;
        this.deliveryTime = convertDeliveryTimeToMinutes(deliveryTime);
    }

    private int convertDeliveryTimeToMinutes(String deliveryTime) {
        String[] timeParts = deliveryTime.split(":");
        int hours = Integer.parseInt(timeParts[0]);
        int minutes = Integer.parseInt(timeParts[1]);
        return hours * 60 + minutes;
    }

    public String getOrderId() {
        return orderId;
    }

    public int getDeliveryTime() {
        return deliveryTime;
    }
}