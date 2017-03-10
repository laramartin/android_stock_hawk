package eu.laramartin.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import eu.laramartin.stockhawk.R;
import eu.laramartin.stockhawk.data.Contract;

import static com.github.mikephil.charting.charts.Chart.LOG_TAG;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int STOCK_LOADER = 0;
    private String symbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
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
        Log.v(LOG_TAG, "historical data: " + history);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
