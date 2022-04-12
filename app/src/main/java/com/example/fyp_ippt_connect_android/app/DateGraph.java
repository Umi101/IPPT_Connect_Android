package com.example.fyp_ippt_connect_android.app;

import android.content.Context;
import android.graphics.Color;

import com.example.fyp_ippt_connect_android.R;
import com.example.fyp_ippt_connect_android.utils.DateConverter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class DateGraph {

    private CombinedChart mChart = null;
    private String mChartName = null;
    private Context mContext = null;

    public DateGraph(Context context, CombinedChart chart, String name){
        mChart = chart;
        mChartName = name;
        mChart.setDoubleTapToZoomEnabled(true);
        mChart.setHorizontalScrollBarEnabled(true);
        mChart.setVerticalScrollBarEnabled(true);
        mChart.setAutoScaleMinMaxEnabled(true);
        mChart.setDrawBorders(true);
        mChart.getDescription().setEnabled(false);
        mChart.setNoDataText(context.getString(R.string.no_chart_data_available));
        mChart.setExtraOffsets(0, 0, 0, 10);
        mChart.setDrawOrder(new DrawOrder[]{
                DrawOrder.BAR, DrawOrder.LINE
        });


        mContext = context;
        Legend l = mChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(ColorTemplate.getHoloBlue());
        xAxis.setTextSize(14);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        xAxis.setGranularity(1); // 1 jour
        xAxis.setValueFormatter(new ValueFormatter() {

            private final SimpleDateFormat mFormat = new SimpleDateFormat("dd-MMM"); // HH:mm:ss

            @Override
            public String getFormattedValue(float value) {
                mFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                Date tmpDate = new Date((long) DateConverter.nbMilliseconds(value)); // Convert days in milliseconds
                return mFormat.format(tmpDate);
            }
        });

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setGranularity((float) 1);
        leftAxis.setTextSize(12);

        //mChart.setFitBars(true);
        leftAxis.setAxisMinimum(0);

        mChart.getAxisRight().setEnabled(false);
    }

    public void draw(ArrayList<BarEntry> barEntries, ArrayList<Entry> lineEntries){
        mChart.clear();
        if (barEntries.isEmpty() && lineEntries.isEmpty()){
            return;
        }

        barEntries.sort(new EntryXComparator());
        lineEntries.sort(new EntryXComparator());

        CombinedData data = new CombinedData();

        data.setData(generateLineData(lineEntries));
        data.setData(generateBarData(barEntries));

        XAxis xAxis = mChart.getXAxis();
        xAxis.setAxisMaximum(data.getXMax() + 0.75f);
        xAxis.setAxisMinimum(data.getXMin() - 0.75f);


        // Set data
        mChart.setData(data);
        mChart.getAxisLeft().setAxisMinimum(0f);
        mChart.invalidate();

    }

    public CombinedChart getChart() { return mChart; }

    private LineData generateLineData(ArrayList<Entry> lineEntries){
        LineData d = new LineData();

        LineDataSet set = new LineDataSet(lineEntries, "Count with Conformance");
        set.setColor(Color.rgb(240, 238, 70));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(240, 238, 70));
        set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(240, 238, 70));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(240, 238, 70));

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return d;

    }

    private BarData generateBarData(ArrayList<BarEntry> barEntries){

        BarDataSet set = new BarDataSet(barEntries, "Total Count");
        set.setColor(Color.rgb(60, 220, 78));
        set.setValueTextColor(Color.rgb(60, 220, 78));
        set.setValueTextSize(12f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        // Create a data object with the datasets
        BarData d = new BarData(set);

        return d;
    }

    public void setZoom(ZoomType z){
        mChart.fitScreen();
        switch (z){
            case ZOOM_ALL:

                break;
            case ZOOM_WEEK:
                if (mChart.getData() != null) {
                    mChart.setVisibleXRangeMaximum((float) 7); // allow 20 values to be displayed at once on the x-axis, not more
                    mChart.moveViewToX(mChart.getData().getXMax() + 7); // set the left edge of the chart to x-index 10
                }
                break;
            case ZOOM_MONTH:
                if (mChart.getData() != null) {
                    mChart.setVisibleXRangeMaximum((float) 30); // allow 30 values to be displayed at once on the x-axis, not more
                    mChart.moveViewToX(mChart.getData().getXMax() - (float) 30); // set the left edge of the chart to x-index 10
                }
                break;
            case ZOOM_YEAR:
                if (mChart.getData() != null) {
                    mChart.setVisibleXRangeMaximum((float) 365); // allow 365 values to be displayed at once on the x-axis, not more
                    mChart.moveViewToX(mChart.getData().getXMax() - (float) 365); // set the left edge of the chart to x-index 10
                }
                break;
        }

        // refresh
        mChart.invalidate();
    }

    public void setGraphDescription(String description) {
        Description desc = new Description();
        desc.setText(description);
        desc.setTextSize(12);
        mChart.setDescription(desc);
    }


}
