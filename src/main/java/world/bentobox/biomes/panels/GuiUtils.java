package world.bentobox.biomes.panels;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import world.bentobox.bentobox.api.panels.PanelItem;
import world.bentobox.bentobox.api.panels.builders.PanelBuilder;
import world.bentobox.bentobox.api.panels.builders.PanelItemBuilder;


/**
 * This class contains static methods that is used through multiple GUIs.
 */
public class GuiUtils
{
    private GuiUtils() {
        // Private constructor to hide the implicit public one
    }

    // ---------------------------------------------------------------------
    // Section: Border around GUIs
    // ---------------------------------------------------------------------


    /**
     * This method creates border of black panes around given panel with 5 rows.
     * @param panelBuilder PanelBuilder which must be filled with border blocks.
     */
    public static void fillBorder(PanelBuilder panelBuilder)
    {
        GuiUtils.fillBorder(panelBuilder, 5, Material.BLACK_STAINED_GLASS_PANE);
    }


    /**
     * This method sets black stained glass pane around Panel with given row count.
     * @param panelBuilder object that builds Panel.
     * @param rowCount in Panel.
     */
    public static void fillBorder(PanelBuilder panelBuilder, int rowCount)
    {
        GuiUtils.fillBorder(panelBuilder, rowCount, Material.BLACK_STAINED_GLASS_PANE);
    }


    /**
     * This method sets blocks with given Material around Panel with 5 rows.
     * @param panelBuilder object that builds Panel.
     * @param material that will be around Panel.
     */
    public static void fillBorder(PanelBuilder panelBuilder, Material material)
    {
        GuiUtils.fillBorder(panelBuilder, 5, material);
    }


    /**
     * This method sets blocks with given Material around Panel with given row count.
     * @param panelBuilder object that builds Panel.
     * @param rowCount in Panel.
     * @param material that will be around Panel.
     */
    public static void fillBorder(PanelBuilder panelBuilder, int rowCount, Material material)
    {
        // Only for useful filling.
        if (rowCount < 3)
        {
            return;
        }

        for (int i = 0; i < 9 * rowCount; i++)
        {
            // First (i < 9) and last (i > 35) rows must be filled
            // First column (i % 9 == 0) and last column (i % 9 == 8) also must be filled.

            if (i < 9 || i > 9 * (rowCount - 1) || i % 9 == 0 || i % 9 == 8)
            {
                panelBuilder.item(i, BorderBlock.getPanelBorder(material));
            }
        }
    }


    // ---------------------------------------------------------------------
    // Section: ItemStack transformations
    // ---------------------------------------------------------------------


    /**
     * This BorderBlock is simple PanelItem but without item meta data.
     */
    private static class BorderBlock extends PanelItem
    {
        private BorderBlock(ItemStack icon)
        {
            super(new PanelItemBuilder().
                    icon(icon.clone()).
                    name(" ").
                    description(Collections.emptyList()).
                    glow(false).
                    clickHandler(null));
        }


        /**
         * This method retunrs BorderBlock with requested item stack.
         * @param material of which broder must be created.
         * @return PanelItem that acts like border.
         */
        private static BorderBlock getPanelBorder(Material material)
        {
            ItemStack itemStack = new ItemStack(material);
            itemStack.getItemMeta().setDisplayName(" ");

            return new BorderBlock(itemStack);
        }
    }


    /**
     * Simple splitter
     *
     * @param string - string to be split
     * @param warpLength - whn warp should be affected.
     * @return list of split strings
     */
    public static List<String> stringSplit(String string, int warpLength)
    {
        // Remove all ending lines from string.
        string = string.replaceAll("([\\r\\n])", "\\|");
        string = ChatColor.translateAlternateColorCodes('&', string);
        // Check length of lines
        List<String> result = new ArrayList<>();

        Arrays.stream(string.split("\\|")).
        map(line -> Arrays.asList(WordUtils.wrap(line, warpLength).split(System.getProperty("line.separator")))).
        forEach(result::addAll);

        // Fix colors, as splitting my lost that information.

        for (int i = 0, resultSize = result.size(); i < resultSize; i++)
        {
            if (i > 0)
            {
                String lastColor = ChatColor.getLastColors(result.get(i - 1));
                result.set(i, lastColor + result.get(i));
            }
        }

        return result;
    }


