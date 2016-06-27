package com.bqt.myview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.SweepGradient;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

public class MyShaderView extends View {
	// Shader渲染器，专门用来渲染图像以及一些几何图形
	private BitmapShader bitmapShader; //bitmap渲染器，主要用来渲染图像
	private LinearGradient linearGradient; //线性渲染
	private RadialGradient radialGradient; //环形渲染
	private SweepGradient sweepGradient; //扫描渐变，围绕一个中心点扫描渐变就像电影里那种雷达扫描，用来梯度渲染
	private ComposeShader composeShader; //混合渲染器，可以和其他几个子类组合起来使用
	private int width;
	private int height;
	private Paint paint;

	public MyShaderView(Context context) {
		super(context);

		Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.icon)).getBitmap();
		width = bitmap.getWidth();
		height = bitmap.getHeight();
		//  tileX tileY：The tiling mode for x/y to draw the bitmap in.   在位图上 X/Y 方向 瓦工/花砖/瓷砖 模式 
		//   CLAMP  ：如果渲染器超出原始边界范围，会复制范围内【边缘】染色
		//   REPEA   ：横向和纵向的【重复】渲染器图片，平铺
		//   MIRROR：横向和纵向的重复渲染器图片，这个和REPEAT 重复方式不一样，他是以【镜像】方式平铺
		bitmapShader = new BitmapShader(bitmap, TileMode.MIRROR, TileMode.MIRROR);

		// 前四个参数:  渐变起 初点/终点 坐标 x/y 位置； colors:  渐变颜色数组；
		// positions:这个也是一个数组，用来指定颜色数组的相对位置，如果为null 就沿坡度线均匀分布
		linearGradient = new LinearGradient(50, 50, 1000, 1000, new int[] { Color.RED, Color.GREEN, Color.BLUE, Color.WHITE }, null, TileMode.REPEAT);

		// centerX  centerY  半径   渐变颜色数组   position     平铺方式  
		radialGradient = new RadialGradient(50, 200, 50, new int[] { Color.WHITE, Color.YELLOW, Color.GREEN, Color.RED }, null, TileMode.REPEAT);

		//混合渲染模式有16种  Shader shaderA, Shader shaderB, Mode mode
		composeShader = new ComposeShader(bitmapShader, linearGradient, PorterDuff.Mode.DARKEN);

		//梯度渲染器  float cx, float cy, int[] colors, float[] positions
		sweepGradient = new SweepGradient(30, 30, new int[] { Color.GREEN, Color.RED, Color.BLUE, Color.WHITE }, null);
		paint = new Paint();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.WHITE); //背景色  

		//将Bitmap截取为圆形、椭圆形
		paint.setShader(bitmapShader);
		canvas.drawOval(new RectF(0, 0, width, height), paint);
		canvas.drawOval(new RectF(150, 0, 150 + width, height), paint);
		canvas.drawOval(new RectF(300, 0, 300 + 100 + width, height), paint);

		//绘制线性渐变的矩形  
		paint.setShader(linearGradient);
		canvas.drawRect(0, 200, 400, 400, paint);

		//绘制环形渐变的圆
		paint.setShader(radialGradient);
		canvas.drawCircle(50, 500, 50, paint);
		canvas.drawCircle(50 + 100, 500, 50, paint);
		canvas.drawCircle(50 + 200, 500, 50, paint);
		canvas.drawCircle(50 + 300, 500, 50, paint);

		//绘制混合渐变(线性与环形混合)的矩形  
		paint.setShader(composeShader);
		canvas.drawRect(0, 600, 600, 900, paint);

		//绘制梯形渐变的矩形  
		paint.setShader(sweepGradient);
		canvas.drawRect(0, 950, 400, 1100, paint);
	}
}