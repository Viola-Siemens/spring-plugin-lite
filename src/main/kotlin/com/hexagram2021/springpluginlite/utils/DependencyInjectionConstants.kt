package com.hexagram2021.springpluginlite.utils

object DependencyInjectionConstants {
    object Annotations {
        const val COMPONENT = "org.springframework.stereotype.Component"
        const val CONFIGURATION = "org.springframework.context.annotation.Configuration"
        const val BEAN = "org.springframework.context.annotation.Bean"

        const val AUTO_WIRED = "org.springframework.beans.factory.annotation.Autowired"
    }
}