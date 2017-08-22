package com.odoo.addons.technic.models;

import android.content.Context;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OInteger;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

/**
 * Created by baaska on 2017-08-21.
 */

public class TechnicDocumentExtension extends OModel {

    OColumn technic_document_id = new OColumn("Technic document", TechnicDocument.class, OColumn.RelationType.ManyToOne);
    OColumn register_date = new OColumn("Бүртгэгдсэн огноо", ODateTime.class);
    OColumn expiry_date = new OColumn("Дуусах огноо", ODateTime.class);
    OColumn alert_date = new OColumn("Анхааруулах огноо", ODateTime.class);
    OColumn state = new OColumn("Төлөв", OSelection.class)
            .addSelection("using", "Ашиглагдаж буй")
            .addSelection("exceed", "Хэтэрсэн")
            .addSelection("finished", "Сунгасан");

    public TechnicDocumentExtension(Context context, OUser user) {
        super(context, "technic.document.extension", user);
    }
}
