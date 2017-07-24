package mn.odoo.addons.scrapTire.services;

import android.content.Context;
import android.content.SyncResult;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.odoo.addons.scrapTire.models.ScrapTires;
import com.odoo.addons.scrapTire.models.TechnicTire;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OSQLite;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.service.ISyncFinishListener;
import com.odoo.core.service.OSyncAdapter;
import com.odoo.core.service.OSyncService;
import com.odoo.core.support.OUser;

import java.util.List;

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
        mContext = getApplicationContext();
        if (adapter.getModel().getModelName().equals("tire.scrap")) {
            adapter.syncDataLimit(80);
            adapter.onSyncFinish(this);
        }
    }

    @Override
    public OSyncAdapter performNextSync(OUser user, SyncResult syncResult) {

        Log.i("FFFFTTTFFTT", "ssdsds");
        ScrapTires scrapTires = new ScrapTires(mContext, null);
        TechnicTire technicTire = new TechnicTire(mContext, null);
        ODomain domain = new ODomain();
        scrapTires.quickSyncRecords(domain);
        List<ODataRow> aaa = technicTire.select(new String[]{"technic_id", "in_scrap"});
        Log.i("tireeee=====", aaa.toString());
        technicTire.quickSyncRecords(domain);
        List<ODataRow> eee = technicTire.select(new String[]{"technic_id", "in_scrap"});
        Log.i("afte_tireeee=====", eee.toString());
        return null;
    }
}
