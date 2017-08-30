package mn.odoo.addons.workOrder.providers;

import com.odoo.addons.workOrder.Models.WorkOrder;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by baaska on 8/29/17.
 */
public class WorkOrserSyncProvider extends BaseModelProvider {
    public static final String TAG = WorkOrserSyncProvider.class.getSimpleName();

    @Override
    public String authority() {
        return WorkOrder.AUTHORITY;
    }
}
