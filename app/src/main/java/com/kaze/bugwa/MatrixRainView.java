package com.kaze.bugwa;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.Random;

public class MatrixRainView extends View {
    private Paint paint = new Paint();
    private Random random = new Random();
    private int[] drops;
    private String[] chars = {"0","1","ア","イ","ウ","エ","オ","カ","キ","ク","ケ","コ"};
    private int fontSize = 30;
    private int columns;

    public MatrixRainView(Context context) {
        super(context);
        init();
    }

    public MatrixRainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint.setColor(Color.parseColor("#00FF41")); // Hijau Matrix
        paint.setTextSize(fontSize);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        columns = w / fontSize;
        drops = new int[columns];
        for (int i = 0; i < columns; i++) {
            drops[i] = random.nextInt(h / fontSize);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.parseColor("#050505")); // Background hitam

        if (drops == null) return;

        for (int i = 0; i < drops.length; i++) {
            String text = chars[random.nextInt(chars.length)];
            canvas.drawText(text, i * fontSize, drops[i] * fontSize, paint);
            if (drops[i] * fontSize > getHeight() && random.nextFloat() > 0.975) {
                drops[i] = 0;
            }
            drops[i]++;
        }
        invalidate();
    }
}
