package ca.fxco.pistonlib.items;

import ca.fxco.pistonlib.base.ModDataComponents;
import ca.fxco.pistonlib.blocks.pistons.basePiston.BasicPistonBaseBlock;
import ca.fxco.pistonlib.helpers.SingleItemTooltip;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.math.Fraction;

import java.util.Optional;

public class PistonWandItem extends Item {
    public PistonWandItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean canAttackBlock(BlockState blockState, Level level, BlockPos blockPos, Player player) {
        return player.canUseGameMasterBlocks(); //todo: change later
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Player player = useOnContext.getPlayer();
        if (player == null || !player.canUseGameMasterBlocks()) { //todo: change later
            return InteractionResult.FAIL;
        }
        Level level = useOnContext.getLevel();
        if (level instanceof ServerLevel serverLevel) {
            ItemStack wandItem = getWandItem(useOnContext.getItemInHand());
            if (wandItem != ItemStack.EMPTY) {
                BlockPos blockPos = useOnContext.getClickedPos();
                Direction face = useOnContext.getClickedFace();
                serverLevel.pl$addPistonEvent(((BasicPistonBaseBlock) ((BlockItem) wandItem.getItem()).getBlock()), blockPos.relative(face), face.getOpposite(), true);
            } else {
                ((ServerPlayer) player).sendSystemMessage(Component.literal("Piston Wand does not currently have a piston!"), true); // todo: translations
            }
        }

        return level.isClientSide ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack itemStack, Slot slot, ClickAction clickAction, Player player) {
        if (clickAction != ClickAction.SECONDARY) {
            return false;
        } else {
            ItemStack itemStack2 = slot.getItem();
            if (itemStack2.isEmpty()) {
                removeItem(itemStack).ifPresent((itemStack2x) -> {
                    this.playRemoveItemSound(player);
                    add(itemStack, slot.safeInsert(itemStack2x));
                });
            } else if (canAcceptItem(itemStack2) && getWandItem(itemStack).isEmpty()) {
                add(itemStack, slot.safeTake(itemStack2.getCount(), 1, player));
                this.playInsertSound(player);
            }
            return true;
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack itemStack, ItemStack itemStack2, Slot slot, ClickAction clickAction, Player player, SlotAccess slotAccess) {
        if (clickAction == ClickAction.SECONDARY && slot.allowModification(player)) {
            if (itemStack2.isEmpty()) {
                removeItem(itemStack).ifPresent((itemStackx) -> {
                    slotAccess.set(itemStackx);
                    this.playRemoveItemSound(player);
                });
            } else if (canAcceptItem(itemStack2)) {
                add(itemStack, itemStack2);
                itemStack2.shrink(1);
                this.playInsertSound(player);
            }
            return true;
        }
        return false;
    }

    private static boolean canAcceptItem(ItemStack addingItem) {
        return !addingItem.isEmpty() &&
                addingItem.getItem().canFitInsideContainerItems() &&
                addingItem.getItem() instanceof BlockItem blockItem &&
                blockItem.getBlock() instanceof BasicPistonBaseBlock;
    }

    private static void add(ItemStack itemStack, ItemStack addingItem) {
        ItemStack itemStack4 = addingItem.copy();
        itemStack4.setCount(1);
        itemStack.set(ModDataComponents.WAND_ITEM, itemStack4);
    }

    private static Optional<ItemStack> removeItem(ItemStack itemStack) {
        ItemStack stack = itemStack.getOrDefault(ModDataComponents.WAND_ITEM, ItemStack.EMPTY);
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        itemStack.set(ModDataComponents.WAND_ITEM, null);
        return Optional.of(stack);
    }

    static ItemStack getWandItem(ItemStack itemStack) {
        ItemStack stack = itemStack.getOrDefault(ModDataComponents.WAND_ITEM, ItemStack.EMPTY);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof BasicPistonBaseBlock) {
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack itemStack) {
        NonNullList<ItemStack> nonNullList = NonNullList.create();
        nonNullList.add(getWandItem(itemStack));
        return Optional.of(new SingleItemTooltip(new BundleContents(nonNullList, Fraction.ONE, -1)));
    }

    private void playRemoveItemSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

}
