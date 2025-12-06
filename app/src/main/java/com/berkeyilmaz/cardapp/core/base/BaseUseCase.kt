package com.berkeyilmaz.cardapp.core.base

abstract class BaseUseCase<in Params, out Type> {
    abstract suspend operator fun invoke(params: Params): Result<Type>
}