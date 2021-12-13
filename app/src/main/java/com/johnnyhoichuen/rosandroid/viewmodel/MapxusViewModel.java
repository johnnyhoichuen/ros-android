package com.johnnyhoichuen.rosandroid.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import geometry_msgs.Quaternion;
import geometry_msgs.Transform;
import tf2_msgs.TFMessage;
import timber.log.Timber;

import com.johnnyhoichuen.rosandroid.domain.RosDomain;
import com.johnnyhoichuen.rosandroid.mapxus.MapxusManager;
import com.johnnyhoichuen.rosandroid.model.entities.widgets.BaseEntity;
import com.johnnyhoichuen.rosandroid.model.repositories.rosRepo.message.RosData;
import com.johnnyhoichuen.rosandroid.temi.TemiManager;
import com.mapxus.map.mapxusmap.api.map.model.LatLng;
import com.mapxus.map.mapxusmap.positioning.IndoorLocation;
import com.robotemi.sdk.navigation.model.Position;

public class MapxusViewModel extends AndroidViewModel {

    private static final String TAG = MapxusViewModel.class.getSimpleName();
    private final RosDomain rosDomain;

    private final TemiManager temiManager;
    private MapxusManager mapxusManager;
    private OnTemiLocationChangedListener temiListener;
    private OnRobotLocationChangedListener robotListener;

//    private double[] darr = new double[2];
//    private final LiveData<RosData> rosData;

