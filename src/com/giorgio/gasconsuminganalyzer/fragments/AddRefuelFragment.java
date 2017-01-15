package com.giorgio.gasconsuminganalyzer.fragments;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.giorgio.gasconsuminganalyzer.FuelType;
import com.giorgio.gasconsuminganalyzer.GasDB;
import com.giorgio.gasconsuminganalyzer.R;
import com.giorgio.gasconsuminganalyzer.domain.FuelNote;

public class AddRefuelFragment extends Fragment implements OnDateSetListener {

//	private final String MILEAGE = "mileage";
//	private final String FUEL_TYPE = "fuel_type";
//	private final String REFUEL_DATE = "refuel_date";
//	private final String FULL_TANK = "full_tank";
//	private final String VALUE = "value";
//	private final String VOLUME = "volume";
	
	private final String FUEL_NOTE = "fuel_note";
	
	private final int DEFAULT_SELECTION_SPINNER = 0;
	
	private EditText mMileageET = null;
	private Spinner mFuelTypeSP = null;
	private EditText mAmountOfGasInCashET = null;
	private CheckBox mFullTankCB = null;
	private EditText mFuelPriceET = null;
	private Button mAddRefuelNoteBT = null;
	
	private TextView mRefuelDateTV;
	private Calendar mRefuelDate;
	
