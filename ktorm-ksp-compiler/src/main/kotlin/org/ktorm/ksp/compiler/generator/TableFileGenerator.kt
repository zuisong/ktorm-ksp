package org.ktorm.ksp.compiler.generator

import com.google.devtools.ksp.processing.KSPLogger
import com.squareup.kotlinpoet.FileSpec
import org.ktorm.ksp.codegen.*
import java.util.*

public object TableFileGenerator {


    private val typeGenerator: TableTypeGenerator = getOneOrNullService() ?: DefaultTableTypeGenerator()
    private val propertyGenerator: TablePropertyGenerator = getOneOrNullService() ?: DefaultTablePropertyGenerator()
    private val functionGenerator: TableFunctionGenerator = getOneOrNullService() ?: DefaultTableFunctionGenerator()
    private val topLevelPropertyGenerator: MutableSet<TopLevelPropertyGenerator> =
        getAllService<TopLevelPropertyGenerator>().toMutableSet()
    private val topLevelFunctionGenerator: MutableSet<TopLevelFunctionGenerator> =
        getAllService<TopLevelFunctionGenerator>().toMutableSet()

    public fun init(config: CodeGenerateConfig, logger: KSPLogger) {
        val defaultGenerator = config.defaultGenerator
        if (defaultGenerator.enableSequenceOf && topLevelPropertyGenerator.none { it is SequencePropertyGenerator }) {
            topLevelPropertyGenerator.add(SequencePropertyGenerator())
        }
        if (defaultGenerator.enableClassEntitySequenceAddFun && topLevelFunctionGenerator.none { it is ClassEntitySequenceAddFunGenerator }) {
            topLevelFunctionGenerator.add(ClassEntitySequenceAddFunGenerator())
        }
        if (defaultGenerator.enableClassEntitySequenceUpdateFun && topLevelFunctionGenerator.none { it is ClassEntitySequenceUpdateFunGenerator }) {
            topLevelFunctionGenerator.add(ClassEntitySequenceUpdateFunGenerator())
        }
        logger.info("typeGenerator: ${typeGenerator::class.simpleName}")
        logger.info("propertyGenerator: ${propertyGenerator::class.simpleName}")
        logger.info("functionGenerator: ${functionGenerator::class.simpleName}")
        logger.info("topLevelPropertyGenerator: ${topLevelPropertyGenerator.map { it::class.simpleName }}")
        logger.info("topLevelFunctionGenerator: ${topLevelFunctionGenerator.map { it::class.simpleName }}")
    }

    private inline fun <reified T> getOneOrNullService(): T? {
        val services = ServiceLoader.load(T::class.java, TableFileGenerator::class.java.classLoader).toSet()
        if (services.isEmpty()) return null
        if (services.size > 1) error("Service ${T::class.java.canonicalName} cannot be more than one")
        return services.first()
    }

    private inline fun <reified T> getAllService(): Set<T> {
        return ServiceLoader.load(T::class.java, TableFileGenerator::class.java.classLoader).toSet()
    }


    private fun <T : Any> Iterable<TableCodeGenerator<T>>.forEachGenerate(
        context: TableGenerateContext,
        action: (T) -> Unit
    ) {
        forEach {
            it.generate(context, action)
        }
    }

    public fun generate(context: TableGenerateContext): FileSpec {
        val fileBuilder = generateFile(context)
        typeGenerator.generate(context) { typeBuilder ->
            propertyGenerator.generate(context) { typeBuilder.addProperty(it) }
            functionGenerator.generate(context) { typeBuilder.addFunction(it) }
            fileBuilder.addType(typeBuilder.build())
        }
        topLevelFunctionGenerator.forEachGenerate(context) { fileBuilder.addFunction(it) }
        topLevelPropertyGenerator.forEachGenerate(context) { fileBuilder.addProperty(it) }
        return fileBuilder.build()
    }

    private fun generateFile(context: TableGenerateContext): FileSpec.Builder {
        val table = context.table
        return FileSpec.builder(table.tableClassName.packageName, table.tableClassName.simpleName)
    }
}