package com.johnnyhoichuen.rosandroid.model.entities;

public enum CustomTopic {
    SLOVLP_EKF("/slovlp_ekf_info"),
    EXTERNAL_POSE("/external_pose");
//    LIPHY_VLP("/liphy_vlp_info");

    public final String name;

    CustomTopic(String name) {
        this.name = name;
    }
}
