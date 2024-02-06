package com.example.ascope_lite.CamTest_v01;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.example.ascope_lite.CrackInspect.CrackCoordinateData;
import com.example.ascope_lite.R;

import java.util.ArrayList;
import java.util.List;

public class DrawView extends View {
    /////////////
    int type = 0;
    CrackCoordinateData mark_data = new CrackCoordinateData();
    List<CrackCoordinateData> mark_list;
    int x = 0;
    int y = 0;
    int MarkerIndex = 0;
    private float fpX0 = 0;
    private float fpY0 = 0;
    private float fpW0 = 0;
    private float fpH0 = 0;
    private boolean is_first = false;
    private boolean is_first2 = false;
    private float[] XY_pos = new float[2];
    private int TouchType = 1;


    public void setMark_data(CrackCoordinateData mark_data) {
        this.mark_data = mark_data;
    }

    public void setMark_list(List<CrackCoordinateData> mark_list) {
        this.mark_list = mark_list;
    }

    public float[] getXY_pos() {
        return XY_pos;
    }

    public void setXY_pos(float[] XY_pos) {
        this.XY_pos = XY_pos;
    }
/////////////

    private int mBackColor = Color.rgb(33, 40, 48);
    private float xCen = 0;
    private float yCen = 0;
    private float xCurCen = 0;
    private float yCurCen = 0;

    //초기 scale값(기기에 맞게 fit)
    private float displayScale_init = 1.0f;

    //터치해서 마지막 변경한 scale값
    private float displayScale = 1.0f;

    private float FullW, FullH, Full2W, Full2H;
    private float previousX = 0;
    private float previousY = 0;
    public Bitmap ViewBaseImg = null;
    private float CutOffDist = 0;
    private float ScrDiagDist = 0;
    private ArrayList<Bitmap> ImgIconList = new ArrayList<Bitmap>();
    public int SelIconIndex = -1;
    private float CurImgW = 0;
    private float CurImgH = 0;
    private float CurStartW = 0;
    private float CurStartH = 0;
    private float CurAdjImgW = 0;
    private float CurAdjImgH = 0;
    private float CurMarkScale = 1.0f;

    //초기시 크기에 추가되는 퍼센트값. (ex. 30으로 적용 시 30% 더 크게 보여짐.)
    private float ADD_SCALE = 0;

    private final int DRAW_SCALE_MODE = 1;
    private final int DRAW_ICON_MODE = 2;
    private int DrawMode = DRAW_SCALE_MODE;

    private final int MARK_NONE = 0;
    private final int MARK_IN = 1;
    private final int MARK_DRAW = 2;
    private int MarkType = MARK_NONE;
    private int CurMarkIndex = -1;
    private float CurMarkX = 0;
    private float CurMarkY = 0;

