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
        String buildImageUrl;
        buildImageUrl = NetworkUtils.buildImageUrl(url).toString();
        if (buildImageUrl != null && buildImageUrl.length() > 0 && !buildImageUrl.contains("null")){
            Picasso.with(context).load(buildImageUrl).into(imageView);

        }else {
            Picasso.with(context).load(R.drawable.no_image_avalaible).into(imageView);
        }
    }
}
