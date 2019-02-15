package com.hw5.juri.hw5;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Locale;

public class NewsFragment extends Fragment{
    public NewsFragment() {

    }

    public static NewsFragment newInstance(News news, int index, int max)
    {
        NewsFragment f = new NewsFragment();
        Bundle bdl = new Bundle(1);
        bdl.putSerializable("NEWS", news);
        bdl.putSerializable("INDEX", index);
        bdl.putSerializable("TOTAL_COUNT", max);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment_layout = inflater.inflate(R.layout.fragment_news, container, false);

        final News currentNews = (News) getArguments().getSerializable("NEWS");
        int index = getArguments().getInt("INDEX");
        int total = getArguments().getInt("TOTAL_COUNT");

        TextView title = fragment_layout.findViewById(R.id.title);
        title.setText(currentNews.getTitle());
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click(currentNews.getUrl());
            }
        });

        TextView time = fragment_layout.findViewById(R.id.time);
        time.setText(currentNews.getPublishedAt());

        TextView author = fragment_layout.findViewById(R.id.author);
        if(!currentNews.getAuthor().equals("null")){
            author.setText(currentNews.getAuthor());
        }else{
            author.setVisibility(View.INVISIBLE);
        }

        TextView text = fragment_layout.findViewById(R.id.text);
        text.setText(currentNews.getDescription().equals("null")?"":currentNews.getDescription());
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click(currentNews.getUrl());
            }
        });

        final ImageView imageView = fragment_layout.findViewById(R.id.image);
        imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click(currentNews.getUrl());
            }
        });
        Picasso picasso = new Picasso.Builder(this.getContext()).build();;
        picasso.load(currentNews.getUrlToImage()).fit().centerCrop()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.brokenimage)
                .into(imageView);

        TextView pageNum = fragment_layout.findViewById(R.id.pageNum);
        pageNum.setText(String.format(Locale.US, "%d of %d", index, total));
        return fragment_layout;
    }

    public void click(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
