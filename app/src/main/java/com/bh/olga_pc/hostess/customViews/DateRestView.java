package com.bh.olga_pc.hostess.customViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.OverScroller;

import com.bh.olga_pc.hostess.R;
import com.bh.olga_pc.hostess.Utilities;
import com.bh.olga_pc.hostess.tbls.DayTableView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import beans.Event;

import static com.bh.olga_pc.hostess.Utilities.isSameDay;

/**
 * Created by olgats on 27/07/2017.
 */

public class DateRestView extends View {
    private android.view.GestureDetector.OnGestureListener gestureListener;
    private float mWidthPerTable;
    private ScrollListener mScrollListener;
    private TableViewLoader mRegionViewLoader;


    private enum Direction {
        NONE, LEFT, RIGHT, VERTICAL
    }

    private Context mContext;
    private Paint mTimeTextPaint;
    private float mTimeTextWidth;
    private float mTimeTextHeight;
    private Paint mHeaderTextPaint;
    private float mHeaderTextHeight;
    private float mHeaderHeight;
    private GestureDetectorCompat mGestureDetector;
    private OverScroller mScroller;
    private PointF mCurrentOrigin = new PointF(0f, 0f);
    private Direction mCurrentScrollDirection = Direction.NONE;
    private Paint mHeaderBackgroundPaint;
    private float widthPerTable;
    private Paint mTableBackgroundPaint;
    private Paint mHourSeparatorPaint;
    private float mHeaderMarginBottom;
    private Paint mTodayBackgroundPaint;
    private Paint mFutureBackgroundPaint;
    private Paint mPastBackgroundPaint;
    private Paint mFutureWeekendBackgroundPaint;
    private Paint mPastWeekendBackgroundPaint;
    private Paint mNowLinePaint;
    private Paint mTodayHeaderTextPaint;
    private Paint mEventBackgroundPaint;
    private float mHeaderColumnWidth;
    private List<EventRect> mEventRects;
    private List<? extends Event> mPreviousPeriodEvents;
    private List<? extends Event> mCurrentPeriodEvents;
    private List<? extends Event> mNextPeriodEvents;
    private TextPaint mEventTextPaint;
    private Paint mHeaderColumnBackgroundPaint;
    private int regionPeriod = -1; // the middle period the calendar has fetched.
    private boolean mRefreshEvents = false;
    private Direction mCurrentFlingDirection = Direction.NONE;
    private ScaleGestureDetector mScaleDetector;
    private boolean mIsZooming;
    private int mDefaultEventColor;
    private int mMinimumFlingVelocity = 0;
    private int mScaledTouchSlop = 0;
    // Attributes and their default values.
    private int mHourHeight = 50;
    private int mNewHourHeight = -1;
    private int mMinHourHeight = 0; //no minimum specified (will be dynamic, based on screen)
    private int mEffectiveMinHourHeight = mMinHourHeight; //compensates for the fact that you can't keep zooming out.
    private int mMaxHourHeight = 250;
    private int mColumnGap = 10;
    private int mTextSize = 12;
    private int mHeaderColumnPadding = 10;
    private int mHeaderColumnTextColor = Color.BLACK;
    private int numberOfTables = 10;
    private int mHeaderRowPadding = 10;
    private int tableBackgroundColor = Color.rgb(245, 245, 245);
    private int mHeaderRowBackgroundColor = Color.WHITE;
    private int mHourSeparatorColor = Color.rgb(230, 230, 230);
    private int mHourSeparatorHeight = 2;
    private int mEventTextSize = 12;
    private int mEventTextColor = Color.BLACK;
    private int mEventPadding = 8;
    private int mHeaderColumnBackgroundColor = Color.WHITE;
    private boolean mIsFirstDraw = true;
    private boolean mAreDimensionsInvalid = true;
    private int mOverlappingEventGap = 0;
    private int mEventMarginVertical = 0;
    private float mXScrollingSpeed = 1f;
    private int scrollToTable =-1;
    private double scrollToHour = -1;
    private int mEventCornerRadius = 0;
    private boolean mHorizontalFlingEnabled = true;
    private boolean mVerticalFlingEnabled = true;
    private int mAllDayEventHeight = 100;
    private int mScrollDuration = 250;
    private Calendar selectedDate;
    private int mFirstVisibleTable;
    private int mLastVisibleTable;


    // Listeners.
    private EventClickListener eventClickListener;
    private EventLongPressListener eventLongPressListener;
    private EmptyViewClickListener emptyViewClickListener;
    private EmptyViewLongPressListener emptyViewLongPressListener;

    public DateRestView(Context context) {
        super(context);
    }

