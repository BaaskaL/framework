package mn.odoo.addons.scrapTire.providers;

import com.odoo.addons.scrapTire.models.ScrapTires;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by baaska on 5/30/17.
 */
public class ScrapTireSyncProvider extends BaseModelProvider {
    public static final String TAG = ScrapTireSyncProvider.class.getSimpleName();

    @Override
    public String authority() {
        return ScrapTires.AUTHORITY;
    }
}
