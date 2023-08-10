package com.example.splitbill;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AmountFragment extends Fragment {
    private static final String FILE_NAME = "amount.txt";

    View view;
    NestedScrollView nestedScrollView;
    private int height;

    //Element declaration
    TextView tv_amount, tv_person, tv_plus, tv_minus, tv_totalPerson;
    Button splitBill;
    EditText inputPerson, inputAmount;
    LinearLayout linearLayout, linearLayout2;
    RelativeLayout linearview1;
    ConstraintLayout closeButton;
    FloatingActionButton fad;

    @Override
    public void onResume() {
        super.onResume();
        //When the fragment is resumed, all input fields will be cleared
        try{
            inputPerson = view.findViewById(R.id.inputPerson);
            inputAmount = view.findViewById(R.id.tv_amount);
            linearview1 = view.findViewById(R.id.linearlayout1);
            linearLayout.removeViews(1, linearLayout.getChildCount() - 1);
            BottomSheetBehavior.from(linearview1).setState(BottomSheetBehavior.STATE_EXPANDED);
            inputAmount.getText().clear();
            inputPerson.setText("0");
        } catch (Exception ex){}
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null){
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        //This is to get the Y coordinate of the user's scroll
        //When resume to the view, the scroll will remain instead of
        //jumping to the top again
        nestedScrollView = view.findViewById(R.id.nestedScrollView);
        int scrollY = nestedScrollView.getScrollY();
        outState.putInt("scrollY", scrollY);
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_amount, container, false);

        if (savedInstanceState != null){
            final int scrollY = savedInstanceState.getInt("scrollY");
            nestedScrollView.post(new Runnable() {
                @Override
                public void run() {
                    nestedScrollView.setScrollY(scrollY);
                } //Scroll to last-scrolled position
            });
        }

        Typeface SFPRO = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/SFPRO.ttf");

        tv_amount = view.findViewById(R.id.tv_amount1);
        tv_person = view.findViewById(R.id.tv_person1);
        tv_plus = view.findViewById(R.id.tv_plus);
        tv_minus = view.findViewById(R.id.tv_minus);
        inputPerson = view.findViewById(R.id.inputPerson);
        tv_totalPerson = view.findViewById(R.id.tv_totalPerson);
        linearLayout = view.findViewById(R.id.linearlayout_inflate);
        linearLayout2 = view.findViewById(R.id.linearlayout_inflate2);
        splitBill = view.findViewById(R.id.split_bill);
        inputAmount = view.findViewById(R.id.tv_amount);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);
        linearview1 = view.findViewById(R.id.linearlayout1);
        closeButton = view.findViewById(R.id.closePercentage);
        fad = view.findViewById(R.id.fad);

        tv_amount.setTypeface(SFPRO);
        tv_person.setTypeface(SFPRO);
        tv_totalPerson.setTypeface(SFPRO);
        splitBill.setTypeface(SFPRO);

        inputPerson.setSelection(inputPerson.getText().length());
        inputPerson.setCursorVisible(false);

        //Expand the bottom sheet on startup
        BottomSheetBehavior.from(linearview1).setState(BottomSheetBehavior.STATE_EXPANDED);
        height = linearLayout2.getLayoutParams().height;

        //To make sure the cursor always stays at the right-hand side position
        inputPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputPerson.setSelection(inputPerson.getText().length());
            }
        });

        //Error validation - Prevent user from going to negative number in the total person field
        inputPerson.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try{
                    if(charSequence.charAt(0) == '0' && charSequence.length() > 1){
                        //Cursor will end at right-hand side position when the input field is clicked
                        StringBuilder sb = new StringBuilder(charSequence);
                        String result = (sb.deleteCharAt(0)).toString();
                        inputPerson.setText(result);
                        inputPerson.setSelection(inputPerson.getText().length());
                    }
                }catch(IndexOutOfBoundsException ex){
                    //Setting the field to 0, so user cant type in negative number
                    inputPerson.setText("0");
                    inputPerson.setSelection(inputPerson.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //Add new person field when + button is clicked
        tv_plus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String personName;
                int t = Integer.parseInt(inputPerson.getText().toString());
                inputPerson.setText(String.valueOf(t+1));
                personName = "Person " + (t+1);
                //inflate layout when user add person
                addLayout(false, 0, personName);
            }
        });

        //Remove the person field when - button is clicked
        tv_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int t = Integer.parseInt(inputPerson.getText().toString());
                //- button stops reacting once the field is 0
                if (t > 0){
                    inputPerson.setText(String.valueOf(t-1));
                    //Remove the layout of the removed person field
                    addLayout(true, t, "");
                } else return;
            }
        });

        //Split the bill when the button is clicked
        splitBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //clear inflate layout first
                String text;
                Date date = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                String formattedDate = df.format(date);
                linearLayout2.removeAllViews();

                //if one of the field is empty do not allow to split bill
                if ((inputAmount.getText().toString()).isEmpty() || (inputPerson.getText().toString()).isEmpty() ||
                        Integer.parseInt(inputPerson.getText().toString()) == 0){return;}

                //Calculation
                float amount = Float.parseFloat(inputAmount.getText().toString());
                float finalAmount = 0;
                int totalPerson = ((ViewGroup) linearLayout).getChildCount() - 1;
                text = formattedDate + ";" + totalPerson;

                for (int i = 1; i <= totalPerson; i++) {
                    View childView = ((ViewGroup) linearLayout).getChildAt(i);
                    EditText amountInput = childView.findViewById(R.id.personAmountInput);
                    float personAmount = Float.parseFloat(amountInput.getText().toString());
                    finalAmount += personAmount;
                }
                if (finalAmount == amount){
                    for (int i = 1; i <= totalPerson; i++) {
                        View childView = ((ViewGroup) linearLayout).getChildAt(i);
                        EditText amountInput = childView.findViewById(R.id.personAmountInput);
                        String name = ((TextView) childView.findViewById(R.id.tv_cv_person)).getText().toString();
                        float personAmount = Float.parseFloat(amountInput.getText().toString());
                        nestedScrollView.setScrollY(0);
                        BottomSheetBehavior.from(linearview1).setState(BottomSheetBehavior.STATE_COLLAPSED);
                        addLayout2(name, personAmount);
                        String finalAmount2 = String.format("%.2f", personAmount);
                        text += (";" + "RM" + finalAmount2);
                    }
                    //Data persistence - All records will be saved in a text file (.txt) which can be found internally
                    //in the phone storage
                    FileOutputStream fos = null;
                    text += "\n";
                    try {
                        fos = getActivity().openFileOutput(FILE_NAME, Context.MODE_PRIVATE | Context.MODE_APPEND);
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
                } else{
                    Toast toast = Toast.makeText(view.getContext().getApplicationContext(), "Final amount is not tally with total bill", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });

        //History button, start the History activity when clicked
        fad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent next = new Intent(getActivity(), HistoryActivity.class);
                next.putExtra("history", "amount");
                startActivity(next);
                ((Activity) getActivity()).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        return view;
    }

    //Add / Remove person view when called
    public void addLayout(boolean b, int index, String name){
        View viewPerson = getLayoutInflater().inflate(R.layout.amountview, null);
        TextView tv_person = viewPerson.findViewById(R.id.tv_cv_person);

        Typeface SFPRO = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/SFPRO.ttf");
        tv_person.setTypeface(SFPRO);
        if (b){
            linearLayout.removeViewAt(index);
            collapse(viewPerson);
        }else{

            tv_person.setText(name);
            linearLayout.addView(viewPerson);
            expand(viewPerson);
        }
    }

    //This is the view of the result, calculated result will be inflated to the
    //nested scroll view
    public void addLayout2(String name, float amount){
        View personAmount = getLayoutInflater().inflate(R.layout.personview, null);
        TextView tv_person = personAmount.findViewById(R.id.tv_person);
        TextView tv_amount = personAmount.findViewById(R.id.tv_amount);

        Typeface SFPRO = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/SFPRO.ttf");
        tv_person.setTypeface(SFPRO);
        tv_amount.setTypeface(SFPRO);

        tv_person.setText(name);
        tv_amount.setText(String.format("RM %.2f", amount));
        linearLayout2.addView(personAmount);
    }

    //Credit to Tom Esterez on Stackoverflow for this animation
    public static void expand(final View v) {
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }
}