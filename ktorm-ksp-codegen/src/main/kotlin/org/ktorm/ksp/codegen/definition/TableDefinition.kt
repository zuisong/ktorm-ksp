/*
 * Copyright 2018-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ktorm.ksp.codegen.definition

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.ClassName
import org.ktorm.entity.Entity
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Table
import kotlin.reflect.KClass

/**
 * Table definition, which contains all the information in the table.
 */
public data class TableDefinition(

    /**
     * The table name，Corresponds to the [org.ktorm.ksp.api.Table.tableName] property, may be an empty string.
     */
    val tableName: String,

    /**
     * The table class name，By default, the noun plural of the entity class name is converted, and the value
     * will not be an empty string.
     */
    val tableClassName: ClassName,

    /**
     * The sequence name，By default, the first character lowercase of the [tableClassName], and the value
     * will not be an empty string.
     */
    val sequenceName: String,

    /**
     * The Table alias, Corresponding to the [org.ktorm.ksp.api.Table.alias] property, may be an empty string.
     */
    val alias: String,

    /**
     * The table catalog，Corresponding to the [org.ktorm.ksp.api.Table.catalog] property, may be an empty string.
     */
    val catalog: String,

    /**
     * The table schema，Corresponding to the [org.ktorm.ksp.api.Table.schema] property, may be an empty string.
     */
    val schema: String,

    /**
     * The entity class name.
     */
    val entityClassName: ClassName,

    /**
     * Column definition list.
     */
    val columns: List<ColumnDefinition>,

    /**
     * The file where the entity class code.
     */
    val entityFile: KSFile,

    /**
     * Class declaration for entity class.
     */
    val entityClassDeclaration: KSClassDeclaration,

    /**
     * Type of entity class.
     */
    val ktormEntityType: KtormEntityType
)

/**
 * Entity class type, the code generated by different types of entities will be different.
 */
public enum class KtormEntityType(
    public val defaultTableSuperClass: KClass<out BaseTable<*>>
) {

    /**
     * Interface entity of inherited [Entity], whose table must be a subclass of [Table].
     */
    ENTITY_INTERFACE(Table::class),

    /**
     * Entity of any kind of class whose table must be a subclass of [BaseTable].
     */
    ANY_KIND_CLASS(BaseTable::class)
}
