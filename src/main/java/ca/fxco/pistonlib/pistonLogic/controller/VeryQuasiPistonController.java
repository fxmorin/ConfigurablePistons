package ca.fxco.pistonlib.pistonLogic.controller;

import ca.fxco.pistonlib.helpers.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.PistonType;

public class VeryQuasiPistonController extends VanillaPistonController {

    private final int quasiStrength; // Use 1 to replicate vanilla behaviour

    public VeryQuasiPistonController(PistonType type, int quasiStrength) {
        super(type);

        this.quasiStrength = quasiStrength;
    }

    @Override
    public boolean hasNeighborSignal(Level level, BlockPos pos, Direction facing) {
        return Utils.hasNeighborSignalExceptFromFacing(level, pos, facing) ||
                level.pl$hasQuasiNeighborSignalColumn(pos, this.quasiStrength);
    }
}
