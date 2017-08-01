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
package com.odoo.addons.scrapTechnic.models;

import android.content.Context;

import com.odoo.BuildConfig;
import com.odoo.addons.technic.models.TechnicsModel;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.annotation.Odoo;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

public class ScrapTechnic extends OModel {
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".core.provider.content.sync.technic_scrap";

    OColumn date = new OColumn("Огноо", ODateTime.class);
    OColumn technic = new OColumn("Техник", TechnicsModel.class, OColumn.RelationType.ManyToOne);
    OColumn description = new OColumn("Тайлбар", OVarchar.class);
    OColumn scrap_reason = new OColumn("Актын шалтгаан", TechnicScrapReason.class, OColumn.RelationType.ManyToOne);
    OColumn scrap_photos = new OColumn("Зураг", TechnicScrapPhoto.class, OColumn.RelationType.OneToMany).setRelatedColumn("scrap_id");
    OColumn state = new OColumn("Төлөв", OSelection.class)
            .addSelection("request", "Хүсэлт")
            .addSelection("waiting_approval", "Зөвшөөрөл хүлээж буй")
            .addSelection("approved", "Баталсан")
            .addSelection("refused", "Цуцлагдсан")
            .addSelection("returned", "Буцаагдсан")
            .addSelection("done", "Дууссан")
            .setDefaultValue("draft");

    @Odoo.Functional(store = true, depends = {"technic"}, method = "storeTechnicName")
    OColumn technic_name = new OColumn("Техник", OVarchar.class).setLocalColumn();

    public ScrapTechnic(Context context, OUser user) {
        super(context, "technic.scrap", user);
    }

    public String storeTechnicName(OValues row) {
        String name = "Хоосон";
        if (row.size() > 0) {
            try {
                if (!row.getString("technic").equals(false)) {
                    String value = row.getString("technic");
                    String[] parts = value.split(",");
                    name = parts[1].substring(1, parts[1].length() - 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return name;
    }

}
