package ca.fxco.pistonlib.mixin.toggle;

import ca.fxco.pistonlib.api.item.PLItem;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BooleanSupplier;

import static ca.fxco.pistonlib.PistonLib.NEVER_ENABLED_SET;

@Mixin(Item.class)
public class Item_toggleMixin implements PLItem {
    
    @Unique
    private BooleanSupplier pl$isDisabled;

    @Inject(
            method = "<init>",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/item/Item$Properties;" +
                            "requiredFeatures:Lnet/minecraft/world/flag/FeatureFlagSet;",
                    shift = At.Shift.AFTER
            )
    )
    private void pl$onInit(Item.Properties properties, CallbackInfo ci) {
        this.pl$isDisabled = properties.pl$getIsDisabled();
    }

    @Override
    public BooleanSupplier pl$getIsDisabled() {
        return this.pl$isDisabled;
    }

    @Inject(
            method = "requiredFeatures",
            at = @At("HEAD"),
            cancellable = true
    )
    private void pl$disableItem(CallbackInfoReturnable<FeatureFlagSet> cir) {
        if (this.pl$isDisabled.getAsBoolean()) {
            cir.setReturnValue(NEVER_ENABLED_SET);
        }
    }
}
