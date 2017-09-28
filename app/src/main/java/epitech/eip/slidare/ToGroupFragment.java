package epitech.eip.slidare;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;
import com.google.android.gms.tasks.Tasks;
import com.mvc.imagepicker.ImagePicker;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 42350 on 27/09/2017.
 */

public class ToGroupFragment extends Fragment {

    static final String TAG = "ToContactFragment";

    private OnItemSelectedListener listener;

    private Context mContext;

    private ImageView mAttachment;
    private TextView mSend;

    private SharingListAdapter mGroupListAdapter;
    private ListView mGroupList;
    private List<String> mList;

    private String mToken;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_togroup, container, false);

        Log.d(TAG, "----------> onCreateView");

        mContext = getActivity().getApplicationContext();
        Intent intent = getActivity().getIntent();
        mToken = intent.getStringExtra("token");

        try {
            fetchGroups(mToken);
        } catch (Exception error) {
            Log.d(TAG, "EXCEPTION ERROR : " + error);
        }

        mGroupList = (ListView) view.findViewById(R.id.list_group);

        mAttachment = (ImageView) view.findViewById(R.id.ico_attachment);
        mAttachment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ImagePicker.pickImage(getActivity(), "Select your image:");
            }
        });

        mSend = (TextView) view.findViewById(R.id.send);
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGroupList.getCheckedItemPosition() >= -1){
                    mGroupList.getItemAtPosition(mGroupList.getCheckedItemPosition()).toString();
                }
            }
        });

        return view;
    }

    public interface OnItemSelectedListener {
        public void onGroupItemSelected(String link);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity = context instanceof Activity ? (Activity) context : null;

        if (activity instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement ToGroupFragment.OnItemSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void updateDetail() {

        String newTime = String.valueOf(System.currentTimeMillis());
        listener.onGroupItemSelected(newTime);
    }

    public void fetchGroups(String token) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+token);

        Fuel.get("http://34.227.142.101:50000/fetchGroups").header(header).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("fetchGroups SUCCESS : ",response.toString());

                try {
                    JSONObject data = new JSONObject(new String(response.getData()));
                    //Log.d(TAG, "----------> result : "+data.getString("groups"));

                    if (data.getString("groups").compareTo("null") != 0) {
                        JSONArray groups = data.getJSONArray("groups");
                        ArrayList<String> list = new ArrayList<String>();
                        for (int i = 0; i < groups.length(); ++i) {

                            JSONObject group = groups.getJSONObject(i);
                            list.add(group.getString("name"));
                        }
                        mList = list;
                    }
                    else {
                        Toast.makeText(mContext, "You have no group yet.", Toast.LENGTH_SHORT).show();
                    }
                    mGroupListAdapter = new SharingListAdapter(mList, mContext, mToken);
                    mGroupList.setAdapter(mGroupListAdapter);
                } catch (Throwable tx) {
                    tx.printStackTrace();
                }
            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                Log.d("fetchGroups FAILURE : ",response.toString());
            }
        });
    }
}
