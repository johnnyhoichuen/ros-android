package com.johnnyhoichuen.rosandroid.widgets.gridmap;

import com.johnnyhoichuen.rosandroid.model.entities.widgets.SubscriberLayerEntity;
import com.johnnyhoichuen.rosandroid.model.repositories.rosRepo.message.Topic;

import nav_msgs.OccupancyGrid;


/**
 * TODO: Description
 *
 * @author Nico Studt
 * @version 1.0.0
 * @created on 08.03.21
 */
public class GridMapEntity extends SubscriberLayerEntity {
    
    public GridMapEntity() {
        this.topic = new Topic("/move_base/local_costmap/costmap", OccupancyGrid._TYPE);
    }
    
}
