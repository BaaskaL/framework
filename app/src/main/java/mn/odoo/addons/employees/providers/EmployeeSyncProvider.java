package mn.odoo.addons.employees.providers;

import com.odoo.addons.employees.models.Employee;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by baaska on 7/20/17.
 */
public class EmployeeSyncProvider extends BaseModelProvider {
    public static final String TAG = EmployeeSyncProvider.class.getSimpleName();

    @Override
    public String authority() {
        return Employee.AUTHORITY;
    }
}
