package com.hyber.example.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hyber.Hyber;
import com.hyber.example.R;
import com.hyber.example.ui.Adapters.DevicesRVAdapter;
import com.hyber.handler.EmptyResult;
import com.hyber.handler.HyberCallback;
import com.hyber.handler.HyberError;
import com.hyber.handler.LogoutUserHandler;
import com.hyber.log.HyberLogger;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnDevicesFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DevicesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DevicesFragment extends Fragment {

    @BindView(R.id.devices_RecyclerView)
    RecyclerView mRecyclerView;
    private DevicesRVAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private OnDevicesFragmentInteractionListener mListener;
    private Unbinder unbinder;

    public DevicesFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MessagesFragment.
     */
    public static DevicesFragment newInstance() {
        DevicesFragment fragment = new DevicesFragment();
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
        View view = inflater.inflate(R.layout.fragment_devices, container, false);

        unbinder = ButterKnife.bind(this, view);

        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new DevicesRVAdapter(getActivity(), Hyber.getAllUserDevices());
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnRevokeDeviceListener(new DevicesRVAdapter.RevokeDeviceListener() {
            @Override
            public void onRevoke(final String deviceId, final Boolean isCurrent) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.RevokeConfirmTitle)
                        .setMessage(R.string.RevokeConfirmText)
                        .setNegativeButton(R.string.Revoke, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ArrayList<String> ids = new ArrayList<>();
                                ids.add(deviceId);
                                Hyber.revokeDevices(ids, new HyberCallback<EmptyResult, HyberError>() {
                                    @Override
                                    public void onSuccess(EmptyResult result) {
                                        if (isCurrent) logout();
                                    }

                                    @Override
                                    public void onFailure(HyberError error) {
                                        if (error.getStatus() == HyberError.HyberErrorStatus.UNAUTHORIZED) {
                                            logout();
                                        }
                                    }
                                });
                            }
                        })
                        .setPositiveButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .create()
                        .show();
            }
        });

        Hyber.getAllDevices(new HyberCallback<EmptyResult, HyberError>() {
            @Override
            public void onSuccess(EmptyResult result) {

            }

            @Override
            public void onFailure(HyberError error) {
                if (error.getStatus() == HyberError.HyberErrorStatus.UNAUTHORIZED) {
                    logout();
                }
            }
        });
        return view;
    }

    private void logout() {
        Hyber.logoutCurrentUser(new LogoutUserHandler() {
            @Override
            public void onSuccess() {
                Intent intent = new Intent(getActivity(), SplashActivity.class);
                getActivity().startActivity(intent);
            }

            @Override
            public void onFailure() {
                Toast.makeText(getActivity(), "Logout on failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDevicesFragmentInteractionListener) {
            mListener = (OnDevicesFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMessagesFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void onDeviceRevokeRequestAction(@NonNull String deviceId) {
        if (mListener != null) {
            mListener.onDeviceRevokeRequestActionInteraction(deviceId);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnDevicesFragmentInteractionListener {
        void onDeviceRevokeRequestActionInteraction(@NonNull String deviceId);
    }
}
