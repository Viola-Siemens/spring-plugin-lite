package com.hexagram2021.springpluginlite.utils

import com.hexagram2021.springpluginlite.utils.DependencyInjectionConstants.Annotations.AUTO_WIRED
import com.hexagram2021.springpluginlite.utils.DependencyInjectionConstants.Annotations.COMPONENT
import com.hexagram2021.springpluginlite.utils.DependencyInjectionConstants.Annotations.SERVICE
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMember

val PsiClass.isBean
    get() = beanAnnotation != null

val PsiClass.beanAnnotation
    get() = modifierList?.findAnnotation(COMPONENT) ?: modifierList?.findAnnotation(SERVICE)

val PsiMember.isAutoWired
    get() = autoWiredAnnotation != null

val PsiMember.autoWiredAnnotation
    get() = modifierList?.findAnnotation(AUTO_WIRED)