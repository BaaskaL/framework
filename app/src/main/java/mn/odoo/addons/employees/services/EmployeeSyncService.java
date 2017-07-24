package mn.odoo.addons.employees.services;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.odoo.addons.employees.models.Employee;
import com.odoo.core.orm.OSQLite;
import com.odoo.core.service.OSyncAdapter;
import com.odoo.core.service.OSyncService;
import com.odoo.core.support.OUser;

/**
 * Created by baaska on 7/20/17.
 */

public class EmployeeSyncService extends OSyncService {
    public static final String TAG = EmployeeSyncService.class.getSimpleName();

    private OSQLite sqLite = null;

    @Override
    public OSyncAdapter getSyncAdapter(OSyncService service, Context context) {
        return new OSyncAdapter(context, Employee.class, service, true);
    }

    public SQLiteDatabase getReadableDatabase() {
        return sqLite.getReadableDatabase();
    }

    @Override
    public void performDataSync(OSyncAdapter adapter, Bundle extras, OUser user) {
        if (adapter.getModel().getModelName().equals("hr.employee")) {
            adapter.syncDataLimit(80);
        }
    }
}
