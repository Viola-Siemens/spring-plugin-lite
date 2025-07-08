package com.hexagram2021.springpluginlite.insights

import com.hexagram2021.springpluginlite.assets.DependencyInjectionAssets
import com.hexagram2021.springpluginlite.utils.autoWiredAnnotation
import com.hexagram2021.springpluginlite.utils.isAutoWired
import com.hexagram2021.springpluginlite.utils.isBean
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProviderDescriptor
import com.intellij.codeInsight.hint.HintManager
import com.intellij.codeInsight.navigation.getPsiElementPopup
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.psi.*
import com.intellij.psi.search.searches.ClassInheritorsSearch
import com.intellij.ui.awt.RelativePoint
import com.intellij.util.PsiNavigateUtil
import java.awt.event.MouseEvent

class BeanLineMarkerProvider : LineMarkerProviderDescriptor() {
    override fun getName() = "Mixin element line marker"
    override fun getIcon() = DependencyInjectionAssets.BEAN_ELEMENT_ICON

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<PsiIdentifier>? {
        if (element !is PsiMember || !element.isAutoWired) {
            return null
        }
        // Do we need this?
//        val containingClass = element.containingClass ?: return null
//        if (!containingClass.isBean) {
//            return null
//        }

        return when (element) {
            is PsiField -> {
                val identifier = element.nameIdentifier

                val annotation = element.autoWiredAnnotation ?: return null
                val simpleName = annotation.qualifiedName?.substringAfterLast('.') ?: return null

                return LineMarkerInfo(
                    identifier,
                    identifier.textRange,
                    this.icon,
                    { "Go to the $simpleName target" },
                    BeanInstanceGutterIconNavigationHandler(element.createSmartPointer()),
                    GutterIconRenderer.Alignment.LEFT,
                    { "bean instance $simpleName target indicator" }
                )
            }
            else -> null
        }
    }

    private class BeanInstanceGutterIconNavigationHandler(
        private val elementPointer: SmartPsiElementPointer<PsiField>
    ) : GutterIconNavigationHandler<PsiIdentifier> {
        override fun navigate(e: MouseEvent, elt: PsiIdentifier) {
            val targets = resolveForNavigation(elt) ?: return
            val editor = FileEditorManager.getInstance(elt.project).selectedTextEditor
            when (targets.size) {
                0 -> {
                    if (editor != null) {
                        HintManager.getInstance().showErrorHint(
                            editor,
                            "Cannot find corresponding bean instance in source code",
                        )
                    }
                }
                1 -> {
                    PsiNavigateUtil.navigate(targets[0])
                }
                else -> {
                    if (editor != null) {
                        getPsiElementPopup(targets.toTypedArray(), "Choose Target")
                            .showInBestPositionFor(editor)
                    } else {
                        getPsiElementPopup(targets.toTypedArray(), "Choose Target")
                            .show(RelativePoint(e))
                    }
                }
            }
        }

        fun resolveForNavigation(elt: PsiIdentifier): List<PsiElement>? {
            val element = elementPointer.element ?: return null
            if (element.nameIdentifier != elt) {
                return null
            }
            val type = element.type
            if (type !is PsiClassType) {
                return null
            }
            val clazz = type.resolve() ?: return null
            return ClassInheritorsSearch.search(clazz).plus(clazz).mapNotNull {
                it?.takeIf { it.isBean }
            }.toList()
        }
    }
}