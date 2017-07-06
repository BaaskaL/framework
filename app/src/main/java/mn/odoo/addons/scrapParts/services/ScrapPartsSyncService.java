package mn.odoo.addons.scrapParts.services;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.odoo.addons.scrapParts.models.ScrapParts;
import com.odoo.core.orm.OSQLite;
import com.odoo.core.service.OSyncAdapter;
import com.odoo.core.service.OSyncService;
import com.odoo.core.support.OUser;

/**
 * Created by baaska on 5/30/17.
 */

public class ScrapPartsSyncService extends OSyncService {
    public static final String TAG = ScrapPartsSyncService.class.getSimpleName();

    private OSQLite sqLite = null;

    @Override
    public OSyncAdapter getSyncAdapter(OSyncService service, Context context) {
        return new OSyncAdapter(context, ScrapParts.class, service, true);
    }

    public SQLiteDatabase getReadableDatabase() {
        return sqLite.getReadableDatabase();
    }

    @Override
    public void performDataSync(OSyncAdapter adapter, Bundle extras, OUser user) {
        if (adapter.getModel().getModelName().equals("technic.parts.scrap")) {
            adapter.syncDataLimit(80);
        }
    }
}
