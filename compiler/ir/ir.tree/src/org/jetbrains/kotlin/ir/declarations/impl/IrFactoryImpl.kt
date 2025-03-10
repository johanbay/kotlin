/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.declarations.impl

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrExpressionBody
import org.jetbrains.kotlin.ir.expressions.impl.IrBlockBodyImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrExpressionBodyImpl
import org.jetbrains.kotlin.ir.symbols.*
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.serialization.deserialization.descriptors.DeserializedContainerSource
import org.jetbrains.kotlin.types.Variance

object IrFactoryImpl : AbstractIrFactoryImpl() {
    override val stageController: StageController = StageController()
}

abstract class AbstractIrFactoryImpl : IrFactory {

    override fun createAnonymousInitializer(
        startOffset: Int,
        endOffset: Int,
        origin: IrDeclarationOrigin,
        symbol: IrAnonymousInitializerSymbol,
        isStatic: Boolean,
    ): IrAnonymousInitializer =
        IrAnonymousInitializerImpl(startOffset, endOffset, origin, symbol, isStatic, factory = this)

    override fun createClass(
        startOffset: Int,
        endOffset: Int,
        origin: IrDeclarationOrigin,
        symbol: IrClassSymbol,
        name: Name,
        kind: ClassKind,
        visibility: DescriptorVisibility,
        modality: Modality,
        isCompanion: Boolean,
        isInner: Boolean,
        isData: Boolean,
        isExternal: Boolean,
        isValue: Boolean,
        isExpect: Boolean,
        isFun: Boolean,
        source: SourceElement,
    ): IrClass =
        IrClassImpl(
            startOffset, endOffset, origin, symbol, name, kind, visibility, modality,
            isCompanion, isInner, isData, isExternal, isValue, isExpect, isFun, source,
            factory = this
        )

    override fun createConstructor(
        startOffset: Int,
        endOffset: Int,
        origin: IrDeclarationOrigin,
        symbol: IrConstructorSymbol,
        name: Name,
        visibility: DescriptorVisibility,
        returnType: IrType,
        isInline: Boolean,
        isExternal: Boolean,
        isPrimary: Boolean,
        isExpect: Boolean,
        containerSource: DeserializedContainerSource?,
    ): IrConstructor =
        IrConstructorImpl(
            startOffset, endOffset, origin, symbol, name, visibility, returnType, isInline, isExternal, isPrimary, isExpect,
            containerSource, factory = this
        )

    override fun createEnumEntry(
        startOffset: Int,
        endOffset: Int,
        origin: IrDeclarationOrigin,
        name: Name,
        symbol: IrEnumEntrySymbol,
    ): IrEnumEntry =
        IrEnumEntryImpl(startOffset, endOffset, origin, symbol, name, factory = this)

    override fun createErrorDeclaration(
        startOffset: Int,
        endOffset: Int,
        descriptor: DeclarationDescriptor?,
    ): IrErrorDeclaration =
        IrErrorDeclarationImpl(startOffset, endOffset, descriptor, factory = this)

    override fun createField(
        startOffset: Int,
        endOffset: Int,
        origin: IrDeclarationOrigin,
        symbol: IrFieldSymbol,
        name: Name,
        type: IrType,
        visibility: DescriptorVisibility,
        isFinal: Boolean,
        isExternal: Boolean,
        isStatic: Boolean,
    ): IrField =
        IrFieldImpl(startOffset, endOffset, origin, symbol, name, type, visibility, isFinal, isExternal, isStatic, factory = this)

    override fun createFunction(
        startOffset: Int,
        endOffset: Int,
        origin: IrDeclarationOrigin,
        symbol: IrSimpleFunctionSymbol,
        name: Name,
        visibility: DescriptorVisibility,
        modality: Modality,
        returnType: IrType,
        isInline: Boolean,
        isExternal: Boolean,
        isTailrec: Boolean,
        isSuspend: Boolean,
        isOperator: Boolean,
        isInfix: Boolean,
        isExpect: Boolean,
        isFakeOverride: Boolean,
        containerSource: DeserializedContainerSource?,
    ): IrSimpleFunction =
        IrFunctionImpl(
            startOffset, endOffset, origin, symbol, name, visibility, modality, returnType,
            isInline, isExternal, isTailrec, isSuspend, isOperator, isInfix, isExpect, isFakeOverride,
            containerSource, factory = this
        )

    override fun createFunctionWithLateBinding(
        startOffset: Int,
        endOffset: Int,
        origin: IrDeclarationOrigin,
        name: Name,
        visibility: DescriptorVisibility,
        modality: Modality,
        returnType: IrType,
        isInline: Boolean,
        isExternal: Boolean,
        isTailrec: Boolean,
        isSuspend: Boolean,
        isOperator: Boolean,
        isInfix: Boolean,
        isExpect: Boolean,
    ): IrSimpleFunction =
        IrFunctionWithLateBindingImpl(
            startOffset, endOffset, origin, name, visibility, modality, returnType,
            isInline, isExternal, isTailrec, isSuspend, isOperator, isInfix, isExpect,
            factory = this
        )

