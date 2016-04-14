package allen.bitmapprocressing;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import allen.bitmapprocressing.listphoto.ListPhotoFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentById(R.id.frame_content) == null) {
            fragmentManager.beginTransaction().add(R.id.frame_content, new ListPhotoFragment(), "hello").commit();
        }
    }
}
