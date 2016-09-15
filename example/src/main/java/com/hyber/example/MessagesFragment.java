package com.hyber.example;

import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hyber.Hyber;
import com.hyber.example.adapter.MyMessagesRVAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.HyberMessageHistoryBaseRecyclerViewAdapter;

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
    private MyMessagesRVAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private OnMessagesFragmentInteractionListener mListener;

    private Unbinder unbinder;

    @BindView(R.id.answerLayout)
    RelativeLayout mAnswerLayout;
    @BindView(R.id.inputAnswerEditTextView)
    AppCompatEditText mInputAnswer;
    @BindView(R.id.sendAnswerAppCompatImageButton)
    AppCompatImageButton mSendAnswer;

    public MessagesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MessagesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessagesFragment newInstance() {
        MessagesFragment fragment = new MessagesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //process arguments
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
        mAdapter = new MyMessagesRVAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnChangeListener(new HyberMessageHistoryBaseRecyclerViewAdapter.OnChangeListener() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mRecyclerView.smoothScrollToPosition(positionStart);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {

            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {

            }
        });
        mAdapter.setOnMessageActionListener(new MyMessagesRVAdapter.OnMessageActionListener() {
            @Override
            public void onAction(@NonNull String action) {
                onMessageAction(action);
            }
        });

        if (Hyber.isBidirectionalAvailable()) {
            mAnswerLayout.setVisibility(View.VISIBLE);
            mAdapter.setOnMessageAnswerListener(new MyMessagesRVAdapter.OnMessageAnswerListener() {
                @Override
                public void onAction(@NonNull final String messageId, @NonNull final String answerText) {
                    Toast.makeText(getActivity(), "messageId: " + messageId + "\n" + "answerText: " + answerText, Toast.LENGTH_SHORT).show();
                    Hyber.sendBidirectionalAnswer(messageId, answerText, new Hyber.SendBidirectionalAnswerHandler() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getActivity(), "Success sended!\nmessageId: " + messageId + "\n" + "answerText: " + answerText, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(String message) {
                            Toast.makeText(getActivity(), "Failure sended!\nmessageId: " + messageId + "\n" + "answerText: " + answerText + "\n" + message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } else {
            mAnswerLayout.setVisibility(View.GONE);
            mAdapter.setOnMessageAnswerListener(null);
        }

        return view;
    }

    @OnClick(R.id.sendAnswerAppCompatImageButton)
    public void onClickSendAnswerAppCompatImageButton(View v) {
        InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        final String messageId = mAdapter.getMessageId(mAdapter.getItemCount() - 1);
        final String answerText = mInputAnswer.getText().toString();
        if (!answerText.isEmpty()) {
            Hyber.sendBidirectionalAnswer(messageId, answerText, new Hyber.SendBidirectionalAnswerHandler() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getActivity(), "Success sended!\nmessageId: " + messageId + "\n" + "answerText: " + answerText, Toast.LENGTH_SHORT).show();
                    mInputAnswer.setText("");
                    mInputAnswer.clearFocus();
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(getActivity(), "Failure sended!\nmessageId: " + messageId + "\n" + "answerText: " + answerText + "\n" + message, Toast.LENGTH_SHORT).show();
                }
            });
        }
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
        super.onDestroyView();
        unbinder.unbind();
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
