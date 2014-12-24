package com.zhoujg77.guaguaka;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by 建刚 on 2014/12/23.
 */

 public class Guaka extends View
 {
 	private Paint mPaint;
 	private Path mPath;
 	private Canvas mCanvas;
 	private Bitmap mBitmap;

 	private int mLastX;
 	private int mLastY;
     private Bitmap mOutterBitmap;
     //底层bitmap
   //
     private String mText;
     private Paint mBackPaint;
     //记录文本de矩形
     private Rect mTextBound;
     private int mTextSize;
     private int mTextColor;


    //volatile 子线程对值更新是主线程知道
     private volatile boolean mComplete = false;

     /**
      * 刮完时回掉
      */
     public interface OnGuaKaCompleteListener {
        void complete();
     }


     private OnGuaKaCompleteListener mLister ;

     public void setOnGuaKaCompleteListener(OnGuaKaCompleteListener mLister) {
         this.mLister = mLister;
     }

     public Guaka(Context context)
 	{
 		this(context, null);
 	}

 	public Guaka(Context context, AttributeSet attrs)
 	{
 		this(context, attrs, 0);
 	}

 	public Guaka(Context context, AttributeSet attrs, int defStyle)
 	{
 		super(context, attrs, defStyle);
        init();

        TypedArray a = null;
        try{
            a = context.getTheme().obtainStyledAttributes(attrs,R.styleable.Guaka,defStyle,0);

            int n = a.getIndexCount();
            for (int i = 0; i < n; i++){
                int attr = a.getIndex(i);
                switch (attr){
                    case R.styleable.Guaka_text:
                        mText = a.getString(attr);
                        break;
                    case R.styleable.Guaka_textSize:
                        mTextSize = (int) a.getDimension(attr, TypedValue
                                .applyDimension(TypedValue.COMPLEX_UNIT_SP, 22,
                                        getResources().getDisplayMetrics()));
                        break;
                    case R.styleable.Guaka_textColor:
                        mTextColor = a.getColor(attr, 0x000000);
                        break;

                }
            }
        } finally
        {
            if (a != null)
                a.recycle();
        }
    }

     public void setText(String mText) {
         this.mText = mText;
         //获得画笔绘制文本的宽和高 的矩形
         mBackPaint.getTextBounds(mText,0,mText.length(),mTextBound);
     }

     @Override
 	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
 	{
 		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

 		int width = getMeasuredWidth();
 		int height = getMeasuredHeight();
 		// 初始化我们的bitmap
 		mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
 		mCanvas = new Canvas(mBitmap);

 		// 设置绘制path画笔的一些属性
 		setPaint();
        setUpBackPaint();
       // mCanvas.drawColor(Color.parseColor("#c0c0c0"));
        mCanvas.drawRoundRect(new RectF(0,0,width,height),30,30,mPaint);

        mCanvas.drawBitmap(mOutterBitmap,null,new Rect(0,0,width,height),null);

 	}
        //设置绘制获奖信息的画笔颜色
     private void setUpBackPaint() {
            mBackPaint.setColor(mTextColor);
            mBackPaint.setStyle(Paint.Style.FILL);
           mBackPaint.setTextSize(mTextSize);
            //获得画笔绘制文本的宽和高 的矩形
           mBackPaint.getTextBounds(mText,0,mText.length(),mTextBound);



     }

     /**
 	 * 设置我们绘制获奖信息的画笔属性
 	 */


 	/**
 	 * 设置绘制path画笔的一些属性
 	 */
 	private void setPaint()
 	{
 		mPaint.setColor(Color.parseColor("#c0c0c0"));
 		mPaint.setAntiAlias(true);
 		mPaint.setDither(true);
 		mPaint.setStrokeJoin(Paint.Join.ROUND);
 		mPaint.setStrokeCap(Paint.Cap.ROUND);
 		mPaint.setStyle(Paint.Style.FILL);
 		mPaint.setStrokeWidth(20);
 	}

 	@Override
 	public boolean onTouchEvent(MotionEvent event)
 	{
 		int action = event.getAction();

 		int x = (int) event.getX();
 		int y = (int) event.getY();

 		switch (action)
 		{
 		case MotionEvent.ACTION_DOWN:

 			mLastX = x;
 			mLastY = y;
 			mPath.moveTo(mLastX, mLastY);
 			break;
 		case MotionEvent.ACTION_MOVE:

 			int dx = Math.abs(x - mLastX);
 			int dy = Math.abs(y - mLastY);

 			if (dx > 3 || dy > 3)
 			{
 				mPath.lineTo(x, y);
 			}

 			mLastX = x;
 			mLastY = y;

 			break;
 		case MotionEvent.ACTION_UP:
                new Thread(mRunable).start();
 			break;
 		}

 			invalidate();
 		return true;

 	}


     private Runnable mRunable = new Runnable() {
         @Override
         public void run() {
            int w = getWidth();
            int h  = getHeight();

             float wipeArea = 0;
             float totalArea = w*h;

             Bitmap bitmap = mBitmap;
             int [] mPixels = new int[w*h];
             //获得bitmap上所有的像素信息
             bitmap.getPixels(mPixels,0,w,0,0,w,h);

             for (int i = 0; i <w ; i++) {
                 for (int j = 0; j <h ; j++) {
                    int index = i+j*w;
                     if (mPixels[index]==0){
                         wipeArea++;
                     }

                 }
             }

             if (wipeArea>0&&totalArea>0) {
                 int percent = (int) (wipeArea*100/totalArea);
                    Log.i("---Tag","--"+percent);
                 if (percent>60){
                     //清除图层区域
                     mComplete  = true;
                     postInvalidate();

                 }
             }


         }
     };



 	@Override
 	protected void onDraw(Canvas canvas)
 	{

        //canvas.drawBitmap(bitmap,0,0,null);
            canvas.drawText(mText,getWidth()/2-mTextBound.width()/2,
                    getHeight()/2+mTextBound.height()/2,mBackPaint);
        if (mComplete){
            if (mLister != null){
                mLister.complete();
            }
        }


        if (!mComplete) {
            drawPath();
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
	}

	private void drawPath()
	{
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
		mCanvas.drawPath(mPath, mPaint);
	}

	/**
	 * 进行一些初始化操作
	 */
	private void init()
	{
		mPaint = new Paint();
		mPath = new Path();
      //  bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.t2);
        mOutterBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.fg_guaguaka);

        mText = "5千万";
        mTextBound = new Rect();
        mBackPaint = new Paint();
        mTextSize= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,22,
                getResources().getDisplayMetrics());

	}

}
