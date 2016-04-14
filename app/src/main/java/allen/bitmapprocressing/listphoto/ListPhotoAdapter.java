package allen.bitmapprocressing.listphoto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import allen.bitmapprocressing.R;
import allen.bitmapprocressing.model.Images;

/**
 * Created by Allen on 14-Apr-16.
 */
public class ListPhotoAdapter extends BaseAdapter {

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
        float density = parent.getContext().getResources().getDisplayMetrics().density;
        int inSampleSize = 1;
        int length = (int) (density * 150);
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item_view, parent, false);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else vh = (ViewHolder) convertView.getTag();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(parent.getContext().getResources(), Images.limage[position], options);
        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;

        if (imageHeight > length || imageWidth > length) {
            final int hh = imageHeight / 2;
            final int hw = imageWidth / 2;

            while (hh / inSampleSize > length && hw / inSampleSize > length) {
                inSampleSize *= 2;
            }
        }
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeResource(parent.getContext().getResources(), Images.limage[position], options);
//        vh.imageView.setImageResource(Images.limage[position]);
        vh.imageView.setImageBitmap(bm);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private static class ViewHolder {
        ImageView imageView;

        public ViewHolder(View view) {
            this.imageView = (ImageView) view;
        }
    }
}
