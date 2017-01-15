package com.giorgio.gasconsuminganalyzer;


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.giorgio.gasconsuminganalyzer.domain.FuelNote;
import com.giorgio.gasconsuminganalyzer.fragments.AddRefuelFragment;
import com.giorgio.gasconsuminganalyzer.fragments.DatePickerFragment;
import com.giorgio.gasconsuminganalyzer.fragments.ListRefuelNotesFragment;
import com.giorgio.gasconsuminganalyzer.fragments.NavigationDrawerFragment;
import com.giorgio.gasconsuminganalyzer.fragments.StatsFragment;
import com.giorgio.gasconsuminganalyzer.fragments.TravelCostFragment;

public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

	protected String FRAGMENTS_BUNDLE = "mfragments";
	
	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;
	
	private Fragment [] fragments;
	
	private AddRefuelFragment addRefuelFragment;
	private ListRefuelNotesFragment listRefuelNotesFragment;
	private StatsFragment statsFragment;
	private TravelCostFragment travelCostFragment;
	
	private AddRefuelFragment mAddRefuelFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(savedInstanceState == null) {
			addRefuelFragment = new AddRefuelFragment();
			listRefuelNotesFragment = new ListRefuelNotesFragment();
			statsFragment = new StatsFragment();
			travelCostFragment = new TravelCostFragment();
			
			fragments = new Fragment [] {addRefuelFragment, listRefuelNotesFragment, statsFragment, travelCostFragment};
		} else {
			fragments = (Fragment[]) savedInstanceState.getSerializable(FRAGMENTS_BUNDLE);
		}
		
		
		mAddRefuelFragment = (AddRefuelFragment) fragments[0];
		
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();
		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.container, fragments[position]).commit();
		onSectionAttached(position);
	}
	
	public void showDatePickerDialog(View v) {
    	DatePickerFragment dpf = new DatePickerFragment(mAddRefuelFragment);
    	dpf.show(getSupportFragmentManager(), "datePicker");
    }

	public void onSectionAttached(int number) {
		switch (number) {
		case 0:
			mTitle = getString(R.string.title_refuel_note);
			break;
		case 1:
			mTitle = getString(R.string.title_list_refuel_notes);
			break;
		case 2:
			mTitle = getString(R.string.title_stats);
			break;
		case 3:
			mTitle = getString(R.string.title_travel_cost);
			break;
		}
		getSupportActionBar().setTitle(mTitle);
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	// Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_m, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        if(item.getItemId() == R.id.action_export_to_csv) {
        	if(exportCSVAction())
        		Toast.makeText(this, R.string.export_csv_successfully, Toast.LENGTH_LONG).show();
        }
        if(item.getItemId() == R.id.action_import_from_csv) {
        	if(importCSVAction())
        		Toast.makeText(this, R.string.import_csv_successfully, Toast.LENGTH_LONG).show();
        	else Toast.makeText(this, R.string.error_import_csv, Toast.LENGTH_LONG).show();
        }
        
        return super.onOptionsItemSelected(item);
    }
	
	public boolean exportCSVAction() {
    	boolean isMediaWritable = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    	if(isMediaWritable) {
    		File root = new File(Environment.getExternalStorageDirectory(), "FuelConsumingAnalyzer");
    		Toast.makeText(this, getString(R.string.exporting_csv_to, root.getAbsolutePath()), Toast.LENGTH_LONG).show();
    		if(!root.exists()) {
    			root.mkdirs();
    		}
    		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_-_HH-mm", Locale.getDefault());
    		
    		File backupCsv = new File(root, "fuel_consuming_analyzer_-_" + sdf.format(new Date()) + ".csv");
    		if(!backupCsv.exists()) {
    			try {
					backupCsv.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
    		}
    		
    		CSVWriter writer = null;
    		try {
				writer = new CSVWriter(new FileWriter(backupCsv), ';');
			} catch (IOException e) {
				e.printStackTrace();
			}
    		if(writer != null) {
        		String [] line = new String[]{"KM", "Tipo de Combustível", "Valor do Abastecimento (R$)", "Quantidade de combustível (L)", "Distância Total Desde Abastecimento Anterior", "Abastecimento tanque completo", "Data do Abastecimento"};
	    		writer.writeNext(line);
	    		List<FuelNote> fuelNotes = GasDB.getInstance(this).getFuelNoteArrayListDescRefuelDate();
	        	for(int i=0; i<fuelNotes.size(); i++) {
	        		FuelNote atual = fuelNotes.get(i);
	        		
	        		line[FuelNote.KM_CSV_INDEX] = atual.getMileage().toString();
	        		line[FuelNote.FUEL_TYPE_CSV_INDEX] = atual.getFuelType().getName();
	        		line[FuelNote.FUEL_AMOUNT_CASH_CSV_INDEX] = String.valueOf(atual.getFuelAmountCash().doubleValue());
	        		line[FuelNote.FUEL_AMOUNT_VOLUME_CSV_INDEX] = atual.getFuelAmountVolume().toString();
	        		line[FuelNote.DISTANCE_LAST_REFUEL_CSV_INDEX] = "---";
	        		line[FuelNote.FULL_TANK_CSV_INDEX] = atual.getFullTank() ? "sim" : "não";
	        		line[FuelNote.REFUEL_DATE_CSV_INDEX] = Util.getFormattedDateTime(atual.getRefuelDate().getTime(), '/', '-', "_-_");
	        		
	        		writer.writeNext(line);
	        		
	        		if(i>0) {
	        			FuelNote anterior = fuelNotes.get(i);
	        			line[FuelNote.DISTANCE_LAST_REFUEL_CSV_INDEX] = String.valueOf(Util.roundDouble(atual.getMileage()-anterior.getMileage()));
	        		}
	        	}
	    		try {
					writer.flush();
					writer.close();
					return true;
				} catch (IOException e) {
					e.printStackTrace();
				}
    		}
    	}
    	
    	return false;
    }
	
	public boolean importCSVAction() {
    	boolean isMediaReadable = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    	if(isMediaReadable) {
    		File root = new File(Environment.getExternalStorageDirectory(), "FuelConsumingAnalyzer");
    		if(!root.exists()) {
    			Toast.makeText(this, "Crie a pasta antes de importar e coloque o arquivo para importação lá dentro primeiro.", Toast.LENGTH_LONG).show();
    			return false;
    		}
    		
    		long lastModified = 0;
    		File lastBackupCreated = null;
    		for(File f : root.listFiles(Util.getCSVFilenameFilter())) {
    			if(f.lastModified() > lastModified) {
    				lastModified = f.lastModified();
    				lastBackupCreated = f;
    			}
    		}
    		
    		if(lastBackupCreated != null) {
    			try {
    				Toast.makeText(this, getString(R.string.importing_csv_from, lastBackupCreated.getAbsolutePath()), Toast.LENGTH_LONG).show();
	    			CSVReader reader = new CSVReader(new FileReader(lastBackupCreated), ';');
		        	
	    			String [] line = null;
	    			reader.readNext(); // pula o cabeçalho do arquivo
	    			while((line = reader.readNext()) != null) {
	    				long refuelDate = new Date().getTime();
	    				
	    				boolean parseOk = false;
	    				int trY = 1;
	    				while(!parseOk) {
	    					SimpleDateFormat sdf = null;
	    					switch (trY) {
							case 1:
								sdf = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
								sdf.applyPattern("dd/MM/yyyy_-_HH-mmss");
								break;
							case 2:
								sdf = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
								sdf.applyPattern("dd/MM/yyyy_-_HH-mm-ss");
								break;
							case 3:
								sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
								sdf.applyPattern("dd/MM/yyyy");
								break;
							default:
								break;
							}
	    					
	    					try {
		    					refuelDate = sdf.parse(line[FuelNote.REFUEL_DATE_CSV_INDEX]).getTime();
		    					parseOk = true;
		    				} catch(ParseException e) {
		    					trY++;
		    					parseOk = false;
		    				} catch(Exception e) {
		    					reader.close();
		    					return false;
		    				}
	    				}
	    				
	    				if(line.length == 5)
	    					GasDB.getInstance(this).open().createRefuelNote(new FuelNote(Double.parseDouble(line[FuelNote.KM_CSV_INDEX]),FuelType.getFuelType(line[FuelNote.FUEL_TYPE_CSV_INDEX]),Double.parseDouble(line[FuelNote.FUEL_AMOUNT_CASH_CSV_INDEX]),Double.parseDouble(line[FuelNote.FUEL_AMOUNT_VOLUME_CSV_INDEX]), Boolean.parseBoolean(line[FuelNote.FULL_TANK_CSV_INDEX]),refuelDate));
	    				else
	    					GasDB.getInstance(this).open().createRefuelNote(new FuelNote(Double.parseDouble(line[FuelNote.KM_CSV_INDEX]),FuelType.getFuelType(line[FuelNote.FUEL_TYPE_CSV_INDEX]),Double.parseDouble(line[FuelNote.FUEL_AMOUNT_CASH_CSV_INDEX]),Double.parseDouble(line[FuelNote.FUEL_AMOUNT_VOLUME_CSV_INDEX]), line[FuelNote.FULL_TANK_CSV_INDEX].equalsIgnoreCase("sim"), refuelDate));
		        	}
		        	
	    			reader.close();
	    			
					return true;
				} catch (Exception e) {
					e.printStackTrace();
				}
    		}
    	}
    	
    	return false;
    }
	
	@Override
	public void onBackPressed() {
		if(mNavigationDrawerFragment.isDrawerOpen()) {
			mNavigationDrawerFragment.closeDrawer();
		} else {
			super.onBackPressed();
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(FRAGMENTS_BUNDLE, fragments);
		
		super.onSaveInstanceState(outState);
	}

}
