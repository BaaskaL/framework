package mn.odoo.addons.technic.providers;

import com.odoo.addons.technic.models.TechnicsModel;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by baaska on 5/30/17.
 */
public class TechnicsSyncProvider extends BaseModelProvider{
    public static final String TAG = TechnicsSyncProvider.class.getSimpleName();

    @Override
    public String authority() {
        return TechnicsModel.AUTHORITY;
    }
}
