package com.giorgio.gasconsuminganalyzer.fragments;

import java.util.Calendar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.giorgio.gasconsuminganalyzer.GasDB;
import com.giorgio.gasconsuminganalyzer.R;
import com.giorgio.gasconsuminganalyzer.domain.AccountPassword;

public class AddAccountPasswordFragment extends Fragment {

//	private final String MILEAGE = "mileage";
//	private final String FUEL_TYPE = "fuel_type";
//	private final String REFUEL_DATE = "refuel_date";
//	private final String FULL_TANK = "full_tank";
//	private final String VALUE = "value";
//	private final String VOLUME = "volume";
	
	private final String ACCOUNT_PASSWORD = "account_password";
	
	private EditText mAccountET = null;
	private EditText mAccountSiteET = null;
	private EditText mAccountLoginET = null;
	private EditText mAccountPasswordET = null;
	private Button mAddAccountPasswordBT = null;
	
	private TextView mRefuelDateTV;
	private Calendar mRefuelDate;
	
	private AccountPassword accountPassword = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_add_password, container, false);
		
		if(savedInstanceState != null) {
			accountPassword = (AccountPassword) savedInstanceState.getSerializable(ACCOUNT_PASSWORD);
		}
        
        initFields();
        
		return rootView;
	}
	
	private void initFields() {
		if(accountPassword != null) {
			mAccountET.setText(accountPassword.getAccount());
			mAccountSiteET.setText(accountPassword.getAddress());
			mAccountLoginET.setText(accountPassword.getLogin());
			mAccountPasswordET.setText(accountPassword.getPassword());
		}
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
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(ACCOUNT_PASSWORD, accountPassword);
		super.onSaveInstanceState(outState);
	}
	
	private AccountPassword getAccountPassword() {
		if(accountPassword == null) {
			accountPassword = new AccountPassword();
		}
		
		return accountPassword;
	}
	
	public void addAccountPasswordBTAction() {
    	
    }
	
	private void clearUIFields() {
    	mAccountET.setText("");
    	mAccountSiteET.setText("");
    	mAccountLoginET.setText("");
    	mAccountET.requestFocus();
    	accountPassword = null;
    }
	
}
