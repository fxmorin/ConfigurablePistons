package ca.fxco.pistonlib.pistonLogic.structureResolvers;

import ca.fxco.pistonlib.api.pistonLogic.controller.PistonController;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

@Getter
public class DebugStructureResolver extends BasicStructureResolver {

    private ResolverResult result = ResolverResult.PASS;
    private BlockPos resultPos = BlockPos.ZERO;

    public DebugStructureResolver(PistonController controller, Level level, BlockPos pos,
                                  Direction facing, int length, boolean extend) {
        super(controller, level, pos, facing, length, extend);
    }

    protected boolean runStructureGeneration() {
        // Structure Generation
        BlockState state = this.level.getBlockState(this.startPos);
        if (!this.controller.canMoveBlock(state, this.level, this.startPos,
                this.pushDirection, false, this.pistonDirection)) {
            // Block directly in front is immovable, can only be true if extending, and it can be destroyed
            if (this.extending) {
                Block piston = state.getBlock();
                if (piston.pl$usesConfigurablePistonBehavior()) {
                    if (piston.pl$canDestroy(this.level, this.startPos, state)) {
                        this.toDestroy.add(this.startPos);
                        return true;
                    }
                } else if (state.getPistonPushReaction() == PushReaction.DESTROY) {
                    this.toDestroy.add(this.startPos);
                    return true;
                }
            }
            addResultFail(ResolverResult.FAIL_IMMOVABLE, this.startPos);
            return false;
        } else { // Start block isn't immovable, we can check if it's possible to move this line
            Direction pushDir = !this.extending ? this.pushDirection.getOpposite() : this.pushDirection;
            if (this.attemptMoveLine(state, this.startPos, pushDir)) {
                addResultFail(ResolverResult.FAIL_IMMOVABLE, this.startPos); // TODO: Move this into the attemptMoveLine method for more precision
                return false;
            }
        }

        // This loops through the blocks to push and creates the branches
        for (int i = 0; i < this.toPush.size(); ++i) {
            BlockPos blockPos = this.toPush.get(i);
            if (!attemptCreateBranchesAtBlock(this.level.getBlockState(blockPos), blockPos)) {
                addResultFail(ResolverResult.FAIL_MOVELINE, blockPos); // TODO: Move this into the attemptCreateBranchesAtBlock method for more precision
                return false;
            }
        }
        return true;
    }

    private void addResultFail(ResolverResult result, BlockPos resultPos) {
        this.result = result;
        this.resultPos = resultPos;
    }

    public enum ResolverResult {
        PASS,
        //FAIL_PUSHLIMIT,
        FAIL_IMMOVABLE,
        FAIL_MOVELINE
    }
}
