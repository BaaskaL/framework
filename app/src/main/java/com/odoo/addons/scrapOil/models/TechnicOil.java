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
package com.odoo.addons.scrapOil.models;

import android.content.Context;

import com.odoo.addons.technic.models.TechnicsModel;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

public class TechnicOil extends OModel {

    OColumn name = new OColumn("Дугуйны нэр", OVarchar.class);
    OColumn serial = new OColumn("Сериал", OVarchar.class);
    OColumn technic_id = new OColumn("Техникийн нэр", TechnicsModel.class, OColumn.RelationType.ManyToOne);
    OColumn product_id = new OColumn("Бараа", ProductProduct.class, OColumn.RelationType.ManyToOne);
    OColumn capacity = new OColumn("Хэмжээ", OFloat.class);
    OColumn date_record = new OColumn("Суурилуулсан огноо", ODateTime.class);
    OColumn reason = new OColumn("Reason", TiresOilReason.class, OColumn.RelationType.ManyToOne);
    OColumn state = new OColumn("State", OVarchar.class);

    public TechnicOil(Context context, OUser user) {
        super(context, "shtm.register", user);
    }
}
