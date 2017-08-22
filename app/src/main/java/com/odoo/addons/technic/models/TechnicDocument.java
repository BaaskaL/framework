package com.odoo.addons.technic.models;

import android.content.Context;

import com.odoo.addons.employees.models.Employee;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.annotation.Odoo;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baaska on 8/11/17.
 */

public class TechnicDocument extends OModel {

    OColumn technic_id = new OColumn("Техник", TechnicsModel.class, OColumn.RelationType.ManyToOne);
    OColumn document_type_id = new OColumn("Бичиг баримтын төрөл", TechnicDocumentType.class, OColumn.RelationType.ManyToOne);
    @Odoo.SyncColumnName("extension_ids")
    OColumn extension_ids = new OColumn("Бичиг баримт", TechnicDocumentExtension.class, OColumn.RelationType.OneToMany).setRelatedColumn("technic_document_id");
    OColumn document_name = new OColumn("Бичиг баримтын дугаар", OVarchar.class);
    OColumn respondent_id = new OColumn("Бичиг баримт хариуцагч", Employee.class, OColumn.RelationType.ManyToOne);
//    OColumn state = new OColumn("Төлөв", OSelection.class)
//            .addSelection("draft", "Ноорог")
//            .addSelection("using", "Ашиглагдаж буй")
//            .addSelection("aborted", "Ашиглагдаж буй  ");

    @Odoo.Functional(store = true, depends = {"document_type_id"}, method = "storeDocumentTypeName")
    OColumn document_type_name = new OColumn("Бичиг баримтын төрөл", OVarchar.class).setLocalColumn();

    @Odoo.Functional(store = true, depends = {"respondent_id"}, method = "storeRespondentName")
    OColumn respondent_name = new OColumn("Бичиг баримтын төрөл", OVarchar.class).setLocalColumn();

    public TechnicDocument(Context context, OUser user) {
        super(context, "technic.document", user);
    }

    public String storeDocumentTypeName(OValues value) {
        try {
            if (!value.getString("document_type_id").equals("false")) {
                List<Object> name = (ArrayList<Object>) value.get("document_type_id");
                return name.get(1) + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String storeRespondentName(OValues value) {
        try {
            if (!value.getString("respondent_id").equals("false")) {
                List<Object> name = (ArrayList<Object>) value.get("respondent_id");
                return name.get(1) + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
