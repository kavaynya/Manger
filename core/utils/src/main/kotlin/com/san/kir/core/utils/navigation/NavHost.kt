package com.san.kir.core.utils.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composer
import androidx.compose.runtime.remember
import com.arkivanov.decompose.Child
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.FaultyDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.StackAnimation
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.StackAnimator
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.isFront
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.san.kir.core.utils.ManualDI
import com.san.kir.core.utils.viewModel.LocalComponentContext
import kotlin.jvm.internal.Intrinsics

@OptIn(FaultyDecomposeApi::class)
private val defaultAnimations: StackAnimation<NavConfig, NavContainer> =
    stackAnimation(false) { initial, target, direction ->
        if (direction.isFront) frontAnimation(initial.configuration)
        else frontAnimation(target.configuration)
    }

private fun frontAnimation(config: NavConfig): StackAnimator {
    val navAnimation = ManualDI.navAnimation(config)
    return navAnimation ?: EmptyStackAnimator
}

@Composable
fun NavHost(
    componentContext: ComponentContext,
    startConfig: NavConfig,
    stackAnimation: StackAnimation<NavConfig, NavContainer> = defaultAnimations,
) {
    val navHostComponent = remember {
        NavHostComponent(componentContext, startConfig, stackAnimation)
    }
    navHostComponent.Show()
}

@Composable
fun NavHost(
    startConfig: NavConfig,
    stackAnimation: StackAnimation<NavConfig, NavContainer> = defaultAnimations,
) = NavHost(LocalComponentContext.current, startConfig, stackAnimation)

@Composable
fun NavHost(
    componentContext: ComponentContext,
    startConfig: NavConfig,
    animation: StackAnimation<NavConfig, NavContainer>? = null,
    builder: (NavConfig) -> NavComponent<*>?,
) {

    val navHost = remember { NavHostComponent(componentContext, startConfig, animation, builder) }
    navHost.Show()
}


@Composable
fun NavHost(
    startConfig: NavConfig,
    animation: StackAnimation<NavConfig, NavContainer>? = null,
    builder: (NavConfig) -> NavComponent<*>?,
) {

    val componentContext = checkNotNull(LocalComponentContext.current) {
        "No ComponentContext was provided via LocalComponentContext"
    }

    val navHost = remember { NavHostComponent(componentContext, startConfig, animation, builder) }
    navHost.Show()
}

class NavContainer(
    val context: ComponentContext,
    val component: NavComponent<*>?,
)

internal class NavHostComponent(
    componentContext: ComponentContext,
    startConfig: NavConfig,
    private val animation: StackAnimation<NavConfig, NavContainer>? = null,
    private val builder: (NavConfig) -> NavComponent<*>?,
) : ComponentContext by componentContext, NavComponentScope {

    private val navigation = StackNavigation<NavConfig>()
    private val childStack = childStack(
        source = navigation,
        initialConfiguration = startConfig,
        handleBackButton = true,
        childFactory = ::createChild
    )

    private fun createChild(config: NavConfig, componentContext: ComponentContext): NavContainer {
        Timber.d("config -> $config")
        return NavContainer(componentContext, builder.invoke(config))
    }

    @Composable
    override fun replace(config: NavConfig) = rememberLambda { navigation.replaceCurrent(config) }

    override fun simpleAdd(config: NavConfig): () -> Unit = { navigation.push(config) }

    override fun <P1> simpleAdd(config: (P1) -> NavConfig): (P1) -> Unit = { p1 ->
        navigation.push(config.invoke(p1))
    }

    override fun <P1, P2> simpleAdd(config: (P1, P2) -> NavConfig): (P1, P2) -> Unit = { p1, p2 ->
        navigation.push(config.invoke(p1, p2))
    }

    @Composable
    override fun add(config: NavConfig) = rememberLambda { navigation.push(config) }

    @Composable
    override fun <P1> add(config: (P1) -> NavConfig): (P1) -> Unit =
        rememberLambda { p1 -> navigation.push(config.invoke(p1)) }

    @Composable
    override fun <P1, P2> add(config: (P1, P2) -> NavConfig): (P1, P2) -> Unit =
        rememberLambda { p1, p2 -> navigation.push(config.invoke(p1, p2)) }

    @Composable
    override fun back() = remember { { navigation.pop() } }

    @Composable
    fun Show() {
        Children(childStack, animation = animation) { child ->
            CompositionLocalProvider(LocalComponentContext provides child.instance.context) {
                child.instance.component?.apply {
                    Render()
                }
            }
        }
    }
}

