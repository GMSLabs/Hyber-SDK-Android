package com.hyber.example.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hyber.Hyber;
import com.hyber.example.ApplicationLoader;
import com.hyber.example.BuildConfig;
import com.hyber.example.R;
import com.hyber.handler.LogoutUserHandler;
import com.hyber.log.HyberLogger;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnSettingsFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    private OnSettingsFragmentInteractionListener mListener;
    private Unbinder unbinder;
    @BindView(R.id.currentDataTextView)
    TextView currentDataTextView;
    @BindView(R.id.clientApiKeyInputLayout)
    TextInputLayout clientApiKeyInputLayout;
    @BindView(R.id.clientApiKeyEditText)
    EditText clientApiKeyEditText;
    @BindView(R.id.saveButton)
    Button saveButton;
    @BindView(R.id.backToDefaultButton)
    Button backToDefaultButton;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingsFragment.
     */
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HyberLogger.i("I'm alive!");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        unbinder = ButterKnife.bind(this, view);
        SharedPreferences sp = getActivity().getSharedPreferences(ApplicationLoader.SP_EXAMPLE_SETTINGS, MODE_PRIVATE);

        clientApiKeyEditText.setText(sp.getString(ApplicationLoader.SP_HYBER_CLIENT_API_KEY, BuildConfig.HYBER_CLIENT_API_KEY));

        currentDataTextView.setText(getActivity().getPackageName());

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSettingsFragmentInteractionListener) {
            mListener = (OnSettingsFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSettingsFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @OnClick(R.id.saveButton)
    public void saveButtonOnClick(View v) {
        final String clientApiKey = clientApiKeyEditText.getEditableText().toString();
        if (!isValidClientApiKey(clientApiKey)) {
            clientApiKeyInputLayout.setError("Incorrect Client API Key!");
        } else {
            Hyber.logoutCurrentUser(new LogoutUserHandler() {
                @Override
                public void onSuccess() {
                    clientApiKeyInputLayout.setError(null);
                    SharedPreferences sp = getActivity().getSharedPreferences(ApplicationLoader.SP_EXAMPLE_SETTINGS, MODE_PRIVATE);
                    sp.edit().putString(ApplicationLoader.SP_HYBER_CLIENT_API_KEY, clientApiKey).commit();
                    ApplicationLoader.restartApplication(getActivity().getBaseContext());
                }

                @Override
                public void onFailure() {
                    clientApiKeyInputLayout.setError("Oops... Something went wrong...");
                }
            });
        }
    }

    @OnClick(R.id.backToDefaultButton)
    public void backToDefaultButtonOnClick(View v) {
        SharedPreferences sp = getActivity().getSharedPreferences(ApplicationLoader.SP_EXAMPLE_SETTINGS, MODE_PRIVATE);
        sp.edit().putString(ApplicationLoader.SP_HYBER_CLIENT_API_KEY, BuildConfig.HYBER_CLIENT_API_KEY).commit();
        Hyber.logoutCurrentUser(new LogoutUserHandler() {
            @Override
            public void onSuccess() {
                ApplicationLoader.restartApplication(getActivity().getBaseContext());
            }

            @Override
            public void onFailure() {
                clientApiKeyInputLayout.setError("Oops... Something went wrong...");
            }
        });
    }

    private boolean isValidClientApiKey(String clientApiKey) {
        try {
            return UUID.fromString(clientApiKey) != null;
        } catch (IllegalArgumentException iae) {
            return false;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnSettingsFragmentInteractionListener {
        void onActionInteraction(@NonNull String action);
    }

}
