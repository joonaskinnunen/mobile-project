package com.jk.mytattooartist;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;

public class PopUpClass {

    // new string Arraylist for checked values
    ArrayList<String> checked = new ArrayList<>();

    public PopUpClass(ArrayList<String> checked) {
        this.checked = checked;
    }

    public ArrayList<String> getChecked() {
        return checked;
    }

    public void setChecked(ArrayList<String> checked) {
        this.checked = checked;
    }

    //PopupWindow display method
    public void showPopupWindow(final View view, int layout, ArtistAdapter artistAdapter) {

        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(layout, null);

        // Set label formatting for distance slider
        if (layout == R.layout.popup_window_distance) {
            RangeSlider rangeSlider = popupView.findViewById(R.id.rangeSlider);
            TextView textView = popupView.findViewById(R.id.setDistance);
            rangeSlider.setLabelFormatter(new LabelFormatter() {
                @NonNull
                @Override
                public String getFormattedValue(float value) {
                    textView.setText("Distance set at: " + (int) value + " km");
                    return (int) value + " km";
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

            // Iterate arrayList and compare to checked list. Set checked status accordingly
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
                            checked.remove(checked.indexOf(checkBox.getText().toString()));
                            Toast.makeText(view.getContext(), "Your selections: " + checked, Toast.LENGTH_SHORT).show();
                        }
//                        artistAdapter.filterList(checked);
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
}
