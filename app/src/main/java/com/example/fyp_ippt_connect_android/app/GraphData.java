package com.example.fyp_ippt_connect_android.app;

public class GraphData {
    private double x;
    private double y1;
    private double y2;

    public GraphData(double x, double y1, double y2) {
        this.x = x;
        this.y1 = y1;
        this.y2 = y2;
    }
    public GraphData(){}

    public double getX() {
        return x;
    }

    public double getY1() {
        return y1;
    }

    public double getY2() {
        return y2;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY1(double y1) {
        this.y1 = y1;
    }

    public void setY2(double y2) {
        this.y2 = y2;
    }
}
