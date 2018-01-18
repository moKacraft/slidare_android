package epitech.eip.slidare;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import epitech.eip.slidare.request.Config;
import epitech.eip.slidare.request.Contact;
import epitech.eip.slidare.request.Group;

/**
 * Created by ferrei_e on 13/02/2017.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    static final String TAG = "ExpandableListAdapter";

    private Context mContext;

    private String mToken;
    private String mBody;

    private String mGroupname;
    private String mEmail;

    private List<String> mList;
    private HashMap<String, List<String>> mMap;

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
            mGroupname = mList.get(groupPosition);
            mEmail = childText;
            if (mEmail != null && mGroupname != null) {
                try {
                    Handler<String> handler = new Handler<String>() {
                        @Override
                        public void success(@NotNull Request request, @NotNull Response response, String s) {
                            Log.d("userContacts " + Config.ONSUCCESS,response.toString());
                            try {
                                JSONObject data = new JSONObject(new String(response.getData()));
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
                                                str[i] = str[i].replace("\"", "");
                                                mBody = "{ \"contact_identifier\": \"" + str[i] + "\" }";
                                                try {
                                                    Handler<String> handler = new Handler<String>() {
                                                        @Override
                                                        public void success(@NotNull Request request, @NotNull Response response, String s) {
                                                            Log.d("rmCGroup " + Config.ONSUCCESS,response.toString());
                                                            Toast.makeText(mContext, Config.CTCTDELETED, Toast.LENGTH_SHORT).show();
                                                            mMap.get(mList.get(groupPosition)).remove(childPosition);
                                                            notifyDataSetChanged();
                                                        }

                                                        @Override
                                                        public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                                                            Log.d("rmGGroup " + Config.FAILURE,response.toString());
                                                            Toast.makeText(mContext, Config.CTCTNOTDEL, Toast.LENGTH_SHORT).show();
                                                        }
                                                    };
                                                    Group.removeFromGroup(mGroupname, mBody, mToken, handler);
                                                } catch (Exception error) {
                                                    Log.d(TAG, Config.EXCEPTION + error);
                                                }
                                                lock = 0;
                                            }
                                            if (str[i].toString().equals("\"email\"")) {
                                                i++;
                                                str[i] = str[i].replace("\"", "");
                                                if (str[i].toString().equals(mEmail)){
                                                    lock = 1;
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (Throwable tx) {
                                tx.printStackTrace();
                            }
                        }

                        @Override
                        public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                            Log.d("userContacts " + Config.FAILURE,response.toString());
                        }
                    };
                    Contact.userContacts(mToken, handler);
                }
                catch (Exception e){
                    Log.d(TAG, Config.EXCEPTION + e);
                }
            } else {
                Toast.makeText(mContext, Config.ERROROCCUR, Toast.LENGTH_SHORT).show();
            }
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

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
