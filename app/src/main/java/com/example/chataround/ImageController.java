package com.example.chataround;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.NetworkPolicy;

import java.io.ByteArrayOutputStream;

public class ImageController {

    private static ImageView croppedImageView,expandedImageView,expandedImageView1;
    private static RelativeLayout container1, container2;
    public static boolean isFullscreen=false;

    public static Bitmap ResizeImage(Bitmap bitmap, int maxSize){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float ratio = (float)width/(float)height;
        if(ratio>1){
            width = maxSize;
            height = (int)(width/ratio);
        }else{
            height = maxSize;
            width = (int)(height*ratio);
        }
        return Bitmap.createScaledBitmap(bitmap,width,height,true);
    }

    public static byte[] BitmapToBytes(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();
        return byteArray;
    }

    public static void setViews(ImageView expanded1, ImageView expanded2, ImageView cropped,
                                RelativeLayout layout1, RelativeLayout layout2){
        expandedImageView=expanded1;
        expandedImageView1=expanded2;
        croppedImageView=cropped;
        container1=layout1;
        container2=layout2;
    }

    public static void zoomImageIn(final Activity activity, final String imageId) {
        PicassoCache.getPicassoInstance(activity).load(imageId).
                networkPolicy(NetworkPolicy.OFFLINE).into(expandedImageView);

        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        croppedImageView.getGlobalVisibleRect(startBounds);
        container1.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        float startScale;
        if((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        expandedImageView.setVisibility(View.VISIBLE);

        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(200);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                expandedImageView1.setImageDrawable(expandedImageView.getDrawable());
                container2.setVisibility(View.VISIBLE);
                container1.setVisibility(View.INVISIBLE);
                isFullscreen=true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
        set.start();
    }

    public static void zoomImageOut(Activity activity){
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        croppedImageView.getGlobalVisibleRect(startBounds);
        container2.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        container2.setVisibility(View.GONE);
        container1.setVisibility(View.VISIBLE);
        expandedImageView.setVisibility(View.VISIBLE);

        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator
                .ofFloat(expandedImageView, View.X, startBounds.left))
                .with(ObjectAnimator
                        .ofFloat(expandedImageView,
                                View.Y,startBounds.top))
                .with(ObjectAnimator
                        .ofFloat(expandedImageView,
                                View.SCALE_Y, startScale))
                .with(ObjectAnimator
                        .ofFloat(expandedImageView,
                                View.SCALE_X, startScale));
        set.setDuration(200);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                expandedImageView.setVisibility(View.GONE);
                isFullscreen=false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
        set.start();
    }
}
