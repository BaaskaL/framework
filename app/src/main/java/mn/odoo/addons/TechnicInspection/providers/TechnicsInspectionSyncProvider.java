package mn.odoo.addons.TechnicInspection.providers;

import com.odoo.addons.TechnicInsoection.Models.TechnicsInspectionModel;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by baaska on 5/30/17.
 */
public class TechnicsInspectionSyncProvider extends BaseModelProvider {
    public static final String TAG = TechnicsInspectionSyncProvider.class.getSimpleName();

    @Override
    public String authority() {
        return TechnicsInspectionModel.AUTHORITY;
    }
}
