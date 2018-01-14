package epitech.eip.slidare;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by 42350 on 03/01/2018.
 */

public class HomeListAdapter extends BaseAdapter implements ListAdapter {

    static final String TAG = "HomeListAdapter";
    private Context mContext;
    private String mToken;
    private List<String> mList;

    public HomeListAdapter(List<String> list, Context context, String token) {

        Log.d(TAG, "------------> create");

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
            view = inflater.inflate(R.layout.home_list_item, null);
        }

        Log.d(TAG, "getVIEW = " + mList.get(position));
        String[] files = mList.get(position).split(";");

        final TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        listItemText.setText(files[0]);

        final ImageView listItemImg = (ImageView)view.findViewById(R.id.list_item_img);
        Picasso.with(mContext).load(files[1]).into(listItemImg);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] files = mList.get(position).split(";");
                Intent intent = new Intent(mContext, FileActivity.class);
                intent.putExtra("token", mToken);
                intent.putExtra("image_url",files[1]);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        return view;
    }
}