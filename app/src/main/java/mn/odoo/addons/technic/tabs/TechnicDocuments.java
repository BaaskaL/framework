package mn.odoo.addons.technic.tabs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.odoo.R;
import com.odoo.addons.technic.models.TechnicDocument;
import com.odoo.addons.technic.models.TechnicDocumentExtension;
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
    private TechnicDocument technicDocument;
    private TechnicDocumentExtension technicDocumentExtension;
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
        mList = (ExpandableListControl) mView.findViewById(R.id.ExpandListDocumentLine);
        technicsModel = new TechnicsModel(mContext, null);
        technicDocument = new TechnicDocument(mContext, null);
        technicDocumentExtension = new TechnicDocumentExtension(mContext, null);
        List<ODataRow> docRows = new ArrayList<>();
        docRows = technicsModel.browse(TechnicId).getO2MRecord("technic_document_ids").browseEach();
        Log.i("docRows=======", docRows.toString());
        rows.clear();
        for (ODataRow row : docRows) {
            List<ODataRow> exRows = new ArrayList<>();
            Log.i("select========", technicDocument.select().toString());
            Log.i("technicDocumentExten===", technicDocumentExtension.select().toString());
            Log.i("technicDocument========", technicDocument.browse(row.getInt("_id")).toString());

            exRows = technicDocument.browse(row.getInt("_id")).getO2MRecord("extension_ids").browseEach();
            row.put("register_date", "");
            row.put("expiry_date", "");
            row.put("alert_date", "");
            row.put("state", "");
            Log.i("exRows=======", exRows.toString());
            for (ODataRow exRow : exRows) {
                row.put("register_date", exRow.getString("register_date"));
                row.put("expiry_date", exRow.getString("expiry_date"));
                row.put("alert_date", exRow.getString("alert_date"));
                if (exRow.getString("alert_date").equals("using"))
                    row.put("state", "Ашиглагдаж буй");
                else if (exRow.getString("alert_date").equals("exceed"))
                    row.put("state", "Хэтэрсэн");
                else if (exRow.getString("alert_date").equals("finished"))
                    row.put("state", "Сунгасан");
            }
            rows.add(row);
            Log.i("kkkrows=====", rows.toString());
        }
        mObjects.addAll(rows);
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
                            TextView register_date = (TextView) mView.findViewById(R.id.register_date);
                            TextView expiry_date = (TextView) mView.findViewById(R.id.expiry_date);
                            TextView state = (TextView) mView.findViewById(R.id.state);
                            final ODataRow row = (ODataRow) mAdapter.getItem(position);
                            document_type_name.setText((position + 1) + ". " + row.getString("document_type_name"));
                            document_name.setText(row.getString("document_name"));
                            respondent_name.setText(row.getString("respondent_name"));
                            register_date.setText(row.getString("register_date"));
                            expiry_date.setText(row.getString("expiry_date"));
                            state.setText(row.getString("state"));
                            return mView;
                        }
                    });
        }
        mObjects.clear();
        mObjects.addAll(rows);
        mAdapter.notifyDataSetChanged(mObjects);
    }
}
