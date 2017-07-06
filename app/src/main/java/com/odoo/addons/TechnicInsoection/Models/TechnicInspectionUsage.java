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
import android.util.Log;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.annotation.Odoo;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

public class TechnicInspectionUsage extends OModel {

    OColumn inspection_id = new OColumn("Inspection", TechnicsInspectionModel.class, OColumn.RelationType.ManyToOne);
    OColumn usage_uom_id = new OColumn("Usage measurement", UsageUom.class, OColumn.RelationType.ManyToOne);
    OColumn product_uom_id = new OColumn("Unit of measure", ProductUom.class, OColumn.RelationType.ManyToOne);
    OColumn usage_value = new OColumn("Usage value", OFloat.class);

    @Odoo.Functional(store = true, depends = {"usage_uom_id"}, method = "getUsageUomName")
    OColumn usage_uom_name = new OColumn("Usage uom name", OVarchar.class).setLocalColumn();

    @Odoo.Functional(store = true, depends = {"product_uom_id"}, method = "getProductUomName")
    OColumn product_uom_name = new OColumn("Product uom name", OVarchar.class).setLocalColumn();


    public TechnicInspectionUsage(Context context, OUser user) {
        super(context, "technic.inspection.usage", user);
    }

    public String getUsageUomName(OValues row) {
        Log.i("ROW====", row.toString());
        String name = "Хоосон";
        try {
            if (!row.getString("usage_uom_id").equals(null)) {
                Log.i("usage_uom_id====", row.getString("usage_uom_id"));
                String value = row.getString("usage_uom_id");
                String[] parts = value.split(",");
                name = parts[1].substring(1, parts[1].length() - 1);
                Log.i("name====", name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    public String getProductUomName(OValues row) {
        String name = "Хоосон";
        Log.i("product uom name=", row.getString("product_uom_id"));
        try {
            if (!row.getString("product_uom_id").equals(null)) {
                String value = row.getString("product_uom_id");
                String[] parts = value.split(",");
                name = parts[1].substring(1, parts[1].length() - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

}
