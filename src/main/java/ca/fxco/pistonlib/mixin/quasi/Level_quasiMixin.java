package ca.fxco.pistonlib.mixin.quasi;

import ca.fxco.pistonlib.api.level.PLLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Redstone;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashSet;
import java.util.Set;

import static ca.fxco.pistonlib.PistonLib.DIRECTIONS;

@Mixin(Level.class)
public class Level_quasiMixin implements PLLevel {

    @Shadow
    public BlockState getBlockState(BlockPos pos) { return null; }

    /**
     * BlockPos is the position that the check happens at
     */
    @Override
    public int pl$getDirectQuasiSignalTo(BlockPos pos, int d) {
        int i = Redstone.SIGNAL_NONE;
        if ((i = Math.max(i, pl$getDirectQuasiSignal(pos.below(), Direction.DOWN, d))) >= Redstone.SIGNAL_MAX) {
            return i;
        }
        if ((i = Math.max(i, pl$getDirectQuasiSignal(pos.above(), Direction.UP, d))) >= Redstone.SIGNAL_MAX) {
            return i;
        }
        if ((i = Math.max(i, pl$getDirectQuasiSignal(pos.north(), Direction.NORTH, d))) >= Redstone.SIGNAL_MAX) {
            return i;
        }
        if ((i = Math.max(i, pl$getDirectQuasiSignal(pos.south(), Direction.SOUTH, d))) >= Redstone.SIGNAL_MAX) {
            return i;
        }
        if ((i = Math.max(i, pl$getDirectQuasiSignal(pos.west(), Direction.WEST, d))) >= Redstone.SIGNAL_MAX) {
            return i;
        }
        return Math.max(i, pl$getDirectQuasiSignal(pos.east(), Direction.EAST, d));
    }

    /**
     * BlockPos is the position that the check happens at
     */
    @Override
    public boolean pl$hasDirectQuasiSignalTo(BlockPos pos, int dist) {
        return pl$getDirectQuasiSignal(pos.below(), Direction.DOWN, dist) > 0 ||
                pl$getDirectQuasiSignal(pos.above(), Direction.UP, dist) > 0 ||
                pl$getDirectQuasiSignal(pos.north(), Direction.NORTH, dist) > 0 ||
                pl$getDirectQuasiSignal(pos.south(), Direction.SOUTH, dist) > 0 ||
                pl$getDirectQuasiSignal(pos.west(), Direction.WEST, dist) > 0 ||
                pl$getDirectQuasiSignal(pos.east(), Direction.EAST, dist) > 0;
    }

    /**
     * BlockPos is the block position of the block doing the check, not the location that the check happens at
     */
    @Override
    public int pl$getStrongestQuasiNeighborSignal(BlockPos pos, int dist) {
        pos = pos.above(dist);
        int strongest = Redstone.SIGNAL_NONE;
        for (Direction dir : DIRECTIONS) {
            int strength = this.pl$getQuasiSignal(pos.relative(dir), dir, dist);
            if (strength >= Redstone.SIGNAL_MAX) {
                return Redstone.SIGNAL_MAX;
            } else if (strength > strongest) {
                strongest = strength;
            }
        }
        return strongest;
    }

    /**
     * BlockPos is the block position of the block doing the check, not the location that the check happens at
     */
    @Override
    public int pl$getStrongestQuasiNeighborSignal(BlockPos pos, Direction dir, int dist) {
        pos = pos.relative(dir, dist);
        int strongest = Redstone.SIGNAL_NONE;
        for (Direction neighborDir : DIRECTIONS) {
            int strength = this.pl$getQuasiSignal(pos.relative(neighborDir), neighborDir, dist);
            if (strength >= Redstone.SIGNAL_MAX) {
                return Redstone.SIGNAL_MAX;
            } else if (strength > strongest) {
                strongest = strength;
            }
        }
        return strongest;
    }

    /**
     * BlockPos is the position that the check happens at
     */
    @Override
    public int pl$getQuasiSignal(BlockPos pos, Direction dir, int dist) {
        BlockState state = this.getBlockState(pos);
        int i = state.pl$getQuasiSignal((Level)(Object)this, pos, dir, dist);
        return state.pl$isQuasiConductor((Level)(Object)this, pos) ?
                Math.max(i, this.pl$getDirectQuasiSignalTo(pos, dist)) : i;
    }

    /**
     * BlockPos is the block position of the block doing the check, not the location that the check happens at
     */
    @Override
    public boolean pl$hasQuasiNeighborSignal(BlockPos pos, int dist) {
        pos = pos.above(dist);
        return this.pl$hasQuasiSignal(pos.below(), Direction.DOWN, dist) ||
                this.pl$hasQuasiSignal(pos.above(), Direction.UP, dist) ||
                this.pl$hasQuasiSignal(pos.north(), Direction.NORTH, dist) ||
                this.pl$hasQuasiSignal(pos.south(), Direction.SOUTH, dist) ||
                this.pl$hasQuasiSignal(pos.west(), Direction.WEST, dist) ||
                this.pl$hasQuasiSignal(pos.east(), Direction.EAST, dist);
    }

