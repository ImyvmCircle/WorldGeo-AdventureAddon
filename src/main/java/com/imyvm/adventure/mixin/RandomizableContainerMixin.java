package com.imyvm.adventure.mixin;

import com.imyvm.adventure.api.event.ChestEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RandomizableContainer.class)
public interface RandomizableContainerMixin {
	@Inject(at = @At("HEAD"), method = "unpackLootTable")
	default void adventure$onUnpackLootTable(Player player, CallbackInfo ci) {
		if (!(player instanceof ServerPlayer sp)) return;
		RandomizableContainer container = (RandomizableContainer) (Object) this;
		if (container.getLootTable() == null) return;
		if (container.getLevel() == null || container.getLevel().getServer() == null) return;
		ChestEvents.ON_CHEST_LOOT_UNPACKED.invoker().onChestLootUnpacked(sp, container);
	}
}
