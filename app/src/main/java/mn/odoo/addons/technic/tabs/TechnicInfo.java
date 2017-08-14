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

public class TechnicInfo extends Fragment {
    private TechnicsModel technic;
    private OForm mForm;
    private ODataRow record = null;
    private Context mContext;
    private int TechnicId;

    public TechnicInfo(int TechnicId) {
        this.TechnicId = TechnicId;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.technic_info, container, false);
        mForm = (OForm) mView.findViewById(R.id.technicForm);
        technic = new TechnicsModel(mContext, null);
        record = technic.browse(TechnicId);
        mForm.initForm(record);
        return mView;
    }
}