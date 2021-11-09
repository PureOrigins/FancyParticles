package it.pureorigins.fancyparticles.advancements

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import it.pureorigins.fancyparticles.FancyParticles
import mixin.AdvancementRewardsAccessor
import net.minecraft.advancement.AdvancementRewards
import net.minecraft.server.function.CommandFunction
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.JsonHelper

class ParticlesAdvancementRewards(private val otherRewards: AdvancementRewards, private val particleIds: IntArray) :
    AdvancementRewards(otherRewards.experience, otherRewards.loot, otherRewards.recipes, otherRewards.function) {
    override fun apply(player: ServerPlayerEntity) {
        otherRewards.apply(player)
        particleIds.forEach { FancyParticles.addParticle(player.uuid, it) }
    }

    override fun toJson(): JsonElement {
        var json = otherRewards.toJson()
        if (json == JsonNull.INSTANCE) json = JsonObject()
        json as JsonObject
        json.add("particles", JsonArray().apply { particleIds.forEach { add(it) } })
        return json
    }

    companion object {
        @JvmStatic
        fun fromJson(otherRewards: AdvancementRewards, json: JsonObject): ParticlesAdvancementRewards {
            val array = JsonHelper.getArray(json, "particles", JsonArray())!!
            val particleIds = IntArray(array.size())
            array.forEachIndexed { i, element ->
                particleIds[i] = JsonHelper.asInt(element, "particles[$i]")
            }
            return ParticlesAdvancementRewards(otherRewards, particleIds)
        }
    }
}

val AdvancementRewards.experience: Int get() = (this as AdvancementRewardsAccessor).experience
val AdvancementRewards.loot: Array<Identifier> get() = (this as AdvancementRewardsAccessor).loot
val AdvancementRewards.function: CommandFunction.LazyContainer
    get() = (this as AdvancementRewardsAccessor).function