package eu.laramartin.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

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
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.laramartin.stockhawk.R;
import eu.laramartin.stockhawk.data.Contract;
import eu.laramartin.stockhawk.data.PrefUtils;
import eu.laramartin.stockhawk.data.TextUtils;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int STOCK_LOADER = 0;
    private String symbol;
    @BindView(R.id.chart)
    LineChart chart;
    @BindView(R.id.text_details_stock_name)
    TextView textStockName;
    @BindView(R.id.text_details_price)
    TextView textStockPrice;
    @BindView(R.id.text_details_change)
    TextView textStockChange;
    @BindView(R.id.text_details_volume)
    TextView textStockVolume;
    @BindView(R.id.text_details_low)
    TextView textStockLow;
    @BindView(R.id.text_details_high)
    TextView textStockHigh;
    @BindView(R.id.text_details_open)
    TextView textStockOpen;
    @BindView(R.id.text_details_prev_close)
    TextView textStockPrevClose;
    @BindView(R.id.text_details_price_label)
    TextView textStockPriceLabel;
    @BindView(R.id.text_details_change_label)
    TextView textStockChangeLabel;
    @BindView(R.id.text_details_volume_label)
    TextView textStockVolumeLabel;
    @BindView(R.id.text_details_low_label)
    TextView textStockLowLabel;
    @BindView(R.id.text_details_high_label)
    TextView textStockHighLabel;
    @BindView(R.id.text_details_open_label)
    TextView textStockOpenLabel;
    @BindView(R.id.text_details_prev_close_label)
    TextView textStockPrevCloseLabel;

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
        chart.getXAxis().setTextColor(getResources().getColor(R.color.colorSoftWhite));
        chart.getAxisLeft().setTextColor(getResources().getColor(R.color.colorSoftWhite));
        chart.getAxisRight().setTextColor(getResources().getColor(R.color.colorSoftWhite));
        chart.getLegend().setEnabled(false);
        chart.getDescription().setEnabled(false);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                Contract.Quote.makeUriForStock(symbol),
                new String[]{Contract.Quote.COLUMN_SYMBOL,
                        Contract.Quote.COLUMN_PRICE,
                        Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
                        Contract.Quote.COLUMN_PERCENTAGE_CHANGE,
                        Contract.Quote.COLUMN_HISTORY,
                        Contract.Quote.COLUMN_VOLUME,
                        Contract.Quote.COLUMN_DAY_LOW,
                        Contract.Quote.COLUMN_DAY_HIGH,
                        Contract.Quote.COLUMN_DAY_OPEN,
                        Contract.Quote.COLUMN_PREV_CLOSE
                },
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        displayStockStats(data);
        displayHistoryStockChart(data);
    }

    private void displayHistoryStockChart(Cursor data) {
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
        LineDataSet dataSet = new LineDataSet(chartEntries, null);
        LineData lineData = new LineData(dataSet);
        setXAxisFormat();
        chart.setData(lineData);
        chart.invalidate();
        chart.setContentDescription(getString(R.string.content_description_chart,
                data.getString(data.getColumnIndex(Contract.Quote.COLUMN_SYMBOL))));
    }

    private void displayStockStats(Cursor data) {
        data.moveToFirst();
        textStockName.setText(data.getString(
                data.getColumnIndex(Contract.Quote.COLUMN_SYMBOL)));
        textStockName.setContentDescription(getString(R.string.content_description_stock,
                data.getString(data.getColumnIndex(Contract.Quote.COLUMN_SYMBOL))));
        textStockPrice.setText(data.getString(
                data.getColumnIndex(Contract.Quote.COLUMN_PRICE)));
        textStockPrice.setContentDescription(getString(R.string.content_description_share_price,
                data.getString(data.getColumnIndex(Contract.Quote.COLUMN_PRICE))));
        textStockPriceLabel.setContentDescription(getString(R.string.content_description_share_price,
                data.getString(data.getColumnIndex(Contract.Quote.COLUMN_PRICE))));
        String change;
        if (PrefUtils.getDisplayMode(this)
                .equals(this.getString(R.string.pref_display_mode_absolute_key))) {
            change = TextUtils.setDollarFormatWithPlus(this).format(
                    data.getFloat(
                            data.getColumnIndex(Contract.Quote.COLUMN_ABSOLUTE_CHANGE)));
            textStockChange.setText(change);
            textStockChange.setContentDescription(getString(R.string.content_description_share_price_change_absolute,
                    change));
            textStockChangeLabel.setContentDescription(getString(R.string.content_description_share_price_change_absolute,
                    change));
        } else {
            float percentage = data.getFloat(
                    data.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE));
            change = TextUtils.setPercentageFormat(this).format(percentage / 100);
            textStockChange.setText(change);
            textStockChange.setContentDescription(getString(R.string.content_description_share_price_change_percentage,
                    change));
            textStockChangeLabel.setContentDescription(getString(R.string.content_description_share_price_change_percentage,
                    change));
        }
        try {
            float volume = Float.parseFloat(data.getString(
                    data.getColumnIndex(Contract.Quote.COLUMN_VOLUME)));
            volume /= 1000000;
            textStockVolume.setText(String.format(Locale.US, "%.2fM", volume));
            textStockVolume.setContentDescription(getString(R.string.content_description_stock_volume,
                    volume));
            textStockVolumeLabel.setContentDescription(
                    getString(R.string.content_description_stock_volume, volume));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        textStockLow.setText(data.getString(
                data.getColumnIndex(Contract.Quote.COLUMN_DAY_LOW)));
        textStockLow.setContentDescription(getString(R.string.content_description_day_low,
                data.getString(data.getColumnIndex(Contract.Quote.COLUMN_DAY_LOW))));
        textStockLowLabel.setContentDescription(getString(R.string.content_description_day_low,
                data.getString(data.getColumnIndex(Contract.Quote.COLUMN_DAY_LOW))));
        textStockHigh.setText(data.getString(
                data.getColumnIndex(Contract.Quote.COLUMN_DAY_HIGH)));
        textStockHigh.setContentDescription(getString(R.string.content_description_day_high,
                data.getString(data.getColumnIndex(Contract.Quote.COLUMN_DAY_HIGH))));
        textStockHighLabel.setContentDescription(getString(R.string.content_description_day_high,
                data.getString(data.getColumnIndex(Contract.Quote.COLUMN_DAY_HIGH))));
        textStockOpen.setText(data.getString(
                data.getColumnIndex(Contract.Quote.COLUMN_DAY_OPEN)));
        textStockOpen.setContentDescription(getString(R.string.content_description_day_open,
                data.getString(data.getColumnIndex(Contract.Quote.COLUMN_DAY_OPEN))));
        textStockOpenLabel.setContentDescription(getString(R.string.content_description_day_open,
                data.getString(data.getColumnIndex(Contract.Quote.COLUMN_DAY_OPEN))));
        textStockPrevClose.setText(data.getString(
                data.getColumnIndex(Contract.Quote.COLUMN_PREV_CLOSE)));
        textStockPrevClose.setContentDescription(getString(R.string.content_description_day_close,
                data.getString(data.getColumnIndex(Contract.Quote.COLUMN_PREV_CLOSE))));
        textStockPrevCloseLabel.setContentDescription(getString(R.string.content_description_day_close,
                data.getString(data.getColumnIndex(Contract.Quote.COLUMN_PREV_CLOSE))));
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
        Date date = new Date((long) (dateInMillis));
        SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.date_american_format),
                Locale.US);
        return formatter.format(date);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
