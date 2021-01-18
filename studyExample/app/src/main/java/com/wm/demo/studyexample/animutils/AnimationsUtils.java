package com.wm.demo.studyexample.animutils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.widget.ImageView;

import com.wm.demo.studyexample.MyApplication;
import com.wm.demo.studyexample.R;

import java.lang.ref.SoftReference;

/**
 * 描述：
 * 作者：WangMei on 2021/1/18 09:56
 * 邮箱：1026983393@qq.com
 * 版本：1.0.0
 *
 * @author :Ellen
 */
public class AnimationsUtils {
    /**
     * 每秒播放帧数，fps = 1/t，t-动画两帧时间间隔
     */
    private int FPS = 58;

    /**
     * 图片资源
     */
    private int resId = R.array.loading_anim;
    private Context mContext = MyApplication.getAppContext();
    private static AnimationsContainer mInstance;

    public AnimationsUtils() {
    }

    /**
     * 获取单例
     */
    public static AnimationsContainer getInstance(int resId, int fps) {
        if (mInstance == null) {
            mInstance = new AnimationsContainer();
        }
        mInstance.setResId(resId, fps);
        return mInstance;
    }

    public void setResId(int resId, int fps) {
        this.resId = resId;
        this.FPS = fps;
    }

    /**
     * @param imageView
     * @return progress dialog animation
     */
    public FramesSequenceAnimation createProgressDialogAnim(ImageView imageView) {
        return new FramesSequenceAnimation(imageView, getData(resId), FPS);
    }


    /**
     * 循环读取帧---循环播放帧
     */
    public class FramesSequenceAnimation {
        /***帧数组*/
        private int[] mFrames;
        /***当前帧*/
        private int mIndex;
        /***开始/停止播放用*/
        private boolean mShouldRun;
        /***动画是否正在播放，防止重复播放*/
        private boolean mIsRunning;
        /***软引用ImageView，以便及时释放掉*/
        private SoftReference<ImageView> mSoftReferenceImageView;
        private Handler mHandler;
        private int mDelayMillis;
        /***播放停止监听*/
        private OnAnimationStoppedListener mOnAnimationStoppedListener;
        /***播放结束监听*/
        private OnAnimationFinishedListener mOnAnimationFinishedListener;
        private Bitmap mBitmap = null;
        /***Bitmap管理类，可有效减少Bitmap的OOM问题*/
        private BitmapFactory.Options mBitmapOptions;

        public FramesSequenceAnimation(ImageView imageView, int[] frames, int fps) {
            mHandler = new Handler();
            mFrames = frames;
            mIndex = -1;
            mSoftReferenceImageView = new SoftReference<>(imageView);
            mShouldRun = false;
            mIsRunning = false;
            //帧动画时间间隔，毫秒
            //mDelayMillis = 1000 / fps;
            //暂时将fps当做两帧时间间隔，毫秒
            mDelayMillis = fps;

            imageView.setImageResource(mFrames[0]);

            // 当图片大小类型相同时进行复用，避免频繁GC
            if (Build.VERSION.SDK_INT >= 11) {
                Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                int width = bmp.getWidth();
                int height = bmp.getHeight();
                Bitmap.Config config = bmp.getConfig();
                mBitmap = Bitmap.createBitmap(width, height, config);
                mBitmapOptions = new BitmapFactory.Options();
                //设置Bitmap内存复用
                //Bitmap复用内存块，类似对象池，避免不必要的内存分配和回收
                mBitmapOptions.inBitmap = mBitmap;
                //解码时返回可变Bitmap
                mBitmapOptions.inMutable = true;
                //缩放比例
                mBitmapOptions.inSampleSize = 1;
            }
        }

        /**
         * 循环读取下一帧
         */
        private int getNext() {
            mIndex++;
            if (mIndex >= mFrames.length) {
                mIndex = 0;
            }
            return mFrames[mIndex];
        }

        /**
         * 循环读取下一帧
         */
        private void reset() {
            mIndex = -1;
            mIsRunning = false;
        }

        /**
         * 播放动画，同步锁防止多线程读帧时，数据安全问题
         */
        public synchronized void start() {
            mShouldRun = true;
            if (mIsRunning) {
                return;
            }
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    ImageView imageView = mSoftReferenceImageView.get();
                    if (!mShouldRun || imageView == null) {
                        mIsRunning = false;
                        if (mOnAnimationStoppedListener != null) {
                            mOnAnimationStoppedListener.AnimationStopped();
                        }
                        return;
                    }
                    mIsRunning = true;
                    //新开线程去读下一帧
                    mHandler.postDelayed(this, mDelayMillis);
                    if (imageView.isShown()) {
                        int imageRes = getNext();
                        if (mBitmap != null) {
                            // so Build.VERSION.SDK_INT >= 11
                            Bitmap bitmap = null;
                            try {
                                bitmap = BitmapFactory.decodeResource(imageView.getResources(), imageRes, mBitmapOptions);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (bitmap != null) {
                                imageView.setImageBitmap(bitmap);
                            } else {
                                imageView.setImageResource(imageRes);
                                mBitmap.recycle();
                                mBitmap = null;
                            }
                        } else {
                            imageView.setImageResource(imageRes);
                        }
                    }
                    if (mFrames.length == mIndex + 1) {
                        if (mOnAnimationFinishedListener != null) {
                            mOnAnimationFinishedListener.AnimationFinished();
                            stop();
                        }
                    }
                }
            };
            mHandler.post(runnable);
        }

        /**
         * 停止播放
         */
        public synchronized void stop() {
            mShouldRun = false;
            reset();
        }

        /**
         * 设置停止播放监听
         *
         * @param listener
         */
        public void setOnAnimStopListener(OnAnimationStoppedListener listener) {
            this.mOnAnimationStoppedListener = listener;
        }

        public void setmOnAnimationFinishedListener(OnAnimationFinishedListener mOnAnimationFinishedListener) {
            this.mOnAnimationFinishedListener = mOnAnimationFinishedListener;
        }

        public boolean isRunning() {
            return mIsRunning;
        }
    }

    /**
     * 从xml中读取帧数组
     *
     * @param resId
     * @return
     */
    private int[] getData(int resId) {
        TypedArray array = mContext.getResources().obtainTypedArray(resId);

        int len = array.length();
        int[] intArray = new int[array.length()];

        for (int i = 0; i < len; i++) {
            intArray[i] = array.getResourceId(i, 0);
        }
        array.recycle();
        return intArray;
    }

    /**
     * 停止播放监听
     */
    public interface OnAnimationStoppedListener {
        /**
         * 停止播放
         */
        void AnimationStopped();
    }

    /**
     * 结束播放监听
     */
    public interface OnAnimationFinishedListener {
        /**
         * 结束播放
         */
        void AnimationFinished();
    }
}
