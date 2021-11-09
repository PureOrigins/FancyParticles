package mixin;

import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.server.function.CommandFunction.LazyContainer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AdvancementRewards.class)
public interface AdvancementRewardsAccessor {
    @Accessor
    int getExperience();

    @Accessor
    Identifier[] getLoot();

    @Accessor
    LazyContainer getFunction();
}