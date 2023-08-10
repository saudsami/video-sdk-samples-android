package io.agora.android_reference_app;

import io.agora.agora_manager.AgoraManager;
import io.agora.call_quality_manager.CallQualityManager;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import io.agora.rtc2.IRtcEngineEventHandler;

public class CallQualityActivity extends AppCompatActivity {
    private CallQualityManager agoraManager;

    private TextView networkStatus; // For updating the network status
    private boolean isEchoTestRunning = false; // Keeps track of the echo test
    private Button echoTestButton, btnJoinLeave;
    FrameLayout remoteFrameLayout;

    private void updateNetworkStatus(int quality) {
        if (quality > 0 && quality < 3) networkStatus.setBackgroundColor(Color.GREEN);
        else if (quality <= 4) networkStatus.setBackgroundColor(Color.YELLOW);
        else if (quality <= 6) networkStatus.setBackgroundColor(Color.RED);
        else networkStatus.setBackgroundColor(Color.WHITE);
    }

    public void setStreamQuality(View view) {
       // agoraManager.switchStreamQuality();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_quality);
        // Find the root view of the included layout
        LinearLayout baseLayout = findViewById(R.id.base_layout);
        // Find the widgets inside the included layout using the root view
        btnJoinLeave = baseLayout.findViewById(R.id.btnJoinLeave);
        btnJoinLeave.setOnClickListener(this::joinLeave);
        remoteFrameLayout = baseLayout.findViewById(R.id.remote_video_view_container);

        agoraManager = new CallQualityManager(this);
        // Set the current product depending on your application
        agoraManager.setCurrentProduct(AgoraManager.ProductName.VIDEO_CALLING);

        agoraManager.setListener(new CallQualityManager.CallQualityManagerListener() {
            @Override
            public void onMessageReceived(String message) {
                showMessage(message);
            }

            @Override
            public void onRemoteUserJoined(int remoteUid, SurfaceView surfaceView) {

            }

            @Override
            public void onRemoteUserLeft(int remoteUid) {

            }

            public void onNetworkQuality(int uid, int txQuality, int rxQuality) {
                // Use down-link network quality to update the network status
                runOnUiThread(() -> updateNetworkStatus(rxQuality));
            }

            @Override
            public void onLastMileQuality(int quality) {
                runOnUiThread(() -> updateNetworkStatus(quality));
            }

            @Override
            public void onUserJoined(int uid) {
                setupOverlayText();
            }

            @Override
            public void onRemoteVideoStats(IRtcEngineEventHandler.RemoteVideoStats stats) {
                if (agoraManager.remoteUids.contains(stats.uid) ) {
                    String caption = "Renderer frame rate: " + stats.rendererOutputFrameRate
                            + "\nReceived bitrate: " + stats.receivedBitrate
                            + "\nPublish duration: " + stats.publishDuration
                            + "\nFrame loss rate: " + stats.frameLossRate;
                    runOnUiThread(() -> remoteStatsText.setText(caption)
                    );
                }
            }
        });

        RadioGroup radioGroup = findViewById(R.id.radioGroup);

        // Manage Broadcaster and Audience roles in Interactive live streaming
        if (agoraManager.getCurrentProduct()==AgoraManager.ProductName.INTERACTIVE_LIVE_STREAMING
                || agoraManager.getCurrentProduct()==AgoraManager.ProductName.BROADCAST_STREAMING) {
            radioGroup.setVisibility(View.VISIBLE);
        } else {
            radioGroup.setVisibility(View.GONE);
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId)
                -> agoraManager.setBroadcasterRole(checkedId == R.id.radioButtonBroadcaster));

        // Switch stream quality when a user taps the remote video
        remoteFrameLayout.setOnClickListener(this::setStreamQuality);

        // Start the probe test
        agoraManager.startProbeTest();

        networkStatus = findViewById(R.id.networkStatus);
        echoTestButton = findViewById(R.id.echoTestButton);
    }

    public void joinLeave(View view) {
        RadioGroup radioGroup = findViewById(R.id.radioGroup);

        if (!agoraManager.isJoined()) {
            int result = agoraManager.joinChannelWithToken();
            if (result == 0) {
                btnJoinLeave.setText(R.string.leave);
                if (radioGroup.getVisibility() != View.GONE) radioGroup.setVisibility(View.INVISIBLE);
            }
        } else {
            agoraManager.leaveChannel();
            btnJoinLeave.setText(R.string.join);
            if (radioGroup.getVisibility() != View.GONE) radioGroup.setVisibility(View.VISIBLE);
            removeOverlayText();
        }
    }

    public void echoTest(View view) {
        if (!isEchoTestRunning) {
            echoTestButton.setText("Stop Echo Test");
            agoraManager.startEchoTest();
            isEchoTestRunning = true;
        } else {
            agoraManager.stopEchoTest();
            echoTestButton.setText("Start Echo Test");
            isEchoTestRunning = false;
        }
    }

    TextView remoteStatsText;
    public void setupOverlayText() {
        // Create a new TextView
        remoteStatsText = new TextView(this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.gravity = Gravity.BOTTOM; // Set gravity to bottom left
        remoteStatsText.setLayoutParams(layoutParams);
        remoteStatsText.setTextSize(14);
        remoteStatsText.setTextColor(Color.WHITE);
        remoteStatsText.setPadding(10, 0, 0, 0);
        // Add the TextView to the FrameLayout
        runOnUiThread(() ->
                remoteFrameLayout.addView(remoteStatsText));
    }

    private void removeOverlayText() {
        runOnUiThread(() ->
                remoteFrameLayout.removeView(remoteStatsText));
        // Dispose the TextView
        remoteStatsText = null;
    }

    private void showMessage(String message) {
        runOnUiThread(() ->
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());
    }
}