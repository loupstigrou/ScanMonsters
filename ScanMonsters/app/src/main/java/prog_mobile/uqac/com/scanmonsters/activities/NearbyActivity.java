package prog_mobile.uqac.com.scanmonsters.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.NearbyMessagesStatusCodes;
import com.google.android.gms.nearby.messages.PublishCallback;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;

import java.nio.charset.Charset;
import java.util.ArrayList;

import prog_mobile.uqac.com.scanmonsters.R;
import prog_mobile.uqac.com.scanmonsters.asynctasks.BasicService;
import prog_mobile.uqac.com.scanmonsters.nearby.DeviceMessage;
import prog_mobile.uqac.com.scanmonsters.user.SessionManager;

/**
 * Activité de recherche d'amis proche
 */
public class NearbyActivity extends InGameActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{


    private static final String TAG = "NearbyActivity";
    private View progressView;
    private View roomFoundedView;

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

    private ArrayAdapter<String> mNearbyDevicesArrayAdapter;

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
    private final ArrayList<String> mNearbyDevicesArrayList = new ArrayList<>();
    private boolean mResolvingError;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_room);

        this.progressView =    (View) findViewById(R.id.wait_room_info);
        this.roomFoundedView = (View) findViewById(R.id.search_room_info);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();


        // Publish bytes to send
        mMessage = new Message("Coucou".getBytes(Charset.forName("UTF-8")));
        /*Nearby.Messages.publish(mGoogleApiClient, mMessage)
                .setResultCallback(new ErrorCheckingCallback("publish()"));*/

        mMessageListener = new MessageListener() {
            @Override
            public void onFound(final Message message) {
                Log.i(TAG, "MessageListener onFound "+message.getContent());
                        //mNearbyDevicesArrayAdapter.add(DeviceMessage.fromNearbyMessage(message).getMessageBody());
            }
            @Override
            public void onLost(final Message message) {
                Log.i(TAG, "MessageListener connected "+message.getContent());

                // Called when a message is no onLost detectable nearby.
                      //  mNearbyDevicesArrayAdapter.remove(DeviceMessage.fromNearbyMessage(message).getMessageBody());
            }
        };

        // Subscribe to receive messages
       /* Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener)
                .setResultCallback(new ErrorCheckingCallback("subscribe()"));*/


        mDeviceInfoMessage = DeviceMessage.newNearbyMessage(InstanceID.getInstance(this.getApplicationContext()).getId());




    }


    private void arriveAtRoom()
    {
        Intent intent = new Intent(NearbyActivity.this, OCRActivity.class);
        startActivity(intent);
        finish();
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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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
