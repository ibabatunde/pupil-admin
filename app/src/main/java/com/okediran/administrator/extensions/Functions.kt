package com.okediran.administrator.extensions
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.okediran.administrator.data.models.Error
import kotlinx.coroutines.CoroutineExceptionHandler
import java.lang.reflect.Type
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KClass

inline fun <reified T> String.toObject(): T {
    val type = typeToken<T>()
    return Gson().fromJson(this, type)
}

inline fun <reified T> T.toJson(): String {
    val type = typeToken<T>()
    return Gson().toJson(this, type)
}

inline fun <reified T> String.toObject(clazz: Class<T>, deserializer: JsonDeserializer<T>): T {
    val type = typeToken<T>()

    return GsonBuilder().registerTypeAdapter(clazz::class.java, deserializer).create().fromJson(this, type)
}

inline fun <reified T> T.toJson(clazz: Class<T>, serializer: JsonSerializer<T>): String {
    val type = typeToken<T>()
    return GsonBuilder().registerTypeAdapter(clazz::class.java, serializer).create().toJson(this, type)
}

inline fun <reified T> String.toObject(typeAdapterFactory: TypeAdapterFactory): T {
    val type = typeToken<T>()
    return GsonBuilder().registerTypeAdapterFactory(typeAdapterFactory).create().fromJson(this, type)
}

inline fun <reified T> T.toJson(typeAdapterFactory: TypeAdapterFactory): String {
    val type = typeToken<T>()
    return GsonBuilder().registerTypeAdapterFactory(typeAdapterFactory).create().toJson(this, type)
}

class SealedTypeAdapterFactory: TypeAdapterFactory {
    override fun <T : Any> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T> {
        val kclass = Reflection.getOrCreateKotlinClass(type.rawType)
        return if (kclass.sealedSubclasses.any()) {
            SealedClassTypeAdapter<T>(kclass, gson)
        } else
            gson.getDelegateAdapter(this, type)
    }
}
class SealedClassTypeAdapter<T : Any>(val kclass: KClass<Any>, val gson: Gson) : TypeAdapter<T>() {
    override fun read(jsonReader: JsonReader): T? {
        jsonReader.beginObject() //start reading the object
        val nextName = jsonReader.nextName() //get the name on the object
        val innerClass = kclass.sealedSubclasses.firstOrNull {
            it.simpleName!!.contains(nextName)
        }
            ?: throw Exception("$nextName is not found to be a data class of the sealed class ${kclass.qualifiedName}")
        val x = gson.fromJson<T>(jsonReader, innerClass.javaObjectType)
        jsonReader.endObject()
        //if there a static object, actually return that back to ensure equality and such!
        return innerClass.objectInstance as T? ?: x
    }

    override fun write(out: JsonWriter, value: T) {
        val jsonString = gson.toJson(value)
        out.beginObject()
        out.name(value.javaClass.canonicalName.splitToSequence(".").last()).jsonValue(jsonString)
        out.endObject()
    }

}

inline fun <reified T> typeToken(): Type = object : TypeToken<T>() {}.type

fun handleException(error: (message: String) -> Unit) = CoroutineExceptionHandler { _, throwable ->
    val message = ErrorHandler.parse<Error>(throwable)
    error(message)
    throwable.printStackTrace()
}