package com.hexagram2021.springpluginlite.utils

import com.hexagram2021.springpluginlite.config.BeanApplicationContextFileType
import com.hexagram2021.springpluginlite.utils.DependencyInjectionConstants.Annotations.AUTO_WIRED
import com.hexagram2021.springpluginlite.utils.DependencyInjectionConstants.Annotations.BEAN
import com.hexagram2021.springpluginlite.utils.DependencyInjectionConstants.Annotations.COMPONENT
import com.hexagram2021.springpluginlite.utils.DependencyInjectionConstants.Annotations.CONFIGURATION
import com.intellij.psi.*
import com.intellij.psi.search.searches.AnnotatedElementsSearch
import com.intellij.psi.xml.XmlTag
import java.util.*

val PsiClass.isBean
    get() = !beanAnnotations.isEmpty() || !definedXmlTags.isEmpty() || !definedBeanMethods.isEmpty()

val PsiClass.beanAnnotations: List<PsiAnnotation>
    get() {
        if(this.isAnnotationType) {
            return Collections.emptyList()
        }
        return this.annotations.filterNotNull()
            .filter { it.hasQualifiedName(COMPONENT) || (it.resolveAnnotationType()?.hasAnnotation(COMPONENT) ?: false) }
    }

val PsiClass.definedXmlTags: List<XmlTag>
    get() = BeanApplicationContextFileType.getApplicationContextConfigs(project, this.resolveScope).mapNotNull {
        applicationContext -> applicationContext.beans.array
    }.flatMap {
        array -> array.toList()
    }.filterNotNull().filter { bean -> bean.getAttribute("class")?.value?.equals(qualifiedName) ?: false }

val PsiClass.definedBeanMethods: List<PsiMethod>
    get() {
        val annotationClazz = JavaPsiFacade.getInstance(project).findClass(BEAN, this.resolveScope) ?: return Collections.emptyList()
        return AnnotatedElementsSearch.searchPsiMethods(annotationClazz, this.resolveScope).filter {
            val parent = it.containingClass
            val returnType = it.returnType
            returnType is PsiClassType && this == returnType.resolve() && parent != null && parent.hasAnnotation(CONFIGURATION)
        }
    }

val PsiMember.isAutoWired
    get() = autoWiredAnnotation != null

val PsiMember.autoWiredAnnotation
    get() = modifierList?.findAnnotation(AUTO_WIRED)