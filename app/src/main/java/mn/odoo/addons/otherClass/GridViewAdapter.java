package mn.odoo.addons.otherClass;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.odoo.R;

import java.util.ArrayList;

/**
 * Created by baaska on 8/7/17.
 */

public class GridViewAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private ArrayList data = new ArrayList();

    public GridViewAdapter(Context context, int layoutResourceId, ArrayList data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(layoutResourceId, parent, false);
        ImageView image = (ImageView) convertView.findViewById(R.id.image);
        convertView.setTag(image);
        Bitmap item = (Bitmap) data.get(position);
        image.setImageBitmap(item);
        return convertView;
    }

    public void updateContent(ArrayList<Bitmap> updates) {
        this.data = updates;
        this.notifyDataSetChanged();
    }
}
