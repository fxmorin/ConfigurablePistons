package ca.fxco.pistonlib.helpers;

import lombok.experimental.UtilityClass;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.Collection;

import static ca.fxco.pistonlib.PistonLib.DIRECTIONS;
import static net.minecraft.core.Direction.*;

@UtilityClass
public class Utils {

    public static Direction applyFacing(Direction dir, Direction facing) {
        return switch(facing) {
            case DOWN -> switch(dir) {
                case UP -> SOUTH;
                case DOWN -> NORTH;
                case SOUTH -> UP;
                case NORTH -> DOWN;
                default -> dir.getOpposite();
            };
            case UP -> switch(dir) {
                case UP -> NORTH;
                case DOWN -> SOUTH;
                case SOUTH -> DOWN;
                case NORTH -> UP;
                default -> dir.getOpposite();
            };
            case NORTH -> dir;
            case SOUTH -> dir != DOWN && dir != UP ? dir.getOpposite() : dir;
            case WEST -> switch(dir) {
                case EAST -> SOUTH;
                case WEST -> NORTH;
                case UP, DOWN -> dir;
                case SOUTH -> WEST;
                case NORTH -> EAST;
            };
            case EAST -> switch(dir) {
                case EAST -> NORTH;
                case WEST -> SOUTH;
                case UP, DOWN -> dir;
                case SOUTH -> EAST;
                case NORTH -> WEST;
            };
        };
    }

    public static boolean hasNeighborSignalExceptFromFacing(SignalGetter level, BlockPos pos, Direction except) {
        for (Direction dir : DIRECTIONS) {
            if (dir != except && level.hasSignal(pos.relative(dir), dir)) {
                return true;
            }
        }

        return false;
    }

    public static boolean setBlockWithEntity(Level level, BlockPos pos, BlockState state,
                                             BlockEntity blockEntity, int flags) {
        level.pl$prepareBlockEntityPlacement(pos, state, blockEntity);
        return level.setBlock(pos, state, flags);
    }

    // Ya pretend this is not here xD
    public static DyeColor properDyeMixing(DyeColor col1, DyeColor col2) {
        if (col1.equals(col2)) return col1;
        return switch(col1) {
            case WHITE -> switch(col2) {
                    case BLUE -> DyeColor.LIGHT_BLUE;
                    case GRAY -> DyeColor.LIGHT_GRAY;
                    case BLACK -> DyeColor.GRAY;
                    case GREEN -> DyeColor.LIME;
                    case RED -> DyeColor.PINK;
                    default -> col1;
                };
            case BLUE -> switch(col2) {
                    case WHITE -> DyeColor.LIGHT_BLUE;
                    case GREEN -> DyeColor.CYAN;
                    case RED -> DyeColor.PURPLE;
                    default -> col1;
                };
            case RED -> switch(col2) {
                    case YELLOW -> DyeColor.ORANGE;
                    case WHITE -> DyeColor.PINK;
                    case BLUE -> DyeColor.PURPLE;
                    default -> col1;
                };
            case GREEN -> switch(col2) {
                    case BLUE -> DyeColor.CYAN;
                    case WHITE -> DyeColor.LIME;
                    default -> col1;
                };
            case YELLOW -> col2.equals(DyeColor.RED) ? DyeColor.ORANGE : col1;
            case PURPLE -> col2.equals(DyeColor.PINK) ? DyeColor.MAGENTA : col1;
            case PINK -> col2.equals(DyeColor.PURPLE) ? DyeColor.MAGENTA : col1;
            case GRAY -> col2.equals(DyeColor.WHITE) ? DyeColor.LIGHT_GRAY : col1;
            case BLACK -> col2.equals(DyeColor.WHITE) ? DyeColor.GRAY : col1;
            default -> col1;
        };
    }

    public static <T> boolean containsAny(Collection<T> collection, Collection<T> anyOf) {
        boolean failed = false;
        for (T val : anyOf) {
            if (collection.contains(val)) {
                failed = true;
                break;
            }
        }
        return !failed;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] createInstances(Class<T> tClass, Class<?>[] classes) {
        Object[] instances = (Object[]) Array.newInstance(tClass, classes.length);
        for (int i = 0; i < classes.length; i++) {
            instances[i] = Utils.createInstance(classes[i]);
        }
        return (T[]) instances;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> T createInstance(Class<T> clazz) {
        try {
            Constructor<T> c = clazz.getDeclaredConstructor();
            c.setAccessible(true);
            return c.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
