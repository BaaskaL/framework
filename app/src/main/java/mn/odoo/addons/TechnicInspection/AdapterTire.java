package mn.odoo.addons.TechnicInspection;

import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

    public static class ViewHolderTire extends RecyclerView.ViewHolder {
        public TextView name, date_record, current_position, state;
        public EditText serial;

        public ViewHolderTire(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            date_record = (TextView) view.findViewById(R.id.date_record);
            current_position = (TextView) view.findViewById(R.id.current_position);
            state = (TextView) view.findViewById(R.id.state);
            serial = (EditText) view.findViewById(R.id.serial);

            serial.setEnabled(TechnicsInspectionDetails.mEditMode);
        }
    }

    public AdapterTire(List<ODataRow> tireRows) {
        this.tireRows = tireRows;
    }

    @Override
    public ViewHolderTire onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.technic_inspection_tire_item, parent, false);
        return new ViewHolderTire(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolderTire holder, final int position) {
        ODataRow tireRow = tireRows.get(position);
        Log.i("tireRows====", tireRows.toString());
        Log.i("\ntireRow====", tireRow.toString());

        holder.name.setText(((position + 1) + ". " + tireRow.getString("name")));
        holder.date_record.setText(tireRow.getString("date_record").toString());
        holder.current_position.setText(tireRow.getString("current_position").toString());
        holder.serial.setText("");
        if (!tireRow.getString("serial").equals("false"))
            holder.serial.setText(tireRow.getString("serial").toString());
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
    }

    @Override
    public int getItemCount() {
        Log.i("tireRows_size()====", tireRows.size() + "");
        return tireRows.size();
    }
}