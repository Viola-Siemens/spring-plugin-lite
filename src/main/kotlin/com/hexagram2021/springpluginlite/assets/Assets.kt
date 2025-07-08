package com.hexagram2021.springpluginlite.assets

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

abstract class Assets protected constructor() {
    protected fun loadIcon(path: String): Icon {
        return IconLoader.getIcon(path, Assets::class.java)
    }
}