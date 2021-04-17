package com.example.das_entrega2.widget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.das_entrega2.R;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_platos_momento);

        String entrante = WidgetPlatosMomento.sacarEntrante(context);
        String principal = WidgetPlatosMomento.sacarPrincipal(context);
        String postre = WidgetPlatosMomento.sacarPostre(context);
        remoteViews.setTextViewText(R.id.entrante, "Entrante: " + entrante);
        remoteViews.setTextViewText(R.id.principal,"Principal: " +  principal);
        remoteViews.setTextViewText(R.id.postr,"Postre: " + postre);
        ComponentName tipowidget = new ComponentName(context, WidgetPlatosMomento.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(tipowidget, remoteViews);
    }
}
