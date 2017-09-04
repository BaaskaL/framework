package com.odoo.addons.workOrder.Models;

import android.content.Context;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

/**
 * Created by baaska on 2017-08-29.
 */

public class WoStage extends OModel {

    OColumn name = new OColumn("Нэр", OVarchar.class);

    public WoStage(Context context, OUser user) {
        super(context, "work.order.stage", user);
    }
}
