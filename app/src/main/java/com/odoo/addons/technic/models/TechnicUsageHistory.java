package com.odoo.addons.technic.models;

import android.content.Context;

import com.odoo.addons.TechnicInsoection.Models.ProductUom;
import com.odoo.addons.TechnicInsoection.Models.UsageUom;
import com.odoo.base.addons.res.ResUsers;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.annotation.Odoo;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OText;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baaska on 8/14/17.
 */

public class TechnicUsageHistory extends OModel {

    OColumn technic_id = new OColumn("Техник", TechnicsModel.class, OColumn.RelationType.ManyToOne);
    OColumn change_measurement = new OColumn("Өөрчлөгдсөн хэмжигдэхүүн", UsageUom.class, OColumn.RelationType.ManyToOne);
    OColumn uom = new OColumn("Хэмжих нэгж", ProductUom.class, OColumn.RelationType.ManyToOne);
    OColumn change_value = new OColumn("Өөрчлөгдсөн утга", OFloat.class);
    OColumn change_date = new OColumn("Өөрчлөгдсөн огноо", ODateTime.class);
    OColumn change_user_id = new OColumn("Өөрчилсөн хэрэглэгч", ResUsers.class, OColumn.RelationType.ManyToOne);
    OColumn description = new OColumn("Тайлбар", OText.class);

    @Odoo.Functional(store = true, depends = {"change_measurement"}, method = "storeChangeMeasurementName")
    OColumn change_measurement_name = new OColumn("Change measurement name", OVarchar.class).setLocalColumn();

    @Odoo.Functional(store = true, depends = {"uom"}, method = "storeUomName")
    OColumn uom_name = new OColumn("Uom name", OVarchar.class).setLocalColumn();

    @Odoo.Functional(store = true, depends = {"change_user_id"}, method = "storeChangeUserName")
    OColumn change_user_name = new OColumn("User name", OVarchar.class).setLocalColumn();

    public TechnicUsageHistory(Context context, OUser user) {
        super(context, "technic.usage.history", user);
    }

    public String storeChangeMeasurementName(OValues value) {
        try {
            if (!value.getString("change_measurement").equals("false")) {
                List<Object> name = (ArrayList<Object>) value.get("change_measurement");
                return name.get(1) + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String storeUomName(OValues value) {
        try {
            if (!value.getString("uom").equals("false")) {
                List<Object> name = (ArrayList<Object>) value.get("uom");
                return name.get(1) + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String storeChangeUserName(OValues value) {
        try {
            if (!value.getString("change_user_id").equals("false")) {
                List<Object> name = (ArrayList<Object>) value.get("change_user_id");
                return name.get(1) + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
