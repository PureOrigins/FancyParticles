package it.pureorigins.fancyparticles.mixin;

import com.google.gson.JsonObject;
import it.pureorigins.fancyparticles.advancements.ParticlesAdvancementRewards;
import net.minecraft.advancement.AdvancementRewards;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AdvancementRewards.class)
public class AdvancementRewardsMixin {
    @Inject(method = "fromJson", at = @At("RETURN"), cancellable = true)
    private static void fromJson(JsonObject json, CallbackInfoReturnable<AdvancementRewards> callback) {
        if (json.has("particles")) {
            callback.setReturnValue(ParticlesAdvancementRewards.fromJson(callback.getReturnValue(), json));
        }
    }
}
