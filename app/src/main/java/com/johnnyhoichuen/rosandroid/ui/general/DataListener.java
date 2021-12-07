package com.johnnyhoichuen.rosandroid.ui.general;


import com.johnnyhoichuen.rosandroid.model.repositories.rosRepo.node.BaseData;

/**
 * TODO: Description
 *
 * @author Nico Studt
 * @version 1.0.0
 * @created on 15.03.20
 * @updated on 15.03.20
 * @modified by
 */
public interface DataListener {

// johnny renamed it from onNewWidgetData to this
    void onNewWidgetData(BaseData data);
}
