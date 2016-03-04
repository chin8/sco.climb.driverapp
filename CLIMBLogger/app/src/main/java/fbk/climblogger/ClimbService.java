package fbk.climblogger;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

public class ClimbService extends Service {

    public final static String ACTION_DEVICE_ADDED_TO_LIST = "fbk.climblogger.ClimbService.ACTION_DEVICE_ADDED_TO_LIST";
    public final static String ACTION_DEVICE_REMOVED_FROM_LIST = "fbk.climblogger.ClimbService.ACTION_DEVICE_REMOVED_FROM_LIST";
    public final static String ACTION_METADATA_CHANGED ="fbk.climblogger.ClimbService.ACTION_METADATA_CHANGED";
    public final static String ACTION_DATALOG_ACTIVE ="fbk.climblogger.ClimbService.ACTION_DATALOG_ACTIVE";
    public final static String ACTION_DATALOG_INACTIVE ="fbk.climblogger.ClimbService.ACTION_DATALOG_INACTIVE";
    public final static String ACTION_NODE_ALERT ="fbk.climblogger.ClimbService.ACTION_NODE_ALERT";
    public final static String ACTION_DATA_AVAILABLE ="fbk.climblogger.ClimbService.ACTION_DATA_AVAILABLE";
    public final static String STATE_CONNECTED_TO_CLIMB_MASTER = "fbk.climblogger.ClimbService.STATE_CONNECTED_TO_CLIMB_MASTER";
    public final static String STATE_DISCONNECTED_FROM_CLIMB_MASTER = "fbk.climblogger.ClimbService.STATE_DISCONNECTED_FROM_CLIMB_MASTER";

    private BluetoothDevice  mBTDevice = null;
    private BluetoothGattService mBTService = null;
    private BluetoothGattCharacteristic mCIPOCharacteristic = null, mPICOCharacteristic = null;
    final static private UUID mClimbServiceUuid = ConfigVals.Service.CLIMB;
    final static private UUID mCIPOCharacteristicUuid = ConfigVals.Characteristic.CIPO;
    final static private UUID mPICOCharacteristicUuid = ConfigVals.Characteristic.PICO;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt = null;

    public final static String EXTRA_STRING ="fbk.climblogger.ClimbService.EXTRA_STRING";
    public final static String EXTRA_INT_ARRAY ="fbk.climblogger.ClimbService.EXTRA_INT_ARRAY";
    public final static String EXTRA_BYTE_ARRAY ="fbk.climblogger.ClimbService.EXTRA_BYTE_ARRAY";

    private boolean nodeTimeOutEnabled = false;

    private final static int TEXAS_INSTRUMENTS_MANUFACTER_ID = 0x000D;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private boolean mScanning = false;
    private IBinder mBinder;
    private final String TAG = "ClimbService_GIOVA";
    private ArrayList<ClimbNode> nodeList;


    public String dirName, dirName2,update_name_log,file_name_log;
    File root;
    private File mFile = null;
    private FileWriter mFileWriter = null;
    private BufferedWriter mBufferedWriter = null;
    private boolean logEnabled;

    private int used_mtu = 23;
    private int index = 0;

    private Context appContext = null;

