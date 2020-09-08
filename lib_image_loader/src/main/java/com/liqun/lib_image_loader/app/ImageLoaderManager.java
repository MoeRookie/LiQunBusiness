package com.liqun.lib_image_loader.app;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.liqun.lib_image_loader.R;
import com.liqun.lib_image_loader.image.Utils;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 图片加载类, 与外界的唯一通信类
 * 支持为各种view, notification, appwidget, viewgroup加载图片
 */
public class ImageLoaderManager {

    public ImageLoaderManager() {
    }

    private static class SingleHolder{
        private static ImageLoaderManager instance = new ImageLoaderManager();
    }

    public static ImageLoaderManager getInstance(){
        return SingleHolder.instance;
    }

    /**
     * 为ImageView加载图片
     */
    public void displayImageForView(ImageView imageView, String url){
        Glide.with(imageView.getContext())
             .asBitmap()
             .load(url)
             .apply(initCommonRequestOption())
             .transition(BitmapTransitionOptions.withCrossFade())
             .into(imageView);
    }

    @SuppressLint("CheckResult")
    private RequestOptions initCommonRequestOption() {
        RequestOptions options = new RequestOptions();
        options.placeholder(R.mipmap.b4y)
               .error(R.mipmap.b4y)
               .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
               .skipMemoryCache(false)
               .priority(Priority.NORMAL);
        return options;
    }

    /**
     * 为imageView加载圆形图片
     * @param imageView
     * @param url
     */
    public void displayImageForCircle(final ImageView imageView, String url){
        Glide.with(imageView.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .into(new BitmapImageViewTarget(imageView){
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable drawable =
                                RoundedBitmapDrawableFactory.create(imageView.getResources(), resource);
                        drawable.setCircular(true);
                        imageView.setImageDrawable(drawable);
                    }
                });
    }

    /**
     * 完成为viewGroup设置背景并根据需要进行模糊处理
     * @param group
     * @param url
     */
    public void displayImageForViewGroup(final ViewGroup group, String url, final boolean isBlur){
        Glide.with(group.getContext())
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull final Bitmap resource,
                                                @Nullable Transition<? super Bitmap> transition) {
                        final Bitmap res = resource;
                        Observable.just(resource)
                                .map(new Function<Bitmap, Drawable>() {
                                    @SuppressLint("CheckResult")
                                    @Override
                                    public Drawable apply(Bitmap bitmap) {
                                        // 将bitmap根据需要进行模糊处理并转为drawable
                                        BitmapDrawable drawable = new BitmapDrawable(
                                                isBlur?Utils.doBlur(res,100, true):null
                                        );
                                        return drawable;
                                    }
                                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Drawable>() {
                            @Override
                            public void accept(Drawable drawable) throws Exception {
                                group.setBackground(drawable);
                            }
                        });
                    }
                });
    }

    /**
     * 为 notification 中的id控件加载图片
     * @param context
     * @param id
     * @param remoteViews
     * @param notification
     * @param notificationId
     * @param url
     */
    public void displayImageForNotification(Context context, int id,
                                            RemoteViews remoteViews, Notification notification,
                                            int notificationId, String url){
        displayImageForTarget(context, initNotificationTarget(
                context,id,remoteViews,notification, notificationId
        ), url);
    }

    // 构造一个notification target
    private NotificationTarget initNotificationTarget(Context context, int id,
                                                      RemoteViews remoteViews, Notification notification,
                                                      int notificationId){
        NotificationTarget target =
                new NotificationTarget(context, id, remoteViews, notification, notificationId);
        return target;
    }

    /**
     * 为非view加载图片
     * @param context
     * @param target
     * @param url
     */
    private void displayImageForTarget(Context context, Target target, String url){
        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(initCommonRequestOption())
                .transition(BitmapTransitionOptions.withCrossFade())
                .fitCenter()
                .into(target);
    }
}
