package prog_mobile.uqac.com.scanmonsters.activities;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import prog_mobile.uqac.com.scanmonsters.R;
import prog_mobile.uqac.com.scanmonsters.adapters.FriendListAdapter;
import prog_mobile.uqac.com.scanmonsters.database.Friend;
import prog_mobile.uqac.com.scanmonsters.nearby.DeviceMessage;

/**
 * Activité de recherche d'amis à proximité
 */
public class NearbyActivity extends InGameActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, FriendListAdapter.FriendAdapterListener {


    private static final String TAG = "NearbyActivity";
    private View progressView;
    private View nearbyDevicesFounded;


    private FriendListAdapter adapter;

    /**
     * Request code to use when launching the resolution activity.
     */
    static final int REQUEST_RESOLVE_ERROR = 1001;

    // The time-to-live when subscribing or publishing in this sample. Three minutes.
    static final int TTL_IN_SECONDS = 3 * 60;

    // Keys to get and set the current subscription and publication tasks using SharedPreferences.
    static final String KEY_SUBSCRIPTION_TASK = "subscription_task";
    static final String KEY_PUBLICATION_TASK = "publication_task";

    // Tasks 
    static final String TASK_SUBSCRIBE = "task_subscribe";
    static final String TASK_UNSUBSCRIBE = "task_unsubscribe";
    static final String TASK_PUBLISH = "task_publish";
    static final String TASK_UNPUBLISH = "task_unpublish";
    static final String TASK_NONE = "task_none";


    private GoogleApiClient mGoogleApiClient;
    private Message mDeviceInfoMessage;

    /**
     * A {@link MessageListener} for processing messages from nearby devices.
     */
    private MessageListener mMessageListener;


    private Message mMessage;

    /**
     * Tracks if we are currently resolving an error related to Nearby permissions. Used to avoid
     * duplicate Nearby permission dialogs if the user initiates both subscription and publication
     * actions without having opted into Nearby.
     */
    private boolean mResolvingNearbyPermissionError = false;

    /**
     * Backing data structure for {@code mNearbyDevicesArrayAdapter}.
     */
    private boolean mResolvingError;



