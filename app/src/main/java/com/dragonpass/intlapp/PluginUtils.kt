package com.dragonpass.intlapp

object PluginUtils {
    @JvmStatic
    fun isEmpty(s: CharSequence?): Boolean = s.isNullOrEmpty()
    fun getPgyIdentifier(): String = "pgyer"
} 