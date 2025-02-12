package ca.fxco.pistonlib.mixin.toggle;

import ca.fxco.pistonlib.api.block.PLBlockProperties;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.BooleanSupplier;

@Mixin(BlockBehaviour.Properties.class)
public class Properties_blockMixin implements PLBlockProperties {

    @Unique
    private BooleanSupplier pl$isDisabled = () -> false;

    @Override
    public BlockBehaviour.Properties pl$setDisabled(BooleanSupplier isDisabled) {
        this.pl$isDisabled = isDisabled;
        return (BlockBehaviour.Properties)(Object)this;
    }

    @Override
    public BooleanSupplier pl$getIsDisabled() {
        return this.pl$isDisabled;
    }
}
