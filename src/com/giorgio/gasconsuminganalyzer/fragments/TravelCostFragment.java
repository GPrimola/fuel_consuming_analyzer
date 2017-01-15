package com.giorgio.gasconsuminganalyzer.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.giorgio.gasconsuminganalyzer.FuelType;
import com.giorgio.gasconsuminganalyzer.GasDB;
import com.giorgio.gasconsuminganalyzer.R;
import com.giorgio.gasconsuminganalyzer.Util;
import com.giorgio.gasconsuminganalyzer.domain.FuelNote;

public class TravelCostFragment extends Fragment {

	private EditText mDistanceET = null;
	private TextView mTravelCostTV = null;
	private TextView mPartTravelCostTV = null;
	private Button mCalculateTravelCostBT = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_travel_cost, container, false);
		
		mDistanceET = (EditText) rootView.findViewById(R.id.et_value_distance);
        mTravelCostTV = (TextView) rootView.findViewById(R.id.tv_value_travel_cost);
        mPartTravelCostTV = (TextView) rootView.findViewById(R.id.tv_value_part_travel_cost);
        
        
        mCalculateTravelCostBT = (Button) rootView.findViewById(R.id.button_calculate_travel_cost);
        mCalculateTravelCostBT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				calculateTravelCostBTAction();
			}
		});
        
		return rootView;
	}
	
	@Override
	public void onResume() {
		GasDB.getInstance(getActivity()).open();
        super.onResume();
	}
	
	@Override
	public void onDestroy() {
		GasDB.getInstance(getActivity()).close();
		super.onDestroy();
	}
	
	public void calculateTravelCostBTAction() {
    	String kmStr = mDistanceET.getText().toString();
    	double km = Double.NEGATIVE_INFINITY;
    	
    	if(kmStr == null || kmStr.length() == 0) {
    		Toast.makeText(getActivity(), R.string.validation_mileage_warn, Toast.LENGTH_LONG).show();
    		mDistanceET.requestFocus();
    		return;
    	}
    	else {
    		try {
    			km = Double.parseDouble(kmStr);
    		} catch(NumberFormatException e) {
    			Toast.makeText(getActivity(), R.string.validation_mileage_warn, Toast.LENGTH_LONG).show();
    			mDistanceET.requestFocus();
    		}
    	}
    	
    	Double totalMileage = 0.0;
    	Double totalCash = 0.0;
    	Double totalVolume = 0.0;
    	
    	double kmI = 0.0;
    	double kmF = 0.0;
    	Cursor c = GasDB.getInstance(getActivity()).fetchAllRefuelNotes();
    	FuelNote aux = null;
    	while(c.moveToNext()) {
    		aux = new FuelNote(c.getDouble(c.getColumnIndex(FuelNote.KEY_KM)), FuelType.getFuelType(c.getInt(c.getColumnIndex(FuelNote.KEY_FUEL_TYPE))), c.getDouble(c.getColumnIndex(FuelNote.KEY_FUEL_AMOUNT_CASH)), c.getDouble(c.getColumnIndex(FuelNote.KEY_FUEL_AMOUNT_VOLUME)), Boolean.parseBoolean(c.getString(c.getColumnIndex(FuelNote.KEY_FULL_TANK))), c.getLong(c.getColumnIndex(FuelNote.KEY_REFUEL_DATE)));
    		if(c.isFirst()) kmI = aux.getMileage();
    		if(c.isLast()) kmF = aux.getMileage();
    		else {
	    		totalCash += aux.getFuelAmountCash().doubleValue();
	    		totalVolume += aux.getFuelAmountVolume();
    		}
    	}
    	totalMileage = kmF - kmI;
    	
    	
    	if(!Double.valueOf(totalMileage).equals(Double.valueOf(0.0)) && km != Double.NEGATIVE_INFINITY) {
    		mTravelCostTV.setText(String.format("%.2f",2*Util.roundDouble(totalCash/totalMileage * km)));
    		mPartTravelCostTV.setText(String.format("%.2f",Util.roundDouble(totalCash/totalMileage * km)));
    	} else {
    		mTravelCostTV.setText(String.format("%.2f",Util.roundDouble(0.0)));
    		mPartTravelCostTV.setText(String.format("%.2f",Util.roundDouble(0.0)));
    	}
    	
    }
	
}
