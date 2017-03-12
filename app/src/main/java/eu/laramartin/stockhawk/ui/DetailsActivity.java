package eu.laramartin.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.common.collect.Lists;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.laramartin.stockhawk.R;
import eu.laramartin.stockhawk.data.Contract;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int STOCK_LOADER = 0;
    private String symbol;
    @BindView(R.id.chart)
    LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            symbol = intent.getExtras().getString(getString(R.string.intent_stock_symbol_key));
        }
        getSupportLoaderManager().initLoader(STOCK_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                Contract.Quote.makeUriForStock(symbol),
                new String[]{Contract.Quote.COLUMN_HISTORY},
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        String history = data.getString(data.getColumnIndex(Contract.Quote.COLUMN_HISTORY));
        List<String> weeklyClosingSharePrice = Lists.reverse(Arrays.asList(
                history.split(getString(R.string.symbol_line_break))));
        List<Entry> chartEntries = new ArrayList<>();
        for (String pair : weeklyClosingSharePrice) {
            List<String> dataPair = Arrays.asList(pair.split(getString(R.string.symbol_separator_pairs_data_history_data)));
            chartEntries.add(new Entry(Float.parseFloat(dataPair.get(0)),
                    Float.parseFloat(dataPair.get(1))));
        }
        LineDataSet dataSet = new LineDataSet(chartEntries, getString(R.string.chart_label));
        dataSet.setColor(R.color.colorAccent);
        LineData lineData = new LineData(dataSet);
        setXAxisFormat();
        chart.setData(lineData);
        chart.invalidate();
    }

    private void setXAxisFormat() {
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return convertTimeInMillisToDate(value);
            }
        });
        xAxis.setLabelRotationAngle(-45);
    }

    private String convertTimeInMillisToDate(float dateInMillis) {
        Date date = new Date((long)(dateInMillis));
        SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.date_american_format));
        return formatter.format(date);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //TODO

    }
}
