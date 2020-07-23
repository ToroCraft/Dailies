package net.torocraft.dailies.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.ResourceLocation;
import net.torocraft.dailies.BaileyInventory;
import net.torocraft.dailies.DailiesMod;

import javax.annotation.Nullable;

public class BaileyInventoryContainer extends Container {

    public static final ContainerType<BaileyInventoryContainer> CT_BAILEYCONTAINER = register(BaileyInventoryContainer::new, "ct_baileycontainer");

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

    private PlayerEntity player;
    private PlayerInventory inventory;

    public BaileyInventoryContainer(int id, PlayerInventory playerInventory){
        this(id, playerInventory, playerInventory.player);
    }

    public BaileyInventoryContainer(int id, PlayerInventory inventory, PlayerEntity player) {
        super(null, id);
    }

    protected BaileyInventoryContainer(@Nullable ContainerType<?> type, int id) {
        super(type, id);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return playerIn == this.player;
    }

    private static <T extends Container> ContainerType<T> register(ContainerType.IFactory<T> factory, String regname)
    {
        ContainerType<T> container_type = new ContainerType<T>(factory);
        container_type.setRegistryName(new ResourceLocation(DailiesMod.MODID, regname));
        return container_type;
    }
}
