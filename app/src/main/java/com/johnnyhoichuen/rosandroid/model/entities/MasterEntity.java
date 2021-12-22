package com.johnnyhoichuen.rosandroid.model.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


/**
 * TODO: Description
 *
 * @author Nico Studt
 * @version 1.0.1
 * @created on 30.01.20
 * @updated on 31.01.20
 * @modified by
 */
@Entity(tableName = "master_table")
public class MasterEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public long configId;

    /*
    lenovo carbon x1: 10
     */
    public String ip = "192.168.0.10";
    public int port = 11311;
}
