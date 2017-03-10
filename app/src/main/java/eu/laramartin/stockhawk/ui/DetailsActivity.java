package eu.laramartin.stockhawk.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import eu.laramartin.stockhawk.R;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            String symbol = intent.getExtras().getString(getString(R.string.intent_stock_symbol_key));
            Toast.makeText(this, "symbol: " + symbol, Toast.LENGTH_SHORT).show();
        }
    }
}
