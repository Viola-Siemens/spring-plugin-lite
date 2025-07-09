package com.hexagram2021.springpluginlite.config

import com.hexagram2021.springpluginlite.assets.DependencyInjectionAssets
import com.intellij.ide.highlighter.XmlLikeFileType
import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.fileTypes.ex.FileTypeIdentifiableByVirtualFile
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.xml.XmlFile

interface BeanApplicationContextFileType : FileTypeIdentifiableByVirtualFile {
    val filenameRegex: Regex
    override fun isMyFileType(file: VirtualFile) = file.name.contains(filenameRegex)

    override fun getDefaultExtension() = ""
    override fun getIcon() = DependencyInjectionAssets.BEAN_ICON

    object Xml : XmlLikeFileType(XMLLanguage.Companion.INSTANCE), BeanApplicationContextFileType {
        override val filenameRegex = "^applicationContext\\.xml$".toRegex()

        override fun getName() = "Application Context"
        override fun getDescription() = "Application context for spring beans"
    }

    companion object {
        private val applicationContextFileTypes = listOf(Xml)

        fun getApplicationContextConfigs(project: Project, scope: GlobalSearchScope): Collection<ApplicationContext> {
            return applicationContextFileTypes
                .flatMap { FileTypeIndex.getFiles(it, scope) }
                .mapNotNull { file ->
                    (PsiManager.getInstance(project).findFile(file) as? XmlFile)?.document
                }.map { xmlDocument ->
                    ApplicationContext(project, xmlDocument)
                }
        }
    }
}