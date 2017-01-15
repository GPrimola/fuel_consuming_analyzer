package com.giorgio.gasconsuminganalyzer.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.giorgio.gasconsuminganalyzer.GasDB;
import com.giorgio.gasconsuminganalyzer.R;
import com.giorgio.gasconsuminganalyzer.Util;
import com.giorgio.gasconsuminganalyzer.domain.FuelNote;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StatsFragment extends Fragment {
	
	private TextView mTotalMileageTV;
	private TextView mTotalNotesTV;
	private TextView mTotalMoneySpentTV;
	private TextView mTotalFuelVolumeTV;
	private TextView mAverageConsumptionTV;
	private TextView mLowerConsumptionTV;
	private TextView mHigherConsumptionTV;
	private TextView mFuelMeanPriceTV;
	private TextView mRunCostMeanTV;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_stats, container, false);
		
		mTotalMileageTV = (TextView) rootView.findViewById(R.id.totalKmTV);
		mTotalNotesTV = (TextView) rootView.findViewById(R.id.totalNotesTV);
        mTotalMoneySpentTV = (TextView) rootView.findViewById(R.id.totalSpentTV);
        mTotalFuelVolumeTV = (TextView) rootView.findViewById(R.id.totalLitresTV);
        mAverageConsumptionTV = (TextView) rootView.findViewById(R.id.meanConsumptionTV);
        mLowerConsumptionTV = (TextView) rootView.findViewById(R.id.lowerMeanConsumptionTV);
        mHigherConsumptionTV = (TextView) rootView.findViewById(R.id.higherMeanConsumptionTV);
        mFuelMeanPriceTV = (TextView) rootView.findViewById(R.id.tv_value_mean_price);
		mRunCostMeanTV = (TextView) rootView.findViewById(R.id.tv_value_run_cost);
        
		return rootView;
	}
	
	@Override
	public void onResume() {
		GasDB.getInstance(getActivity()).open();
        calculateTotals2();
        super.onResume();
	}
	
	@Override
	public void onDestroy() {
		GasDB.getInstance(getActivity()).close();
		super.onDestroy();
	}
	
	@Deprecated
	private void calculateTotals() {
    	Double totalMileage = 0.0;
    	Double totalCash = 0.0;
    	Double totalVolume = 0.0;
    	Double totalDays = 0.0;
    	
    	/*
		 * Lógica:
		 * O banco guarda a quilometragem total percorrida do carro. Exemplo: Um carro abasteceu em 31/31/12 e tinha 30.300 Km rodados. Em 7/1/13 ele 
		 * abasteceu novamente e tinha 30.350Km rodados.
		 * No entanto, os valores atuais de combustível colocados não devem ser computados, pois eles serão usados para chegar a uma outra quilomet-
		 * ragem. Ou seja, se existem 20 notas de combustível, apenas o dado de quilometragem da última linha será levado em consideração.
		 * 
		 */
    	double kmI = 0.0;
    	double kmF = 0.0;
    	Calendar dateI = Calendar.getInstance();
    	Calendar dateF = Calendar.getInstance();
    	Cursor c = GasDB.getInstance(getActivity()).fetchAllRefuelNotes();
    	List<FuelNote> fuelNotes = new ArrayList<FuelNote>();
    	FuelNote aux = null;
    	while(c.moveToNext()) {
    		//aux = new FuelNote(c.getDouble(c.getColumnIndex(FuelNote.KEY_KM)), FuelType.getFuelType(c.getInt(c.getColumnIndex(FuelNote.KEY_FUEL_TYPE))), c.getDouble(c.getColumnIndex(FuelNote.KEY_FUEL_AMOUNT_CASH)), c.getDouble(c.getColumnIndex(FuelNote.KEY_FUEL_AMOUNT_VOLUME)), c.getLong(c.getColumnIndex(FuelNote.KEY_REFUEL_DATE)));
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
    	} 
    	
    	
    	mTotalMileageTV.setText(String.format("%.3f", Util.roundDouble(totalMileage)));
    	mTotalMoneySpentTV.setText(String.format("%.3f", Util.roundDouble(totalCash)));
    	mTotalFuelVolumeTV.setText(String.format("%.3f", totalVolume));
    	
    	if(!Double.valueOf(totalVolume).equals(Double.valueOf(0.0))) {
    		mAverageConsumptionTV.setText(String.format("%.3f", Util.roundDouble(totalMileage/totalVolume)));
    		mFuelMeanPriceTV.setText(String.format("%.3f", Util.roundDouble(totalCash/totalVolume)));
    		mRunCostMeanTV.setText(String.format("%.3f", Util.roundDouble(totalCash/totalMileage)));
    	} else {
    		mAverageConsumptionTV.setText(String.format("%.3f", totalVolume));
    		mFuelMeanPriceTV.setText(String.format("%.3f", totalVolume));
    		mRunCostMeanTV.setText(String.format("%.3f", Util.roundDouble(totalCash)));
    	}
    	
    	fuelNotes.clear();
    }
	
	/*
	 * Lógica:
	 * O banco guarda a quilometragem total percorrida do carro. Exemplo: Um carro abasteceu em 31/31/12 e tinha 30.300 Km rodados. Em 7/1/13 ele 
	 * abasteceu novamente e tinha 30.350Km rodados.
	 * No entanto, os valores atuais de combustível colocados não devem ser computados, pois eles serão usados para chegar a uma outra quilomet-
	 * ragem. Ou seja, se existem 20 notas de combustível, apenas o dado de quilometragem da última linha será levado em consideração.
	 * 
	 */
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
    			anterior = fuelNotes.get(i-1);
    			
    			distanciaDoisAbastecimentos = Math.abs(Double.valueOf(atual.getMileage() - anterior.getMileage()));
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
        		
        		if(i == totalNotas-1) distanciaTotalPercorrida += atual.getMileage();
    		} else { // if(i == 0)
    			distanciaTotalPercorrida -= atual.getMileage();
    		}
    		
    		if(i < totalNotas-1) {
    			volumeTotalConsumido += atual.getFuelAmountVolume();
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
    	
    	
    	mTotalMileageTV.setText(String.format("%.3f", Util.roundDouble(distanciaTotalPercorrida)));
    	mTotalNotesTV.setText(String.format("%d", totalNotas));
    	mTotalMoneySpentTV.setText(String.format("%.3f", Util.roundDouble(dinheiroTotalAbastecimentos)));
    	mTotalFuelVolumeTV.setText(String.format("%.3f", volumeTotalConsumido));
    	
    	mLowerConsumptionTV.setText(String.format("%.3f", Util.roundDouble(menorConsumo == Double.MAX_VALUE ? 0.0 : maiorConsumo)));
    	mHigherConsumptionTV.setText(String.format("%.3f", Util.roundDouble(maiorConsumo == Double.MIN_VALUE ? 0.0 : menorConsumo)));
    	
    	if(!Double.valueOf(volumeTotalConsumido).equals(Double.valueOf(0.0))) {
    		mAverageConsumptionTV.setText(String.format("%.3f", Util.roundDouble(distanciaTotalPercorrida/volumeTotalConsumido)));
    		mFuelMeanPriceTV.setText(String.format("%.3f", Util.roundDouble(dinheiroTotalConsumido/volumeTotalConsumido)));
    		mRunCostMeanTV.setText(String.format("%.3f", Util.roundDouble(dinheiroTotalConsumido/distanciaTotalPercorrida)));
    	} else {
    		mAverageConsumptionTV.setText(String.format("%.3f", volumeTotalConsumido));
    		mFuelMeanPriceTV.setText(String.format("%.3f", volumeTotalConsumido));
    		mRunCostMeanTV.setText(String.format("%.3f", Util.roundDouble(dinheiroTotalConsumido)));
    	}
    	
    	fuelNotes.clear();
    }
	
}
