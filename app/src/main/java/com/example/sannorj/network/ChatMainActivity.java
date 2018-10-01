package com.example.sannorj.network;

import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ibm.mobilefirstplatform.clientsdk.android.analytics.api.*;
import com.ibm.mobilefirstplatform.clientsdk.android.core.api.*;
import com.ibm.mobilefirstplatform.clientsdk.android.logger.api.*;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneHelper;
import com.ibm.watson.developer_cloud.android.library.audio.MicrophoneInputStream;
import com.ibm.watson.developer_cloud.android.library.audio.StreamPlayer;
import com.ibm.watson.developer_cloud.conversation.v1.Conversation;
import com.ibm.watson.developer_cloud.conversation.v1.model.InputData;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageOptions;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech;

import org.json.JSONObject;

import java.util.ArrayList;

public class ChatMainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter mAdapter;
    private ArrayList messageArrayList;
    private EditText inputMessage;
    private ImageButton btnSend;


    private com.ibm.watson.developer_cloud.conversation.v1.model.Context context = null;
    private boolean initialRequest;
    private Context mContext;
    private String workspace_id;
    private String conversation_username;
    private String conversation_password;
    private Logger myLogger;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatactivity_main);

        mContext = getApplicationContext();
        conversation_username = mContext.getString(R.string.conversation_username);
        conversation_password = mContext.getString(R.string.conversation_password);
        workspace_id = mContext.getString(R.string.workspace_id);



        BMSClient.getInstance().initialize(getApplicationContext(), BMSClient.REGION_SYDNEY);
        myLogger = Logger.getLogger("myLogger");
               Analytics.send(new ResponseListener() {
            @Override
            public void onSuccess(Response response) {

            }

            @Override
            public void onFailure(Response response, Throwable throwable, JSONObject jsonObject) {

            }
        });


        Logger.send(new ResponseListener() {
            @Override
            public void onSuccess(Response response) {

            }

            @Override
            public void onFailure(Response response, Throwable throwable, JSONObject jsonObject) {

            }
        });

        inputMessage = (EditText) findViewById(R.id.message);
        btnSend = (ImageButton) findViewById(R.id.btn_send);

        String customFont = "Montserrat-Regular.ttf";
        Typeface typeface = Typeface.createFromAsset(getAssets(), customFont);
        inputMessage.setTypeface(typeface);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        messageArrayList = new ArrayList<>();
        mAdapter = new ChatAdapter(messageArrayList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        this.inputMessage.setText("");
        this.initialRequest = true;
        sendMessage();



        btnSend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(checkInternetConnection()) {
                    sendMessage();
                }
            }
        });


    };


    private void sendMessage() {

        final String inputmessage = this.inputMessage.getText().toString().trim();
        if(!this.initialRequest) {
            Message inputMessage = new Message();
            inputMessage.setMessage(inputmessage);
            inputMessage.setId("1");
            messageArrayList.add(inputMessage);
            myLogger.info("Sending a message to Watson Conversation Service");

        }
        else
        {
            Message inputMessage = new Message();
            inputMessage.setMessage(inputmessage);
            inputMessage.setId("100");
            this.initialRequest = false;


        }

        this.inputMessage.setText("");
        mAdapter.notifyDataSetChanged();

        Thread thread = new Thread(new Runnable(){
            public void run() {
                try {


                    Conversation service = new Conversation("2018-07-10");
                    service.setUsernameAndPassword(conversation_username, conversation_password);
                    InputData input = new InputData.Builder(inputmessage).build();
                    MessageOptions options = new MessageOptions.Builder(workspace_id).input(input).context(context).build();
                    MessageResponse response = service.message(options).execute();


                    if(response.getContext() !=null)
                    {

                        context = response.getContext();

                    }
                    final Message outMessage=new Message();
                    if(response!=null)
                    {
                        if(response.getOutput()!=null && response.getOutput().containsKey("text"))
                        {

                            ArrayList responseList = (ArrayList) response.getOutput().get("text");
                            if(null !=responseList && responseList.size()>0){
                                outMessage.setMessage((String)responseList.get(0));
                                outMessage.setId("2");
                            }
                            messageArrayList.add(outMessage);
                            Thread thread = new Thread(new Runnable() {
                                public void run() {
                                    Message audioMessage;

                                }
                            });
                            thread.start();
                        }

                        runOnUiThread(new Runnable() {
                            public void run() {
                                mAdapter.notifyDataSetChanged();
                                if (mAdapter.getItemCount() > 1) {
                                    recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount()-1);

                                }

                            }
                        });


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }


    private boolean checkInternetConnection() {

        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();


        if (isConnected){
            return true;
        }
        else {
            Toast.makeText(this, " No Internet Connection available ", Toast.LENGTH_LONG).show();
            return false;
        }

    }

    private void showError(final Exception e) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                Toast.makeText(ChatMainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

}
