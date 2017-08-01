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
package com.odoo.addons.employees.models;

import android.content.Context;

import com.odoo.BuildConfig;
import com.odoo.base.addons.res.ResCompany;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.annotation.Odoo;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.ODate;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

import java.util.ArrayList;
import java.util.List;

public class Employee extends OModel {
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".core.provider.content.sync.hr_employee";

    OColumn last_name = new OColumn("Овог", OVarchar.class);
    OColumn name = new OColumn("Нэр", OVarchar.class).setRequired();
    OColumn confirm_code = new OColumn("Зөвшөөрөх код", OVarchar.class);
    OColumn mobile_phone = new OColumn("Ажлын утас", OVarchar.class);
    OColumn ssnid = new OColumn("Регистр", OVarchar.class).setRequired();
    OColumn birthday = new OColumn("Төрсөн огноо", ODate.class);
    OColumn company_id = new OColumn("Компани", ResCompany.class, OColumn.RelationType.ManyToOne).setRequired();
    OColumn job_id = new OColumn("Ажлын нэр", HrJob.class, OColumn.RelationType.ManyToOne).setRequired();
    OColumn department_id = new OColumn("Хэлтэс", HrDepartment.class, OColumn.RelationType.ManyToOne).setRequired();
    OColumn image_small = new OColumn("Image small", OVarchar.class);
    OColumn image_medium = new OColumn("Image medium", OVarchar.class);

    @Odoo.Functional(store = true, depends = {"company_id"}, method = "storeCompanyName")
    OColumn company_name = new OColumn("company name", OVarchar.class).setLocalColumn();
    @Odoo.Functional(store = true, depends = {"job_id"}, method = "storeJobName")
    OColumn job_name = new OColumn("job_name", OVarchar.class).setLocalColumn();
    @Odoo.Functional(store = true, depends = {"department_id"}, method = "storeDepartmentName")
    OColumn department_name = new OColumn("department_name", OVarchar.class).setLocalColumn();

    public Employee(Context context, OUser user) {
        super(context, "hr.employee", user);
    }

    public String storeCompanyName(OValues value) {
        try {
            if (!value.getString("company_id").equals("false")) {
                List<Object> parent_id = (ArrayList<Object>) value.get("company_id");
                return parent_id.get(1) + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String storeJobName(OValues value) {
        try {
            if (!value.getString("job_id").equals("false")) {
                List<Object> parent_id = (ArrayList<Object>) value.get("job_id");
                return parent_id.get(1) + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String storeDepartmentName(OValues value) {
        try {
            if (!value.getString("department_id").equals("false")) {
                List<Object> parent_id = (ArrayList<Object>) value.get("department_id");
                return parent_id.get(1) + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