    private Thread threadSendMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_devices);

        this.progressView =    (View) findViewById(R.id.wait_search_info);
        this.nearbyDevicesFounded = (View) findViewById(R.id.friendsList);

        showProgress(true, progressView, nearbyDevicesFounded);

        adapter = new FriendListAdapter(this);
        adapter.addListener(this);
        ListView list = (ListView)findViewById(R.id.friendsList);

        list.setAdapter(adapter);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();


        // Publish bytes to send
        String tmpMessageTxtToSend = user.getLogin();
        mMessage = new Message(tmpMessageTxtToSend.getBytes(Charset.forName("UTF-8")));


        mMessageListener = new MessageListener() {
            @Override
            public void onFound(final Message message) {
                Log.i(TAG, "MessageListener onFound "+message.getContent());
                String messageTxt;
                try {
                    messageTxt = new String(message.getContent(),"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    messageTxt = "Message inconnu";
                }
                Log.i(TAG, "MessageListener onFound " + messageTxt);
                addFriendFounded(messageTxt);

            }
            @Override
            public void onLost(final Message message) {
                Log.i(TAG, "MessageListener connected "+message.getContent());

                String messageTxt;
                try {
                    messageTxt = new String(message.getContent(),"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    messageTxt = "Message inconnu";
                }
                Log.i(TAG, "MessageListener onLost " + messageTxt);

                // Called when a message is no onLost detectable nearby.
                      //  mNearbyDevicesArrayAdapter.remove(DeviceMessage.fromNearbyMessage(message).getMessageBody());
            }
        };

        mDeviceInfoMessage = DeviceMessage.newNearbyMessage(InstanceID.getInstance(this.getApplicationContext()).getId());
    }

    public void addFriendFounded(final String name) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            //stuff that updates ui
                if(!adapter.containsFriendName(name))
                {
                    adapter.add(new Friend(name, 0));
                    adapter.invalidate();

                    showProgress(false, progressView, nearbyDevicesFounded);
                }

            }
        });
    }

    public void onClick(View view) {
        Intent intent;
        /*switch (view.getId()) {


            case R.id.refresh:
                try{
                    Nearby.Messages.publish(mGoogleApiClient, mMessage)
                            .setResultCallback(new ErrorCheckingCallback("publish()"));
                }catch(Exception e)
                {

                }

                break;

            case R.id.localise:
                intent = new Intent(this, LocateGoogleMapActivity.class);
                startActivity(intent);
                break;
        }*/
        //adapter.notifyDataSetChanged();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient.isConnected()) {
            // Clean up when the user leaves the activity.
            Nearby.Messages.unpublish(mGoogleApiClient, mMessage)
                    .setResultCallback(new ErrorCheckingCallback("unpublish()"));
            Nearby.Messages.unsubscribe(mGoogleApiClient, mMessageListener)
                    .setResultCallback(new ErrorCheckingCallback("unsubscribe()"));
        }
        if(threadSendMessage != null) threadSendMessage.interrupt();
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    // GoogleApiClient connection callback.
    @Override
    public void onConnected(Bundle connectionHint) {

        Nearby.Messages.getPermissionStatus(mGoogleApiClient).setResultCallback(
                new ErrorCheckingCallback("getPermissionStatus", new Runnable() {
                    @Override
                    public void run() {

                        publishAndSubscribe();

                        // thread envoyant un message toutes les 5 secondes
                        threadSendMessage = new Thread() {

                            @Override
                            public void run() {
                                try {
                                    while (!isInterrupted()) {
                                        Thread.sleep(5000);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                publishImHere(); // Send message to all
                                            }
                                        });
                                    }
                                } catch (InterruptedException e) {
                                }
                            }
                        };

                        threadSendMessage.start();
                    }
                })
        );
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    // This is called in response to a button tap in the Nearby permission dialog.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Permission granted or error resolved successfully then we proceed
                // with publish and subscribe..
                publishAndSubscribe();
            } else {
                // This may mean that user had rejected to grant nearby permission.
                Log.i(TAG, "Failed to resolve error with code " + resultCode);
            }
        }
    }

    private void publishAndSubscribe() {
        // We automatically subscribe to messages from nearby devices once
        // GoogleApiClient is connected. If we arrive here more than once during
        // an activity's lifetime, we may end up with multiple calls to
        // subscribe(). Repeated subscriptions using the same MessageListener
        // are ignored.
        Nearby.Messages.publish(mGoogleApiClient, mMessage)
                .setResultCallback(new ErrorCheckingCallback("publish()"));
        Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener)
                .setResultCallback(new ErrorCheckingCallback("subscribe()"));
    }

    private void publishImHere() {
        Nearby.Messages.publish(mGoogleApiClient, mMessage)
                .setResultCallback(new ErrorCheckingCallback("publish()"));
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onClickNom(Friend item, int position) {

    }

    /**
     * A simple ResultCallback that logs when errors occur.
     * It also displays the Nearby opt-in dialog when necessary.
     */
    private class ErrorCheckingCallback implements ResultCallback<Status> {
        private final String method;
        private final Runnable runOnSuccess;

        private ErrorCheckingCallback(String method) {
            this(method, null);
        }

        private ErrorCheckingCallback(String method, Runnable runOnSuccess) {
            this.method = method;
            this.runOnSuccess = runOnSuccess;
        }

        @Override
        public void onResult(Status status) {
            if (status.isSuccess()) {
                Log.i(TAG, method + " succeeded.");
                if (runOnSuccess != null) {
                    runOnSuccess.run();
                }
            } else {
                // Currently, the only resolvable error is that the device is not opted
                // in to Nearby. Starting the resolution displays an opt-in dialog.
                if (status.hasResolution()) {
                    if (!mResolvingError) {
                        try {
                            status.startResolutionForResult(NearbyActivity.this, REQUEST_RESOLVE_ERROR);
                            mResolvingError = true;
                        } catch (IntentSender.SendIntentException e) {
                            Log.e(TAG, method + " failed with exception: " + e);
                        }
                    } else {
                        // This will be encountered on initial startup because we do
                        // both publish and subscribe together.
                        Log.i(TAG, method + " failed with status: " + status
                                + " while resolving error.");
                    }
                } else {
                    Log.e(TAG, method + " failed with : " + status
                            + " resolving error: " + mResolvingError);
                }
            }
        }
    }
}
