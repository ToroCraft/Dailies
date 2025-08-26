package net.torocraft.dailies.gui;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nullable;

public class BaileyInventoryContainer extends AbstractContainerMenu {

    private final int HOTBAR_SLOT_COUNT = 9;
    private final int INVENTORY_ROW_COUNT = 3;
    private final int INVENTORY_COLUMN_COUNT = 9;

    private final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + (INVENTORY_COLUMN_COUNT * INVENTORY_ROW_COUNT);
    private final int BAILEY_INVENTORY_SLOT_COUNT = 3;

    private final int VANILLA_FIRST_SLOT_INDEX = 0;
    private final int BAILEY_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;


    private Player player;
    public BaileyInventoryContainer(int id, Inventory playerInventory) {
        super(net.torocraft.dailies.gui.MenuRegistryHandler.BAILEY_CONTAINER.get(), id);
        this.player = playerInventory.player;
    }

    protected BaileyInventoryContainer(@Nullable net.minecraft.world.inventory.MenuType<?> type, int id) {
        super(type, id);
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return playerIn == this.player;
    }

    @Override
    public net.minecraft.world.item.ItemStack quickMoveStack(Player player, int index) {
        net.minecraft.world.item.ItemStack itemstack = net.minecraft.world.item.ItemStack.EMPTY;
        net.minecraft.world.inventory.Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            net.minecraft.world.item.ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            // Handle shift-click from different inventory sections
            if (index < VANILLA_SLOT_COUNT) {
                // From player inventory to Bailey inventory
                if (!this.moveItemStackTo(slotStack, BAILEY_INVENTORY_FIRST_SLOT_INDEX, 
                    BAILEY_INVENTORY_FIRST_SLOT_INDEX + BAILEY_INVENTORY_SLOT_COUNT, false)) {
                    return net.minecraft.world.item.ItemStack.EMPTY;
                }
            } else if (index < BAILEY_INVENTORY_FIRST_SLOT_INDEX + BAILEY_INVENTORY_SLOT_COUNT) {
                // From Bailey inventory to player inventory
                if (!this.moveItemStackTo(slotStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_SLOT_COUNT, true)) {
                    return net.minecraft.world.item.ItemStack.EMPTY;
                }
            } else {
                return net.minecraft.world.item.ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.set(net.minecraft.world.item.ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == itemstack.getCount()) {
                return net.minecraft.world.item.ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
        }

        return itemstack;
    }

    // Registration helper removed; use DeferredRegister in your mod init class for MenuType registration.
}
