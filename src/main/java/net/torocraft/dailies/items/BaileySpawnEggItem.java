package net.torocraft.dailies.items;

import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

public class BaileySpawnEggItem extends SpawnEggItem {
    public BaileySpawnEggItem(EntityType<? extends Mob> entityType, Properties properties) {
        super(entityType, properties);
    }
    
    public int getColor(int tintIndex) {
        if (tintIndex == 0) {
            return 0x8B4513;
        } else if (tintIndex == 1) {
            return 0xDEB887;  
        }
        return 0xFFFFFF;
    }
}