	private FuelNote newFuelNote = null;
	private Double fuelPrice = 0.0;
	
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_add_refuel, container, false);
		
		if(savedInstanceState != null) {
			newFuelNote = (FuelNote) savedInstanceState.getSerializable(FUEL_NOTE);
		}
		
		mMileageET = (EditText) rootView.findViewById(R.id.kmRoundedET);
		mMileageET.addTextChangedListener(new TextWatcher() {
			@Override public void onTextChanged(CharSequence s, int start, int before, int count) {
				try {
					getFuelNote().setMileage(Double.parseDouble(s.toString()));
				} catch(Exception e) {}
			}
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override public void afterTextChanged(Editable s) {}
		});
        mAmountOfGasInCashET = (EditText) rootView.findViewById(R.id.quantityOfGasET);
        mAmountOfGasInCashET.addTextChangedListener(new TextWatcher() {
			@Override public void onTextChanged(CharSequence s, int start, int before, int count) {
				try {
					getFuelNote().setFuelAmountCash(new BigDecimal(Double.parseDouble(s.toString())));
					getFuelNote().setFuelAmountVolume(getFuelNote().getFuelAmountCash().doubleValue()/fuelPrice);
				} catch(Exception e) {}
			}
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override public void afterTextChanged(Editable s) {}
		});
        mFullTankCB = (CheckBox) rootView.findViewById(R.id.fullTankCB);
        mFullTankCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				getFuelNote().setFullTank(isChecked);
			}
		});
        mFuelPriceET = (EditText) rootView.findViewById(R.id.litresET);
        mFuelPriceET.addTextChangedListener(new TextWatcher() {
			@Override public void onTextChanged(CharSequence s, int start, int before, int count) {
				try {
					fuelPrice = Double.parseDouble(s.toString());
				} catch(Exception e) {}
			}
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override public void afterTextChanged(Editable s) {}
		});
		
        mFuelTypeSP = (Spinner) rootView.findViewById(R.id.fuelTypeSP);
        mFuelTypeSP.setAdapter(new ArrayAdapter<FuelType>(getActivity(), android.R.layout.simple_spinner_dropdown_item, FuelType.values()));
        mFuelTypeSP.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				getFuelNote().setFuelType(FuelType.getFuelType(position));
			}
			@Override public void onNothingSelected(AdapterView<?> parent) {
				getFuelNote().setFuelType((FuelType)mFuelTypeSP.getSelectedItem());
			}
		});
        mRefuelDateTV = (TextView) rootView.findViewById(R.id.whenTV);
        mRefuelDate = Calendar.getInstance();
        
        mAddRefuelNoteBT = (Button) rootView.findViewById(R.id.addRefuelNoteBT);
        mAddRefuelNoteBT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addRefuelNoteBTAction();
			}
		});
        
        initFields();
        
		return rootView;
	}
	
	private void initFields() {
		if(newFuelNote != null) {
			try {
				mMileageET.setText( newFuelNote.getMileage().toString() );
			} catch(NullPointerException e) {}
			try {
				mFuelTypeSP.setSelection( newFuelNote.getFuelType().ordinal() );
			} catch(NullPointerException e) {}
			
			mRefuelDate = newFuelNote.getRefuelDate();
			
			setRefuelDateTVText( newFuelNote.getRefuelDate() );
			mFullTankCB.setChecked( newFuelNote.getFullTank() );
			
			try {
				mAmountOfGasInCashET.setText( newFuelNote.getFuelAmountCash().toString() );
			} catch(NullPointerException e) {}
			try {
				mFuelPriceET.setText( newFuelNote.getFuelAmountVolume().toString() );
			} catch(NullPointerException e) {}
		}
	}
	
	@Override
	public void onResume() {
		GasDB.getInstance( getActivity() ).open();
        super.onResume();
	}
	
	@Override
	public void onDestroy() {
		GasDB.getInstance( getActivity() ).close();
		super.onDestroy();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(FUEL_NOTE, newFuelNote);
		super.onSaveInstanceState(outState);
	}
	
	public void showDatePickerDialog(View v) {
    	DatePickerFragment dpf = new DatePickerFragment(this);
    	dpf.show(getFragmentManager(), "datePicker");
    }
	
	private FuelNote getFuelNote() {
		if(newFuelNote == null) {
			newFuelNote = new FuelNote();
		}
		
		return newFuelNote;
	}
	
	public void addRefuelNoteBTAction() {
    	String kmStr = mMileageET.getText().toString();
    	double km = Double.NEGATIVE_INFINITY;
    	String fuelStr = mAmountOfGasInCashET.getText().toString();
    	double fuel = Double.NEGATIVE_INFINITY;
    	String litresStr = mFuelPriceET.getText().toString();
    	double litres = Double.NEGATIVE_INFINITY;
    	FuelType fuelType = FuelType.getFuelType(mFuelTypeSP.getSelectedItemPosition());
    	
    	if(kmStr == null || kmStr.length() == 0) {
    		Toast.makeText(getActivity(), R.string.validation_mileage_warn, Toast.LENGTH_LONG).show();
    		mMileageET.requestFocus();
    		return;
    	}
    	else {
    		try {
    			km = Double.parseDouble(kmStr);
    		} catch(NumberFormatException e) {
    			Toast.makeText(getActivity(), R.string.validation_mileage_warn, Toast.LENGTH_LONG).show();
    			mMileageET.requestFocus();
    		}
    	}
    	if(fuelStr == null || fuelStr.length() == 0) {
    		Toast.makeText(getActivity(), R.string.validation_quantity_fuel_cash_warn, Toast.LENGTH_LONG).show();
    		mAmountOfGasInCashET.requestFocus();
    		return;
    	}
    	else {
    		try {
    			fuel = Double.parseDouble(fuelStr);
    		} catch(NumberFormatException e) {
    			Toast.makeText(getActivity(), R.string.validation_quantity_fuel_cash_warn, Toast.LENGTH_LONG).show();
    			mAmountOfGasInCashET.requestFocus();
    		}
    	}
    	if(litresStr == null || litresStr.length() == 0) {
    		Toast.makeText(getActivity(), R.string.validation_quantity_fuel_volume_warn, Toast.LENGTH_LONG).show();
    		mFuelPriceET.requestFocus();
    		return;
    	}
    	else {
    		try {
    			litres = Double.parseDouble(litresStr);
    		} catch(NumberFormatException e) {
    			Toast.makeText(getActivity(), R.string.validation_quantity_fuel_volume_warn, Toast.LENGTH_LONG).show();
    			mFuelPriceET.requestFocus();
    		}
    	}
    	if(fuelType == null) {
    		Toast.makeText(getActivity(), R.string.validation_select_proper_fuel_type_warn, Toast.LENGTH_LONG).show();
			mFuelTypeSP.requestFocus();
			return;
    	}
    	
    	if(km != Double.NEGATIVE_INFINITY && fuel != Double.NEGATIVE_INFINITY && litres != Double.NEGATIVE_INFINITY) {
    		FuelNote fn = getFuelNote();
    		GasDB.getInstance(getActivity()).createRefuelNote(fn);
    		if(fn.getId() != null) {
    			Toast.makeText(getActivity(), R.string.add_fuel_note_successfully, Toast.LENGTH_LONG).show();
    			Toast.makeText(getActivity(), fn.getRefuelDate().getTime().toString(), Toast.LENGTH_SHORT).show();
    			clearUIFields();
    		} else {
    			Toast.makeText(getActivity(), R.string.fuel_note_not_added, Toast.LENGTH_LONG).show();
    		}
    	} else {
    		Toast.makeText(getActivity(), R.string.fuel_note_not_added, Toast.LENGTH_LONG).show();
    	}
    	
    }
	
	private void clearUIFields() {
    	mMileageET.setText("");
    	mAmountOfGasInCashET.setText("");
    	mFuelPriceET.setText("");
    	mFuelTypeSP.setSelection(DEFAULT_SELECTION_SPINNER, true);
    	mMileageET.requestFocus();
    	newFuelNote = null;
    }
	
	protected void setRefuelDateTVText(Calendar date) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
		mRefuelDateTV.setText(sdf.format(date.getTime()));
	}
	
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		if(view.isShown()) {
			mRefuelDate.set(Calendar.YEAR, year);
			mRefuelDate.set(Calendar.MONTH, monthOfYear);
			mRefuelDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		}
		setRefuelDateTVText(mRefuelDate);
		getFuelNote().setRefuelDate(mRefuelDate);
	}

}
