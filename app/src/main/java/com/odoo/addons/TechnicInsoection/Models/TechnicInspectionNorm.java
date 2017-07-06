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

import com.odoo.addons.technic.models.TechnicNorm;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.support.OUser;

public class TechnicInspectionNorm extends OModel {

    OColumn norm_id = new OColumn("norm_id", TechnicNorm.class, OColumn.RelationType.ManyToOne);
    OColumn inspection_type_id = new OColumn("Үзлэгийн төрөл", TechnicInspectionType.class, OColumn.RelationType.ManyToOne);
    OColumn inspection_pack_id = new OColumn("Үзлэгийн Багц", TechnicInspectionPack.class, OColumn.RelationType.ManyToOne);


    public TechnicInspectionNorm(Context context, OUser user) {
        super(context, "technic.inspection.norm", user);
    }

}
