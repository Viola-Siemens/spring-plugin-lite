package com.hexagram2021.springpluginlite.inspection.suppress

import com.hexagram2021.springpluginlite.utils.DependencyInjectionConstants.Annotations
import com.intellij.codeInspection.InspectionSuppressor
import com.intellij.codeInspection.SuppressQuickFix
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.intellij.psi.PsiIdentifier

class AutowiredInspectionSuppressor : InspectionSuppressor {
    override fun isSuppressedFor(element: PsiElement, toolId: String): Boolean {
        if (element !is PsiIdentifier) {
            return false
        }

        val field = element.parent as? PsiField ?: return false

        // Skip nullable warnings for @Autowired fields
        if (toolId == "NullableProblems" || toolId == "NotNullFieldNotInitialized" || toolId == "unused") {
            return field.modifierList?.findAnnotation(Annotations.AUTO_WIRED) != null
        }

        return false
    }

    override fun getSuppressActions(element: PsiElement?, toolId: String): Array<SuppressQuickFix> =
        SuppressQuickFix.EMPTY_ARRAY
}