package mn.odoo.addons.scrapTire.services;

import android.content.Context;
import android.content.SyncResult;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.odoo.addons.scrapTire.models.ScrapTires;
import com.odoo.core.orm.OSQLite;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.service.ISyncFinishListener;
import com.odoo.core.service.OSyncAdapter;
import com.odoo.core.service.OSyncService;
import com.odoo.core.support.OUser;

/**
 * Created by baaska on 5/30/17.
 */

public class ScrapTireSyncService extends OSyncService implements ISyncFinishListener {
    public static final String TAG = ScrapTireSyncService.class.getSimpleName();
    private Context mContext;
    private OSQLite sqLite = null;

    @Override
    public OSyncAdapter getSyncAdapter(OSyncService service, Context context) {
        return new OSyncAdapter(context, ScrapTires.class, service, true);
    }

    public SQLiteDatabase getReadableDatabase() {
        return sqLite.getReadableDatabase();
    }

    @Override
    public void performDataSync(OSyncAdapter adapter, Bundle extras, OUser user) {
        if (adapter.getModel().getModelName().equals("tire.scrap")) {
            adapter.syncDataLimit(80);
        }
    }

    @Override
    public OSyncAdapter performNextSync(OUser user, SyncResult syncResult) {
        ScrapTires scrapTires = new ScrapTires(mContext, null);
        ODomain aa = new ODomain();
        scrapTires.quickSyncRecords(aa);
        return null;
    }
}
