package com.arp.paint;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    private Vista vista;
    private Paint pincel;
    private RelativeLayout r;
    private final int CARGAR=1;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CARGAR && resultCode == Activity.RESULT_OK){
            Uri uri = data.getData();
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (Exception ex){
                bitmap = null;
            }
            vista.cargarImagen(bitmap);
        }
    }

    private void guardar(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Guardar");
        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.dialog_guardar, null);
        alert.setView(view);
        final EditText et=(EditText)view.findViewById(R.id.etNombre);
        alert.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String nombre = et.getText().toString();
                        Bitmap mapaDeBits = vista.getMapaDeBits();
                        File carpeta = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath());
                        File archivo = new File(carpeta, nombre + ".PNG");
                        try {
                            FileOutputStream fos = new FileOutputStream(archivo);
                            mapaDeBits.compress(Bitmap.CompressFormat.PNG, 90, fos);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri uri = Uri.fromFile(archivo);
                        intent.setData(uri);
                        getApplicationContext().sendBroadcast(intent);
                    }

    });
        alert.setNegativeButton(android.R.string.no, null);
        alert.show();

    }

    private void cargar(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        Intent i = Intent.createChooser(galleryIntent, "imagen");
        startActivityForResult(i, CARGAR);
    }
}
