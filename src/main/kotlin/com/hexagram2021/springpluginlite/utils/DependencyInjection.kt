package com.hexagram2021.springpluginlite.utils

import com.hexagram2021.springpluginlite.config.BeanApplicationContextFileType
import com.hexagram2021.springpluginlite.utils.DependencyInjectionConstants.Annotations.AUTO_WIRED
import com.hexagram2021.springpluginlite.utils.DependencyInjectionConstants.Annotations.COMPONENT
import com.intellij.execution.JavaExecutionUtil
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMember
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.xml.XmlTag
import java.util.*

val PsiClass.isBean
    get() = !beanAnnotations.isEmpty() || definedXmlTags != null

val PsiClass.beanAnnotations: List<PsiAnnotation>
    get() {
        if(this.isAnnotationType) {
            return Collections.emptyList()
        }
        return this.annotations.filterNotNull()
            .filter { it.hasQualifiedName(COMPONENT) || (it.resolveAnnotationType()?.hasAnnotation(COMPONENT) ?: false) }
    }

val PsiClass.definedXmlTags: List<XmlTag>?
    get() {
        val findModule = JavaExecutionUtil.findModule(this) ?: return null
        val ret = BeanApplicationContextFileType.getApplicationContextConfigs(project, GlobalSearchScope.moduleScope(findModule)).mapNotNull {
            applicationContext -> applicationContext.beans.array
        }.flatMap {
            array -> array.toList()
        }.filterNotNull().filter { bean -> bean.getAttribute("class")?.value?.equals(qualifiedName) ?: false }
        if (ret.isEmpty()) {
            return null
        }
        return ret
    }

val PsiMember.isAutoWired
    get() = autoWiredAnnotation != null

val PsiMember.autoWiredAnnotation
    get() = modifierList?.findAnnotation(AUTO_WIRED)