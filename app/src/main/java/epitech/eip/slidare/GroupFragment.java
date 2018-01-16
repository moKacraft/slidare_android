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

import epitech.eip.slidare.request.Config;

public class GroupFragment extends Fragment {

    static final String TAG = "GroupFragment";

    private Context mContext;

    private String mToken;

    private GroupFragment.OnItemSelectedListener listener;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);

        Log.d(TAG, Config.ONCREATEVIEW);

        mContext = getActivity().getApplicationContext();
        Intent intent = getActivity().getIntent();
        mToken = intent.getStringExtra("token");

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

    public void updateDetail() {

        String newTime = String.valueOf(System.currentTimeMillis());
        listener.onGroupItemSelected(newTime);
    }
}
