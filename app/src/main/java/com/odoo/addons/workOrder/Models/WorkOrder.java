package com.odoo.addons.workOrder.Models;

import android.content.Context;
import android.util.Log;

import com.odoo.BuildConfig;
import com.odoo.addons.employees.models.Employee;
import com.odoo.addons.technic.models.ProjectProject;
import com.odoo.addons.technic.models.TechnicsModel;
import com.odoo.base.addons.res.ResUsers;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.annotation.Odoo;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.ODate;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baaska on 2017-08-29.
 */

public class WorkOrder extends OModel {

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".core.provider.content.sync.work_order";

    OColumn name = new OColumn("Нэр", OVarchar.class).setRequired();
    OColumn origin = new OColumn("Лавлах дугаар", OVarchar.class);
    OColumn planned_date = new OColumn("Төлөвлөсөн огноо", ODate.class).setRequired();
    OColumn create_date = new OColumn("Үүсгэсэн огноо", ODateTime.class).setRequired();
    OColumn technic_id = new OColumn("Техник", TechnicsModel.class, OColumn.RelationType.ManyToOne);
    OColumn project = new OColumn("Төсөл", ProjectProject.class, OColumn.RelationType.ManyToOne).setRequired();
    @Odoo.onChange(method = "technicNormOnChange")
    OColumn norm = new OColumn("Засварын ажилбарын норм", WoNorm.class, OColumn.RelationType.ManyToOne);
    OColumn assigned_to = new OColumn("Хариуцагч", ResUsers.class, OColumn.RelationType.ManyToOne);
    OColumn stage = new OColumn("Төлөв", WoStage.class, OColumn.RelationType.ManyToOne);
    OColumn repair_team = new OColumn("Засварын баг", Employee.class, OColumn.RelationType.ManyToMany);
    OColumn priority = new OColumn("Урьтамж", OSelection.class)
            .addSelection("1", "Хэвийн")
            .addSelection("2", "Яаралтай")
            .addSelection("3", "Маш яаралтай")
            .setDefaultValue("1");
    OColumn notes = new OColumn("Тодорхойлолт", OVarchar.class);
    OColumn planned_hours = new OColumn("Төлөвлөсөн цаг", OFloat.class).setRequired();

    @Odoo.Functional(store = true, depends = {"stage"}, method = "storeStageName")
    OColumn stage_name = new OColumn("Төлөв", OVarchar.class).setLocalColumn();

    @Odoo.Functional(store = true, depends = {"assigned_to"}, method = "storeAssignedName")
    OColumn assigned_to_name = new OColumn("Хариуцагч", OVarchar.class).setLocalColumn();

    public WorkOrder(Context context, OUser user) {
        super(context, "work.order", user);
    }

    public String storeStageName(OValues value) {
        try {
            if (!value.getString("stage").equals("false")) {
                List<Object> product_id = (ArrayList<Object>) value.get("stage");
                return product_id.get(1) + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String storeAssignedName(OValues value) {
        try {
            if (!value.getString("assigned_to").equals("false")) {
                List<Object> assigned = (ArrayList<Object>) value.get("assigned_to");
                return assigned.get(1) + "";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public ODataRow technicNormOnChange(ODataRow row) {
        ODataRow rec = new ODataRow();
        try {
            if (!row.getFloat("working_hours").equals("false")) {
                rec.put("planned_hours", row.getFloat("working_hours"));
            }
            if (!row.getString("name").equals("false")) {
                rec.put("name", row.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rec;
    }
}
