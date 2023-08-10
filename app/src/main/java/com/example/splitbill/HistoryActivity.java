package com.example.splitbill;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class HistoryActivity extends AppCompatActivity {

    private static final String FILE_EQUAL = "equal.txt";
    private static final String FILE_PERCENTAGE = "percentage.txt";
    private static final String FILE_AMOUNT = "amount.txt";

    LinearLayout layoutInflate;
    MaterialToolbar toolbar;
    String historyType;

    FileInputStream fis = null;

    //This is the history page, can show 3 different pages depending on the user's current page
    //e.g. If user is in By Amount page, it will only show By Amount history
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        toolbar = findViewById(R.id.topAppbar);
        layoutInflate = findViewById(R.id.layout_inflate);

        //To clear previous history before displaying the relevant records
        layoutInflate.removeAllViews();

        //Get key from the relevant fragments
        Bundle history = getIntent().getExtras();
        if (history != null){
            historyType = history.getString("history");
        }

        //Show relevant history based on equal, percentage or amount
        if ("equal".equals(historyType)){
            try {
                fis = openFileInput(FILE_EQUAL);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String text;

                while ((text = br.readLine()) != null){
                    String[] row = text.split(";");
                    String date = row[0];
                    String numPerson = row[1];
                    String amount = row[2];
                    inflateEqual(date, numPerson, amount);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fis != null){
                    try{
                        fis.close();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }
        if ("percentage".equals(historyType)){
            try {
                fis = openFileInput(FILE_PERCENTAGE);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String text;

                while ((text = br.readLine()) != null){
                    String[] row = text.split(";");
                    String date = row[0];
                    String numPerson = row[1];
                    int index = 0;
                    //inflate date here first
                    inflateDate(date);
                    //inflate person
                    for (int i = 2; i <= Integer.parseInt(numPerson) + 1; i++){
                        index++;
                        String amount = row[i];
                        inflateCustom(index, amount);
                    }
                    //inflate horizontal line as a closing line
                    inflateLine();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fis != null){
                    try{
                        fis.close();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }
        if ("amount".equals(historyType)){
            try {
                fis = openFileInput(FILE_AMOUNT);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String text;

                while ((text = br.readLine()) != null){
                    String[] row = text.split(";");
                    String date = row[0];
                    String numPerson = row[1];
                    int index = 0;
                    //inflate date here first
                    inflateDate(date);
                    //inflate person
                    for (int i = 2; i <= Integer.parseInt(numPerson) + 1; i++){
                        index++;
                        String amount = row[i];
                        inflateCustom(index, amount);
                    }
                    //inflate horizontal line as a closing line
                    inflateLine();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fis != null){
                    try{
                        fis.close();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }

        //Close activity when back button is pressed
        toolbar.getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //This function is called when user wants to see the equal's history page
    public void inflateEqual(String date, String noOfPerson, String amount1){
        View equalView = getLayoutInflater().inflate(R.layout.equalview, null);
        TextView tv_date = equalView.findViewById(R.id.date);
        TextView noPerson = equalView.findViewById(R.id.numOfPerson);
        TextView amount = equalView.findViewById(R.id.tv_amount);

        Typeface SFPRO = Typeface.createFromAsset(getAssets(), "fonts/SFPRO.ttf");
        tv_date.setTypeface(SFPRO);
        noPerson.setTypeface(SFPRO);
        amount.setTypeface(SFPRO);

        tv_date.setText(date);
        noPerson.setText(noOfPerson);
        amount.setText(amount1);

        layoutInflate.addView(equalView);
    }

    //This function is used for both By Percentage and By Amount fragment
    public void inflateCustom(int i, String amount){
        View customView = getLayoutInflater().inflate(R.layout.inflatepercentage, null);
        TextView tv_person = customView.findViewById(R.id.tv_person);
        TextView tv_amount = customView.findViewById(R.id.tv_amount);

        Typeface SFPRO = Typeface.createFromAsset(getAssets(), "fonts/SFPRO.ttf");
        tv_person.setTypeface(SFPRO);
        tv_amount.setTypeface(SFPRO);

        tv_person.setText("Person " + String.valueOf(i));
        tv_amount.setText(amount);

        layoutInflate.addView(customView);
    }

    //Individually inflate the date of the bill into the view
    public void inflateDate(String customDate){
        View dateView = getLayoutInflater().inflate(R.layout.inflatedate, null);
        TextView tv_date = dateView.findViewById(R.id.tv_date);

        Typeface SFPRO = Typeface.createFromAsset(getAssets(), "fonts/SFPRO.ttf");
        tv_date.setTypeface(SFPRO);

        tv_date.setText(customDate);

        layoutInflate.addView(dateView);
    }

    //A horizontal line is inflated at the every end of each bill
    public void inflateLine(){
        View lineView = getLayoutInflater().inflate(R.layout.inflateline, null);
        layoutInflate.addView(lineView);
    }


}