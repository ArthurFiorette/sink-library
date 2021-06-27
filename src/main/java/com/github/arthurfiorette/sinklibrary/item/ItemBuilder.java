package com.github.arthurfiorette.sinklibrary.item;

import com.github.arthurfiorette.sinklibrary.services.JavaService;
import com.github.arthurfiorette.sinklibrary.services.SpigotService;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

/**
 * A builder class to customize in multiple ways a item stack.
 *
 * @author https://github.com/ArthurFiorette/sink-library/
 */
public class ItemBuilder {

  private transient ItemStack lastBuild = new ItemStack(Material.AIR);
  private transient boolean modified = true;

  private final Material material;
  private EnumMap<ItemProperty, Consumer<ItemStack>> properties = new EnumMap<>(ItemProperty.class);

  /**
   * Constructs a ItemBuilder from a specified material.
   *
   * @param material the material to use in the builder.
   *
   * @throws NullPointerException if the material is null
   */
  public ItemBuilder(Material material) {
    JavaService.requireNonNull(material);
    this.material = material;
  }

  /**
   * @param durability set item durability
   *
   * @return itself
   */
  public ItemBuilder setDurability(int durability) {
    return this.addProperties(ItemProperty.DAMAGE, is -> is.setDurability((short) durability));
  }

  /**
   * @param amount set the item amount
   *
   * @return itself
   */
  public ItemBuilder setAmount(int amount) {
    return this.addProperties(ItemProperty.AMOUNT, is -> is.setAmount(amount));
  }

  /**
   * @param data set the item data
   *
   * @return itself
   */
  public ItemBuilder setData(MaterialData data) {
    return this.addProperties(ItemProperty.MATERIAL_DATA, is -> is.setData(data));
  }

  /**
   * @param ench the enchantment to add
   * @param level the enchantment level
   *
   * @return itself
   */
  public ItemBuilder addEnchantment(Enchantment ench, int level) {
    return this.addProperties(ItemProperty.ENCHANTMENT, is -> is.addUnsafeEnchantment(ench, level));
  }

  /**
   * @param enchantments set the item enchantments in a map of
   * {@code <enchantment,
   *                     level>}
   *
   * @return itself
   */
  public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments) {
    return this.addProperties(
        ItemProperty.ENCHANTMENT,
        is -> is.addUnsafeEnchantments(enchantments)
      );
  }

  /**
   * @param name set the item name
   *
   * @return itself
   */
  public ItemBuilder setName(String name) {
    return this.addProperties(
        ItemProperty.NAME,
        is -> SpigotService.updateItemMeta(is, im -> im.setDisplayName(name))
      );
  }

  /**
   * Add to the item all the item flags.
   *
   * @return itself
   */
  public ItemBuilder setItemFlags() {
    return this.setItemFlags(ItemFlag.values());
  }

  /**
   * @param itemFlags set to the item all the item flags specified in varargs
   *
   * @return itself
   */
  public ItemBuilder setItemFlags(ItemFlag... itemFlags) {
    return this.addProperties(
        ItemProperty.ITEM_FLAG,
        is ->
          SpigotService.updateItemMeta(
            is,
            im -> {
              im.removeItemFlags(ItemFlag.values());
              im.addItemFlags(itemFlags);
            }
          )
      );
  }

  /**
   * @param itemFlags add to the item all the item flags specified in varargs.
   *
   * @return itself
   */
  public ItemBuilder addItemFlags(ItemFlag... itemFlags) {
    return this.addProperties(
        ItemProperty.ITEM_FLAG,
        is -> SpigotService.updateItemMeta(is, im -> im.addItemFlags(itemFlags))
      );
  }

  /**
   * @param loreLines set the item lore
   *
   * @return itself
   */
  public ItemBuilder setLores(String... loreLines) {
    return this.setLore(Arrays.asList(loreLines));
  }

  /**
   * @param lore set the item lore
   *
   * @return itself
   */
  public ItemBuilder setLore(List<String> lore) {
    return this.addProperties(
        ItemProperty.LORE,
        is -> SpigotService.updateItemMeta(is, im -> im.setLore(lore))
      );
  }

  /**
   * @param loreLines add this lore to the item
   *
   * @return itself
   */
  public ItemBuilder addLores(String... loreLines) {
    return this.addLore(Arrays.asList(loreLines));
  }

  /**
   * @param lore add this lores to the item
   *
   * @return itself
   */
  public ItemBuilder addLore(List<String> lore) {
    if (this.properties.containsKey(ItemProperty.LORE)) {
      return this.addProperties(
          ItemProperty.LORE,
          is -> SpigotService.updateItemMeta(is, im -> im.getLore().addAll(lore))
        );
    }
    return this.setLore(lore);
  }

  /**
   * @param unbreakable true if the items needs to be unbreakable
   *
   * @return itself
   */
  public ItemBuilder setUnbreakable(boolean unbreakable) {
    return this.addProperties(
        ItemProperty.CUSTOM_META,
        is -> SpigotService.updateItemMeta(is, im -> im.spigot().setUnbreakable(unbreakable))
      );
  }

  /**
   * @param customMeta any custom meta to be added to the item at the end
   *
   * @return itself
   */
  public ItemBuilder addCustomMeta(UnaryOperator<ItemMeta> customMeta) {
    return this.addProperties(
        ItemProperty.CUSTOM_META,
        is -> is.setItemMeta(customMeta.apply(is.getItemMeta()))
      );
  }

  /**
   * Build a stack of items with this real configuration. if nothing has been
   * changed, it will return the last saved construction.
   *
   * @return the item stack
   */
  public ItemStack build() {
    if (!this.modified) {
      return this.getLastBuild();
    }
    ItemStack item = new ItemStack(this.material);
    this.properties.values().stream().forEach(c -> c.accept(item));
    this.modified = false;
    return this.lastBuild = item;
  }

  /**
   * @return the last item stack build or null if there wasn't a last build
   */
  public ItemStack getLastBuild() {
    return this.lastBuild;
  }

  /**
   * @return a copy fro this builder
   */
  public ItemBuilder copy() {
    ItemBuilder clone = new ItemBuilder(this.material);
    clone.properties = this.properties;
    return clone;
  }

  /**
   * Remove any property from this builder
   *
   * @param property the property to remove
   *
   * @return itself
   */
  public ItemBuilder remove(ItemProperty property) {
    this.properties.remove(property);
    return this;
  }

  private ItemBuilder addProperties(ItemProperty type, Consumer<ItemStack> consumer) {
    if (this.properties.containsKey(type) && type.isCumulative()) {
      this.properties.compute(type, (k, v) -> v.andThen(consumer));
    } else {
      this.properties.put(type, consumer);
    }
    this.modified = true;
    return this;
  }
}