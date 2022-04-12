package com.example.fyp_ippt_connect_android.app;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.fyp_ippt_connect_android.R;
import com.example.fyp_ippt_connect_android.ble.BleUtils;
import com.example.fyp_ippt_connect_android.ble.UartPacket;
import com.example.fyp_ippt_connect_android.ble.UartPacketManagerBase;
import com.example.fyp_ippt_connect_android.ble.central.BlePeripheralUart;
import com.example.fyp_ippt_connect_android.ble.central.UartPacketManager;
import com.example.fyp_ippt_connect_android.mqtt.MqttManager;
import com.example.fyp_ippt_connect_android.utils.DialogUtils;
import com.example.fyp_ippt_connect_android.utils.DateConverter;
import com.example.fyp_ippt_connect_android.utils.Keyboard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.ikovac.timepickerwithseconds.MyTimePickerDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExerciseFragment extends ConnectedPeripheralFragment implements UartPacketManagerBase.Listener {

    // Log
    private final static String TAG = ExerciseFragment.class.getSimpleName();

    private TextView mResultTextView;


    private String exercise = "Push-up";
    protected final Handler mMainHandler = new Handler(Looper.getMainLooper());
    protected UartPacketManagerBase mUartData;
    protected List<BlePeripheralUart> mBlePeripheralsUart = new ArrayList<>();
    private CheckBox autoTimeCheckBox = null;
    private TextView dateEdit = null;

    private final DatePickerDialog.OnDateSetListener dateSet = (view, year, month, day) -> {
        dateEdit.setText(DateConverter.dateToLocalDateStr(year, month, day, getContext()));
        Keyboard.hide(getContext(), dateEdit);
    };
    private TextView timeEdit = null;
    private final MyTimePickerDialog.OnTimeSetListener timeSet = (view, hourOfDay, minute, second) -> {
        Date date = DateConverter.timeToDate(hourOfDay, minute, second);
        timeEdit.setText(DateConverter.dateToLocalTimeStr(date, getContext()));
        Keyboard.hide(getContext(), timeEdit);
    };
    private final CompoundButton.OnCheckedChangeListener checkedAutoCheckBox = (buttonView, isChecked) ->{
        dateEdit.setEnabled(!isChecked);
        timeEdit.setEnabled(!isChecked);
        if (isChecked){
            dateEdit.setText(DateConverter.currentDate(getContext()));
            timeEdit.setText(DateConverter.currentDate(getContext()));
        }
    };

    private DatePickerDialogFragment mDateFrag = null;
    private TimePickerDialogFragment mTimeFrag = null;
    private final OnClickListener clickDateEdit = v -> {
        switch (v.getId()){
            case R.id.editDate:
                showDatePickerFragment();
                break;
            case R.id.editTime:
                showTimePicker(timeEdit);
                break;
        }
    };

    private List<Double> altitudeList = new ArrayList<>();
    private int countTotal  = 0;
    private int countAccurate = 0;
    private float height = 0f;
    private double pushUpBenchmark = 0.4;
    private double pullUpBenchmark = 0.4;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    protected MqttManager mMqttManager;

    private final OnItemSelectedListener onItemSelectedList = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            exercise = adapterView.getItemAtPosition(i).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    // region Fragment Lifecycle
    public static ExerciseFragment newInstance(@Nullable String singlePeripheralIdentifier) {
        ExerciseFragment fragment = new ExerciseFragment();
        fragment.setArguments(createFragmentArgs(singlePeripheralIdentifier));
        return fragment;
    }

    public ExerciseFragment(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        Spinner exerciseFilter = view.findViewById(R.id.filterExercise);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.exercise_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseFilter.setAdapter(adapter);
        exerciseFilter.setOnItemSelectedListener(onItemSelectedList);

        Button mStartButton = view.findViewById(R.id.startbutton);
        Button mStopButton = view.findViewById(R.id.stopbutton);
        mResultTextView = view.findViewById(R.id.resulttextView);

        autoTimeCheckBox = view.findViewById(R.id.autoTimeCheckBox);
        dateEdit = view.findViewById(R.id.editDate);
        timeEdit = view.findViewById(R.id.editTime);

        dateEdit.setOnClickListener(clickDateEdit);
        timeEdit.setOnClickListener(clickDateEdit);
        autoTimeCheckBox.setOnCheckedChangeListener(checkedAutoCheckBox);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Start", Toast.LENGTH_SHORT).show();
                // Setup Uart
                mResultTextView.setText("");
                setupUart();
            }
        });

        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Stop", Toast.LENGTH_SHORT).show();
                if (mBlePeripheralsUart != null){
                    for (BlePeripheralUart blePeripheralUart : mBlePeripheralsUart) {
                        blePeripheralUart.uartDisable();
                    }
                }
                getBenchMark();
                storeData();
                Log.d(TAG, "altitude: " + altitudeList);
                countNumber();
                Log.d(TAG, "Total Count: " + countTotal);
                Log.d(TAG, "Accurate Count: " + countAccurate);
                saveData();
                String text = String.format(Locale.getDefault(), "You have %d out of %d push-ups fulfill the standards!", countAccurate, countTotal);
                mResultTextView.setText(text);
                altitudeList.clear();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        dateEdit.setText(DateConverter.currentDate(getContext()));
        timeEdit.setText(DateConverter.currentTime(getContext()));
    }

    @Override
    public void onResume(){
        Log.d(TAG, "onResume");

        super.onResume();

        FragmentActivity activity = getActivity();
        if (activity != null){
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

    }

    @Override
    public void onPause(){
        Log.d(TAG, "onPause");

        super.onPause();

    }

    @Override
    public void onDestroy(){
        Log.d(TAG, "onDestroy");

        mUartData = null;

        // Disconnect mqtt
        if (mMqttManager != null) {
            mMqttManager.disconnect();
        }

        // Uart
        if (mBlePeripheralsUart != null) {
            for (BlePeripheralUart blePeripheralUart : mBlePeripheralsUart) {
                blePeripheralUart.uartDisable();
            }
            mBlePeripheralsUart.clear();
            mBlePeripheralsUart = null;
        }

        super.onDestroy();
    }

    // endregion

    private void showDatePickerFragment() {
        if (mDateFrag == null) {
            mDateFrag = DatePickerDialogFragment.newInstance(dateSet);
            mDateFrag.show(getActivity().getSupportFragmentManager().beginTransaction(), "dialog");
        } else {
            if (!mDateFrag.isVisible())
                mDateFrag.show(getActivity().getSupportFragmentManager().beginTransaction(), "dialog");
        }
    }

    private void showTimePicker(TextView timeTextView) {
        Calendar calendar = Calendar.getInstance();
        Date time = DateConverter.localTimeStrToDate(timeTextView.getText().toString(), getContext());
        calendar.setTime(time);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);

        if (timeTextView.getId() == R.id.editTime) {
            if (mTimeFrag == null) {
                mTimeFrag = TimePickerDialogFragment.newInstance(timeSet, hour, min, sec);
                mTimeFrag.show(getActivity().getSupportFragmentManager().beginTransaction(), "dialog_time");
            } else {
                if (!mTimeFrag.isVisible()) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("HOUR", hour);
                    bundle.putInt("MINUTE", min);
                    bundle.putInt("SECOND", sec);
                    mTimeFrag.setArguments(bundle);
                    mTimeFrag.show(getActivity().getSupportFragmentManager().beginTransaction(), "dialog_time");
                }
            }
        }
    }

    private static double doubleFromPacket(UartPacket packet){
        final byte[] bytes = packet.getData();
        final String dataString = BleUtils.bytesToText(bytes, true);
        final double dataFloat = Double.parseDouble(dataString);
        return dataFloat;
    }

    // region Uart
    protected void setupUart(){
        // Init
        Context context = getContext();
        if (context == null) {
            return;
        }
        mUartData = new UartPacketManager(context, this, true, mMqttManager);           // Note: mqttmanager should have been initialized previously
        //mBufferItemAdapter.setUartData(mUartData);

        if (!BlePeripheralUart.isUartInitialized(mBlePeripheral, mBlePeripheralsUart)){
            //updateUartReadyUI(false);
            BlePeripheralUart blePeripheralUart = new BlePeripheralUart(mBlePeripheral);
            mBlePeripheralsUart.add(blePeripheralUart);
            blePeripheralUart.uartEnable(mUartData, status -> mMainHandler.post(() -> {
                if(status == BluetoothGatt.GATT_SUCCESS){
                    // Done
                    Log.d(TAG, "Uart enabled");
                    //updateUartReadyUI(true);
                }
                else{
                    WeakReference<BlePeripheralUart> weakBlePeripheralUart = new WeakReference<>(blePeripheralUart);
                    Context context1 = getContext();
                    if (context1 != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context1);
                        AlertDialog dialog = builder.setMessage(R.string.uart_error_peripheralinit)
                                .setPositiveButton(android.R.string.ok, (dialogInterface, which) -> {
                                    BlePeripheralUart strongBlePeripheralUart = weakBlePeripheralUart.get();
                                    if (strongBlePeripheralUart != null) {
                                        strongBlePeripheralUart.disconnect();
                                    }
                                })
                                .show();
                        DialogUtils.keepDialogOnOrientationChanges(dialog);
                    }
                }
            }));
        }
    }

    //endregion

    private void getBenchMark(){

        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();

        DatabaseReference heightRef = mDatabase.child("Users").child(userId).child("height");

        heightRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    height = (long) snapshot.getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (height > 170 && height < 180){
            pushUpBenchmark = 0.4;
            pullUpBenchmark = 0.4;
        }

        if (height >= 180 && height < 190){
            pushUpBenchmark = 0.45;
            pullUpBenchmark = 0.45;
        }

    }

    private void storeData(){
        List<UartPacket> packets = mUartData.getPacketsCache();

        for (int i = 0; i < packets.size(); i++){
            UartPacket packet = packets.get(i);
            double altitude = doubleFromPacket(packet);
            altitudeList.add(altitude);
        }
    }

    private void countNumber() {

        if (altitudeList == null){
            Log.d(TAG, "no altitudelist");
        }else{
            Double peak = altitudeList.get(0);
            Double trough = 0d;
            for (int i = 1; i < altitudeList.size() - 1; i++) {
                if ((altitudeList.get(i) < altitudeList.get(i - 1)) && (altitudeList.get(i) < altitudeList.get(i + 1))) {
                    Double troughTemp = altitudeList.get(i);
                    if (exercise.equals(getString(R.string.pushUp))){
                        if (peak - troughTemp > 0.2) {
                            trough = troughTemp;
                            if (peak - trough > pushUpBenchmark) {
                                countAccurate++;
                            }
                            countTotal++;
                        }
                    }
                    else if(exercise.equals(getString(R.string.sitUp))){
                        if (peak - troughTemp > 0.2) {
                            trough = troughTemp;
                            if (peak - trough > pullUpBenchmark) {
                                countAccurate++;
                            }
                            countTotal++;
                        }
                    }

                }
                if ((altitudeList.get(i) > altitudeList.get(i - 1)) && (altitudeList.get(i) > altitudeList.get(i + 1))) {
                    if (altitudeList.get(i) - trough > 0.2) {
                        peak = altitudeList.get(i);
                    }
                }
            }
        }
    }

    private void saveData() {

        Date date;

        if (autoTimeCheckBox.isChecked()){
            date = new Date();
        }else{
            date = DateConverter.localDateTimeStrToDateTime(dateEdit.getText().toString(), timeEdit.getText().toString(), getContext());
        }

        String dateDB = DateConverter.dateTimeToDBDateStr(date);
        double x = DateConverter.nbDays(date);
        double y1 = (double) countAccurate;
        double y2 = (double) countTotal;

        GraphData graphData = new GraphData(x,y1,y2);

        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();

        DatabaseReference userRef = mDatabase.child("Users").child(userId);
        DatabaseReference pushUpTotalCountRef = userRef.child("pushUpTotalCount");
        DatabaseReference sitUpTotalCountRef = userRef.child("sitUpTotalCount");
        DatabaseReference pushUpRecordRef = userRef.child("PushUpRecord").child(dateDB);
        DatabaseReference sitUpRecordRef = userRef.child("sitUpRecord").child(dateDB);

        if (exercise.equals(getString(R.string.pushUp))){
            pushUpRecordRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        pushUpRecordRef.child("y1").setValue(ServerValue.increment(countAccurate));
                        pushUpRecordRef.child("y2").setValue((ServerValue.increment(countTotal)));
                    }else{
                        pushUpRecordRef.setValue(graphData);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            pushUpTotalCountRef.setValue(ServerValue.increment(countAccurate));
        }
        else if (exercise.equals(getString(R.string.sitUp))){
            pushUpRecordRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        sitUpRecordRef.child("y1").setValue(ServerValue.increment(countAccurate));
                        sitUpRecordRef.child("y2").setValue((ServerValue.increment(countTotal)));
                    }else{
                        sitUpRecordRef.setValue(graphData);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            sitUpTotalCountRef.setValue(ServerValue.increment(countAccurate));
        }



    }

    // region UartPacketManagerBase.Listener

    @Override
    public void onUartPacket(UartPacket packet) {

    }

    // endregion

}
