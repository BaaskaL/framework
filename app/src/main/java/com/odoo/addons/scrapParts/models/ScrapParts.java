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
import android.util.Log;

import com.odoo.BuildConfig;
import com.odoo.addons.technic.models.TechnicsModel;
import com.odoo.base.addons.res.ResUsers;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.annotation.Odoo;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

public class ScrapParts extends OModel {
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".core.provider.content.sync.scrap_parts";

    OColumn origin = new OColumn("Актын дугаар", OVarchar.class);
    OColumn date = new OColumn("Хүсэлтийн огноо", ODateTime.class);
    OColumn created_user = new OColumn("Үүсгэсэн хэрэглэгч", ResUsers.class, OColumn.RelationType.ManyToOne);
    OColumn technic_id = new OColumn("Техникийн нэр", TechnicsModel.class, OColumn.RelationType.ManyToOne);
    OColumn parts = new OColumn("Сэлбэг", TechnicParts.class, OColumn.RelationType.ManyToMany);
    OColumn is_payable = new OColumn("Төлбөртэй эсэх", OBoolean.class);
    OColumn description = new OColumn("Тайлбар", OVarchar.class);

    @Odoo.Functional(store = true, depends = {"technic_id"}, method = "storeTechnicName")
    OColumn technic_name = new OColumn("Technic name", OVarchar.class).setLocalColumn();
//    OColumn history_lines = new OColumn("Цохолт", TechnicInspectionUsage.class, OColumn.RelationType.OneToMany).setRelatedColumn("inspection_id");

    public ScrapParts(Context context, OUser user) {
        super(context, "technic.parts.scrap", user);
    }

    public String storeTechnicName(OValues row) {
        Log.i("ROW====", row.toString());
        String name = "Хоосон";
        if (row.size() > 0) {
            try {
                if (!row.getString("technic_id").equals(null)) {
                    Log.i("usage_uom_id====", row.getString("technic_id"));
                    String value = row.getString("technic_id");
                    String[] parts = value.split(",");
                    name = parts[1].substring(1, parts[1].length() - 1);
                    Log.i("name====", name);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return name;
    }

}
