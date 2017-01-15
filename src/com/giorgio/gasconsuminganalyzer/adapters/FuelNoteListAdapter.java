package com.giorgio.gasconsuminganalyzer.adapters;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.giorgio.gasconsuminganalyzer.GasDB;
import com.giorgio.gasconsuminganalyzer.R;
import com.giorgio.gasconsuminganalyzer.Util;
import com.giorgio.gasconsuminganalyzer.domain.FuelNote;

public class FuelNoteListAdapter extends BaseAdapter {

	private Context mCtx;
	private ArrayList<FuelNote> mArrayList;
	
	public static class FuelNoteViewHolder {
		public TextView labelFieldValue;
		public TextView helpLabelFieldValue;
		public TextView valueField;
		public TextView helpFieldValue;
	}
	
	public FuelNoteListAdapter(Context context) {
		super();
		mCtx = context;
		mArrayList = GasDB.getInstance(mCtx).open().getFuelNoteArrayListDescRefuelDate();
	}
	
	@Override
	public int getCount() {
		return mArrayList.size();
	}
	
	@Override
	public FuelNote getItem(int position) {
		return mArrayList.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		if(rowView == null) {
			LayoutInflater inflater = (LayoutInflater) mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.listrow_refuel_notes, parent, false);
			FuelNoteViewHolder vh = new FuelNoteViewHolder();
			vh.labelFieldValue = (TextView) rowView.findViewById(R.id.label_field_value);
			vh.helpLabelFieldValue = (TextView) rowView.findViewById(R.id.help_label_field_value);
			vh.valueField = (TextView) rowView.findViewById(R.id.value_field);
			vh.helpFieldValue = (TextView) rowView.findViewById(R.id.help_field_value);
			
			rowView.setTag(vh);
		}
		
		FuelNoteViewHolder holder = (FuelNoteViewHolder) rowView.getTag();
		List<FuelNote> notes = mArrayList;
		if(notes != null && notes.size() > 0) {
			FuelNote fn = notes.get(position);
			holder.labelFieldValue.setText(Util.getDate(fn.getRefuelDate().getTime(), true, true, !Util.isSameYear(fn.getRefuelDate(), Calendar.getInstance()), '/'));
			holder.helpLabelFieldValue.setText(fn.getMileage().intValue() + " km");
			holder.valueField.setText("R$ " + new BigDecimal(fn.getFuelAmountCash().doubleValue()).setScale(2, RoundingMode.HALF_UP));
			holder.helpFieldValue.setText(Util.roundDouble3(fn.getFuelAmountVolume()) + "L DE " + fn.getFuelType().toString());
		}
		
		return rowView;
	}
	
}
