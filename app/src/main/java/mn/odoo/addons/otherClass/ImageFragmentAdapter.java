package mn.odoo.addons.otherClass;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.odoo.R;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.OControls;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David Pacioianu on 2/12/16.
 */

public class ImageFragmentAdapter extends FragmentPagerAdapter {
    private static List<ODataRow> CONTENT = new ArrayList<>();
    private int mCount = 0;
    private List<Integer> deleteIds = new ArrayList<>();

    public ImageFragmentAdapter(FragmentManager fm, List<ODataRow> picRow) {
        super(fm);
        CONTENT = picRow;
        if (picRow.isEmpty()) {
            ODataRow nullRow = new ODataRow();
            this.CONTENT.add(nullRow);
        }
        mCount = CONTENT.size();
    }

    @Override
    public Fragment getItem(int position) {
        return TestFragment.newInstance(CONTENT.get(position));
    }

    @Override
    public int getCount() {
        return mCount;
    }

    public void update(ODataRow row) {
        CONTENT.add(row);
        mCount = CONTENT.size();
        notifyDataSetChanged();
    }

    public ODataRow getRow(int position) {
        return CONTENT.get(position);
    }

    public List<Integer> getDeleteIds() {
        return deleteIds;
    }

    private boolean deleteContent(ODataRow row) {
        if (row.getInt("id") > 0)
            deleteIds.add(row.getInt("_id"));
//        CONTENT.remove(positoin);
        this.notifyDataSetChanged();
        return true;
    }

    public static class TestFragment extends Fragment {

        public static TestFragment newInstance(ODataRow content) {
            TestFragment fragment = new TestFragment();
            fragment.mContent = content;
            return fragment;
        }

        private ODataRow mContent = null;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View mView = inflater.inflate(R.layout.slide_image_view, container, false);
            ImageView imageView = (ImageView) mView.findViewById(R.id.imgView);
            TextView textView = (TextView) mView.findViewById(R.id.imgName);
            OControls.setImage(mView, R.id.imgView, R.drawable.bg_technic_scrap);
            if (mContent.size() > 0) {
                String newImage = mContent.getString("photo");
                Bitmap img = BitmapUtils.getBitmapImage(getContext(), newImage);
                imageView.setImageBitmap(img);
                String name = mContent.getString("create_date");
                if (!name.equals("false"))
                    textView.setText(name);
            }
//            imageView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    technicScrapShowPopupMenu(v, mContent.getString("photo"));
//                    return true;
//                }
//            });
            return mView;
        }

//        private void technicScrapShowPopupMenu(View view, String photo) {
//            PopupMenu popup = new PopupMenu(view.getContext(), view, Gravity.AXIS_PULL_BEFORE);
//            MenuInflater inflater = popup.getMenuInflater();
//            inflater.inflate(R.menu.card_view_menu, popup.getMenu());
//            popup.getMenu().clear();
//            popup.getMenu().add("Зураг устгах");
//            popup.setOnMenuItemClickListener(new technicImageMenuItemClickListener(photo));
//            popup.show();
//        }

    }

//    static class technicImageMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
//        private String photo;
//
//        public technicImageMenuItemClickListener(String photo) {
//            this.photo = photo;
//        }
//
//        @Override
//        public boolean onMenuItemClick(MenuItem menuItem) {
//            for (ODataRow row : CONTENT) {
//                if (row.getString("photo") == photo) {
//                    ImageFragmentAdapter aa=new ImageFragmentAdapter();
//                    deleteContent(row);
//                }
//            }
////            oilImages.remove(key);
//            return true;
//        }
//    }
}
