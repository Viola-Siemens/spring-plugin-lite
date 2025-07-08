package com.hexagram2021.springpluginlite.utils

import com.hexagram2021.springpluginlite.utils.DependencyInjectionConstants.Annotations.COMPONENT
import com.hexagram2021.springpluginlite.utils.DependencyInjectionConstants.Annotations.SERVICE
import com.intellij.psi.PsiClass

val PsiClass.isBean
    get() = beanAnnotation != null

val PsiClass.beanAnnotation
    get() = modifierList?.findAnnotation(COMPONENT) ?: modifierList?.findAnnotation(SERVICE)