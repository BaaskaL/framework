/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 * <p>
 * Created on 31/12/14 6:43 PM
 */
package com.odoo.addons.TechnicInsoection.Models;

import android.content.Context;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

public class TechnicInspectionTires extends OModel {

    OColumn inspection_id = new OColumn("Inspection", TechnicsInspectionModel.class, OColumn.RelationType.ManyToOne);
    OColumn name = new OColumn("Нэр", OVarchar.class);
    OColumn date_record = new OColumn("Date record", ODateTime.class);
    OColumn serial = new OColumn("Serial", OVarchar.class);
    OColumn current_position = new OColumn("Current position", OVarchar.class);
    OColumn norm = new OColumn("Norm", OVarchar.class);
    OColumn usage_percent = new OColumn("Usage percent", OFloat.class);

    public TechnicInspectionTires(Context context, OUser user) {
        super(context, "gatsuurt.tire.inspection", user);
    }

}
