package mn.odoo.addons.technic.tabs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.odoo.R;
import com.odoo.addons.technic.models.TechnicsModel;
import com.odoo.core.orm.ODataRow;

import odoo.controls.OForm;

/**
 * Created by baaska on 8/13/17.
 */

public class TechnicNorms extends Fragment {
    private TechnicsModel technicsModel;
    private OForm mForm;
    private Context mContext;
    private int TechnicId;
    private ODataRow record = null;

    public TechnicNorms(int TechnicId) {
        this.TechnicId = TechnicId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.technic_norm, container, false);
        mForm = (OForm) mView.findViewById(R.id.technicNormForm);
        technicsModel = new TechnicsModel(mContext, null);
        record = technicsModel.browse(TechnicId);
        mForm.initForm(record);
        return mView;
    }
}
