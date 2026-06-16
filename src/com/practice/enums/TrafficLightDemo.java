package com.practice.enums;

/**
 * 交通信号灯控制系统
 */
public class TrafficLightDemo {
    public interface Describable {
        String describe();
    }

    enum TrafficLight implements Describable {
        RED(60) {
            @Override
            public String describe() {
                return "停车";
            }
        },
        GREEN(45) {
            @Override
            public String describe() {
                return "通行";
            }
        },
        YELLOW(3) {
            @Override
            public String describe() {
                return "注意";
            }
        };

        private final int duration;

        TrafficLight(int duration) {
            this.duration = duration;
        }

        public int getDuration() {
            return duration;
        }
    }

    // 三种颜色：RED(60秒), GREEN(45秒), YELLOW(3秒)

    public static void main(String[] args) {
        for (TrafficLight light : TrafficLight.values()) {
            System.out.println(light.name() + ": " + light.getDuration() + "秒 → " + light.describe());
        }
    }
}
