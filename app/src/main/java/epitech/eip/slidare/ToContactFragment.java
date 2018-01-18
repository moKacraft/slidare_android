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

import com.github.kittinunf.fuel.core.FuelError;
import com.github.kittinunf.fuel.core.Handler;
import com.github.kittinunf.fuel.core.Request;
import com.github.kittinunf.fuel.core.Response;
import com.mvc.imagepicker.ImagePicker;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import epitech.eip.slidare.request.Config;
import epitech.eip.slidare.request.Contact;

public class ToContactFragment extends Fragment {

    static final String TAG = "ToContactFragment";

    private OnItemSelectedListener listener;

    private Context mContext;

    private ImageView mAttachment;

    private SharingListAdapter mAdapter;
    private ListView mListView;
    private List<String> mList;

    private String mToken;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tocontact, container, false);

        Log.d(TAG, Config.ONCREATEVIEW);

        mContext = getActivity().getApplicationContext();
        Intent intent = getActivity().getIntent();
        mToken = intent.getStringExtra("token");

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
                            Toast.makeText(mContext, Config.NO_CONTACT, Toast.LENGTH_SHORT).show();
                        }
                        mAdapter = new SharingListAdapter(mList, mContext, mToken);
                        mListView.setAdapter(mAdapter);
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
        } catch (Exception error) {
            Log.d(TAG, Config.EXCEPTION + error);
        }

        mListView = (ListView) view.findViewById(R.id.list_contact);
        mAttachment = (ImageView) view.findViewById(R.id.ico_attachment);
        mAttachment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            if (mListView.getCheckedItemPosition() > -1){
                ShareActivity.mEmails.put(mListView.getItemAtPosition(mListView.getCheckedItemPosition()).toString());
                ImagePicker.setMinQuality(600, 600);
                ImagePicker.pickImage(getActivity(), "Select your image:");
            } else {
                Toast.makeText(mContext, Config.CHOOSECTCT, Toast.LENGTH_SHORT).show();
            }
            }
        });
        return view;
    }

    public interface OnItemSelectedListener {
        public void onContactItemSelected(String link);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity = context instanceof Activity ? (Activity) context : null;

        if (activity instanceof OnItemSelectedListener) {
            listener = (OnItemSelectedListener) activity;
        } else {
            throw new ClassCastException(activity.toString() + " must implement ToContactFragment.OnItemSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void updateDetail() {

        String newTime = String.valueOf(System.currentTimeMillis());
        listener.onContactItemSelected(newTime);
    }
}
