/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.touringmusician;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

public class TourMap extends View {

    private Bitmap mapImage;
    private CircularLinkedList list = new CircularLinkedList();
    private CircularLinkedList begList = new CircularLinkedList();
    private CircularLinkedList closeList = new CircularLinkedList();
    private CircularLinkedList smallList = new CircularLinkedList();
    private String insertMode = "Add";

    public TourMap(Context context) {
        super(context);
        mapImage = BitmapFactory.decodeResource(
                getResources(),
                R.drawable.map);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mapImage, 0, 0, null);
        Paint closePaint = new Paint();
        closePaint.setColor(Color.YELLOW);
        closePaint.setStrokeWidth(4);

        Paint begPaint = new Paint();
        begPaint.setColor(Color.BLUE);
        begPaint.setStrokeWidth(4);

        Paint smallPaint = new Paint();
        smallPaint.setColor(Color.RED);
        smallPaint.setStrokeWidth(4);

        Point p2 = new Point();
        if (!insertMode.equals("All")) {
            lineDraw(list,canvas, p2, smallPaint);
        } else {
            lineDraw(smallList,canvas,p2,smallPaint);
            lineDraw(closeList,canvas,p2,closePaint);
            lineDraw(begList,canvas,p2,begPaint);
        }
    }

    void lineDraw (CircularLinkedList list, Canvas canvas, Point p2, Paint paint) {
        if (list.iterator().hasNext()) {
            p2 = list.iterator().next();
        }
        for (Point p : list) {
            canvas.drawLine(p.x,p.y,p2.x,p2.y,paint);
            canvas.drawCircle(p.x, p.y, 20, paint);
            p2 = p;
        }
        if (list.iterator().hasNext()) {
            canvas.drawLine(p2.x,p2.y,list.iterator().next().x,list.iterator().next().y,paint);
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Point p = new Point((int) event.getX(), (int)event.getY());
                if (insertMode.equals("Closest")) {
                    list.insertNearest(p);
                } else if (insertMode.equals("Smallest")) {
                    list.insertSmallest(p);
                } else {
                    list.insertBeginning(p);
                }
                begList.insertBeginning(p);
                smallList.insertSmallest(p);
                closeList.insertNearest(p);
                TextView message = (TextView) ((Activity) getContext()).findViewById(R.id.game_status);
                if (message != null) {
                    if (insertMode.equals("All")) {
                        message.setText(String.format(Locale.US,"Beginning: %.2f , Closest: %.2f, Smallest: %.2f",
                                begList.totalDistance(),closeList.totalDistance(),smallList.totalDistance()));
                    } else {
                        message.setText(String.format(Locale.US,"Tour length is now %.2f", list.totalDistance()));
                    }
                }
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    public void reset() {
        list.reset();
        begList.reset();
        smallList.reset();
        closeList.reset();
        invalidate();
    }

    public void setInsertMode(String mode) {
        insertMode = mode;
    }
}
