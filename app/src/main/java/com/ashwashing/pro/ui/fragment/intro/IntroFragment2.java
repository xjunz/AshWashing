package com.ashwashing.pro.ui.fragment.intro;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ashwashing.pro.R;

public class IntroFragment2 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View content = inflater.inflate(R.layout.fragment_intro_2, container, false);
        content.findViewById(R.id.btn_learn_more).setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://developer.android.google.cn/guide/topics/connectivity/bluetooth"));
            Intent.createChooser(i, getString(R.string.learn_more));
            startActivity(i);
        });
        return content;
    }
}
