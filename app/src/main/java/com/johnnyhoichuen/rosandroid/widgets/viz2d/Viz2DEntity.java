package com.johnnyhoichuen.rosandroid.widgets.viz2d;


import com.johnnyhoichuen.rosandroid.model.entities.widgets.GroupEntity;

/**
 * TODO: Description
 *
 * @author Nico Studt
 * @version 1.0.0
 * @created on 08.03.21
 */
public class Viz2DEntity extends GroupEntity {

    public String frame;


    public Viz2DEntity() {
        this.width = 8;
        this.height = 8;
        this.frame = "odom";
    }
    
}
