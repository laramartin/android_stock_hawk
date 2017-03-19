package eu.laramartin.stockhawk.widget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RemoteViews;

import java.util.Set;

import eu.laramartin.stockhawk.R;
import eu.laramartin.stockhawk.data.Contract;
import eu.laramartin.stockhawk.data.PrefUtils;
import eu.laramartin.stockhawk.data.TextUtils;
import eu.laramartin.stockhawk.ui.MainActivity;

/**
 * Created by lara on 14.03.17.
 */

public class WidgetIntentService extends IntentService {

    private static final String[] STOCK_COLUMNS = {
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_PRICE,
            Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
            Contract.Quote.COLUMN_PERCENTAGE_CHANGE
    };

    public WidgetIntentService() {
        super("WidgetIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                WidgetProvider.class));

        Set<String> symbolSet = PrefUtils.getStocks(this);
        if (symbolSet.size() == 0) {
            for (int appWidgetId : appWidgetIds) {
                RemoteViews views = new RemoteViews(
                        getPackageName(),
                        R.layout.widget_stock_item);
                views.setViewVisibility(R.id.widget_single_stock_empty, View.VISIBLE);
                views.setViewVisibility(R.id.widget, View.INVISIBLE);
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
            return;
        }
        String symbol = symbolSet.iterator().next();
        Uri stockUri = Contract.Quote.makeUriForStock(symbol);
        Cursor data = getContentResolver().query(stockUri, STOCK_COLUMNS, null,
                null, Contract.Quote.COLUMN_SYMBOL + " ASC");
        if (data == null) {
            return;
        }
        if (!data.moveToFirst()) {
            data.close();
            return;
        }
        String price = TextUtils.setDollarFormat().format(data.getFloat(data.getColumnIndex(
                Contract.Quote.COLUMN_PRICE)));
        String change;
        if (PrefUtils.getDisplayMode(this).equals(
                this.getResources().getString(R.string.pref_display_mode_absolute_key))) {
            change = TextUtils.setDollarFormatWithPlus(this).format(
                    data.getFloat(data.getColumnIndex(Contract.Quote.COLUMN_ABSOLUTE_CHANGE)));
        } else {
            float percentage = data.getFloat(
                    data.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE));
            change = TextUtils.setPercentageFormat(this).format(percentage / 100);
        }
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(
                    getPackageName(),
                    R.layout.widget_stock_item);
            views.setViewVisibility(R.id.widget_single_stock_empty, View.INVISIBLE);
            views.setViewVisibility(R.id.widget, View.VISIBLE);
            views.setTextViewText(R.id.text_widget_stock_name, symbol);
            views.setTextViewText(R.id.text_widget_share_price, String.valueOf(price));
            views.setTextViewText(R.id.text_widget_price_change, String.valueOf(change));
            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);
            setRemoteContentDescription(views,
                    getString(R.string.content_description_widget_item, symbol, price, change));
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        data.close();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.widget, description);
    }
}
