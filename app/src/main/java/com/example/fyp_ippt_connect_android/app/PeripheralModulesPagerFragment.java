package com.example.fyp_ippt_connect_android.app;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.example.fyp_ippt_connect_android.R;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class PeripheralModulesPagerFragment extends ConnectedPeripheralFragment{
    // log
    private final static String TAG = PeripheralModulesPagerFragment.class.getSimpleName();

    // Fragment parameters
    private final static int CONNECTIONMODE_SINGLEPERIPHERAL = 0;
    private final static int CONNECTIONMODE_MULTIPLEPERIPHERAL = 1;

    FragmentPagerItemAdapter pagerAdapter = null;
    ViewPager mViewPager = null;

    // Constants
    private final static int MODULE_EXERCISE = 0;
    private final static int MODULE_ACTIVITY = 1;
    private final static int MODULE_LEADERBOARD = 2;

    private static String peripheralIdentifier;

    //private PeripheralModulesPagerFragmentListener mListener;

    // region Fragment Lifecycle
    public static PeripheralModulesPagerFragment newInstance(@Nullable String singlePeripheralIdentifier) {      // if singlePeripheralIdentifier is null, uses multi-connect
        PeripheralModulesPagerFragment fragment = new PeripheralModulesPagerFragment();
        fragment.setArguments(createFragmentArgs(singlePeripheralIdentifier));
        peripheralIdentifier = singlePeripheralIdentifier;
        return fragment;
    }

    public PeripheralModulesPagerFragment() {
        // Required empty public constructor
    }

    /*
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mListener = (PeripheralModulesPagerFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement PeripheralModulesPagerFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pager_peripheralmodules, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setActionBarTitle(R.string.app_name);

        mViewPager = view.findViewById(R.id.peripheralmodules_viewpager);

        final String singlePeripheralIdentifier = mBlePeripheral != null ? mBlePeripheral.getIdentifier() : null;

        if (mViewPager.getAdapter() == null){
            pagerAdapter = new FragmentPagerItemAdapter(
                    getChildFragmentManager(), FragmentPagerItems.with(this.getContext())
                    .add(R.string.exercise_tab_title, ExerciseFragment.class, ExerciseFragment.createFragmentArgs(singlePeripheralIdentifier))
                    .add(R.string.activity_tab_title, ActivityFragment.class)
                    .add(R.string.leaderboard_tab_title, LeaderboardFragment.class)
                    .create());

            mViewPager.setAdapter(pagerAdapter);

            SmartTabLayout viewPagerTab = view.findViewById(R.id.peripheralmodules_pagertab);
            viewPagerTab.setViewPager(mViewPager);

            viewPagerTab.setOnPageChangeListener(new OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    Fragment frag1 = pagerAdapter.getPage(position);
                    if (frag1 != null)
                        frag1.onHiddenChanged(false);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

    }

    /*
    // region Listeners
    interface PeripheralModulesPagerFragmentListener {
        void startModuleFragment(Fragment fragment);
    }

     */

    public FragmentPagerItemAdapter getViewPagerAdapter(){
        return (FragmentPagerItemAdapter) ((ViewPager) getView().findViewById(R.id.peripheralmodules_viewpager)).getAdapter();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden){
            if (getViewPagerAdapter() != null){
                Fragment frag1;
                for (int i = 0; i < 3; i++){
                    frag1 = getViewPagerAdapter().getPage(i);
                    if (frag1 != null)
                        frag1.onHiddenChanged(false);
                }
            }
        }
    }
}
