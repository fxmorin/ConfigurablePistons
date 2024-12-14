package ca.fxco.pistonlib.blocks.pistons;

import ca.fxco.api.pistonlib.pistonLogic.families.PistonFamily;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.PistonType;

public class FrontPoweredPistonBaseBlock extends BasicPistonBaseBlock {

	public FrontPoweredPistonBaseBlock(PistonFamily family, PistonType type) {
        super(family, type);
    }

    @Override
    public boolean hasNeighborSignal(Level level, BlockPos pos, Direction facing) {
        // Implementation that allows power received through the piston face.
        return level.hasNeighborSignal(pos) || level.pl$hasQuasiNeighborSignal(pos, 1);
    }
}
