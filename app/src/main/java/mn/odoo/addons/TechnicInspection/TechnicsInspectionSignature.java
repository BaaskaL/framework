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
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.odoo.R;
import com.odoo.addons.TechnicInsoection.Models.TechnicsInspectionModel;
import com.odoo.addons.employees.models.Employee;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.support.OUser;

import java.io.ByteArrayOutputStream;

public class TechnicsInspectionSignature extends Activity {
    signature mSignature;
    private TechnicsInspectionModel technicIns;
    Paint paint;
    LinearLayout mContent;
    Button clear, save;
    private String confirmCode;
    private OUser mUser;
    private EditText etxConfirmCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.technic_inspection_signature);
        mUser = OUser.current(this);
        technicIns = new TechnicsInspectionModel(this, null);
        save = (Button) findViewById(R.id.save);
        save.setEnabled(false);
        clear = (Button) findViewById(R.id.clear);
        mContent = (LinearLayout) findViewById(R.id.mysignature);
        etxConfirmCode = (EditText) findViewById(R.id.et_confirm_code);
        Employee employee = new Employee(this, null);
        ODataRow employeeInfo = employee.select(null, "_id = ?", new String[]{"" + technicIns.myId()}).get(0);
        confirmCode = employeeInfo.getString("confirm_code");
        mSignature = new signature(this, null);
        mContent.addView(mSignature);

        save.setOnClickListener(onButtonClick);
        clear.setOnClickListener(onButtonClick);
    }

    Button.OnClickListener onButtonClick = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (v == clear) {
                mSignature.clear();
            } else if (v == save) {
                mSignature.save();
//                if(!etxConfirmCode.getText().toString().equals("") && confirmCode.equals(etxConfirmCode.getText().toString()))
//                    mSignature.save();
//                else
//                    Toast.makeText(CaptureSignature.this, R.string.warning_confirm_code, Toast.LENGTH_LONG).show();
            }
        }
    };

    public class signature extends View {
        static final float STROKE_WIDTH = 10f;
        static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        Paint paint = new Paint();
        Path path = new Path();

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
            path.reset();
            invalidate();
            save.setEnabled(false);
        }

        public void save() {
            Bitmap returnedBitmap = Bitmap.createBitmap(mContent.getWidth(),
                    mContent.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(returnedBitmap);
            Drawable bgDrawable = mContent.getBackground();
            if (bgDrawable != null)
                bgDrawable.draw(canvas);
            else
                canvas.drawColor(Color.WHITE);
            mContent.draw(canvas);

            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            returnedBitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
            Intent intent = new Intent();
            intent.putExtra("byteArray", bs.toByteArray());
            setResult(1, intent);
            finish();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            save.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
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

            return true;
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }
}