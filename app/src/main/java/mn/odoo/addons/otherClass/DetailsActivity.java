package mn.odoo.addons.otherClass;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;

import com.odoo.R;

/**
 * Created by baaska on 8/7/17.
 */

public class DetailsActivity extends ActionBarActivity {
    public static Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);

        ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setImageBitmap(image);
    }
}
