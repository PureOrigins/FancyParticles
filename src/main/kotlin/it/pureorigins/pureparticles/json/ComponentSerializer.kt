package it.pureorigins.pureparticles.json

import com.google.gson.*
import it.pureorigins.common.unsafeGetStaticField
import it.pureorigins.common.unsafeSetStaticField
import it.pureorigins.pureparticles.ParticleComponent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.util.GsonHelper
import java.lang.reflect.Type
import java.util.*

object ComponentSerializer : Component.Serializer() {
    override fun deserialize(element: JsonElement, type: Type, context: JsonDeserializationContext): MutableComponent {
        if (element.isJsonObject) {
            val json = element.asJsonObject
            var text: MutableComponent? = null
            if (json.has("particle")) {
                val titleName = GsonHelper.getAsString(json, "particle")
                text = ParticleComponent(titleName)
            }
            if (text != null) {
                if (json.has("extra")) {
                    val jsonArray = GsonHelper.getAsJsonArray(json, "extra")
                    if (jsonArray.size() <= 0) {
                        throw JsonParseException("Unexpected empty array of components")
                    }
                    jsonArray.forEach {
                        text.append(context.deserialize(it, type) as Component)
                    }
                }
                text.style = context.deserialize(json, Style::class.java)
                return text
            }
        }
        return super.deserialize(element, type, context)
    }
    
    override fun serialize(component: Component, type: Type, context: JsonSerializationContext): JsonElement {
        val json = super.serialize(component, type, context)
        if (component is ParticleComponent) {
            json as JsonObject
            json.addProperty("particle", component.particleName)
        }
        return json
    }
    
    fun register() {
        val field = Component.Serializer::class.java.declaredFields.first { it.type == Gson::class.java }
        val oldGson = unsafeGetStaticField(field) as Gson
        val builder = oldGson.newBuilder()
        builder.registerTypeHierarchyAdapter(Component::class.java, ComponentSerializer)
        unsafeSetStaticField(field, builder.create())
    }
}