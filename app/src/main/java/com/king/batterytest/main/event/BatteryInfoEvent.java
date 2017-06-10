package com.king.batterytest.main.event;

/**
 * Created by alvis on 2017/4/14.
 */

public class BatteryInfoEvent {
    private int num;
    private int BatteryV;
    private int BatteryT;

    public BatteryInfoEvent(int num, int batteryV, int batteryT) {
        this.num = num;
        BatteryV = batteryV;
        BatteryT = batteryT;
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
}
