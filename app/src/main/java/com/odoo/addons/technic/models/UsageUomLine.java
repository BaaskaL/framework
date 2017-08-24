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
package com.odoo.addons.technic.models;

import android.content.Context;

import com.odoo.addons.TechnicInsoection.Models.ProductUom;
import com.odoo.addons.TechnicInsoection.Models.UsageUom;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.annotation.Odoo;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

import java.util.ArrayList;
import java.util.List;

public class UsageUomLine extends OModel {

    OColumn technic_norm_id = new OColumn("Техникийн нэр", TechnicNorm.class, OColumn.RelationType.ManyToOne);
    OColumn usage_uom_id = new OColumn("Техникийн нэр", UsageUom.class, OColumn.RelationType.ManyToOne);
    OColumn product_uom_id = new OColumn("Техникийн нэр", ProductUom.class, OColumn.RelationType.ManyToOne);

    @Odoo.Functional(store = true, depends = {"usage_uom_id"}, method = "storeUomName")
    OColumn usage_uom_name = new OColumn("Name", OVarchar.class).setLocalColumn();

    @Odoo.Functional(store = true, depends = {"product_uom_id"}, method = "storeProductName")
    OColumn product_uom_name = new OColumn("Name", OVarchar.class).setLocalColumn();

    public UsageUomLine(Context context, OUser user) {
        super(context, "usage.uom.line", user);
    }

    public String storeUomName(OValues value) {
        try {
            if (!value.getString("usage_uom_id").equals("false")) {
                List<Object> name = (ArrayList<Object>) value.get("usage_uom_id");
                return name.get(1) + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String storeProductName(OValues value) {
        try {
            if (!value.getString("product_uom_id").equals("false")) {
                List<Object> name = (ArrayList<Object>) value.get("product_uom_id");
                return name.get(1) + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
