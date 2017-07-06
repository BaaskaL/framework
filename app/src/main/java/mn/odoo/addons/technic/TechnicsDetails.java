package mn.odoo.addons.technic;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.odoo.R;
import com.odoo.addons.technic.models.TechnicNorm;
import com.odoo.addons.technic.models.TechnicsModel;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.support.OdooCompatActivity;

import odoo.controls.OField;
import odoo.controls.OForm;

/**
 * Created by baaska on 5/30/17.
 */

public class TechnicsDetails extends OdooCompatActivity
        implements View.OnClickListener, OField.IOnFieldValueChangeListener {

    public static final String TAG = TechnicsDetails.class.getSimpleName();

    private Bundle extras;
    private OForm mForm;
    private TechnicsModel technic;
    private ODataRow record = null;
    private Toolbar toolbar;
    private TechnicNorm technic_norm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.technic_detail);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Technic detail");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        extras = getIntent().getExtras();
        technic = new TechnicsModel(this, null);
        technic_norm = new TechnicNorm(this, null);
        mForm = (OForm) findViewById(R.id.technicForm);
        int rowId = extras.getInt(OColumn.ROW_ID);
        record = technic.browse(rowId);
        mForm.initForm(record);

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onFieldValueChange(OField field, Object value) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
