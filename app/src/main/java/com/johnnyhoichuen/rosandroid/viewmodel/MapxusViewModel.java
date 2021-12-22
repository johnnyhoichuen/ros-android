package com.johnnyhoichuen.rosandroid.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import geometry_msgs.Pose;
import geometry_msgs.Quaternion;
import geometry_msgs.Transform;
import tf2_msgs.TFMessage;
import timber.log.Timber;

import com.johnnyhoichuen.rosandroid.domain.RosDomain;
import com.johnnyhoichuen.rosandroid.mapxus.MapxusManager;
import com.johnnyhoichuen.rosandroid.model.repositories.rosRepo.message.RosData;
import com.johnnyhoichuen.rosandroid.temi.TemiData;
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

        // observe any data from ros and filter the topic
        getRosData().observeForever(this::onRosDataChanged);

        // observe temi's location
        getTemiPosition().observeForever(this::onTemiPositionChanged);
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

    public void onMapClicked(LatLng latLng) {
        Timber.tag("temi").d("receiving latlng");

        // move temi by click the map
        // TODO: 14 Dec 2021 filter by enum later for handling different modes

        // translate latlng to temi's xy
        // TODO: 14 Dec 2021 get angle specific to venue (360 - angle used in temiCoorToLatlng())
        Position position = temiManager.translateToTemiCoor(latLng.latitude, latLng.longitude, 90);
        temiManager.goToPosition(position);

        Timber.tag("temi").d("going to latlng: (%f, %f) by clicking on map", latLng.latitude, latLng.longitude);
        Timber.tag("temi").d("going to xy: (%f, %f) by clicking on map", position.getX(), position.getY());

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

    private void onRosDataChanged(RosData data) {
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

                Log.d(TAG, "tf q (w, x, y, z): " + q.getW() + ", " + q.getX() + ", " + q.getY() + ", " + q.getZ() + ", ");

                //                    double orientation = Math.atan2(2.0*(q.getY()*q.getZ() + q.getW()*q.getX()),
                //                        q.getW()*q.getW() - q.getX()*q.getX() - q.getY()*q.getY() + q.getZ()*q.getZ());
                double orientation = Math.atan2(2.0 * (q.getZ() * q.getW() + q.getX() * q.getY()),
                        -1 + (2.0 * (q.getW() * q.getW() + q.getX() * q.getX())));
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
            case "/external_pose":
                assert data.getMessage() instanceof Pose;

                Pose pose = (Pose) data.getMessage();
                double posX = pose.getPosition().getX();
                double posY = pose.getPosition().getY();
                org.ros.rosjava_geometry.Quaternion quat = new org.ros.rosjava_geometry.Quaternion(
                        pose.getOrientation().getW(),
                        pose.getOrientation().getX(),
                        pose.getOrientation().getY(),
                        pose.getOrientation().getZ()
                );

                Timber.tag("ros sub").d("external pose topic: " + data.getTopic().name + data.getTopic().type);
                Timber.tag("ros sub").d("external pose message: " + data.getMessage());
                Timber.tag("ros sub").d("external pose position: %f, %f", posX, posY);
                Timber.tag("ros sub").d("external pose orientation: %f %f %f %f",
                        quat.getW(), quat.getX(), quat.getY(), quat.getZ());

                break;
        }

        // print sth
//            Log.d(TAG, "general data topic: " + data.getTopic().name + data.getTopic().type);
//            Log.d(TAG, "general data message: " + data.getMessage());
    }

    private void onTemiPositionChanged(Position position) {

        if (position == null) return;

        // filter out repeating signals
        if (temiManager.getLastPosition() != null)
            if (temiManager.getLastPosition().getX() == position.getX() &&
                    temiManager.getLastPosition().getY() == position.getY() &&
                    temiManager.getLastPosition().getYaw() == position.getYaw())
                return;

//            Timber.tag("temi").d("position xy: (%f, %f), yaw: %f",
//                    position.getX(), position.getY(), position.getYaw());

        // diff between true north to y-axis (clockwise)
        // ICDC: 90 degrees shift
        LatLng latLng = temiManager.translateToLatlng(position.getX(), position.getY(), 90);

        // get degree in [0, 360]
        double degree = temiManager.getYawInDegree(position.getYaw());

//            Timber.tag("temi").d("position latlng: (%f, %f), yaw in degree: %f",
//                    latLng.latitude, latLng.longitude, degree);

        // update location & orientation
        IndoorLocation location = new IndoorLocation("TEMI",
                latLng.latitude, latLng.longitude, "3F", "31742af5bc8446acad14e0c053ae468a", System.currentTimeMillis());
        location.setBearing((float) degree);

        // pass it to UI/mapxus manager
        temiListener.OnTemiLocationChanged(location);

        // publish data to ROS server
//            // passing temi's xy
//            rosDomain.publishData(new TemiData(position.getX(), position.getY(), (float) degree));
        // passing latlng
        rosDomain.publishData(new TemiData(latLng.latitude, latLng.longitude, degree));

        // update last position in temi manager
        temiManager.setLastPosition(position);
    }

}
