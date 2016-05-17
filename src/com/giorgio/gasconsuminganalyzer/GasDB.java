/*
 * Por favor, ao usar, divulgar ou modificar, fazer referência ao material do autor.
 * 
 * Please, you can use, share or edit, but make reference to the author's material.
 * 
 * Je vous en prie, vous pouvez utiliser, partager et modifier, mais faites reference au materiel d'auteur.
 * 
 * Disponível em/Available in/Disponible à : 
 *     
 *         SVN: http://album-cover-notes.googlecode.com/svn/trunk/
 *
 * 
 * @author Giorgio P. F. G. Torres
 */
package com.giorgio.gasconsuminganalyzer;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.giorgio.gasconsuminganalyzer.domain.FuelNote;

public class GasDB {
	
	private static GasDB instance = null;
	
	public static GasDB getInstance(Context ctx) {
		if(instance == null) {
			instance = new GasDB(ctx);
		}
		
		return instance;
	}
	
	public static final String DB_NAME = "gas_consuming_analizer_db";
	
	
	/* Comando para criação do banco de dados */
	private static final String DB_CREATE;
	
	private static final int DB_VERSION = 1;
	
	private DbHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mContext;
	
	private boolean isOpened;
	
	private static class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context ctx) {
			super(ctx, DB_NAME, null, DB_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL(DB_CREATE);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + FuelNote.DB_TABLE);
			onCreate(db);
		}
		
	}
	
	/*
	 * O banco de dados SQLiteDatabase precisa de um contexto para ser criado.
	 * Esse contexto deve vir da Activity que utilizará o banco de dados.
	 */
	private GasDB(Context ctx) {
		this.mContext = ctx;
		this.isOpened = false;
	}
	
	public GasDB open() throws SQLException {
		if(!isOpened) {
			mDbHelper = new DbHelper(mContext);
			mDb = mDbHelper.getWritableDatabase();
			isOpened = true;
		}
		return this;
	}
	
	public void close() {
		if(isOpened) {
			isOpened = false;
			mDbHelper.close();
		}
	}
	
	public boolean isOpened() {
		return isOpened;
	}
	
	public long createRefuelNote(FuelNote fuelNote) {
		if(!isDuplicate(fuelNote)) {
			ContentValues initialValues = new ContentValues();
			initialValues.put(FuelNote.KEY_KM, fuelNote.getMileage());
			initialValues.put(FuelNote.KEY_FUEL_TYPE, fuelNote.getFuelType().ordinal());
	        initialValues.put(FuelNote.KEY_FUEL_AMOUNT_CASH, fuelNote.getFuelAmountCash().doubleValue());
	        initialValues.put(FuelNote.KEY_FUEL_AMOUNT_VOLUME, fuelNote.getFuelAmountVolume());
	        initialValues.put(FuelNote.KEY_FULL_TANK, fuelNote.isFullTank());
	        initialValues.put(FuelNote.KEY_REFUEL_DATE, fuelNote.getRefuelDate().getTime().getTime());
	        long newRowId = mDb.insert(FuelNote.DB_TABLE, null, initialValues);
	        if(newRowId != -1) {
	        	fuelNote.setId(newRowId);
	        	
	        }
	        return newRowId; 
		}
		
		return fuelNote.getId();
	}
	
	public boolean isDuplicate(FuelNote fuelNote) {
		List<FuelNote> fuelNotes = getFuelNoteArrayList();
		for(FuelNote fn : fuelNotes) {
			if(fuelNote.equals(fn)) {
				fuelNote.setId(fn.getId());
				return true;
			}
		}
		
		return false;
	}
	
	public boolean deleteNote(long rowId) {
		/* O método delete de SQLiteDatabase retorna o número de linhas afetadas pelo comando */
		/* Veja o link:
		 * 
		 * http://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html#delete%28java.lang.String,%20java.lang.String,%20java.lang.String[]%29
		 * 
		 */
		return mDb.delete(FuelNote.DB_TABLE, FuelNote.KEY_ID + "=" + rowId, null) > 0;
	}
	
	public Cursor fetchAllRefuelNotes() {
		/* Veja o link:
		 * 
		 * http://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html#query%28java.lang.String,%20java.lang.String[],%20java.lang.String,%20java.lang.String[],%20java.lang.String,%20java.lang.String,%20java.lang.String%29
		 * 
		 */
		
		return mDb.query(FuelNote.DB_TABLE, FuelNote.tableColumns.toArray(new String[0]), null, null, null, null, FuelNote.KEY_KM+" ASC");
	}
	
	public Cursor fetchAllRefuelNotes(String orderBy) {
		/* Veja o link:
		 * 
		 * http://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html#query%28java.lang.String,%20java.lang.String[],%20java.lang.String,%20java.lang.String[],%20java.lang.String,%20java.lang.String,%20java.lang.String%29
		 * 
		 */
		
		return mDb.query(FuelNote.DB_TABLE, FuelNote.tableColumns.toArray(new String[0]), null, null, null, null, orderBy);
	}
	
	public ArrayList<FuelNote> getFuelNoteArrayList() {
		ArrayList<FuelNote> fuelNotes = new ArrayList<FuelNote>();
		Cursor c = fetchAllRefuelNotes();
		while(c.moveToNext()) {
    		fuelNotes.add(createFuelNote(c));
    	}
		
		return fuelNotes;
	}
	
	public ArrayList<FuelNote> getFuelNoteArrayListDescRefuelDate() {
		ArrayList<FuelNote> fuelNotes = new ArrayList<FuelNote>();
		Cursor cursor = fetchAllRefuelNotes(FuelNote.KEY_REFUEL_DATE + " DESC");
		while(cursor.moveToNext()) {
    		fuelNotes.add(createFuelNote(cursor));
    	}
		
		return fuelNotes;
	}
	
	private FuelNote createFuelNote(Cursor cursor) {
		return new FuelNote(cursor.getDouble(cursor.getColumnIndex(FuelNote.KEY_KM)), FuelType.getFuelType(cursor.getInt(cursor.getColumnIndex(FuelNote.KEY_FUEL_TYPE))), cursor.getDouble(cursor.getColumnIndex(FuelNote.KEY_FUEL_AMOUNT_CASH)), cursor.getDouble(cursor.getColumnIndex(FuelNote.KEY_FUEL_AMOUNT_VOLUME)), cursor.getInt(cursor.getColumnIndex(FuelNote.KEY_FULL_TANK)) == 1, cursor.getLong(cursor.getColumnIndex(FuelNote.KEY_REFUEL_DATE)));
	}
	
	/*
	public Cursor fetchImageNote(long rowId) {
		
		Cursor c = mDb.query(DB_TABLE, allColumns, KEY_ROWID + "=" + rowId, null, null, null, null, null);
	
		if(c != null) {
			c.moveToFirst();
		}
		
		return c;
	}
	*/
	
	/*
	public Row getImageNote(long rowId) {
		
		Row row = null;
		Cursor c = fetchImageNote(rowId);
		
		if(c != null) {
			row = new Row();
			row.setId(Long.parseLong(c.getString(c.getColumnIndex(KEY_ROWID))));
			row.setTitle(c.getString(c.getColumnIndex(KEY_TITLE)));
			row.setAuthor(c.getString(c.getColumnIndex(KEY_AUTHOR)));
			row.setImagePath(c.getString(c.getColumnIndex(KEY_COVERPATH)));
		}
		
		return row;
	}
	*/
	
	public boolean updateRefuelNote(FuelNote fuelNote) {
        ContentValues newValues = new ContentValues();
        newValues.put(FuelNote.KEY_KM, fuelNote.getMileage());
        newValues.put(FuelNote.KEY_FUEL_TYPE, fuelNote.getFuelType().ordinal());
        newValues.put(FuelNote.KEY_FUEL_AMOUNT_CASH, fuelNote.getFuelAmountCash().doubleValue());
        newValues.put(FuelNote.KEY_FUEL_AMOUNT_VOLUME, fuelNote.getFuelAmountVolume());
        newValues.put(FuelNote.KEY_FULL_TANK, fuelNote.isFullTank());
        newValues.put(FuelNote.KEY_REFUEL_DATE, fuelNote.getRefuelDate().getTime().getTime());

        return mDb.update(FuelNote.DB_TABLE, newValues, FuelNote.KEY_ID + "=" + fuelNote.getId(), null) > 0;
    }
	
	public void dropTable() {
		mDbHelper.onUpgrade(mDb, 1, 2);
	}
	
	private static String buildCreateQuery() {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE ");
		sb.append(FuelNote.DB_TABLE);
		sb.append(" (");
		sb.append(FuelNote.KEY_ID);
			sb.append(" INTEGER PRIMARY KEY AUTOINCREMENT,");
		sb.append(FuelNote.KEY_KM);
			sb.append(" DOUBLE NOT NULL,");
		sb.append(FuelNote.KEY_FUEL_TYPE);
			sb.append(" INTEGER NOT NULL,");
		sb.append(FuelNote.KEY_FUEL_AMOUNT_VOLUME);
			sb.append(" DOUBLE NOT NULL,");
		sb.append(FuelNote.KEY_FUEL_AMOUNT_CASH);
			sb.append(" DOUBLE NOT NULL,");
		sb.append(FuelNote.KEY_FULL_TANK);
			sb.append(" TINYINT NOT NULL,");
		sb.append(FuelNote.KEY_REFUEL_DATE);
			sb.append(" BIGINT NOT NULL");
		sb.append(");");
		
		return sb.toString();
	}
	
	static {
		DB_CREATE = buildCreateQuery();
	}
	
}

