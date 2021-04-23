package com.example.das_entrega2.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.example.das_entrega2.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;


public class WidgetPlatosMomento extends AppWidgetProvider {


    private PendingIntent pi;
    private AlarmManager am;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        //Layout del widget en widget_platos_momento.xml
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_platos_momento);

        //Forzar actualización del widget desde el propio widget con un PendingIntent
        Intent intent = new Intent(context,WidgetPlatosMomento.class);
        intent.setAction("com.example.das_entrega2.ACTUALIZAR_WIDGET");
        intent.putExtra( AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                7768, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //sacamos un plato aleatorio de cada parte del menu
        String entrante = sacarEntrante(context);
        String principal = sacarPrincipal(context);
        String postre = sacarPostre(context);
        //Los ponemos en los TextViews del Widget
        views.setTextViewText(R.id.entrante, "Entrante: " + entrante);
        views.setTextViewText(R.id.principal, "Principal: " + principal);
        views.setTextViewText(R.id.postr,"Postre: " + postre);

        //cuando se pulsa el boton Actualizar se actualizan los platos del día
        views.setOnClickPendingIntent(R.id.buttonActualizar, pendingIntent);

        //Actualizar los RemoteViews de la instancia del Widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    //este método se encargará de sacar un plato aleatorio del fichero raw/entrantes.xml
    static String sacarEntrante(Context context){
        String entrante = "";
        try {

            Resources res = context.getResources();
            BufferedReader reader = new BufferedReader(new InputStreamReader(res.openRawResource(R.raw.entrantes)));
            String line = reader.readLine();
            ArrayList<String> entrantes = new ArrayList<String>();
            while(line != null){
                System.out.println(line);
                entrantes.add(line);
                line = reader.readLine();

            }
            //sacar frase aleatoria
            Random randomGenerator = new Random();
            int index = randomGenerator.nextInt(entrantes.size());
            entrante = entrantes.get(index);

        } catch (Exception e) {

        }

        return entrante;

    }


    //este método se encargará de sacar un plato aleatorio del fichero raw/principales.xml
    static String sacarPrincipal(Context context){
        String principal = "";
        try {

            Resources res = context.getResources();
            BufferedReader reader = new BufferedReader(new InputStreamReader(res.openRawResource(R.raw.principales)));
            String line = reader.readLine();
            ArrayList<String> principales = new ArrayList<String>();


            while(line != null){
                System.out.println(line);
                principales.add(line);
                line = reader.readLine();
            }

            //sacar frase aleatoria
            Random randomGenerator = new Random();
            int index = randomGenerator.nextInt(principales.size());
            principal = principales.get(index);

        } catch (Exception e) {
        }

        return principal;

    }


    //este método se encargará de sacar un plato aleatorio del fichero raw/postres.xml
    static String sacarPostre(Context context){
        String postre = "";
        try {

            Resources res = context.getResources();
            BufferedReader reader = new BufferedReader(new InputStreamReader(res.openRawResource(R.raw.postres)));
            String line = reader.readLine();
            ArrayList<String> postres = new ArrayList<String>();
            while(line != null){
                System.out.println(line);
                postres.add(line);
                line = reader.readLine();
            }

            //sacar frase aleatoria
            Random randomGenerator = new Random();
            int index = randomGenerator.nextInt(postres.size());
            postre = postres.get(index);

        } catch (Exception e) {
        }

        return postre;

    }



    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            //Recorrer todas las instancias del Widget
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            //WidgetPlatosMomentoConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        //crear la alarma para que el Widget se actualize cada hora mediante AlarmManagerBroadcastReceiver
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        pi = PendingIntent.getBroadcast(context, 7475, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //para que se actualize cada hora con la alarma
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+ 3 * 1000, 3600 * 1000 , pi);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        //cancelar la alarma
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 7475, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        //En la clase del widget se programa qué hay que hacer al recibir cada acción (ACTUALIZAR_WIDGET)
        if (intent.getAction().equals("com.example.das_entrega2.ACTUALIZAR_WIDGET")) {
            //Extraer el identificador del Widget
            int widgetId = intent.getIntExtra( AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
            if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                //Actualizar la instancia del Widget
                updateAppWidget(context, widgetManager, widgetId);
            }
        }
        super.onReceive(context,intent);
    }



}