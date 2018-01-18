package epitech.eip.slidare;

import android.content.Context;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import epitech.eip.slidare.request.Config;
import epitech.eip.slidare.request.Contact;
import epitech.eip.slidare.request.Group;

/**
 * Created by ferrei_e on 13/02/2017.
 */

public class CustomListAdapter extends BaseAdapter implements ListAdapter, Filterable {

    static final String TAG = "CustomListAdapter";

    private Context mContext;

    private String mToken;

    private List<String> mList;
    private List<String> mSaveList;
    private List<String> mArrayList;

    public CustomListAdapter(List<String> list, Context context, String token) {

        Log.d(TAG, Config.ONCREATE);

        mList = list;
        mSaveList = list;
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
            view = inflater.inflate(R.layout.custom_list_item, null);
        }

        final TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        listItemText.setText(mList.get(position));

        ImageView deleteBtn = (ImageView) view.findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            String body = mList.get(position);
            mList.remove(position);
            try {
                if (Patterns.EMAIL_ADDRESS.matcher(body).matches()) {
                    Handler<String> handler = new Handler<String>() {
                        @Override
                        public void success(@NotNull Request request, @NotNull Response response, String s) {
                            Log.d("rmByEmail " + Config.ONSUCCESS,response.toString());
                            Toast.makeText(mContext, Config.CTCTDELETED, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                            Log.d("rmByEmail " + Config.FAILURE,response.toString());
                            Toast.makeText(mContext, Config.CTCTNOTDEL, Toast.LENGTH_SHORT).show();
                        }
                    };
                    Contact.removeContactByEmail(body, mToken, handler);
                }
                else {
                    Handler<String> handler = new Handler<String>() {
                        @Override
                        public void success(@NotNull Request request, @NotNull Response response, String s) {
                            Log.d("rmGroup " + Config.ONSUCCESS,response.toString());
                            Toast.makeText(mContext, Config.GRPDELETED, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                            Log.d("rmGroup " + Config.FAILURE,response.toString());
                            Toast.makeText(mContext, Config.GRPDELFAILED, Toast.LENGTH_SHORT).show();
                        }
                    };
                    Group.removeGroup(body, mToken, handler);
                }
            } catch (Exception error) {
                Log.d(TAG, Config.EXCEPTION + error);
            }
            notifyDataSetChanged();
            }
        });
        return view;
    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {

                mArrayList = (ArrayList<String>) results.values;
                if (mArrayList != mList)
                    mList = mArrayList;
                if (constraint.length() == 0)
                    mList = mSaveList;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<String> FilteredArrList = new ArrayList<String>();

                if (mList == null) {
                    mList = new ArrayList<String>(mArrayList);
                }

                if (constraint == null || constraint.length() == 0) {
                    results.count = mList.size();
                    results.values = mList;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mList.size(); i++) {
                        String data = mList.get(i);
                        if (data.toLowerCase().contains(constraint.toString())) {
                            FilteredArrList.add(data);
                        }
                    }
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }
}
