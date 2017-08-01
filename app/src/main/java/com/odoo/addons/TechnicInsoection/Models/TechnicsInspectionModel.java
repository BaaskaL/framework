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
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.odoo.BuildConfig;
import com.odoo.addons.employees.models.Employee;
import com.odoo.addons.technic.models.TechnicsModel;
import com.odoo.base.addons.res.ResUsers;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.annotation.Odoo;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.rpc.helper.ORecordValues;
import com.odoo.core.support.OUser;

public class TechnicsInspectionModel extends OModel {
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".core.provider.content.sync.technic_inspection";

    private static int TechnicNormId;
    private static TechnicsModel technic;
    private static TechnicInspectionType insType;
    private static Employee employ;
    private static ResUsers resUser;
    private Context mContext;
    private int myId;

    OColumn origin = new OColumn("Үзлэгийн дугаар", OVarchar.class);
    OColumn inspection_date = new OColumn("Үзлэгийн огноо", ODateTime.class);
    @Odoo.onChange(method = "technicIdOnChange")
    OColumn inspection_technic_id = new OColumn("Техникийн нэр", TechnicsModel.class, OColumn.RelationType.ManyToOne).setRequired();
    OColumn inspection_type_id = new OColumn("Үзлэгийн төрөл", TechnicInspectionType.class, OColumn.RelationType.ManyToOne).setRequired();
    @Odoo.Functional(store = true, depends = {"inspection_type_id"}, method = "storeTypeName")
    OColumn inspection_type_name = new OColumn("Үзлэгийн төрөл", OVarchar.class).setLocalColumn();
    OColumn technic_inspection_check_list_ids = new OColumn("Check lists", TechnicInspectionCheckList.class, OColumn.RelationType.OneToMany).setRelatedColumn("inspection_id");
    OColumn inspection_usage_ids = new OColumn("inspection_id", TechnicInspectionUsage.class, OColumn.RelationType.OneToMany).setRelatedColumn("inspection_id");
    OColumn tire_ids = new OColumn("Дугуй", TechnicInspectionTires.class, OColumn.RelationType.OneToMany).setRelatedColumn("inspection_id");
    OColumn ins_photo = new OColumn("Зураг", TechnicInspectionPhoto.class, OColumn.RelationType.OneToMany).setRelatedColumn("inspection_id");
    OColumn inspection_respondent_id = new OColumn("Жолооч/Хариуцагч", Employee.class, OColumn.RelationType.ManyToOne);
    OColumn inspection_registrar_id = new OColumn("Бүртгэгч", ResUsers.class, OColumn.RelationType.ManyToOne);
    @Odoo.Functional(store = true, depends = {"inspection_respondent_id"}, method = "storeInsName")
    OColumn inspection_respondent_name = new OColumn("Жолооч", OVarchar.class).setLocalColumn();
    OColumn ins_description = new OColumn("Тайлбар", OVarchar.class);
    @Odoo.Functional(store = true, depends = {"inspection_technic_id"}, method = "storeTechnicName")
    OColumn technic_name = new OColumn("Technic name", OVarchar.class).setLocalColumn();
    OColumn state = new OColumn("Төлөв", OSelection.class)
            .addSelection("draft", "Ноорог")
            .addSelection("done", "Дууссан").setDefaultValue("draft");

    public TechnicsInspectionModel(Context context, OUser user) {
        super(context, "technic.inspection", user);
        mContext = context;

        technic = new TechnicsModel(context, user);
        insType = new TechnicInspectionType(context, user);
        employ = new Employee(context, user);
        resUser = new ResUsers(context, user);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }

    public ODataRow technicIdOnChange(ODataRow row) {
        ODataRow rec = new ODataRow();
        try {
            if (!row.getInt("current_respondent_id").equals("false")) {
                rec.put("inspection_respondent_id", row.getInt("current_respondent_id"));
                Log.i("TechnicNormId===111", "" + row.getInt("technic_norm_id"));
                TechnicNormId = row.getInt("technic_norm_id");
                Log.i("TechnicNormId===222", "" + getTechnicNorm() + "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rec;
    }

    public void setTechnicNorm(Integer technicId) {
        TechnicsModel technic = new TechnicsModel(mContext, getUser());
        for (ODataRow row : technic.select(new String[]{"technic_norm_id"}, "_id = ?", new String[]{technicId + ""})) {
            TechnicNormId = row.getInt("technic_norm_id");
        }
    }

    public Integer getTechnicNorm() {
        return TechnicNormId;
    }

    public String storeInsName(OValues values) {
        String name = "Хоосон";

        try {
            if (!values.getString("inspection_respondent_id").equals("false")) {
                String value = values.getString("inspection_respondent_id");
                String[] parts = value.split(",");
                name = parts[1].substring(1, parts[1].length() - 1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return name;
    }

    public Integer myId() {
        return myId = ResUsers.myId(mContext);
    }

    @Override
    public void onModelUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Execute upgrade script
    }

    public static ORecordValues valuesToData(OValues value) {
        ORecordValues data = new ORecordValues();
        data.put("origin", value.getString("origin"));
        data.put("inspection_date", value.getString("inspection_date"));
        data.put("inspection_technic_id", technic.selectServerId(value.getInt("inspection_technic_id")));
        data.put("inspection_type_id", insType.selectServerId(value.getInt("inspection_type_id")));
//        data.put("technic_inspection_check_list_ids", value.get("lines"));
        data.put("inspection_respondent_id", employ.selectServerId(value.getInt("inspection_respondent_id")));
        data.put("inspection_registrar_id", resUser.selectServerId(value.getInt("inspection_registrar_id")));
        data.put("ins_description", value.getString("ins_description"));
        data.put("state", value.getString("state"));
        return data;
    }

    public String storeTechnicName(OValues row) {
        String name = "Хоосон";
        try {
            if (!row.getString("inspection_technic_id").equals(null)) {
                String value = row.getString("inspection_technic_id");
                String[] parts = value.split(",");
                name = parts[1].substring(1, parts[1].length() - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    public String storeTypeName(OValues row) {
        String name = "Хоосон";
        try {
            if (!row.getString("inspection_type_id").equals(null)) {
                String value = row.getString("inspection_type_id");
                Log.i("value===", value.toString());
                String[] parts = value.split(",");
                name = parts[1].substring(1, parts[1].length() - 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }
}