    override fun createLocalDelegatedProperty(
        startOffset: Int,
        endOffset: Int,
        origin: IrDeclarationOrigin,
        symbol: IrLocalDelegatedPropertySymbol,
        name: Name,
        type: IrType,
        isVar: Boolean,
    ): IrLocalDelegatedProperty =
        IrLocalDelegatedPropertyImpl(startOffset, endOffset, origin, symbol, name, type, isVar, factory = this)

    override fun createProperty(
        startOffset: Int,
        endOffset: Int,
        origin: IrDeclarationOrigin,
        symbol: IrPropertySymbol,
        name: Name,
        visibility: DescriptorVisibility,
        modality: Modality,
        isVar: Boolean,
        isConst: Boolean,
        isLateinit: Boolean,
        isDelegated: Boolean,
        isExternal: Boolean,
        isExpect: Boolean,
        isFakeOverride: Boolean,
        containerSource: DeserializedContainerSource?,
    ): IrProperty =
        IrPropertyImpl(
            startOffset, endOffset, origin, symbol, name, visibility, modality,
            isVar, isConst, isLateinit, isDelegated, isExternal, isExpect, isFakeOverride,
            containerSource, factory = this
        )

    override fun createPropertyWithLateBinding(
        startOffset: Int,
        endOffset: Int,
        origin: IrDeclarationOrigin,
        name: Name,
        visibility: DescriptorVisibility,
        modality: Modality,
        isVar: Boolean,
        isConst: Boolean,
        isLateinit: Boolean,
        isDelegated: Boolean,
        isExternal: Boolean,
        isExpect: Boolean,
    ): IrProperty =
        IrPropertyWithLateBindingImpl(
            startOffset, endOffset, origin, name, visibility, modality,
            isVar, isConst, isLateinit, isDelegated, isExternal, isExpect,
            factory = this
        )

    override fun createTypeAlias(
        startOffset: Int,
        endOffset: Int,
        symbol: IrTypeAliasSymbol,
        name: Name,
        visibility: DescriptorVisibility,
        expandedType: IrType,
        isActual: Boolean,
        origin: IrDeclarationOrigin,
    ): IrTypeAlias =
        IrTypeAliasImpl(startOffset, endOffset, symbol, name, visibility, expandedType, isActual, origin, factory = this)

    override fun createTypeParameter(
        startOffset: Int,
        endOffset: Int,
        origin: IrDeclarationOrigin,
        symbol: IrTypeParameterSymbol,
        name: Name,
        index: Int,
        isReified: Boolean,
        variance: Variance,
    ): IrTypeParameter =
        IrTypeParameterImpl(startOffset, endOffset, origin, symbol, name, index, isReified, variance, factory = this)

    override fun createValueParameter(
        startOffset: Int,
        endOffset: Int,
        origin: IrDeclarationOrigin,
        symbol: IrValueParameterSymbol,
        name: Name,
        index: Int,
        type: IrType,
        varargElementType: IrType?,
        isCrossinline: Boolean,
        isNoinline: Boolean,
        isHidden: Boolean,
        isAssignable: Boolean,
    ): IrValueParameter =
        IrValueParameterImpl(
            startOffset, endOffset, origin, symbol, name, index, type, varargElementType,
            isCrossinline, isNoinline, isHidden, isAssignable, factory = this
        )

    override fun createExpressionBody(
        startOffset: Int,
        endOffset: Int,
        initializer: IrExpressionBody.() -> Unit,
    ): IrExpressionBody =
        IrExpressionBodyImpl(startOffset, endOffset, initializer)

    override fun createExpressionBody(
        startOffset: Int,
        endOffset: Int,
        expression: IrExpression,
    ): IrExpressionBody =
        IrExpressionBodyImpl(startOffset, endOffset, expression)

    override fun createExpressionBody(
        expression: IrExpression,
    ): IrExpressionBody =
        IrExpressionBodyImpl(expression)

    override fun createBlockBody(
        startOffset: Int,
        endOffset: Int,
    ): IrBlockBody =
        IrBlockBodyImpl(startOffset, endOffset)

    override fun createBlockBody(
        startOffset: Int,
        endOffset: Int,
        statements: List<IrStatement>,
    ): IrBlockBody =
        IrBlockBodyImpl(startOffset, endOffset, statements)

    override fun createBlockBody(
        startOffset: Int,
        endOffset: Int,
        initializer: IrBlockBody.() -> Unit,
    ): IrBlockBody =
        IrBlockBodyImpl(startOffset, endOffset, initializer)
}