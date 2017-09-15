package mn.odoo.addons.otherClass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.odoo.R;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import mn.odoo.addons.scrapAccumulator.wizards.AccumulatorDetailsWizard;
import mn.odoo.addons.scrapTire.ScrapTireDetails;

/**
 * Created by baaska on 8/7/17.
 */

public class GridViewAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private List<ODataRow> data = new ArrayList<>();
    public List<Integer> deleteIds = new ArrayList<>();

    public GridViewAdapter(Context context, int layoutResourceId, List<ODataRow> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(layoutResourceId, parent, false);
        ImageView image = (ImageView) convertView.findViewById(R.id.image);
        convertView.setTag(image);
        ODataRow row = data.get(position);
        final Bitmap item = BitmapUtils.getBitmapImage(context, row.getString("photo"));
        image.setImageBitmap(item);
        return convertView;
    }

    public boolean updateContent(ODataRow content) {
        for (ODataRow row : data) {
            if (row.getString("photo").equals(content.getString("photo"))) {
                return false;
            }
        }
        this.data.add(content);
        this.notifyDataSetChanged();
        return true;
    }

    public boolean deleteContent(int positoin) {
        ODataRow row = data.get(positoin);
        if (row.getInt("id") > 0)
            deleteIds.add(row.getInt("_id"));
        data.remove(positoin);
        this.notifyDataSetChanged();
        return true;
    }

}
