package io.agora.android_reference_app;

import io.agora.agora_manager.AgoraManager;
import io.agora.authentication_manager.AuthenticationManager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.RadioGroup;

public class AuthenticationActivity extends AppCompatActivity {
    private AuthenticationManager agoraManager;
    private Button btnJoinLeave;
    private EditText editChannelName; // To read the channel name from the UI.
    private EditText editServerUrl; // To read the server Url from the UI.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        // Find the root view of the included layout
        LinearLayout baseLayout = findViewById(R.id.base_layout);
        // Find the widgets inside the included layout using the root view
        btnJoinLeave = baseLayout.findViewById(R.id.btnJoinLeave);
        // Create an instance of the AgoraManager class
        agoraManager = new AuthenticationManager(this);
        // Set the current product depending on your application
        agoraManager.setCurrentProduct(AgoraManager.ProductName.VIDEO_CALLING);
        agoraManager.setVideoFrameLayouts(
                baseLayout.findViewById(R.id.main_video_container),
                baseLayout.findViewById(R.id.remote_video_view_container)
        );
        agoraManager.setListener(new AgoraManager.AgoraManagerListener() {
            @Override
            public void onMessageReceived(String message) {
                runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onRemoteUserJoined(int remoteUid, SurfaceView surfaceView) {

            }

            @Override
            public void onRemoteUserLeft(int remoteUid) {

            }
        });

        RadioGroup radioGroup = findViewById(R.id.radioGroup);

        if (agoraManager.getCurrentProduct()==AgoraManager.ProductName.INTERACTIVE_LIVE_STREAMING
                || agoraManager.getCurrentProduct()==AgoraManager.ProductName.BROADCAST_STREAMING) {
            radioGroup.setVisibility(View.VISIBLE);
        } else {
            radioGroup.setVisibility(View.GONE);
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) ->
                agoraManager.setBroadcasterRole(checkedId == R.id.radioButtonBroadcaster));

        editChannelName = findViewById(R.id.editChannelName);
        editChannelName.setText(agoraManager.channelName);

        editServerUrl = findViewById(R.id.editServerUrl);
        editServerUrl.setText(agoraManager.serverUrl);
    }

    public void joinLeave(View view) {
        RadioGroup radioGroup = findViewById(R.id.radioGroup);

        if (!agoraManager.isJoined()) {
            String channelName = editChannelName.getText().toString();
            agoraManager.serverUrl = editServerUrl.getText().toString();
            agoraManager.fetchToken(channelName, new AuthenticationManager.TokenCallback() {
                @Override
                public void onTokenReceived(String rtcToken) {
                    // Handle the received rtcToken
                    agoraManager.joinChannel(channelName, rtcToken);
                }

                @Override
                public void onError(String errorMessage) {
                    // Handle the error
                    System.out.println("Error: " + errorMessage);
                }
            });
            btnJoinLeave.setText(R.string.leave);
        } else {
            agoraManager.leaveChannel();
            btnJoinLeave.setText(R.string.join);
            if (radioGroup.getVisibility() != View.GONE) radioGroup.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}