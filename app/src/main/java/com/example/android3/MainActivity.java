package com.example.android3;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // Create and set the game view
        gameView = new GameView(this);
        setContentView(gameView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // TODO: Pause game
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO: Resume game
    }
}