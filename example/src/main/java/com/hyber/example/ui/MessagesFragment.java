package com.hyber.example.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hyber.Hyber;
import com.hyber.RealmRecyclerViewAdapter;
import com.hyber.example.R;
import com.hyber.example.ui.Adapters.MessagesRVAdapter;
import com.hyber.handler.HyberCallback;
import com.hyber.handler.HyberError;
import com.hyber.handler.LogoutUserHandler;
import com.hyber.log.HyberLogger;
import com.hyber.model.Message;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnMessagesFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MessagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessagesFragment extends Fragment {

    @BindView(R.id.messages_RecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.answerLayout)
    RelativeLayout mAnswerLayout;
    @BindView(R.id.inputAnswerEditTextView)
    AppCompatEditText mInputAnswer;
    @BindView(R.id.sendAnswerAppCompatImageButton)
    AppCompatImageButton mSendAnswer;
    private MessagesRVAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private OnMessagesFragmentInteractionListener mListener;
    private Unbinder unbinder;
    private int mMaxHistoryRequests = 2;
    private Long mTimeForNextHistoryRequest;

    public MessagesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MessagesFragment.
     */
    public static MessagesFragment newInstance() {
        MessagesFragment fragment = new MessagesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HyberLogger.i("I'm alive!");
    }

    @OnClick(R.id.sendAnswerAppCompatImageButton)
    public void sendCallbackOnClick() {
        Message message = mAdapter.getItem(mAdapter.getItemCount() - 1);
        if (message != null) {
            Hyber.sendMessageCallback(message.getId(), mInputAnswer.getText().toString(),
                    new HyberCallback<String, HyberError>() {
                        @Override
                        public void onSuccess(String result) {
                            Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(HyberError error) {
                            if (error.getStatus() == HyberError.HyberErrorStatus.UNAUTHORIZED) {
                                logout();
                            }
                        }
                    });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        unbinder = ButterKnife.bind(this, view);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setStackFromEnd(true);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MessagesRVAdapter(getActivity(), Hyber.getAllUserMessages());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnChangeListener(
                new RealmRecyclerViewAdapter.OnChangeListener() {
                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        try {
                            mRecyclerView.smoothScrollToPosition(positionStart + itemCount - 1);
                        } catch (NullPointerException e) {
                            HyberLogger.e(e, "mAdapter count %d, position %d",
                                    mAdapter.getItemCount(), positionStart + itemCount - 1);
                        }
                    }

                    @Override
                    public void onItemRangeRemoved(int positionStart, int itemCount) {

                    }

                    @Override
                    public void onItemRangeChanged(int positionStart, int itemCount) {

                    }
                });
        mAdapter.setOnMessageActionListener(new MessagesRVAdapter.OnMessageActionListener() {
            @Override
            public void onAction(@NonNull String action) {
                onMessageAction(action);
            }
        });

        if (mAdapter.getItemCount() > 0) {
            mAnswerLayout.setVisibility(View.VISIBLE);
        } else {
            mAnswerLayout.setVisibility(View.GONE);
        }

        mTimeForNextHistoryRequest = System.currentTimeMillis();
        getMessagesFromHistory(mTimeForNextHistoryRequest);

        return view;
    }

    private void getMessagesFromHistory(long historyFromThisTimeToPast) {
        mMaxHistoryRequests -= 1;
        Hyber.getMessageHistory(historyFromThisTimeToPast, new HyberCallback<Long, HyberError>() {
            @Override
            public void onSuccess(@NonNull Long recommendedNextTime) {
                mTimeForNextHistoryRequest = recommendedNextTime;
                if (mMaxHistoryRequests > 0)
                    getMessagesFromHistory(mTimeForNextHistoryRequest);
            }

            @Override
            public void onFailure(HyberError error) {
                if (error.getStatus() == HyberError.HyberErrorStatus.UNAUTHORIZED) {
                    logout();
                }
            }
        });
    }

    public void onMessageAction(@NonNull String action) {
        if (mListener != null) {
            mListener.onMessageActionInteraction(action);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMessagesFragmentInteractionListener) {
            mListener = (OnMessagesFragmentInteractionListener) context;
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
        unbinder.unbind();
        super.onDestroyView();
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnMessagesFragmentInteractionListener {
        void onMessageActionInteraction(@NonNull String action);
    }
}
