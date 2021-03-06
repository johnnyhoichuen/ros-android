package com.johnnyhoichuen.rosandroid.widgets.debug;

import com.johnnyhoichuen.rosandroid.model.entities.widgets.SubscriberWidgetEntity;
import com.johnnyhoichuen.rosandroid.model.repositories.rosRepo.message.Topic;

import org.ros.node.topic.Subscriber;


/**
 * TODO: Description
 *
 * @author Nils Rottmann
 * @version 1.0.0
 * @created on 17.08.20
 * @updated on 17.09.20
 * @modified by Nils Rottmann
 */
public class DebugEntity extends SubscriberWidgetEntity {

    public int numberMessages;


    public DebugEntity() {
        this.width = 4;
        this.height = 3;
        this.topic = new Topic("MessageToDebug", Subscriber.TOPIC_MESSAGE_TYPE_WILDCARD);
        this.numberMessages = 10;
    }
}
