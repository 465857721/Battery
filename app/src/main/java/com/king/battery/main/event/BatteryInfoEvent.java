package com.king.battery.main.event;

/**
 * Created by alvis on 2017/4/14.
 */

public class BatteryInfoEvent {
    private int num;
    private int BatteryV;
    private int BatteryT;
    private String state;


    public BatteryInfoEvent(int num, int batteryV, int batteryT,String state) {
        this.num = num;
        this.BatteryV = batteryV;
        this.BatteryT = batteryT;
        this.state =state;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getBatteryV() {
        return BatteryV;
    }

    public void setBatteryV(int batteryV) {
        BatteryV = batteryV;
    }

    public int getBatteryT() {
        return BatteryT;
    }

    public void setBatteryT(int batteryT) {
        BatteryT = batteryT;
    }
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
