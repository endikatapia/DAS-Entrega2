package com.example.das_entrega2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class miBD extends SQLiteOpenHelper {
    public miBD(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Crear la tabla de Pedidos (codigoPedido(PK): INTEGER, elementosPedidos: VARCHAR(255), precioPedido: REAL);
        sqLiteDatabase.execSQL("CREATE TABLE Pedidos ('CodigoPedido' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 'Elementos' VARCHAR(255), 'Precio' REAL)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



    //este metodo inserta un nuevo pedido en la BBDD Pedidos pasandole de parametros
    //un String que contendra los elementos Pedidos y un Double que sera el precio total del pedido
    public void guardarPedido(String elementosPedidosSinRepeticion, double precioTotal) {
        SQLiteDatabase bd = getWritableDatabase();
        //añadimos a la BD Pedidos el ultimo pedido realizado
        bd.execSQL("INSERT INTO Pedidos('Elementos','Precio') VALUES ('"+elementosPedidosSinRepeticion+"','"+precioTotal+"')");
        System.out.println("Pedido añadido con estos ELEMENTOS: " + elementosPedidosSinRepeticion + " y este PRECIO: "+ precioTotal);


    }


}
