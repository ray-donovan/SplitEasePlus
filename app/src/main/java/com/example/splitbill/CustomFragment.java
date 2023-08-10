package com.example.splitbill;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class CustomFragment extends Fragment {

    //This fragment class is used to inflate the amount fragment and the percentage fragment
    View view;
    RadioButton rb_percentage, rb_amount;
    RadioGroup rb_Group1;

    Fragment percentageFrag = new PercentageFragment();
    Fragment amountFrag = new AmountFragment();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_custom, container, false);

        Typeface SFPRO = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/SFPRO.ttf");
        Typeface SFBOLD = Typeface.createFromAsset(view.getContext().getAssets(), "fonts/SFPRO-Bold.ttf");

        rb_percentage = view.findViewById(R.id.btn_percentage);
        rb_amount = view.findViewById(R.id.btn_amount);
        rb_Group1 = view.findViewById(R.id.radio_group1);

        int idPercentage = 1;
        int idAmount = 2;
        rb_percentage.setId(idPercentage);
        rb_amount.setId(idAmount);

        //On startup, the custom breakdown - percentage, will be selected by default
        if (savedInstanceState == null){
            rb_percentage.setChecked(true);
            rb_percentage.setTypeface(SFBOLD);
            rb_amount.setTypeface(SFPRO);
            rb_percentage.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
            rb_amount.setTextColor(ResourcesCompat.getColor(getResources(), R.color.grey1, null));
            replaceFragment(percentageFrag);
        }

        //Show relevant fragment (By Amount / By Percentage) specified by the user
        rb_Group1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == 1){
                    //Replace the fragment with By Percentage
                    rb_percentage.setTypeface(SFBOLD);
                    rb_amount.setTypeface(SFPRO);
                    rb_percentage.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                    rb_amount.setTextColor(ResourcesCompat.getColor(getResources(), R.color.grey1, null));
                    replaceFragment(percentageFrag);
                }else
                {
                    //Replace the fragment with By Amount
                    rb_percentage.setTypeface(SFPRO);
                    rb_amount.setTypeface(SFBOLD);
                    rb_percentage.setTextColor(ResourcesCompat.getColor(getResources(), R.color.grey1, null));
                    rb_amount.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                    replaceFragment(amountFrag);
                }
            }
        });
        return view;
    }

    //Replace fragment function
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.frameLayout2, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}