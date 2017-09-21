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
package com.odoo.addons.scrapTire.models;

import android.content.Context;

import com.odoo.addons.technic.models.TechnicsModel;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.annotation.Odoo;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBlob;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OInteger;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

import java.util.ArrayList;
import java.util.List;

public class TechnicTire extends OModel {

    OColumn name = new OColumn("Дугуйны нэр", OVarchar.class);
    OColumn serial = new OColumn("Сериал", OVarchar.class);
    OColumn technic_id = new OColumn("Техникийн нэр", TechnicsModel.class, OColumn.RelationType.ManyToOne);
    OColumn date_record = new OColumn("Суурилуулсан огноо", ODateTime.class);
    OColumn usage_percent = new OColumn("Ашиглалтын хувь", OFloat.class);
    OColumn tread_cuurnet_deep = new OColumn("Хээний одоогийн гүн", OFloat.class);
    OColumn tread_depreciation_percent = new OColumn("Хээний элэгдлийн хувь", OFloat.class);
    OColumn current_position = new OColumn("Одоогийн байрлал", OInteger.class);
    OColumn reason = new OColumn("Шалтгаан", TiresScrapReason.class, OColumn.RelationType.ManyToOne);
    OColumn state = new OColumn("Төлөв", OSelection.class)
            .addSelection("draft", "Ноорог")
            .addSelection("using", "Хэрэглэж буй")
            .addSelection("inactive", "Нөөцөнд")
            .addSelection("rejected", "Акталсан")
            .setDefaultValue("draft");
    OColumn scrap_photos = new OColumn("Photos", TireScrapPhoto.class, OColumn.RelationType.OneToMany).setRelatedColumn("part_id");
    OColumn in_scrap = new OColumn("In scrap", OBoolean.class);

    @Odoo.Functional(store = true, depends = {"reason"}, method = "storeReasonName")
    OColumn reason_name = new OColumn("Reason name", OVarchar.class).setLocalColumn();

    public TechnicTire(Context context, OUser user) {
        super(context, "tire.register", user);
    }

    public String storeReasonName(OValues value) {
        try {
            if (!value.getString("reason").equals("false")) {
                List<Object> reason = (ArrayList<Object>) value.get("reason");
                return reason.get(1) + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