    public DateRestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DateRestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DateRestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        // Hold references.
        mContext = context;
        selectedDate = Calendar.getInstance();
        // Get the attribute values (if any).
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DateRestView, 0, 0);
        try {

            mHourHeight = a.getDimensionPixelSize(R.styleable.DateRestView_hourHeight, mHourHeight);
            mMinHourHeight = a.getDimensionPixelSize(R.styleable.DateRestView_minHourHeight, mMinHourHeight);
            mEffectiveMinHourHeight = mMinHourHeight;
            mMaxHourHeight = a.getDimensionPixelSize(R.styleable.DateRestView_maxHourHeight, mMaxHourHeight);
            mTextSize = a.getDimensionPixelSize(R.styleable.DateRestView_textSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTextSize, context.getResources().getDisplayMetrics()));
            mHeaderColumnPadding = a.getDimensionPixelSize(R.styleable.DateRestView_headerColumnPadding, mHeaderColumnPadding);
            mColumnGap = a.getDimensionPixelSize(R.styleable.DateRestView_columnGap, mColumnGap);
            mHeaderColumnTextColor = a.getColor(R.styleable.DateRestView_headerColumnTextColor, mHeaderColumnTextColor);
            numberOfTables = a.getInteger(R.styleable.DateRestView_noOfTables, numberOfTables);
            mHeaderRowPadding = a.getDimensionPixelSize(R.styleable.DateRestView_headerRowPadding, mHeaderRowPadding);
            mHeaderRowBackgroundColor = a.getColor(R.styleable.DateRestView_headerRowBackgroundColor, mHeaderRowBackgroundColor);
            tableBackgroundColor = a.getColor(R.styleable.DateRestView_tableBackgroundColor, tableBackgroundColor);
            mHourSeparatorColor = a.getColor(R.styleable.DateRestView_hourSeparatorColor, mHourSeparatorColor);
            mHourSeparatorHeight = a.getDimensionPixelSize(R.styleable.DateRestView_hourSeparatorHeight, mHourSeparatorHeight);
            mEventTextSize = a.getDimensionPixelSize(R.styleable.DateRestView_eventTextSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mEventTextSize, context.getResources().getDisplayMetrics()));
            mEventTextColor = a.getColor(R.styleable.DateRestView_eventTextColor, mEventTextColor);
            mEventPadding = a.getDimensionPixelSize(R.styleable.DateRestView_eventPadding, mEventPadding);
            mHeaderColumnBackgroundColor = a.getColor(R.styleable.DateRestView_headerColumnBackground, mHeaderColumnBackgroundColor);
            mOverlappingEventGap = a.getDimensionPixelSize(R.styleable.DateRestView_overlappingEventGap, mOverlappingEventGap);
            mEventMarginVertical = a.getDimensionPixelSize(R.styleable.DateRestView_eventMarginVertical, mEventMarginVertical);
            mXScrollingSpeed = a.getFloat(R.styleable.DateRestView_xScrollingSpeed, mXScrollingSpeed);
            mEventCornerRadius = a.getDimensionPixelSize(R.styleable.DateRestView_eventCornerRadius, mEventCornerRadius);
            mHorizontalFlingEnabled = a.getBoolean(R.styleable.DateRestView_horizontalFlingEnabled, mHorizontalFlingEnabled);
            mVerticalFlingEnabled = a.getBoolean(R.styleable.DateRestView_verticalFlingEnabled, mVerticalFlingEnabled);
            mAllDayEventHeight = a.getDimensionPixelSize(R.styleable.DateRestView_allDayEventHeight, mAllDayEventHeight);
            mScrollDuration = a.getInt(R.styleable.DateRestView_scrollDuration, mScrollDuration);
        } finally {
            a.recycle();
        }

        init();
    }

    private void init() {
        initGestureListener();
        // Scrolling initialization.
        mGestureDetector = new GestureDetectorCompat(mContext, gestureListener);
        mScroller = new OverScroller(mContext, new FastOutLinearInInterpolator());

        mMinimumFlingVelocity = ViewConfiguration.get(mContext).getScaledMinimumFlingVelocity();
        mScaledTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();

        // Measure settings for time column.
        mTimeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimeTextPaint.setTextAlign(Paint.Align.RIGHT);
        mTimeTextPaint.setTextSize(mTextSize);
        mTimeTextPaint.setColor(mHeaderColumnTextColor);
        Rect rect = new Rect();
        mTimeTextPaint.getTextBounds("00 PM", 0, "00 PM".length(), rect);
        mTimeTextHeight = rect.height();
        mHeaderMarginBottom = mTimeTextHeight / 2;
        initTextTimeWidth();

        // Measure settings for header row.
        mHeaderTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHeaderTextPaint.setColor(mHeaderColumnTextColor);
        mHeaderTextPaint.setTextAlign(Paint.Align.CENTER);
        mHeaderTextPaint.setTextSize(mTextSize);
        mHeaderTextPaint.getTextBounds("00 PM", 0, "00 PM".length(), rect);
        mHeaderTextHeight = rect.height();
        mHeaderTextPaint.setTypeface(Typeface.DEFAULT_BOLD);

        // Prepare header background paint.
        mHeaderBackgroundPaint = new Paint();
        mHeaderBackgroundPaint.setColor(mHeaderRowBackgroundColor);

        // Prepare day background color paint.
        mTableBackgroundPaint = new Paint();
        mTableBackgroundPaint.setColor(tableBackgroundColor);

        // Prepare hour separator color paint.
        mHourSeparatorPaint = new Paint();
        mHourSeparatorPaint.setStyle(Paint.Style.STROKE);
        mHourSeparatorPaint.setStrokeWidth(mHourSeparatorHeight);
        mHourSeparatorPaint.setColor(mHourSeparatorColor);


        // Prepare event background color.
        mEventBackgroundPaint = new Paint();
        mEventBackgroundPaint.setColor(Color.rgb(174, 208, 238));

        // Prepare header column background color.
        mHeaderColumnBackgroundPaint = new Paint();
        mHeaderColumnBackgroundPaint.setColor(mHeaderColumnBackgroundColor);

        // Prepare event text size and color.
        mEventTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        mEventTextPaint.setStyle(Paint.Style.FILL);
        mEventTextPaint.setColor(mEventTextColor);
        mEventTextPaint.setTextSize(mEventTextSize);

        // Set default event color.
        mDefaultEventColor = Color.parseColor("#9fc6e7");

        mScaleDetector = new ScaleGestureDetector(mContext, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                mIsZooming = false;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                mIsZooming = true;
                goToNearestOrigin();
                return true;
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                mNewHourHeight = Math.round(mHourHeight * detector.getScaleFactor());
                invalidate();
                return true;
            }
        });
    }

    private void initGestureListener() {
        gestureListener = new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDown(MotionEvent e) {
                goToNearestOrigin();
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                // Check if view is zoomed.
                if (mIsZooming)
                    return true;

                switch (mCurrentScrollDirection) {
                    case NONE: {
                        // Allow scrolling only in one direction.
                        if (Math.abs(distanceX) > Math.abs(distanceY)) {
                            if (distanceX > 0) {
                                mCurrentScrollDirection = Direction.LEFT;
                            } else {
                                mCurrentScrollDirection = Direction.RIGHT;
                            }
                        } else {
                            mCurrentScrollDirection = Direction.VERTICAL;
                        }
                        break;
                    }
                    case LEFT: {
                        // Change direction if there was enough change.
                        if (Math.abs(distanceX) > Math.abs(distanceY) && (distanceX < -mScaledTouchSlop)) {
                            mCurrentScrollDirection = Direction.RIGHT;
                        }
                        break;
                    }
                    case RIGHT: {
                        // Change direction if there was enough change.
                        if (Math.abs(distanceX) > Math.abs(distanceY) && (distanceX > mScaledTouchSlop)) {
                            mCurrentScrollDirection = Direction.LEFT;
                        }
                        break;
                    }
                }

                // Calculate the new origin after scroll.
                switch (mCurrentScrollDirection) {
                    case LEFT:
                    case RIGHT:
                        mCurrentOrigin.x -= distanceX * mXScrollingSpeed;
                        ViewCompat.postInvalidateOnAnimation(DateRestView.this);
                        break;
                    case VERTICAL:
                        mCurrentOrigin.y -= distanceY;
                        ViewCompat.postInvalidateOnAnimation(DateRestView.this);
                        break;
                }
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (mIsZooming)
                    return true;

                if ((mCurrentFlingDirection == Direction.LEFT && !mHorizontalFlingEnabled) ||
                        (mCurrentFlingDirection == Direction.RIGHT && !mHorizontalFlingEnabled) ||
                        (mCurrentFlingDirection == Direction.VERTICAL && !mVerticalFlingEnabled)) {
                    return true;
                }

                mScroller.forceFinished(true);

                mCurrentFlingDirection = mCurrentScrollDirection;
                switch (mCurrentFlingDirection) {
                    case LEFT:
                    case RIGHT:
                        mScroller.fling((int) mCurrentOrigin.x, (int) mCurrentOrigin.y, (int) (velocityX * mXScrollingSpeed), 0, Integer.MIN_VALUE, Integer.MAX_VALUE, (int) -(mHourHeight * 24 + mHeaderHeight + mHeaderRowPadding * 2 + mHeaderMarginBottom + mTimeTextHeight / 2 - getHeight()), 0);
                        break;
                    case VERTICAL:
                        mScroller.fling((int) mCurrentOrigin.x, (int) mCurrentOrigin.y, 0, (int) velocityY, Integer.MIN_VALUE, Integer.MAX_VALUE, (int) -(mHourHeight * 24 + mHeaderHeight + mHeaderRowPadding * 2 + mHeaderMarginBottom + mTimeTextHeight / 2 - getHeight()), 0);
                        break;
                }

                ViewCompat.postInvalidateOnAnimation(DateRestView.this);
                return true;
            }


            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                // If the tap was on an event then trigger the callback.
                if (mEventRects != null && eventClickListener != null) {
                    List<EventRect> reversedEventRects = mEventRects;
                    Collections.reverse(reversedEventRects);
                    for (EventRect event : reversedEventRects) {
                        if (event.rectF != null && e.getX() > event.rectF.left && e.getX() < event.rectF.right && e.getY() > event.rectF.top && e.getY() < event.rectF.bottom) {
                            eventClickListener.onEventClick(event.originalEvent, event.rectF);
                            playSoundEffect(SoundEffectConstants.CLICK);
                            return super.onSingleTapConfirmed(e);
                        }
                    }
                }

                // If the tap was on in an empty space, then trigger the callback.
                if (emptyViewClickListener != null && e.getX() > mHeaderColumnWidth && e.getY() > (mHeaderHeight + mHeaderRowPadding * 2 + mHeaderMarginBottom)) {
                    TableAppointment ap = getTimeFromPoint(e.getX(), e.getY());
                    if (ap != null) {
                        playSoundEffect(SoundEffectConstants.CLICK);
                        emptyViewClickListener.onEmptyViewClicked(ap);
                    }
                }

                return super.onSingleTapConfirmed(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);

                if (eventLongPressListener != null && mEventRects != null) {
                    List<EventRect> reversedEventRects = mEventRects;
                    Collections.reverse(reversedEventRects);
                    for (EventRect event : reversedEventRects) {
                        if (event.rectF != null && e.getX() > event.rectF.left && e.getX() < event.rectF.right && e.getY() > event.rectF.top && e.getY() < event.rectF.bottom) {
                            eventLongPressListener.onEventLongPress(event.originalEvent, event.rectF);
                            performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                            return;
                        }
                    }
                }

                // If the tap was on in an empty space, then trigger the callback.
                if (emptyViewLongPressListener != null && e.getX() > mHeaderColumnWidth && e.getY() > (mHeaderHeight + mHeaderRowPadding * 2 + mHeaderMarginBottom)) {
                    TableAppointment appointment = getTimeFromPoint(e.getX(), e.getY());
                    if (appointment != null) {
                        performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                        emptyViewLongPressListener.onEmptyViewLongPress(appointment);
                    }
                }
            }
        };
    }

    // fix rotation changes
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mAreDimensionsInvalid = true;
    }

    public Calendar getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Calendar selectedDate) {
        this.selectedDate = selectedDate;
    }

    /**
     * Initialize time column width. Calculate value with all possible hours (supposed widest text).
     */
    private void initTextTimeWidth() {
        mTimeTextWidth = 0;
        for (int i = 0; i < 24; i++) {
            // Measure time string and get max width.
            String time = Utilities.interpretTime(getContext(), i);
            if (time == null)
                throw new IllegalStateException("A DateTimeInterpreter must not return null time");
            mTimeTextWidth = Math.max(mTimeTextWidth, mTimeTextPaint.measureText(time));
        }
    }

    private void goToNearestOrigin() {
        double leftTables = mCurrentOrigin.x / (widthPerTable + mColumnGap);
        if (mCurrentFlingDirection != Direction.NONE) {
            // snap to nearest table
            leftTables = Math.round(leftTables);
        } else if (mCurrentScrollDirection == Direction.LEFT) {
            // snap to last day
            leftTables = Math.floor(leftTables);
        } else if (mCurrentScrollDirection == Direction.RIGHT) {
            // snap to next day
            leftTables = Math.ceil(leftTables);
        } else {
            // snap to nearest day
            leftTables = Math.round(leftTables);
        }

        int nearestOrigin = (int) (mCurrentOrigin.x - leftTables * (widthPerTable + mColumnGap));

        if (nearestOrigin != 0) {
            // Stop current animation.
            mScroller.forceFinished(true);
            // Snap to date.
            mScroller.startScroll((int) mCurrentOrigin.x, (int) mCurrentOrigin.y, -nearestOrigin, 0, (int) (Math.abs(nearestOrigin) / widthPerTable * mScrollDuration));
            ViewCompat.postInvalidateOnAnimation(this);
        }
        // Reset scrolling and fling direction.
        mCurrentScrollDirection = mCurrentFlingDirection = Direction.NONE;
    }

    /**
     * Get the time and date where the user clicked on.
     *
     * @param x The x position of the touch event.
     * @param y The y position of the touch event.
     * @return The time and date at the clicked position.
     */
    private TableAppointment getTimeFromPoint(float x, float y) {
        int leftTablesWithGaps = (int) -(Math.ceil(mCurrentOrigin.x / (widthPerTable + mColumnGap)));
        float startPixel = mCurrentOrigin.x + (widthPerTable + mColumnGap) * leftTablesWithGaps +
                mHeaderColumnWidth;
        for (int table = leftTablesWithGaps + 1;
             table <= leftTablesWithGaps + numberOfTables + 1;
             table++) {
            float start = (startPixel < mHeaderColumnWidth ? mHeaderColumnWidth : startPixel);
            if (widthPerTable + startPixel - start > 0 && x > start && x < startPixel + widthPerTable) {
                Calendar day = selectedDate;
                float pixelsFromZero = y - mCurrentOrigin.y - mHeaderHeight
                        - mHeaderRowPadding * 2 - mTimeTextHeight / 2 - mHeaderMarginBottom;
                int hour = (int) (pixelsFromZero / mHourHeight);
                int minute = (int) (60 * (pixelsFromZero - hour * mHourHeight) / mHourHeight);
                day.add(Calendar.HOUR, hour);
                day.set(Calendar.MINUTE, minute);
                return new TableAppointment(table, day);
            }
            startPixel += widthPerTable + mColumnGap;
        }
        return null;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the header row.
        drawHeaderRowAndEvents(canvas);

        // Draw the time column and all the axes/separators.
        drawTimeColumnAndAxes(canvas);
    }

    private void calculateHeaderHeight() {
        //Make sure the header is the right size (depends on AllDay events)
        boolean containsAllDayEvent = false;
        if (mEventRects != null && mEventRects.size() > 0) {
            for (int tableNumber = 0;  tableNumber < numberOfTables&&!containsAllDayEvent;  tableNumber++) {
                for (int i = 0; i < mEventRects.size()&&!containsAllDayEvent; i++) {
                    containsAllDayEvent=mEventRects.get(i).event.getTbl()==tableNumber+1/* && mEventRects.get(i).event.isAllDay()*/ ;
                }
            }
        }
        if (containsAllDayEvent) {
            mHeaderHeight = mHeaderTextHeight + (mAllDayEventHeight + mHeaderMarginBottom);
        } else {
            mHeaderHeight = mHeaderTextHeight;
        }
    }


    private void drawHeaderRowAndEvents(Canvas canvas) {
        // Calculate the available width for each day.
        mHeaderColumnWidth = mTimeTextWidth + mHeaderColumnPadding * 2;
        mWidthPerTable = getWidth() - mHeaderColumnWidth - mColumnGap * (numberOfTables - 1);
        mWidthPerTable = mWidthPerTable / numberOfTables;

        calculateHeaderHeight(); //Make sure the header is the right size (depends on AllDay events)



        if (mAreDimensionsInvalid) {
            mEffectiveMinHourHeight = Math.max(mMinHourHeight, (int) ((getHeight() - mHeaderHeight - mHeaderRowPadding * 2 - mHeaderMarginBottom) / 24));

            mAreDimensionsInvalid = false;
            if (scrollToTable >=0)
                goToTable(scrollToTable);

            mAreDimensionsInvalid = false;
            if (scrollToHour >= 0)
                goToHour(scrollToHour);

            scrollToTable = -1;
            scrollToHour = -1;
            mAreDimensionsInvalid = false;
        }
        if (mIsFirstDraw) {
            mIsFirstDraw = false;

            // If the week view is being drawn for the first time, then consider the first day of the week.
           /* if (numberOfTables >= 7 && today.get(Calendar.DAY_OF_WEEK) != mFirstDayOfWeek && mShowFirstDayOfWeekFirst) {
                int difference = (today.get(Calendar.DAY_OF_WEEK) - mFirstDayOfWeek);
                mCurrentOrigin.x += (mWidthPerTable + mColumnGap) * difference;
            }*/
        }

        // Calculate the new height due to the zooming.
        if (mNewHourHeight > 0) {
            if (mNewHourHeight < mEffectiveMinHourHeight)
                mNewHourHeight = mEffectiveMinHourHeight;
            else if (mNewHourHeight > mMaxHourHeight)
                mNewHourHeight = mMaxHourHeight;

            mCurrentOrigin.y = (mCurrentOrigin.y / mHourHeight) * mNewHourHeight;
            mHourHeight = mNewHourHeight;
            mNewHourHeight = -1;
        }

        // If the new mCurrentOrigin.y is invalid, make it valid.
        if (mCurrentOrigin.y < getHeight() - mHourHeight * 24 - mHeaderHeight - mHeaderRowPadding * 2 - mHeaderMarginBottom - mTimeTextHeight / 2)
            mCurrentOrigin.y = getHeight() - mHourHeight * 24 - mHeaderHeight - mHeaderRowPadding * 2 - mHeaderMarginBottom - mTimeTextHeight / 2;

        // Don't put an "else if" because it will trigger a glitch when completely zoomed out and
        // scrolling vertically.
        if (mCurrentOrigin.y > 0) {
            mCurrentOrigin.y = 0;
        }

        // Consider scroll offset.
        int leftTablesWithGaps = (int) -(Math.ceil(mCurrentOrigin.x / (mWidthPerTable + mColumnGap)));
        float startFromPixel = mCurrentOrigin.x + (mWidthPerTable + mColumnGap) * leftTablesWithGaps +
                mHeaderColumnWidth;
        float startPixel = startFromPixel;

        // Prepare to iterate for each day.
        Calendar day = (Calendar) selectedDate.clone();
        day.add(Calendar.HOUR, 6);

        // Prepare to iterate for each hour to draw the hour lines.
        int lineCount = (int) ((getHeight() - mHeaderHeight - mHeaderRowPadding * 2 -
                mHeaderMarginBottom) / mHourHeight) + 1;
        lineCount = (lineCount) * (numberOfTables + 1);
        float[] hourLines = new float[lineCount * 4];

        // Clear the cache for event rectangles.
        if (mEventRects != null) {
            for (EventRect eventRect : mEventRects) {
                eventRect.rectF = null;
            }
        }

        // Clip to paint events only.
        canvas.clipRect(mHeaderColumnWidth, mHeaderHeight + mHeaderRowPadding * 2 + mHeaderMarginBottom + mTimeTextHeight / 2, getWidth(), getHeight(), Region.Op.REPLACE);

        // Iterate through each table.
        int oldFirstVisibleTable = mFirstVisibleTable;
        mFirstVisibleTable -=(Math.round(mCurrentOrigin.x / (mWidthPerTable + mColumnGap)));
        if (mFirstVisibleTable!=oldFirstVisibleTable && mScrollListener != null) {
            mScrollListener.onFirstVisibleTableChanged(mFirstVisibleTable, oldFirstVisibleTable);
        }
        for (int tableNumber = leftTablesWithGaps + 1;
             tableNumber <= leftTablesWithGaps + numberOfTables + 1;
             tableNumber++) {

            // Check if the day is today.
           /* day = (Calendar) selectedDate.clone();
            mLastVisibleTable = (Calendar) day.clone();
            day.add(Calendar.DATE, tableNumber - 1);
            mLastVisibleTable.add(Calendar.DATE, tableNumber - 2);
            boolean sameDay = isSameDay(day, today);*/

            mLastVisibleTable-=tableNumber - 2;
            boolean sameDay =false;// isSameDay(day, today);

            // Get more events if necessary. We want to store the events 3 months beforehand. Get
            // events only when it is the first iteration of the loop.
            if (mEventRects == null || mRefreshEvents ||
                    (tableNumber == leftTablesWithGaps + 1 && regionPeriod != (int) mRegionViewLoader.toRegionViewPeriodIndex(tableNumber) &&
                            Math.abs(regionPeriod - mRegionViewLoader.toRegionViewPeriodIndex(tableNumber)) > 0.5)) {
                getMoreEvents(selectedDate,tableNumber);
                mRefreshEvents = false;
            }

            // Draw background color for each table.
            float start = (startPixel < mHeaderColumnWidth ? mHeaderColumnWidth : startPixel);
            if (mWidthPerTable + startPixel - start > 0) {
               /* if (mShowDistinctPastFutureColor) {
                    boolean isWeekend = day.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || day.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
                    Paint pastPaint = isWeekend && mShowDistinctWeekendColor ? mPastWeekendBackgroundPaint : mPastBackgroundPaint;
                    Paint futurePaint = isWeekend && mShowDistinctWeekendColor ? mFutureWeekendBackgroundPaint : mFutureBackgroundPaint;
                    float startY = mHeaderHeight + mHeaderRowPadding * 2 + mTimeTextHeight / 2 + mHeaderMarginBottom + mCurrentOrigin.y;

                    if (sameDay) {
                        Calendar now = Calendar.getInstance();
                        float beforeNow = (now.get(Calendar.HOUR_OF_DAY) + now.get(Calendar.MINUTE) / 60.0f) * mHourHeight;
                        canvas.drawRect(start, startY, startPixel + mWidthPerTable, startY + beforeNow, pastPaint);
                        canvas.drawRect(start, startY + beforeNow, startPixel + mWidthPerTable, getHeight(), futurePaint);
                    } else if (day.before(today)) {
                        canvas.drawRect(start, startY, startPixel + mWidthPerTable, getHeight(), pastPaint);
                    } else {
                        canvas.drawRect(start, startY, startPixel + mWidthPerTable, getHeight(), futurePaint);
                    }
                } else */{
                    canvas.drawRect(start, mHeaderHeight + mHeaderRowPadding * 2 + mTimeTextHeight / 2 + mHeaderMarginBottom, startPixel + mWidthPerTable, getHeight(), sameDay ? mTodayBackgroundPaint : mTableBackgroundPaint);
                }
            }

            // Prepare the separator lines for hours.
            int i = 0;
            for (int hourNumber = 0; hourNumber < 24; hourNumber++) {
                float top = mHeaderHeight + mHeaderRowPadding * 2 + mCurrentOrigin.y + mHourHeight * hourNumber + mTimeTextHeight / 2 + mHeaderMarginBottom;
                if (top > mHeaderHeight + mHeaderRowPadding * 2 + mTimeTextHeight / 2 + mHeaderMarginBottom - mHourSeparatorHeight && top < getHeight() && startPixel + mWidthPerTable - start > 0) {
                    hourLines[i * 4] = start;
                    hourLines[i * 4 + 1] = top;
                    hourLines[i * 4 + 2] = startPixel + mWidthPerTable;
                    hourLines[i * 4 + 3] = top;
                    i++;
                }
            }

            // Draw the lines for hours.
            canvas.drawLines(hourLines, mHourSeparatorPaint);

            // Draw the events.
            drawEvents(tableNumber, startPixel, canvas);

            // Draw the line at the current time.
            if (/*mShowNowLine && */sameDay) {
                float startY = mHeaderHeight + mHeaderRowPadding * 2 + mTimeTextHeight / 2 + mHeaderMarginBottom + mCurrentOrigin.y;
                Calendar now = Calendar.getInstance();
                float beforeNow = (now.get(Calendar.HOUR_OF_DAY) + now.get(Calendar.MINUTE) / 60.0f) * mHourHeight;
                canvas.drawLine(start, startY + beforeNow, startPixel + mWidthPerTable, startY + beforeNow, mNowLinePaint);
            }

            // In the next iteration, start from the next day.
            startPixel += mWidthPerTable + mColumnGap;
        }

        // Hide everything in the first cell (top left corner).
        canvas.clipRect(0, 0, mTimeTextWidth + mHeaderColumnPadding * 2, mHeaderHeight + mHeaderRowPadding * 2, Region.Op.REPLACE);
        canvas.drawRect(0, 0, mTimeTextWidth + mHeaderColumnPadding * 2, mHeaderHeight + mHeaderRowPadding * 2, mHeaderBackgroundPaint);

        // Clip to paint header row only.
        canvas.clipRect(mHeaderColumnWidth, 0, getWidth(), mHeaderHeight + mHeaderRowPadding * 2, Region.Op.REPLACE);

        // Draw the header background.
        canvas.drawRect(0, 0, getWidth(), mHeaderHeight + mHeaderRowPadding * 2, mHeaderBackgroundPaint);

        // Draw the header row texts.
        startPixel = startFromPixel;
        for (int tableNumber = leftTablesWithGaps + 1; tableNumber <= leftTablesWithGaps + numberOfTables + 1; tableNumber++) {
            // Check if the day is today.
            /*day = (Calendar) today.clone();
            day.add(Calendar.DATE, tableNumber - 1);*/

            boolean sameDay = false;//isSameDay(day, today);

            // Draw the day labels.
            String dayLabel =String.valueOf(tableNumber);// getDateTimeInterpreter().interpretDate(day);
            if (dayLabel == null)
                throw new IllegalStateException("A DateTimeInterpreter must not return null date");
            canvas.drawText(dayLabel, startPixel + mWidthPerTable / 2, mHeaderTextHeight + mHeaderRowPadding, sameDay ? mTodayHeaderTextPaint : mHeaderTextPaint);
            drawAllDayEvents(day, startPixel, canvas);
            startPixel += mWidthPerTable + mColumnGap;
        }

    }


    /**
     * Gets more events of one/more month(s) if necessary. This method is called when the user is
     * scrolling the week view. The week view stores the events of three months: the visible month,
     * the previous month, the next month.
     *
     * @param day The day where the user is currently is.
     */
    private void getMoreEvents(Calendar day,int table) {

        // Get more events if the month is changed.
        if (mEventRects == null)
            mEventRects = new ArrayList<EventRect>();
        if (mRegionViewLoader == null && !isInEditMode())
            throw new IllegalStateException("You must provide a MonthChangeListener");

        // If a refresh was requested then reset some variables.
        if (mRefreshEvents) {
            mEventRects.clear();
            mPreviousPeriodEvents = null;
            mCurrentPeriodEvents = null;
            mNextPeriodEvents = null;
            regionPeriod = -1;
        }

        if (mRegionViewLoader != null) {
            int periodToFetch = (int) mRegionViewLoader.toRegionViewPeriodIndex(table);
            if (!isInEditMode() && (regionPeriod < 0 || regionPeriod != periodToFetch || mRefreshEvents)) {
                List<? extends Event> previousPeriodEvents = null;
                List<? extends Event> currentPeriodEvents = null;
                List<? extends Event> nextPeriodEvents = null;

                if (mPreviousPeriodEvents != null && mCurrentPeriodEvents != null && mNextPeriodEvents != null) {
                    if (periodToFetch == regionPeriod - 1) {
                        currentPeriodEvents = mPreviousPeriodEvents;
                        nextPeriodEvents = mCurrentPeriodEvents;
                    } else if (periodToFetch == regionPeriod) {
                        previousPeriodEvents = mPreviousPeriodEvents;
                        currentPeriodEvents = mCurrentPeriodEvents;
                        nextPeriodEvents = mNextPeriodEvents;
                    } else if (periodToFetch == regionPeriod + 1) {
                        previousPeriodEvents = mCurrentPeriodEvents;
                        currentPeriodEvents = mNextPeriodEvents;
                    }
                }
                if (currentPeriodEvents == null)
                    currentPeriodEvents = mRegionViewLoader.onLoad(periodToFetch);
                if (previousPeriodEvents == null)
                    previousPeriodEvents = mRegionViewLoader.onLoad(periodToFetch - 1);
                if (nextPeriodEvents == null)
                    nextPeriodEvents = mRegionViewLoader.onLoad(periodToFetch + 1);


                // Clear events.
                mEventRects.clear();
                sortAndCacheEvents(previousPeriodEvents);
                sortAndCacheEvents(currentPeriodEvents);
                sortAndCacheEvents(nextPeriodEvents);
                calculateHeaderHeight();

                mPreviousPeriodEvents = previousPeriodEvents;
                mCurrentPeriodEvents = currentPeriodEvents;
                mNextPeriodEvents = nextPeriodEvents;
                regionPeriod = periodToFetch;
            }
        }

        // Prepare to calculate positions of each events.
        List<EventRect> tempEvents = mEventRects;
        mEventRects = new ArrayList<>();

        // Iterate through each day with events to calculate the position of the events.
        while (tempEvents.size() > 0) {
            ArrayList<EventRect> eventRects = new ArrayList<>(tempEvents.size());

            // Get first event for a day.
            EventRect eventRect1 = tempEvents.remove(0);
            eventRects.add(eventRect1);

            int i = 0;
            while (i < tempEvents.size()) {
                // Collect all other events for same day.
                EventRect eventRect2 = tempEvents.get(i);
                if (isSameDay(eventRect1.event.getStartTime(), eventRect2.event.getStartTime())) {
                    tempEvents.remove(i);
                    eventRects.add(eventRect2);
                } else {
                    i++;
                }
            }
            computePositionOfEvents(eventRects);
        }
    }

    /**
     * Sort and cache events.
     *
     * @param events The events to be sorted and cached.
     */
    private void sortAndCacheEvents(List<? extends Event> events) {
        sortEvents(events);
        for (Event event : events) {
            cacheEvent(event);
        }
    }

    /**
     * Sorts the events in ascending order.
     *
     * @param events The events to be sorted.
     */
    private void sortEvents(List<? extends Event> events) {
        Collections.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event event1, Event event2) {
                long start1 = event1.getStartTime();
                long start2 = event2.getStartTime();
                int comparator = start1 > start2 ? 1 : (start1 < start2 ? -1 : 0);
                if (comparator == 0) {
                    long end1 = event1.getEndTime();
                    long end2 = event2.getEndTime();
                    comparator = end1 > end2 ? 1 : (end1 < end2 ? -1 : 0);
                }
                return comparator;
            }
        });
    }

    /**
     * Cache the event for smooth scrolling functionality.
     *
     * @param event The event to cache.
     */
    private void cacheEvent(Event event) {
        if (event.getStartTime()-event.getEndTime() >= 0)
            return;
        /*List<Event> splitedEvents = event.splitWeekViewEvents();
        for (Event splitedEvent : splitedEvents) {
            mEventRects.add(new EventRect(splitedEvent, event, null));
        }*/
    }

    /**
     * Calculates the left and right positions of each events. This comes handy specially if events
     * are overlapping.
     *
     * @param eventRects The events along with their wrapper class.
     */
    private void computePositionOfEvents(List<EventRect> eventRects) {
        // Make "collision groups" for all events that collide with others.
        List<List<EventRect>> collisionGroups = new ArrayList<List<EventRect>>();
        for (EventRect eventRect : eventRects) {
            boolean isPlaced = false;

            outerLoop:
            for (List<EventRect> collisionGroup : collisionGroups) {
                for (EventRect groupEvent : collisionGroup) {
                    if (isEventsCollide(groupEvent.event, eventRect.event) /*&& groupEvent.event.isAllDay() == eventRect.event.isAllDay()*/) {
                        collisionGroup.add(eventRect);
                        isPlaced = true;
                        break outerLoop;
                    }
                }
            }

            if (!isPlaced) {
                List<EventRect> newGroup = new ArrayList<>();
                newGroup.add(eventRect);
                collisionGroups.add(newGroup);
            }
        }

        for (List<EventRect> collisionGroup : collisionGroups) {
            expandEventsToMaxWidth(collisionGroup);
        }
    }

    /**
     * Expands all the events to maximum possible width. The events will try to occupy maximum
     * space available horizontally.
     *
     * @param collisionGroup The group of events which overlap with each other.
     */
    private void expandEventsToMaxWidth(List<EventRect> collisionGroup) {
        // Expand the events to maximum possible width.
        List<List<EventRect>> columns = new ArrayList<List<EventRect>>();
        columns.add(new ArrayList<EventRect>());
        for (EventRect eventRect : collisionGroup) {
            boolean isPlaced = false;
            for (List<EventRect> column : columns) {
                if (column.size() == 0) {
                    column.add(eventRect);
                    isPlaced = true;
                } else if (!isEventsCollide(eventRect.event, column.get(column.size() - 1).event)) {
                    column.add(eventRect);
                    isPlaced = true;
                    break;
                }
            }
            if (!isPlaced) {
                List<EventRect> newColumn = new ArrayList<EventRect>();
                newColumn.add(eventRect);
                columns.add(newColumn);
            }
        }


        // Calculate left and right position for all the events.
        // Get the maxRowCount by looking in all columns.
        int maxRowCount = 0;
        for (List<EventRect> column : columns) {
            maxRowCount = Math.max(maxRowCount, column.size());
        }
        for (int i = 0; i < maxRowCount; i++) {
            // Set the left and right values of the event.
            float j = 0;
            for (List<EventRect> column : columns) {
                if (column.size() >= i + 1) {
                    EventRect eventRect = column.get(i);
                    eventRect.width = 1f / columns.size();
                    eventRect.left = j / columns.size();
                    if (true/*!eventRect.event.isAllDay()*/) {
                        Calendar cStart=Calendar.getInstance();
                        cStart.setTimeInMillis(eventRect.event.getStartTime());
                        Calendar cEnd=Calendar.getInstance();
                        cEnd.setTimeInMillis(eventRect.event.getEndTime());
                        eventRect.top = cStart.get(Calendar.HOUR_OF_DAY) * 60 + cStart.get(Calendar.MINUTE);
                        eventRect.bottom = cEnd.get(Calendar.HOUR_OF_DAY) * 60 + cEnd.get(Calendar.MINUTE);
                    }/* else {
                        eventRect.top = 0;
                        eventRect.bottom = mAllDayEventHeight;
                    }*/
                    mEventRects.add(eventRect);
                }
                j++;
            }
        }
    }


    /**
     * Checks if two events overlap.
     *
     * @param event1 The first event.
     * @param event2 The second event.
     * @return true if the events overlap.
     */
    private boolean isEventsCollide(Event event1, Event event2) {
        long start1 = event1.getStartTime();
        long end1 = event1.getEndTime();
        long start2 = event2.getStartTime();
        long end2 = event2.getEndTime();
        return !((start1 >= end2) || (end1 <= start2));
    }


    /**
     * Vertically scroll to a specific hour in the week view.
     *
     * @param hour The hour to scroll to in 24-hour format. Supported values are 0-24.
     */
    public void goToHour(double hour) {
        if (mAreDimensionsInvalid) {
            scrollToHour = hour;
            return;
        }

        int verticalOffset = 0;
        if (hour > 24)
            verticalOffset = mHourHeight * 24;
        else if (hour > 0)
            verticalOffset = (int) (mHourHeight * hour);

        if (verticalOffset > mHourHeight * 24 - getHeight() + mHeaderHeight + mHeaderRowPadding * 2 + mHeaderMarginBottom)
            verticalOffset = (int) (mHourHeight * 24 - getHeight() + mHeaderHeight + mHeaderRowPadding * 2 + mHeaderMarginBottom);

        mCurrentOrigin.y = -verticalOffset;
        invalidate();
    }


    /**
     * Show a specific table on the date view.
     *
     * @param table The table to show.
     */
    public void goToTable(int table) {
        mScroller.forceFinished(true);
        mCurrentScrollDirection = mCurrentFlingDirection = Direction.NONE;
        if (mAreDimensionsInvalid) {
            scrollToTable = table;
            return;
        }
        mRefreshEvents = true;
        long tableDifference = table - scrollToTable;
        mCurrentOrigin.x = -tableDifference * (mWidthPerTable + mColumnGap);
        invalidate();
    }

    private void drawTimeColumnAndAxes(Canvas canvas) {
        // Draw the background color for the header column.
        canvas.drawRect(0, mHeaderHeight + mHeaderRowPadding * 2, mHeaderColumnWidth, getHeight(), mHeaderColumnBackgroundPaint);

        // Clip to paint in left column only.
        canvas.clipRect(0, mHeaderHeight + mHeaderRowPadding * 2, mHeaderColumnWidth, getHeight(), Region.Op.REPLACE);

        for (int i = 0; i < 24; i++) {
            float top = mHeaderHeight + mHeaderRowPadding * 2 + mCurrentOrigin.y + mHourHeight * i + mHeaderMarginBottom;

            // Draw the text if its y position is not outside of the visible area. The pivot point of the text is the point at the bottom-right corner.
            String time = Utilities.interpretTime(getContext(),i);
            if (time == null)
                throw new IllegalStateException("A DateTimeInterpreter must not return null time");
            if (top < getHeight())
                canvas.drawText(time, mTimeTextWidth + mHeaderColumnPadding, top + mTimeTextHeight, mTimeTextPaint);
        }
    }


    public static class TableAppointment {
        private int table;
        private Calendar calendar;

        public TableAppointment(int table, Calendar calendar) {
            this.table = table;
            this.calendar = calendar;
        }

        public int getTable() {
            return table;
        }

        public void setTable(int table) {
            this.table = table;
        }

        public Calendar getCalendar() {
            return calendar;
        }

        public void setCalendar(Calendar calendar) {
            this.calendar = calendar;
        }
    }


    /**
     * Draw all the events of a particular table.
     *
     * @param table           The table.
     * @param startFromPixel The left position of the day area. The events will never go any left from this value.
     * @param canvas         The canvas to draw upon.
     */
    private void drawEvents(int table, float startFromPixel, Canvas canvas) {
        if (mEventRects != null && mEventRects.size() > 0) {
            for (int i = 0; i < mEventRects.size(); i++) {
                if (mEventRects.get(i).event.getTbl()==table/* && !mEventRects.get(i).event.isAllDay()*/) {

                    // Calculate top.
                    float top = mHourHeight * 24 * mEventRects.get(i).top / 1440 + mCurrentOrigin.y + mHeaderHeight + mHeaderRowPadding * 2 + mHeaderMarginBottom + mTimeTextHeight / 2 + mEventMarginVertical;

                    // Calculate bottom.
                    float bottom = mEventRects.get(i).bottom;
                    bottom = mHourHeight * 24 * bottom / 1440 + mCurrentOrigin.y + mHeaderHeight + mHeaderRowPadding * 2 + mHeaderMarginBottom + mTimeTextHeight / 2 - mEventMarginVertical;

                    // Calculate left and right.
                    float left = startFromPixel + mEventRects.get(i).left * mWidthPerTable;
                    if (left < startFromPixel)
                        left += mOverlappingEventGap;
                    float right = left + mEventRects.get(i).width * mWidthPerTable;
                    if (right < startFromPixel + mWidthPerTable)
                        right -= mOverlappingEventGap;

                    // Draw the event and the event name on top of it.
                    if (left < right &&
                            left < getWidth() &&
                            top < getHeight() &&
                            right > mHeaderColumnWidth &&
                            bottom > mHeaderHeight + mHeaderRowPadding * 2 + mTimeTextHeight / 2 + mHeaderMarginBottom
                            ) {
                        mEventRects.get(i).rectF = new RectF(left, top, right, bottom);
                        mEventBackgroundPaint.setColor(mEventRects.get(i).event.getColor() == 0 ? mDefaultEventColor : mEventRects.get(i).event.getColor());
                        canvas.drawRoundRect(mEventRects.get(i).rectF, mEventCornerRadius, mEventCornerRadius, mEventBackgroundPaint);
                        drawEventTitle(mEventRects.get(i).event, mEventRects.get(i).rectF, canvas, top, left);
                    } else
                        mEventRects.get(i).rectF = null;
                }
            }
        }
    }


    /**
     * Draw all the Allday-events of a particular day.
     *
     * @param date           The day.
     * @param startFromPixel The left position of the day area. The events will never go any left from this value.
     * @param canvas         The canvas to draw upon.
     */
    private void drawAllDayEvents(Calendar date, float startFromPixel, Canvas canvas) {
        if (mEventRects != null && mEventRects.size() > 0) {
            for (int i = 0; i < mEventRects.size(); i++) {
                if (isSameDay(mEventRects.get(i).event.getStartTime(), date.getTimeInMillis()) /*&& mEventRects.get(i).event.isAllDay()*/) {

                    // Calculate top.
                    float top = mHeaderRowPadding * 2 + mHeaderMarginBottom + +mTimeTextHeight / 2 + mEventMarginVertical;

                    // Calculate bottom.
                    float bottom = top + mEventRects.get(i).bottom;

                    // Calculate left and right.
                    float left = startFromPixel + mEventRects.get(i).left * mWidthPerTable;
                    if (left < startFromPixel)
                        left += mOverlappingEventGap;
                    float right = left + mEventRects.get(i).width * mWidthPerTable;
                    if (right < startFromPixel + mWidthPerTable)
                        right -= mOverlappingEventGap;

                    // Draw the event and the event name on top of it.
                    if (left < right &&
                            left < getWidth() &&
                            top < getHeight() &&
                            right > mHeaderColumnWidth &&
                            bottom > 0
                            ) {
                        mEventRects.get(i).rectF = new RectF(left, top, right, bottom);
                        mEventBackgroundPaint.setColor(mEventRects.get(i).event.getColor() == 0 ? mDefaultEventColor : mEventRects.get(i).event.getColor());
                        canvas.drawRoundRect(mEventRects.get(i).rectF, mEventCornerRadius, mEventCornerRadius, mEventBackgroundPaint);
                        drawEventTitle(mEventRects.get(i).event, mEventRects.get(i).rectF, canvas, top, left);
                    } else
                        mEventRects.get(i).rectF = null;
                }
            }
        }
    }


    /**
     * Draw the name of the event on top of the event rectangle.
     *
     * @param event        The event of which the title (and location) should be drawn.
     * @param rect         The rectangle on which the text is to be drawn.
     * @param canvas       The canvas to draw upon.
     * @param originalTop  The original top position of the rectangle. The rectangle may have some of its portion outside of the visible area.
     * @param originalLeft The original left position of the rectangle. The rectangle may have some of its portion outside of the visible area.
     */
    private void drawEventTitle(Event event, RectF rect, Canvas canvas, float originalTop, float originalLeft) {
        if (rect.right - rect.left - mEventPadding * 2 < 0) return;
        if (rect.bottom - rect.top - mEventPadding * 2 < 0) return;

        // Prepare the name of the event.
        SpannableStringBuilder bob = new SpannableStringBuilder();
        if (event.getClient() != null) {
            bob.append(event.getClient().getName());
            bob.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, bob.length(), 0);
            bob.append(' ');
        }

        // Prepare the location of the event.
       /* if (event.getLocation() != null) {
            bob.append(event.getLocation());
        }*/

        int availableHeight = (int) (rect.bottom - originalTop - mEventPadding * 2);
        int availableWidth = (int) (rect.right - originalLeft - mEventPadding * 2);

        // Get text dimensions.
        StaticLayout textLayout = new StaticLayout(bob, mEventTextPaint, availableWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        int lineHeight = textLayout.getHeight() / textLayout.getLineCount();

        if (availableHeight >= lineHeight) {
            // Calculate available number of line counts.
            int availableLineCount = availableHeight / lineHeight;
            do {
                // Ellipsize text to fit into event rect.
                textLayout = new StaticLayout(TextUtils.ellipsize(bob, mEventTextPaint, availableLineCount * availableWidth, TextUtils.TruncateAt.END), mEventTextPaint, (int) (rect.right - originalLeft - mEventPadding * 2), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

                // Reduce line count.
                availableLineCount--;

                // Repeat until text is short enough.
            } while (textLayout.getHeight() > availableHeight);

            // Draw text.
            canvas.save();
            canvas.translate(originalLeft + mEventPadding, originalTop + mEventPadding);
            textLayout.draw(canvas);
            canvas.restore();
        }
    }




    /**
     * A class to hold reference to the events and their visual representation. An EventRect is
     * actually the rectangle that is drawn on the calendar for a given event. There may be more
     * than one rectangle for a single event (an event that expands more than one day). In that
     * case two instances of the EventRect will be used for a single event. The given event will be
     * stored in "originalEvent". But the event that corresponds to rectangle the rectangle
     * instance will be stored in "event".
     */
    private class EventRect {
        public Event event;
        public Event originalEvent;
        public RectF rectF;
        public float left;
        public float width;
        public float top;
        public float bottom;

        /**
         * Create a new instance of event rect. An EventRect is actually the rectangle that is drawn
         * on the calendar for a given event. There may be more than one rectangle for a single
         * event (an event that expands more than one day). In that case two instances of the
         * EventRect will be used for a single event. The given event will be stored in
         * "originalEvent". But the event that corresponds to rectangle the rectangle instance will
         * be stored in "event".
         *
         * @param event         Represents the event which this instance of rectangle represents.
         * @param originalEvent The original event that was passed by the user.
         * @param rectF         The rectangle.
         */
        public EventRect(Event event, Event originalEvent, RectF rectF) {
            this.event = event;
            this.rectF = rectF;
            this.originalEvent = originalEvent;
        }
    }


    /////////////////////////////////////////////////////////////////
    //
    //      Interfaces.
    //
    /////////////////////////////////////////////////////////////////

    public interface EventClickListener {
        /**
         * Triggered when clicked on one existing event
         *
         * @param event:     event clicked.
         * @param eventRect: view containing the clicked event.
         */
        void onEventClick(Event event, RectF eventRect);
    }

    public interface EventLongPressListener {
        /**
         * Similar to {@link DayTableView.EventClickListener} but with a long press.
         *
         * @param event:     event clicked.
         * @param eventRect: view containing the clicked event.
         */
        void onEventLongPress(Event event, RectF eventRect);
    }

    public interface EmptyViewClickListener {
        /**
         * Triggered when the users clicks on a empty space of the calendar.
         *
         * @param appointment: {@link TableAppointment} object set with the table, the date and time of the clicked position on the view.
         */
        void onEmptyViewClicked(TableAppointment appointment);
    }

    public interface EmptyViewLongPressListener {
        /**
         * Similar to {@link DayTableView.EmptyViewClickListener} but with long press.
         *
         * @param appointment: {@link TableAppointment} object set with the table, the date and time of the clicked position on the view.
         */
        void onEmptyViewLongPress(TableAppointment appointment);
    }

    public interface ScrollListener {
        /**
         * Called when the first visible table has changed.
         * <p>
         * (this will also be called during the first draw of the weekview)
         *
         * @param newFirstVisibleTable The new first visible table
         * @param oldFirstVisibleTable The old first visible table (is null on the first call).
         */
        void onFirstVisibleTableChanged(int newFirstVisibleTable, int oldFirstVisibleTable);
    }

    public interface TableViewLoader {
        /**
         * Convert a table into a int that will be used to reference when you're loading data.
         *
         * All periods that have the same integer part, define one period. Dates that are later in time
         * should have a greater return value.
         *
         * @param tbl the table
         * @return The period index in which the table falls (int point number).
         */
        int toRegionViewPeriodIndex(int tbl);

        /**
         * Load the events within the period
         * @param periodIndex the period to load
         * @return A list with the events of this period
         */
        List<? extends Event> onLoad(int periodIndex);
    }

}
