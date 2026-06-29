package net.grilledham.iceball.mixin;

import com.mojang.authlib.GameProfile;
import net.grilledham.iceball.entity.BigBouncyBallEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
//~ if >= 1.21.11 'Input' -> 'ClientInput'
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class BigBouncyBallInputsMixin extends AbstractClientPlayer {
	
	//~ if >= 1.21.11 'Input' -> 'ClientInput'
	@Shadow public ClientInput input;
	
	@Shadow private boolean handsBusy;
	
	public BigBouncyBallInputsMixin(ClientLevel world, GameProfile profile) {
		super(world, profile);
	}
	
	@Inject(method = "rideTick", at = @At("TAIL"))
	private void updateInputs(CallbackInfo ci) {
		Entity entity = getControlledVehicle();
		if (entity instanceof BigBouncyBallEntity ballEntity) {
			//? if >= 1.21.11 {
			ballEntity.setInputs(this.input.keyPresses.forward(), this.input.keyPresses.backward(), this.input.keyPresses.left(), this.input.keyPresses.right());
			this.handsBusy |= this.input.keyPresses.left() || this.input.keyPresses.right() || this.input.keyPresses.forward() || this.input.keyPresses.backward();
			//?} else {
			/*ballEntity.setInputs(this.input.up, this.input.down, this.input.left, this.input.right);
			this.handsBusy |= this.input.left || this.input.right || this.input.up || this.input.down;
			*///?}
		}
	}
}
