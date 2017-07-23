package com.bh.olga_pc.hostess.customViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.constraint.solver.widgets.Rectangle;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.bh.olga_pc.hostess.R;

/**
 * Created by Olga-PC on 7/23/2017.
 */

public class TableView extends View {
    private int shape;
    private boolean showText;
    private int textPos;
    private int bg;
    private int fg;
    private int id;
    private int waiter;
    private Paint textPaint;
    private float mTextHeight;
    private Paint shapePaint;
    private Paint shadowPaint;
    private float diameter;
    private RectF tableBounds;


    public TableView(Context context) {
        this(context, null);
    }

    public TableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TableView, 0, 0);
        try {
            showText = a.getBoolean(R.styleable.TableView_showText, true);
            textPos = a.getInteger(R.styleable.TableView_labelPosition, 0);
            shape = a.getInteger(R.styleable.TableView_shape, 1);
            bg = a.getColor(R.styleable.TableView_bg, ContextCompat.getColor(context, R.color.primary_light));
            fg = a.getColor(R.styleable.TableView_fg, ContextCompat.getColor(context, R.color.primary_dark));
        } finally {
            //Note that TypedArray objects are a shared resource and must be recycled after use.
            a.recycle();
        }
        init();
    }

    private void init() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(fg);
        if (mTextHeight == 0) {
            mTextHeight = textPaint.getTextSize();
        } else {
            textPaint.setTextSize(mTextHeight);
        }

        shapePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shapePaint.setStyle(Paint.Style.FILL);
        shapePaint.setTextSize(mTextHeight);

        shadowPaint = new Paint(0);
        shadowPaint.setColor(0xff101010);
        shadowPaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));
        tableBounds = new RectF(0,0,0,0);
    }

    public TableView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public boolean isShowText() {
        return showText;
    }

    public int getTextPos() {
        return textPos;
    }

    public int getBg() {
        return bg;
    }

    public int getFg() {
        return fg;
    }

    public void setShowText(boolean showText) {
        this.showText = showText;
        invalidate();
        requestLayout();
    }

    public void setTextPos(int textPos) {
        this.textPos = textPos;
        invalidate();
        requestLayout();
    }

    public void setBg(int bg) {
        this.bg = bg;
        invalidate();
        requestLayout();
    }

    public void setFg(int fg) {
        this.fg = fg;
        invalidate();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
        invalidate();
    }

    public int getWaiter() {
        return waiter;
    }

    public void setWaiter(int waiter) {
        this.waiter = waiter;
        invalidate();
    }

    public int getShape() {
        return shape;
    }

    public void setShape(int shape) {
        this.shape = shape;
        invalidate();
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawOval(tableBounds, shapePaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Account for padding
        float xpad = (float) (getPaddingLeft() + getPaddingRight());
        float ypad = (float) (getPaddingTop() + getPaddingBottom());

        // Account for the label
        //if (showText) xpad += textWidth;

        float ww = (float) w - xpad;
        float hh = (float) h - ypad;

        // Figure out how big we can make the pie.
        diameter = Math.min(ww, hh);
        tableBounds = new RectF(getPaddingLeft(), getPaddingTop(), diameter, diameter);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Try for a width based on our minimum
        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int w = resolveSizeAndState(minw, widthMeasureSpec, 1);

        // Whatever the width ends up being, ask for a height that would let the pie
        // get as big as it can
        int minh = MeasureSpec.getSize(w) + getPaddingBottom() + getPaddingTop();
        int h = resolveSizeAndState(MeasureSpec.getSize(w), heightMeasureSpec, 0);
// Figure out how big we can make the pie.
        diameter = Math.min(w, h);
        tableBounds.set(getPaddingLeft(), getPaddingTop(), diameter, diameter);
        setMeasuredDimension(w, h);
    }
}