    ArrayList<clsIcon> clsIconList;
    private int iconCount;

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);

        ScreenSizeInit();
        scaleDetector = new ScaleGestureDetector(this.getContext(), new ScaleListener());
        //setOnTouchListener(this);
        // need this to get our onDraw method called...  :-\
        this.setWillNotDraw(false);
    }


    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //canvas.translate(xCen, yCen);
        //canvas.scale(displayScale, displayScale, xCurCen, yCurCen);
        drawDesign(canvas);
    }


    public void PutIconList(ArrayList<clsIcon> icons) {
        this.clsIconList = icons;
    }

    public void PutIcon(Bitmap bImage) {
        Bitmap imgTemp = Bitmap.createBitmap(bImage);
        ImgIconList.add(imgTemp);
    }

    public void PutImage(Bitmap bImage, int type) {
        if (ViewBaseImg != null) {
            ViewBaseImg = null;
        }
        ScreenSizeInit();
        CurImgW = bImage.getWidth();
        CurImgH = bImage.getHeight();
//        Log.e("PutImage():", "CurImgW : " + CurImgW);
//        Log.e("PutImage():", "CurImgH : " + CurImgH);
        LoadBitmapInit();
        CurScaleImgHW();
        ViewBaseImg = bImage.copy(bImage.getConfig(), true);
        this.type = type;
    }

    public void ReSetDraw() {
//        ScreenSizeInit();
//        LoadBitmapInit();
//        CurScaleImgHW();
    }

    private void LoadBitmapInit() {
        float fScaleW, fScaleH;
        CurStartW = CurImgW / 2;
        CurStartH = CurImgH / 2;
        fScaleW = FullW / CurImgW;
        fScaleH = FullH / CurImgH;
        if (fScaleW < fScaleH) {
            displayScale = fScaleW;
        } else {
            displayScale = fScaleH;
        }
        displayScale = displayScale * (100 + ADD_SCALE) / 100;

        if (!is_first2) {
            displayScale_init = displayScale;
            is_first2 = true;
        }
    }

    private void CurScaleImgHW() {
        CurAdjImgW = CurImgW * displayScale;
        CurAdjImgH = CurImgH * displayScale;
    }

    private ScaleGestureDetector scaleDetector;

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float newDisplayScale = displayScale * detector.getScaleFactor();
            displayScale = newDisplayScale;
            CurScaleImgHW();
            //invalidate();
            return true;
        }
    }

    private void ScreenSizeInit() {
        FullW = this.getWidth();
        FullH = this.getHeight();
        Full2W = FullW / 2;
        Full2H = FullH / 2;
        xCen = 0;
        yCen = 0;
        xCurCen = Full2W;
        yCurCen = Full2H;
        displayScale = 1.0f;
        DrawMode = DRAW_SCALE_MODE;
        SelIconIndex = -1;
        CurMarkIndex = -1;
        MarkType = MARK_NONE;
        CurMarkScale = 1.0f;
        if (clsIconList != null) {
            clsIconList.clear();
            iconCount = 0;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float deltaX, deltaY, deltaavX, deltaavY, entGetX1, entGetY1, entGetX2, entGetY2, entGetavX, entGetavY;
        float cmp_sum1;

        scaleDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                TouchType = 1;
                if (DrawMode == DRAW_ICON_MODE) {
                    previousX = event.getX();
                    previousY = event.getY();
//                    Log.e("ACTION_DOWN: ", "DRAW_ICON_MODE");
//                    Log.e("previousX: ", String.valueOf(previousX));
//                    Log.e("previousY: ", String.valueOf(previousY));
                    MarkIconImg(SelIconIndex, previousX, previousY);
                    invalidate();
                } else if (DrawMode == DRAW_SCALE_MODE) {
                    //xCen = 0;
                    //yCen = 0;
                    previousX = event.getX();
                    previousY = event.getY();
//                    Log.e("ACTION_DOWN: ", "DRAW_SCALE_MODE");
//                    Log.e("previousX: ", String.valueOf(previousX));
//                    Log.e("previousY: ", String.valueOf(previousY));
                    //xCurCen = previousX;
                    //yCurCen = this.getHeight() - previousY;
                    //yCurCen = previousY;
                    xCurCen = Full2W;
                    yCurCen = Full2H;
                    //displayScale = 1.0f;
                    ScrDiagDist = CurDistNum(this.getWidth(), this.getHeight());
                    CutOffDist = ScrDiagDist * 8 / 100;
                } else {
                    xCen = 0;
                    yCen = 0;
                    previousX = event.getX();
                    previousY = event.getY();
//                    Log.e("ACTION_DOWN: ", "기본");
//                    Log.e("previousX: ", String.valueOf(previousX));
//                    Log.e("previousY: ", String.valueOf(previousY));
                    //xCurCen = previousX;
                    //yCurCen = this.getHeight() - previousY;
                    //yCurCen = previousY;
                    xCurCen = Full2W;
                    yCurCen = Full2H;
                    //displayScale = 1.0f;
                    ScrDiagDist = CurDistNum(this.getWidth(), this.getHeight());
                    CutOffDist = ScrDiagDist * 8 / 100;
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (DrawMode == DRAW_ICON_MODE) {
                } else if (DrawMode == DRAW_SCALE_MODE) {
                    if (event.getPointerCount() > 1) {
                        entGetX1 = event.getX(0);
                        entGetY1 = event.getY(0);
                        entGetX2 = event.getX(1);
                        entGetY2 = event.getY(1);
                        entGetavX = (entGetX1 + entGetX2) / 2;
                        entGetavY = (entGetY1 + entGetY2) / 2;
                        deltaavX = entGetavX - previousX;
                        deltaavY = entGetavY - previousY;
                        if (TouchType == 1) {
                            TouchType = 2;
                            deltaavX = 0;
                            deltaavY = 0;
                        }
                        xCen += deltaavX;
                        yCen += deltaavY;
                        previousX = entGetavX;
                        previousY = entGetavY;

//                        Log.e("ACTION_MOVE: ", "1");
//                        Log.e("previousX: ", String.valueOf(previousX));
//                        Log.e("previousY: ", String.valueOf(previousY));
                        xCurCen = entGetavX;
                        yCurCen = entGetavY;//this.getHeight() - entGetavY;
                    } else {
                        deltaX = event.getX(0) - previousX;
                        deltaY = event.getY(0) - previousY;
                        if (TouchType == 2) {
                            TouchType = 1;
                            //cmp_sum1 = deltaX + deltaY;
                            cmp_sum1 = CurDistNum(deltaX, deltaY);
                            if (CutOffDist < cmp_sum1) {
                                deltaX = 0;
                                deltaY = 0;
                            }
                        }

                        xCen += deltaX;
                        yCen += deltaY;

                        previousX = event.getX(0);
                        previousY = event.getY(0);
//                        Log.e("ACTION_MOVE: ", "2");
//                        Log.e("previousX: ", String.valueOf(previousX));
//                        Log.e("previousY: ", String.valueOf(previousY));

                        //xCurCen = previousX;
                        //yCurCen = previousY;//this.getHeight() - previousY;
                        /**
                         xCurCen = previousX;
                         yCurCen = this.getHeight() - previousY;
                         xCurCen = event.getX(0);
                         yCurCen = this.getHeight() - event.getY(0);
                         */
                    }
                } else {
                    if (event.getPointerCount() > 1) {
                        entGetX1 = event.getX(0);
                        entGetY1 = event.getY(0);
                        entGetX2 = event.getX(1);
                        entGetY2 = event.getY(1);
                        entGetavX = (entGetX1 + entGetX2) / 2;
                        entGetavY = (entGetY1 + entGetY2) / 2;

                        deltaavX = entGetavX - previousX;
                        deltaavY = entGetavY - previousY;

                        xCen += deltaavX;
                        yCen += deltaavY;
                        previousX = entGetavX;
                        previousY = entGetavY;
//                        Log.e("ACTION_MOVE: ", "3");
//                        Log.e("previousX: ", String.valueOf(previousX));
//                        Log.e("previousY: ", String.valueOf(previousY));
                        xCurCen = entGetavX;
                        yCurCen = entGetavY;//this.getHeight() - entGetavY;
                    } else {
                        deltaX = event.getX(0) - previousX;
                        deltaY = event.getY(0) - previousY;

                        xCen += deltaX;
                        yCen += deltaY;

                        previousX = event.getX(0);
                        previousY = event.getY(0);
//                        Log.e("ACTION_MOVE: ", "4");
//                        Log.e("previousX: ", String.valueOf(previousX));
//                        Log.e("previousY: ", String.valueOf(previousY));

                        //xCurCen = previousX;
                        //yCurCen = previousY;//this.getHeight() - previousY;
                        /**
                         xCurCen = previousX;
                         yCurCen = this.getHeight() - previousY;
                         xCurCen = event.getX(0);
                         yCurCen = this.getHeight() - event.getY(0);
                         */
                    }
                }
                invalidate();
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (DrawMode == DRAW_ICON_MODE) {

                } else if (DrawMode == DRAW_SCALE_MODE) {
                } else {

                }
                break;
            }
            default: {
                break;
            }
        }

        return true;
    }

    private void drawDesign(Canvas canvas) {
        float[] result_pos = new float[2];
        float result_pos_x;
        float result_pos_y;
        float mx, my, dx1, dy1, dx2, dy2;
        float fpX1, fpY1, fpW1, fpH1;

        CurScaleImgHW();
        Paint paint = new Paint();
        paint.setColor(mBackColor);
        //paint.setColor(Color.rgb(33, 40, 48));
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);

        if (ViewBaseImg != null) {
            float textSize = 16f;

            fpX1 = Full2W + xCen - CurAdjImgW / 2;
            fpY1 = Full2H + yCen - CurAdjImgH / 2;
            fpW1 = CurAdjImgW + fpX1;
            fpH1 = CurAdjImgH + fpY1;

            if (!is_first) {
                fpX0 = Full2W + xCen - CurAdjImgW / 2;
                fpY0 = Full2H + yCen - CurAdjImgH / 2;
                fpW0 = CurAdjImgW + fpX0;
                fpH0 = CurAdjImgH + fpY0;
                is_first = true;
            }
//            Log.e("fpX0, fpY0, fpW0, fpH0", fpX0 + ", " + fpY0 + ", " + fpW0 + ", " + fpH0);
//            Log.e("fpX1, fpY1, fpW1, fpH1", fpX1 + ", " + fpY1 + ", " + fpW1 + ", " + fpH1);
//            Log.e("Full2W", Full2W + "");
//            Log.e("Full2H", Full2H + "");
//            Log.e("xCen", xCen + "");
//            Log.e("yCen", yCen + "");
//            Log.e("CurAdjImgW", CurAdjImgW + "");
//            Log.e("CurAdjImgH", CurAdjImgH + "");

            canvas.drawBitmap(ViewBaseImg, null, new RectF(fpX1, fpY1, fpW1, fpH1), null);

            if (MarkType == MARK_IN) {
                if (CurMarkIndex > -1) {
//                    초기 이미지는 displayScale_init (뷰에 꽉 차게 맞는 scale 값)으로 확대/축소 되어 그려짐.
//                    mx: 터치 지점 x좌표, my: 터치 지점 y좌표 (화면 좌상단 0,0 기준)
//                    fpX0: 초기이미지 x1 좌표, fpY0: 초기이미지 y1 좌표, fpW0: 초기이미지 x2 좌표 fpH0: 초기이미지 y2좌표
//                    fpX1: 이동 및 확대 후 x1 좌표, fpY1: 이동 및 확대 후 y1 좌표, fpW1: 이동 및 확대 후 x2 좌표 fpH1: 이동 및 확대 후 y2좌표
//                    ex. 초기이미지의 좌상단 좌표: (fpX0, fpY0), 좌하단 좌표: (fpX0, fpH0), 우상단 좌표: (fpW0, fpY0), 우하단 좌표: (fpW0, fpH0)
//                        이동 및 확대 후의 좌상단 좌표: (fpX1, fpY1), 좌하단 좌표: (fpX1, fpH1), 우상단 좌표: (fpW1, fpY1), 우하단 좌표: (fpW1, fpH1)

//                    마커 좌표 변환 (이동 및 확대/축소 된 bitmap 의 좌상단을 0,0 기준으로 생성한 마커 좌표 변환)
//                    public float getMarkerPos(float x, float fpx0, float fpx1, float fpw0, float fpw1) {
//                        return (x - fpx1) * (fpw0 - fpx0) / (fpw1 - fpx1);
//                    }

//                    result_pos_x: 현재 등록되고 있는 x좌표 (반올림하여 등록 중)
//                    result_pos_y: 현재 등록되고 있는 y좌표 (반올림하여 등록 중)
                    mx = CurMarkX;
                    my = CurMarkY;
                    result_pos_x = getMarkerPos(mx, fpX0, fpX1, fpW0, fpW1);
                    result_pos_y = getMarkerPos(my, fpY0, fpY1, fpH0, fpH1);

//                    result_pos[0] = result_pos_x;
//                    result_pos[1] = result_pos_y;

                    result_pos[0] = (result_pos_x / displayScale_init);
                    result_pos[1] = (result_pos_y / displayScale_init);
                    setXY_pos(result_pos);
//                    Log.e("(x, y) : ", "(x, y) : " + mx + ", " + my);
//                    Log.e("(x, y) : ", "(result_pos[0], result_pos[1]) : " + result_pos[0] + ", " + result_pos[1]);
                    /////////////
                    dx1 = mx - 20;
                    dy1 = my - 20;
                    dx2 = mx + 20;
                    dy2 = my + 20;
                    RectF rectF = new RectF(dx1, dy1, dx2, dy2);
                    canvas.drawBitmap(ImgIconList.get(CurMarkIndex), null, rectF, null);
                }
            }

            if (type == 2) {
                MarkerIndex = 0;
                x = (int) (mark_data.getX() * displayScale_init);
                y = (int) (mark_data.getY() * displayScale_init);

                MarkDraw(MarkerIndex, x, y, canvas, mark_data.getIndex());
            }

            if (type == 3) {
                MarkerIndex = 0;

                for (int i = 0; i < mark_list.size(); i++) {
                    x = (int) (mark_list.get(i).getX() * displayScale_init);
                    y = (int) (mark_list.get(i).getY() * displayScale_init);
//                    Log.e("displayScale_init:::", "displayScale_init::" + displayScale_init);
//                    Log.e("(x, y) : ", "(x, y) : " + x + ", " + y);
                    MarkDraw(MarkerIndex, x, y, canvas, mark_list.get(i).getIndex());
                }
            }
        }

        if (clsIconList != null) {
            for (int i = 0; i < clsIconList.size(); i++) {
                clsIcon ciDat = clsIconList.get(i);
                if (ciDat.iIndex > -1 && ciDat.iIndex < 6) {
                    mx = Full2W + xCen + (ciDat.iX - CurStartW) * displayScale;
                    my = Full2H + yCen + (ciDat.iY - CurStartH) * displayScale;
                    dx1 = mx - 20;
                    dy1 = my - 20;
                    dx2 = mx + 20;
                    dy2 = my + 20;
                    RectF rectF = new RectF(dx1, dy1, dx2, dy2);
                    canvas.drawBitmap(getResource(ciDat.iIndex, 0), null, rectF, null);
                }
            }
        }
    }

    public void MarkDraw(int index, int posX, int posY, Canvas canvas, int marker_num) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);   // Text Color 설정
        paint.setTextSize(16f);
