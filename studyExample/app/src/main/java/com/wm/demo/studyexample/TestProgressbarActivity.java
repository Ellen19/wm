package com.wm.demo.studyexample;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * TITLE
 * Created by shixiaoming on 17/1/3.
 */

public class TestProgressbarActivity extends AppCompatActivity {
    private ProgressBar imageView;
    private TextView textView;
    private Button playBtn;
    private boolean start = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_test);

        imageView = findViewById(R.id.imgview);
        textView = findViewById(R.id.title);
        playBtn = findViewById(R.id.play_btn);

        textView.setText("普通帧动画");
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!switchBtn()) {
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    imageView.setVisibility(View.GONE);
                }

            }
        });
    }

    //控制开关
    private boolean switchBtn() {
        boolean returnV = start;
        start = !start;
        playBtn.setText(start == false ? "START" : "STOP");
        return returnV;
    }
}