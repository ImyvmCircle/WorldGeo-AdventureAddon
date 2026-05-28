package com.imyvm.adventure.mixin;

import com.imyvm.adventure.api.event.BrushEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrushableBlockEntity.class)
public abstract class BrushableBlockEntityMixin {
    @Inject(method = "brushingCompleted", at = @At("HEAD"))
    private void adventure$onBrushingCompleted(ServerLevel level, LivingEntity user, ItemStack stack, CallbackInfo ci) {
        if (user instanceof ServerPlayer player) {
            BrushableBlockEntity self = (BrushableBlockEntity) (Object) this;
            BrushEvents.ON_BRUSHING_COMPLETED.invoker().onBrushingCompleted(player, level, self);
        }
    }
}
