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
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import epitech.eip.slidare.request.Config;
import epitech.eip.slidare.request.Contact;
import epitech.eip.slidare.request.Group;

public class GroupFragment extends Fragment {

    static final String TAG = "GroupFragment";

    private Context mContext;
    private String mToken;
    private String mEmail;

    private boolean mFirst = true;
    private static int index;

    private TextView mAddGroup;
    private ImageView mSettings;

    private List<String> mGroups;
    private List<String> mGroupnames;
    private List<String> mCounts;
    private List<String> mEmails;
    private List<String> mIds;

    private ExpandableListAdapter mGroupListAdapter;
    private ExpandableListView mGroupList;
    private HashMap<String, List<String>> mContacts;

    private GroupFragment.OnItemSelectedListener listener;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);

        Log.d(TAG, Config.ONCREATEVIEW);

        mContext = getActivity().getApplicationContext();
        Intent intent = getActivity().getIntent();
        mToken = intent.getStringExtra("token");

        try {
            Handler<String> handler = new Handler<String>() {
                @Override
                public void success(@NotNull Request request, @NotNull Response response, String s) {
                    Log.d("fetchGroups " + Config.ONSUCCESS,response.toString());
                    mFirst = false;
                    try {
                        JSONObject data = new JSONObject(new String(response.getData()));
                        mGroupnames = new ArrayList<String>();
                        mEmails = new ArrayList<String>();
                        mIds = new ArrayList<String>();
                        mCounts = new ArrayList<String>();
                        if (data.getString("groups").compareTo("null") != 0) {
                            JSONArray groups = data.getJSONArray("groups");
                            for (int i = 0; i < groups.length(); ++i) {
                                JSONObject group = groups.getJSONObject(i);
                                mGroupnames.add(group.getString("name"));
                                if (!group.isNull("users")) {
                                    String ids = group.getString("users").replace("[","").replace("]","").replace("{","").replace("}","");
                                    if (ids.contains(",")){
                                        String[] tab = ids.split(",");
                                        mCounts.add(String.valueOf(tab.length));
                                        for (int j = 0; j < tab.length; j++) {
                                            mIds.add(tab[j].replace("\"", ""));
                                        }
                                    } else {
                                        mCounts.add("1");
                                        mIds.add(ids.replace("\"", ""));
                                    }
                                } else {
                                    mCounts.add("0");
                                    if (i == 0 && !group.isNull("name")){
                                        mGroups.add(mGroupnames.get(i));
                                        List<String> listTmp = new ArrayList<String>();
                                        listTmp.add("No contacts");
                                        mContacts.put(mGroups.get(i), listTmp);
                                        mGroupListAdapter = new ExpandableListAdapter(mContext, mGroups, mContacts, mToken);
                                        mGroupList.setAdapter(mGroupListAdapter);
                                        for ( int l = 0; l < mGroupListAdapter.getGroupCount(); l++)
                                            mGroupList.expandGroup(l);
                                    }
                                }
                            }
                        }
                        else {
                            Toast.makeText(mContext, Config.NO_GROUP, Toast.LENGTH_SHORT).show();
                        }
                        for (int k = 0; k < mIds.size(); k++) {
                            try {
                                Handler<String> handler = new Handler<String>() {
                                    @Override
                                    public void success(@NotNull Request request, @NotNull Response response, String s) {
                                        Log.d("userContact " + Config.ONSUCCESS,response.toString());
                                        try {
                                            JSONObject data = new JSONObject(new String(response.getData()));
                                            if (data.getString("contact").compareTo("null") != 0) {
                                                JSONObject contacts = data.getJSONObject("contact");
                                                mEmail = contacts.getString("email");
                                                mEmails.add(mEmail);
                                                index++;
                                                if (index == mIds.size()){
                                                    index = 0;
                                                    int k = 0;
                                                    for (int i = 0; i < mGroupnames.size(); i++){
                                                        mGroups.add(mGroupnames.get(i));
                                                        if (mCounts.get(i).compareTo("0") != 0){
                                                            List<String> listEmails = new ArrayList<String>();
                                                            for (int j = 0; j < Integer.parseInt(mCounts.get(i)); j++) {
                                                                listEmails.add(mEmails.get(k));
                                                                k++;
                                                            }
                                                            mContacts.put(mGroups.get(i), listEmails);
                                                        } else {
                                                            List<String> listTmp = new ArrayList<String>();
                                                            listTmp.add("No contacts");
                                                            mContacts.put(mGroups.get(i), listTmp);
                                                        }
                                                    }
                                                    mGroupListAdapter = new ExpandableListAdapter(mContext, mGroups, mContacts, mToken);
                                                    mGroupList.setAdapter(mGroupListAdapter);
                                                    for ( int i = 0; i < mGroupListAdapter.getGroupCount(); i++)
                                                        mGroupList.expandGroup(i);
                                                    mFirst = false;
                                                }
                                            }
                                        } catch (Throwable tx) {
                                            tx.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                                        Log.d("userContact " + Config.FAILURE,response.toString());
                                    }
                                };
                                Contact.userContact(mIds.get(k), mToken, handler);
                            } catch (Exception error) {
                                Log.d(TAG, Config.EXCEPTION + error);
                            }
                        }
                    } catch (Throwable tx) {
                        tx.printStackTrace();
                    }
                }

                @Override
                public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                    Log.d("fetchGroups " + Config.FAILURE,response.toString());
                }
            };
            Group.fetchGroups(mToken, handler);
        } catch (Exception error) {
            Log.d(TAG, Config.EXCEPTION + error);
        }

        mGroups = new ArrayList<String>();
        mContacts = new HashMap<String, List<String>>();
        mAddGroup = (TextView) view.findViewById(R.id.ico_addGroup);
        mSettings = (ImageView) view.findViewById(R.id.ico_settings);
        mGroupList = (ExpandableListView) view.findViewById(R.id.group_listview);

        View.OnClickListener mAddGroupListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(mContext, AddGroupActivity.class);
            intent.putExtra("token", mToken);
            startActivity(intent);
            }
        };

        View.OnClickListener mSettingsListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(mContext, AddContactToGroupActivity.class);
            intent.putExtra("token", mToken);
            startActivity(intent);
            }
        };

        mAddGroup.setOnClickListener(mAddGroupListener);
        mSettings.setOnClickListener(mSettingsListener);
        return view;
    }

    public interface OnItemSelectedListener {
        public void onGroupItemSelected(String link);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity = context instanceof Activity ? (Activity) context : null;
        if (activity instanceof GroupFragment.OnItemSelectedListener) {
            listener = (GroupFragment.OnItemSelectedListener) activity;
        } else {
            throw new ClassCastException(activity.toString() + " must implement GroupFragment.OnItemSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mFirst == false) {
            mGroups = new ArrayList<String>();
            mContacts = new HashMap<String, List<String>>();
            mGroupListAdapter = new ExpandableListAdapter(mContext, mGroups, mContacts, mToken);
            try {
                final Handler<String> handler = new Handler<String>() {
                    @Override
                    public void success(@NotNull Request request, @NotNull Response response, String s) {
                        Log.d("fetchGroups " + Config.ONSUCCESS,response.toString());
                        mFirst = false;
                        try {
                            JSONObject data = new JSONObject(new String(response.getData()));
                            mGroupnames = new ArrayList<String>();
                            mEmails = new ArrayList<String>();
                            mIds = new ArrayList<String>();
                            mCounts = new ArrayList<String>();
                            if (data.getString("groups").compareTo("null") != 0) {
                                JSONArray groups = data.getJSONArray("groups");
                                for (int i = 0; i < groups.length(); ++i) {
                                    JSONObject group = groups.getJSONObject(i);
                                    mGroupnames.add(group.getString("name"));
                                    if (!group.isNull("users")) {
                                        String ids = group.getString("users").replace("[","").replace("]","").replace("{","").replace("}","");
                                        if (ids.contains(",")){
                                            String[] tab = ids.split(",");
                                            mCounts.add(String.valueOf(tab.length));
                                            for (int j = 0; j < tab.length; j++) {
                                                mIds.add(tab[j].replace("\"", ""));
                                            }
                                        } else {
                                            mCounts.add("1");
                                            mIds.add(ids.replace("\"", ""));
                                        }
                                    } else {
                                        mCounts.add("0");
                                        if (i == 0 && !group.isNull("name")){
                                            mGroups.add(mGroupnames.get(i));
                                            List<String> listTmp = new ArrayList<String>();
                                            listTmp.add("No contacts");
                                            mContacts.put(mGroups.get(i), listTmp);
                                            mGroupListAdapter = new ExpandableListAdapter(mContext, mGroups, mContacts, mToken);
                                            mGroupList.setAdapter(mGroupListAdapter);
                                            for ( int l = 0; l < mGroupListAdapter.getGroupCount(); l++)
                                                mGroupList.expandGroup(l);
                                        }
                                    }
                                }
                            }
                            else {
                                Toast.makeText(mContext, Config.NO_GROUP, Toast.LENGTH_SHORT).show();
                            }
                            for (int k = 0; k < mIds.size(); k++) {
                                try {
                                    Handler<String> handler = new Handler<String>() {
                                        @Override
                                        public void success(@NotNull Request request, @NotNull Response response, String s) {
                                            Log.d("userContact " + Config.ONSUCCESS,response.toString());
                                            try {
                                                JSONObject data = new JSONObject(new String(response.getData()));
                                                if (data.getString("contact").compareTo("null") != 0) {
                                                    JSONObject contacts = data.getJSONObject("contact");
                                                    mEmail = contacts.getString("email");
                                                    mEmails.add(mEmail);
                                                    index++;
                                                    if (index == mIds.size()){
                                                        index = 0;
                                                        int k = 0;
                                                        for (int i = 0; i < mGroupnames.size(); i++){
                                                            mGroups.add(mGroupnames.get(i));
                                                            if (mCounts.get(i).compareTo("0") != 0){
                                                                List<String> listEmails = new ArrayList<String>();
                                                                for (int j = 0; j < Integer.parseInt(mCounts.get(i)); j++) {
                                                                    listEmails.add(mEmails.get(k));
                                                                    k++;
                                                                }
                                                                mContacts.put(mGroups.get(i), listEmails);
                                                            } else {
                                                                List<String> listTmp = new ArrayList<String>();
                                                                listTmp.add("No contacts");
                                                                mContacts.put(mGroups.get(i), listTmp);
                                                            }
                                                        }
                                                        mGroupListAdapter = new ExpandableListAdapter(mContext, mGroups, mContacts, mToken);
                                                        mGroupList.setAdapter(mGroupListAdapter);
                                                        for ( int i = 0; i < mGroupListAdapter.getGroupCount(); i++)
                                                            mGroupList.expandGroup(i);
                                                        mFirst = false;
                                                    }
                                                }
                                            } catch (Throwable tx) {
                                                tx.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                                            Log.d("userContact " + Config.FAILURE,response.toString());
                                        }
                                    };
                                    Contact.userContact(mIds.get(k), mToken, handler);
                                } catch (Exception error) {
                                    Log.d(TAG, Config.EXCEPTION + error);
                                }
                            }
                        } catch (Throwable tx) {
                            tx.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                        Log.d("fetchGroups " + Config.FAILURE,response.toString());
                    }
                };
                Group.fetchGroups(mToken, handler);
            } catch (Exception error) {
                Log.d(TAG, Config.EXCEPTION + error);
            }
        }
    }

    public void updateDetail() {

        String newTime = String.valueOf(System.currentTimeMillis());
        listener.onGroupItemSelected(newTime);
    }
}
