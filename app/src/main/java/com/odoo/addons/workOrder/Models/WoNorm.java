package com.odoo.addons.workOrder.Models;

import android.content.Context;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

/**
 * Created by baaska on 2017-08-29.
 */

public class WoNorm extends OModel {

    public WoNorm(Context context, OUser user) {
        super(context, "work.order.norm", user);
    }

    OColumn name = new OColumn("Нэр", OVarchar.class);
    OColumn working_hours = new OColumn("Нэр", OFloat.class);
}
