package it.unibs.mp.horace.backend

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import it.unibs.mp.horace.R

/**
 * Wraps app settings.
 */
class Settings(val context: Context) {

    // The settings are stored in the default shared preferences.
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    /**
     * Supported timer modes.
     */
    enum class Mode {
        Pomodoro, Stopwatch
    }

    /*
     * Supported themes.
     */
    enum class Theme {
        Light, Dark, System
    }

    /**
     * The current app theme.
     */
    var theme: Theme
        get() {
            val themeString = prefs.getString(
                context.getString(R.string.preference_theme),
                context.getString(R.string.theme_device)
            )

            return when (themeString) {
                context.getString(R.string.theme_light) -> Theme.Light
                context.getString(R.string.theme_dark) -> Theme.Dark
                else -> Theme.System
            }
        }
        private set(value) {
            val themeString = when (value) {
                Theme.Light -> context.getString(R.string.theme_light)
                Theme.Dark -> context.getString(R.string.theme_dark)
                else -> context.getString(R.string.theme_device)
            }
            prefs.edit().putString(context.getString(R.string.preference_theme), themeString)
                .apply()
        }

    /**
     * Whether the quick actions are enabled or not.
     * The default value is `true`.
     */
    var isQuickActionsEnabled: Boolean
        get() = prefs.getBoolean(context.getString(R.string.preference_quick_actions), true)
        set(value) {
            prefs.edit().putBoolean(context.getString(R.string.preference_quick_actions), value)
                .apply()
        }

    /**
     * The current activity id selected by the user in the home. The default value is `null`.
     */
    var activityId: String?
        get() = prefs.getString(context.getString(R.string.preference_activity_id), null)
        set(value) {
            if (value == null) {
                prefs.edit().remove(context.getString(R.string.preference_activity_id)).apply()
                return
            }
            prefs.edit().putString(context.getString(R.string.preference_activity_id), value)
                .apply()
        }

    /**
     * Whether the activities should be sorted in ascending order (by name) or not.
     */
    var isActivitySortAscending: Boolean
        get() = prefs.getBoolean(
            context.getString(R.string.preference_activity_sort_ascending),
            true
        )
        set(value) {
            prefs.edit()
                .putBoolean(context.getString(R.string.preference_activity_sort_ascending), value)
                .apply()
        }

    /**
     * Whether the workgroup should be sorted in ascending order (by username) or not.
     */
    var isWorkgroupSortAscending: Boolean
        get() = prefs.getBoolean(
            context.getString(R.string.preference_workgroup_sort_ascending),
            true
        )
        set(value) {
            prefs.edit()
                .putBoolean(context.getString(R.string.preference_workgroup_sort_ascending), value)
                .apply()
        }

    /**
     * Whether the friends should be sorted in ascending order (by username) or not.
     */
    var isFriendsSortAscending: Boolean
        get() = prefs.getBoolean(
            context.getString(R.string.preference_friends_sort_ascending),
            true
        )
        set(value) {
            prefs.edit()
                .putBoolean(context.getString(R.string.preference_friends_sort_ascending), value)
                .apply()
        }

    /**
     * The current timer mode. The default value is [Mode.Pomodoro].
     */
    private var mode: Mode
        get() = Mode.values()[prefs.getInt(
            context.getString(R.string.preference_mode), Mode.Pomodoro.ordinal
        )]
        private set(value) {
            prefs.edit().putInt(context.getString(R.string.preference_mode), value.ordinal).apply()
        }

    /**
     * Whether the current mode is [Mode.Pomodoro]
     */
    val isModePomodoro: Boolean get() = mode == Mode.Pomodoro

    /**
     * Switches the current mode to `Pomodoro`.
     */
    fun switchModeToPomodoro() {
        mode = Mode.Pomodoro
    }

    /**
     * Switches the current mode to stopwatch.
     */
    fun switchModeToStopwatch() {
        mode = Mode.Stopwatch
    }

    /**
     * Whether the volume is enabled or not.
     * The default is `false`.
     */
    var isVolumeEnabled: Boolean
        get() = prefs.getBoolean(context.getString(R.string.preference_volume_on), false)
        private set(value) {
            prefs.edit().putBoolean(context.getString(R.string.preference_volume_on), value).apply()
        }

    /**
     * Toggles the current volume on/off status.
     */
    fun toggleVolume() {
        isVolumeEnabled = !isVolumeEnabled
    }
}