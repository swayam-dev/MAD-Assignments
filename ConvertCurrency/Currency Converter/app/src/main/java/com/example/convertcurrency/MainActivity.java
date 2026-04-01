package com.example.convertcurrency;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    EditText amount;
    Spinner currencyFrom;
    Spinner currencyTo;
    Button submitBtn;
    TextView answerTV;


    public double convert(double amt, String from, String to){
        if(from.equals(to))return amt;
        double toINR = 0;
        if(from.equals("USD")){
            toINR = amt*93;
        }
        if(from.equals("EUR")){
            toINR = amt*109;
        }
        if(from.equals("JPY")){
            toINR = amt*0.6;
        }
        double finalAmt=0;

        if(to.equals("USD")){
            finalAmt = toINR/93;
        }
        if(to.equals("JPY")){
            finalAmt = toINR/0.6;
        }
        if(to.equals("EUR")){
            finalAmt = toINR/109;
        }
        if(to.equals("INR")){
            finalAmt = toINR;
        }

        return finalAmt;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        answerTV = findViewById(R.id.answerTV);
        amount = findViewById(R.id.amount);
        currencyFrom = findViewById(R.id.currencyFrom);
        currencyTo = findViewById(R.id.currencyTo);
        submitBtn = findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(v -> {
            double amt = Double.parseDouble(amount.getText().toString());
            String from = currencyFrom.getSelectedItem().toString();
            String to = currencyTo.getSelectedItem().toString();
            double ans = convert(amt,from,to);
            answerTV.setText(String.valueOf(ans));
        });
    }
}