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
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

public class TechnicInspectionCheckList extends OModel {

    OColumn inspection_id = new OColumn("Inspection", TechnicsInspectionModel.class, OColumn.RelationType.ManyToOne);
    OColumn technic_inspection_category_id = new OColumn("Category", TechnicInspectionCategory.class, OColumn.RelationType.ManyToOne);
    OColumn technic_inspection_item_id = new OColumn("Inspection item", TechnicInspectionItems.class, OColumn.RelationType.ManyToOne);
    OColumn inspection_isitnormal = new OColumn("Inspection normal", OBoolean.class);
    OColumn inspection_check = new OColumn("Check", OBoolean.class);
    OColumn description = new OColumn("Description", OVarchar.class);

    @Odoo.Functional(store = true, depends = {"technic_inspection_item_id"}, method = "getItemName")
    OColumn item_name = new OColumn("Item name", OVarchar.class).setLocalColumn();

    @Odoo.Functional(store = true, depends = {"technic_inspection_category_id"}, method = "getCategName")
    OColumn categ_name = new OColumn("Category name", OVarchar.class).setLocalColumn();

    public TechnicInspectionCheckList(Context context, OUser user) {
        super(context, "technic.inspection.check.list", user);
    }

    public String getCategName(OValues row) {
        String name = "Хоосон";
        Log.i("Technic_name ====", row.toString());
        try {
            if (!row.getString("technic_inspection_category_id").equals("false")) {
                String value = row.getString("technic_inspection_category_id");
                Log.i("tehcnic_name222===", value);
                String[] parts = value.split(",");
                name = parts[1].substring(1, parts[1].length() - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    public String getItemName(OValues row) {
        String name = "Хоосон";
        try {
            if (!row.getString("technic_inspection_item_id").equals(null)) {
                String value = row.getString("technic_inspection_item_id");
                String[] parts = value.split(",");
                name = parts[1].substring(1, parts[1].length() - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

}
