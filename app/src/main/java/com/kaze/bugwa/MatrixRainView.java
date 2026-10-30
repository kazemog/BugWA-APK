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
    private String chars = "01アイウエオカキクケコサシスセソタチツテトナニヌネノABCDEFGHIJKLMNOPQRSTUVWXYZ@#$%&*";
    private int fontSize = 24;
    private int columns;

    public MatrixRainView(Context context) { super(context); init(); }
    public MatrixRainView(Context context, AttributeSet attrs) { super(context, attrs); init(); }

    private void init() {
        paint.setColor(Color.parseColor("#8B4513")); // Warna Coklat Gelap
        paint.setTextSize(fontSize);
        paint.setAntiAlias(true);
        paint.setShadowLayer(4, 0, 0, Color.parseColor("#D2B48C")); // Glow Tan
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            columns = w / fontSize;
            drops = new int[columns];
            for (int i = 0; i < columns; i++) {
                drops[i] = random.nextInt(h / fontSize);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.parseColor("#050505"));
        
        // PENGAMAN CRASH: Jika drops belum siap, jangan gambar apa-apa
        if (drops == null || drops.length == 0) return;

        for (int i = 0; i < drops.length; i++) {
            int charIndex = random.nextInt(chars.length());
            String text = String.valueOf(chars.charAt(charIndex));
            
            paint.setColor(Color.WHITE); 
            canvas.drawText(text, i * fontSize, drops[i] * fontSize, paint);
            
            paint.setColor(Color.parseColor("#8B4513"));
            canvas.drawText(text, i * fontSize, (drops[i] - 1) * fontSize, paint);

            if (drops[i] * fontSize > getHeight() && random.nextFloat() > 0.95) {
                drops[i] = 0;
            }
            drops[i]++;
        }
        invalidate();
    }
}
