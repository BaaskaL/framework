package mn.odoo.addons.technic.tabs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.odoo.R;
import com.odoo.addons.technic.models.TechnicsModel;
import com.odoo.core.orm.ODataRow;

import java.util.ArrayList;
import java.util.List;

import odoo.controls.ExpandableListControl;
import odoo.controls.OForm;

/**
 * Created by baaska on 8/13/17.
 */

public class TechnicDocuments extends Fragment {

    private TechnicsModel technicsModel;
    private OForm mForm;
    private Context mContext;
    private int TechnicId;
    private ExpandableListControl mList;
    private ExpandableListControl.ExpandableListAdapter mAdapter;
    private List<Object> mObjects = new ArrayList<>();
    private List<ODataRow> rows = new ArrayList<>();

    public TechnicDocuments(int TechnicId) {
        this.TechnicId = TechnicId;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.technic_document, container, false);
        mForm = (OForm) mView.findViewById(R.id.technicDocumentForm);
        mList = (ExpandableListControl) mView.findViewById(R.id.ExpandListDocumentLine);
        technicsModel = new TechnicsModel(mContext, null);
        rows = technicsModel.browse(TechnicId).getO2MRecord("technic_document_ids").browseEach();
        getStateHistory();
        return mView;
    }

    private void getStateHistory() {
        if (rows != null) {
            final int template = R.layout.technic_document_line;
            mAdapter = mList.getAdapter(template, mObjects,
                    new ExpandableListControl.ExpandableListAdapterGetViewListener() {
                        @Override
                        public View getView(final int position, View mView, ViewGroup parent) {
                            TextView document_type_name = (TextView) mView.findViewById(R.id.document_type_name);
                            TextView document_name = (TextView) mView.findViewById(R.id.document_name);
                            TextView respondent_name = (TextView) mView.findViewById(R.id.respondent_name);
                            CheckBox is_tax = (CheckBox) mView.findViewById(R.id.is_tax);
                            TextView state = (TextView) mView.findViewById(R.id.state);
                            final ODataRow row = (ODataRow) mAdapter.getItem(position);
                            document_type_name.setText((position + 1) + ". " + row.getString("document_type_name"));
                            document_name.setText(row.getString("document_name"));
                            is_tax.setChecked(row.getBoolean("is_tax"));
                            respondent_name.setText(row.getString("respondent_name"));
                            if (row.getString("state").equals("draft"))
                                state.setText("Ноорог");
                            if (row.getString("state").equals("using"))
                                state.setText("Ашиглагдаж буй");
                            if (row.getString("state").equals("aborted"))
                                state.setText("Ашиглагдаж буй");
                            return mView;
                        }
                    });
        }
        mObjects.clear();
        mObjects.addAll(rows);
        mAdapter.notifyDataSetChanged(mObjects);
    }
}
