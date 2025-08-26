package net.torocraft.dailies.gui;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

import javax.annotation.Nullable;

public class BaileyInventoryContainer extends AbstractContainerMenu {

    private final int HOTBAR_SLOT_COUNT = 9;
    private final int INVENTORY_ROW_COUNT = 3;
    private final int INVENTORY_COLUMN_COUNT = 9;
    private final int SUBMIT_ITEM_ROW_COUNT = 1;
    private final int SUBMIT_ITEM_COLUMN_COUNT = 3;

    private final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + (INVENTORY_COLUMN_COUNT * INVENTORY_ROW_COUNT);
    private final int BAILEY_INVENTORY_SLOT_COUNT = 3;

    private final int VANILLA_FIRST_SLOT_INDEX = 0;
    private final int BAILEY_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    private final int SLOT_X_SPACING = 18;
    private final int SLOT_Y_SPACING = 18;

    private final int HOTBAR_XPOS = 8;
    private final int HOTBAR_YPOS = 106;

    private final int INVENTORY_XPOS = 8;
    private final int INVENTORY_YPOS = 48;;

    private final int SUBMIT_ITEM_XPOS = 30;
    private final int SUBMIT_ITEM_YPOS = 17;

    private final int OUTPUT_ITEM_XPOS = 117;
    private final int OUTPUT_ITEM_YPOS = 17;

    private Player player;
    private Inventory inventory;

    public BaileyInventoryContainer(int id, Inventory playerInventory) {
        super(net.torocraft.dailies.gui.MenuRegistryHandler.BAILEY_CONTAINER.get(), id);
        this.inventory = playerInventory;
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
        // TODO: Implement proper shift-click behavior for the container
        return net.minecraft.world.item.ItemStack.EMPTY;
    }

    // Registration helper removed; use DeferredRegister in your mod init class for MenuType registration.
}
