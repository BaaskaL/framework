package mn.odoo.addons.TechnicInspection.services;

import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.odoo.addons.TechnicInsoection.Models.TechnicInspectionItems;
import com.odoo.addons.TechnicInsoection.Models.TechnicInspectionNorm;
import com.odoo.addons.TechnicInsoection.Models.TechnicsInspectionModel;
import com.odoo.addons.technic.models.TechnicsModel;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.service.ISyncFinishListener;
import com.odoo.core.service.OSyncAdapter;
import com.odoo.core.service.OSyncService;
import com.odoo.core.support.OUser;

import java.util.List;

/**
 * Created by baaska on 5/30/17.
 */

public class TechnicInspectionSyncService extends OSyncService implements ISyncFinishListener {
    private Context mContext;
    public static final String TAG = TechnicInspectionSyncService.class.getSimpleName();

    @Override
    public OSyncAdapter getSyncAdapter(OSyncService service, Context context) {
        mContext = context;
        return new OSyncAdapter(context, TechnicsInspectionModel.class, service, true);
    }

    @Override
    public void performDataSync(OSyncAdapter adapter, Bundle extras, OUser user) {
        TechnicInspectionNorm norm = new TechnicInspectionNorm(getApplicationContext(), user);
        TechnicsModel technic = new TechnicsModel(getApplicationContext(), user);
        TechnicsInspectionModel model = new TechnicsInspectionModel(getApplicationContext(), user);
        ODomain normDomain = new ODomain();
        if (technic.isEmptyTable()) {

        } else {
            norm.quickSyncRecords(normDomain);
            adapter.syncDataLimit(80);
            adapter.onSyncFinish(this);
        }
    }

    @Override
    public OSyncAdapter performNextSync(OUser user, SyncResult syncResult) {
        TechnicInspectionItems isnpectionItem = new TechnicInspectionItems(mContext, user);
        isnpectionItem.quickSyncRecords(null);
//        List<ODataRow> rows = isnpectionItem.select();
//        for (ODataRow row : rows) {
//            if (row.getString("inspection_category_id").equals("false")) {
//                Log.i("created inspection item", row.toString());
//                isnpectionItem.quickCreateRecord(row);
//            }
//        }
        return null;
    }
}
