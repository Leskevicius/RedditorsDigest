package com.magicbuddha.redditorsdigest.submissions;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.magicbuddha.redditorsdigest.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FullScreenPicture extends AppCompatActivity {

    public  static final String URL_EXTRA = "URL_EXTRA";
    @BindView(R.id.full_screen_image)
    ImageView fullScreenImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_picture);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String url = intent.getStringExtra(URL_EXTRA);

        Glide.with(this)
                .load(url)
                .error(R.drawable.ic_cannot_load_picture)
                .into(fullScreenImage);
    }
}