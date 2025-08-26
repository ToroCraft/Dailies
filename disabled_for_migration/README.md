# Files Disabled During 1.19.2 Migration

These files were temporarily disabled during the migration from 1.16.1 to 1.19.2 to focus on core functionality first.

## Structure Generation (disabled_for_migration/generation/)

**Priority: Low** - Can be migrated after core mod functionality is working

Files moved:
- `BaileysShopVillagePiece.java` - Village piece generation
- `BaileyShopStructure.java` - Structure definition  
- `VillageHandlerBailey.java` - Village handler

**Migration Notes:**
- Structure generation API was completely rewritten in 1.18+
- Need to use new Structure system with datapacks
- See: https://docs.minecraftforge.net/en/1.19.x/worldgen/structures/
- May need to convert to structure templates instead of code-based generation

**Todo when migrating:**
1. Update to new Structure API
2. Convert village piece to structure template
3. Create datapack structure definitions
4. Update registration system
5. Test structure generation in world

## Status
- Core mod functionality: ✅ In progress
- Entity system: ✅ In progress  
- GUI system: ✅ In progress
- Networking: ✅ Completed
- Quest system: ✅ In progress
- Structure generation: ❌ Disabled for later migration

Return these files to `src/main/java/net/torocraft/dailies/generation/` after core functionality is working.
