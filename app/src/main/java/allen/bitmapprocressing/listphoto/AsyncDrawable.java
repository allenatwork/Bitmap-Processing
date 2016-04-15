package allen.bitmapprocressing.listphoto;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.lang.ref.WeakReference;

/**
 * Created by Allen on 15-Apr-16.
 */
public class AsyncDrawable extends BitmapDrawable {
    private final WeakReference<ListPhotoAdapter.BitmapWorkerTask> bitmapWorkerTaskReference;

    public AsyncDrawable(Resources res, Bitmap bitmap,
                         ListPhotoAdapter.BitmapWorkerTask bitmapWorkerTask) {
        super(res, bitmap);
        bitmapWorkerTaskReference =
                new WeakReference<>(bitmapWorkerTask);
    }

    public ListPhotoAdapter.BitmapWorkerTask getBitmapWorkerTask() {
        return bitmapWorkerTaskReference.get();
    }
}
