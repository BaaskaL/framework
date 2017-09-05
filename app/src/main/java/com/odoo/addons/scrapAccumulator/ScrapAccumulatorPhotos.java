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
package com.odoo.addons.scrapAccumulator;

import android.content.Context;

import com.odoo.addons.scrapOil.models.ScrapOils;
import com.odoo.addons.scrapOil.models.TechnicOil;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBlob;
import com.odoo.core.support.OUser;

public class ScrapAccumulatorPhotos extends OModel {

    OColumn scrap_id = new OColumn("Акт", ScrapAccumulator.class, OColumn.RelationType.ManyToOne);
    OColumn accumulator_id = new OColumn("ШТМ", Accumulator.class, OColumn.RelationType.ManyToOne);
    OColumn photo = new OColumn("Зураг", OBlob.class);

    public ScrapAccumulatorPhotos(Context context, OUser user) {
        super(context, "product.accumulator.photos", user);
    }
}
