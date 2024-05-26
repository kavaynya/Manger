import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.validate
import java.io.OutputStream


private const val ANIMATION_DEFAULT_VALUE = "animation"
private const val ANIMATION_PARAMETER_NAME = "animationName"
private const val CREATOR_DEFAULT_VALUE = "creator"
private const val CREATOR_PARAMETER_NAME = "creatorName"
private const val NAME = "NavEntry"

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class NavEntry(
    val animationName: String = ANIMATION_DEFAULT_VALUE,
    val creatorName: String = CREATOR_DEFAULT_VALUE
)

internal data class ParamsContainer(
    val declaration: KSClassDeclaration,
    val creator: String,
    val animation: String,
    var hasAnimation: Boolean
)

class NavConfigProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return NavConfigProcessor(environment.codeGenerator, environment.logger)
    }
}

class NavConfigProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {
    private val file: OutputStream? = null
    private fun emit(s: String) {
        logger.info(s)
        file?.write((s + "\n").toByteArray(Charsets.UTF_8))
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("NavConfigProcessor is Launched $resolver")
        logger.warn(resolver.getAllFiles().toList().toString())
        resolver
            .getSymbolsWithAnnotation("NavEntry", true)
            .filterIsInstance<KSClassDeclaration>()
            .filter { it.classKind in listOf(ClassKind.CLASS, ClassKind.OBJECT) }
            .mapNotNull { declaration ->
                declaration.annotations
                    .filter { it.shortName.asString() == NAME }
                    .forEach { annotation ->
                        annotation.arguments.forEach { creator ->
                            if (creator.name?.asString() == CREATOR_PARAMETER_NAME) {
                                val creatorArgument = creator.value as? String
                                val creatorName =
                                    if (creatorArgument.isNullOrEmpty()) CREATOR_DEFAULT_VALUE else creatorArgument
                                annotation.arguments.forEach { animator ->
                                    if (animator.name?.asString() == ANIMATION_PARAMETER_NAME) {
                                        val animationArgument = animator.value as? String
                                        return@mapNotNull ParamsContainer(
                                            declaration,
                                            creatorName,
                                            if (animationArgument.isNullOrEmpty()) ANIMATION_DEFAULT_VALUE else animationArgument,
                                            false
                                        )
                                    }
                                }
                            }
                        }
                    }
                return@mapNotNull null
            }
            .filter { params ->
                val validated = params.declaration
                    .declarations
                    .filter { it.validate() }
                val declarations = (
                        validated
                            .filterIsInstance<KSClassDeclaration>()
                            .flatMap { it.declarations } + validated
                        ).filterIsInstance<KSPropertyDeclaration>()

                val hasCreator = declarations.any { it.simpleName.asString() == params.creator }

                if (hasCreator.not()) {
                    logger.error(
                        "${params.declaration} no has creator with ${params.creator} name. Please add creator",
                        params.declaration
                    )
                }

                params.hasAnimation =
                    declarations.any { it.simpleName.asString() == params.animation }

                hasCreator
            }
            .groupBy { params -> params.declaration.packageName.asString() }
            .forEach { (packageName, params) ->
                emit("-> $packageName")
                params.forEach { (declaration, creator) ->
                    emit("-> $declaration.$creator")
                }
                runCatching {
                    codeGenerator.createNewFile(
                        dependencies = Dependencies(
                            aggregating = false,
                            sources = resolver.getAllFiles().toList().toTypedArray()
                        ),
                        packageName = "",
                        fileName = "Builder",
                        extensionName = "kt"
                    ).use {
                        it.write(generateBuilder(packageName, params).toByteArray(Charsets.UTF_8))
                    }
                }
            }

        return emptyList()
    }

    private fun generateBuilder(packageName: String, params: List<ParamsContainer>): String {
        return buildString {

            appendLine("package $packageName")
            appendLine()
            appendLine()
            appendLine("import com.san.kir.core.utils.ManualDI");
            appendLine("import com.san.kir.core.utils.navigation.NavComponent");
            appendLine("import com.san.kir.core.utils.navigation.NavConfig");
            appendLine("import com.arkivanov.decompose.extensions.compose.stack.animation.StackAnimator");
            appendLine("import android.util.Log");
            appendLine()
            appendLine("internal object AddNavigationCreators {")
            appendLine("\tinit {")
            appendLine("\t\tLog.d(\"Builder\", \"add to DI $packageName\")")
            params.forEach { (configName, creator, animation, hasAnimation) ->
                appendLine("\t\tManualDI.addNavigationCreator($configName::class, $configName.$creator)")
                if (hasAnimation) {
                    appendLine("\t\tManualDI.addNavigationAnimation($configName::class, $configName.$animation)")
                }
            }
            appendLine("\t}")
            appendLine("}")
        }
    }
}
