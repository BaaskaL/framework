package mn.odoo.addons.workOrder.services;

import android.content.Context;
import android.content.SyncResult;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.odoo.addons.technic.models.ProjectProject;
import com.odoo.addons.workOrder.Models.WorkOrder;
import com.odoo.addons.workOrder.Models.WoNorm;
import com.odoo.addons.workOrder.Models.WoStage;
import com.odoo.core.orm.OSQLite;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.service.ISyncFinishListener;
import com.odoo.core.service.OSyncAdapter;
import com.odoo.core.service.OSyncService;
import com.odoo.core.support.OUser;

import java.util.List;

/**
 * Created by baaska on 8/29/17.
 */

public class WorkOrderSyncService extends OSyncService implements ISyncFinishListener {
    public static final String TAG = WorkOrderSyncService.class.getSimpleName();
    private Context mContext;
    private OSQLite sqLite = null;

    @Override
    public OSyncAdapter getSyncAdapter(OSyncService service, Context context) {
        return new OSyncAdapter(context, WorkOrder.class, service, true);
    }

    public SQLiteDatabase getReadableDatabase() {
        return sqLite.getReadableDatabase();
    }

    @Override
    public void performDataSync(OSyncAdapter adapter, Bundle extras, OUser user) {
        mContext = getApplicationContext();
        if (adapter.getModel().getModelName().equals("work.order")) {
            ProjectProject project = new ProjectProject(mContext, user);
            List<Integer> projectIds = project.selectManyToManyServerIds("members", user.getUserId());
            ODomain domain = new ODomain();
            domain.add("project", "in", projectIds);
            Log.i("projectIds======", projectIds.toString());
            Log.i("DOMAIN==========", domain + "");
            adapter.syncDataLimit(80).setDomain(domain);
            adapter.syncDataLimit(80);
        }
    }

    @Override
    public OSyncAdapter performNextSync(OUser user, SyncResult syncResult) {
        WoNorm norm = new WoNorm(mContext, null);
        WoStage stage = new WoStage(mContext, null);
        ODomain domain = new ODomain();
        norm.quickSyncRecords(domain);
        stage.quickSyncRecords(domain);
        return null;
    }
}
