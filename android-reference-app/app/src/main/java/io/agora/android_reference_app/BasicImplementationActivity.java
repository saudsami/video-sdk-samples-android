package io.agora.android_reference_app;

import io.agora.agora_manager.AgoraManager;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.RadioGroup;

public class BasicImplementationActivity extends AppCompatActivity {
    private AgoraManager agoraManager;
    private LinearLayout baseLayout;
    private Button btnJoinLeave;
    FrameLayout mainFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_implementation);
        // Find the root view of the included layout
        baseLayout = findViewById(R.id.base_layout);
        // Find the widgets inside the included layout using the root view
        btnJoinLeave = baseLayout.findViewById(R.id.btnJoinLeave);
        // Find the main video frame
        mainFrame = findViewById(R.id.main_video_container);
        // Create an instance of the AgoraManager class
        agoraManager = new AgoraManager(this);
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
                runOnUiThread(() -> {
                    LinearLayout linearLayout = findViewById(R.id.containerLayout);

                    // Create a new FrameLayout programmatically
                    FrameLayout remoteFrameLayout = new FrameLayout(getApplicationContext());
                    // Add the SurfaceView to the FrameLayout
                    remoteFrameLayout.addView(surfaceView);
                    // Set the layout parameters for the new FrameLayout
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            400,
                            LinearLayout.LayoutParams.MATCH_PARENT
                    );
                    layoutParams.setMargins(6,6,6,6);
                    // Set the background color for the new FrameLayout
                    remoteFrameLayout.setBackgroundResource(R.color.dark_gray);
                    // Set the id for the new FrameLayout
                    remoteFrameLayout.setId(remoteUid);
                    remoteFrameLayout.setOnClickListener(videoClickListener);
                    // Add the new FrameLayout to the parent LinearLayout
                    linearLayout.addView(remoteFrameLayout,layoutParams);
                });
            }

            @Override
            public void onRemoteUserLeft(int remoteUid) {
                // Assuming you have the reference to your LinearLayout and FrameLayout
                LinearLayout linearLayout = findViewById(R.id.containerLayout);
                FrameLayout frameLayoutToRemove = findViewById(remoteUid);

                // Remove the FrameLayout from the LinearLayout
                linearLayout.removeView(frameLayoutToRemove);
            }
        });

        RadioGroup radioGroup = findViewById(R.id.radioGroup);

        if (agoraManager.getCurrentProduct()==AgoraManager.ProductName.INTERACTIVE_LIVE_STREAMING
                || agoraManager.getCurrentProduct()==AgoraManager.ProductName.BROADCAST_STREAMING) {
            radioGroup.setVisibility(View.VISIBLE);
        } else {
            radioGroup.setVisibility(View.GONE);
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            agoraManager.setBroadcasterRole(checkedId == R.id.radioButtonBroadcaster);
        });
    }

    public void joinLeave(View view) {
        RadioGroup radioGroup = findViewById(R.id.radioGroup);

        if (!agoraManager.isJoined()) {
            int result = agoraManager.joinChannel();
            if (result == 0) {
                btnJoinLeave.setText("Leave");
                if (radioGroup.getVisibility() != View.GONE) radioGroup.setVisibility(View.INVISIBLE);
            }
            if (agoraManager.isBroadcaster) {
                //Show local video
                SurfaceView localVideoSurfaceView = agoraManager.getLocalVideo();
                mainFrame.addView(localVideoSurfaceView);
            }
        } else {
            agoraManager.leaveChannel();
            btnJoinLeave.setText("Join");
            if (radioGroup.getVisibility() != View.GONE) radioGroup.setVisibility(View.VISIBLE);
            // Remove remote views
            LinearLayout linearLayout = findViewById(R.id.containerLayout);
            for (int remoteUid : agoraManager.remoteUids) {
                FrameLayout frameLayoutToRemove = findViewById(remoteUid);
                // Remove the FrameLayout from the LinearLayout
                linearLayout.removeView(frameLayoutToRemove);
            }
            mainFrame.removeAllViews();
        }
    }

    View.OnClickListener videoClickListener = (new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // A small video frame was clicked
            // Swap the videos in the small frame and the main frame
            FrameLayout smallFrame = (FrameLayout) v;
            smallFrame.getId();

            SurfaceView surfaceViewMain = (SurfaceView) mainFrame.getChildAt(0);
            SurfaceView surfaceViewSmall = (SurfaceView) smallFrame.getChildAt(0);

            mainFrame.removeView(surfaceViewMain);
            smallFrame.removeView(surfaceViewSmall);

            mainFrame.addView(surfaceViewSmall);
            smallFrame.addView(surfaceViewMain);
        }
    });

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}