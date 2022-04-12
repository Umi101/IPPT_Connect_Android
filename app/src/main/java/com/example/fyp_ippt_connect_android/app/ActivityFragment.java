package com.example.fyp_ippt_connect_android.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fyp_ippt_connect_android.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.data.BarEntry;

import com.github.mikephil.charting.data.Entry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ActivityFragment extends ConnectedPeripheralFragment{
    // Log
    private final static String TAG = ActivityFragment.class.getSimpleName();

    // UI
    private Spinner machineList = null;
    private String exercise = "Push-up";
    private ZoomType currentZoom = ZoomType.ZOOM_ALL;
    private DateGraph mDateGraph = null;
    private LinearLayout mGraphZoomSelector = null;
    private CombinedChart mChart = null;
    private View mFragmentView = null;
    private final View.OnClickListener onZoomClick = v -> {
        switch (v.getId()) {
            case R.id.allbutton:
                currentZoom = ZoomType.ZOOM_ALL;
                break;
            case R.id.lastweekbutton:
                currentZoom = ZoomType.ZOOM_WEEK;
                break;
            case R.id.lastmonthbutton:
                currentZoom = ZoomType.ZOOM_MONTH;
                break;
            case R.id.lastyearbutton:
                currentZoom = ZoomType.ZOOM_YEAR;
                break;
        }
        mDateGraph.setZoom(currentZoom);
    };

    // Data
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private final OnItemSelectedListener onItemSelectedList = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
            exercise = parent.getItemAtPosition(i).toString();
            drawGraph();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };




    // Region Fragment Lifecycle
    public static ActivityFragment newInstance(@Nullable String singlePeripheralIdentifier){
        ActivityFragment fragment= new ActivityFragment();
        fragment.setArguments(createFragmentArgs(singlePeripheralIdentifier));
        return fragment;
    }

    public ActivityFragment(){
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        // Update ActionBar
        //setActionBarTitle(R.string.activity_tab_title);

        mFragmentView = view;
        machineList = view.findViewById(R.id.filterGraphMachine);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.exercise_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        machineList.setAdapter(adapter);
        machineList.setOnItemSelectedListener(onItemSelectedList);


        Button allButton = view.findViewById(R.id.allbutton);
        Button lastyearButton = view.findViewById(R.id.lastyearbutton);
        Button lastmonthButton = view.findViewById(R.id.lastmonthbutton);
        Button lastweekButton = view.findViewById(R.id.lastweekbutton);


        allButton.setOnClickListener(onZoomClick);
        lastyearButton.setOnClickListener(onZoomClick);
        lastmonthButton.setOnClickListener(onZoomClick);
        lastweekButton.setOnClickListener(onZoomClick);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mGraphZoomSelector = view.findViewById(R.id.graphZoomSelector);
        mChart = view.findViewById(R.id.graphBarChart);
        mDateGraph = new DateGraph(getContext(), mChart, getResources().getText(R.string.countLabel).toString());

        refreshData();

    }

    @Override
    public void onStart() {
        super.onStart();

        refreshData();
    }

    // endregion

    private void drawGraph() {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();

        DatabaseReference userRef = mDatabase.child("Users").child(userId);
        DatabaseReference pushUpRecordRef = userRef.child("PushUpRecord");
        DatabaseReference sitUpRecordRef = userRef.child("SitUpRecord");


        mChart.clear();


        ArrayList<BarEntry> barVals = new ArrayList<>();
        ArrayList<Entry> lineVals = new ArrayList<>();

        if (exercise.equals(getString(R.string.pushUp))) {
            pushUpRecordRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChildren()) {
                        for (DataSnapshot pushUpSnapshot : snapshot.getChildren()) {
                            GraphData graphData = pushUpSnapshot.getValue(GraphData.class);
                            lineVals.add(new Entry((float) graphData.getX(), (float) graphData.getY1()));
                            barVals.add(new BarEntry((float) graphData.getX(), (float) graphData.getY2()));
                        }

                        mDateGraph.draw(barVals, lineVals);

                    } else {
                        mChart.clear();
                        mChart.invalidate();
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else if (exercise.equals(getString(R.string.sitUp))){
            sitUpRecordRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChildren()) {
                        for (DataSnapshot sitUpSnapshot : snapshot.getChildren()) {
                            GraphData graphData = sitUpSnapshot.getValue(GraphData.class);
                            lineVals.add(new Entry((float) graphData.getX(), (float) graphData.getY1()));
                            barVals.add(new BarEntry((float) graphData.getX(), (float) graphData.getY2()));
                        }

                        mDateGraph.draw(barVals, lineVals);
                        ;

                    } else {
                        mChart.clear();
                        mChart.invalidate();
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        CombinedChart.LayoutParams layoutParamsBar = mChart.getLayoutParams();
        if (mChart.getHeight() > mChart.getWidth())
            layoutParamsBar.height = mChart.getWidth();
        mChart.setLayoutParams(layoutParamsBar);

    }

    private void refreshData() {
        if (mFragmentView != null){
            drawGraph();
        }else{
            mChart.clear();
        }
    }


}
