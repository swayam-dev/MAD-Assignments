package com.example.localplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private VideoView videoView;
    private TextView tvStatus;
    private TextView tvVideoPlaceholder;
    private EditText etVideoUrl;
    private MediaPlayer audioPlayer;
    private boolean isVideoMode = false;

    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    setupAudio(result.getData().getData());
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.videoView);
        tvStatus = findViewById(R.id.tvStatus);
        tvVideoPlaceholder = findViewById(R.id.tvVideoPlaceholder);
        etVideoUrl = findViewById(R.id.etVideoUrl);

        findViewById(R.id.btnOpenFile).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            filePickerLauncher.launch(intent);
        });

        findViewById(R.id.btnOpenUrl).setOnClickListener(v -> {
            String url = etVideoUrl.getText().toString().trim();
            if (!url.isEmpty()) {
                setupVideo(Uri.parse(url));
            }
        });

        findViewById(R.id.btnPlay).setOnClickListener(v -> playMedia());
        findViewById(R.id.btnPause).setOnClickListener(v -> pauseMedia());
        findViewById(R.id.btnStop).setOnClickListener(v -> stopMedia());
        findViewById(R.id.btnRestart).setOnClickListener(v -> restartMedia());

        videoView.setOnCompletionListener(mp -> tvStatus.setText("Status: Completed"));
    }

    private void setupAudio(Uri uri) {
        releasePlayers();
        isVideoMode = false;
        try {
            audioPlayer = new MediaPlayer();
            audioPlayer.setDataSource(this, uri);
            audioPlayer.prepareAsync();
            audioPlayer.setOnPreparedListener(mp -> {
                tvStatus.setText("Status: Audio Ready");
                tvVideoPlaceholder.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.INVISIBLE);
            });
            audioPlayer.setOnCompletionListener(mp -> tvStatus.setText("Status: Completed"));
        } catch (Exception e) {
            tvStatus.setText("Status: Error loading audio");
        }
    }

    private void setupVideo(Uri uri) {
        releasePlayers();
        isVideoMode = true;
        videoView.setVisibility(View.VISIBLE);
        tvVideoPlaceholder.setVisibility(View.INVISIBLE);
        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(mp -> tvStatus.setText("Status: Video Ready"));
        videoView.setOnErrorListener((mp, what, extra) -> {
            tvStatus.setText("Status: Error loading video");
            return true;
        });
    }

    private void playMedia() {
        if (isVideoMode) {
            videoView.start();
            tvStatus.setText("Status: Playing Video");
        } else if (audioPlayer != null) {
            audioPlayer.start();
            tvStatus.setText("Status: Playing Audio");
        }
    }

    private void pauseMedia() {
        if (isVideoMode && videoView.isPlaying()) {
            videoView.pause();
            tvStatus.setText("Status: Paused");
        } else if (!isVideoMode && audioPlayer != null && audioPlayer.isPlaying()) {
            audioPlayer.pause();
            tvStatus.setText("Status: Paused");
        }
    }

    private void stopMedia() {
        if (isVideoMode) {
            videoView.pause();
            videoView.seekTo(0);
            tvStatus.setText("Status: Stopped");
        } else if (!isVideoMode && audioPlayer != null) {
            if (audioPlayer.isPlaying()) {
                audioPlayer.pause();
            }
            audioPlayer.seekTo(0);
            tvStatus.setText("Status: Stopped");
        }
    }

    private void restartMedia() {
        if (isVideoMode) {
            videoView.seekTo(0);
            videoView.start();
            tvStatus.setText("Status: Playing Video");
        } else if (!isVideoMode && audioPlayer != null) {
            audioPlayer.seekTo(0);
            audioPlayer.start();
            tvStatus.setText("Status: Playing Audio");
        }
    }

    private void releasePlayers() {
        if (audioPlayer != null) {
            audioPlayer.release();
            audioPlayer = null;
        }
        if (videoView.isPlaying()) {
            videoView.stopPlayback();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayers();
    }
}