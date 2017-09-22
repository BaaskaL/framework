package mn.odoo.addons.TechnicInspection;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.odoo.R;
import com.odoo.core.orm.ODataRow;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by baaska on 2017-08-23.
 */

public class AdapterTire extends RecyclerView.Adapter<AdapterTire.ViewHolderTire> {
    private List<ODataRow> tireRows = new ArrayList<>();
    private static final int CAMERA_REQUEST = 1888;

    public static class ViewHolderTire extends RecyclerView.ViewHolder {
        public TextView name, date_record, current_position, state;
        public EditText serial;
        public FloatingActionButton captureImageTire;

        public ViewHolderTire(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            date_record = (TextView) view.findViewById(R.id.date_record);
            current_position = (TextView) view.findViewById(R.id.current_position);
            state = (TextView) view.findViewById(R.id.state);
            serial = (EditText) view.findViewById(R.id.serial);
            captureImageTire = (FloatingActionButton) view.findViewById(R.id.captureImageTire);
            serial.setEnabled(TechnicsInspectionDetails.mEditMode);
        }
    }


    public AdapterTire(List<ODataRow> tireRows) {
        this.tireRows = tireRows;
    }

    @Override
    public ViewHolderTire onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.technic_inspection_tire_item, null);
        return new ViewHolderTire(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolderTire holder, final int position) {
        final ODataRow row = tireRows.get(position);
        holder.name.setText(((position + 1) + ". " + row.getString("name")));
        holder.date_record.setText(row.getString("date_record").toString());
        holder.current_position.setText(row.getString("current_position").toString());
        holder.serial.setText("");
        if (!row.getString("serial").equals("false"))
            holder.serial.setText(row.getString("serial").toString());
        holder.serial.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    TechnicsInspectionDetails.tireLines.get(position).put("serial", holder.serial.getText().toString());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        holder.captureImageTire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = "(" + row.getString("name") + ")";
                TechnicsInspectionDetails.captureOfLine(name);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tireRows.size();
    }
}