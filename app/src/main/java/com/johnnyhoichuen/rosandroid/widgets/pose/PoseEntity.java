package com.johnnyhoichuen.rosandroid.widgets.pose;

import com.johnnyhoichuen.rosandroid.model.entities.widgets.SubscriberLayerEntity;
import com.johnnyhoichuen.rosandroid.model.repositories.rosRepo.message.Topic;

import geometry_msgs.PoseWithCovarianceStamped;


/**
 * Pose entity represents a widget which subscribes
 * to a topic with message type "geometry_msgs.PoseStamped".
 * Usable in 2D widgets.
 *
 * @author Nico Studt
 * @version 1.0.0
 * @created on 10.03.21
 */
public class PoseEntity extends SubscriberLayerEntity {


    public PoseEntity() {
        this.topic = new Topic("/slovlp_ekf_info", PoseWithCovarianceStamped._TYPE);

    }
}
