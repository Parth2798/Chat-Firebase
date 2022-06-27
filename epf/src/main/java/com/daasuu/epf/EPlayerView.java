package com.daasuu.epf;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.daasuu.epf.chooser.EConfigChooser;
import com.daasuu.epf.contextfactory.EContextFactory;
import com.daasuu.mp4compose.filter.GlFilter;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.video.VideoSize;

/**
 * Created by sudamasayuki on 2017/05/16.
 */
public class EPlayerView extends GLSurfaceView implements AnalyticsListener {

    private final static String TAG = EPlayerView.class.getSimpleName();

    private final EPlayerRenderer renderer;
    private SimpleExoPlayer player;

    private float videoAspect = 1f;
    private PlayerScaleType playerScaleType = PlayerScaleType.RESIZE_FIT_WIDTH;

    public EPlayerView(Context context) {
        this(context, null);
    }

    public EPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setEGLContextFactory(new EContextFactory());
        setEGLConfigChooser(new EConfigChooser());

        renderer = new EPlayerRenderer(this);
        setRenderer(renderer);

    }

    public EPlayerView setSimpleExoPlayer(SimpleExoPlayer player) {
        if (this.player != null) {
            this.player.release();
            this.player = null;
        }
        this.player = player;
        this.player.addAnalyticsListener(this);
        this.renderer.setSimpleExoPlayer(player);
        return this;
    }

    public void setGlFilter(GlFilter glFilter) {
        renderer.setGlFilter(glFilter);
    }

    public void setPlayerScaleType(PlayerScaleType playerScaleType) {
        this.playerScaleType = playerScaleType;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        int viewWidth = measuredWidth;
        int viewHeight = measuredHeight;

//        switch (playerScaleType) {
//            case RESIZE_FIT_WIDTH:
//                viewHeight = (int) (measuredWidth / videoAspect);
//                break;
//            case RESIZE_FIT_HEIGHT:
//                viewWidth = (int) (measuredHeight * videoAspect);
//                break;
//        }

         Log.e(TAG, "onMeasure viewWidth = " + viewWidth + " viewHeight = " + viewHeight);

        setMeasuredDimension(viewWidth, viewHeight);

    }

    @Override
    public void onPause() {
        super.onPause();
        renderer.release();
    }

    //////////////////////////////////////////////////////////////////////////
    // SimpleExoPlayer.VideoListener


    @Override
    public void onVideoSizeChanged(@Nullable EventTime eventTime, @Nullable VideoSize videoSize) {
        if (eventTime != null && videoSize != null) {
            AnalyticsListener.super.onVideoSizeChanged(eventTime, videoSize);

            // Log.d(TAG, "width = " + width + " height = " + height + " unappliedRotationDegrees = " + unappliedRotationDegrees + " pixelWidthHeightRatio = " + pixelWidthHeightRatio);
            videoAspect = ((float) videoSize.width / videoSize.height) * videoSize.pixelWidthHeightRatio;
            // Log.d(TAG, "videoAspect = " + videoAspect);
            requestLayout();
        }
    }
}
