package mn.odoo.addons.technic.services;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.odoo.addons.technic.models.ProjectProject;
import com.odoo.addons.technic.models.TechnicNorm;
import com.odoo.addons.technic.models.TechnicsModel;
import com.odoo.addons.technic.models.UsageUomLine;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OSQLite;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.service.OSyncAdapter;
import com.odoo.core.service.OSyncService;
import com.odoo.core.support.OUser;

import java.util.List;

/**
 * Created by baaska on 5/30/17.
 */

public class TechnicSyncService extends OSyncService {
    public static final String TAG = TechnicSyncService.class.getSimpleName();

    private OSQLite sqLite = null;

    @Override
    public OSyncAdapter getSyncAdapter(OSyncService service, Context context) {
        return new OSyncAdapter(context, TechnicsModel.class, service, true);
    }

    public SQLiteDatabase getReadableDatabase() {
        return sqLite.getReadableDatabase();
    }

    @Override
    public void performDataSync(OSyncAdapter adapter, Bundle extras, OUser user) {
        if (adapter.getModel().getModelName().equals("technic")) {
            TechnicsModel technic = new TechnicsModel(getApplicationContext(), user);
            TechnicNorm norm = new TechnicNorm(getApplicationContext(), user);
            UsageUomLine normLine = new UsageUomLine(getApplicationContext(), user);
            ProjectProject project = new ProjectProject(getApplicationContext(), user);
            ODomain projectDomain = new ODomain();
            projectDomain.add("id", "!=", 0);
            Log.i("project======", "start");
            project.quickSyncRecords(projectDomain);

//            if (technic.isEmptyTable()) {
            Log.i("project_synced_======", "norm start");
            norm.quickSyncRecords(projectDomain);
            Log.i("norm synced======", "line start");
            normLine.quickSyncRecords(projectDomain);
            Log.i("line synced======", "synced");
//            }
            ODomain domain = new ODomain();
            List<Integer> projectIds = project.selectManyToManyServerIds("members", user.getUserId());
            domain.add("project", "in", projectIds);
            Log.i("projectIds======", projectIds.toString());
            Log.i("DOMAIN==========", domain + "");
            adapter.syncDataLimit(80).setDomain(domain);
            adapter.syncDataLimit(80);
        }
    }
}
