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
package com.odoo.addons.technic.models;

import android.content.Context;
import android.util.Log;

import com.odoo.BuildConfig;
import com.odoo.addons.employees.models.Employee;
import com.odoo.addons.scrapTire.models.TechnicTire;
import com.odoo.base.addons.res.ResCompany;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.annotation.Odoo;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OInteger;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.OUser;

import java.util.List;

public class TechnicsModel extends OModel {

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".core.provider.content.sync.technic";
    private ProjectProject projectObj;
    private static OUser userObj;

    OColumn technic_name = new OColumn("Name", OVarchar.class);
    OColumn register_number = new OColumn("Register number", OVarchar.class);
    OColumn last_km = new OColumn("km", OFloat.class);
    @Odoo.Functional(store = true, depends = {"technic_name"}, method = "storeTechnicName")
    OColumn name = new OColumn("name local", OVarchar.class).setLocalColumn();
    OColumn last_motohour = new OColumn("motohour", OFloat.class);
    OColumn technic_norm_id = new OColumn("Technic norm", TechnicNorm.class, OColumn.RelationType.ManyToOne);
    OColumn technic_brand_id = new OColumn("Technic brand", Brand.class, OColumn.RelationType.ManyToOne);
    OColumn technic_type_id = new OColumn("Technic brand", TechnicType.class, OColumn.RelationType.ManyToOne);
    OColumn technic_model_id = new OColumn("Technic model", TechnicModel.class, OColumn.RelationType.ManyToOne);
    OColumn specification_id = new OColumn("Technic specification", TechnicSpecification.class, OColumn.RelationType.ManyToOne);
    OColumn serial_vin_number = new OColumn("Serial/VIN number", OVarchar.class);
    OColumn state_number = new OColumn("State number", OVarchar.class);
    OColumn engine_number = new OColumn("Engine number", OVarchar.class);
    OColumn technic_color = new OColumn("Technic color", GTechnicColor.class, OColumn.RelationType.ManyToOne);
    OColumn technic_number = new OColumn("Technic number", OVarchar.class);
    OColumn manufacture_date = new OColumn("Manufacture date", OVarchar.class);
    OColumn in_mongolia_date = new OColumn("In Mongolia date", OVarchar.class);
    OColumn registration_date = new OColumn("Registration date", ODateTime.class);

    OColumn project = new OColumn("Project", ProjectProject.class, OColumn.RelationType.ManyToOne);

    OColumn ownership_type = new OColumn("Ownership type", OSelection.class)
            .addSelection("own", "Өөрийн")
            .addSelection("leasing", "Лизнг")
            .addSelection("partner", "Харилцагч")
            .addSelection("rental", "Түрээсийн");

    OColumn account_asset_id = new OColumn("Account asset", AccountAssetAsset.class, OColumn.RelationType.ManyToOne);
    OColumn account_analytic_id = new OColumn("Analytic account", AccountAnalyticAccount.class, OColumn.RelationType.ManyToOne);
    OColumn ownership_company_id = new OColumn("Ownership company", ResCompany.class, OColumn.RelationType.ManyToOne);
    OColumn ownership_department_id = new OColumn("Ownership department", HrDepartment.class, OColumn.RelationType.ManyToOne);
    OColumn current_company_id = new OColumn("Current company", ResCompany.class, OColumn.RelationType.ManyToOne);
    OColumn current_department_id = new OColumn("Current department", HrDepartment.class, OColumn.RelationType.ManyToOne);
    OColumn current_respondent_id = new OColumn("Current respondent", Employee.class, OColumn.RelationType.ManyToOne);
    OColumn id_gps = new OColumn("Gps", OInteger.class);
    OColumn tires = new OColumn("Tires", TechnicTire.class, OColumn.RelationType.OneToMany).setRelatedColumn("technic_id");

    OColumn state = new OColumn("status", OSelection.class)
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

    public TechnicsModel(Context context, OUser user) {
        super(context, "technic", user);
        projectObj = new ProjectProject(context, user);
        userObj = user;
    }

    public String storeTechnicName(OValues row) {
        String name = "Хоосон";
        try {
            if (!row.getString("technic_name").equals(null)) {
                name = row.getString("technic_name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    @Override
    public ODomain defaultDomain() {
        ODomain domain = new ODomain();
        List<Integer> projectIds = projectObj.selectManyToManyServerIds("members", userObj.getUserId());
        domain.add("project", "in", projectIds);
        Log.i("projectIds======", projectIds.toString());
        return domain;
    }

}
