package it.unibs.mp.horace.backend

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import it.unibs.mp.horace.R

class Settings(val context: Context) {

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    /**
     * The supported timer modes.
     */
    enum class Mode {
        Pomodoro, Stopwatch
    }

    enum class Theme {
        Light, Dark, System
    }

    var theme: Theme
        get() = Theme.values()[prefs.getInt(
            context.getString(R.string.preference_theme), Theme.System.ordinal
        )]
        private set(value) {
            prefs.edit().putInt(context.getString(R.string.preference_theme), value.ordinal).apply()
        }

    var isQuickActionsEnabled: Boolean
        get() = prefs.getBoolean(context.getString(R.string.preference_quick_actions), true)
        set(value) {
            prefs.edit().putBoolean(context.getString(R.string.preference_quick_actions), value)
                .apply()
        }


    /**
     * The current timer mode.
     * The default is `Pomodoro`.
     */
    private var mode: Mode
        get() = Mode.values()[prefs.getInt(
            context.getString(R.string.preference_mode), Mode.Pomodoro.ordinal
        )]
        private set(value) {
            prefs.edit().putInt(context.getString(R.string.preference_mode), value.ordinal).apply()
        }

    /**
     * Whether the current mode is `Pomodoro`.
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
    var isVolumeOn: Boolean
        get() = prefs.getBoolean(context.getString(R.string.preference_volume_on), false)
        private set(value) {
            prefs.edit().putBoolean("volume_on", value).apply()
        }

    /**
     * Toggles the current volume on/off status.
     */
    fun toggleVolume() {
        isVolumeOn = !isVolumeOn
    }

    var showQuickActions: Boolean
        get() = prefs.getBoolean(context.getString(R.string.preference_quick_actions), true)
        set(value) {
            prefs.edit().putBoolean(context.getString(R.string.preference_quick_actions), value)
                .apply()
        }
}