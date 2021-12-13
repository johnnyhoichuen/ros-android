package com.johnnyhoichuen.rosandroid.temi;

import com.johnnyhoichuen.rosandroid.model.entities.widgets.BaseEntity;
import com.johnnyhoichuen.rosandroid.model.repositories.rosRepo.node.BaseData;
import com.johnnyhoichuen.rosandroid.widgets.joystick.JoystickEntity;

import org.ros.internal.message.Message;
import org.ros.node.topic.Publisher;

import geometry_msgs.Twist;

public class TemiData extends BaseData {

    public static final String TAG = TemiData.class.getSimpleName();
    public float x;
    public float y;

    public TemiData(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public Message toRosMessage(Publisher<Message> publisher, BaseEntity widget) {

        // translate temi's x,y to

//        float xAxisValue = joyWidget.xScaleLeft + (joyWidget.xScaleRight - joyWidget.xScaleLeft) * ((x + 1) / 2f);
//        float yAxisValue = joyWidget.yScaleLeft + (joyWidget.yScaleRight - joyWidget.yScaleLeft) * ((y + 1) / 2f);

        geometry_msgs.Twist message = (Twist) publisher.newMessage();

        return message;
    }
}