/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.js

internal fun equals(obj1: dynamic, obj2: dynamic): Boolean {
    if (obj1 == null) {
        return obj2 == null
    }
    if (obj2 == null) {
        return false
    }

    if (jsTypeOf(obj1) == "object" && jsTypeOf(obj1.equals) == "function") {
        return (obj1.equals)(obj2)
    }

    if (obj1 !== obj1) {
        return obj2 !== obj2
    }

    if (jsTypeOf(obj1) == "number" && jsTypeOf(obj2) == "number") {
        return obj1 === obj2 && (obj1 !== 0 || 1.asDynamic() / obj1 === 1.asDynamic() / obj2)
    }
    return obj1 === obj2
}

internal fun toString(o: dynamic): String = when {
    o == null -> "null"
    isArrayish(o) -> "[...]"

    else -> (o.toString)().unsafeCast<String>()
}

internal fun anyToString(o: dynamic): String = js("Object").prototype.toString.call(o)

private fun hasOwnPrototypeProperty(o: Any, name: String): Boolean {
    return JsObject.getPrototypeOf(o).hasOwnProperty(name).unsafeCast<Boolean>()
}

internal fun hashCode(obj: dynamic): Int {
    if (obj == null)
        return 0

    return when (jsTypeOf(obj)) {
        "object" -> if ("function" === jsTypeOf(obj.hashCode)) (obj.hashCode)() else getObjectHashCode(obj)
        "function" -> getObjectHashCode(obj)
        "number" -> getNumberHashCode(obj)
        "boolean" -> getBooleanHashCode(obj.unsafeCast<Boolean>())
        else -> getStringHashCode(js("String")(obj))
    }
}

internal fun getBooleanHashCode(value: Boolean): Int {
    return if (value) 1231 else 1237
}

private const val POW_2_32 = 4294967296.0
private const val OBJECT_HASH_CODE_PROPERTY_NAME = "kotlinHashCodeValue$"

internal fun getObjectHashCode(obj: dynamic): Int {
    if (!jsIn(OBJECT_HASH_CODE_PROPERTY_NAME, obj)) {
        var hash = jsBitwiseOr(js("Math").random() * POW_2_32, 0) // Make 32-bit singed integer.
        var descriptor = js("new Object()")
        descriptor.value = hash
        descriptor.enumerable = false
        js("Object").defineProperty(obj, OBJECT_HASH_CODE_PROPERTY_NAME, descriptor)
    }
    return obj[OBJECT_HASH_CODE_PROPERTY_NAME].unsafeCast<Int>();
}

internal fun getStringHashCode(str: String): Int {
    var hash = 0
    val length: Int = str.length  // TODO: Implement WString.length
    for (i in 0..length-1) {
        val code: Int = str.asDynamic().charCodeAt(i)
        hash = hash * 31 + code
    }
    return hash
}

internal fun identityHashCode(obj: Any?): Int = getObjectHashCode(obj)

internal fun captureStack(instance: Throwable, constructorFunction: Any) {
    if (js("Error").captureStackTrace != null) {
        js("Error").captureStackTrace(instance, constructorFunction)
    } else {
        instance.asDynamic().stack = js("new Error()").stack
    }
}

internal fun newThrowable(message: String?, cause: Throwable?): Throwable {
    val throwable = js("new Error()")
    throwable.message = if (isUndefined(message)) {
        if (isUndefined(cause)) message else cause?.toString() ?: VOID
    } else message ?: VOID
    throwable.cause = cause
    throwable.name = "Throwable"
    return throwable.unsafeCast<Throwable>()
}

internal fun extendThrowable(this_: dynamic, message: String?, cause: Throwable?) {
    js("Error").call(this_)
    setPropertiesToThrowableInstance(this_, message, cause)
}

internal fun setPropertiesToThrowableInstance(this_: dynamic, message: String?, cause: Throwable?) {
    val errorInfo = calculateErrorInfo(JsObject.getPrototypeOf(this_))
    if ((errorInfo and 0x1) == 0) {
        @Suppress("IfThenToElvis")
        this_.message = if (message == null) {
            @Suppress("SENSELESS_COMPARISON")
            if (message !== null) {
                // undefined
                cause?.toString() ?: VOID
            } else {
                // real null
                VOID
            }
        } else message
    }
    if ((errorInfo and 0x2) == 0) {
        this_.cause = cause
    }
    this_.name = JsObject.getPrototypeOf(this_).constructor.name
}

@JsName("Object")
internal external class JsObject {
    companion object {
        fun getPrototypeOf(obj: Any?): dynamic
    }
}

// Note: once some error-compilation design happened consider to distinguish a special exception for error-code.
internal fun errorCode(description: String): Nothing {
    throw IllegalStateException(description)
}

@Suppress("SENSELESS_COMPARISON")
internal fun isUndefined(value: dynamic): Boolean = value === VOID

internal fun <T, R> boxIntrinsic(@Suppress("UNUSED_PARAMETER") x: T): R = error("Should be lowered")
internal fun <T, R> unboxIntrinsic(@Suppress("UNUSED_PARAMETER") x: T): R = error("Should be lowered")

@Suppress("UNUSED_PARAMETER")
internal fun protoOf(constructor: Any) =
    js("constructor.prototype")

@Suppress("UNUSED_PARAMETER")
internal fun <T> objectCreate(proto: T?) =
    js("Object.create(proto)")

@Suppress("UNUSED_PARAMETER")
internal fun createThis(ctor: Ctor, box: dynamic): dynamic {
    val self = js("Object.create(ctor.prototype)")
    boxApply(self, box)
    return self
}

@Suppress("UNUSED_PARAMETER")
internal fun boxApply(self: dynamic, box: dynamic) {
    if (box !== VOID) js("Object.assign(self, box)")
}

@OptIn(JsIntrinsic::class)
@Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE", "REIFIED_TYPE_PARAMETER_NO_INLINE")
internal fun <reified T : Any> createExternalThis(
    ctor: JsClass<T>,
    superExternalCtor: JsClass<T>,
    parameters: Array<Any?>,
    box: dynamic
): T {
    val selfCtor = if (box === VOID) {
        ctor
    } else {
        val newCtor: dynamic = jsNewAnonymousClass(ctor)
        js("Object.assign(newCtor.prototype, box)")
        newCtor.constructor = ctor
        newCtor
    }
    return js("Reflect.construct(superExternalCtor, parameters, selfCtor)")
}

@Suppress("UNUSED_PARAMETER")
internal fun defineProp(obj: Any, name: String, getter: Any?, setter: Any?) =
    js("Object.defineProperty(obj, name, { configurable: true, get: getter, set: setter })")