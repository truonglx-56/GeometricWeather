package wangdaye.com.geometricweather.utils.helpter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import wangdaye.com.geometricweather.GeometricWeather;
import wangdaye.com.geometricweather.R;
import wangdaye.com.geometricweather.service.PollingService;
import wangdaye.com.geometricweather.service.ProtectService;
import wangdaye.com.geometricweather.service.NormalUpdateService;
import wangdaye.com.geometricweather.service.TodayForecastUpdateService;
import wangdaye.com.geometricweather.service.TomorrowForecastUpdateService;
import wangdaye.com.geometricweather.utils.ValueUtils;
import wangdaye.com.geometricweather.utils.remoteView.ForecastNotificationUtils;
import wangdaye.com.geometricweather.utils.remoteView.NormalNotificationUtils;
import wangdaye.com.geometricweather.utils.remoteView.WidgetClockDayVerticalUtils;
import wangdaye.com.geometricweather.utils.remoteView.WidgetClockDayHorizontalUtils;
import wangdaye.com.geometricweather.utils.remoteView.WidgetClockDayWeekUtils;
import wangdaye.com.geometricweather.utils.remoteView.WidgetDayUtils;
import wangdaye.com.geometricweather.utils.remoteView.WidgetDayWeekUtils;
import wangdaye.com.geometricweather.utils.remoteView.WidgetWeekUtils;

/**
 * Service helper.
 * */

public class ServiceHelper {

    public static void startupService(Context context, int forceRefreshType) {
        startPermanentService(context, forceRefreshType);
    }

    public static void stopNormalService(Context context) {
        startPermanentService(context, PollingService.FORCE_REFRESH_TYPE_NORMAL_VIEW);
        context.stopService(new Intent(context, NormalUpdateService.class));
    }

    public static void stopForecastService(Context context, boolean today) {
        startPermanentService(context, PollingService.FORCE_REFRESH_TYPE_FORECAST);
        if (today) {
            context.stopService(new Intent(context, TodayForecastUpdateService.class));
        } else {
            context.stopService(new Intent(context, TomorrowForecastUpdateService.class));
        }
    }

    private static void startPermanentService(Context context, int forceRefreshType) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        boolean backgroundFree = sharedPreferences.getBoolean(context.getString(R.string.key_background_free), false);
        boolean working = isBackgroundWorking(context);
        String refreshRate = sharedPreferences.getString(context.getString(R.string.key_refresh_rate), "1:30");
        boolean openTodayForecast = sharedPreferences.getBoolean(context.getString(R.string.key_forecast_today), false);
        String todayForecastTime = sharedPreferences.getString(
                context.getString(R.string.key_forecast_today_time),
                GeometricWeather.DEFAULT_TODAY_FORECAST_TIME);
        boolean openTomorrowForecast = sharedPreferences.getBoolean(context.getString(R.string.key_forecast_tomorrow), false);
        String tomorrowForecastTime = sharedPreferences.getString(
                context.getString(R.string.key_forecast_tomorrow_time),
                GeometricWeather.DEFAULT_TOMORROW_FORECAST_TIME);

        // polling service.
        Intent polling = new Intent(context, PollingService.class);
        polling.putExtra(PollingService.KEY_IS_REFRESH, true);
        polling.putExtra(PollingService.KEY_WORKING, working);
        polling.putExtra(PollingService.KEY_BACKGROUND_FREE, backgroundFree);
        polling.putExtra(PollingService.KEY_FORCE_REFRESH_TYPE, forceRefreshType);
        polling.putExtra(PollingService.KEY_POLLING_RATE, ValueUtils.getRefreshRateScale(refreshRate));
        polling.putExtra(PollingService.KEY_OPEN_TODAY_FORECAST, openTodayForecast);
        polling.putExtra(PollingService.KEY_TODAY_FORECAST_TIME, todayForecastTime);
        polling.putExtra(PollingService.KEY_OPEN_TOMORROW_FORECAST, openTomorrowForecast);
        polling.putExtra(PollingService.KEY_TOMORROW_FORECAST_TIME, tomorrowForecastTime);

        // protect service.
        Intent protect = new Intent(context, ProtectService.class);
        protect.putExtra(ProtectService.KEY_IS_REFRESH, true);
        protect.putExtra(ProtectService.KEY_WORKING, working);
        protect.putExtra(ProtectService.KEY_BACKGROUND_FREE, backgroundFree);

        context.startService(polling);
        context.startService(protect);
    }

    private static boolean isBackgroundWorking(Context context) {
        return hasNormalView(context)
                || ForecastNotificationUtils.isEnable(context, true)
                || ForecastNotificationUtils.isEnable(context, false);
    }

    public static boolean hasNormalView(Context context) {
        return WidgetDayUtils.isEnable(context)
                || WidgetWeekUtils.isEnable(context)
                || WidgetDayWeekUtils.isEnable(context)
                || WidgetClockDayHorizontalUtils.isEnable(context)
                || WidgetClockDayVerticalUtils.isEnable(context)
                || WidgetClockDayWeekUtils.isEnable(context)
                || NormalNotificationUtils.isEnable(context)
                || TileHelper.isEnable(context);
    }
}
