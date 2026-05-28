package com.imyvm.adventure.mixin;

import com.imyvm.adventure.api.event.TradeEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantResultSlot.class)
public class MerchantResultSlotMixin {
	@Shadow @Final private Merchant merchant;

	@Inject(at = @At("HEAD"), method = "onTake")
	private void adventure$onTake(Player player, ItemStack stack, CallbackInfo ci) {
		if (player instanceof ServerPlayer sp) {
			TradeEvents.ON_TRADE_COMPLETED.invoker().onTradeCompleted(sp, stack, this.merchant);
		}
	}
}