    public MapxusViewModel(@NonNull Application application) {
        super(application);
        rosDomain = RosDomain.getInstance(application);
        
        Timber.tag(TAG).d("MapxusViewModel init");
        Log.d(TAG, "MapxusViewModel init");

//        // subscribe to "/slovlp_ekf_info"
//        // registered in rosRepository
//        ekfNode = new SubNode();
//        ekfNode.setWidget(new PoseEntity());
//        Topic topic = new Topic("/slovlp_ekf_info", "geometry_msgs.PoseWithCovarianceStamped");
//        ekfNode.setTopic(topic);

//        // example in http://wiki.ros.org/rosjava/Tutorials/Create%20a%20ROS%20Android%20Node
//        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(
//        InetAddressFactory.newNonLoopback().getHostAddress());
//        nodeConfiguration.setMasterUri(getMasterUri());
//        nodeMainExecutor.execute(ekfNode, nodeConfiguration);

        // temi
        temiManager = TemiManager.getInstance();

        // mapxus
        // not usable at this moment. Need to be initialised in UI's onMapxusMapReady
        mapxusManager = MapxusManager.getInstance();

        // observe rosDomain.getData() and get the topic we want
//        rosData = rosDomain.getData();
//        rosData.observeForever(data -> {
        getRosData().observeForever(data -> {
            // get turtlebot's location
            switch (data.getTopic().name) {
                case "/slovlp_ekf_info":
                    Log.d(TAG, "ekf topic: " + data.getTopic().name + data.getTopic().type);
                    Log.d(TAG, "ekf message: " + data.getMessage());

                    break;
                case "/tf":
                    assert data.getMessage() instanceof TFMessage;

                    TFMessage message = (TFMessage) data.getMessage();
                    Transform tf = message.getTransforms().get(0).getTransform();

                    double x = tf.getTranslation().getX();
                    double y = tf.getTranslation().getY();

                    Quaternion q = tf.getRotation();

                    ////                    ArrayList<Double> originalQ = new ArrayList<>();
                    //                    double[] originalQ = {q.getW(), q.getX(), q.getY(), q.getZ()};
                    //                    double[] norm = new double[4];
                    //
                    //                    for (int i = 0; i < 4; i++){
                    //                        norm[i] = Math.sqrt(Math.pow(originalQ[i], 2) + 1);
                    //                    }
                    //
                    //                    double[] newQ = new double[4];
                    //                    for (int i = 0; i < 4; i++) {
                    //                        newQ[i] = originalQ[i] / norm[i];
                    //
                    //                        if (newQ[i] < 0) newQ[i] = -newQ[i];
                    //                    }
                    //
                    //                    double[] eul = new double[3];
                    //                    eul[0] = Math.atan2(2*())

                    Log.d(TAG, "tf q (w, x, y, z): " + q.getW() + ", " + q.getX() + ", " + q.getY() + ", "+ q.getZ() + ", ");

                    //                    double orientation = Math.atan2(2.0*(q.getY()*q.getZ() + q.getW()*q.getX()),
                    //                        q.getW()*q.getW() - q.getX()*q.getX() - q.getY()*q.getY() + q.getZ()*q.getZ());
                    double orientation = Math.atan2(2.0*(q.getZ()*q.getW() + q.getX()*q.getY()),
                            - 1 + (2.0 * (q.getW()*q.getW() + q.getX()*q.getX())));
                    //                    double orientation = (Math.asin(q.getZ()) * 2) * 180 / Math.PI; // 90 degrees shift

                    //atan2(2.0 * (q.q3 * q.q0 + q.q1 * q.q2) , - 1.0 + 2.0 * (q.q0 * q.q0 + q.q1 * q.q1));

                    // change to radian
                    orientation = Math.toDegrees(orientation);

                    // reverse the rotation
                    orientation += 180;
                    if (orientation >= 360)
                        orientation = orientation % 360;

                    // TODO: 13 Dec 2021 fix this 90 degree shift issue
                    // and 90 degree shift
                    orientation = 360 - orientation - 90;


                    Log.d(TAG, "tf topic: " + data.getTopic().name + data.getTopic().type);
                    Log.d(TAG, "tf data (x, y, q, orientation): " + x + ", " + y + ", " + orientation);

                    // CYT building corner as origin
                    double originLat = 22.334566;
                    double originLng = 114.263432;

                    // simple way to calculate
                    final float latScaleFactor = (float) 0.00001;
                    final float lngScaleFactor = (float) 0.00001045;

                    // TODO: simplify this by calculating rotatedX & rotatedY once only (per venue)
                    //                        int angleDiff = 180;
                    //                        float rotatedX = (float) (x * (Math.cos(Math.toRadians(angleDiff)) - Math.sin(Math.toRadians(angleDiff))));
                    //                        float rotatedY = (float) (y * (Math.sin(Math.toRadians(angleDiff)) + Math.cos(Math.toRadians(angleDiff))));

                    // bug: ignoring angle for now
                    double rotatedX = x;
                    double rotatedY = y;

                    double latitude = rotatedY * latScaleFactor + originLat;
                    double longitude = rotatedX * lngScaleFactor + originLng;

                    // pass the location to UI
                    robotListener.OnRobotLocationChanged(new IndoorLocation("TURTLE_BOT",
                            latitude, longitude, "3F", "31742af5bc8446acad14e0c053ae468a", System.currentTimeMillis()));

                    break;
                case "/odom":
                    break;
            }

            // print sth
//            Log.d(TAG, "general data topic: " + data.getTopic().name + data.getTopic().type);
//            Log.d(TAG, "general data message: " + data.getMessage());
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

    public void onDestroy() {
        temiManager.deinit();
        unregisterOnTemiLocationChangedListener();
        unregisterOnRobotLocationChangedListener();
    }



    public LiveData<RosData> getRosData() {
        return this.rosDomain.getData();
    }

    public LiveData<Position> getTemiPosition() {
        return this.temiManager.getPosition();
    }

    public MapxusManager getMapxusManager() {
        // lazy loading
        if (mapxusManager == null) {
            mapxusManager = MapxusManager.getInstance();
        }
        return mapxusManager;
    }

    public void updateWidget(BaseEntity widget) {
        rosDomain.updateWidget(null, widget);
    }

    public void registerOnTemiLocationChangedListener(OnTemiLocationChangedListener listener) {
        this.temiListener = listener;
    }

    public void unregisterOnTemiLocationChangedListener() {
        this.temiListener = null;
    }

    public void registerOnRobotLocationChangedListener(OnRobotLocationChangedListener listener) {
        this.robotListener = listener;
    }

    public void unregisterOnRobotLocationChangedListener() {
        this.robotListener = null;
    }

    public interface OnTemiLocationChangedListener {
        void OnTemiLocationChanged(IndoorLocation location);
    }

    public interface OnRobotLocationChangedListener {
        void OnRobotLocationChanged(IndoorLocation location);
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
}
