package nl.vslcatena.vslcatena.firebase

import kotlin.reflect.KClass

/**
 * Created by Thomas van den Bulk on 14-5-2018.
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class ListType(val value: KClass<*>)