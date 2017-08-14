package com.odoo.addons.technic.models;

import android.content.Context;

import com.odoo.base.addons.res.ResUsers;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.annotation.Odoo;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OText;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baaska on 8/14/17.
 */

public class TechnicStateHistory extends OModel {

    OColumn technic_id = new OColumn("Техник", TechnicsModel.class, OColumn.RelationType.ManyToOne);
    OColumn state_name = new OColumn("status", OSelection.class)
            .addSelection("draft", "Ноорог")
            .addSelection("ready", "Бэлэн")
            .addSelection("waiting_to_approve", "Ажил хүлээж буй")
            .addSelection("in_road", "Замд яваа")
            .addSelection("in_store", "Хадгалалтанд")
            .addSelection("to_check", "Оношлох")
            .addSelection("in_repair", "Засварт")
            .addSelection("in_rent", "Түрээсэнд")
            .addSelection("to_sell", "Худалдсан")
            .addSelection("scrap", "Актлагдсан");
    OColumn change_date = new OColumn("Өөрчлөгдсөн огноо", ODateTime.class);
    OColumn change_user_id = new OColumn("Өөрчилсөн хэрэглэгч", ResUsers.class, OColumn.RelationType.ManyToOne);
    OColumn description = new OColumn("Тайлбар", OText.class);

    @Odoo.Functional(store = true, depends = {"change_user_id"}, method = "storeChangeUserName")
    OColumn change_user_name = new OColumn("User name", OVarchar.class).setLocalColumn();

    public TechnicStateHistory(Context context, OUser user) {
        super(context, "technic.state.history", user);
    }

    public String storeChangeUserName(OValues value) {
        try {
            if (!value.getString("change_user_id").equals("false")) {
                List<Object> parent_id = (ArrayList<Object>) value.get("change_user_id");
                return parent_id.get(1) + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
