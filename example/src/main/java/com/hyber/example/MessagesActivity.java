package com.hyber.example;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.Toast;

import com.hyber.MessageRVAbstractAdapter;
import com.hyber.example.adapter.MyMessagesRVAdapter;

import java.net.URL;

import io.realm.Realm;
import retrofit2.http.Url;

public class MessagesActivity extends AppCompatActivity {

    private Realm realm;
    private RecyclerView mRecyclerView;
    private MyMessagesRVAdapter mAdapter;
    private AlertDialog mAlertDialog;

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
        mAdapter.setOnMessageActionListener(new MyMessagesRVAdapter.OnMessageActionListener() {
            @Override
            public void onAction(@NonNull String action) {
                if (mAlertDialog != null)
                    mAlertDialog.dismiss();
                if (URLUtil.isValidUrl(action)) {
                    WebView webView = new WebView(MessagesActivity.this);
                    webView.loadUrl(action);
                    mAlertDialog = new AlertDialog.Builder(MessagesActivity.this)
                            .setView(webView)
                            .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .create();
                    mAlertDialog.show();
                } else {
                    Toast.makeText(MessagesActivity.this, action, Toast.LENGTH_SHORT).show();
                }
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
