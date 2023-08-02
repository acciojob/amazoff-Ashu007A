package com.driver;

public class Order {

    private String id;
    private int deliveryTimeMinutes;

    public Order(String id, String deliveryTime) {
        this.id = id;
        this.deliveryTimeMinutes = convertToMinutes(deliveryTime);
    }

    private int convertToMinutes(String deliveryTime) {
        String[] parts = deliveryTime.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }

    public String getId() {
        return id;
    }

    public int getDeliveryTimeMinutes() {
        return deliveryTimeMinutes;
    }

    public void setId(String id) {
        this.id = id;
    }
}