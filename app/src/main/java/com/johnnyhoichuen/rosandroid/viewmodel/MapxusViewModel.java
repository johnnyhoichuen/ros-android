package com.johnnyhoichuen.rosandroid.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.johnnyhoichuen.rosandroid.domain.RosDomain;
import com.johnnyhoichuen.rosandroid.model.entities.widgets.BaseEntity;
import com.johnnyhoichuen.rosandroid.model.repositories.rosRepo.message.RosData;
import com.johnnyhoichuen.rosandroid.model.repositories.rosRepo.node.SubNode.NodeListener;

public class MapxusViewModel extends AndroidViewModel implements NodeListener {

    private static final String TAG = MapxusViewModel.class.getSimpleName();
    private final RosDomain rosDomain;
    private double[] darr = new double[2];
    
    private final LiveData<RosData> rosData;

    public MapxusViewModel(@NonNull Application application) {
        super(application);
        rosDomain = RosDomain.getInstance(application);

//        // subscribe to "/slovlp_ekf_info"
//        // registered in rosRepository
//        ekfNode = new SubNode();
//        ekfNode.setWidget(new PoseEntity());
//        Topic topic = new Topic("/slovlp_ekf_info", "geometry_msgs.PoseWithCovarianceStamped");
//        ekfNode.setTopic(topic);

        // example in http://wiki.ros.org/rosjava/Tutorials/Create%20a%20ROS%20Android%20Node
//        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(
//        InetAddressFactory.newNonLoopback().getHostAddress());
//        nodeConfiguration.setMasterUri(getMasterUri());
//        nodeMainExecutor.execute(ekfNode, nodeConfiguration);

        // observe rosDomain.getData(), which include 
        rosData = rosDomain.getData();
        rosData.observeForever(data -> {
            // observe ekf data only
            if (data.getTopic().name.equals("/slovlp_ekf_info")) {
                // print sth
                Log.d(TAG, "ekf topic: " + data.getTopic().name + data.getTopic().type);
                Log.d(TAG, "ekf message: " + data.getMessage());
            }
//            if (data.getMessage() instanceof ) {
//            }

            // print sth
            Log.d(TAG, "general data topic: " + data.getTopic().name + data.getTopic().type);
            Log.d(TAG, "general data message: " + data.getMessage());
        });

        getRosData().observeForever(data -> {
            // print sth
        });

        // get temi's location
        getTemiPosition().observeForever(position -> {

            if (position == null) return;

            // filter out repeating signals
            if (temiManager.getLastPosition() != null)
                if (temiManager.getLastPosition().getX() == position.getX() &&
                        temiManager.getLastPosition().getY() == position.getY() &&
                        temiManager.getLastPosition().getYaw() == position.getYaw())
                    return;

            // log
            Timber.tag("temi").d("position xy: (%f, %f), yaw: %f",
                    position.getX(), position.getY(), position.getYaw());

            // diff between true north to y-axis (clockwise)
            // ICDC: 90 degrees shift
            LatLng latLng = temiManager.translateToLatlng(position.getX(), position.getY(), 90);

            // get degree in [0, 360]
            double degree = temiManager.getYawInDegree(position.getYaw());

            Timber.tag("temi").d("position latlng: (%f, %f), yaw in degree: %f",
                    latLng.latitude, latLng.longitude, degree);


            // set location to update mapxus map and wait for interval update

            // update location & orientation
            IndoorLocation location = new IndoorLocation("TEMI",
                    latLng.latitude, latLng.longitude, "3F", "31742af5bc8446acad14e0c053ae468a", System.currentTimeMillis());
            location.setBearing((float) degree);

            // pass it to UI/mapxus manager
            temiListener.OnTemiLocationChanged(location);

            // public data to ROS server
            // BaseData passed should contain temi's message name
//                BaseData data = new BaseData()
//                rosDomain.publishData();

            // update last position in temi manager
            temiManager.setLastPosition(position);
        });
    }

    public void onStart() {
        temiManager.init();
        mapxusManager.onStart();
    }

    public void onPause() {
        mapxusManager.onPause();
    }

    public LiveData<RosData> getRosData() {
        return this.rosDomain.getData();
    }

    @Override
    public void onNewMessage(RosData data) {

        // should be receiving data from slovlp

        Log.d(TAG, "data topic: " + data.getTopic().name);
//        darr = newMessage(data.getMessage());

    }

//    /**
//     * called when rosDomain.getData() is changed
//     * @param data
//     */
//    public void onNewData(RosData data) {
//        Topic topic = data.getTopic();
//        Message message = data.getMessage();
//
////        Timber.tag("mapxusVM").d("RosData topic: %s", topic);
////        Timber.tag("mapxusVM").d("RosData message: %s", message);
//
//        //PoseWithCovarianceStamped pose = (PoseWithCovarianceStamped) message;
//        //Log.d("Pose X Coordinate: ", String.valueOf(pose.getPose().getPose().getPosition().getX()));
//        // do sth according to the topic and messages
//        // switch () {...}
//    }
//    public double[] newMessage(Message message){
//        PoseWithCovarianceStamped pose = (PoseWithCovarianceStamped) message;
//        darr[0] = pose.getPose().getPose().getPosition().getX();
//        darr[1] = pose.getPose().getPose().getPosition().getY();
//        return darr;
//    }
//
//    public double[] getLocArray(){
//        return darr;
//    }
//
//    public void publishData(BaseData data) {
//        rosDomain.publishData(data);
//    }
//
//    public LiveData<List<BaseEntity>> getCurrentWidgets() {
//        return rosDomain.getCurrentWidgets();
//    }

    public void updateWidget(BaseEntity widget) {
        rosDomain.updateWidget(null, widget);
    }

}
