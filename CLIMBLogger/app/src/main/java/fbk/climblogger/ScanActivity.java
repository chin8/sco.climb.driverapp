package fbk.climblogger;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ScanActivity extends Activity {

    private final static String TAG = "ScanActivity_GIOVA";
    private Button mStartButton,mStopButton,mTagButton,mCheckInAllButton,mCheckOutAllButton,mCheckInBcastButton,mCheckOutBcastButton,mScheduleWUButton;//,mReleaseCmdButton;
    private Vibrator mVibrator;
    private int index = 0;
    private List<String> allowedChidren = new ArrayList<String>();
    private ArrayAdapter<ListView> adapter;
    private ClimbServiceInterface mClimbService;
    private Context mContext = null;
    private EditText mConsole = null;
    private long lastBroadcastMessageSentMillis = 0;
    private int wakeUP_year = 0, wakeUP_month = 0, wakeUP_day = 0, wakeUP_hour = 0, wakeUP_minute = 0;
    ExpandableListView expandableListView;
    MyExpandableListAdapter expandableListAdapter;
    List<ClimbNode> expandableListTitle;
    HashMap<ClimbNode, List<String>> expandableListDetail;

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.


    private void reconnectAfter(final String id, final int t){
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!mClimbService.connectMaster(id)) {
                    Log.w(TAG, "reconnect failed immediately. retry after " + t);
                    reconnectAfter(id, t);
                }
            }
        }, t);
    };

    private final BroadcastReceiver mClimbUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (ClimbService.ACTION_DEVICE_ADDED_TO_LIST.equals(action)) {

                if(intent.hasExtra(ClimbService.EXTRA_INT_ARRAY)){
                    updateDetailsExpandableListDetails(intent.getIntArrayExtra(ClimbService.EXTRA_INT_ARRAY));
                }else{
                    updateDetailsExpandableListDetails();
                }
                expandableListAdapter.notifyDataSetChanged();
                Log.i(TAG,"ACTION_DEVICE_ADDED_TO_LIST broadcast received");

            } else if (ClimbService.ACTION_DEVICE_REMOVED_FROM_LIST.equals(action)) {

                expandableListAdapter.notifyDataSetChanged();
                Log.i(TAG,"ACTION_DEVICE_REMOVED_FROM_LIST broadcast received");

            }else if (ClimbService.ACTION_METADATA_CHANGED.equals(action)) {

                if(intent.hasExtra(ClimbService.EXTRA_INT_ARRAY)){
                    updateDetailsExpandableListDetails(intent.getIntArrayExtra(ClimbService.EXTRA_INT_ARRAY));
                }else{
                    updateDetailsExpandableListDetails();
                }
                expandableListAdapter.notifyDataSetChanged();
                //log("ACTION_METADATA_CHANGED broadcast received");

            }else if (ClimbService.ACTION_DATALOG_ACTIVE.equals(action)) {

                log("Datalog active on file: "+intent.getStringExtra(ClimbService.EXTRA_STRING));
            }else if (ClimbService.ACTION_DATALOG_INACTIVE.equals(action)) {

                log("Datalog stopped");
            }else if (ClimbService.STATE_CONNECTED_TO_CLIMB_MASTER.equals(action)) {
                String id = intent.getStringExtra(ClimbService.INTENT_EXTRA_ID);
                Boolean success = intent.getBooleanExtra(ClimbService.INTENT_EXTRA_SUCCESS, true);

                Toast.makeText(getApplicationContext(),
                        "Connected with GATT? " + success,
                        Toast.LENGTH_SHORT).show();
                expandableListAdapter.notifyDataSetChanged();
                Log.i(TAG,"Connected with GATT? " + success);
                if (success) {
                    mClimbService.setNodeList(allowedChidren.toArray(new String[allowedChidren.size()]));
                } else {
                    if (!mClimbService.connectMaster(id)) {
                        Log.w(TAG, "reconnect failed immediately after connect failure. Retry in 5s");
                        reconnectAfter(id,5000);
                        // schedule a new connect try
                    };
                }
            }else if (ClimbService.STATE_DISCONNECTED_FROM_CLIMB_MASTER.equals(action)) {
                String id = intent.getStringExtra(ClimbService.INTENT_EXTRA_ID);
                Log.w(TAG,"Disconnected from GATT " + id);

                 Toast.makeText(getApplicationContext(),
                    "DISCONNECTED FROM GATT! " + id,
                    Toast.LENGTH_SHORT).show();
                expandableListAdapter.notifyDataSetChanged();

                if (!mClimbService.connectMaster(id)) {
                    Log.w(TAG, "Reconnect to " + id + " failed immediately. Retry in 5s");
                    reconnectAfter(id,5000);
                }
            }
            else if (ClimbService.ACTION_NODE_ALERT.equals(action)) {
                if (intent.hasExtra(ClimbService.EXTRA_BYTE_ARRAY)) {
                    byte[] nodeID = intent.getByteArrayExtra(ClimbService.EXTRA_BYTE_ARRAY);
                    String alertString = "ALERT ON NODE :" + String.format("%02X", nodeID[0]);
                    Toast.makeText(getApplicationContext(),
                            alertString,
                            Toast.LENGTH_LONG).show();

                    log(alertString);
                } else {

                }
            } else if (ClimbService.STATE_CHECKEDIN_CHILD.equals(action)) {
                if(intent.hasExtra(ClimbService.INTENT_EXTRA_ID))   {
                        String nodeID = intent.getStringExtra(ClimbService.INTENT_EXTRA_ID);
                        boolean success = intent.getBooleanExtra(ClimbService.INTENT_EXTRA_SUCCESS, true);
                        String msg = intent.getStringExtra(ClimbService.INTENT_EXTRA_MSG);
                        String alertString = "CHECKIN " + nodeID + " " + success + " " + msg;
                        Toast.makeText(getApplicationContext(),
                                alertString,
                                Toast.LENGTH_LONG).show();

                        log( alertString );
                    }else{

                    }
            } else if (ClimbService.STATE_CHECKEDOUT_CHILD.equals(action)) {
                if(intent.hasExtra(ClimbService.INTENT_EXTRA_ID))   {
                    String nodeID = intent.getStringExtra(ClimbService.INTENT_EXTRA_ID);
                    boolean success = intent.getBooleanExtra(ClimbService.INTENT_EXTRA_SUCCESS, true);
                    String msg = intent.getStringExtra(ClimbService.INTENT_EXTRA_MSG);
                    String alertString = "CHECKOUT " + nodeID + " " + success + " " + msg;
                    Toast.makeText(getApplicationContext(),
                            alertString,
                            Toast.LENGTH_LONG).show();

                    log( alertString );
                }else{

                }
            }
        }
    };

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override //Questa � usata per ritornare l'oggetto IBinder (c'� solo nei bound services)
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            //la prossima istruzione ritorna l'oggetto BluetoothLeService
            mClimbService = ((ClimbSimpleService.LocalBinder) service).getService();
            mClimbService.setContext(getApplicationContext());
            //IN QUESTO PUNTO RICHIEDI LA LISTA DI DISPOSITIVI INIZIALI PER INSERIRLA NELLA LISTVIEW
            expandableListAdapter = new MyExpandableListAdapter(mContext, mClimbService);
            expandableListView.setAdapter(expandableListAdapter);
