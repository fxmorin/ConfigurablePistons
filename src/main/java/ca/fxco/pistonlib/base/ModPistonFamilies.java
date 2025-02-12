package ca.fxco.pistonlib.base;

import ca.fxco.pistonlib.api.PistonLibRegistries;
import ca.fxco.pistonlib.api.pistonLogic.families.PistonFamilies;
import ca.fxco.pistonlib.api.pistonLogic.families.PistonFamily;
import ca.fxco.pistonlib.api.pistonLogic.families.PistonFamilyMember;
import ca.fxco.pistonlib.pistonLogic.families.PistonBehaviorImpl;
import ca.fxco.pistonlib.pistonLogic.families.PistonFamilyImpl;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.PistonType;

import java.util.Objects;

import static ca.fxco.pistonlib.PistonLib.id;

public class ModPistonFamilies {

    public static final PistonFamily VANILLA = register("vanilla", PistonFamilyImpl.builder()
            .behavior(PistonBehaviorImpl.DEFAULT)
            .base(PistonType.DEFAULT, Blocks.PISTON)
            .base(PistonType.STICKY, Blocks.STICKY_PISTON)
            .head(Blocks.PISTON_HEAD)
            .moving(Blocks.MOVING_PISTON)
            .movingBlockEntity(BlockEntityType.PISTON, PistonFamily.createVanillaFactory()));

    private static PistonFamily register(String name, PistonFamily.Builder familyBuilder) {
        return register(name, familyBuilder.build());
    }

    private static PistonFamily register(String name, PistonFamily family) {
        return PistonFamilies.register(id(name), family);
    }

    public static void bootstrap() { }

    public static void validate() {
        PistonLibRegistries.PISTON_FAMILY.forEach(family -> {
            try {
                if (family.getMinLength() > family.getMaxLength()) {
                    throw new IllegalStateException("min length is greater than max length");
                }

                if (family.getBases().isEmpty()) {
                    throw new IllegalStateException("missing base block");
                }
                Objects.requireNonNull(family.getHead(), "head block");
                if (family.getMaxLength() > 1) {
                    Objects.requireNonNull(family.getArm(), "missing arm block");
                }
                Objects.requireNonNull(family.getMoving(), "moving block");
                Objects.requireNonNull(family.getMovingBlockEntityType(), "moving block entity type");
                Objects.requireNonNull(family.getMovingBlockEntityFactory(), "moving block entity factory");
            } catch (Exception e) {
                throw new IllegalStateException("piston family " + family + " is invalid!", e);
            }

            family.getBases().forEach((type, base) -> ((PistonFamilyMember) base).setFamily(family));
            if (family.getArm() != null) {
                ((PistonFamilyMember) family.getArm()).setFamily(family);
            }
            ((PistonFamilyMember) family.getHead()).setFamily(family);
            ((PistonFamilyMember) family.getMoving()).setFamily(family);

            family.getMovingBlockEntityType().addSupportedBlock(family.getMoving());
        });

        if (FabricLoaderImpl.INSTANCE.isDevelopmentEnvironment()) {
            BuiltInRegistries.BLOCK.entrySet().forEach(entry -> {
                if (entry.getValue() instanceof PistonFamilyMember familyMember && familyMember.getFamily() == null) {
                    throw new IllegalStateException("Missing piston family for: " + entry.getValue());
                }
            });
        }
    }
}
