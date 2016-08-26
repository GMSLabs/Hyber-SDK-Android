package com.hyber.example;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hyber.MessageRVAbstractAdapter;
import com.hyber.example.adapter.MyMessagesRVAdapter;

import io.realm.Realm;

public class MessagesActivity extends AppCompatActivity {

    private Realm realm;
    private RecyclerView mRecyclerView;
    private MyMessagesRVAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.messages_RecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        realm = Realm.getDefaultInstance();
        mAdapter = new MyMessagesRVAdapter(this, realm);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnChangeListener(new MessageRVAbstractAdapter.OnChangeListener() {
            @Override
            public void onChange() {
                mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() + 1);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
