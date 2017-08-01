package mn.odoo.addons.scrapTechnic.providers;

import com.odoo.addons.scrapTechnic.models.ScrapTechnic;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by baaska on 7/30/17.
 */
public class ScrapTechnicSyncProvider extends BaseModelProvider {
    public static final String TAG = ScrapTechnicSyncProvider.class.getSimpleName();

    @Override
    public String authority() {
        return ScrapTechnic.AUTHORITY;
    }
}
