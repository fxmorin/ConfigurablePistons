package ca.fxco.pistonlib.mixin;

import ca.fxco.pistonlib.api.block.PLPistonController;
import ca.fxco.pistonlib.api.pistonLogic.controller.PistonController;
import ca.fxco.pistonlib.api.pistonLogic.families.PistonFamily;
import ca.fxco.pistonlib.api.pistonLogic.families.PistonFamilyMember;
import ca.fxco.pistonlib.base.ModTags;
import ca.fxco.pistonlib.pistonLogic.controller.VanillaPistonController;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/*
 * In this mixin we basically change all the !state.is(PISTON) and regular piston to instead check against the
 * PISTONS TagKey, so that all pistons work the same with each other (Including vanilla)
 *
 * We also replace any instances of PistonStructureResolver with the custom ConfigurablePistonStructureResolver.
 * This ensures even vanilla pistons will respect custom movability and sticky behavior.
 */
@Mixin(PistonBaseBlock.class)
public class PistonBaseBlockMixin implements PLPistonController, PistonFamilyMember {

    @Unique
    private static final PistonController VANILLA_CONTROLLER_DEFAULT = new VanillaPistonController(PistonType.DEFAULT);
    @Unique
    private static final PistonController VANILLA_CONTROLLER_STICKY  = new VanillaPistonController(PistonType.STICKY);

    @Shadow
    @Final
    private boolean isSticky;

    @Override
    public PistonFamily getFamily() {
        return this.pl$getPistonController().getFamily();
    }

    @Override
    public void setFamily(PistonFamily family) {
        this.pl$getPistonController().setFamily(family);
    }

    @WrapOperation(
            method = "checkIfExtend(Lnet/minecraft/world/level/Level;" +
                    "Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/world/level/block/piston/PistonStructureResolver"
            )
    )
    private PistonStructureResolver pl$customStructureResolver1(Level level, BlockPos pos,
                                                                Direction facing, boolean extend,
                                                                Operation<PistonStructureResolver> original) {
        return pl$newStructureResolver(level, pos, facing, extend);
    }

    @Inject(
            method = "triggerEvent(Lnet/minecraft/world/level/block/state/BlockState;" +
                    "Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;II)Z",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private void pl$replacePistonLogic(BlockState state, Level level, BlockPos pos,
                                    int i, int j, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(pl$getPistonController().triggerEvent(state, level, pos, i, j));
    }

    @WrapOperation(
        method = "moveBlocks(Lnet/minecraft/world/level/Level;" +
                "Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Z)Z",
        at = @At(
            value = "NEW",
            target = "net/minecraft/world/level/block/piston/PistonStructureResolver"
        )
    )
    private PistonStructureResolver pl$customStructureResolver2(Level level, BlockPos pos,
                                                                Direction facing, boolean extend,
                                                                Operation<PistonStructureResolver> original) {
        return pl$newStructureResolver(level, pos, facing, extend);
    }

    @Override
    public PistonController pl$getPistonController() {
        return this.isSticky ? VANILLA_CONTROLLER_STICKY : VANILLA_CONTROLLER_DEFAULT;
    }

    @Unique
    private PistonStructureResolver pl$newStructureResolver(Level level, BlockPos pos,
                                                            Direction facing, boolean extend) {
        // the basic pistons should act exactly as vanilla pistons anyway
        return pl$getPistonController().newStructureResolver(level, pos, facing, extend ? 0 : 1, extend);
    }
}
