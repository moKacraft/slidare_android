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
import android.widget.Toast;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;
import com.mvc.imagepicker.ImagePicker;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import epitech.eip.slidare.request.Config;

public class ToGroupFragment extends Fragment {

    static final String TAG = "ToContactFragment";

    private OnItemSelectedListener listener;

    private Context mContext;

    private ImageView mAttachment;
    private String mGroupname;

    private SharingListAdapter mGroupListAdapter;
    private ListView mGroupList;
    private List<String> mList;

    private String mToken;

    private boolean mBool = false;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_togroup, container, false);

        Log.d(TAG, Config.ONCREATEVIEW);

        mContext = getActivity().getApplicationContext();
        Intent intent = getActivity().getIntent();
        mToken = intent.getStringExtra("token");

        try {
            fetchGroups(mToken);
        } catch (Exception error) {
            Log.d(TAG, Config.EXCEPTION + error);
        }

        mGroupList = (ListView) view.findViewById(R.id.list_group);
        mAttachment = (ImageView) view.findViewById(R.id.ico_attachment);
        mAttachment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            if (mGroupList.getCheckedItemPosition() > -1){
                mGroupname = mGroupList.getItemAtPosition(mGroupList.getCheckedItemPosition()).toString();
                mBool = true;
                try {
                    fetchGroups(mToken);
                } catch (Exception error) {
                    Log.d(TAG, "EXCEPTION ERROR : " + error);
                }

                ImagePicker.setMinQuality(600, 600);
                ImagePicker.pickImage(getActivity(), "Select your image:");
            } else {
                Toast.makeText(mContext, "You must choose a group first.", Toast.LENGTH_SHORT).show();
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
            throw new ClassCastException(activity.toString() + " must implement ToGroupFragment.OnItemSelectedListener");
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

        Fuel.get("http://34.238.153.180:50000/fetchGroups").header(header).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("fetchGroups " + Config.ONSUCCESS,response.toString());

                try {
                    JSONObject data = new JSONObject(new String(response.getData()));

                    if (data.getString("groups").compareTo("null") != 0) {
                        JSONArray groups = data.getJSONArray("groups");
                        ArrayList<String> list = new ArrayList<String>();
                        for (int i = 0; i < groups.length(); ++i) {
                            JSONObject group = groups.getJSONObject(i);
                            list.add(group.getString("name"));
                            if (mBool){
                                if (group.getString("name").compareTo(mGroupname) == 0){
                                    if (!group.isNull("users")) {
                                        String ids = group.getString("users").replace("[","").replace("]","").replace("{","").replace("}","");
                                        if (ids.contains(",")){
                                            String[] tab = ids.split(",");
                                            for (int j = 0; j < tab.length; j++) {
                                                try {
                                                    userContact(tab[j].replace("\"", ""), mToken);
                                                } catch (Exception error) {
                                                    Log.d(TAG, Config.EXCEPTION + error);
                                                }
                                            }
                                        } else {
                                            try {
                                                userContact(ids.replace("\"", ""), mToken);
                                            } catch (Exception error) {
                                                Log.d(TAG, Config.EXCEPTION + error);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        mList = list;
                        mBool = false;
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
                Log.d("fetchGroups " + Config.FAILURE,response.toString());
            }
        });
    }

    public void userContact(String id, String token) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+token);

        Fuel.get("http://34.238.153.180:50000/userContact/" + id).header(header).responseString(new Handler<String>() {

            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("userContact " + Config.ONSUCCESS,response.toString());

                try {
                    JSONObject data = new JSONObject(new String(response.getData()));
                    if (data.getString("contact").compareTo("null") != 0) {
                        JSONObject contacts = data.getJSONObject("contact");
                        String email = contacts.getString("email");
                        ShareActivity.mEmails.put(email);
                    }
                } catch (Throwable tx) {
                    tx.printStackTrace();
                }
            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                Log.d("userContact " + Config.FAILURE,response.toString());
            }
        });
    }
}
