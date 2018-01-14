package epitech.eip.slidare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;

public class SharingListAdapter extends BaseAdapter implements ListAdapter {

    static final String TAG = "SharingListAdapter";

    private Context mContext;
    private String mToken;
    private List<String> mList;

    public SharingListAdapter(List<String> list, Context context, String token) {
        mList = list;
        mToken = token;
        mContext = context;
    }

    @Override
    public int getCount() {
        if (mList != null)
            return mList.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int pos) {
        return mList.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.sharing_list_item, null);
        }

        final TextView listItemText = (TextView)view.findViewById(R.id.list_item_text);
        listItemText.setText(mList.get(position));
        return view;
    }
}
