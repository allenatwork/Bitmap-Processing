package allen.bitmapprocressing.listphoto;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import allen.bitmapprocressing.R;
import allen.bitmapprocressing.model.Images;

/**
 * Created by Allen on 14-Apr-16.
 */
public class ListPhotoAdapter extends BaseAdapter {
    Context mContext;
    float density;
    private Bitmap mLoadingBitmap;
    int length;
    private static final int FADE_IN_TIME = 200;
    private LruCache<String, Bitmap> mCache;

    public ListPhotoAdapter(Context mContext) {
        this.mContext = mContext;
        density = mContext.getResources().getDisplayMetrics().density;
        length = (int) (density * 150);
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;
        mCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
    }

    @Override
    public int getCount() {
        return Images.limage.length;
    }

    @Override
    public Object getItem(int position) {
        return Images.limage[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item_view, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else vh = (ViewHolder) convertView.getTag();

        loadBitmap(Images.limage[position], vh.imageView);

        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;

        public ViewHolder(View view) {
            this.imageView = (ImageView) view;
        }
    }

    public class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {

        private final WeakReference<ImageView> imageViewReference;
        private int data = 0;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            data = params[0];
            Log.d("Cache", "Load bitmap from resource -" + data);
            Bitmap bm = decodeSampledBitmapFromResource(mContext.getResources(), data, length, length);
            addBitmaptoCache(String.valueOf(data), bm);
            return bm;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    setImageDrawable(imageView, new BitmapDrawable(mContext.getResources(), bitmap));
//                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    public void loadBitmap(int resId, ImageView imageView) {
        Bitmap bm = getBitmapfromCache(String.valueOf(resId));
        if (bm != null) {
            setImageDrawable(imageView, new BitmapDrawable(mContext.getResources(), bm));
            Log.d("Cache", "Load bitmap from Cache " + String.valueOf(resId));
        } else {
            if (cancelPotentialWork(resId, imageView)) {
                final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
                final AsyncDrawable asyncDrawable =
                        new AsyncDrawable(mContext.getResources(), mLoadingBitmap, task);
                setLoadingImage(R.drawable.empty_photo);
                imageView.setImageDrawable(asyncDrawable);
                task.execute(resId);
            }
        }
    }

    public static boolean cancelPotentialWork(int data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final int bitmapData = bitmapWorkerTask.data;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData == 0 || bitmapData != data) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        int a = (int) Math.sqrt(height / reqHeight);
        int b = (int) Math.sqrt(width / reqWidth);
        int log = a >= b ? b : a;
        return (int) (inSampleSize * Math.pow(2, log));
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
//        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inSampleSize = 8;
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    public void setLoadingImage(int resId) {
        mLoadingBitmap = BitmapFactory.decodeResource(mContext.getResources(), resId);
    }

    private void setImageDrawable(ImageView imageView, Drawable drawable) {
        // Transition drawable with a transparent drawable and the final drawable
        final TransitionDrawable td =
                new TransitionDrawable(new Drawable[]{
                        new ColorDrawable(mContext.getResources().getColor(android.R.color.transparent)),
                        drawable
                });

        imageView.setImageDrawable(td);
        td.startTransition(FADE_IN_TIME);
    }

    private Bitmap getBitmapfromCache(String key) {
        return mCache.get(key);
    }

    public void addBitmaptoCache(String key, Bitmap bitmap) {
        Log.d("Cache", "Save bitmap to cache " + key);
        if (getBitmapfromCache(key) == null) {
            mCache.put(key, bitmap);
        }
    }
}
