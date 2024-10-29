package net.grilledham.iceball.mixin;

import com.mojang.authlib.GameProfile;
import net.grilledham.iceball.entity.BigBouncyBallEntity;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class BigBouncyBallInputsMixin extends AbstractClientPlayerEntity {
	
	@Shadow public Input input;
	
	@Shadow private boolean riding;
	
	public BigBouncyBallInputsMixin(ClientWorld world, GameProfile profile) {
		super(world, profile);
	}
	
	@Inject(method = "tickRiding", at = @At("TAIL"))
	private void updateInputs(CallbackInfo ci) {
		Entity entity = getControllingVehicle();
		if (entity instanceof BigBouncyBallEntity ballEntity) {
			ballEntity.setInputs(this.input.playerInput.forward(), this.input.playerInput.backward(), this.input.playerInput.left(), this.input.playerInput.right());
			this.riding |= this.input.playerInput.left() || this.input.playerInput.right() || this.input.playerInput.forward() || this.input.playerInput.backward();
		}
	}
}
