package com.example.myapplication;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FishDrawable extends Drawable {
    private Paint mPaint;
    private Path mPath;

    //身体之外的部分的透明度
    private final static int OTHER_ALPHA = 110;
    //身体的透明度
    private final static int BODY_ALPHA = 160;

    private final static float HEAD_RADIUS = 50;
    //身体的长度
    private final static float BODY_LENGTH = 3.2f * HEAD_RADIUS;

    private final static float FIND_FINS_LENGTH = 0.9f * HEAD_RADIUS;
    private final static float FINS_LENGTH = 1.3f * HEAD_RADIUS;


    private final static float BIG_CIRCLE_RADIUS = 0.7f * HEAD_RADIUS;
    private final static float MIDDLE_CIRCLE_RADIUS = 0.6f * BIG_CIRCLE_RADIUS;
    private final static float SMALL_CIRCLE_RADIUS = 0.4f * MIDDLE_CIRCLE_RADIUS;
    private final static float FIND_MIDDLE_CIRCLE_LENGTH = BIG_CIRCLE_RADIUS + MIDDLE_CIRCLE_RADIUS;
    private final static float FIND_SMALL_CIRCLE_LENGTH = (0.4F + 2.7F) * MIDDLE_CIRCLE_RADIUS;
    private final static float FIND_TRIANGLE_LENGTH = 2.7f * MIDDLE_CIRCLE_RADIUS;


    //鱼的重心（鱼身的中心点）
    private PointF middlePoint;
    private float fishMainAngle =90;
    private float currentValue = 0;


    public FishDrawable() {
        init();
    }

    private void init() {
        mPath = new Path();//路径
        mPaint = new Paint();//画笔
        mPaint.setStyle(Paint.Style.FILL);//画笔类型 ，填充
        mPaint.setARGB(OTHER_ALPHA,244,92,71);
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setDither(true);//防抖
        middlePoint = new PointF(4.19F* HEAD_RADIUS,4.19F*HEAD_RADIUS);
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1200f);
        valueAnimator.setDuration(5*1000);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentValue = (float) animation.getAnimatedValue();
                invalidateSelf();
            }
        });
        valueAnimator.start();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        float fishAngle = (float) (fishMainAngle + Math.sin(Math.toRadians(currentValue* 1.2)) * 4);


        //绘制鱼头
        PointF headPoint = calculatePoint(middlePoint,BODY_LENGTH/2 ,fishAngle);
        canvas.drawCircle(headPoint.x ,headPoint.y,HEAD_RADIUS,mPaint);

        //鱼右鳍
        PointF rightFinsPoint = calculatePoint(headPoint,FIND_FINS_LENGTH, fishAngle -110);
        makeFins(canvas,rightFinsPoint,fishAngle,true);

        //鱼左鳍
        PointF leftFinsPoint = calculatePoint(headPoint,FIND_FINS_LENGTH, fishAngle +110);
        makeFins(canvas,leftFinsPoint,fishAngle,false);

        //身体的底部的中心点
        PointF bodyBottomCenterPoint = calculatePoint(headPoint , BODY_LENGTH, fishAngle - 180);
        //节肢1
        PointF middleCircleCenterPoint= makeSegment(canvas,bodyBottomCenterPoint,BIG_CIRCLE_RADIUS,MIDDLE_CIRCLE_RADIUS ,FIND_MIDDLE_CIRCLE_LENGTH,
                fishAngle,true);

