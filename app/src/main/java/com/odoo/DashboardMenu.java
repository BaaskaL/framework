package com.odoo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.odoo.core.utils.IntentUtils;

public class DashboardMenu extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_layout);
    }

    //    @Override
    public void on_click_menu_button(View v) {
        int index = -1;
        switch (v.getId()) {
            case R.id.btn_employee:
                index = 0;
                break;
            case R.id.btn_part_scrap:
                index = 3;
                break;
            case R.id.btn_tire_scrap:
                index = 4;
                break;
            case R.id.btn_technic:
                index = 5;
                break;
            case R.id.btn_inpection:
                index = 6;
                break;
            case R.id.btn_repair_in:
                index = -1;
                break;
            case R.id.btn_technic_scrap:
                index = -1;
                break;
            case R.id.btn_oil_scrap:
                index = -1;
                break;
            case R.id.btn_accumulator_scrap:
                index = -1;
                break;
//            10,9,
        }

        final Bundle data = new Bundle();
        data.putInt("selectedMenuIndex", index);
        IntentUtils.startActivity(DashboardMenu.this, OdooActivity.class, data);
    }
}