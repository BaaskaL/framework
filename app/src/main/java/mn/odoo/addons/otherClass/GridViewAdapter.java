package mn.odoo.addons.otherClass;

import android.app.Activity;
import android.content.Context;
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
import com.odoo.core.utils.BitmapUtils;

import java.util.ArrayList;

import mn.odoo.addons.scrapTire.ScrapTireDetails;

/**
 * Created by baaska on 8/7/17.
 */

public class GridViewAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private ArrayList<String> data = new ArrayList();

    public GridViewAdapter(Context context, int layoutResourceId, ArrayList<String> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    private void ShowPopupMenu(View view, int position) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.card_view_menu, popup.getMenu());
        popup.getMenu().clear();
        popup.getMenu().add("Зураг устгах");
        popup.setOnMenuItemClickListener(new ImageMenuItemClickListener(position));
        popup.show();
    }

    class ImageMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private int key;

        public ImageMenuItemClickListener(int positon) {
            this.key = positon;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            data.remove(key);
            notifyDataSetChanged();
            return true;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(layoutResourceId, parent, false);
        ImageView image = (ImageView) convertView.findViewById(R.id.image);
        convertView.setTag(image);
        Bitmap item = BitmapUtils.getBitmapImage(context, data.get(position));
        image.setImageBitmap(item);

        if (true) {
            image.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ShowPopupMenu(v, position);
                    return true;
                }
            });
        }
        return convertView;

    }

    public void updateContent(ArrayList<String> updates) {
        this.data = updates;
        this.notifyDataSetChanged();
    }
}
