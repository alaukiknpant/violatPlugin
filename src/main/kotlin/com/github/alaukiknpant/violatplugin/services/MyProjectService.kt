package com.github.alaukiknpant.violatplugin.services

import com.intellij.openapi.project.Project
import com.github.alaukiknpant.violatplugin.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
