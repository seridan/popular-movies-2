package com.example.android.popularmovies.utilities;

import android.content.Context;
import android.widget.ImageView;

import com.example.android.popularmovies.R;
import com.squareup.picasso.Picasso;

/**
 * Created by seridan on 10/03/2018.
 */

public class PicassoUtils {

    public static void getImageFromUrl (Context context, String url, ImageView imageView){

        if (url != null && url.length() > 0){
            Picasso.with(context).load(url).placeholder(R.mipmap.ic_launcher).into(imageView);

        }else {
            Picasso.with(context).load(R.mipmap.ic_launcher).into(imageView);
        }
    }
}
