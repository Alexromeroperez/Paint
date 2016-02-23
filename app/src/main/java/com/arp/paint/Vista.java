package com.arp.paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

public class Vista extends View {

    private int ancho,alto;
    private float x0,y0,xf,yf;
    private float radio;
    private String forma="libre";
    private Bitmap mapaDeBits;
    private Canvas lienzoFondo;
    private Paint pincel;
    private Path rectaPoligonal=new Path();


    public Vista(Context context) {
        super(context);
    }

    public void setLienzoFondo(Canvas lienzoFondo) {
        this.lienzoFondo = lienzoFondo;
    }

    public Bitmap getMapaDeBits() {
        return mapaDeBits;
    }

    public void setMapaDeBits(Bitmap mapaDeBits) {
        this.mapaDeBits = mapaDeBits;
    }

    public void setForma(String forma) {
        this.forma = forma;
    }

    public void setPincel(Paint pincel) {
        this.pincel = pincel;
    }

    /***************************************/
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mapaDeBits = Bitmap.createBitmap(w, h,Bitmap.Config.ARGB_8888);
        lienzoFondo = new Canvas(mapaDeBits);
        ancho=w;
        alto=h;
    }

    @Override
    protected void onDraw(Canvas lienzo) {
        super.onDraw(lienzo);
        lienzo.drawBitmap(mapaDeBits, 0, 0, null);
        if(forma.equals("libre")){
            lienzo.drawPath(rectaPoligonal, pincel);
        } else if(forma.equals("circulo")) {
            lienzo.drawCircle(x0, y0, radio, pincel);
        } else if(forma.equals("rectangulo")) {
            lienzo.drawRect(Math.min(x0, xf), Math.min(y0, yf), Math.max(x0, xf), Math.max(y0, yf), pincel);
        } else if(forma.equals("linea")) {
            lienzo.drawLine(x0, y0, xf, yf, pincel);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(forma.equals("libre")){
            dibujaLibre(event);}
        else if(forma.equals("circulo")) {
            dibujaCirculo(event);
        }
        else if(forma.equals("rectangulo")) {
            dibujaRectangulo(event);
        }
        else if(forma.equals("linea")) {
            dibujaLinea(event);
        }
        return true;
    }

/****************Dibujar las formas****************/
    private void dibujaCirculo(MotionEvent event){
        float x=event.getX();
        float y=event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x0=xf=x;
                y0=yf=y;
                break;
            case MotionEvent.ACTION_MOVE:
                xf=x;
                yf=y;
                invalidate();
                radio=(float)Math.sqrt(Math.pow(x0-xf,2)+Math.pow(y0-yf,2));
                break;
            case MotionEvent.ACTION_UP:
                xf=x;
                yf=y;
                lienzoFondo.drawCircle(x0, y0, radio, pincel);
                invalidate();
                break;
        }
    }

    private void dibujaRectangulo(MotionEvent event){
        float x=event.getX();
        float y=event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x0 = xf = x;
                y0 = yf = y;
                break;
            case MotionEvent.ACTION_MOVE:
                xf=x;
                yf=y;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                xf=x;
                yf = y;
                lienzoFondo.drawRect(Math.min(x0, xf), Math.min(y0, yf), Math.max(x0, xf), Math.max(y0, yf), pincel);
                invalidate();
                break;
        }

    }

    private void dibujaLibre(MotionEvent event){
        float x=event.getX();
        float y=event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x0 = x;
                y0 = y;
                xf = x;
                yf = y;
                rectaPoligonal.reset();
                rectaPoligonal.moveTo(x0, y0);
                break;
            case MotionEvent.ACTION_MOVE:
                rectaPoligonal.quadTo(xf, yf, (x + xf) / 2, (y + yf)/2);
                xf = x;
                yf = y;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                xf = x;
                yf = y;
                lienzoFondo.drawPath(rectaPoligonal, pincel);
                rectaPoligonal.reset();
                invalidate();
                break;
        }
    }

    private void dibujaLinea(MotionEvent event){
        float x=event.getX();
        float y=event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x0=x;
                y0=y;
                break;
            case MotionEvent.ACTION_MOVE:
                xf=x;
                yf=y;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                xf=x;
                yf=y;
                lienzoFondo.drawLine(x0, y0, xf, yf, pincel);
                invalidate();
                break;
        }
    }

    public void limpiar(){
        mapaDeBits = Bitmap.createBitmap(ancho, alto,Bitmap.Config.ARGB_8888);
        lienzoFondo = new Canvas(mapaDeBits);
        invalidate();
        return;
    }

    public void cargarImagen(Bitmap bm){
        float scaleFactor = Math.min( (float) this.getWidth() /  bm.getWidth(),(float)this.getHeight() /  bm.getHeight() );
        Bitmap scaled = Bitmap.createScaledBitmap( bm,(int)(scaleFactor * bm.getWidth()),(int)(scaleFactor * bm.getHeight()),true );
        lienzoFondo.drawBitmap(scaled, 0, 0, pincel);
    }

}
