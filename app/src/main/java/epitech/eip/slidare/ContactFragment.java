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
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kittinunf.fuel.Fuel;
import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import epitech.eip.slidare.request.Config;

public class ContactFragment extends Fragment {

    static final String TAG = "ContactFragment";

    private Context mContext;

    private String mToken;

    private TextView mAddContact;
    private SearchView mSearchView;
    private ListView mListView;

    private CustomListAdapter mAdapter;
    private boolean mFirst = true;
    private List<String> mList;

    private ContactFragment.OnItemSelectedListener listener;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        Log.d(TAG, Config.ONCREATEVIEW);

        mContext = getActivity().getApplicationContext();
        Intent intent = getActivity().getIntent();
        mToken = intent.getStringExtra("token");
        mAdapter = new CustomListAdapter(mList, mContext, mToken);

        try {
            userContacts(mToken);
        } catch (Exception error) {
            Log.d(TAG, Config.EXCEPTION + error);
        }

        mListView = (ListView) view.findViewById(R.id.listView);
        mAddContact = (TextView) view.findViewById(R.id.ico_addContact);
        mSearchView = (android.widget.SearchView) view.findViewById(R.id.search);

        View.OnClickListener mAddContactListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(mContext, AddContactActivity.class);
            intent.putExtra("token", mToken);
            startActivity(intent);
            }
        };

        mSearchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText.toString());
                mListView.setAdapter(mAdapter);
                return false;
            }
        });

        mAddContact.setOnClickListener(mAddContactListener);
        return view;
    }

    public interface OnItemSelectedListener {
        public void onContactItemSelected(String link);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity = context instanceof Activity ? (Activity) context : null;
        if (activity instanceof ContactFragment.OnItemSelectedListener) {
            listener = (ContactFragment.OnItemSelectedListener) activity;
        } else {
            throw new ClassCastException(activity.toString() + " must implement ContactFragment.OnItemSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onResume(){
        super.onResume();

        if (mFirst == false) {
            mList = new ArrayList<String>();
            mAdapter = new CustomListAdapter(mList, mContext, mToken);
            try {
                userContacts(mToken);
            } catch (Exception error) {
                Log.d(TAG, Config.EXCEPTION + error);
            }
        }
    }

    public void updateDetail() {
        String newTime = String.valueOf(System.currentTimeMillis());
        listener.onContactItemSelected(newTime);
    }

    public void userContacts(String token) throws Exception {

        Map<String, Object> header = new HashMap<>();
        header.put("Authorization", "Bearer "+token);

        Fuel.get("http://34.238.153.180:50000/userContacts").header(header).responseString(new Handler<String>() {
            @Override
            public void success(@NotNull Request request, @NotNull Response response, String s) {
                Log.d("userContacts " + Config.ONSUCCESS,response.toString());

                try {
                    JSONObject data = new JSONObject(new String(response.getData()));
                    if (data.getString("contacts").compareTo("null") != 0) {
                        String result = data.getString("contacts");
                        String[] tab = result.split(",");
                        ArrayList<String> list = new ArrayList<String>();
                        for (String elem : tab) {
                            String[] str = elem.split(":");
                            for (int i = 0; i < (str.length - 1) ; i++) {
                                if (str[i].toString().equals("\"email\"")) {
                                    i++;
                                    str[i]= str[i].replace("\"", "");
                                    list.add(str[i]);
                                }
                            }
                        }
                        mList = list;
                    }
                    else {
                        Toast.makeText(mContext, "You have no contact yet.", Toast.LENGTH_SHORT).show();
                    }
                    mAdapter = new CustomListAdapter(mList, mContext, mToken);
                    mListView.setAdapter(mAdapter);
                    mFirst = false;
                } catch (Throwable tx) {
                    tx.printStackTrace();
                }
            }

            @Override
            public void failure(@NotNull Request request, @NotNull Response response, @NotNull FuelError fuelError) {
                Log.d("userContacts " + Config.FAILURE,response.toString());
            }
        });
    }
}
