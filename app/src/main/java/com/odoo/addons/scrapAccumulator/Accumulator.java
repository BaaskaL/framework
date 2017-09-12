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

import com.odoo.addons.scrapOil.models.ProductProduct;
import com.odoo.addons.scrapOil.models.ScrapOilReason;
import com.odoo.addons.scrapOil.models.ShTMScrapPhotos;
import com.odoo.addons.technic.models.TechnicsModel;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.annotation.Odoo;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

import java.util.ArrayList;
import java.util.List;

public class Accumulator extends OModel {

    OColumn product = new OColumn("Аккумлятор", ProductProduct.class, OColumn.RelationType.ManyToOne);
    OColumn name = new OColumn("Нэр", OVarchar.class);
    OColumn serial = new OColumn("Сериал дугаар", OVarchar.class);
    OColumn technic = new OColumn("Техник", TechnicsModel.class, OColumn.RelationType.ManyToOne);
    OColumn reason = new OColumn("Шалтгаан", ScrapAccumulatorReason.class, OColumn.RelationType.ManyToOne);
    //    OColumn capacity = new OColumn("Хэмжээ", OFloat.class);
    OColumn date = new OColumn("Бүртгэгдсэн огноо", ODateTime.class);
    OColumn state = new OColumn("Төлөв", OSelection.class)
            .addSelection("draft", "Ноорог")
            .addSelection("in_using", "Ашиглаж буй")
            .addSelection("in_reserve", "Нөөцөнд")
            .addSelection("in_scrap", "Акталсан")
            .setDefaultValue("draft");
    OColumn scrap_photos = new OColumn("Photos", ScrapAccumulatorPhotos.class, OColumn.RelationType.OneToMany).setRelatedColumn("accumulator_id");
    OColumn in_scrap = new OColumn("In scrap", OBoolean.class);

    @Odoo.Functional(store = true, depends = {"product"}, method = "storeProductName")
    OColumn product_name = new OColumn("Product name", OVarchar.class).setLocalColumn();
    @Odoo.Functional(store = true, depends = {"reason"}, method = "storeReasonName")
    OColumn reason_name = new OColumn("Reason name", OVarchar.class).setLocalColumn();

    public Accumulator(Context context, OUser user) {
        super(context, "technic.accumulator", user);
    }

    public String storeProductName(OValues value) {
        try {
            if (!value.getString("product").equals("false")) {
                List<Object> product_id = (ArrayList<Object>) value.get("product");
                return product_id.get(1) + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
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
