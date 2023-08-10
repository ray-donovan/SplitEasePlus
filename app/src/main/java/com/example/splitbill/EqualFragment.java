package com.example.splitbill;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;


import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EqualFragment extends Fragment {
    private static final String FILE_NAME = "equal.txt";
    View view;

    //Element declaration
    TextView tv_equalText, tv_equalAmount, tv_amount1, tv_person1, tv_plus, tv_minus;
    Button split_bill;
    EditText person_Field, amount_Field;
    FloatingActionButton fad;

    //When the fragment is resumed, the person and amount field will be cleared
    @Override
    public void onResume() {
        super.onResume();
        person_Field = view.findViewById(R.id.inputPerson);
        amount_Field = view.findViewById(R.id.tv_amount);

        amount_Field.getText().clear();
        person_Field.setText("0");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_equal, container, false);



        Typeface SFPRO = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/SFPRO.ttf");

        tv_equalText = view.findViewById(R.id.tv_equalText);
        tv_equalAmount = view.findViewById(R.id.tv_equalAmount);
        tv_amount1 = view.findViewById(R.id.tv_amount1);
        tv_person1 = view.findViewById(R.id.tv_person1);
        tv_minus = view.findViewById(R.id.tv_minus);
        tv_plus = view.findViewById(R.id.tv_plus);
        person_Field = view.findViewById(R.id.inputPerson);
        amount_Field = view.findViewById(R.id.tv_amount);
        split_bill = view.findViewById(R.id.split_bill);
        fad = view.findViewById(R.id.fad);

        tv_equalText.setTypeface(SFPRO);
        tv_equalAmount.setTypeface(SFPRO);
        tv_amount1.setTypeface(SFPRO);
        tv_person1.setTypeface(SFPRO);
        tv_minus.setTypeface(SFPRO);
        tv_plus.setTypeface(SFPRO);
        person_Field.setTypeface(SFPRO);
        amount_Field.setTypeface(SFPRO);
        split_bill.setTypeface(SFPRO);

        person_Field.setSelection(person_Field.getText().length());
        person_Field.setCursorVisible(false);

        //Make sure the cursor always ends up at the right-hand side of the input field
        person_Field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                person_Field.setSelection(person_Field.getText().length());
            }
        });

        //On startup, the result is set to RM00.00 (As the user hasnt started splitting bills yet)
        if (savedInstanceState == null){
            tv_equalAmount.setText("RM 00.00");
        }

        //Prevent user from going to negative number in the person field]
        //As well as making sure the cursor stays at the right-hand side of the input field
        person_Field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try{
                    if(charSequence.charAt(0) == '0' && charSequence.length() > 1){
                        StringBuilder sb = new StringBuilder(charSequence);
                        String result = (sb.deleteCharAt(0)).toString();
                        person_Field.setText(result);
                        person_Field.setSelection(person_Field.getText().length());
                    }
                }catch(IndexOutOfBoundsException ex){
                    //Set field to 0 so user cant have negative number
                    person_Field.setText("0");
                    person_Field.setSelection(person_Field.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //Increase the person field when + button is clicked
        tv_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int t = Integer.parseInt(person_Field.getText().toString());
                person_Field.setText(String.valueOf(t+1));
            }
        });

        //Decrease the person field when - button is clicked
        tv_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int t = Integer.parseInt(person_Field.getText().toString());
                if (t > 0){
                    person_Field.setText(String.valueOf(t-1));
                } else return;
            }
        });

        //equal calculation
        //save calculation internally in phone
        split_bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //validation do not allow to split bill if person_num is zero
                if (person_Field.getText().toString().isEmpty() || person_Field.getText().toString().equals("0")
                || amount_Field.getText().toString().isEmpty() || Float.parseFloat(amount_Field.getText().toString()) == 0) {return;}
                try{
                    int person_num = Integer.parseInt(person_Field.getText().toString());
                    float amountRM = Float.parseFloat(amount_Field.getText().toString());
                    float finalAmount = amountRM / person_num;
                    String result = "RM " + String.format("%.2f", finalAmount);
                    tv_equalAmount.setText(result);

                    //Record is saved in .txt file which resides internally in the phone
                    //Saved in equal.txt
                    Date date = Calendar.getInstance().getTime();
                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                    String formattedDate = df.format(date);

                    FileOutputStream fos = null;

                    try {
                        fos = getActivity().openFileOutput(FILE_NAME, Context.MODE_PRIVATE | Context.MODE_APPEND);
                        String text = formattedDate + ";" + person_num + ";" + result + "\n";
                        fos.write(text.getBytes());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (fos != null){
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                } catch (NumberFormatException ex){
                    return;
                }

            }
        });

        //History button, when clicked, will redirect user to the equal breakdown history page
        fad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent next = new Intent(getActivity(), HistoryActivity.class);
                next.putExtra("history", "equal");
                startActivity(next);
                ((Activity) getActivity()).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        return view;
    }
}