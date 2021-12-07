//package com.johnnyhoichuen.rosandroid.model;
//
//import org.ros.internal.message.Message;
//
//import std_msgs.Float64;
//import std_msgs.UInt8;
//
//public interface liphylight extends Message {
//    String _TYPE = "liphy_vlp/liphylight";
//    String _DEFINITION = "# This expresses the location from liphy light\n\nsome class some var\nsome class some var\n";
//
//    String getKeypoint();
//    UInt8 getCorners();
//
//    Float64 getLightX();
//    Float64 getLightY();
//    Float64 getLightZ();
//    Float64 getTheta();
//
//    void setKeypoint(String keypoint);
//    void setCorners(UInt8 corners);
//
//    void setLightX(Float64 x);
//    void setLightY(Float64 y);
//    void setLightZ(Float64 z);
//    void setTheta(Float64 theta);
//}
//
//
///*
//public interface PoseWithCovarianceStamped extends Message {
//    String _TYPE = "geometry_msgs/PoseWithCovarianceStamped";
//    String _DEFINITION = "# This expresses an estimated pose with a reference coordinate frame and timestamp\n\nHeader header\nPoseWithCovariance pose\n";
//
//    Header getHeader();
//
//    void setHeader(Header var1);
//
//    PoseWithCovariance getPose();
//
//    void setPose(PoseWithCovariance var1);
//}
// */