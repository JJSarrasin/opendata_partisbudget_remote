package ch.opendata.partibudget_remote;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class IconListAdapter extends ArrayAdapter<String> {
	private Context				context;
	private String[]			items;
	private BitmapDrawable[]	icons;

	public IconListAdapter(Context context, int textViewResourceId, String[] items, BitmapDrawable[] icons) {
		super(context, textViewResourceId, items);

		this.context = context;
		this.items = items;
		this.icons = icons;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.listitem_cantons, null);
		}

		TextView nameTextView = (TextView) convertView.findViewById(R.id.listitem_canton_name);
		nameTextView.setText(items[position]);
		icons[position].setBounds( 0, 0, 50, 60);;
		nameTextView.setCompoundDrawables(icons[position], null, null, null);

		return convertView;
	}
}