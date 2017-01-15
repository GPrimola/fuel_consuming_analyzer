package com.giorgio.gasconsuminganalyzer.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.giorgio.gasconsuminganalyzer.FuelType;
import com.giorgio.gasconsuminganalyzer.GasDB;
import com.giorgio.gasconsuminganalyzer.R;
import com.giorgio.gasconsuminganalyzer.Util;
import com.giorgio.gasconsuminganalyzer.adapters.FuelNoteListAdapter;
import com.giorgio.gasconsuminganalyzer.domain.FuelNote;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ListRefuelNotesFragment extends Fragment {
	
	private TextView mNextRefuelDateTV;
	private ListView mList;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_list_refuel_notes, container, false);
		
		mNextRefuelDateTV = (TextView) rootView.findViewById(R.id.nextRefuelTV);
		mList = ((ListView)rootView.findViewById(android.R.id.list));
		mList.setAdapter(new FuelNoteListAdapter(getActivity()));
        
		return rootView;
	}
	
	@Override
	public void onResume() {
		GasDB.getInstance(getActivity()).open();
		calculateTotals();
        super.onResume();
	}
	
	@Override
	public void onDestroy() {
		GasDB.getInstance(getActivity()).close();
		super.onDestroy();
	}
	
	private void calculateTotals() {
    	Double totalMileage = 0.0;
    	Double totalCash = 0.0;
    	Double totalVolume = 0.0;
    	Double totalDays = 0.0;
    	
    	double kmI = 0.0;
    	double kmF = 0.0;
    	Calendar dateI = Calendar.getInstance();
    	Calendar dateF = Calendar.getInstance();
    	Cursor c = GasDB.getInstance(getActivity()).fetchAllRefuelNotes();
    	List<FuelNote> fuelNotes = new ArrayList<FuelNote>();
    	FuelNote aux = null;
    	while(c.moveToNext()) {
    		aux = new FuelNote(c.getDouble(c.getColumnIndex(FuelNote.KEY_KM)), FuelType.getFuelType(c.getInt(c.getColumnIndex(FuelNote.KEY_FUEL_TYPE))), c.getDouble(c.getColumnIndex(FuelNote.KEY_FUEL_AMOUNT_CASH)), c.getDouble(c.getColumnIndex(FuelNote.KEY_FUEL_AMOUNT_VOLUME)), Boolean.parseBoolean(c.getString(c.getColumnIndex(FuelNote.KEY_FULL_TANK))), c.getLong(c.getColumnIndex(FuelNote.KEY_REFUEL_DATE)));
    		if(c.isFirst()) {
    			kmI = aux.getMileage();
    			dateI = aux.getRefuelDate();
    		}
    		if(c.isLast()) {
    			kmF = aux.getMileage();
    			dateF = aux.getRefuelDate();
    		}
    		else {
	    		totalCash += aux.getFuelAmountCash().doubleValue();
	    		totalVolume += aux.getFuelAmountVolume();
    		}
    		fuelNotes.add(aux);
    	}
    	totalMileage = kmF - kmI;
    	totalDays = Util.getDiferencaDiasDouble(dateI, dateF);
    	Calendar nextRefuelDate = dateF;
    	if(Double.compare(totalDays, 0.0) != 0) {
    		Double averageTotalDistance = 0.0;
        	Double kmPerDay = totalMileage / totalDays;
        	for(int i=1; i<fuelNotes.size(); i++) {
        		averageTotalDistance += (fuelNotes.get(i).getMileage() - fuelNotes.get(i-1).getMileage()) * Util.getDiferencaDias(fuelNotes.get(i-1).getRefuelDate(), fuelNotes.get(i).getRefuelDate());
        	}
        	Double averageDistancePerRefuel = 0.0;
    		averageDistancePerRefuel = averageTotalDistance / totalDays;
    		
    		
        	Integer daysNextRefuel = (int)(averageDistancePerRefuel / kmPerDay);
        	nextRefuelDate.add(Calendar.DAY_OF_YEAR, daysNextRefuel);
        	
        	mNextRefuelDateTV.setText(Util.getDate(nextRefuelDate.getTime(), true, true, nextRefuelDate.get(Calendar.YEAR) != Calendar.getInstance().get(Calendar.YEAR), '/'));
    	} else {
    		mNextRefuelDateTV.setText("");
    	}
    	
    	fuelNotes.clear();
    }
	
	private void calculateTotals2() {
		double dinheiroTotalConsumido = 0.0;
		double dinheiroTotalAbastecimentos = 0.0;
    	double distanciaTotalPercorrida = 0.0;
    	double volumeTotalConsumido = 0.0;
    	double volumeTotalAbastecido = 0.0;
    	double diasTotaisDesdeNotaUm = 0.0;
    	double custoPorDia = 0.0;
    	double custoPorKm = 0.0;
    	double mediaKmPorDia = 0.0;
    	double menorConsumo = Double.MAX_VALUE;
    	double maiorConsumo = Double.MIN_VALUE;
    	double consumoMedio = 0.0;
    	double mediaConsumo = 0.0;
    	
    	Calendar dateF = Calendar.getInstance();
    	List<FuelNote> fuelNotes = GasDB.getInstance(getActivity()).open().getFuelNoteArrayList();
    	List<Double> distancias = new ArrayList<Double>();
    	
		FuelNote atual = null;
		FuelNote anterior = null;
		Double distanciaDoisAbastecimentos = null;
		Double consumoAtual;
    	int totalNotas = fuelNotes.size();
    	for(int i=0; i<totalNotas; i++) {
    		atual = fuelNotes.get(i);
    		
    		volumeTotalAbastecido += atual.getFuelAmountVolume();
    		dinheiroTotalAbastecimentos += atual.getFuelAmountCash().doubleValue();
    		
    		if(i > 0) {
    			volumeTotalConsumido += atual.getFuelAmountVolume();
    			
    			anterior = fuelNotes.get(i-1);
    			
    			distanciaDoisAbastecimentos = Math.abs(Double.valueOf(atual.getMileage() - anterior.getMileage()));
    			distanciaTotalPercorrida += distanciaDoisAbastecimentos;
        		distancias.add(distanciaDoisAbastecimentos);
        		
        		consumoAtual = distanciaDoisAbastecimentos/atual.getFuelAmountVolume();
        		
        		diasTotaisDesdeNotaUm += Util.getDiferencaDiasDouble(anterior.getRefuelDate(), atual.getRefuelDate());
        		
        		if(atual.isFullTank() && anterior.isFullTank()) {
	        		if(consumoAtual > maiorConsumo) {
	        			maiorConsumo = consumoAtual;
	        		}
	        		if(consumoAtual < menorConsumo) {
	        			menorConsumo = consumoAtual;
	        		}
        		}
    		}
    		if(i < totalNotas-1) {
    			dinheiroTotalConsumido += atual.getFuelAmountCash().doubleValue();
    		}
    		
    		dateF = atual.getRefuelDate();
    	}
    	Calendar nextRefuelDate = dateF;
    	
    	if(Double.compare(diasTotaisDesdeNotaUm, 0.0) != 0) {
    		Double averageTotalDistance = 0.0;
        	Double kmPerDay = distanciaTotalPercorrida / diasTotaisDesdeNotaUm;
        	for(int i=1; i<fuelNotes.size(); i++) {
        		averageTotalDistance += (fuelNotes.get(i).getMileage() - fuelNotes.get(i-1).getMileage()) * Util.getDiferencaDias(fuelNotes.get(i-1).getRefuelDate(), fuelNotes.get(i).getRefuelDate());
        	}
        	Double averageDistancePerRefuel = 0.0;
    		averageDistancePerRefuel = averageTotalDistance / diasTotaisDesdeNotaUm;
    		
    		
        	Integer daysNextRefuel = (int)(averageDistancePerRefuel / kmPerDay);
        	nextRefuelDate.add(Calendar.DAY_OF_YEAR, daysNextRefuel);
    	} 
    	
    	fuelNotes.clear();
    }
	
}
