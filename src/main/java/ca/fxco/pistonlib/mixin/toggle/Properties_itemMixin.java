package ca.fxco.pistonlib.mixin.toggle;

import ca.fxco.pistonlib.api.toggle.Toggleable;
import ca.fxco.pistonlib.api.toggle.ToggleableProperties;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.BooleanSupplier;

@Mixin(Item.Properties.class)
public class Properties_itemMixin implements ToggleableProperties<Item.Properties>, Toggleable {

    @Unique
    private BooleanSupplier pl$isDisabled = () -> false;

    @Override
    public Item.Properties pl$setDisabled(BooleanSupplier isDisabled) {
        this.pl$isDisabled = isDisabled;
        return (Item.Properties)(Object)this;
    }

    @Override
    public BooleanSupplier pl$getIsDisabled() {
        return this.pl$isDisabled;
    }
}
