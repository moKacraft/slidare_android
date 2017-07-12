package epitech.eip.slidare;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by ferrei_e on 13/02/2017.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context mContext;

    private List<String> mList;
    private HashMap<String, List<String>> mMap;

    public ExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listChildData) {
        mContext = context;
        mList = listDataHeader;
        mMap = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mMap.get(mList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.group_list_subitem, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.group_list_subitem);
        txtListChild.setText(childText);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mMap.get(mList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mList.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.group_list_item, null);
        }

        TextView listItem = (TextView) convertView.findViewById(R.id.group_list_item);
        listItem.setTypeface(null, Typeface.BOLD);
        listItem.setText(headerTitle);

        return convertView;
    }

    @Override
    public int getGroupCount() {
        return mList.size();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