    /**
     * BlockPos is the block position of the block doing the check, not the location that the check happens at
     */
    @Override
    public boolean pl$hasQuasiNeighborSignal(BlockPos pos, Direction dir, int dist) {
        pos = pos.relative(dir, dist);
        return this.pl$hasQuasiSignal(pos.below(), Direction.DOWN, dist) ||
                this.pl$hasQuasiSignal(pos.above(), Direction.UP, dist) ||
                this.pl$hasQuasiSignal(pos.north(), Direction.NORTH, dist) ||
                this.pl$hasQuasiSignal(pos.south(), Direction.SOUTH, dist) ||
                this.pl$hasQuasiSignal(pos.west(), Direction.WEST, dist) ||
                this.pl$hasQuasiSignal(pos.east(), Direction.EAST, dist);
    }

    /**
     * BlockPos is the position that the check happens at
     */
    @Override
    public boolean pl$hasQuasiSignal(BlockPos pos, Direction dir, int dist) {
        BlockState state = this.getBlockState(pos);
        return (state.pl$getQuasiSignal((Level)(Object)this, pos, dir, dist) > Redstone.SIGNAL_NONE ||
                (state.pl$isQuasiConductor((Level)(Object)this, pos) && this.pl$hasDirectQuasiSignalTo(pos, dist)));
    }

    /**
     * BlockPos is the position that the check happens at
     */
    @Override
    public int pl$getDirectQuasiSignal(BlockPos pos, Direction dir, int dist) {
        return this.getBlockState(pos).pl$getDirectQuasiSignal((Level)(Object)this, pos, dir, dist);
    }

    private boolean hasQuasiNeighborSignalOptimized(BlockPos blockPos, int dist) {
        blockPos = blockPos.above(dist);
        return ((dist < 0 || dist == 2) && this.pl$hasQuasiSignal(blockPos.below(), Direction.DOWN, dist)) ||
                ((dist > 0 || dist == -2) && this.pl$hasQuasiSignal(blockPos.above(), Direction.UP, dist)) ||
                this.pl$hasQuasiSignal(blockPos.north(), Direction.NORTH, dist) ||
                this.pl$hasQuasiSignal(blockPos.south(), Direction.SOUTH, dist) ||
                this.pl$hasQuasiSignal(blockPos.west(), Direction.WEST, dist) ||
                this.pl$hasQuasiSignal(blockPos.east(), Direction.EAST, dist);
    }

    private boolean hasQuasiNeighborSignalOptimized(BlockPos blockPos, Direction dir, int dist) {
        blockPos = blockPos.relative(dir, dist);
        Direction dirOpp = dir.getOpposite();
        for (Direction direction : DIRECTIONS) {
            if (direction == dir) {
                if ((dist > 0 || dist == -2) && this.pl$hasQuasiSignal(blockPos.relative(direction), direction, dist)) {
                    return true;
                }
            } else if (direction == dirOpp) {
                if ((dist < 0 || dist == 2) && this.pl$hasQuasiSignal(blockPos.relative(direction), direction, dist)) {
                    return true;
                }
            } else if (this.pl$hasQuasiSignal(blockPos.relative(direction), direction, dist)) {
                return true;
            }
        }
        return false;
    }

    /**
     * BlockPos is the block position of the block doing the check, not the location that the check happens at.
     */
    @Override
    public boolean pl$hasQuasiNeighborSignalColumn(BlockPos pos, int dist) {
        if (dist < 0) {
            for (int i = -1; i >= dist; i--) {
                if (hasQuasiNeighborSignalOptimized(pos, i)) {
                    return true;
                }
            }
        } else {
            for (int i = 1; i <= dist; i++) {
                if (hasQuasiNeighborSignalOptimized(pos, i)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * BlockPos is the block position of the block doing the check, not the location that the check happens at.
     */
    @Override
    public boolean pl$hasQuasiNeighborSignalColumn(BlockPos pos, Direction dir, int dist) {
        if (dist < 0) {
            for (int i = -1; i >= dist; i--) {
                if (hasQuasiNeighborSignalOptimized(pos, dir, i)) {
                    return true;
                }
            }
        } else {
            for (int i = 1; i <= dist; i++) {
                if (hasQuasiNeighborSignalOptimized(pos, dir, i)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * BlockPos is the block position of the block doing the check, not the location that the check happens at.
     *
     * Don't abuse this method, it can be rather expensive when its frequently called
     */
    @Override
    public boolean pl$hasQuasiNeighborSignalBubble(BlockPos pos) {
        Set<BlockPos> blockPosList = new HashSet<>();
        for (Direction extendedDir : DIRECTIONS) {
            BlockPos p = pos.relative(extendedDir);
            Direction extendedDirOpp = extendedDir.getOpposite();
            for (Direction dir : DIRECTIONS) {
                if (dir == extendedDirOpp) {
                    continue; // Don't update self
                }
                BlockPos nextPos = p.relative(dir);
                if (!blockPosList.contains(nextPos)) {
                    if (this.pl$hasQuasiSignal(nextPos, extendedDir, 1)) {
                        return true;
                    }
                    blockPosList.add(nextPos);
                }
            }
        }
        return false;
    }
}
