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
import com.odoo.addons.scrapAccumulator.Accumulator;
import com.odoo.addons.scrapOil.models.TechnicOil;
import com.odoo.addons.scrapTire.models.TechnicTire;
import com.odoo.base.addons.res.ResCompany;
import com.odoo.core.orm.ODataRow;
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

import java.util.ArrayList;
import java.util.List;

public class TechnicsModel extends OModel {

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".core.provider.content.sync.technic";
    private ProjectProject projectObj;
    private static OUser userObj;

    OColumn technic_name = new OColumn("Техникийн нэр", OVarchar.class);
    OColumn register_number = new OColumn("Техникийн дугаар", OVarchar.class);
    OColumn last_km = new OColumn("km", OFloat.class);
    @Odoo.Functional(store = true, depends = {"technic_name"}, method = "storeTechnicName")
    OColumn name = new OColumn("name local", OVarchar.class).setLocalColumn();
    OColumn last_motohour = new OColumn("Мото/ц", OFloat.class);
    OColumn technic_norm_id = new OColumn("Техникийн норм, тохиргоо", TechnicNorm.class, OColumn.RelationType.ManyToOne);
    OColumn technic_brand_id = new OColumn("Техникийн брэнд", Brand.class, OColumn.RelationType.ManyToOne);
    OColumn technic_type_id = new OColumn("Техникийн төрөл", TechnicType.class, OColumn.RelationType.ManyToOne);
    OColumn technic_model_id = new OColumn("Техникийн загвар", TechnicModel.class, OColumn.RelationType.ManyToOne);
    OColumn specification_id = new OColumn("Техникийн зориулалт", TechnicSpecification.class, OColumn.RelationType.ManyToOne);
    OColumn serial_vin_number = new OColumn("Сериал/Арлын дугаар", OVarchar.class);
    OColumn state_number = new OColumn("Улсын дугаар", OVarchar.class);
    OColumn engine_number = new OColumn("Хөдөлгүүрийн дугаар", OVarchar.class);
    OColumn technic_color = new OColumn("Өнгө", GTechnicColor.class, OColumn.RelationType.ManyToOne);
    OColumn technic_number = new OColumn("Дугаарлалт", OVarchar.class);
    OColumn manufacture_date = new OColumn("Үйлдвэрлэсэн огноо", OVarchar.class);
    OColumn in_mongolia_date = new OColumn("МУ-д орж ирсэн он", OVarchar.class);
    OColumn registration_date = new OColumn("Бүртгэгдсэн огноо", ODateTime.class);

    OColumn project = new OColumn("Байршил", ProjectProject.class, OColumn.RelationType.ManyToOne);

    OColumn ownership_type = new OColumn("Эзэмшлийн төрөл", OSelection.class)
            .addSelection("own", "Өөрийн")
            .addSelection("leasing", "Лизнг")
            .addSelection("partner", "Харилцагч")
            .addSelection("rental", "Түрээсийн");

    OColumn account_asset_id = new OColumn("Хөрөнгийн нэр", AccountAssetAsset.class, OColumn.RelationType.ManyToOne);
    OColumn account_analytic_id = new OColumn("Шинжилгээний данс", AccountAnalyticAccount.class, OColumn.RelationType.ManyToOne);
    OColumn ownership_company_id = new OColumn("Эзэмшигч компани", ResCompany.class, OColumn.RelationType.ManyToOne);
    OColumn ownership_department_id = new OColumn("Эзэмшигч салбар", HrDepartment.class, OColumn.RelationType.ManyToOne);
    OColumn current_company_id = new OColumn("Ажиллаж буй компани", ResCompany.class, OColumn.RelationType.ManyToOne);
    OColumn current_department_id = new OColumn("Ажиллаж буй салбар", HrDepartment.class, OColumn.RelationType.ManyToOne);
    OColumn current_respondent_id = new OColumn("Эзэмшигч", Employee.class, OColumn.RelationType.ManyToOne);
    OColumn id_gps = new OColumn("Техникийн дугаар /GPS/", OInteger.class);
    OColumn tires = new OColumn("Дугуй", TechnicTire.class, OColumn.RelationType.OneToMany).setRelatedColumn("technic_id");
    OColumn oils = new OColumn("ШТМ", TechnicOil.class, OColumn.RelationType.OneToMany).setRelatedColumn("tech_id");
    OColumn accumulators = new OColumn("ШТМ", Accumulator.class, OColumn.RelationType.OneToMany).setRelatedColumn("technic");
    OColumn technic_document_ids = new OColumn("Document", TechnicDocument.class, OColumn.RelationType.OneToMany).setRelatedColumn("technic_id");
    OColumn technic_usage_history_ids = new OColumn("Usage history", TechnicUsageHistory.class, OColumn.RelationType.OneToMany).setRelatedColumn("technic_id");
    OColumn technic_state_history_ids = new OColumn("State history", TechnicStateHistory.class, OColumn.RelationType.OneToMany).setRelatedColumn("technic_id");

    OColumn state = new OColumn("Төлөв", OSelection.class)
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
        List<Integer> projectIds = new ArrayList<>();
        List<ODataRow> projectRows = projectObj.select();
        for (ODataRow row : projectRows) {
            List<ODataRow> memberRows = row.getM2MRecord("members").browseEach();
            Log.i("memberRows====", memberRows.toString());
            Log.i("getUserId====", userObj.getUserId().toString());
            Log.i("row_projId====", row.getString("id").toString());
            for (ODataRow memerRow : memberRows) {
                if (memerRow.getString("_id").equals(userObj.getUserId().toString())) {
                    projectIds.add(row.getInt("id"));
                }
            }
        }
        Log.i("projectIds====", projectIds.toString());
//        List<Integer> projectIds = projectObj.selectManyToManyServerIds("members", userObj.getUserId());
        domain.add("project", "in", projectIds);
        return domain;
    }

}
