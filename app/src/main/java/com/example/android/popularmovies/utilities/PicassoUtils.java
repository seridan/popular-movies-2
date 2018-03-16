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

        if (url != null && url.length() > 0 && !url.contains("null")){
            Picasso.with(context).load(url).into(imageView);

        }else {
            Picasso.with(context).load(R.drawable.no_image_avalaible).into(imageView);
        }
    }
}
