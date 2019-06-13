package com.example.bakingrecipeapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.bakingrecipeapp.MainActivity;
import com.example.bakingrecipeapp.R;

public class BakingWidget extends AppWidgetProvider {

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, String titleText, String bodyText) {

        if (titleText == null) {
            titleText = context.getString(R.string.add_widget_help);
        }

        if (bodyText == null) {
            bodyText = context.getString(R.string.app_name);
        }

        // construct RemoteViews
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget_provider);
        views.setTextViewText(R.id.ingredient_appwidget_text_textView, bodyText);
        views.setTextViewText(R.id.title_appwidget_text_textView, titleText);

        // intent for launch activity
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        views.setOnClickPendingIntent(R.id.ingredient_appwidget_text_textView, pendingIntent);

        // widget manager for widget update
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, context.getString(R.string.app_name), context.getString(R.string.add_widget_help));
        }
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }
}
