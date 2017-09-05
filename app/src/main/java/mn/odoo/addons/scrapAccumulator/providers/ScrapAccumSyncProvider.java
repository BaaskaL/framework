package mn.odoo.addons.scrapAccumulator.providers;

import com.odoo.addons.scrapOil.models.ScrapOils;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by baaska on 5/30/17.
 */
public class ScrapAccumSyncProvider extends BaseModelProvider {
    public static final String TAG = ScrapAccumSyncProvider.class.getSimpleName();

    @Override
    public String authority() {
        return ScrapOils.AUTHORITY;
    }
}
