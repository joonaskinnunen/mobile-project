package com.jk.mytattooartist;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.collection.ArrayMap;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Map;

public class FrontPageActivity extends BaseActivity {

    RecyclerView recyclerView;
    ArtistAdapter artistAdapter;
    FloatingActionButton fabFilters, fabDistance, fabGender, fabStyles;
    Boolean isFABOpen = false;
    JSONArray filteredData = new JSONArray();
    JSONArray jsonArray2 = new JSONArray();

    // Integer for filtering by distance -ET
    float distance = 0;

    // new string Arraylist for checked values -ET
    ArrayList<String> checked = new ArrayList<>();

    // Create an arraymap for storing floating action button views and
    // the popup layouts they refer to. -ET
    ArrayMap<View, Integer> arrayMap = new ArrayMap<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.front_page);
        ActionBar actionBar = getSupportActionBar();

        // Get the Firebase data through intent
        String arrayList = getIntent().getExtras().getString("Data");

        filteredData = jsonArray2;

        // Hide the back button in action bar -JK
        actionBar.setDisplayHomeAsUpEnabled(false);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set adapter for recyclerview -ET
        try {
            artistAdapter = new ArtistAdapter(arrayList);
            recyclerView.setAdapter(artistAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Floating action buttons -ET
        fabFilters = findViewById(R.id.fabFilters);
        fabDistance = findViewById(R.id.fabDistance);
        fabGender = findViewById(R.id.fabGender);
        fabStyles = findViewById(R.id.fabStyles);

        // Map holding filter fabs and layout ids. -ET
        arrayMap.put(fabDistance, R.layout.popup_window_distance);
        arrayMap.put(fabGender, R.layout.popup_window_person);
        arrayMap.put(fabStyles, R.layout.popup_window_styles);

        // Iterate arraymap and set onclick listeners to the views it holds
        // The onclick methods create instances of popupclass and call showPopupWindow()
        // giving the respective layout as parameter
        for (int i=0; i<arrayMap.size(); i++) {
            View key = arrayMap.keyAt(i);
            int layout = arrayMap.get(key);
            key.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Create a View object through inflater
                    LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
                    View popupView = inflater.inflate(layout, null);

                    // Set label formatting for distance slider
                    if (layout == R.layout.popup_window_distance) {
                        RangeSlider rangeSlider = popupView.findViewById(R.id.rangeSlider);
                        TextView textView = popupView.findViewById(R.id.setDistance);
                        getFormatted(distance,textView,rangeSlider);
                        Button clearButton = popupView.findViewById(R.id.clearButton);
                        clearButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                distance = 0;
                                getFormatted(0,textView,rangeSlider);
                            }
                        });

                        rangeSlider.setLabelFormatter(new LabelFormatter() {
                            @NonNull
                            @Override
                            public String getFormattedValue(float value) {
                                distance = value;
                                return getFormatted(distance, textView, rangeSlider);
                            }
                        });
                    }

                    // Get checked values for styles or person
                    if (layout == R.layout.popup_window_styles || layout == R.layout.popup_window_person) {

                        // Arraylist for checkboxes
                        ArrayList<CheckBox> arrayList = new ArrayList<>();

                        // Collect checkboxes from layout to arraylist
                        if (layout == R.layout.popup_window_styles) {
                            arrayList.add(popupView.findViewById(R.id.cbBlackWhite));
                            arrayList.add(popupView.findViewById(R.id.cbWaterColor));
                            arrayList.add(popupView.findViewById(R.id.cbOldSchool));
                        } else {
                            arrayList.add(popupView.findViewById(R.id.cbMale));
                            arrayList.add(popupView.findViewById(R.id.cbFemale));
                            arrayList.add(popupView.findViewById(R.id.cbOther));
                        }

                        // Iterate arrayList and compare to checked list. Set checkbox status accordingly
                        for (CheckBox checkBox: arrayList) {
                                if (checked.contains(checkBox.getText().toString())) checkBox.setChecked(true);
                        }

                        // Iterate arraylist and set onclick listeners with onclick methods
                        for (CheckBox checkBox: arrayList) {
                            checkBox.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (checkBox.isChecked()) {
                                        checked.add(checkBox.getText().toString());
                                        Toast.makeText(view.getContext(), "Your selections: " + checked, Toast.LENGTH_SHORT).show();
                                    } else if (!checkBox.isChecked()) {
                                        checked.remove(checkBox.getText().toString());
                                        Toast.makeText(view.getContext(), "Your selections: " + checked, Toast.LENGTH_SHORT).show();
                                    }
                                    artistAdapter.filterList(checked, (int) distance);
                                }
                            });
                        }
                    }
                    //Specify the length and width through constants
                    int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                    int height = LinearLayout.LayoutParams.WRAP_CONTENT;

                    //Make Inactive Items Outside Of PopupWindow
                    boolean focusable = true;

                    //Create a window with our parameters
                    final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                    popupWindow.setAnimationStyle(R.style.popup_window_animation);

                    //Set the location of the window on the screen
                    popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                    //Handler for clicking on the inactive zone of the window
                    popupView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {

                            //Close the window when clicked outside of popup
                            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                                popupWindow.dismiss();
                            }
                            return false;
                        }
                    });
                }
            });
        }
        // Set onclick listener to the fab that brings out the filter menu
        fabFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isFABOpen){
                    showFABMenu();
                }else{
                    closeFABMenu();
                }
            }
        });
    }

    // The animation for opening the filter menu
    private void showFABMenu(){
        isFABOpen=true;
        int y = -150;
        for (Map.Entry<View,Integer> entry: arrayMap.entrySet()) {
            entry.getKey().animate().translationY(y);
            y -= 140;
        }
    }

    // The animation for closing the filter menu
    private void closeFABMenu(){
        isFABOpen=false;
        for (Map.Entry<View,Integer> entry: arrayMap.entrySet()) {
            entry.getKey().animate().translationY(0);
        }
    }

    private String getFormatted(float dist, TextView textView, RangeSlider slider) {
        slider.setValues(dist);
        artistAdapter.filterList(checked, (int) dist);
        if (dist == 0) {
            textView.setText("Distance not set.");
        } else {
            textView.setText("Distance set at: " + (int) dist + " km");
        }
        return (int) dist + " km";
    }
}