package ca.fxco.pistonlib.mixin.entity;

import ca.fxco.pistonlib.PistonLibConfig;
import ca.fxco.pistonlib.helpers.BlockPosUtils;
import ca.fxco.pistonlib.api.entity.EntityPistonMechanics;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class Entity_pushIntoMixin implements EntityPistonMechanics {

    @WrapOperation(
            method = "move",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;collide(Lnet/minecraft/world/phys/Vec3;)" +
                            "Lnet/minecraft/world/phys/Vec3;"
            )
    )
    private Vec3 pl$checkPushInto(Entity instance, Vec3 vec3, Operation<Vec3> original, MoverType moverType) {
        if (PistonLibConfig.entityApi && moverType == MoverType.PISTON && !instance.level().isClientSide) {
            BlockState crushedAgainst = null;
            if (pl$canPushIntoBlocks()) {
                // This looks kinda scary, although it only checks max 25 blocks.
                // Honestly it's not very expensive compared to a lot of the other movement logic which runs every tick
                AABB aabb = instance.getBoundingBox();
                if (aabb.getXsize() <= 4.0 && aabb.getYsize() <= 4.0 && aabb.getZsize() <= 4.0) {
                    boolean single = true;
                    BlockPos min = BlockPos.containing(aabb.minX + 1.0E-7, aabb.minY + 1.0E-7, aabb.minZ + 1.0E-7);
                    BlockPos max = BlockPos.containing(aabb.maxX - 1.0E-7, aabb.maxY - 1.0E-7, aabb.maxZ - 1.0E-7);
                    aabb = aabb.move(vec3);
                    BlockPos pos1 = BlockPos.containing(aabb.minX + 1.0E-7, aabb.minY + 1.0E-7, aabb.minZ + 1.0E-7);
                    BlockPos pos2 = BlockPos.containing(aabb.maxX - 1.0E-7, aabb.maxY - 1.0E-7, aabb.maxZ - 1.0E-7);
                    for (BlockPos pos : BlockPos.betweenClosed(pos1, pos2)) {
                        if (BlockPosUtils.isNotWithin(pos, min, max)) {
                            BlockState state = instance.level().getBlockState(pos);
                            if (single) {
                                single = false;
                                crushedAgainst = state;
                            } else {
                                crushedAgainst = null;
                            }
                            if (instance.pl$onPushedIntoBlock(state, pos)) {
                                state.pl$onPushEntityInto(instance.level(), pos, instance);
                            }
                        }
                    }
                }
            }
            Vec3 afterCollide = original.call(instance, vec3);
            if (vec3.lengthSqr() != 0 && vec3 != afterCollide) { // If entity is being crushed
                pl$onPistonCrushing(crushedAgainst);
            }
            return afterCollide;
        }
        return original.call(instance, vec3);
    }
}
