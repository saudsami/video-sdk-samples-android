package io.agora.android_reference_app;

import io.agora.agora_manager.AgoraManager;
import io.agora.authentication_manager.AuthenticationManager;

import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

public class AuthenticationActivity2 extends BasicImplementationActivity {
    private AuthenticationManager agoraManager;
    //private Button btnJoinLeave;
    private EditText editChannelName; // To read the channel name from the UI.
    private EditText editServerUrl; // To read the server Url from the UI.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editChannelName = findViewById(R.id.editChannelName);
        editChannelName.setText(agoraManager.channelName);

        editServerUrl = findViewById(R.id.editServerUrl);
        editServerUrl.setText(agoraManager.serverUrl);
    }

    @Override
    protected void initializeAgoraManager() {
        agoraManager = new AuthenticationManager(this);
        // Set the current product depending on your application
        agoraManager.setCurrentProduct(AgoraManager.ProductName.VIDEO_CALLING);
        // Set up a listener for updating the UI
        agoraManager.setListener(agoraManagerListener);
    }

    @Override
    protected void join() {
        String channelName = editChannelName.getText().toString();
        agoraManager.serverUrl = editServerUrl.getText().toString();
        agoraManager.fetchToken(channelName, new AuthenticationManager.TokenCallback() {
            @Override
            public void onTokenReceived(String rtcToken) {
                // Handle the received rtcToken
                agoraManager.joinChannel(channelName, rtcToken);
                if (agoraManager.isBroadcaster) {
                    runOnUiThread(() -> {
                        // Display the local video
                        SurfaceView localVideoSurfaceView = agoraManager.getLocalVideo();
                        mainFrame.addView(localVideoSurfaceView);
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Handle the error
                System.out.println("Error: " + errorMessage);
            }
        });
        btnJoinLeave.setText(R.string.leave);
    }
}