package com.example.fyp_ippt_connect_android.app;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_ippt_connect_android.R;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardFragment extends ConnectedPeripheralFragment{
    // Log
    private final static String TAG = LeaderboardFragment.class.getSimpleName();

    private String exercise = "Push-up";
    private RecyclerView mRecyclerView;
    private DatabaseReference mDatabase;
    private LeaderboardAdapter mAdapter;
    List<User> list;


    private final OnItemSelectedListener onItemSelectedList = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            exercise = adapterView.getItemAtPosition(i).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    // Region Fragment Lifecycle
    public static LeaderboardFragment newInstance(@Nullable String singlePeripheralIdentifier){
        LeaderboardFragment fragment = new LeaderboardFragment();
        fragment.setArguments(createFragmentArgs(singlePeripheralIdentifier));
        return fragment;
    }

    public LeaderboardFragment(){
        // Required empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leaderboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Spinner leaderboardFilter = view.findViewById(R.id.filterLeaderboard);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.exercise_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        leaderboardFilter.setAdapter(adapter);
        leaderboardFilter.setOnItemSelectedListener(onItemSelectedList);

        mRecyclerView = view.findViewById(R.id.leaderboardList);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        Drawable lineSeparatorDrawable = ContextCompat.getDrawable(getContext(), R.drawable.simpledivideritemdecoration);
        assert lineSeparatorDrawable != null;
        itemDecoration.setDrawable(lineSeparatorDrawable);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        mAdapter = new LeaderboardAdapter(getContext(), list, exercise);
        mRecyclerView.setAdapter(mAdapter);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    list.add(user);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onResume() {
        Log.d(TAG, "OnResume");

        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "OnPause");

        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "OnDestroy");

        super.onDestroy();
    }
}
