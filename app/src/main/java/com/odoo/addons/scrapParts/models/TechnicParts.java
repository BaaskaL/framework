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
package com.odoo.addons.scrapParts.models;

import android.content.Context;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

public class TechnicParts extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn reason = new OColumn("Reason", PartsScrapReason.class, OColumn.RelationType.ManyToOne);
    OColumn part_cost = new OColumn("Cost", OFloat.class);
    OColumn state = new OColumn("State", OVarchar.class);

    //    OColumn product = new OColumn("Product", ProductProduct.class, OColumn.RelationType.ManyToOne).getRecordSyncLimit(10);
//    OColumn finance_code = new OColumn("Finance code", OFloat.class);
//    OColumn product_number = new OColumn("Product number", OVarchar.class);
//    OColumn product_number_replace = new OColumn("Replace number", OVarchar.class);
//    OColumn mongolian_name = new OColumn("Mongolian name", OVarchar.class);
//    OColumn foriegn_name = new OColumn("Foriegn name", OVarchar.class);
//    OColumn serial = new OColumn("Serial number", OVarchar.class);
//    OColumn technic_id = new OColumn("Техникийн нэр", TechnicsModel.class, OColumn.RelationType.ManyToOne);
//    OColumn date = new OColumn("Date", ODateTime.class);
//    OColumn project = new OColumn("Project", ProjectProject.class, OColumn.RelationType.ManyToOne);
//    OColumn usages = new OColumn("inspection_id", TechnicPartsUsage.class, OColumn.RelationType.OneToMany).setRelatedColumn("inspection_id");

    public TechnicParts(Context context, OUser user) {
        super(context, "technic.parts", user);
    }

//    public String getUsageUomName(OValues row) {
//        Log.i("ROW====", row.toString());
//        String name = "Хоосон";
//        try {
//            if (!row.getString("usage_uom_id").equals(null)) {
//                Log.i("usage_uom_id====", row.getString("usage_uom_id"));
//                String value = row.getString("usage_uom_id");
//                String[] parts = value.split(",");
//                name = parts[1].substring(1, parts[1].length() - 1);
//                Log.i("name====", name);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return name;
//    }

}
