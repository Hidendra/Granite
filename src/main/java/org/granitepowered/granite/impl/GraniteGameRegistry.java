/*
 * License (MIT)
 *
 * Copyright (c) 2014-2015 Granite Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.granitepowered.granite.impl;

import static org.granitepowered.granite.util.MinecraftUtils.wrap;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.NotImplementedException;
import org.granitepowered.granite.Granite;
import org.granitepowered.granite.Main;
import org.granitepowered.granite.impl.effect.particle.GraniteParticleEffectBuilder;
import org.granitepowered.granite.impl.effect.particle.GraniteParticleType;
import org.granitepowered.granite.impl.entity.hanging.art.GraniteArt;
import org.granitepowered.granite.impl.item.GraniteEnchantment;
import org.granitepowered.granite.impl.item.inventory.GraniteItemStackBuilder;
import org.granitepowered.granite.impl.meta.GraniteCareer;
import org.granitepowered.granite.impl.meta.GraniteDyeColor;
import org.granitepowered.granite.impl.meta.GraniteHorseColor;
import org.granitepowered.granite.impl.meta.GraniteHorseStyle;
import org.granitepowered.granite.impl.meta.GraniteHorseVariant;
import org.granitepowered.granite.impl.meta.GraniteOcelotType;
import org.granitepowered.granite.impl.meta.GraniteProfession;
import org.granitepowered.granite.impl.meta.GraniteRabbitType;
import org.granitepowered.granite.impl.meta.GraniteSkeletonType;
import org.granitepowered.granite.impl.potion.GranitePotionBuilder;
import org.granitepowered.granite.impl.potion.GranitePotionEffectType;
import org.granitepowered.granite.impl.status.GraniteFavicon;
import org.granitepowered.granite.impl.util.GraniteRotation;
import org.granitepowered.granite.impl.world.GraniteDimension;
import org.granitepowered.granite.impl.world.GraniteDimensionType;
import org.granitepowered.granite.impl.world.biome.GraniteBiomeType;
import org.granitepowered.granite.mappings.Mappings;
import org.granitepowered.granite.mc.MCBiomeGenBase;
import org.granitepowered.granite.mc.MCBlock;
import org.granitepowered.granite.mc.MCEnchantment;
import org.granitepowered.granite.mc.MCEnumArt;
import org.granitepowered.granite.mc.MCGameRules;
import org.granitepowered.granite.mc.MCItem;
import org.granitepowered.granite.mc.MCPotion;
import org.granitepowered.granite.util.Instantiator;
import org.granitepowered.granite.util.ReflectionUtils;
import org.spongepowered.api.GameProfile;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.meta.BannerPatternShape;
import org.spongepowered.api.block.meta.NotePitch;
import org.spongepowered.api.block.meta.SkullType;
import org.spongepowered.api.effect.particle.ParticleEffectBuilder;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.hanging.art.Art;
import org.spongepowered.api.entity.hanging.art.Arts;
import org.spongepowered.api.entity.living.animal.DyeColor;
import org.spongepowered.api.entity.living.animal.DyeColors;
import org.spongepowered.api.entity.living.animal.HorseColor;
import org.spongepowered.api.entity.living.animal.HorseColors;
import org.spongepowered.api.entity.living.animal.HorseStyle;
import org.spongepowered.api.entity.living.animal.HorseStyles;
import org.spongepowered.api.entity.living.animal.HorseVariant;
import org.spongepowered.api.entity.living.animal.HorseVariants;
import org.spongepowered.api.entity.living.animal.OcelotType;
import org.spongepowered.api.entity.living.animal.OcelotTypes;
import org.spongepowered.api.entity.living.animal.RabbitType;
import org.spongepowered.api.entity.living.animal.RabbitTypes;
import org.spongepowered.api.entity.living.monster.SkeletonType;
import org.spongepowered.api.entity.living.monster.SkeletonTypes;
import org.spongepowered.api.entity.living.villager.Career;
import org.spongepowered.api.entity.living.villager.Careers;
import org.spongepowered.api.entity.living.villager.Profession;
import org.spongepowered.api.entity.living.villager.Professions;
import org.spongepowered.api.entity.player.gamemode.GameMode;
import org.spongepowered.api.item.Enchantment;
import org.spongepowered.api.item.Enchantments;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStackBuilder;
import org.spongepowered.api.item.merchant.TradeOfferBuilder;
import org.spongepowered.api.potion.PotionEffectBuilder;
import org.spongepowered.api.potion.PotionEffectType;
import org.spongepowered.api.potion.PotionEffectTypes;
import org.spongepowered.api.status.Favicon;
import org.spongepowered.api.util.rotation.Rotation;
import org.spongepowered.api.util.rotation.Rotations;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GraniteGameRegistry implements GameRegistry {

    public Map<String, Art> arts = Maps.newHashMap();
    public Map<String, BiomeType> biomes = Maps.newHashMap();
    public Map<String, BlockType> blockTypes = Maps.newHashMap();
    public Map<String, Career> careers = Maps.newHashMap();
    public Map<String, DimensionType> dimensions = Maps.newHashMap();
    public Map<String, DyeColor> dyeColors = Maps.newHashMap();
    public Map<String, Enchantment> enchantments = Maps.newHashMap();
    public Map<String, HorseColor> horseColors = Maps.newHashMap();
    public Map<String, HorseStyle> horseStyles = Maps.newHashMap();
    public Map<String, HorseVariant> horseVariants = Maps.newHashMap();
    public Map<String, ItemType> itemTypes = Maps.newHashMap();
    public Map<String, Profession> professions = Maps.newHashMap();
    public Map<String, OcelotType> ocelots = Maps.newHashMap();
    public Map<String, ParticleType> particles = Maps.newHashMap();
    public Map<String, PotionEffectType> potionEffects = Maps.newHashMap();
    public Map<Profession, List<Career>> professionCareers = Maps.newHashMap();
    public Map<String, RabbitType> rabbits = Maps.newHashMap();
    public Map<Integer, Rotation> rotations = Maps.newHashMap();
    public Map<String, SkeletonType> skeletons = Maps.newHashMap();
    public Map<String, SoundType> sounds = Maps.newHashMap();

    Collection<String> defaultGameRules = new ArrayList<>();

    GraniteItemStackBuilder itemStackBuilder = new GraniteItemStackBuilder();
    GranitePotionBuilder potionBuilder = new GranitePotionBuilder();

    public void register() {
        registerArts();
        registerBiomes();
        registerBlocks();
        registerDimensions();
        registerDyes();
        registerEnchantments();
        registerGameRules();
        registerHorseColors();
        registerHorseStyles();
        registerHorseVariants();
        registerItems();
        registerOcelots();
        registerParticleTypes();
        registerPotionEffects();
        registerProfessionsAndCareers();
        registerRabbits();
        registerRotations();
        registerSkeletons();
        registerSounds();
    }

    private void registerArts() {
        Granite.instance.getLogger().info("Registering Arts");

        List<MCEnumArt> mcEnumArts = Arrays.asList((MCEnumArt[]) Mappings.getClass("EnumArt").getEnumConstants());
        for (Field field : Arts.class.getDeclaredFields()) {
            ReflectionUtils.forceAccessible(field);

            String name = field.getName().toLowerCase().replace("_", "");
            for (MCEnumArt mcEnumArt : mcEnumArts) {
                if (name.equals(mcEnumArt.fieldGet$name().toLowerCase())) {
                    try {
                        Art art = new GraniteArt(mcEnumArt);
                        field.set(null, art);
                        arts.put(name, art);
                        if (Main.debugLog) {
                            Granite.getInstance().getLogger().info("Registered Art minecraft:" + art.getName());
                        }
                    } catch (IllegalAccessException e) {
                        Throwables.propagate(e);
                    }
                }
            }
        }
    }

    private void registerBiomes() {
        Granite.instance.getLogger().info("Registering Biomes");

        try {
            Class biomeGenBaseClass = Mappings.getClass("BiomeGenBase");
            Field biomeList = Mappings.getField(biomeGenBaseClass, "biomeList");
            ArrayList<MCBiomeGenBase> biomesGenBase = Lists.newArrayList((MCBiomeGenBase[]) biomeList.get(biomeGenBaseClass));
            biomesGenBase.removeAll(Collections.singleton(null));

            for (Field field : BiomeTypes.class.getDeclaredFields()) {
                ReflectionUtils.forceAccessible(field);

                String name = field.getName().toLowerCase();
                for (MCBiomeGenBase biome : biomesGenBase) {

                    if (name.equals("sky")) {
                        name = "the_end";
                    } else if (name.equals("extreme_hills_plus")) {
                        name = "extreme_hills+";
                    } else if (name.equals("frozen_ocean") || field.getName().equals("frozen_river") || field.getName().equals("mushroom_island")
                               || field.getName().equals("mushroom_island_shore") || field.getName().equals("desert_hills") || field.getName()
                            .equals("forest_hills") || field.getName().equals("taiga_hills") || field.getName().equals("jungle_hills") || field
                                       .getName().equals("jungle_edge")) {
                        name = name.replace("_", "");
                    } else if (name.equals("mesa_plateau_forest")) {
                        name = "mesa_plateau_f";
                    }

                    String biomeName = biome.fieldGet$biomeName().toLowerCase().replace(" ", "_");

                    if (biomeName.equals(name)) {
                        BiomeType biomeType = new GraniteBiomeType(biome);
                        field.set(null, biomeType);
                        biomes.put(name, biomeType);
                        if (Main.debugLog) {
                            Granite.getInstance().getLogger().info("Registered Biome minecarft:" + name);
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            Granite.error(e);
        }
    }

    private void registerBlocks() {
        Granite.instance.getLogger().info("Registering Blocks");

        for (Field field : BlockTypes.class.getDeclaredFields()) {
            ReflectionUtils.forceAccessible(field);

            String name = field.getName().toLowerCase();
            try {
                MCBlock mcBlock = (MCBlock) Mappings.invokeStatic("Blocks", "getRegisteredBlock", name);

                BlockType block = wrap(mcBlock);
                field.set(null, block);
                blockTypes.put(name, block);

                if (Main.debugLog) {
                    Granite.getInstance().getLogger().info("Registered Block minecraft:" + block.getId());
                }
            } catch (IllegalAccessException e) {
                Throwables.propagate(e);
            }
        }
    }

    // TODO: Needs to not be new instances but the ones that already exist if possible.
    private void registerDimensions() {
        Granite.instance.getLogger().info("Registering Dimensions");

        for (Field field : DimensionTypes.class.getDeclaredFields()) {
            ReflectionUtils.forceAccessible(field);

            String name = field.getName().toLowerCase();
            DimensionType dimensionType = null;
            boolean registered = false;
            try {
                switch (name) {
                    case "overworld":
                        dimensionType = new GraniteDimensionType(new GraniteDimension(Mappings.getClass("WorldProviderSurface").newInstance()));
                        registered = true;
                        break;
                    case "nether":
                        dimensionType = new GraniteDimensionType(new GraniteDimension(Mappings.getClass("WorldProviderHell").newInstance()));
                        registered = true;
                        break;
                    case "end":
                        dimensionType = new GraniteDimensionType(new GraniteDimension(Mappings.getClass("WorldProviderEnd").newInstance()));
                        registered = true;
                        break;
                }
                if (Main.debugLog && registered) {
                    field.set(null, dimensionType);
                    dimensions.put(name, dimensionType);
                    Granite.getInstance().getLogger().info("Registered Dimension minecraft:" + name);
                }
            } catch (IllegalAccessException | InstantiationException e) {
                Granite.error(e);
            }
        }
    }

    private void registerDyes() {
        Granite.instance.getLogger().info("Registering Dyes");

        for (int i = 0; i < DyeColors.class.getDeclaredFields().length; i++) {
            Field field = DyeColors.class.getDeclaredFields()[i];
            ReflectionUtils.forceAccessible(field);

            String name = field.getName().toLowerCase();
            try {
                DyeColor dyeColor = new GraniteDyeColor(i, name);
                field.set(null, dyeColor);
                dyeColors.put(name, dyeColor);
                if (Main.debugLog) {
                    Granite.getInstance().getLogger().info("Registered Dye Color minecraft:" + dyeColor.getName());
                }
            } catch (IllegalAccessException e) {
                Throwables.propagate(e);
            }
        }
    }

    private void registerEnchantments() {
        Granite.instance.getLogger().info("Registering Enchantments");

        for (Field field : Enchantments.class.getDeclaredFields()) {
            ReflectionUtils.forceAccessible(field);

            String name = field.getName().toLowerCase();
            try {
                MCEnchantment mcEnchantment = (MCEnchantment) Mappings.invokeStatic("Enchantment", "getEnchantmentByLocation", name);

                Enchantment enchantment = new GraniteEnchantment(mcEnchantment);
                field.set(null, enchantment);
                enchantments.put(name, enchantment);

                if (Main.debugLog) {
                    Granite.getInstance().getLogger().info("Registered Enchantment " + enchantment.getId());
                }
            } catch (IllegalAccessException e) {
                Throwables.propagate(e);
            }

        }
    }

    private void registerGameRules() {
        Granite.instance.getLogger().info("Registering default GameRules");
        MCGameRules gameRules = Instantiator.get().newGameRules();
        String[] rules = gameRules.getRules();
        for (String rule : rules) {
            defaultGameRules.add(rule);
            if (Main.debugLog) {
                Granite.getInstance().getLogger().info("Registered default GameRule minecraft:" + rule);
            }
        }
    }

    private void registerHorseColors() {
        Granite.instance.getLogger().info("Registering Horse Colors");

        for (int i = 0; i < HorseColors.class.getDeclaredFields().length; i++) {
            Field field = HorseColors.class.getDeclaredFields()[i];
            ReflectionUtils.forceAccessible(field);

            String name = field.getName().toLowerCase();
            try {
                HorseColor horseColor = new GraniteHorseColor(i, name);
                field.set(null, horseColor);
                horseColors.put(name, horseColor);
                if (Main.debugLog) {
                    Granite.getInstance().getLogger().info("Registered Horse Color minecraft:" + horseColor.getName());
                }
            } catch (IllegalAccessException e) {
                Throwables.propagate(e);
            }
        }
    }

    private void registerHorseStyles() {
        Granite.instance.getLogger().info("Registering Horse Styles");

        for (int i = 0; i < HorseStyles.class.getDeclaredFields().length; i++) {
            Field field = HorseStyles.class.getDeclaredFields()[i];
            ReflectionUtils.forceAccessible(field);

            String name = field.getName().toLowerCase();
            try {
                HorseStyle horseStyle = new GraniteHorseStyle(i, name);
                field.set(null, horseStyle);
                horseStyles.put(name, horseStyle);
                if (Main.debugLog) {
                    Granite.getInstance().getLogger().info("Registered Horse Style minecraft:" + horseStyle.getName());
                }
            } catch (IllegalAccessException e) {
                Throwables.propagate(e);
            }
        }
    }

    private void registerHorseVariants() {
        Granite.instance.getLogger().info("Registering Horse Variants");

        for (int i = 0; i < HorseVariants.class.getDeclaredFields().length; i++) {
            Field field = HorseVariants.class.getDeclaredFields()[i];
            ReflectionUtils.forceAccessible(field);

            String name = field.getName().toLowerCase();
            try {
                HorseVariant horseVariant = new GraniteHorseVariant(i, name);
                field.set(null, horseVariant);
                horseVariants.put(name, horseVariant);
                if (Main.debugLog) {
                    Granite.getInstance().getLogger().info("Registered Horse Variant minecraft:" + horseVariant.getName());
                }
            } catch (IllegalAccessException e) {
                Throwables.propagate(e);
            }
        }
    }

    private void registerItems() {
        Granite.instance.getLogger().info("Registering Items");

        for (Field field : ItemTypes.class.getDeclaredFields()) {
            ReflectionUtils.forceAccessible(field);

            String name = field.getName().toLowerCase();
            try {
                Object mcItem = Mappings.invokeStatic("Items", "getRegisteredItem", name);

                ItemType item = wrap((MCItem) mcItem);
                field.set(null, item);
                itemTypes.put(name, item);

                if (Main.debugLog) {
                    Granite.getInstance().getLogger().info("Registered Item minecraft:" + item.getId());
                }
            } catch (IllegalAccessException e) {
                Throwables.propagate(e);
            }
        }
    }

    private void registerOcelots() {
        Granite.instance.getLogger().info("Registering Ocelots");

        for (int i = 0; i < OcelotTypes.class.getDeclaredFields().length; i++) {
            Field field = OcelotTypes.class.getDeclaredFields()[i];
            ReflectionUtils.forceAccessible(field);

            String name = field.getName().toLowerCase();
            try {
                OcelotType ocelotType = new GraniteOcelotType(i, name);
                field.set(null, ocelotType);
                ocelots.put(name, ocelotType);
                if (Main.debugLog) {
                    Granite.getInstance().getLogger().info("Registered Ocelot minecraft:" + ocelotType.getName());
                }
            } catch (IllegalAccessException e) {
                Throwables.propagate(e);
            }
        }
    }

    private void registerParticleTypes() {
        Granite.instance.getLogger().info("Registering ParticleTypes");

        List<GraniteParticleType> types = new ArrayList<>();
        types.add(new GraniteParticleType("EXPLOSION_NORMAL", true));
        types.add(new GraniteParticleType.GraniteResizable("EXPLOSION_LARGE", 1f));
        types.add(new GraniteParticleType("EXPLOSION_HUGE", false));
        types.add(new GraniteParticleType("FIREWORKS_SPARK", true));
        types.add(new GraniteParticleType("WATER_BUBBLE", true));
        types.add(new GraniteParticleType("WATER_SPLASH", true));
        types.add(new GraniteParticleType("WATER_WAKE", true));
        types.add(new GraniteParticleType("SUSPENDED", false));
        types.add(new GraniteParticleType("SUSPENDED_DEPTH", false));
        types.add(new GraniteParticleType("CRIT", true));
        types.add(new GraniteParticleType("CRIT_MAGIC", true));
        types.add(new GraniteParticleType("SMOKE_NORMAL", true));
        types.add(new GraniteParticleType("SMOKE_LARGE", true));
        types.add(new GraniteParticleType("SPELL", false));
        types.add(new GraniteParticleType("SPELL_INSTANT", false));
        types.add(new GraniteParticleType.GraniteColorable("SPELL_MOB", Color.BLACK));
        types.add(new GraniteParticleType.GraniteColorable("SPELL_MOB_AMBIENT", Color.BLACK));
        types.add(new GraniteParticleType("SPELL_WITCH", false));
        types.add(new GraniteParticleType("DRIP_WATER", false));
        types.add(new GraniteParticleType("DRIP_LAVA", false));
        types.add(new GraniteParticleType("VILLAGER_ANGRY", false));
        types.add(new GraniteParticleType("VILLAGER_HAPPY", true));
        types.add(new GraniteParticleType("TOWN_AURA", true));
        types.add(new GraniteParticleType.GraniteNote("NOTE", 0f));
        types.add(new GraniteParticleType("PORTAL", true));
        types.add(new GraniteParticleType("ENCHANTMENT_TABLE", true));
        types.add(new GraniteParticleType("FLAME", true));
        types.add(new GraniteParticleType("LAVA", false));
        types.add(new GraniteParticleType("FOOTSTEP", false));
        types.add(new GraniteParticleType("CLOUD", true));
        types.add(new GraniteParticleType.GraniteColorable("REDSTONE", Color.RED));
        types.add(new GraniteParticleType("SNOWBALL", false));
        types.add(new GraniteParticleType("SNOW_SHOVEL", true));
        types.add(new GraniteParticleType("SLIME", false));
        types.add(new GraniteParticleType("HEART", false));
        types.add(new GraniteParticleType("BARRIER", false));
        types.add(new GraniteParticleType.GraniteMaterial("ITEM_CRACK", true, getItemBuilder().itemType(ItemTypes.STONE).build()));
        types.add(new GraniteParticleType.GraniteMaterial("BLOCK_CRACK", true, getItemBuilder().itemType(ItemTypes.STONE).build()));
        types.add(new GraniteParticleType.GraniteMaterial("BLOCK_DUST", true, getItemBuilder().itemType(ItemTypes.STONE).build()));
        types.add(new GraniteParticleType("WATER_DROP", false));
        types.add(new GraniteParticleType("ITEM_TAKE", false));
        types.add(new GraniteParticleType("MOB_APPEARANCE", false));

        for (int i = 0; i < types.size(); i++) {
            GraniteParticleType type = types.get(i);
            type.setId(i);
            particles.put(type.getName(), type);

            Field field = ParticleTypes.class.getDeclaredFields()[i];
            ReflectionUtils.forceAccessible(field);
            try {
                field.set(null, type);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            if (Main.debugLog) {
                Granite.getInstance().getLogger().info("Registered Particle minecraft:" + type.getName());
            }
        }
    }

    private void registerPotionEffects() {
        Granite.instance.getLogger().info("Registering PotionEffects");

        try {
            Class potionClass = Mappings.getClass("Potion");
            Field potionTypes = Mappings.getField(potionClass, "potionTypes");
            ArrayList<MCPotion> mcPotions = Lists.newArrayList((MCPotion[]) potionTypes.get(potionClass));
            mcPotions.removeAll(Collections.singleton(null));

            for (Field field : PotionEffectTypes.class.getDeclaredFields()) {
                ReflectionUtils.forceAccessible(field);

                String name = field.getName().toLowerCase();
                for (MCPotion potion : mcPotions) {
                    HashMap<Object, MCPotion>
                            resourceToPotion =
                            (HashMap) Mappings.getField(potion.getClass(), "resourceToPotion").get(potion.getClass());

                    Object resourceLocation = null;
                    for (Map.Entry entry : resourceToPotion.entrySet()) {
                        if (potion.equals(entry.getValue())) {
                            resourceLocation = entry.getKey();
                        }
                    }

                    String potionName = (String) Mappings.getField(resourceLocation.getClass(), "resourcePath").get(resourceLocation);
                    if (name.equals(potionName)) {
                        PotionEffectType potionEffectType = new GranitePotionEffectType(potion);
                        field.set(null, potionEffectType);
                        potionEffects.put(name, potionEffectType);
                        if (Main.debugLog) {
                            Granite.getInstance().getLogger().info("Registered Potion Effect minecraft:" + potionName);
                        }
                    }

                }
            }

        } catch (IllegalAccessException e) {
            Throwables.propagate(e);
        }

    }

    private void registerProfessionsAndCareers() {
        Granite.instance.getLogger().info("Registering Professions and Careers");

        for (int i = 0; i < Professions.class.getDeclaredFields().length; i++) {
            Field field = Professions.class.getDeclaredFields()[i];
            ReflectionUtils.forceAccessible(field);

            String name = field.getName().toLowerCase();
            try {
                Profession profession = new GraniteProfession(i, name);
                field.set(null, profession);
                professions.put(name, profession);
                if (Main.debugLog) {
                    Granite.getInstance().getLogger().info("Registered Profession minecraft:" + profession.getName());
                }
            } catch (IllegalAccessException e) {
                Throwables.propagate(e);
            }
        }

        List<Career> farmers = new ArrayList<>();
        List<Career> librarians = new ArrayList<>();
        List<Career> priests = new ArrayList<>();
        List<Career> blacksmiths = new ArrayList<>();
        List<Career> butchers = new ArrayList<>();

        Profession farmerProfession = professions.get("farmer");
        Profession librarianProfession = professions.get("librarian");
        Profession priestProfession = professions.get("priest");
        Profession blacksmithProfession = professions.get("blacksmith");
        Profession butcherProfession = professions.get("butcher");

        for (int i = 0; i < Careers.class.getDeclaredFields().length; i++) {
            Field field = Careers.class.getDeclaredFields()[i];
            ReflectionUtils.forceAccessible(field);

            String name = field.getName().toLowerCase();
            try {
                boolean registered = false;
                if (i < 4) {
                    Career career = new GraniteCareer(i, name, farmerProfession);
                    field.set(null, career);
                    farmers.add(career);
                    registered = true;
                } else if (i == 4) {
                    Career career = new GraniteCareer(i, name, librarianProfession);
                    field.set(null, career);
                    librarians.add(career);
                    registered = true;
                } else if (i == 5) {
                    Career career = new GraniteCareer(i, name, priestProfession);
                    field.set(null, career);
                    priests.add(career);
                    registered = true;
                } else if (i > 5 && i <= 7) {
                    Career career = new GraniteCareer(i, name, blacksmithProfession);
                    field.set(null, career);
                    blacksmiths.add(career);
                    registered = true;
                } else if (i >= 8 && i <= 10) {
                    Career career = new GraniteCareer(i, name, butcherProfession);
                    field.set(null, career);
                    butchers.add(career);
                    registered = true;
                }
                if (Main.debugLog && registered) {
                    Granite.getInstance().getLogger().info("Registered Career minecraft:" + name);
                }
            } catch (IllegalAccessException e) {
                Throwables.propagate(e);
            }
        }

        professionCareers.put(farmerProfession, farmers);
        professionCareers.put(librarianProfession, librarians);
        professionCareers.put(priestProfession, priests);
        professionCareers.put(blacksmithProfession, blacksmiths);
        professionCareers.put(butcherProfession, butchers);
    }

    private void registerRabbits() {
        Granite.instance.getLogger().info("Registering Rabbits");

        for (int i = 0; i < RabbitTypes.class.getDeclaredFields().length; i++) {
            Field field = RabbitTypes.class.getDeclaredFields()[i];
            ReflectionUtils.forceAccessible(field);

            String name = field.getName().toLowerCase();
            try {
                RabbitType rabbitType = new GraniteRabbitType(RabbitTypes.class.getDeclaredFields()[i].toString().equals("KILLER") ? 99 : i, name);
                field.set(null, rabbitType);
                rabbits.put(name, rabbitType);
                if (Main.debugLog) {
                    Granite.getInstance().getLogger().info("Registered Rabbit minecraft:" + rabbitType.getName());
                }
            } catch (IllegalAccessException e) {
                Throwables.propagate(e);
            }
        }
    }

    private void registerRotations() {
        Granite.instance.getLogger().info("Registering Rotations");

        int angle = 0;
        Field[] fields = Rotations.class.getDeclaredFields();

        for (Field field : fields) {
            ReflectionUtils.forceAccessible(field);

            try {
                Rotation rotation = new GraniteRotation(angle);
                field.set(null, rotation);
                rotations.put(angle, rotation);
                angle += 45;
                if (Main.debugLog) {
                    Granite.getInstance().getLogger().info("Registered Rotation degrees:" + rotation.getAngle());
                }
            } catch (IllegalAccessException e) {
                Throwables.propagate(e);
            }
        }
    }

    private void registerSkeletons() {
        Granite.instance.getLogger().info("Registering Skeletons");

        for (int i = 0; i < SkeletonTypes.class.getDeclaredFields().length; i++) {
            Field field = SkeletonTypes.class.getDeclaredFields()[i];
            ReflectionUtils.forceAccessible(field);

            String name = field.getName().toLowerCase();
            try {
                SkeletonType skeletonType = new GraniteSkeletonType(i, name);
                field.set(null, skeletonType);
                skeletons.put(name, skeletonType);
                if (Main.debugLog) {
                    Granite.getInstance().getLogger().info("Registered Skeleton minecraft:" + skeletonType.getName());
                }
            } catch (IllegalAccessException e) {
                Throwables.propagate(e);
            }
        }
    }

    private void registerSounds() {
        // TODO: Register all sounds to list in order they are in the SpongeAPI Class
        /*Granite.instance.getLogger().info("Registering Sounds");

        List<String> minecraftSoundNames = new ArrayList<>();
        minecraftSoundNames.add("");

        for (int i = 0; i < minecraftSoundNames.size(); i++) {
            SoundType soundType = new GraniteSoundType(minecraftSoundNames.get(i));
            sounds.put(minecraftSoundNames.get(i), soundType);
            Field field = SoundTypes.class.getDeclaredFields()[i];
            ReflectionUtils.forceAccessible(field);
            try {
                field.set(null, soundType);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (Main.debugLog) {
                Granite.getInstance().getLogger().info("Registered Sound minecraft:" + soundType.getName());
            }
        }*/
    }

    @Override
    public Optional<BlockType> getBlock(String id) {
        return Optional.fromNullable(blockTypes.get(id));
    }

    @Override
    public List<BlockType> getBlocks() {
        return ImmutableList.copyOf(blockTypes.values());
    }

    @Override
    public Optional<ItemType> getItem(String id) {
        return Optional.fromNullable(itemTypes.get(id));
    }

    @Override
    public List<ItemType> getItems() {
        return ImmutableList.copyOf(itemTypes.values());
    }

    @Override
    public Optional<BiomeType> getBiome(String id) {
        return Optional.fromNullable(biomes.get(id));
    }

    @Override
    public List<BiomeType> getBiomes() {
        return ImmutableList.copyOf(biomes.values());
    }

    @Override
    public ItemStackBuilder getItemBuilder() {
        return itemStackBuilder;
    }

    @Override
    public TradeOfferBuilder getTradeOfferBuilder() {
        // TODO: TradeOfferBuilder API
        throw new NotImplementedException("");
    }

    @Override
    public PotionEffectBuilder getPotionEffectBuilder() {
        return potionBuilder;
    }

    @Override
    public Optional<ParticleType> getParticleType(String name) {
        return Optional.fromNullable(particles.get(name));
    }

    @Override
    public List<ParticleType> getParticleTypes() {
        return ImmutableList.copyOf(particles.values());
    }

    @Override
    public ParticleEffectBuilder getParticleEffectBuilder(ParticleType particle) {
        if (particle instanceof ParticleType.Resizable) {
            return new GraniteParticleEffectBuilder.GraniteResizable((GraniteParticleType.GraniteResizable) particle);
        }
        if (particle instanceof ParticleType.Colorable) {
            return new GraniteParticleEffectBuilder.GraniteColorable((GraniteParticleType.GraniteColorable) particle);
        }
        if (particle instanceof ParticleType.Note) {
            return new GraniteParticleEffectBuilder.GraniteNote((GraniteParticleType.GraniteNote) particle);
        }
        if (particle instanceof ParticleType.Material) {
            return new GraniteParticleEffectBuilder.GraniteMaterial((GraniteParticleType.GraniteMaterial) particle);
        }
        return new GraniteParticleEffectBuilder((GraniteParticleType) particle);
    }

    @Override
    public Optional<SoundType> getSound(String id) {
        return Optional.fromNullable(sounds.get(id));
    }

    @Override
    public List<SoundType> getSounds() {
        return ImmutableList.copyOf(sounds.values());
    }

    @Override
    public Optional<EntityType> getEntity(String id) {
        // TODO: EntityType API
        throw new NotImplementedException("");
    }

    @Override
    public List<EntityType> getEntities() {
        // TODO: EntityType API
        throw new NotImplementedException("");
    }

    @Override
    public Optional<Art> getArt(String id) {
        return Optional.fromNullable(arts.get(id));
    }

    @Override
    public List<Art> getArts() {
        return ImmutableList.copyOf(arts.values());
    }

    @Override
    public Optional<DyeColor> getDye(String id) {
        return Optional.fromNullable(dyeColors.get(id));
    }

    @Override
    public List<DyeColor> getDyes() {
        return ImmutableList.copyOf(dyeColors.values());
    }

    @Override
    public Optional<HorseColor> getHorseColor(String id) {
        return Optional.fromNullable(horseColors.get(id));
    }

    @Override
    public List<HorseColor> getHorseColors() {
        return ImmutableList.copyOf(horseColors.values());
    }

    @Override
    public Optional<HorseStyle> getHorseStyle(String id) {
        return Optional.fromNullable(horseStyles.get(id));
    }

    @Override
    public List<HorseStyle> getHorseStyles() {
        return ImmutableList.copyOf(horseStyles.values());
    }

    @Override
    public Optional<HorseVariant> getHorseVariant(String id) {
        return Optional.fromNullable(horseVariants.get(id));
    }

    @Override
    public List<HorseVariant> getHorseVariants() {
        return ImmutableList.copyOf(horseVariants.values());
    }

    @Override
    public Optional<OcelotType> getOcelotType(String id) {
        return Optional.fromNullable(ocelots.get(id));
    }

    @Override
    public List<OcelotType> getOcelotTypes() {
        return ImmutableList.copyOf(ocelots.values());
    }

    @Override
    public Optional<RabbitType> getRabbitType(String id) {
        return Optional.fromNullable(rabbits.get(id));
    }

    @Override
    public List<RabbitType> getRabbitTypes() {
        return ImmutableList.copyOf(rabbits.values());
    }

    @Override
    public Optional<SkeletonType> getSkeletonType(String id) {
        return Optional.fromNullable(skeletons.get(id));
    }

    @Override
    public List<SkeletonType> getSkeletonTypes() {
        return ImmutableList.copyOf(skeletons.values());
    }

    @Override
    public Optional<Career> getCareer(String id) {
        return Optional.fromNullable(careers.get(id));
    }

    @Override
    public List<Career> getCareers() {
        return ImmutableList.copyOf(careers.values());
    }

    @Override
    public List<Career> getCareers(Profession profession) {
        return professionCareers.get(profession);
    }

    @Override
    public Optional<Profession> getProfession(String id) {
        return Optional.fromNullable(professions.get(id));
    }

    @Override
    public List<Profession> getProfessions() {
        return ImmutableList.copyOf(professions.values());
    }

    @Override
    public List<GameMode> getGameModes() {
        // TODO: GameMode API
        throw new NotImplementedException("");
    }

    @Override
    public List<PotionEffectType> getPotionEffects() {
        return ImmutableList.copyOf(potionEffects.values());
    }

    @Override
    public Optional<Enchantment> getEnchantment(String id) {
        return Optional.fromNullable(enchantments.get(id));
    }

    @Override
    public List<Enchantment> getEnchantments() {
        return ImmutableList.copyOf(enchantments.values());
    }

    @Override
    public Collection<String> getDefaultGameRules() {
        return defaultGameRules;
    }

    @Override
    public Optional<DimensionType> getDimensionType(String id) {
        return Optional.fromNullable(dimensions.get(id));
    }

    @Override
    public List<DimensionType> getDimensionTypes() {
        return ImmutableList.copyOf(dimensions.values());
    }

    @Override
    public Optional<Rotation> getRotationFromDegree(int degrees) {
        return Optional.fromNullable(rotations.get(degrees));
    }

    @Override
    public List<Rotation> getRotations() {
        return ImmutableList.copyOf(rotations.values());
    }

    @Override
    public GameProfile createGameProfile(UUID uuid, String name) {
        return new GraniteGameProfile(Instantiator.get().newGameProfile(uuid, name));
    }

    @Override
    public Favicon loadFavicon(String image) throws IOException {
        return new GraniteFavicon(image);
    }

    @Override
    public Favicon loadFavicon(File file) throws IOException {
        return new GraniteFavicon(file);
    }

    @Override
    public Favicon loadFavicon(URL url) throws IOException {
        return new GraniteFavicon(url);
    }

    @Override
    public Favicon loadFavicon(InputStream inputStream) throws IOException {
        return new GraniteFavicon(inputStream);
    }

    @Override
    public Favicon loadFavicon(BufferedImage bufferedImage) throws IOException {
        return new GraniteFavicon(bufferedImage);
    }

    @Override
    public Optional<NotePitch> getNotePitch(String id) {
        throw new NotImplementedException("");
    }

    @Override
    public List<NotePitch> getNotePitches() {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<SkullType> getSkullType(String id) {
        throw new NotImplementedException("");
    }

    @Override
    public List<SkullType> getSkullTypes() {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<BannerPatternShape> getBannerPatternShape(String id) {
        throw new NotImplementedException("");
    }

    @Override
    public Optional<BannerPatternShape> getBannerPatternShapeById(String id) {
        throw new NotImplementedException("");
    }

    @Override
    public List<BannerPatternShape> getBannerPatternShapes() {
        throw new NotImplementedException("");
    }
}
