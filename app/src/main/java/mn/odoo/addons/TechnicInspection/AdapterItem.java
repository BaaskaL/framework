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

public class AdapterItem extends RecyclerView.Adapter<AdapterItem.ViewHolder> {
    private List<ODataRow> itemRows = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView edtName, edtCategory;
        public RadioButton inspection_isitnormal, inspection_check;
        public RadioGroup edtRadioGroup;
        public EditText edtDescription;

        public ViewHolder(View view) {
            super(view);
            edtName = (TextView) view.findViewById(R.id.edtName);
            edtCategory = (TextView) view.findViewById(R.id.edtCategory);
            inspection_isitnormal = (RadioButton) view.findViewById(R.id.inspection_isitnormal);
            inspection_check = (RadioButton) view.findViewById(R.id.inspection_check);
            edtRadioGroup = (RadioGroup) view.findViewById(R.id.edtRadioGroup);
            edtDescription = (EditText) view.findViewById(R.id.edtDescription);

            edtDescription.setEnabled(TechnicsInspectionDetails.mEditMode);
            inspection_isitnormal.setEnabled(TechnicsInspectionDetails.mEditMode);
            inspection_check.setEnabled(TechnicsInspectionDetails.mEditMode);
        }
    }

    public AdapterItem(List<ODataRow> itemRows) {
        this.itemRows = itemRows;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.technic_inspection_line_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.i("fffffffffff====", "");
        ODataRow itemRow = itemRows.get(position);
        holder.edtName.setText(((position + 1) + ". " + itemRow.getString("item_name")));
        holder.edtCategory.setText(itemRow.getString("categ_name").toString());
        holder.edtDescription.setText("");
        if (!itemRow.getString("description").equals("false"))
            holder.edtDescription.setText(itemRow.getString("description").toString());
        holder.inspection_isitnormal.setChecked(itemRow.getBoolean("inspection_isitnormal"));
        holder.inspection_check.setChecked(itemRow.getBoolean("inspection_check"));
        holder.edtDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    TechnicsInspectionDetails.inspectionItemLines.get(position).put("description", holder.edtDescription.getText().toString());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        holder.edtRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                TechnicsInspectionDetails.inspectionItemLines.get(position).put("inspection_isitnormal", (checkedId == R.id.inspection_isitnormal) ? true : false);
                TechnicsInspectionDetails.inspectionItemLines.get(position).put("inspection_check", (checkedId == R.id.inspection_check) ? true : false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemRows.size();
    }
}