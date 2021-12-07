package com.johnnyhoichuen.rosandroid.model.entities;

public enum CustomTopicName {
    SLOVLP_EKF("/slovlp_ekf_info");
//    LIPHY_VLP("/liphy_vlp_info");

    public final String name;

    CustomTopicName(String name) {
        this.name = name;
    }
}
