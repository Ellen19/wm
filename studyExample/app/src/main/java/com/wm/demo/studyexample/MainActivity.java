package com.wm.demo.studyexample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author 80021799
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView img_show;
    private TextView tv_show;
    private Animation animation = null;
    private ScaleAnimation scaleAnimation;
    private AlphaAnimation alphaAnimation;
    private Button normal, advance, progress;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img_show = findViewById(R.id.img_show);
        tv_show = findViewById(R.id.tv_show);

        normal = findViewById(R.id.normal_anim);
        advance = findViewById(R.id.advance_anim);
        progress = findViewById(R.id.progressbar_anim);

        normal.setOnClickListener(this);
        advance.setOnClickListener(this);
        progress.setOnClickListener(this);

        alphaAnimation = (AlphaAnimation) AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        scaleAnimation = (ScaleAnimation) AnimationUtils.loadAnimation(this, R.anim.anim_scale);

        findViewById(R.id.btn_alpha).setOnClickListener(this);
        findViewById(R.id.btn_scale).setOnClickListener(this);
        findViewById(R.id.btn_tran).setOnClickListener(this);
        findViewById(R.id.btn_rotate).setOnClickListener(this);
        findViewById(R.id.btn_set).setOnClickListener(this);

        img_show.postDelayed(new Runnable() {
            @Override
            public void run() {
                img_show.setVisibility(View.VISIBLE);
                img_show.startAnimation(scaleAnimation);
            }
        }, 2000);

        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tv_show.setVisibility(View.VISIBLE);
                tv_show.startAnimation(alphaAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_alpha:
                animation = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
                img_show.startAnimation(animation);
                break;
            case R.id.btn_scale:
                animation = AnimationUtils.loadAnimation(this, R.anim.anim_scale);
                img_show.startAnimation(animation);
                break;
            case R.id.btn_tran:
                animation = AnimationUtils.loadAnimation(this, R.anim.anim_translate);
                img_show.startAnimation(animation);
                break;
            case R.id.btn_rotate:
                animation = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
                img_show.startAnimation(animation);
                break;
            case R.id.btn_set:
                animation = AnimationUtils.loadAnimation(this, R.anim.anim_set);
                img_show.startAnimation(animation);
                tv_show.startAnimation(animation);
                break;
            case R.id.normal_anim:
                intent = new Intent(MainActivity.this, TestActivity.class);
                intent.putExtra("mode", 1);
                startActivity(intent);
                break;
            case R.id.advance_anim:
                intent = new Intent(MainActivity.this, TestActivity.class);
                intent.putExtra("mode", 2);
                startActivity(intent);
                break;
            case R.id.progressbar_anim:
                intent = new Intent(MainActivity.this, TestProgressbarActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
