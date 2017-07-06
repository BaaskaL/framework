package mn.odoo.addons.scrapParts.providers;

import com.odoo.addons.scrapParts.models.ScrapParts;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by baaska on 5/30/17.
 */
public class ScrapPartsSyncProvider extends BaseModelProvider {
    public static final String TAG = ScrapPartsSyncProvider.class.getSimpleName();

    @Override
    public String authority() {
        return ScrapParts.AUTHORITY;
    }
}
