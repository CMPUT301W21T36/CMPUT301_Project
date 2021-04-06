package com.cmput301w21t36.phenocount;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class HistogramActivity extends AppCompatActivity {
    BarChart barchart;
    Experiment experiment;
    String type;
    BarData barData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_PhenoCount);
        setContentView(R.layout.activity_histogram);

        //get the experiment from intent
        experiment = (Experiment) getIntent().getSerializableExtra("experiment");

        barchart = (BarChart) findViewById(R.id.bargraph);

        barData = new BarData();

        ArrayList<Trial> trialList = experiment.getTrials();
        ArrayList<BarEntry> dataSet1 = new ArrayList<>();
        ArrayList<BarEntry> dataSet2 = new ArrayList<>();
        ArrayList<Entry> yValues = new ArrayList<>();
        ArrayList<String> datesList = new ArrayList<>();

        type = experiment.getExpType();

        for (int i = 0; i < trialList.size(); i++){
            datesList.add(trialList.get(i).getDate());
        }

        String[] dates = new String[datesList.size()];
        dates = datesList.toArray(dates);

        XAxis xAxis = barchart.getXAxis();
//        xAxis.setValueFormatter(new LabelFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(15);

        Description description = new Description();
        description.setTextColor(Color.BLACK);
        description.setTextSize(10);

        barchart.animateXY(3000, 3000);

        int i = 0;
        int j = 0;
        switch (type) {
            case "Binomial":
                final String[] labels = new String[] {"Success", "Fail"};
                description.setText("Binomial Trial Histogram");
                barchart.setDescription(description);

                int successCount = 0;
                int failCount = 0;
                for (i = 0; i < trialList.size(); i++) {
                    if (((Binomial) trialList.get(i)).getResult()) {
                        successCount++;
                    } else {
                        failCount++;
                    }
                }

                dataSet1.add(new BarEntry(0, successCount));
                dataSet2.add(new BarEntry(1, failCount));

                BarDataSet successDataSet = new BarDataSet(dataSet1, "Success");
                successDataSet.setColor(Color.GREEN);
                successDataSet.setValueTextSize(10);
                BarDataSet failDataSet = new BarDataSet(dataSet2, "Fails");
                failDataSet.setColor(Color.RED);
                failDataSet.setValueTextSize(10);

                xAxis.setGranularity(1);

                barData.addDataSet(successDataSet);
                barData.addDataSet(failDataSet);
                barchart.setData(barData);

                break;
            case "Count":
//                int count = 0;
//                String currentDate = null;
//                if (trialList.size() != 0) {
//                    currentDate = trialList.get(0).getDate();
//
//                    for (i = 0; i < trialList.size(); i++) {
//                        for (j = 0; j < trialList.size(); j++) {
//                            if (trialList.get(0).getDate().equals(currentDate)) {
//                                count++;
//                            }
//                        }
//
//                        yValues.add(new Entry(i, count));
//                        count = 0;
//                        if (currentDate.equals(trialList.get(i).getDate())) {
//                            i = trialList.size() + 1;
//                        } else {
//                            currentDate = trialList.get(i).getDate();
//                        }
//                    }
//
//                    LineDataSet set1 = new LineDataSet(yValues, "Data Set 1");
//                    set1.setLineWidth(10);
//
//                    LineData countData = new LineData();
//                    countData.addDataSet(set1);
//
//                    lineChart.setData(countData);
//                }

                //                trial = String.valueOf(((Count) trialList.get(position)).getCount());
                break;
            case "NonNegativeCount":
                //                trial = String.valueOf(((NonNegativeCount) trialList.get(position)).getValue());
                break;
            case "Measurement":
                int barRange = 10;
                int barMultiplier = 1;
                int num = 0;
                int numAdded = 0;
                float measurement = 0;

                ArrayList<String> measurementLabels = new ArrayList<>();
                while (numAdded != trialList.size()) {
                    for (j = 0; j < trialList.size(); j++) {
                        measurement = ((Measurement) trialList.get(j)).getMeasurement();
                        if ((measurement <= barRange*barMultiplier) && (measurement > barRange*(barMultiplier-1))) {
                            num++;
                        }
                    }
                    measurementLabels.add(String.valueOf(barRange*barMultiplier));
                    dataSet1.add(new BarEntry(barRange*barMultiplier, num));
                    if (num > 0) {
                        numAdded += num;
                    }
                    num = 0;
                    barMultiplier++;
                }

//                String[] measurementsList = new String[measurementLabels.size()];
//                measurementsList = measurementLabels.toArray(measurementsList);
//                xAxis.setValueFormatter(new LabelFormatter(measurementsList));
                xAxis.setGranularity(10);

                YAxis yLeftAxis;
                yLeftAxis = barchart.getAxisLeft();
                yLeftAxis.setGranularity(1);

                YAxis yRightAxis;
                yRightAxis = barchart.getAxisRight();
                yRightAxis.setGranularity(1);

                description.setText("Measurement Trial Histogram");
                barchart.setDescription(description);

                BarDataSet measurementDataSet = new BarDataSet(dataSet1, "Measurements");
                barData.addDataSet(measurementDataSet);
                barData.setValueTextSize(0);
                barData.setBarWidth(7);
                barchart.setData(barData);

                break;
        }
    }



    public class LabelFormatter implements IAxisValueFormatter {
        private final String[] mLabels;

        public LabelFormatter(String[] labels) {
            mLabels = labels;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mLabels[(int) value];
        }
    }
}