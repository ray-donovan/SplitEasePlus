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
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PercentageFragment extends Fragment {
    private static final String FILE_NAME = "percentage.txt";

    View view;
    NestedScrollView nestedScrollView;

    private int height;

    TextView tv_amount, tv_person, tv_plus, tv_minus, tv_totalPerson;
    Button splitBill;
    EditText inputPerson, inputAmount;
    LinearLayout linearLayout, linearLayout2;
    RelativeLayout linearview1;
    ConstraintLayout closeButton;
    FloatingActionButton fad;

    //When fragment is resumed, all the input fields will be cleared
    @Override
    public void onResume() {
        super.onResume();
        try{
            inputPerson = view.findViewById(R.id.inputPerson);
            inputAmount = view.findViewById(R.id.tv_amount);
            linearview1 = view.findViewById(R.id.linearlayout1);
            linearLayout.removeViews(1, linearLayout.getChildCount() - 1);
            BottomSheetBehavior.from(linearview1).setState(BottomSheetBehavior.STATE_EXPANDED);
            inputAmount.getText().clear();
            inputPerson.setText("0");
        } catch (ClassCastException ex){}
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null){
        }
    }


    //Save the scroll's Y coordinate of the user, so that it won't jump back
    //to top everytime the fragment is resumed
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
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
        view = inflater.inflate(R.layout.fragment_percentage, container, false);

        //Resume the scroll Y coordinate
        if (savedInstanceState != null){
            final int scrollY = savedInstanceState.getInt("scrollY");
            nestedScrollView.post(new Runnable() {
                @Override
                public void run() {
                    nestedScrollView.setScrollY(scrollY);
                }
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

        BottomSheetBehavior.from(linearview1).setState(BottomSheetBehavior.STATE_EXPANDED);
        height = linearLayout2.getLayoutParams().height;

        //Ensure the cursor is always at the right hand-side of the input field
        inputPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputPerson.setSelection(inputPerson.getText().length());
            }
        });

        //Prevent user from going to negative number
        inputPerson.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try{
                    if(charSequence.charAt(0) == '0' && charSequence.length() > 1){
                        StringBuilder sb = new StringBuilder(charSequence);
                        String result = (sb.deleteCharAt(0)).toString();
                        inputPerson.setText(result);
                        inputPerson.setSelection(inputPerson.getText().length());
                    }
                }catch(IndexOutOfBoundsException ex){
                    //User can't have negative number, only number >= 0 is allowed
                    inputPerson.setText("0");
                    inputPerson.setSelection(inputPerson.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //Increase the person number and inflate person view
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

        //Decrease the person number and remove person view
        tv_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int t = Integer.parseInt(inputPerson.getText().toString());
                if (t > 0){
                    inputPerson.setText(String.valueOf(t-1));
                    addLayout(true, t, "");
                } else return;
            }
        });

        //When clicked, the bill will be split
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
                int totalPerson = ((ViewGroup) linearLayout).getChildCount() - 1;

                text = formattedDate + ";" + totalPerson;
                for (int i = 1; i <= totalPerson; i++){
                    View childView = ((ViewGroup) linearLayout).getChildAt(i);
                    EditText percentInput = childView.findViewById(R.id.personPercentageInput);
                    String name = ((TextView) childView.findViewById(R.id.tv_cv_person)).getText().toString();

                    float percent = Integer.parseInt(percentInput.getText().toString());
                    float finalAmount = amount * (percent / 100);
                    nestedScrollView.setScrollY(0);
                    BottomSheetBehavior.from(linearview1).setState(BottomSheetBehavior.STATE_COLLAPSED);
                    addLayout2(name, finalAmount);
                    String finalAmount2 = String.format("%.2f", finalAmount);
                    text += (";" + "RM" + finalAmount2);
                }
                //Information is retain in the percentage.txt text file which resides internally in the phone
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
            }
        });

        //History button, when clicked, it will redirect user to the history page of by percentage breakdown
        fad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent next = new Intent(getActivity(), HistoryActivity.class);
                next.putExtra("history", "percentage");
                startActivity(next);
                ((Activity) getActivity()).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        return view;
    }

    //This function returns view of the person
    public void addLayout(boolean b, int index, String name){
        View viewPerson = getLayoutInflater().inflate(R.layout.cardview, null);
        TextView tv_person = viewPerson.findViewById(R.id.tv_cv_person);
        EditText amountField = viewPerson.findViewById(R.id.personPercentageInput);

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

    //This function returns the view of the result to the nested scroll view
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