/*
            //crea un adapter per gestire la listView
            adapter = new ArrayAdapter<ListView>(mContext, android.R.layout.simple_list_item_1, android.R.id.text1,climbNodeList);

            // Assign adapter to ListView
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
*/
            Log.i(TAG, "Service connected!");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mClimbService = null;
            Log.i(TAG,"Service disconnected!");
        }
    };

    View.OnClickListener startButtonHandler = new View.OnClickListener(){
        public void onClick(View v) {
            if(mClimbService != null){
                if (mClimbService.init()) {
                    mVibrator.vibrate(ConfigVals.vibrationTimeout);
                    Log.i(TAG, "Start scan with data logging!");
                    log("Start scan with data logging command sent!");
                } else {
                    Log.i(TAG, "Failed to start scan!");
                    log("Failed to start scan!");
                }
            }else{
                Log.i(TAG, "Start scan not sent: service not available!");
                log("Start scan not sent!");
            }

        }
    };

    View.OnClickListener stopButtonHandler = new View.OnClickListener(){
        public void onClick(View v) {

            /* NOT SUPPORTED through iface
            if(mClimbService != null){
                mVibrator.vibrate(ConfigVals.vibrationTimeout);
                Log.i(TAG, java.util.Arrays.toString(mClimbService.getLogFiles()));
                mClimbService.StopMonitoring();
                Log.i(TAG, "Stop scan!");
                log("Stop scan command sent!");
            }else{
                Log.i(TAG, "Stop scan not sent!");
                log("Stop scan not sent!");
            }
            */

        }
    };

    View.OnClickListener tagButtonHandler = new View.OnClickListener(){
        public void onClick(View v) {

            /* NOT SUPPORTED through iface
            if(mClimbService != null){
                if(mClimbService.insertTag("Manually_inserted_tag")){
                    mVibrator.vibrate(ConfigVals.vibrationTimeout);
                    log("Tag Inserted!");
                }else{
                    log("Tag not inserted! Something went wrong in ClimbService!");
                }

            }else{
                Log.i(TAG, "Tag not inserted! mClimbService == null");
                log("Tag not inserted! mClimbService == null");
            }*/

        }
    };

    View.OnClickListener ckInAllButtonHandler = new View.OnClickListener(){
        public void onClick(View v) {

            if( (SystemClock.uptimeMillis() - lastBroadcastMessageSentMillis) > ConfigVals.consecutiveBroadcastMessageTimeout_ms) {
                if (mClimbService != null) {
                    ClimbServiceInterface.NodeState[] nss =  mClimbService.getNetworkState();
                    String[] ids = new String[nss.length];
                    for (int i = 0; i < nss.length; i++) {
                        ids[i] = nss[i].nodeID;
                    }
                    if (mClimbService.checkinChildren(ids)) {
                        mVibrator.vibrate(ConfigVals.vibrationTimeout);
                        lastBroadcastMessageSentMillis = SystemClock.uptimeMillis();
                    } else {
                        Log.i(TAG, "Check in all not sent!");
                        log("Check in all not sent!");
                    }
                } else {
                    Log.i(TAG, "Check in all not sent!");
                    log("Check in all not sent!");
                }
            }else{
                String alertString = "Wait a little";
                Toast.makeText(getApplicationContext(),
                        alertString,
                        Toast.LENGTH_LONG).show();
            }
        }
    };
    View.OnClickListener ckOutAllButtonHandler = new View.OnClickListener(){
        public void onClick(View v) {

            if ((SystemClock.uptimeMillis() - lastBroadcastMessageSentMillis) > ConfigVals.consecutiveBroadcastMessageTimeout_ms) {
                if (mClimbService != null) {
                    ClimbServiceInterface.NodeState[] nss =  mClimbService.getNetworkState();
                    String[] ids = new String[nss.length];
                    for (int i = 0; i < nss.length; i++) {
                        ids[i] = nss[i].nodeID;
                    }
                    if (mClimbService.checkoutChildren(ids)) {
                        mVibrator.vibrate(ConfigVals.vibrationTimeout);
                        lastBroadcastMessageSentMillis = SystemClock.uptimeMillis();
                    } else {
                        Log.i(TAG, "Check out all not sent!");
                        log("Check out all not sent!");
                    }
                } else {
                    Log.i(TAG, "Check out all not sent!");
                    log("Check out all not sent!");
                }

            }else{
                String alertString = "Wait a little";
                Toast.makeText(getApplicationContext(),
                        alertString,
                        Toast.LENGTH_LONG).show();
            }
        }
    };
    View.OnClickListener ckInBcastButtonHandler = new View.OnClickListener(){
        public void onClick(View v) {

            /* NOT SUPPORTED through iface
            if( (SystemClock.uptimeMillis() - lastBroadcastMessageSentMillis) > ConfigVals.consecutiveBroadcastMessageTimeout_ms) {
                if (mClimbService != null) {
                    if (mClimbService.SendCheckInAllCmd()) {
                        mVibrator.vibrate(ConfigVals.vibrationTimeout);
                        lastBroadcastMessageSentMillis = SystemClock.uptimeMillis();
                    } else {
                        Log.i(TAG, "Check in all not sent!");
                        log("Check in all not sent!");
                    }
                } else {
                    Log.i(TAG, "Check in all not sent!");
                    log("Check in all not sent!");
                }
            }else{
                String alertString = "Wait a little";
                Toast.makeText(getApplicationContext(),
                        alertString,
                        Toast.LENGTH_LONG).show();
            }
            */
        }
    };
    View.OnClickListener ckOutBcastButtonHandler = new View.OnClickListener(){
        public void onClick(View v) {

            /* NOT SUPPORTED through iface
            if ((SystemClock.uptimeMillis() - lastBroadcastMessageSentMillis) > ConfigVals.consecutiveBroadcastMessageTimeout_ms) {
                if (mClimbService != null) {
                    if (mClimbService.SendCheckOutAllCmd()) {
                        mVibrator.vibrate(ConfigVals.vibrationTimeout);
                        lastBroadcastMessageSentMillis = SystemClock.uptimeMillis();
                    } else {
                        Log.i(TAG, "Check out all not sent!");
                        log("Check out all not sent!");
                    }
                } else {
                    Log.i(TAG, "Check out all not sent!");
                    log("Check out all not sent!");
                }

            }else{
                String alertString = "Wait a little";
                Toast.makeText(getApplicationContext(),
                        alertString,
                        Toast.LENGTH_LONG).show();
            }
            */
        }
    };
    View.OnClickListener scheduleWUButtonHandler = new View.OnClickListener(){
        public void onClick(View v) {

            mVibrator.vibrate(ConfigVals.vibrationTimeout);

            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getFragmentManager(), "datePicker");

/*            if( (SystemClock.uptimeMillis() - lastBroadcastMessageSentMillis) > ConfigVals.consecutiveBroadcastMessageTimeout_ms) {
                if (mClimbService != null) {
                    if (mClimbService.ScheduleWakeUpCmd()) {
                        mVibrator.vibrate(ConfigVals.vibrationTimeout);
                        lastBroadcastMessageSentMillis = SystemClock.uptimeMillis();
                    } else {
                        Log.i(TAG, "schedule wake up not sent!");
                        log("schedule wake up not sent!");
                    }
                } else {
                    Log.i(TAG, "schedule wake up not sent!");
                    log("schedule wake up not sent!");
                }
            }else{
                String alertString = "Wait a little";
                Toast.makeText(getApplicationContext(),
                        alertString,
                        Toast.LENGTH_LONG).show();
            }*/
        }
    };

    public void setWakeUpDate(int year, int month, int day){
        wakeUP_year = year;
        wakeUP_month = month;
        wakeUP_day = day;
    }

    public void setWakeUpHour(int hour, int minute){
        wakeUP_hour = hour;
        wakeUP_minute = minute;
    }

    public void sendWakeUpCMD(){

        /* NOT SUPPORTED through iface
        if( (SystemClock.uptimeMillis() - lastBroadcastMessageSentMillis) > ConfigVals.consecutiveBroadcastMessageTimeout_ms) {
            if (mClimbService != null) {

                GregorianCalendar wakeUpDate = new GregorianCalendar(wakeUP_year,wakeUP_month,wakeUP_day,wakeUP_hour,wakeUP_minute);
                GregorianCalendar nowDate = new GregorianCalendar(Locale.ITALY);

                long wakeUpDate_millis = wakeUpDate.getTimeInMillis();
                long nowDate_millis = nowDate.getTimeInMillis();

                if( wakeUpDate_millis > nowDate_millis ) {
                    long diff_Sec = (wakeUpDate_millis-nowDate_millis)/1000;
                    if(diff_Sec < ConfigVals.MAX_WAKE_UP_DELAY_SEC && diff_Sec == (int)diff_Sec){

                        if (mClimbService.ScheduleWakeUpCmd((int) diff_Sec)) {
                            mVibrator.vibrate(ConfigVals.vibrationTimeout);
                            lastBroadcastMessageSentMillis = SystemClock.uptimeMillis();
                            Log.i(TAG, "schedule wake up sent!");
                            return;
                        } else {
                            Log.i(TAG, "schedule wake up not sent!");
                            log("schedule wake up not sent!");
                        }
                        return;

                    }
                }

                Toast.makeText(getApplicationContext(),
                        "Date not accepted...",
                        Toast.LENGTH_SHORT).show();
                mVibrator.vibrate(ConfigVals.vibrationTimeout*2);

            } else {
                Log.i(TAG, "schedule wake up not sent!");
                log("schedule wake up not sent!");
            }
        }else{
            String alertString = "Wait a little";
            Toast.makeText(getApplicationContext(),
                    alertString,
                    Toast.LENGTH_LONG).show();
        }
        */

    }

