package ca.fxco.pistonlib.pistonLogic.controller;

import ca.fxco.pistonlib.helpers.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.PistonType;

public class StalePistonController extends VanillaPistonController {

    public StalePistonController(PistonType type) {
        super(type);
    }

    @Override
    public boolean hasNeighborSignal(Level level, BlockPos pos, Direction facing) {
        return Utils.hasNeighborSignalExceptFromFacing(level, pos, facing);
    }
}
