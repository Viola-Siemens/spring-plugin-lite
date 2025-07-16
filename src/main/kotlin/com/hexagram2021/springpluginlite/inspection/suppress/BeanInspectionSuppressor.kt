package com.hexagram2021.springpluginlite.inspection.suppress

import com.hexagram2021.springpluginlite.utils.DependencyInjectionConstants.Annotations
import com.intellij.codeInspection.InspectionSuppressor
import com.intellij.codeInspection.SuppressQuickFix
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiMethod

class BeanInspectionSuppressor : InspectionSuppressor {
    override fun isSuppressedFor(element: PsiElement, toolId: String): Boolean {
        if (element !is PsiIdentifier) {
            return false
        }

        val field = element.parent as? PsiMethod ?: return false

        // Skip nullable warnings for @Autowired fields
        if (toolId == "unused") {
            return field.modifierList.findAnnotation(Annotations.BEAN) != null
        }

        return false
    }

    override fun getSuppressActions(element: PsiElement?, toolId: String): Array<SuppressQuickFix> =
        SuppressQuickFix.EMPTY_ARRAY
}