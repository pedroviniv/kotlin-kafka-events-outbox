package io.github.kieckegard.outbox

import org.springframework.context.annotation.*
import org.springframework.core.type.filter.AnnotationTypeFilter

/**
 * component that scans classes in the classpath
 * for various filters.
 *
 * currently supporting scaning classes annotated with a given annotation
 */
@ComponentScan
class ClassScanner {

    fun getClassesAnnotatedWith(annotation: Class<out Annotation>): List<Class<*>> {

        val scanner = ClassPathScanningCandidateComponentProvider(true)
        scanner.addIncludeFilter(AnnotationTypeFilter(annotation))

        val candidates = scanner
                .findCandidateComponents("io.github.kieckegard.outbox")

        return candidates.map { Class.forName(it.beanClassName) }
                .toList()
    }
}