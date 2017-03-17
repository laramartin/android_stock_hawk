package eu.laramartin.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import eu.laramartin.stockhawk.R;
import eu.laramartin.stockhawk.data.Contract;
import eu.laramartin.stockhawk.data.PrefUtils;
import eu.laramartin.stockhawk.data.TextUtils;

/**
 * Created by lara on 17.03.17.
 */

public class DetailWidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {

            private Cursor cursor = null;

            private final String[] STOCK_COLUMNS = {
                    Contract.Quote.COLUMN_ID,
                    Contract.Quote.COLUMN_SYMBOL,
                    Contract.Quote.COLUMN_PRICE,
                    Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
                    Contract.Quote.COLUMN_PERCENTAGE_CHANGE
            };

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if (cursor != null) {
                    cursor.close();
                }
                final long identityToken = Binder.clearCallingIdentity();
//                Set<String> symbolSet = PrefUtils.getStocks(getApplicationContext());
//                String symbol = symbolSet.iterator().next();
                Uri stockUri = Contract.Quote.URI;
                cursor = getContentResolver().query(stockUri, STOCK_COLUMNS, null,
                        null, Contract.Quote.COLUMN_SYMBOL + " ASC");
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }

            @Override
            public int getCount() {
//                if (cursor == null) {
//                    return 0;
//                }
//                return cursor.getCount();
                return cursor == null ? 0 : cursor.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        cursor == null || !cursor.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_list_item);
                String symbol = cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_SYMBOL));
                String price = TextUtils.setDollarFormat().format(cursor.getFloat(cursor.getColumnIndex(
                        Contract.Quote.COLUMN_PRICE)));
                String change;
                if (PrefUtils.getDisplayMode(getApplicationContext()).equals(
                        getApplicationContext().getResources().getString(R.string.pref_display_mode_absolute_key))) {
                    change = TextUtils.setDollarFormatWithPlus(getApplicationContext()).format(
                            cursor.getFloat(cursor.getColumnIndex(Contract.Quote.COLUMN_ABSOLUTE_CHANGE)));
                } else {
                    float percentage = cursor.getFloat(
                            cursor.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE));
                    change = TextUtils.setPercentageFormat(getApplicationContext()).format(percentage / 100);
                }
                views.setTextViewText(R.id.text_widget_stock_name, symbol);
                views.setTextViewText(R.id.text_widget_share_price, price);
                views.setTextViewText(R.id.text_widget_price_change, change);
//                final Intent fillInIntent = new Intent();
//                String locationSetting =
//                        Utility.getPreferredLocation(DetailWidgetRemoteViewsService.this);
//                Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
//                        locationSetting,
//                        dateInMillis);
//                fillInIntent.setData(weatherUri);
//                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }

//            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
//            private void setRemoteContentDescription(RemoteViews views, String description) {
//                views.setContentDescription(R.id.widget_icon, description);
//            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (cursor.moveToPosition(position)) {
                    return cursor.getLong(cursor.getColumnIndex(Contract.Quote.COLUMN_ID));
                }
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
