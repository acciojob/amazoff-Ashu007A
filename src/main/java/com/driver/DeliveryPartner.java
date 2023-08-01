package com.driver;

public class DeliveryPartner {

    private String partnerId;
    private int numberOfOrders;

    public DeliveryPartner(String partnerId) {
        this.partnerId = partnerId;
        this.numberOfOrders = 0;
    }

    public String getId() {
        return partnerId;
    }

    public int getNumberOfOrders() {
        return numberOfOrders;
    }

    public void setNumberOfOrders(int numberOfOrders) {
        this.numberOfOrders = numberOfOrders;
    }
}