//        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        MarkIconImg(index, posX, posY);

        float dx1 = posX + fpX0 - 10;
        float dy1 = posY + fpY0 - 10;
        float dx2 = posX + fpX0 + 10;
        float dy2 = posY + fpY0 + 10;

        RectF rectF = new RectF(dx1, dy1, dx2, dy2);
//        Log.e("CurMarkIndex", "" + CurMarkIndex);
        canvas.drawBitmap(ImgIconList.get(CurMarkIndex), null, rectF, null);

        String output = String.valueOf(marker_num);
        canvas.drawText(output, rectF.centerX() - paint.measureText(output) / 2, rectF.centerY() + paint.measureText(output) / 2 - 1, paint);
    }

    public float getMarkerPos(float xy, float xy0, float xy1, float wh0, float wh1) {
        return (xy - xy1) * (wh0 - xy0) / (wh1 - xy1);
    }

    private Bitmap getResource(int type, int num) {
        Resources res = getContext().getResources();
//        int id = res.getIdentifier("icon_" + (type + 1) + "_" + num, "drawable", getContext().getPackageName());
//        if (id == 0) {
        return BitmapFactory.decodeResource(getContext().getResources(), R.drawable.icon_1_0);
//        } else {
//            return BitmapFactory.decodeResource(getContext().getResources(), id);
//        }
    }

    private String[] MarkIconImg(int IconIndex, float mPosX, float mPosY) {
        //float mx, my, dx1, dy1, dx2, dy2, iconW, iconH, pX, pY;
        String[] result_pos = new String[2];

        if (IconIndex > -1) {
            if (ViewBaseImg != null) {
                CurMarkIndex = IconIndex;

                if (type == 1) {
                    MarkType = MARK_IN;
                }
//                CurMarkX = (mPosX - (Full2W + xCen)) / displayScale;
//                CurMarkY = (mPosY - (Full2H + yCen)) / displayScale;

                CurMarkX = mPosX;
                CurMarkY = mPosY;

                result_pos[0] = String.valueOf(CurMarkX);
                result_pos[1] = String.valueOf(CurMarkY);

//                Log.e("MarkIconImg: ", "CurMarkX: " + CurMarkX);
//                Log.e("MarkIconImg: ", "CurMarkY: " + CurMarkY);
                //canvas.drawBitmap(ImgIconList.get(IconIndex), null, new RectF(dx1, dy1, dx2, dy2), null);
            }
        }
        return result_pos;
    }

    public void SetScaleMode() {
        DrawMode = DRAW_SCALE_MODE;
    }

    public void SetEditMode(int SelIconKey) {
        DrawMode = DRAW_ICON_MODE;
        SelIconIndex = SelIconKey;
    }

    private float CurDistNum(float X1, float Y1) {
        float ResCurDistNum = 0;
        ResCurDistNum = (float) Math.sqrt(X1 * X1 + Y1 * Y1);
        return ResCurDistNum;
    }

    public boolean GetExistView() {
        boolean resBlFlag = false;
        if (ViewBaseImg != null) {
            resBlFlag = true;
        } else {

        }
        return resBlFlag;
    }
}
