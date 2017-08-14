package com.odoo.addons.technic.models;

import android.content.Context;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OInteger;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

/**
 * Created by baaska on 8/11/17.
 */

public class TechnicDocumentType extends OModel {

    OColumn document_type_name = new OColumn("Name", OVarchar.class);
    OColumn expiry_date = new OColumn("Name", OInteger.class);

    public TechnicDocumentType(Context context, OUser user) {
        super(context, "document.type", user);
        setDefaultNameColumn("document_type_name");
    }
}