    private Handler mHandler = null;
    //private int masterNodeGATTConnectionState = BluetoothProfile.STATE_DISCONNECTED;

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        Log.d(TAG, "Sending broadcast, action = " + action);

        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final String extra_type, final int[] extra_data) {
        final Intent intent = new Intent(action);

        intent.putExtra(extra_type,extra_data);

        Log.d(TAG, "Sending broadcast, action = " + action + ". extra_type = " + extra_type + ". extra dimensions = " + extra_data.length);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final String extra_type, final byte[] extra_data) {
        final Intent intent = new Intent(action);

        intent.putExtra(extra_type,extra_data);

        Log.d(TAG, "Sending broadcast, action = " + action + ". extra_type = " + extra_type + ". extra dimensions = " + extra_data.length);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final String extra_type, String extra_string) {
        final Intent intent = new Intent(action);

        intent.putExtra(extra_type,extra_string);
        Log.d(TAG, "Sending broadcast, action = " + action + ". extra_type = " + extra_type);
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "ClimbService bound");

        return mBinder;
    }


    public class LocalBinder extends Binder {
        ClimbService getService() {
            return ClimbService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "ClimbService started");

        mBinder = new LocalBinder();

        if(nodeList == null)  nodeList = new ArrayList<ClimbNode>(); //crea la lista (vuota) che terrà conto dei dispositivi attualmente visibili
        if(mBluetoothManager == null) initialize();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ClimbService Stopped.");

    }

    @Override
    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + ": " + intent);

        return START_STICKY; // run until explicitly stopped.
    }

    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE); //qua era context.BLUETOOTH_SERVICE
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (mBluetoothLeScanner == null) {
            Log.e(TAG, "Unable to obtain a mBluetoothLeScanner.");
            return false;
        }



        return true;
    }


    public ArrayList getNodeList(){

        return nodeList;
    }

    //DEBUG
    /*
    public ArrayList addNode(){
        String name = "Nome " + index;
        nodeList.add(index, new ClimbNode(name, null, index*10));
        index ++;

        broadcastUpdate(ACTION_DEVICE_ADDED_TO_LIST);

        return nodeList;
    }
    //DEBUG
    public ArrayList removeNode(){
        index--;
        nodeList.remove(index);

        broadcastUpdate(ACTION_DEVICE_ADDED_TO_LIST);

        return nodeList;
    }
*/
    public int StartMonitoring(boolean enableDatalog){

        if(mBluetoothAdapter != null) {

            if(enableDatalog) {

                startDataLog();
                insertTag("Start_Monitoring");
                logEnabled = true;
                broadcastUpdate(ACTION_DATALOG_ACTIVE,EXTRA_STRING,file_name_log);
            }

            mScanning = true;
            //prepara il filtro che fa in modo di fare lo scan solo per device compatibili con climb (per ora filtra il nome)
            ScanFilter mScanFilter = new ScanFilter.Builder().setDeviceName(ConfigVals.CLIMB_MASTER_DEVICE_NAME).build();
            List<ScanFilter> mScanFilterList = new ArrayList<>();
            mScanFilterList.add(mScanFilter);
            mScanFilter = new ScanFilter.Builder().setDeviceName(ConfigVals.CLIMB_CHILD_DEVICE_NAME).build();
            mScanFilterList.add(mScanFilter);

            //imposta i settings di scan. vedere dentro la clase ScanSettings le opzioni disponibili
            ScanSettings mScanSettings = new ScanSettings.Builder()
                                             .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                                             .build();

            mBluetoothLeScanner.startScan(mScanFilterList, mScanSettings, mScanCallback);
            //mBluetoothLeScanner.startScan(mScanCallback);
            enableNodeTimeout();
        }else{
            Log.w(TAG, "mBluetoothAdapter == NULL!!");
        }
        //TODO: iniziare la ricerca ble
        //TODO: avviare il log
        return 1;
    }

    public int StopMonitoring(){
        if(mBluetoothAdapter != null) {
            mScanning = false;
            boolean disableTimeout = true;
            for(int i = 0; i < nodeList.size(); i++){
                if(nodeList.get(i).isMasterNode() && nodeList.get(i).getConnectionState()){ //se c'è almeno un nodo master connesso non disabilitare il timeout
                    disableTimeout = false;
                }
            }
            if(disableTimeout){
                disableNodeTimeout();
            }
            mBluetoothLeScanner.stopScan(mScanCallback);

            if(logEnabled){
                logEnabled = false;
                insertTag("Stop_Monitoring");
                stopDataLog();
                broadcastUpdate(ACTION_DATALOG_INACTIVE);
            }
        }else{
            Log.w(TAG, "mBluetoothAdapter == NULL!!");
        }
        //TODO: spegnere la ricerca ble
        //TODO: fermare il log
        return 1;
    }

    public boolean insertTag(String tagDescriptiveString){

            if (logEnabled) {
                if (mBufferedWriter != null) {
                    long nowMillis = SystemClock.uptimeMillis();
                    final String timestamp = new SimpleDateFormat("yyyy MM dd HH mm ss").format(new Date()); // salva il timestamp per il log
                    try {

                        try {
                            String tagString = "" + timestamp +
                                    " " + nowMillis +
                                    " " + mBTDevice.getAddress() +
                                    " LOCAL_DEVICE " +
                                    "TAG data " +
                                    tagDescriptiveString +
                                    "\n";

                            mBufferedWriter.write(tagString);

                        /*
                        mBufferedWriter.write(timestamp + " " + nowMillis);
                        mBufferedWriter.write(" " + mBTDevice.getAddress());
                        mBufferedWriter.write(" LOCAL_DEVICE "); //NAME
                        mBufferedWriter.write("TAG" + " ");

                        mBufferedWriter.write("\n");
                        mBufferedWriter.flush();
*/
                            return true;
                        } catch (NullPointerException e){
                            String tagString = "" + timestamp +
                                    " " + nowMillis +
                                    " NO_ADDRESS" +
                                    " LOCAL_DEVICE " +
                                    "TAG data " +
                                    tagDescriptiveString +
                                    "\n";

                            mBufferedWriter.write(tagString);
                            return true;
                        }
                    } catch (IOException e) {
                        Log.w(TAG, "Exception throwed while writing data to file.");
                        return false;
                    }
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }

//    public boolean SendCheckInAllCmd() {
//        if (mBluetoothAdapter != null) {
//
//            if (mBluetoothGatt == null) {
//                Toast.makeText(appContext,
//                        "Master NOT CONNECTED!",
//                        Toast.LENGTH_SHORT).show();
//                return false;
//            }
//
//            if(mPICOCharacteristic != null) {
//                byte[] gattData = {(byte) 0xFF, (byte) 0x01,(byte) 0x02};
//                String tempString = "Checking_in_all_nodes";
//                insertTag(tempString);
//                mPICOCharacteristic.setValue(gattData);
//                mBluetoothGatt.writeCharacteristic(mPICOCharacteristic);
//                return true;
//            }else{
//                Log.w(TAG, "mPICOCharacteristic not already discovered?");
//                return false;
//            }
//
//        }
//        return false;
//    }

    public boolean SendBroadcastStateChange(int stateToSend){
        if (mBluetoothAdapter != null) {

            if (mBluetoothGatt == null) {
                Toast.makeText(appContext,
                        "Master NOT CONNECTED!",
                        Toast.LENGTH_SHORT).show();
                return false;
            }

            if(mPICOCharacteristic != null) {
                byte[] gattData = {(byte) 0xFF,(byte) 0x01, (byte) stateToSend};
                String tempString;
                if(stateToSend == 0x00) {
                    tempString = "Setting_ByMyself_state_with_broadcast_command";
                }else if(stateToSend == 0x01) {
                    tempString = "Setting_Checking_state_with_broadcast_command";
                }else if(stateToSend == 0x02) {
                    tempString = "Setting_OnBoard_state_with_broadcast_command";
                }else if(stateToSend == 0x03) {
                    tempString = "Setting_Alert_state_with_broadcast_command";
                }else if(stateToSend == 0x04) {
                    tempString = "Setting_GoingToSleep_with_broadcast_command";
                }else{
                    tempString = "Setting_state:_"+stateToSend+"_with_broadcast_command";
                }
                insertTag(tempString);
                mPICOCharacteristic.setValue(gattData);
                boolean ret = mBluetoothGatt.writeCharacteristic(mPICOCharacteristic);
                //mBluetoothGatt.executeReliableWrite();
                return ret;
            }else{
                Log.w(TAG, "mPICOCharacteristic not already discovered?");
                return false;
            }

        }
        return false;
    }

    public boolean ScheduleWakeUpCmd(int timeout_sec){
        if (mBluetoothAdapter != null) {

            if (mBluetoothGatt == null) {
                Toast.makeText(appContext,
                        "Master NOT CONNECTED!",
                        Toast.LENGTH_SHORT).show();
                return false;
            }

            if(mPICOCharacteristic != null) {

                byte[] gattData = {(byte) 0xFF,(byte) 0x02, (byte)((timeout_sec>>16)&0xFF), (byte)((timeout_sec>>8)&0xFF), (byte)(timeout_sec&0xFF)};
                String tempString = "Sending_wake_up_schedule";
                insertTag(tempString);
                mPICOCharacteristic.setValue(gattData);
                boolean ret = mBluetoothGatt.writeCharacteristic(mPICOCharacteristic);
                //mBluetoothGatt.executeReliableWrite();
                return ret;
            }else{
                Log.w(TAG, "mPICOCharacteristic not already discovered?");
                return false;
            }

        }
        return false;
    }

    public boolean SendReleaseAllCmd(){
        if (mBluetoothAdapter != null) {

            if (mBluetoothGatt == null) {
                Toast.makeText(appContext,
                        "Master NOT CONNECTED!",
                        Toast.LENGTH_SHORT).show();
                return false;
            }

            if(mPICOCharacteristic != null) {
                byte[] gattData = {(byte) 0xFF, (byte) 0xFF};
                String tempString = "Accepting all nodes";
                insertTag(tempString);
                mPICOCharacteristic.setValue(gattData);
                boolean ret = mBluetoothGatt.writeCharacteristic(mPICOCharacteristic);
                //mBluetoothGatt.executeReliableWrite();
                return ret;
            }else{
                Log.w(TAG, "mPICOCharacteristic not already discovered?");
            }

        }
        return false;
    }

    public boolean isMonitoring(){
        return mScanning;
    }

    public void onNodeClick(int groupPosition, int childPosition){
        ClimbNode clickedNode = nodeList.get(groupPosition);
        if(clickedNode != null & clickedNode.isMasterNode()){ //do something only if it is a master node

            if(mBluetoothGatt == null && childPosition == -1){
                insertTag("Connecting_to_GATT");
                mBTDevice = clickedNode.getBleDevice();
                mBluetoothGatt = mBTDevice.connectGatt(this, false, mGattCallback);

                if(mBluetoothGatt == null){
                    Log.w(TAG, "connectGatt returned null!");
                }

                //masterNodeGATTConnectionState = BluetoothProfile.STATE_CONNECTING;

                Log.i(TAG, "Try to connect a CLIMB master node!");
                Toast.makeText(appContext,
                        "Connecting!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if(mBluetoothGatt != null && childPosition == -2){
                //mBTDevice = clickedNode.getBleDevice();
                insertTag("Disconnecting_from_GATT");

                mBluetoothGatt.close();
                mBluetoothGatt.disconnect();
                mBluetoothGatt = null;
                mBTService = null;
                mCIPOCharacteristic = null;
                mPICOCharacteristic = null;

                //mBTDevice = null;

                Log.i(TAG, "Climb master node disconnected!");
                Toast.makeText(appContext,
                        "Disconnecting...",
                        Toast.LENGTH_SHORT).show();

                if(mBTDevice != null) {
                    int index = isAlreadyInList(mBTDevice);
                    if (index >= 0) {
                        nodeList.get(index).setConnectionState(false);
                    } else {
                        Log.d(TAG, "Master not found in the list, CHECK!!!!");
                    }
                }
                broadcastUpdate(STATE_DISCONNECTED_FROM_CLIMB_MASTER);
                return;
            }

            if(childPosition >= 0){ //il click è stato fatto su uno dei child, fagli fare il check in
                MonitoredClimbNode monitoredChild = nodeList.get(groupPosition).getMonitoredClimbNodeList().get(childPosition);
                if(monitoredChild != null){
                    byte[] clickedChildID = monitoredChild.getNodeID();
                    byte clickedChildState = monitoredChild.getNodeState();

                    if(clickedChildState == 0) { //se lo stato è BY_MYSELF e voglio forzare il cambio di stato...
                        byte[] gattData = {clickedChildID[0], 1}; //assegna lo stato ON_BAORD e invia tutto al gatt
                        String tempString = "Check_node_" + clickedChildID[0];
                        insertTag(tempString);
                        mPICOCharacteristic.setValue(gattData);
                        mBluetoothGatt.writeCharacteristic(mPICOCharacteristic);
                        //mBluetoothGatt.executeReliableWrite();
                    }else if(clickedChildState == 1){ //se lo stato è CHECKING
                        byte[] gattData = {clickedChildID[0],  2}; //assegna lo stato ON_BAORD e invia tutto al gatt
                        String tempString = "Acceptiong_node_"+clickedChildID[0];
                        insertTag(tempString);
                        mPICOCharacteristic.setValue(gattData);
                        mBluetoothGatt.writeCharacteristic(mPICOCharacteristic);
                        //mBluetoothGatt.executeReliableWrite();
                    }else if(clickedChildState == 2) { //se lo stato è ON_BAORD
                        byte[] gattData = {clickedChildID[0],  0}; //assegna lo stato BY_MYSELF e invia tutto al gatt
                        String tempString = "Checking_out_node_"+clickedChildID[0];
                        insertTag(tempString);
                        mPICOCharacteristic.setValue(gattData);
                        mBluetoothGatt.writeCharacteristic(mPICOCharacteristic);
                        //mBluetoothGatt.executeReliableWrite();
                    }
                }
            }else{
            }
        }else{
            Log.i(TAG, "it isn't a climb master!!");
        }
    }

    public boolean setNewStateToCheckedNodes(int newState, boolean[] checked, int masterGroupPosition){
//TODO: GESTIRE IL CASO DI MOOOOOLTI NODI CHE NON CI STANNO DENTRO L'ARRAY temp_gattData
        MonitoredClimbNode monitoredChild;
        byte[] temp_gattData = new byte[used_mtu-3];
        int gattDataIndex = 0;
        if(checked.length == nodeList.get(masterGroupPosition).getMonitoredClimbNodeList().size()) {
            for(int i = 0; i < checked.length; i++) {
                if(checked[i]) { //the node has the checkbox selected
                    monitoredChild = nodeList.get(masterGroupPosition).getMonitoredClimbNodeList().get(i);
                    temp_gattData[gattDataIndex++] = monitoredChild.getNodeID()[0];
                    temp_gattData[gattDataIndex++] = (byte)newState;
                    String tempString;
                    if(newState == 0x00) {
                        tempString = "Setting_ByMyself_state_on_node_" + monitoredChild.getNodeID()[0];
                    }else if(newState == 0x01) {
                        tempString = "Setting_Checking_state_on_node_" + monitoredChild.getNodeID()[0];
                    }else if(newState == 0x02) {
                        tempString = "Setting_OnBoard_state_on_node_" + monitoredChild.getNodeID()[0];
                    }else if(newState == 0x03) {
                        tempString = "Setting_Alert_state_on_node_" + monitoredChild.getNodeID()[0];
                    }else if(newState == 0x04) {
                        tempString = "Setting_GoingToSleep_state_on_node_" + monitoredChild.getNodeID()[0];
                    }else{
                        tempString = "Setting_state:_"+newState+"_to_node_" + monitoredChild.getNodeID()[0];
                    }
                    insertTag(tempString);
                }
                if(gattDataIndex >= temp_gattData.length){
                    Toast.makeText(appContext,
                            "Not all nodes have been updated!",
                            Toast.LENGTH_SHORT).show();
                    break;
                }
            }

            if(gattDataIndex > 0) {
                byte[] gattData = Arrays.copyOf(temp_gattData, gattDataIndex);
                mPICOCharacteristic.setValue(gattData);
                Log.i(TAG, "writing characteristic!");
                boolean ret = mBluetoothGatt.writeCharacteristic(mPICOCharacteristic);
                //mBluetoothGatt.executeReliableWrite();
                return ret;
            }else{
                Log.w(TAG, "No selected nodes!");
                Toast.makeText(getApplicationContext(),
                            "Select at least one node!",
                            Toast.LENGTH_SHORT).show();
                return false;
            }

        } else{
            Log.w(TAG, "error on length of lists...check!!!");
            return false;
        }

    }
    // Device scan callback.

    public BluetoothDevice getBTDevice_ClimbMaster(){
        return mBTDevice;
    }

    ScanCallback mScanCallback = new ScanCallback() {

        @Override
        public void onBatchScanResults(List<ScanResult> results){

        }

        @Override
        public void onScanFailed(int errorCode){

        }

        @Override
        public void onScanResult(int callbackType, ScanResult result){
            Log.d(TAG, "onScanResult called!");
            if(callbackType == ScanSettings.CALLBACK_TYPE_ALL_MATCHES) {
                long nowMillis = SystemClock.uptimeMillis();
                //PRIMA DI TUTTO SALVA IL LOG
                if (logEnabled) {
                    if (mBufferedWriter != null) { // questo significa che il log � stato abilitato
                        final String timestamp = new SimpleDateFormat("yyyy MM dd HH mm ss").format(new Date()); // salva il timestamp per il log
                        String manufString = "";

                        byte[] manufacterData = result.getScanRecord().getManufacturerSpecificData(TEXAS_INSTRUMENTS_MANUFACTER_ID);
                        if(manufacterData != null) {
                            for (int i = 0; i < manufacterData.length; i++) {
                                manufString = manufString + String.format("%02X", manufacterData[i]);
                            }
                        }


                        try {
                            String logLine =  ""+ timestamp +
                                            " " + nowMillis +
                                            " " + result.getDevice().getAddress() +
                                            " " + result.getDevice().getName() +
                                            " ADV data" +
                                            " " + manufString +
                                            "\n" ;
                            //TODO: AGGIUNGERE RSSI
                            //mBufferedWriter.write(timestamp + " " + nowMillis);
                            //mBufferedWriter.write(" " + result.getDevice().getAddress()); //MAC ADDRESS
                            //mBufferedWriter.write(" " + result.getDevice().getName() + " "); //NAME
                            //mBufferedWriter.write(" " + "ADV data" + " ");
                            mBufferedWriter.write(logLine);

                        } catch (IOException e) {
                            Log.w(TAG, "Exception throwed while writing data to file.");
                        }
                    }
                }

                if (result.getDevice().getName().equals(ConfigVals.CLIMB_MASTER_DEVICE_NAME)) {  //AGGIUNGI alla lista SOLO I NODI MASTER!!!!
                    //POI AVVIA IL PROCESSO PER AGGIORNARE LA UI
                    int index = isAlreadyInList(result.getDevice());
                    if (index >= 0) {
                        Log.d(TAG, "Found device is already in database and it is at index: " + index);
                        updateScnMetadata(index, result, nowMillis);
                    } else {
                        Log.d(TAG, "New device found, adding it to database!");
                        addToList(result, nowMillis);
                    }
                }else if(result.getDevice().getName().equals(ConfigVals.CLIMB_CHILD_DEVICE_NAME)){ //check battery!!!
                    byte[] scanResponseData = result.getScanRecord().getManufacturerSpecificData(TEXAS_INSTRUMENTS_MANUFACTER_ID);
                    if (scanResponseData.length > 17) {
                        int batteryVoltage_mV =  (  ((((int) scanResponseData[16]) << 24) >>> 24)<<8) + ( (((int) scanResponseData[17]) << 24) >>> 24 );
                        if (batteryVoltage_mV < 2000){
                            Toast.makeText(appContext,
                                    "Battery low on node: 0x" + String.format("%02X",scanResponseData[0]),
                                    Toast.LENGTH_SHORT).show();

                        }
                    }

                }
 /*               //se trovo il master connetilo!
                if(result.getDevice().getName().equals(ConfigVals.CLIMB_MASTER_DEVICE_NAME) && mBluetoothGatt == null){

                    mBluetoothGatt = result.getDevice().connectGatt(appContext, false, mGattCallback);
                    Log.i(TAG, "Climb master has been found, try to connect it!");
                }

   */
               // }
            }
        }

    };

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                //masterNodeGATTConnectionState = BluetoothProfile.STATE_CONNECTED;
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.

                insertTag("Connected_to_GATT");
                mBluetoothGatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
                mBluetoothGatt.requestMtu(256);
                enableNodeTimeout();


            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

                Log.i(TAG, "Disconnected from GATT server. Status: " + status);
                if(mBTDevice != null) {
                    int index = isAlreadyInList(mBTDevice);
                    if (index >= 0) {
                        nodeList.get(index).setConnectionState(false);
                    } else {
                        Log.d(TAG, "Master not found in the list, CHECK!!!!");
                    }
                }
                //masterNodeGATTConnectionState = BluetoothProfile.STATE_DISCONNECTED;
                broadcastUpdate(STATE_DISCONNECTED_FROM_CLIMB_MASTER);
                mBluetoothGatt.disconnect();
                mBluetoothGatt.close();
                mBTDevice = null;
                mBluetoothGatt = null;
                mBTService = null;
                mCIPOCharacteristic = null;
                mPICOCharacteristic = null;
                insertTag("Disconnected_from_GATT");
                used_mtu = 23;
                //broadcastUpdate(intentAction);
                if(!mScanning){
                    disableNodeTimeout();
                }

                Log.i(TAG, "Attempting to reconnect");
                onNodeClick(0, -1);

            }else if (newState == BluetoothProfile.STATE_CONNECTING) {
               // masterNodeGATTConnectionState = BluetoothProfile.STATE_CONNECTING;
                Log.i(TAG, "Connecting to GATT server. Status: " + status);
                used_mtu = 23;
            }else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
               // masterNodeGATTConnectionState = BluetoothProfile.STATE_DISCONNECTING;
                Log.i(TAG, "Disconnecting from GATT server. Status: " + status);

                if(mBTDevice != null) {
                    int index = isAlreadyInList(mBTDevice);
                    if (index >= 0) {
                        nodeList.get(index).setConnectionState(false);
                    } else {
                        Log.d(TAG, "Master not found in list, CHECK!!!!");
                    }
                }

                mBluetoothGatt.disconnect();
                mBluetoothGatt.close();
                mBluetoothGatt = null;
                used_mtu = 23;

            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // cerca subito i servizi necessari (aggiorna il broadcast solo quando tutte le caratteristiche saranno salvate)
                Log.i(TAG, "onServicesDiscovered received: " + status);
                if(mBTService == null){
                    getClimbService();

//                    mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if(!mBluetoothGatt.requestMtu(35)){
//                                Log.w(TAG, "requestMtu returns FALSE!!!!");
//                            }
//                        }
//                    }, 1000);

                }else{

                }
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {

            Log.d(TAG, "onCharacteristicChanged called!");
            // if(callbackType == ScanSettings.CALLBACK_TYPE_ALL_MATCHES){
            long nowMillis = SystemClock.uptimeMillis();
            //PRIMA DI TUTTO SALVA IL LOG
            if (logEnabled) {
                if (mBufferedWriter != null) { // questo significa che il log � stato abilitato
                    final String timestamp = new SimpleDateFormat("yyyy MM dd HH mm ss").format(new Date()); // salva il timestamp per il log
                    String gattString = "";
                    byte[] gattData = characteristic.getValue();
                    for (int i = 0; i < gattData.length; i++) {
                        gattString = gattString + String.format("%02X", gattData[i]); //gatt DATA
                    }

                    try {
                        String logLine = "" + timestamp +
                                        " " + nowMillis +
                                        " " + mBTDevice.getAddress() +
                                        " " + mBTDevice.getName() +
                                        " GATT data " +
                                        " " + gattString +
                                        "\n";
                        mBufferedWriter.write(logLine);

                        //mBufferedWriter.write(timestamp + " " + nowMillis);
                        //mBufferedWriter.write(" " + mBTDevice.getAddress()); //MAC ADDRESS
                        //mBufferedWriter.write(" " + mBTDevice.getName() + " "); //NAME
                        //mBufferedWriter.write(" " + "GATT data ");



                        //mBufferedWriter.write("\n");
                        mBufferedWriter.flush();
                    } catch (IOException e) {
                        Log.w(TAG, "Exception throwed while writing data to file.");
                    }
                }
            }

            //POI AVVIA IL PROCESSO PER AGGIORNARE LA UI
            int index = isAlreadyInList(mBTDevice);
            if (index >= 0) {
                Log.d(TAG, "Found device is already in database and it is at index: " + index);
                updateGATTMetadata(index, characteristic.getValue(), nowMillis);
                if(nodeList != null){
//                    if(!nodeList.isEmpty()) {
//                        checkNodeStates(index);
//                    }
                }

            } else {
                Log.d(TAG, "New device found, it should be already in the list...verify!");
            }
            //broadcastUpdate(ACTION_METADATA_CHANGED);
        }

        @Override
        public void onMtuChanged (BluetoothGatt gatt, int mtu, int status){
            Log.i(TAG, "MTU changed. MTU = " + mtu);
            Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());
            if(status == 0){
                used_mtu = mtu;
                return;
            }
            return;
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status){

//            if(status == 0) {
//                gatt.abortReliableWrite();
//            } else {
//                gatt.executeReliableWrite();
//            }

            Log.i(TAG, "Characteristic write response with status: " + status);
            return;
        }

        @Override
        public void onReliableWriteCompleted (BluetoothGatt gatt, int status) {

            Log.i(TAG, "onReliableWriteCompleted with status: " + status);
            return;
        }

    };

    public void getClimbService() {
        Log.i(TAG, "Getting CLIMB Service");
        mBTService = mBluetoothGatt.getService(mClimbServiceUuid); //QUI VIENE CONTROLLATO CHE IL SERVER SU CUI SI E' CONNESSI ABBIA IL SERVIZIO ADATTO

        if(mBTService == null) {
            Log.i(TAG, "Could not get CLIMB Service");
            return;
        }
        else {
            Log.i(TAG, "CLIMB Service successfully retrieved");
            if(getCIPOCharacteristic() && getPICOCharacteristic()){
                int index = isAlreadyInList(mBTDevice);
                if (index >= 0) {
                    nodeList.get(index).setConnectionState(true);
                   // masterNodeGATTConnectionState = BluetoothProfile.STATE_CONNECTED;
                } else {
                    Log.d(TAG, "Master not found in the list, CHECK!!!!");
                }

                //mBluetoothGatt.beginReliableWrite();
                broadcastUpdate(STATE_CONNECTED_TO_CLIMB_MASTER);


                return;
            }
        }
    }

    private boolean getCIPOCharacteristic() {
        Log.i(TAG, "Getting CIPO characteristic");
        mCIPOCharacteristic = mBTService.getCharacteristic(mCIPOCharacteristicUuid);

        if(mCIPOCharacteristic == null) {
            Log.i(TAG, "Could not find CIPO Characteristic");
            return false;
        }
        else {
            Log.i(TAG, "CIPO characteristic retrieved properly");
            enableNotificationForCIPO();
            return true;
        }
    }

    private boolean getPICOCharacteristic() {
        Log.i(TAG, "Getting PICO characteristic");
        mPICOCharacteristic = mBTService.getCharacteristic(mPICOCharacteristicUuid);

        if(mPICOCharacteristic == null) {
            Log.i(TAG, "Could not find PICO Characteristic");
            return false;
        }
        else {
            Log.i(TAG, "PICO characteristic retrieved properly");
            return true;
        }
    }

    private void enableNotificationForCIPO() {

        Log.i(TAG, "Enabling notification on Android API for CIPO");
        if(mCIPOCharacteristic == null){
            Log.w(TAG, "mCIPOCharacteristic == null !!");
            return;
        }
        boolean success = mBluetoothGatt.setCharacteristicNotification(mCIPOCharacteristic, true);
        if(!success) {
            Log.i(TAG, "Enabling Android API notification failed!");
            return;
        }
        else{
            Log.i(TAG, "Notification enabled on Android API!");
        }

        BluetoothGattDescriptor descriptor = mCIPOCharacteristic.getDescriptor(ConfigVals.Descriptor.CHAR_CLIENT_CONFIG);
        if(descriptor != null) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
            Log.i(TAG, "Notification on remote device (CCCD) enabled.");
        }
        else {
            Log.i(TAG, "Could not get descriptor for characteristic! CCCD Notification are not enabled.");
        }
    }

    private int isAlreadyInList(BluetoothDevice device){//ScanResult targetNode){

        for(int i = 0; i < nodeList.size(); i++){
            if( nodeList.get(i).getAddress().equals(device.getAddress()) ){
                return i;
            }
        }

        return -1;
    }

    private boolean updateScnMetadata(int recordIndex, ScanResult targetNode, long nowMillis){

        //if(targetNode.getScanRecord().getManufacturerSpecificData(TEXAS_INSTRUMENTS_MANUFACTER_ID) != null) {
            //if (targetNode.getScanRecord().getManufacturerSpecificData(TEXAS_INSTRUMENTS_MANUFACTER_ID).length > 0) {
                nodeList.get(recordIndex).updateScnMetadata((byte)targetNode.getRssi(), targetNode.getScanRecord().getManufacturerSpecificData(TEXAS_INSTRUMENTS_MANUFACTER_ID));//, nowMillis);

                //broadcastUpdate(ACTION_METADATA_CHANGED, EXTRA_INT_ARRAY, new int[]{recordIndex}); //questa allega  al broadcast l'indice che è cambiato, per ora non serve
                broadcastUpdate(ACTION_METADATA_CHANGED);
                return true;
            //}
        //}
       // return false;
    }


    private boolean updateGATTMetadata(int recordIndex, byte[] cipo_data, long nowMillis){

//TODO: L'rssi viene letto tramite un'altra callback, quindi per ora non ne tengo conto (in ClimbNode.updateGATTMetadata l'rssi non viene toccato)
                nodeList.get(recordIndex).updateGATTMetadata(0, cipo_data);//, nowMillis);

                //broadcastUpdate(ACTION_METADATA_CHANGED, EXTRA_INT_ARRAY, new int[]{recordIndex}); //questa allega  al broadcast l'indice che è cambiato, per ora non serve
                broadcastUpdate(ACTION_METADATA_CHANGED);
                return true;

    }

    private boolean addToList(ScanResult targetNode, long nowMillis){
        BluetoothDevice device = targetNode.getDevice();
        boolean isMaster = device.getName().equals(ConfigVals.CLIMB_MASTER_DEVICE_NAME);
        ClimbNode newNode = new ClimbNode(device,
                                (byte)targetNode.getRssi() ,
                                targetNode.getScanRecord().getManufacturerSpecificData(TEXAS_INSTRUMENTS_MANUFACTER_ID),
                                isMaster);
                                //nowMillis);
        nodeList.add(newNode);
        //broadcastUpdate(ACTION_DEVICE_ADDED_TO_LIST, EXTRA_INT_ARRAY, new int[]{nodeList.indexOf(newNode)});
        broadcastUpdate(ACTION_DEVICE_ADDED_TO_LIST);
        Log.d(TAG, "Node added with index: " + nodeList.indexOf(newNode));
        return true;
    }

    private void checkNodeStates(int index){ //
        ClimbNode nodeUnderCheck = nodeList.get(index);

        byte[] temp_gattData = new byte[used_mtu-3];
        int gattDataIndex = 0;

        if(nodeUnderCheck != null) {
            ArrayList<MonitoredClimbNode> monitoredClimbNodeList = nodeUnderCheck.getMonitoredClimbNodeList();
            if(monitoredClimbNodeList != null) {
                for (int i = 0; i < monitoredClimbNodeList.size(); i++) { //non servirebbe controllare tutta la lista, basterebbe controllare i contatti inviati tramite l'ultimo messaggio gatt. Per ora faccio così per comodità
                    MonitoredClimbNode tempNode = monitoredClimbNodeList.get(i);
                    byte[] tempNodeID = tempNode.getNodeID();
                    byte tempNodeState = tempNode.getNodeState();
                    switch (tempNodeState){
                        case 0x00: //BY_MYSELF
                            if(!tempNode.getAutomaticStateChangeRequest()) { //se la richiesta è già stata fatta non ripeterla
                                if (isMyChild(tempNodeID)) {
                                    temp_gattData[gattDataIndex++] = tempNodeID[0];
                                    temp_gattData[gattDataIndex++] = 0x01;
                                    tempNode.setAutomaticStateChangeRequested(true); //evita di richiedere il cambio di stato continuamente
                                }
                            }
                            break;
                        case 0x01: //CHECKING
                            break;
                        case 0x02:  //ON_BOARD
                            break;
                        case 0x03: //ALERT
                            break;
                        default:    //INVALID STATE
                            break;
                    }

                    if( (gattDataIndex >= used_mtu-4 || i == monitoredClimbNodeList.size()-1) && gattDataIndex > 0){ //ATTENZIONE, SE SI METTONO IN CHECKING PIU DI 10 NODI SI RISCHIA DI RICHIAMARE writeCharacteristic più volte velocemente, e non è chiaro cosa possa succedere
                        if(mPICOCharacteristic != null && mBluetoothGatt != null) {
                            byte[] gattData = Arrays.copyOf(temp_gattData, gattDataIndex);
                            mPICOCharacteristic.setValue(gattData);
                            mBluetoothGatt.writeCharacteristic(mPICOCharacteristic);
                            //mBluetoothGatt.executeReliableWrite();
                            return;

                            //gattData = new byte[used_mtu-3];
                            //gattDataIndex = 0;
                        }else{
                            Log.w(TAG, "mPICOCharacteristic or mBluetoothGatt == null" );
                        }
                    }
                }
            }
        }
    }

    private boolean isMyChild(byte[] nodeID){
        return true;
    }

    private String startDataLog(){
        //TODO:se il file c'� gi� non crearlo, altrimenti creane un'altro
        if(mBufferedWriter == null){ // il file non � stato creato, quindi crealo
            if( get_log_num() == 1 ){
                return file_name_log;
            }
        } else{
            return null;
        }

        return null;
    }
    private String stopDataLog(){
        //TODO:chiudi il file
        if(mBufferedWriter != null){ // il file � presente
            try {
                mBufferedWriter.close();
                mFile = null;
                mBufferedWriter = null;
                file_name_log = null;
            }catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        return null;
    }

    private int get_log_num(){
        Log.i(TAG, "Initializing log file.");
        root = Environment.getExternalStorageDirectory();
        TimeZone tz = TimeZone.getTimeZone("Europe/Rome");

        Calendar rightNow = Calendar.getInstance(tz);// .getInstance();
        dirName=ConfigVals.folderName;
        //dirName2="CUPID_data/"+rightNow.get(Calendar.DAY_OF_MONTH)+"_"+ (rightNow.get(Calendar.MONTH) + 1) +"_"+ rightNow.get(Calendar.YEAR) +"/";

        try{
//	    		    dirName = "/sdcard/"+dirName2;
//	    			//dirName = Environment.getExternalStorageDirectory().getPath()+dirName2;
            File newFile = new File(dirName);
            newFile.mkdirs();
            Log.i(TAG, "Directory \""+ dirName + "\" created.");

        }
        catch(Exception e)
        {
            Log.w(TAG, "Exception creating folder");
            return -1;
        }

        if (root.canRead()) {

        }
        if (root.canWrite()){

            file_name_log = "log_"+rightNow.get(Calendar.DAY_OF_YEAR)+"_"+rightNow.get(Calendar.HOUR_OF_DAY)+"."+rightNow.get(Calendar.MINUTE)+"."+rightNow.get(Calendar.SECOND)+".txt";

            //mFile = new File(root,"log_data/"+file_name_log);
            mFile = new File(dirName,file_name_log);


            try {
                mFileWriter = new FileWriter(mFile);
                mBufferedWriter = new BufferedWriter(mFileWriter);
                Log.i(TAG, "Log file \""+ file_name_log + "\"created!");

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.w(TAG, "IOException in creating file");
            }

            //setOutFile(mBufferedWriter);
        }else{
            Log.w(TAG, "Can't write to file");
            return -1;
        }

        return 1;
    }

    private void enableNodeTimeout(){

        if(!nodeTimeOutEnabled) {
            nodeTimeOutEnabled = true;

            nodeTimeoutCheck();
        }
    }

    private void disableNodeTimeout(){
        nodeTimeOutEnabled = false;
    }

    private void nodeTimeoutCheck(){
        //controlla che il TimeoutCheck non sia stato disabilitato
        boolean nodeRemoved = false;
        if(nodeTimeOutEnabled) {
            //CONTROLLA I NODI DIRETTAMENTE VISIBILI DAL DISPOSITIVO ANDROID
            //long nowMillis = SystemClock.uptimeMillis();

            for(int i =  0; i < nodeList.size(); i++) {
                //long millisSinceLastScan = nowMillis - nodeList.get(i).getLastContactMillis();
                if( nodeList.get(i).getTimedOut() ){
                    if( !(nodeList.get(i).isMasterNode() &&  nodeList.get(i).getConnectionState() ) ) {
                        nodeList.remove(i);
                        nodeRemoved = true;
                    }
                }else {
                    nodeList.get(i).setTimedOut(true); //se al prossimo controllo è ancora true significa che non è mai stato visto nell'ultimo periodo, quindi eliminalo
                }
            }

            //CONTROLLA I NODI VISIBILI DAL NODO MASTER
            ClimbNode masterNode = null;
            //cerca il master
            for(int i = 0; i < nodeList.size(); i++) {
                if(nodeList.get(i).isMasterNode()){
                    masterNode = nodeList.get(i);
                    break; //una volta trovato il master interrompi, non gestisco più master...
                }
            }
            if(masterNode != null) {
                ArrayList<MonitoredClimbNode> childrenList = masterNode.getMonitoredClimbNodeList();
                byte[] gattData = new byte[20];
                int alertCounter = 0;
                int timedOutCounter = 0;
                for(int i = 0; i < childrenList.size(); i++) {
                    MonitoredClimbNode childNode = childrenList.get(i);

                    if(childNode.getNodeState() == 2 ) { //dai l'alert solo se il nodo è monitorato (è nello stato ON_BOARD)
                        //long millisSinceLastScan = nowMillis - childNode.getLastContactMillis();
                        if (childNode.getTimedOut()) {
                            childNode.setNodeState((byte) 3); //setta lo stato ALERT
                            byte [] childID = childNode.getNodeID();
                            broadcastUpdate(ACTION_NODE_ALERT, EXTRA_BYTE_ARRAY, childNode.getNodeID());

                            gattData[alertCounter*2] = childID[0];
                            //gattData[alertCounter*3 + 1] = childID[1];
                            gattData[alertCounter*2 + 1] = 3;

                            alertCounter++;
                        }
                        childNode.setTimedOut(true); //se al prossimo controllo è ancora true significa che non è mai stato visto nell'ultimo periodo, quindi eliminalo
                    }
                    if(childNode.getNodeState() == 0 || childNode.getNodeState() == 1 || childNode.getNodeState() == 4) { //Se il nodo è in BY MYSELF o CHECKING controllalo e semmai rimuovilo semplicemente dalla lista
                        //long millisSinceLastScan = nowMillis - childNode.getLastContactMillis();
                        if (childNode.getTimedOut()) {
                            timedOutCounter++;
                            childrenList.remove(i);
                        }else {
                            childNode.setTimedOut(true); //se al prossimo controllo è ancora true significa che non è mai stato visto nell'ultimo periodo, quindi eliminalo
                        }
                    }
                }
//                if(alertCounter > 0 && masterNodeGATTConnectionState == BluetoothProfile.STATE_CONNECTED){ //invia un pacchetto gatt solo se almeno un nodo è andato in timeout
//                    if(mBluetoothGatt != null && mPICOCharacteristic != null) {
//                        mPICOCharacteristic.setValue(gattData);
//                        mBluetoothGatt.writeCharacteristic(mPICOCharacteristic);
//                    }
//                }

                if(timedOutCounter > 0 || nodeRemoved){
                    broadcastUpdate(ACTION_DEVICE_REMOVED_FROM_LIST);
                }
            }
            // schedula un nuovo check
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    nodeTimeoutCheck();
                }
            }, ConfigVals.NODE_TIMEOUT);
        }
    }

    public void setHandler(Handler handler)
    {
        mHandler = handler;
    }

    public void setContext(Context context)
    {
        appContext = context;
    }
}
