package it.unibz.mobilevpl.adapter;

import it.unibz.mobilevpl.R;
import it.unibz.mobilevpl.object.Project;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

	private Context context;
	private Project project;

	public ExpandableListAdapter(Context context, Project project) {
		this.context = context;
		this.project = project;
	}

	@Override
	public int getGroupCount() {
		return this.project.getScenes().size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this.project.getScenes().get(groupPosition).getSprites().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this.project.getScenes().get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return this.project.getScenes().get(groupPosition).getSprites().get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		String headerTitle = "Scene " + (groupPosition + 1);

		if(convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.list_group, null);
		}

		TextView listHeader = (TextView)convertView.findViewById(R.id.listHeader);
		listHeader.setTypeface(null, Typeface.BOLD);
		listHeader.setText(headerTitle);

		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		String itemTitle = "Sprite " + (childPosition + 1);

		if(convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.list_item, null);
		}

		TextView listItem = (TextView)convertView.findViewById(R.id.listItem);
		listItem.setText(itemTitle);

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true; //Every child is selectable
	}

}
