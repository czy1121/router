package me.reezy.cosmo.router.processor

import com.squareup.kotlinpoet.*
import me.reezy.cosmo.router.annotation.Route
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ksp.toTypeName
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets



class RouteProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    options: Map<String, String>,
) : SymbolProcessor {
    private val moduleName: String = options["router.moduleName"] ?: ""
    private val formattedModuleName: String = moduleName.replace("[^0-9a-zA-Z_]+".toRegex(), "")
    private val generatedPackageName: String = (options["router.packageName"] ?: PKG) + ".generated"

    init {
        logger.warn(options.toString())
    }

    private val routes = mutableSetOf<String>()


    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.warn("====== >>> route [$moduleName] init")

        if (moduleName.isBlank()) {
            logger.warn("this moduleName is empty, skipped")
            return listOf()
        }
        val symbols0 = resolver.getSymbolsWithAnnotation(requireNotNull(Route::class.qualifiedName))
        val symbols1 = symbols0.filter { it.validate() }

        logger.warn("Found ${symbols0.count()} - ${symbols1.count()} routes in [$moduleName]")
        if (symbols0.count() > 0) {
            generate(resolver, symbols0)
        }
        return listOf()
    }


    @OptIn(KspExperimental::class)
    private fun generate(resolver: Resolver, symbols: Sequence<KSAnnotated>) {
        val clazzActivity = resolver.getClassDeclarationByName("android.app.Activity")!!.asType(listOf())
        val clazzRouteCallable = resolver.getClassDeclarationByName("$PKG.RouteCallable")!!.asType(listOf())
        val clazzRouteTable = resolver.getClassDeclarationByName("$PKG.internal.RouteTable")!!.asType(listOf())

        routes.clear()
        val funcSpec = FunSpec.builder("load").addParameter("rt", clazzRouteTable.toTypeName())

        symbols.forEach {
            val annotation = it.getAnnotationsByType(Route::class).first()

            val paths = (arrayOf(annotation.value) + annotation.routes).filter { path ->
                if (path.isEmpty()) {
                    logger.warn("The route path is empty, so skip $it")
                    return@filter false
                }
                if (routes.contains(path)) {
                    logger.warn("The route path $path already exists, so skip $it")
                    return@filter false
                }
                return@filter true
            }.toTypedArray()

            if (paths.isEmpty()) {
                logger.warn("The route paths is empty, so skip $it")
                return@forEach
            }

            it.accept(object : KSVisitorVoid() {
                override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
                    val type = classDeclaration.asType(listOf())
                    if (clazzActivity.isAssignableFrom(type) || clazzRouteCallable.isAssignableFrom(type)) {
                        logger.warn("Found $type")

                        routes.addAll(paths)
                        funcSpec.addStatement("rt.add(%T::class.java, %L, %L, %L)", type.toTypeName(), paths.format(), annotation.interceptors.format(), annotation.flags)

                    } else {
                        logger.warn("Unknown route type, so skip $type")
                    }
                }
            }, Unit)
        }

        val typeSpec = TypeSpec.classBuilder("RouteLoader_$formattedModuleName")
            .addKdoc(WARNINGS)
            .addFunction(funcSpec.build())
            .build()

        val fileSpec = FileSpec.builder(generatedPackageName, "RouteLoader_$formattedModuleName")
            .addType(typeSpec)
            .build()


        fileSpec.writeFile(codeGenerator)
    }

    private fun FileSpec.writeFile(codeGenerator: CodeGenerator) {
        val file = codeGenerator.createNewFile(Dependencies.ALL_FILES, packageName, name)
        OutputStreamWriter(file, StandardCharsets.UTF_8).use(::writeTo)
    }

    private fun Array<String>.format(): String {
        if (isEmpty()) {
            return "null"
        }
        return joinToString("\", \"", "arrayOf(\"", "\")")
    }

    companion object {
        private const val WARNINGS = """
   ***************************************************
   * THIS CODE IS GENERATED BY Router, DO NOT EDIT.  *
   ***************************************************
"""
        private const val PKG = "me.reezy.cosmo.router"
    }
}



