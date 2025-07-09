package com.hexagram2021.springpluginlite.config

import com.intellij.lang.LanguageImportStatements
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.psi.xml.XmlDocument
import com.intellij.psi.xml.XmlFile
import com.intellij.psi.xml.XmlTag

class ApplicationContext(private val project: Project, private var xml: XmlDocument) {
    var autoReformat = true

    val file: VirtualFile?
        get() = xml.containingFile.virtualFile

    var beans: ApplicationContextList
        get() = ApplicationContextList("bean")
        set(value) {
            val prevAutoReformat = autoReformat
            autoReformat = false
            val v = beans
            v.clear()
            v.addAll(value)
            autoReformat = prevAutoReformat
            if (autoReformat) {
                reformat()
            }
        }

    private fun reformat() {
        xml = CodeStyleManager.getInstance(project).reformat(xml) as XmlDocument
        file?.let { file ->
            val psiFile = PsiManager.getInstance(project).findFile(file) as? XmlFile ?: return
            LanguageImportStatements.INSTANCE.forFile(psiFile).forEach { it.processFile(psiFile).run() }
            xml = (PsiManager.getInstance(project).findFile(file) as XmlFile).document ?: return
        }
    }

    inner class ApplicationContextList(private val key: String) : AbstractMutableList<String?>() {
        private val parent: XmlTag?
            get() = xml.rootTag
        val array: Array<XmlTag?>?
            get() = parent?.findSubTags(key)

        override val size: Int
            get() = array?.size ?: 0

        override fun add(index: Int, element: String?) {
            val oldSize = size
            if (index < 0 || index > oldSize) {
                throw IndexOutOfBoundsException(index.toString())
            }
            val arr = xml.rootTag
            if (arr == null) {
                return
            }
            val newValue = arr.createChildTag(key, arr.namespace, "", false)
            if (element != null) {
                val id = element.substringAfterLast('.')
                newValue.setAttribute("id", id)
                newValue.setAttribute("class", element)
            }
            when {
                oldSize == 0 -> {
                    arr.addAfter(newValue, arr.firstChild)
                }
                index == oldSize -> {
                    val anchor = arr.lastChild
                    arr.addBefore(newValue, anchor)
                }
                else -> {
                    // Add comma after
                    val anchor = arr.children[index]
                    arr.addBefore(newValue, anchor)
                }
            }
            if (autoReformat) {
                reformat()
            }
        }

        override fun get(index: Int): String? {
            if (index < 0 || index >= size) {
                throw IndexOutOfBoundsException(index.toString())
            }
            return array?.get(index)?.getAttribute("class")?.value
        }

        override fun removeAt(index: Int): String? {
            if (index < 0 || index >= size) {
                throw IndexOutOfBoundsException(index.toString())
            }
            val toDelete = array?.get(index) ?: return null
            val oldStr = toDelete.getAttribute("class")?.value
            toDelete.delete()
            if (autoReformat) {
                reformat()
            }
            return oldStr
        }

        override fun set(index: Int, element: String?): String? {
            if (index < 0 || index >= size) {
                throw IndexOutOfBoundsException(index.toString())
            }
            val toReplace = array?.get(index) ?: return null
            val oldStr = toReplace.getAttribute("class")?.value
            if (element != null) {
                val id = element.substringAfterLast('.')
                toReplace.setAttribute("id", id)
                toReplace.setAttribute("class", element)
            }
            if (autoReformat) {
                reformat()
            }
            return oldStr
        }
    }
}
