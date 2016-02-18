package com.arp.paint;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    private Vista vista;
    private Paint pincel;
    private RelativeLayout r;
    private boolean relleno=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vista=new Vista(this);
        r=(RelativeLayout)findViewById(R.id.layaout);
        r.addView(vista);
        setTitle("");
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        vista.setPincel(pincel());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            /*Color picker*/
            case R.id.color:
                colorPicker();
                return true;
            /*Dibuja todas las formas que necesito*/
            case R.id.circulo:
                vista.setForma("circulo");
                return true;
            case R.id.rectangulo:
                vista.setForma("rectangulo");
                return true;
            case R.id.linea:
                vista.setForma("linea");
                return true;
            case R.id.libre:
                vista.setForma("libre");
                return true;
            /*Guarda el archivo*/
            case R.id.guardar:
                guardar();
                return true;
            /*Creamos una vista nueva para conseguir el efecto de limpiar*/
            case R.id.limpiar:
                vista.limpiar();
                return true;
            /*Cargar el archivo*/
            case R.id.cargar:
                cargar();
                return true;
            case R.id.relleno:
                if(relleno==false) {
                    pincel.setStyle(Paint.Style.FILL);
                    vista.setPincel(pincel);
                    relleno=true;
                }else {
                    pincel.setStyle(Paint.Style.STROKE);
                    vista.setPincel(pincel);
                    relleno=false;
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Paint pincel(){
        pincel=new Paint();
        pincel.setColor(Color.BLACK);
        pincel.setAntiAlias(true);
        pincel.setStyle(Paint.Style.STROKE);
        pincel.setStrokeWidth(5);
        return pincel;
    }

    private void colorPicker(){
        ColorPickerDialog colorPickerDialog = new ColorPickerDialog(this, pincel.getColor(), new ColorPickerDialog.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                pincel.setColor(color);
                vista.setPincel(pincel);
            }
        });
        colorPickerDialog.show();
    }

    private void guardar(){
        try {
            File carpeta=new File(getExternalFilesDir(null).getPath());
            File archivo = new File(carpeta, "dibujo.png");
            FileOutputStream fos = null;
            fos = new FileOutputStream(archivo);
            vista.getMapaDeBits().createBitmap(vista.getWidth(), vista.getHeight(),
                    Bitmap.Config.ARGB_8888).compress(
                    Bitmap.CompressFormat.PNG, 90, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void cargar(){
        File carpeta=new File(getExternalFilesDir(null).getPath());
        File archivo = new File (carpeta, "dibujo.png");
        Bitmap imagenFondo = Bitmap.createBitmap(vista.getWidth(), vista.getHeight(), Bitmap.Config.ARGB_8888);
        if (archivo.exists()){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable=true;
            imagenFondo=BitmapFactory.decodeFile(archivo.getAbsolutePath(),options);
            vista.setMapaDeBits(imagenFondo);
        }
        Canvas lienzoFondo = new Canvas(imagenFondo);
        vista.setLienzoFondo(lienzoFondo);
    }
}
