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

public class AdapterAccumulator extends RecyclerView.Adapter<AdapterAccumulator.ViewHolderAccum> {
    private List<ODataRow> accumulatorRows = new ArrayList<>();
    private static final int CAMERA_REQUEST = 1888;

    public static class ViewHolderAccum extends RecyclerView.ViewHolder {
        public TextView name, date, usage;
        public EditText serial;
        public FloatingActionButton captureImageAccum;

        public ViewHolderAccum(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.nameAccum);
            date = (TextView) view.findViewById(R.id.dateAccum);
            usage = (TextView) view.findViewById(R.id.stateUsagePercent);
            serial = (EditText) view.findViewById(R.id.serialAccum);
            captureImageAccum = (FloatingActionButton) view.findViewById(R.id.captureImageAccumu);
            serial.setEnabled(TechnicsInspectionDetails.mEditMode);
        }
    }


    public AdapterAccumulator(List<ODataRow> accumulatorRows) {
        this.accumulatorRows = accumulatorRows;
    }

    @Override
    public ViewHolderAccum onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.technic_inspection_accumulator_item, null);
        return new ViewHolderAccum(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolderAccum holder, final int position) {
        final ODataRow row = accumulatorRows.get(position);
        holder.name.setText(((position + 1) + ". " + row.getString("name")));
        holder.date.setText(row.getString("date").toString());
        holder.usage.setText(row.getString("usage_percent"));
        holder.serial.setText("");
        if (!row.getString("serial").equals("false"))
            holder.serial.setText(row.getString("serial").toString());
        holder.serial.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    TechnicsInspectionDetails.accumulatorLines.get(position).put("serial", holder.serial.getText().toString());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        holder.captureImageAccum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = "(" + row.getString("name") + ")";
                TechnicsInspectionDetails.captureOfLine(name);
            }
        });
    }

    @Override
    public int getItemCount() {
        return accumulatorRows.size();
    }
}