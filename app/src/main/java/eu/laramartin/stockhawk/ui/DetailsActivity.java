package eu.laramartin.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

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
            Toast.makeText(this, "symbol: " + symbol, Toast.LENGTH_SHORT).show();
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
        List<String> weeklyClosingSharePrice = Arrays.asList(history.split("\n"));
//        Log.v(LOG_TAG, "historical data: " + history);
        List<Entry> chartEntries = new ArrayList<Entry>();
//        chartEntries.add(new Entry(1.0f, 1.0f));
//        chartEntries.add(new Entry(2.0f, 2.0f));
//        chartEntries.add(new Entry(3.0f, 3.0f));
//        chartEntries.add(new Entry(4.0f, 4.0f));
        //long dateOfToday = System.currentTimeMillis();
        int entry = 0;
        for (String pair : weeklyClosingSharePrice) {
//            Log.v(LOG_TAG, "weeklyClosingSharePrice: " + pair);
            List<String> dataPair = Arrays.asList(pair.split(", "));
//            Log.v(LOG_TAG, "dataPair: " + dataPair);
//            long dateOfQuote = Long.parseLong(dataPair.get(0));
//            long dateRelativeInMillis = dateOfQuote - dateOfToday;
//            float days = dateRelativeInMillis/(24*60*60*1000);
            chartEntries.add(new Entry(entry,
                    Float.parseFloat(dataPair.get(1))));
            entry++;
        }
        LineDataSet dataSet = new LineDataSet(chartEntries, "Label");
        dataSet.setColor(R.color.colorAccent);
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }

    private String convertTimeInMillisToDate(String dateInMillis) {
        Date date = new Date(Long.parseLong(dateInMillis) * 1000);
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        return formatter.format(date);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