/*    View.OnClickListener releaseCmdButtonHandler = new View.OnClickListener(){
        public void onClick(View v) {

            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getFragmentManager(), "datePicker");
        }

    };*/

    ExpandableListView.OnGroupExpandListener mOnGroupExpandListener = new ExpandableListView.OnGroupExpandListener() {
        @Override
        public void onGroupExpand(int groupPosition) {
            Log.i(TAG, "Group expanded, position: " + groupPosition);
            String clickedNode = expandableListAdapter.getMasters()[groupPosition];
            if (! mClimbService.connectMaster(clickedNode)) {
                Log.w(TAG, "connect failed immediately to " + clickedNode);
            }
            mVibrator.vibrate(ConfigVals.vibrationTimeout);
        }
    };

    ExpandableListView.OnGroupCollapseListener mOnGroupCollapseListener = new ExpandableListView.OnGroupCollapseListener() {
        @Override
        public void onGroupCollapse(int groupPosition) {
            mClimbService.disconnectMaster();
            mVibrator.vibrate(ConfigVals.vibrationTimeout);
        }
    };

    ExpandableListView.OnChildClickListener mOnChildClickListener = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v,
                                    int groupPosition, int childPosition, long id) {

            ClimbServiceInterface.NodeState monitoredChild = (ClimbServiceInterface.NodeState) expandableListAdapter.getChild(groupPosition,childPosition);
            String actionString = "";
            String childID = monitoredChild.nodeID;
            switch (monitoredChild.state) {
                case 0:
                    if (!allowedChidren.contains(childID)) {
                        allowedChidren.add(childID);
                    }
                    mClimbService.setNodeList(allowedChidren.toArray(new String[allowedChidren.size()]));
                    actionString = "allowing " + childID;
                    break;
                case 1:
                    mClimbService.checkinChild(childID);
                    actionString = "checkin " + childID;
                    break;
                case 2:
                    mClimbService.checkoutChild(childID);
                    actionString = "checkout " + childID;
                    break;
                default:
            }
            mVibrator.vibrate(ConfigVals.vibrationTimeout);
            //Toast.makeText(getApplicationContext(),
            //        actionString,
            //        Toast.LENGTH_LONG).show();

            return true;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mContext = this.getApplicationContext();
        //mConsole = (EditText) findViewById(R.id.console_item);

        //listView = (ListView) findViewById(R.id.list);

        mStartButton = (Button) findViewById(R.id.buttonStart);
        mStartButton.setOnClickListener(startButtonHandler);


        mStopButton = (Button) findViewById(R.id.buttonStop);
        mStopButton.setOnClickListener(stopButtonHandler);

        mTagButton = (Button) findViewById(R.id.buttonTag);
        mTagButton.setOnClickListener(tagButtonHandler);

        mCheckInAllButton = (Button) findViewById(R.id.buttonCheckInAll);
        mCheckInAllButton.setOnClickListener(ckInAllButtonHandler);

        mCheckOutAllButton = (Button) findViewById(R.id.buttonCheckOutAll);
        mCheckOutAllButton.setOnClickListener(ckOutAllButtonHandler);

        mCheckInBcastButton = (Button) findViewById(R.id.buttonCheckInBcast);
        mCheckInBcastButton.setOnClickListener(ckInBcastButtonHandler);

        mCheckOutBcastButton = (Button) findViewById(R.id.buttonCheckOutBcast);
        mCheckOutBcastButton.setOnClickListener(ckOutBcastButtonHandler);

        mScheduleWUButton = (Button) findViewById(R.id.scheduleWakeUpAll);
        mScheduleWUButton.setOnClickListener(scheduleWUButtonHandler);

       // mReleaseCmdButton = (Button) findViewById(R.id.test);
        //mReleaseCmdButton.setOnClickListener(releaseCmdButtonHandler);

        mVibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        expandableListView = (ExpandableListView) findViewById(R.id.list);

        //Intent climbServiceIntent = new Intent(ScanActivity.this, ClimbService.class); //capire come si comporta nel caso in qui il servizio sia ancora in esecuzione in background
        //startService(climbServiceIntent);


        expandableListView.setOnGroupExpandListener( mOnGroupExpandListener );

        expandableListView.setOnGroupCollapseListener( mOnGroupCollapseListener );

        expandableListView.setOnChildClickListener( mOnChildClickListener );
    }


    @Override
    protected void onStart() {
        super.onStart();


    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "ScanActivity.onResume() called.");
        log("ScanActivity.onResume() called.");

        //AVVIA IL SERVIZIO E INIZIALIZZA IL BIND
        Intent climbServiceIntent = new Intent(ScanActivity.this, ClimbSimpleService.class); //capire come si comporta nel caso in qui il servizio sia ancora in esecuzione in background
        bindService(climbServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        registerReceiver(mClimbUpdateReceiver, makeClimbServiceIntentFilter());

    }

    @Override
    protected void onPause() {
        Log.i(TAG, "ScanActivity.onPause() called.");
        log("ScanActivity.onPause() called.");
        super.onPause();
        unregisterReceiver(mClimbUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ScanActivity.onDestroy() called.");
        log("ScanActivity.onDestroy() called.");

        //stopService(new Intent(ScanActivity.this, ClimbService.class));
        //climbNodeList = null;
        unbindService(mServiceConnection);
        //mClimbService = null;
        //expandableListDetail = null;
        //expandableListAdapter = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateDetailsExpandableListDetails(final int[] changedClimbNodeIndex){
    }

    private void updateDetailsExpandableListDetails(){
    }

    private void log(final String txt) {
        if(mConsole == null) return;

        final String timestamp = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConsole.setText(timestamp + " : " + txt + "\n" + mConsole.getText());
            }
        });
    }

    private static IntentFilter makeClimbServiceIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ClimbService.ACTION_DATALOG_ACTIVE);
        intentFilter.addAction(ClimbService.ACTION_DATALOG_INACTIVE);
        intentFilter.addAction(ClimbService.ACTION_METADATA_CHANGED);
        intentFilter.addAction(ClimbService.ACTION_DEVICE_ADDED_TO_LIST);
        intentFilter.addAction(ClimbService.STATE_CONNECTED_TO_CLIMB_MASTER);
        intentFilter.addAction(ClimbService.STATE_DISCONNECTED_FROM_CLIMB_MASTER);
        intentFilter.addAction(ClimbService.ACTION_DEVICE_REMOVED_FROM_LIST);
        intentFilter.addAction(ClimbService.ACTION_NODE_ALERT);
        intentFilter.addAction(ClimbService.STATE_CHECKEDIN_CHILD);
        intentFilter.addAction(ClimbService.STATE_CHECKEDOUT_CHILD);
        return intentFilter;
    }



}
