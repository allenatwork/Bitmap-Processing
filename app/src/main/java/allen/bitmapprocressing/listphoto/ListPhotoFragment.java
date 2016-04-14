package allen.bitmapprocressing.listphoto;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import allen.bitmapprocressing.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListPhotoFragment extends Fragment {

    GridView gridView;

    public ListPhotoFragment() {
        Log.d("ListPhtoScreen", "who that ?");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_photo, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gridView = (GridView) view.findViewById(R.id.grid);
        ListPhotoAdapter adapter = new ListPhotoAdapter();
        gridView.setAdapter(adapter);
    }
}
