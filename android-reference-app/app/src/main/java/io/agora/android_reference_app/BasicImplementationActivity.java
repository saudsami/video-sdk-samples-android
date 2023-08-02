package io.agora.android_reference_app;

import io.agora.agora_manager.AgoraManager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.RadioGroup;

public class get_started_sdk_activity extends AppCompatActivity {
    private AgoraManager agoraManager;
    private LinearLayout baseLayout;
    private Button btnJoinLeave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_started_sdk_layout);
        // Find the root view of the included layout
        baseLayout = findViewById(R.id.base_layout);
        // Find the widgets inside the included layout using the root view
        btnJoinLeave = baseLayout.findViewById(R.id.btnJoinLeave);
        // Create an instance of the AgoraManager class
        agoraManager = new AgoraManager(this);
        // Set the current product depending on your application
        agoraManager.setCurrentProduct(AgoraManager.ProductName.VIDEO_CALLING);
        agoraManager.setVideoFrameLayouts(
                baseLayout.findViewById(R.id.local_video_view_container),
                baseLayout.findViewById(R.id.remote_video_view_container)
        );
        agoraManager.setListener(new AgoraManager.AgoraManagerListener() {
            @Override
            public void onMessageReceived(String message) {
                runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());
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
        } else {
            agoraManager.leaveChannel();
            btnJoinLeave.setText("Join");
            if (radioGroup.getVisibility() != View.GONE) radioGroup.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}