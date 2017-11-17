package epitech.eip.slidare;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kittinunf.fuel.Fuel;
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
import java.util.Map;

/**
 * Created by ferrei_e on 13/02/2017.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    static final String TAG = "ExpandableListAdapter";

    private Context mContext;

    private String mToken;
    private String mBody;

    private List<String> mList;
    private HashMap<String, List<String>> mMap;

    private boolean mExist = false;

    public ExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listChildData, String token) {
        mContext = context;
        mToken = token;
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
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.group_list_subitem, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.group_list_subitem);
        txtListChild.setText(childText);

        ImageView deleteBtn = (ImageView) convertView.findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("KIKOULOL", "DELETE = " + getGroup(groupPosition));
                Log.d("KIKOULOL", "DELETE = " + getChild(groupPosition, childPosition));

                mMap.get(mList.get(groupPosition)).remove(childPosition);

                try {
                    fetchGroups(getGroup(groupPosition).toString(), getChild(groupPosition, childPosition).toString(), mToken);
                } catch (Exception error) {
                    Log.d(TAG, "EXCEPTION ERROR = " + error);
                }
                notifyDataSetChanged();
            }
        });

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

    public void fetchGroups(final String groupname, final String email, String token) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+token);

        Fuel.get("http://34.238.153.180:50000/fetchGroups").header(header).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("fetchGroups SUCCESS : ",response.toString());

                try {
                    JSONObject data = new JSONObject(new String(response.getData()));
                    //Log.d(TAG, "----------> result : "+data.getString("groups"));
                    if (data.getString("groups").compareTo("null") != 0) {
                        JSONArray groups = data.getJSONArray("groups");
                        for (int i = 0; i < groups.length(); ++i) {

                            JSONObject group = groups.getJSONObject(i);
                            //Log.d(TAG, "-----> NAME = " + mName);
                            //Log.d(TAG, "-----> GROUPS : " + group.getString("name"));
                            if(groupname.compareTo(group.getString("name")) == 0){
                                mExist = true;
                                try {
                                    userContacts(groupname, email, mToken);
                                } catch (Exception error) {
                                    Log.d(TAG, "EXCEPTION ERROR = " + error);
                                }
                            }
                        }
                        if (mExist == false){
                            Toast.makeText(mContext, "This group does not exist.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(mContext, "You have no group yet.", Toast.LENGTH_SHORT).show();
                    }
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

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void userContacts(final String groupname, final String email, String token) throws Exception {

        Log.d(TAG, "----------> userContacts");

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+token);

        Fuel.get("http://34.238.153.180:50000/userContacts").header(header).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("userContacts SUCCESS : ",response.toString());

                try {
                    JSONObject data = new JSONObject(new String(response.getData()));
                    //Log.d(TAG, "----------> result : "+data.getString("contacts"));

                    if (data.getString("contacts").compareTo("null") != 0) {
                        String result = data.getString("contacts");
                        String[] tab = result.split(",");
                        ArrayList<String> list = new ArrayList<String>();
                        int lock = 0;
                        for (String elem : tab) {
                            String[] str = elem.split(":");

                            for (int i = 0; i < (str.length - 1) ; i++) {

                                if (str[i].toString().equals("\"id\"") && lock == 1) {
                                    i++;
                                    //Log.d(TAG, "ID = " + str[i]);
                                    str[i] = str[i].replace("\"", "");
                                    mBody = "{ \"contact_identifier\": \"" + str[i] + "\" }";
                                    //Log.d(TAG, "TEST = " + mBody);
                                    try {
                                        removeFromGroup(groupname, mBody, mToken);
                                    } catch (Exception error) {
                                        Log.d(TAG, "EXCEPTION ERROR : " + error);
                                    }
                                    lock = 0;
                                }

                                if (str[i].toString().equals("\"email\"")) {
                                    i++;
                                    str[i] = str[i].replace("\"", "");
                                    if (str[i].toString().equals(email)){
                                        lock = 1;
                                    }
                                }
                            }
                        }
                    }
                    else {
                        /*userListContacts = new String[1];
                        userListContacts[0] = "0 contacts";*/
                        Toast.makeText(mContext, "You do not have this contact.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Throwable tx) {
                    tx.printStackTrace();
                }
            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                Log.d("userContacts FAILURE : ",response.toString());
            }
        });
    }

    public void removeFromGroup(String name, String body, String token) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+token);

        Fuel.put("http://34.238.153.180:50000/removeFromGroup/"+ name).header(header).body(body.getBytes()).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {

                Log.d("rmCGroup SUCCESS : ",response.toString());
                Toast.makeText(mContext, "Contact successfully deleted.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {

                Log.d("rmGGroup FAILURE : ",response.toString());
                Toast.makeText(mContext, "This contact can not be deleted.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
