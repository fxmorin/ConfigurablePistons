package ca.fxco.pistonlib.pistonLogic.controller;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.PistonType;

public class FrontPoweredPistonController extends VanillaPistonController {

    public FrontPoweredPistonController(PistonType type) {
        super(type);
    }

    @Override
    public boolean hasNeighborSignal(Level level, BlockPos pos, Direction facing) {
        // Implementation that allows power received through the piston face.
        return level.hasNeighborSignal(pos) || level.pl$hasQuasiNeighborSignal(pos, 1);
    }
}
