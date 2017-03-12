package eu.laramartin.stockhawk.data;

import android.content.Context;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import eu.laramartin.stockhawk.R;

/**
 * Created by lara on 12.03.17.
 */

public final class TextUtils {

    public TextUtils() {
    }

    public static DecimalFormat setDollarFormat() {
        return (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
    }

    public static DecimalFormat setDollarFormatWithPlus(Context context) {
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        decimalFormat.setPositivePrefix(context.getString(R.string.dollar_prefix_positive));
        return decimalFormat;
    }

    public static DecimalFormat setPercentageFormat(Context context) {
        DecimalFormat percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
        percentageFormat.setMaximumFractionDigits(2);
        percentageFormat.setMinimumFractionDigits(2);
        percentageFormat.setPositivePrefix(context.getString(R.string.percentage_prefix_positive));
        return percentageFormat;
    }


}
