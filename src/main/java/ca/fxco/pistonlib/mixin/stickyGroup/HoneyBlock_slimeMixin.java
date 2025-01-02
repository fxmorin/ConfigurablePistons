package ca.fxco.pistonlib.mixin.stickyGroup;

import ca.fxco.api.pistonlib.pistonLogic.sticky.StickyGroup;
import ca.fxco.api.pistonlib.pistonLogic.sticky.StickyGroups;
import ca.fxco.api.pistonlib.block.PLBlockBehaviour;
import net.minecraft.world.level.block.HoneyBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(HoneyBlock.class)
public class HoneyBlock_slimeMixin implements PLBlockBehaviour {

    @Override
    public StickyGroup pl$getStickyGroup() {
        return StickyGroups.HONEY;
    }
}
