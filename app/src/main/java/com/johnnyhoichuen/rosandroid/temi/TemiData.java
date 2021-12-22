package com.johnnyhoichuen.rosandroid.temi;

import com.johnnyhoichuen.rosandroid.model.entities.CustomTopic;
//import com.johnnyhoichuen.rosandroid.model.entities.TemiEntity;
import com.johnnyhoichuen.rosandroid.model.entities.widgets.BaseEntity;
import com.johnnyhoichuen.rosandroid.model.repositories.rosRepo.message.Topic;
import com.johnnyhoichuen.rosandroid.model.repositories.rosRepo.node.BaseData;

import org.ros.internal.message.Message;
import org.ros.node.topic.Publisher;


import geometry_msgs.Point;
import geometry_msgs.Pose;
import geometry_msgs.Quaternion;
import geometry_msgs.Twist;

public class TemiData extends BaseData {

    public static final String TAG = TemiData.class.getSimpleName();
    public double latitude;
    public double longitude;
    public double yaw;

    public TemiData(double latitude, double longitude, double yaw) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.yaw = yaw;

        /*
        other similar classes (eg. JoystickData does not have topic embedded. Instead they put it
        in JoystickEntity)
         */
        setTopic(new Topic(CustomTopic.EXTERNAL_POSE.name, Pose._TYPE));
    }

    @Override
    public Message toRosMessage(Publisher<Message> publisher, BaseEntity entity) {

//        TemiEntity temiEntity = (TemiEntity) entity; // not useful
        geometry_msgs.Pose message = (Pose) publisher.newMessage();

// TODO: 16 Dec 2021 first, pass some arbitrary data to ROS master
//        message.getLinear().setX(x);
//        message.getLinear().setY(y);
////        message.getLinear().setZ();
////        message.getAngular().setX();
////        message.getAngular().setY();
//        message.getAngular().setZ(angle);

// TODO: 16 Dec 2021 translate temi's x,y to latlng and comment above lines
//        message.getLinear().setX(latitude);
//        message.getLinear().setY(longitude);
//        message.getAngular().setZ(angle);
        message.getPosition().setX(latitude);
        message.getPosition().setY(longitude);

        // angle to quaternion
        double cy = Math.cos(yaw * 0.5);
        double sy = Math.sin(yaw * 0.5);
        double cp = 1; // Math.cos(pitch * 0.5);
        double sp = 0; // Math.sin(pitch * 0.5);
        double cr = 1; // Math.cos(roll * 0.5);
        double sr = 0; // Math.sin(roll * 0.5);

        // quaternion values
        double w = cr * cp * cy + sr * sp * sy;
        double x = sr * cp * cy - cr * sp * sy;
        double y = cr * sp * cy + sr * cp * sy;
        double z = cr * cp * sy - sr * sp * cy;

        message.getOrientation().setW(w);
        message.getOrientation().setX(x);
        message.getOrientation().setY(y);
        message.getOrientation().setZ(z);

        return message;
    }
}