//        PointF middleCircleCenterPoint = calculatePoint(bodyBottomCenterPoint , FIND_MIDDLE_CIRCLE_LENGTH, fishAngle - 180);
        //节肢2
        makeSegment(canvas,middleCircleCenterPoint,MIDDLE_CIRCLE_RADIUS,SMALL_CIRCLE_RADIUS ,FIND_SMALL_CIRCLE_LENGTH,
                fishAngle,false);
        float findEdgeLength = (float) Math.abs(Math.sin(Math.toRadians(currentValue*1.5))* BIG_CIRCLE_RADIUS);
        //绘制大三角形
        makeTriangle(canvas ,middleCircleCenterPoint,FIND_TRIANGLE_LENGTH,findEdgeLength,fishAngle);
        //绘制小三角形
        makeTriangle(canvas ,middleCircleCenterPoint,FIND_TRIANGLE_LENGTH -10,findEdgeLength -20,fishAngle);

        //画身体
        makeBody(canvas ,headPoint,bodyBottomCenterPoint,fishAngle);
    }

    private void makeBody(Canvas canvas , PointF headPoint, PointF bodyBottomCenterPoint ,float fishAngle){
        // 身体的四个点
        PointF topLeftPoint = calculatePoint(headPoint , HEAD_RADIUS ,fishAngle +90);
        PointF topRightPoint = calculatePoint(headPoint , HEAD_RADIUS ,fishAngle -90);
        PointF bottomLeftPoint = calculatePoint(bodyBottomCenterPoint , BIG_CIRCLE_RADIUS ,fishAngle +90);
        PointF bottomRightPoint = calculatePoint(bodyBottomCenterPoint , BIG_CIRCLE_RADIUS,fishAngle- 90);

        PointF controlLeft = calculatePoint(headPoint , BODY_LENGTH * 0.56f, fishAngle+130);
        PointF controlRight = calculatePoint(headPoint , BODY_LENGTH * 0.56f, fishAngle-130);

        mPath.reset();
        mPath.moveTo(topLeftPoint.x, topLeftPoint.y);
        mPath.quadTo(controlLeft.x,controlLeft.y,bottomLeftPoint.x,bottomLeftPoint.y);
        mPath.lineTo(bottomRightPoint.x, bottomRightPoint.y);
        mPath.quadTo(controlRight.x, controlRight.y , topRightPoint.x ,topRightPoint.y);
        mPaint.setAlpha(BODY_ALPHA);
        canvas.drawPath(mPath,mPaint);
    }

    /**
     *  //画三角形
     * @param startPoint
     * @param findCenterLength 顶点到底部的垂直线长
     * @param findEdgeLength 底部一半
     */
    private void makeTriangle(Canvas canvas,PointF startPoint,float findCenterLength, float findEdgeLength,float fishAngle){

        //三角形鱼尾的摆动角度需要跟着节肢2走
        float triangleAngle = (float) (fishAngle + Math.sin(Math.toRadians(currentValue * 1.5))*35);
        //底部中心点的坐标
        PointF centerPoint = calculatePoint(startPoint , findCenterLength , triangleAngle -180);

        //三角形底部两个点
        PointF leftPoint = calculatePoint(centerPoint , findEdgeLength , triangleAngle +90);
        PointF rightPoint = calculatePoint(centerPoint , findEdgeLength , triangleAngle -90);

        mPath.reset();
        mPath.moveTo(startPoint.x, startPoint.y);
        mPath.lineTo(leftPoint.x, leftPoint.y);
        mPath.lineTo(rightPoint.x, rightPoint.y);
        canvas.drawPath(mPath,mPaint);
    }


    /**
     *
     * @param bottomCenterPint 梯形底部的中心点坐标(长边)
     * @param bigRadius 大圆的半径
     * @param smallRadius 小圆的半径
     * @param findSmallCircleLength 寻找梯形小圆的线长
     * @param hasBigCircle 是否有大圆
     */
    private PointF makeSegment(Canvas canvas, PointF bottomCenterPint, float bigRadius,
                             float smallRadius, float findSmallCircleLength,float fishAngle,
                             boolean hasBigCircle){
        //节肢摆动的角度

        float segmentAngle;
        if (hasBigCircle){
            segmentAngle = (float) (fishAngle + Math.cos(Math.toRadians(currentValue * 1.5))*15);
        }else{
            segmentAngle = (float) (fishAngle + Math.sin(Math.toRadians(currentValue * 1.5))*35);
        }


        //梯形上底的中心点（短边）
        PointF upperCenterPoint = calculatePoint(bottomCenterPint, findSmallCircleLength ,segmentAngle - 180);

        //梯形的四个顶点
        PointF bottomLeftPoint = calculatePoint(bottomCenterPint, bigRadius ,segmentAngle + 90);
        PointF bottomRightPoint = calculatePoint(bottomCenterPint, bigRadius ,segmentAngle - 90);
        PointF upperLeftPoint = calculatePoint(upperCenterPoint, smallRadius ,segmentAngle + 90);
        PointF upperRightPoint = calculatePoint(upperCenterPoint, smallRadius ,segmentAngle - 90);

        if (hasBigCircle){
            //绘制大圆
            canvas.drawCircle(bottomCenterPint.x ,bottomCenterPint.y ,bigRadius ,mPaint);
        }

        //绘制小圆
        canvas.drawCircle(upperCenterPoint.x ,upperCenterPoint.y ,smallRadius ,mPaint);

        //绘制梯形
        mPath.reset();
        mPath.moveTo(bottomLeftPoint.x,bottomLeftPoint.y);
        mPath.lineTo(upperLeftPoint.x,upperLeftPoint.y);
        mPath.lineTo(upperRightPoint.x,upperRightPoint.y);
        mPath.lineTo(bottomRightPoint.x,bottomRightPoint.y);
        canvas.drawPath(mPath,mPaint);

        return upperCenterPoint;
    }

    /**
     * 绘制鱼鳍
     * @param startPoint 起始点的坐标
     * @param fishAngle 鱼头相对于x坐标的角度
     * @param isRightFins
     */
    private void makeFins(Canvas canvas ,PointF startPoint, float fishAngle,boolean isRightFins){

        float controlAngle = 115;
        //结束点
        PointF endPoint = calculatePoint(startPoint , FINS_LENGTH  , fishAngle -180);
        //控制点
        PointF controlPoint = calculatePoint(startPoint , 1.8f * FINS_LENGTH,
                isRightFins?fishAngle - controlAngle :fishAngle + controlAngle);

        mPath.reset();
        mPath.moveTo(startPoint.x, startPoint.y);
        //二阶贝塞尔曲线
        mPath.quadTo(controlPoint.x,controlPoint.y ,endPoint.x, endPoint.y);
        canvas.drawPath(mPath,mPaint);
    }

    /**
     * 求对应点的坐标
     * @param startPoint 起始点的坐标
     * @param length 两点间的长度
     * @param angle 鱼头相对于x坐标的角度
     * @return
     */
    public static PointF calculatePoint(PointF startPoint, float length ,float angle){
        float deltaX = (float) (Math.cos(Math.toRadians(angle)) * length);
        float deltaY = (float) (Math.sin(Math.toRadians(angle-180 )) * length);
        return new PointF(startPoint.x + deltaX , startPoint.y +deltaY);
    }

    //设置透明度
    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    //设置颜色过滤器
    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }

    //如果ImageView的宽高是wrap_content ,则获取固定宽高
    @Override
    public int getIntrinsicHeight() {
        return (int)(8.38f * HEAD_RADIUS);
    }

    @Override
    public int getIntrinsicWidth() {
        return (int)(8.38f * HEAD_RADIUS);
    }
}
