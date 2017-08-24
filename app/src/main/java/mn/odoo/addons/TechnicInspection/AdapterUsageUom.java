package mn.odoo.addons.TechnicInspection;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

public class AdapterUsageUom extends RecyclerView.Adapter<AdapterUsageUom.ViewHolder> {
    private List<ODataRow> uomRows = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView productUom, usageUom;
        public EditText usageValue;


        public ViewHolder(View view) {
            super(view);
            productUom = (TextView) view.findViewById(R.id.productUom);
            usageUom = (TextView) view.findViewById(R.id.usageUom);
            usageValue = (EditText) view.findViewById(R.id.usageValue);
        }
    }

    public AdapterUsageUom(List<ODataRow> uomRows) {
        this.uomRows = uomRows;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.technic_inspection_usage_uom_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ODataRow uomRow = uomRows.get(position);
        holder.productUom.setText(uomRow.getString("product_uom_name"));
        holder.usageUom.setText(uomRow.getString("usage_uom_name"));
        holder.usageValue.setText("");
        if (!uomRow.getString("usage_value").equals("false")) {
            holder.usageValue.setText(uomRow.getString("usage_value").toString());
        }
        holder.usageValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() != 0) {
                    TechnicsInspectionDetails.linesUom.get(position).put("usage_value", holder.usageValue.getText().toString());
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return uomRows.size();
    }
}