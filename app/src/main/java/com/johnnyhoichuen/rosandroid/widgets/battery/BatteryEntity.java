package com.johnnyhoichuen.rosandroid.widgets.battery;

import com.johnnyhoichuen.rosandroid.model.entities.widgets.SubscriberWidgetEntity;
import com.johnnyhoichuen.rosandroid.model.repositories.rosRepo.message.Topic;


/**
 * TODO: Description
 *
 * @author Nico Studt
 * @version 1.0.0
 * @created on 13.05.2021
 */
public class BatteryEntity extends SubscriberWidgetEntity {

    public boolean displayVoltage;


    public BatteryEntity() {
        this.width = 1;
        this.height = 2;
        this.topic = new Topic("battery", sensor_msgs.BatteryState._TYPE);
        this.displayVoltage = false;
    }

}