    /**
     * Simple splitter for all strings in list.
     * @param stringList - list of string to be split
     * @param warpLength - whn warp should be affected.
     * @return list of split strings
     */
    public static List<String> stringSplit(List<String> stringList, int warpLength)
    {
        if (stringList.isEmpty())
        {
            return stringList;
        }

        List<String> newList = new ArrayList<>(stringList.size());
        stringList.stream().map(string -> GuiUtils.stringSplit(string, warpLength)).forEach(newList::addAll);
        return newList;
    }


    // ---------------------------------------------------------------------
    // Section: Materials
    // ---------------------------------------------------------------------


    /**
     * This method transforms material into item stack that can be displayed in users
     * inventory.
     * @param material Material which item stack must be returned.
     * @return ItemStack that represents given material.
     */
    public static ItemStack getMaterialItem(Material material)
    {
        return GuiUtils.getMaterialItem(material, 1);
    }


    /**
     * This method transforms material into item stack that can be displayed in users
     * inventory.
     * @param material Material which item stack must be returned.
     * @param amount Amount of ItemStack elements.
     * @return ItemStack that represents given material.
     */
    public static ItemStack getMaterialItem(Material material, int amount)
    {
        ItemStack itemStack;

        // Process items that cannot be item-stacks.
        if (material.name().contains("WALL_"))
        {
            // Materials that is attached to wall cannot be showed in GUI. But they should be in list.
            itemStack = new ItemStack(Material.getMaterial(material.name().replace("WALL_", "")));
        }
        else if (material.name().startsWith("POTTED_"))
        {
            // Materials Potted elements cannot be in inventory.
            itemStack = new ItemStack(Material.getMaterial(material.name().replace("POTTED_", "")));
        }
        else if (M2M.containsKey(material)) {
            itemStack = new ItemStack(M2M.get(material));
        }
        else
        {
            itemStack = new ItemStack(material);
        }

        itemStack.setAmount(amount);

        return itemStack;
    }

    private static final Map<Material, Material> M2M;
    static {
        Map<Material, Material> aMap = new EnumMap<>(Material.class);
        aMap.put(Material.MELON_STEM, Material.MELON_SEEDS);
        aMap.put(Material.ATTACHED_MELON_STEM, Material.MELON_SEEDS);
        aMap.put(Material.PUMPKIN_STEM, Material.PUMPKIN_SEEDS);
        aMap.put(Material.ATTACHED_PUMPKIN_STEM, Material.PUMPKIN_SEEDS);
        aMap.put(Material.TALL_SEAGRASS, Material.SEAGRASS);
        aMap.put(Material.CARROTS, Material.CARROT);
        aMap.put(Material.BEETROOTS, Material.BEETROOT);
        aMap.put(Material.POTATOES, Material.POTATO);
        aMap.put(Material.COCOA, Material.COCOA_BEANS);
        aMap.put(Material.KELP_PLANT, Material.KELP);
        aMap.put(Material.REDSTONE_WIRE, Material.REDSTONE);
        aMap.put(Material.TRIPWIRE, Material.STRING);
        aMap.put(Material.FROSTED_ICE, Material.ICE);
        aMap.put(Material.END_PORTAL, Material.PAPER);
        aMap.put(Material.END_GATEWAY, Material.PAPER);
        aMap.put(Material.NETHER_PORTAL, Material.PAPER);
        aMap.put(Material.BUBBLE_COLUMN,Material.WATER_BUCKET);
        aMap.put(Material.WATER, Material.WATER_BUCKET);
        aMap.put(Material.LAVA, Material.LAVA_BUCKET);
        aMap.put(Material.FIRE, Material.FIRE_CHARGE);
        aMap.put(Material.AIR, Material.GLASS_BOTTLE);
        aMap.put(Material.CAVE_AIR, Material.GLASS_BOTTLE);
        aMap.put(Material.VOID_AIR, Material.GLASS_BOTTLE);
        aMap.put(Material.PISTON_HEAD, Material.PISTON);
        aMap.put(Material.MOVING_PISTON, Material.PISTON);
        M2M = Collections.unmodifiableMap(aMap);
    }
}