package com.hexagram2021.springpluginlite.icons

import com.hexagram2021.springpluginlite.assets.DependencyInjectionAssets
import com.hexagram2021.springpluginlite.utils.isBean
import com.intellij.ide.IconLayerProvider
import com.intellij.openapi.util.Iconable
import com.intellij.psi.PsiClass
import javax.swing.Icon

class BeanIconProvider : IconLayerProvider {
    override fun getLayerIcon(element: Iconable, isLocked: Boolean): Icon? {
        if (element !is PsiClass || !element.isBean) {
            return null
        }
        
        return DependencyInjectionAssets.BEAN_MARK
    }

    override fun getLayerDescription(): String = "Bean"
}