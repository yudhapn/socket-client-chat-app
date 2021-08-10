package id.ypran.authentication

import id.ypran.authentication.presentation.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.loadKoinModules
import org.koin.dsl.module

fun injectFeature() = loadFeature

private val loadFeature by lazy {
    loadKoinModules(authComponent)
}

val viewModelModule = module {
    viewModel { LoginViewModel() }
}

val authComponent = listOf(viewModelModule)