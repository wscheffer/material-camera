package com.afollestad.materialcamera.internal;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;

import com.afollestad.materialcamera.R;
import com.afollestad.materialcamera.util.ImageUtil;

/**
 * Created by tomiurankar on 04/03/16.
 */
public class StillshotPreviewFragment extends BaseGalleryFragment {


    private ImageView mImageView;
    /**
     * Reference to the bitmap, in case 'onConfigurationChange' event comes, so we do not recreate the bitmap
     */
    private Bitmap mBitmap;

    public static StillshotPreviewFragment newInstance(String outputUri, boolean allowRetry, int primaryColor) {
        StillshotPreviewFragment fragment = new StillshotPreviewFragment();
        fragment.setRetainInstance(true);
        Bundle args = new Bundle();
        args.putString("output_uri", outputUri);
        args.putBoolean(CameraIntentKey.ALLOW_RETRY, allowRetry);
        args.putInt(CameraIntentKey.PRIMARY_COLOR, primaryColor);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mcam_fragment_stillshot, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImageView = (ImageView) view.findViewById(R.id.stillshot_imageview);

        ((Button) mConfirm).setText(mInterface.labelConfirm());
        ((Button) mRetry).setText(mInterface.labelRetry());

        mRetry.setOnClickListener(this);
        mConfirm.setOnClickListener(this);

        mImageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                setImageBitmap();
                mImageView.getViewTreeObserver().removeOnPreDrawListener(this);

                return true;
            }
        });
    }

    /**
     * Sets bitmap to ImageView widget
     */
    private void setImageBitmap() {

        final int width = mImageView.getMeasuredWidth();
        final int height = mImageView.getMeasuredHeight();

        if (mBitmap == null) {
            Bitmap bitmap = ImageUtil.getRotatedBitmap(Uri.parse(mOutputUri).getPath(), width, height);
            mBitmap = bitmap;
        }

        if (mBitmap == null)
            showDialog("Image preview error", "Could not decode bitmap");
        else
            mImageView.setImageBitmap(mBitmap);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.retry)
            mInterface.onRetry(mOutputUri);
        else if (v.getId() == R.id.confirm)
            mInterface.useVideo(mOutputUri);
    }
}
