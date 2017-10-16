package mn.odoo.addons.TechnicInspection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.odoo.R;
import com.odoo.addons.TechnicInsoection.Models.TechnicsInspectionModel;
import com.odoo.addons.employees.models.Employee;
import com.odoo.base.addons.res.ResUsers;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.support.OdooCompatActivity;
import com.odoo.core.utils.OAlert;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.graphics.Bitmap.Config.ARGB_8888;

public class TechnicsInspectionSignature extends OdooCompatActivity {
    signature mSignature;
    signature mSignature_2;
    signature mSignature_3;
    private Menu mMenu;
    private TechnicsInspectionModel technicIns;
    private Employee employee;
    private ResUsers resUsers;
    private CardView worker_1, worker_2, worker_3;
    LinearLayout segtnature_1, segtnature_2, segtnature_3;
    private TextView clear_1, save_1, clear_2, save_2, clear_3, save_3, title_1, title_2, title_3;
    private EditText ConfirmCode_1, ConfirmCode_2, ConfirmCode_3;
    private Bundle extra;
    private Toolbar toolbar;
    int registrar_worker, respondent_worker, inspection_commis = -1;
    private Bundle data = new Bundle();
    private boolean isSave, isSave_2, isSave_3 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.technic_inspection_signature);
        toolbar = (Toolbar) findViewById(R.id.toolbarSignature);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        extra = getIntent().getExtras();

        registrar_worker = extra.getInt("registrar_worker");
        respondent_worker = extra.getInt("respondent_worker");
        inspection_commis = extra.getInt("inspection_commis");

        technicIns = new TechnicsInspectionModel(this, null);
        employee = new Employee(this, null);
        resUsers = new ResUsers(this, null);

        worker_1 = (CardView) findViewById(R.id.worker_1);
        worker_2 = (CardView) findViewById(R.id.worker_2);
        worker_3 = (CardView) findViewById(R.id.worker_3);
        title_1 = (TextView) findViewById(R.id.worker_1_title);
        title_2 = (TextView) findViewById(R.id.worker_2_title);
        title_3 = (TextView) findViewById(R.id.worker_3_title);
        isSave = true;
        isSave_2 = true;
        isSave_3 = true;
        if (registrar_worker > 0) {
            title_1.setText("Бүртгэгч: " + resUsers.browse(registrar_worker).getString("name"));
            worker_1.setVisibility(View.VISIBLE);
            isSave = false;
        }

        if (respondent_worker > 0) {
            title_2.setText("Жолооч/Хариуцагч: " + employee.browse(respondent_worker).getString("name"));
            worker_2.setVisibility(View.VISIBLE);
            isSave_2 = false;
        }
        if (inspection_commis > 0) {
            title_3.setText("Комисс: " + employee.browse(inspection_commis).getString("name"));
            worker_3.setVisibility(View.VISIBLE);
            isSave_3 = false;
        }

        save_1 = (TextView) findViewById(R.id.tvSave);
        save_2 = (TextView) findViewById(R.id.tvSave_2);
        save_3 = (TextView) findViewById(R.id.tvSave_3);

        clear_1 = (TextView) findViewById(R.id.btnClear);
        clear_2 = (TextView) findViewById(R.id.btnClear_2);
        clear_3 = (TextView) findViewById(R.id.btnClear_3);

        segtnature_1 = (LinearLayout) findViewById(R.id.workerSignature_1);
        segtnature_2 = (LinearLayout) findViewById(R.id.workerSignature_2);
        segtnature_3 = (LinearLayout) findViewById(R.id.workerSignature_3);

        ConfirmCode_1 = (EditText) findViewById(R.id.tvCode);
        ConfirmCode_2 = (EditText) findViewById(R.id.tvCode_2);
        ConfirmCode_3 = (EditText) findViewById(R.id.tvCode_3);

        mSignature = new signature(this, null);
        mSignature_2 = new signature(this, null);
        mSignature_3 = new signature(this, null);

        segtnature_1.addView(mSignature);
        segtnature_2.addView(mSignature_2);
        segtnature_3.addView(mSignature_3);

        save_1.setOnClickListener(onButtonClick);
        save_2.setOnClickListener(onButtonClick);
        save_3.setOnClickListener(onButtonClick);

        clear_1.setOnClickListener(onButtonClick);
        clear_2.setOnClickListener(onButtonClick);
        clear_3.setOnClickListener(onButtonClick);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_signature_save:
                if (isSave && isSave_2 && isSave_3) {
                    Intent intent = new Intent();
                    intent.putExtras(data);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    OAlert.showError(this, "Гарын үсэг болон нууц үгээ оруулна уу!!!");
                }
                return true;
            default:
                setResult(RESULT_CANCELED);
                finish();
                return true;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_technic_inspection_signature_detail, menu);
        mMenu = menu;
        return true;
    }

    private void viewEditAble(TextView v, EditText e, boolean editAble) {
        if (v != null && !editAble) {
            v.setTextColor(Color.parseColor("#6a6a6a"));
        } else if (v != null && editAble) {
            v.setTextColor(Color.parseColor("#0099CC"));
        }
        if (e != null) {
            e.setEnabled(editAble);
        }
    }

    Button.OnClickListener onButtonClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnClear:
                    viewEditAble(save_1, ConfirmCode_1, true);
                    mSignature.clear();
                    isSave = false;
                    break;
                case R.id.btnClear_2:
                    viewEditAble(save_2, ConfirmCode_2, true);
                    mSignature_2.clear();
                    isSave_2 = false;
                    break;
                case R.id.btnClear_3:
                    viewEditAble(save_3, ConfirmCode_3, true);
                    mSignature_3.clear();
                    isSave_3 = false;
                    break;
                case R.id.tvSave:
                    if (mSignature.save(segtnature_1, registrar_worker, ConfirmCode_1, "registrar_worker")) {
                        isSave = true;
                        viewEditAble(save_1, ConfirmCode_1, false);
                    }
                    break;
                case R.id.tvSave_2:
                    if (mSignature_2.save(segtnature_2, respondent_worker, ConfirmCode_2, "respondent_worker")) {
                        isSave_2 = true;
                        viewEditAble(save_2, ConfirmCode_2, false);
                    }
                    break;
                case R.id.tvSave_3:
                    if (mSignature_3.save(segtnature_3, inspection_commis, ConfirmCode_3, "inspection_commis")) {
                        isSave_3 = true;
                        viewEditAble(save_3, ConfirmCode_3, false);
                    }
                    break;
            }
        }
    };

    public class signature extends View {
        static final float STROKE_WIDTH = 6f;
        static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        Paint paint = new Paint();
        Path path = new Path();
        boolean done = true;
        float lastTouchX;
        float lastTouchY;
        final RectF dirtyRect = new RectF();


        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void clear() {
            done = true;
            path.reset();
            invalidate();
        }

        public boolean save(LinearLayout ll, int id, EditText txt, String key) {
            Bitmap returnedBitmap = Bitmap.createBitmap(ll.getWidth(), ll.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(returnedBitmap);
            Drawable bgDrawable = ll.getBackground();
            if (bgDrawable != null)
                bgDrawable.draw(canvas);
            else
                canvas.drawColor(Color.WHITE);
            ll.draw(canvas);

            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            Bitmap resized = Bitmap.createScaledBitmap(returnedBitmap, 500, 312, true);
            resized.compress(Bitmap.CompressFormat.PNG, 100, bs);
            byte[] b = bs.toByteArray();
            String strImg = Base64.encodeToString(b, Base64.DEFAULT);
            List<ODataRow> emp = new ArrayList<>();
            if (key.equals("registrar_worker")) {
                emp = employee.select(null, "user_id = ?", new String[]{id + ""});
            } else {
                emp = employee.select(null, "_id = ?", new String[]{id + ""});
            }
            if (emp.size() > 0) {
                ODataRow employ = emp.get(0);
                if (employ.getString("confirm_code").equals(txt.getText().toString())) {
                    data.putString(key, strImg);
                    done = false;
                    return true;
                } else {
                    OAlert.showError(TechnicsInspectionSignature.this, employ.get("name") + ": Нүүц үг буруу байна.!!!");
                }
            } else {
                OAlert.showError(TechnicsInspectionSignature.this, "Ажилтны мэдээлэл татна уу.!!!");
            }
            return false;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (done) {
                float eventX = event.getX();
                float eventY = event.getY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        path.moveTo(eventX, eventY);
                        lastTouchX = eventX;
                        lastTouchY = eventY;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        int historySize = event.getHistorySize();
                        for (int i = 0; i < historySize; i++) {
                            float historicalX = event.getHistoricalX(i);
                            float historicalY = event.getHistoricalY(i);
                            path.lineTo(historicalX, historicalY);
                        }
                        path.lineTo(eventX, eventY);
                        break;
                }
                invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                        (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                        (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                        (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

                lastTouchX = eventX;
                lastTouchY = eventY;
            }
            return true;
        }
    }
}