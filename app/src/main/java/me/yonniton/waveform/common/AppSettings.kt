package me.yonniton.waveform.common

import android.content.SharedPreferences
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class AppSettings(private val sharedPreferences: SharedPreferences) {

    fun getInt(key: String, defaultValue: Int) = sharedPreferences.getInt(key, defaultValue)

    fun getInt(key: String) = sharedPreferences.getInt(key, 0)

    fun putInt(key: String, value: Int) = sharedPreferences
        .edit()
        .putInt(key, value)
        .apply()

    fun getLong(key: String, defaultValue: Long) = sharedPreferences.getLong(key, defaultValue)

    fun getLong(key: String) = sharedPreferences.getLong(key, 0)

    fun putLong(key: String, value: Long) = sharedPreferences
        .edit()
        .putLong(key, value)
        .apply()

    fun getString(key: String?, defValue: String?): String? = sharedPreferences.getString(key, defValue)

    fun getString(key: String): String? = sharedPreferences.getString(key, null)

    fun putString(key: String, value: String?) = sharedPreferences
        .edit()
        .putString(key, value)
        .apply()

    fun getBoolean(key: String, defaultValue: Boolean): Boolean = sharedPreferences.getBoolean(key, defaultValue)

    fun getBoolean(key: String) = sharedPreferences.getBoolean(key, false)

    fun putBoolean(key: String, value: Boolean) = sharedPreferences
        .edit()
        .putBoolean(key, value)
        .apply()

    fun remove(key: String) = sharedPreferences
        .edit()
        .remove(key)
        .apply()

    fun removeAll() = sharedPreferences
        .edit()
        .clear()
        .apply()

    fun contains(key: String) = sharedPreferences.contains(key)

    val settingsChanged: Observable<String> = PublishSubject.create<String>().also { subject ->
        sharedPreferences.registerOnSharedPreferenceChangeListener { _, key ->
            subject.onNext(key)
        }
    }
}
