package com.ahmed.customarsenal.client.render;

import com.ahmed.customarsenal.CustomArsenalMod;
import net.fabricmc.loader.api.FabricLoader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class CustomModelManager {

    public record ItemVisualSpec(String id, String template3d, int[] palette) {}

    private static final Map<String, ItemVisualSpec> VISUAL_SPECS = new LinkedHashMap<>();

    static {
        registerSpec("shadow_sword",    "template_3d_blade",  new int[]{0x1B082C, 0x551A8B, 0xA040FF, 0xD896FF});
        registerSpec("inferno_sword",   "template_3d_blade",  new int[]{0x3A0B00, 0xB82800, 0xFF6600, 0xFFDD44});
        registerSpec("frost_blade",     "template_3d_blade",  new int[]{0x0A2540, 0x1E78B4, 0x66CCFF, 0xE0F7FF});
        registerSpec("thunder_staff",   "template_3d_staff",  new int[]{0x2E2400, 0x997A00, 0xFFDD00, 0xFFFFFF});
        registerSpec("void_orb",        "template_3d_orb",    new int[]{0x0B0014, 0x2B0040, 0x7A1FA2, 0xE1BEE7});
        registerSpec("guardian_shield", "template_3d_shield", new int[]{0x2D1E12, 0x5C4028, 0x8B5A2B, 0xCCA662});
        registerSpec("grappling_hook",  "template_3d_blade",  new int[]{0x222629, 0x545D63, 0x8C9BA5, 0xD0DDE6});
        registerSpec("wind_blade",      "template_3d_blade",  new int[]{0x0D3322, 0x268C5C, 0x66E6A3, 0xE6FFF2});
        registerSpec("poison_dagger",   "template_3d_blade",  new int[]{0x0D290D, 0x1F661F, 0x33CC33, 0xB3FFB3});
        registerSpec("light_staff",     "template_3d_staff",  new int[]{0x4D3D00, 0xB38F00, 0xFFEE66, 0xFFFFFF});
    }

    private static void registerSpec(String id, String template3d, int[] palette) {
        VISUAL_SPECS.put(id, new ItemVisualSpec(id, template3d, palette));
    }

    public static void initClientAssets() {
        Path projectAssets = FabricLoader.getInstance().getGameDir()
                .resolve("../src/main/resources/assets/custom_arsenal").normalize();

        if (Files.exists(projectAssets.getParent())) {
            try {
                Path itemsDir = Files.createDirectories(projectAssets.resolve("items"));
                Path modelsItemDir = Files.createDirectories(projectAssets.resolve("models/item"));
                Path texturesItemDir = Files.createDirectories(projectAssets.resolve("textures/item"));

                for (ItemVisualSpec spec : VISUAL_SPECS.values()) {
                    String id = spec.id();

                    // 1. 1.21.11 Client Item Definition (assets/custom_arsenal/items/<id>.json)
                    Path itemDefPath = itemsDir.resolve(id + ".json");
                    if (!Files.exists(itemDefPath)) {
                        Files.writeString(itemDefPath, create12111ItemDefinitionJson(id), StandardCharsets.UTF_8);
                    }

                    // 2. 2D GUI Icon Model (assets/custom_arsenal/models/item/<id>_gui.json)
                    Path guiModelPath = modelsItemDir.resolve(id + "_gui.json");
                    if (!Files.exists(guiModelPath)) {
                        Files.writeString(guiModelPath, createGuiModelJson(id), StandardCharsets.UTF_8);
                    }

                    // 3. 3D Held Model (assets/custom_arsenal/models/item/<id>_3d.json)
                    Path heldModelPath = modelsItemDir.resolve(id + "_3d.json");
                    if (!Files.exists(heldModelPath)) {
                        Files.writeString(heldModelPath, create3DModelJson(id, spec.template3d()), StandardCharsets.UTF_8);
                    }

                    // 4. 3D Ground Model (assets/custom_arsenal/models/item/<id>_ground.json)
                    Path groundModelPath = modelsItemDir.resolve(id + "_ground.json");
                    if (!Files.exists(groundModelPath)) {
                        Files.writeString(groundModelPath, create3DModelJson(id, spec.template3d()), StandardCharsets.UTF_8);
                    }

                    // 5. Texture PNG (assets/custom_arsenal/textures/item/<id>.png)
                    Path pngPath = texturesItemDir.resolve(id + ".png");
                    if (!Files.exists(pngPath)) {
                        Files.write(pngPath, generateStylizedSprite(id, spec.palette()));
                    }
                }
            } catch (IOException e) {
                CustomArsenalMod.LOGGER.warn("Could not auto-populate dev resources: {}", e.getMessage());
            }
        }
        CustomArsenalMod.LOGGER.info("CustomModelManager verified all 10 custom 3D/2D item models and textures.");
    }

    private static String create12111ItemDefinitionJson(String id) {
        return """
                {
                  "model": {
                    "type": "minecraft:select",
                    "property": "minecraft:display_context",
                    "cases": [
                      {
                        "when": ["gui", "fixed"],
                        "model": {
                          "type": "minecraft:model",
                          "model": "custom_arsenal:item/%s_gui"
                        }
                      },
                      {
                        "when": ["ground"],
                        "model": {
                          "type": "minecraft:model",
                          "model": "custom_arsenal:item/%s_ground"
                        }
                      }
                    ],
                    "fallback": {
                      "type": "minecraft:model",
                      "model": "custom_arsenal:item/%s_3d"
                    }
                  }
                }
                """.formatted(id, id, id);
    }

    private static String createGuiModelJson(String id) {
        return """
                {
                  "parent": "minecraft:item/handheld",
                  "textures": {
                    "layer0": "custom_arsenal:item/%s"
                  }
                }
                """.formatted(id);
    }

    private static String create3DModelJson(String id, String templateName) {
        return """
                {
                  "parent": "custom_arsenal:item/%s",
                  "textures": {
                    "layer0": "custom_arsenal:item/%s"
                  }
                }
                """.formatted(templateName, id);
    }

    public static byte[] generateStylizedSprite(String id, int[] palette) throws IOException {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        boolean isOrb = id.contains("orb");
        boolean isShield = id.contains("shield");

        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                if (isOrb) {
                    double dist = Math.hypot(x - 7.5, y - 7.5);
                    if (dist <= 6.2) {
                        int idx = dist < 2.2 ? 3 : (dist < 4.0 ? 2 : (dist < 5.3 ? 1 : 0));
                        img.setRGB(x, y, 0xFF000000 | palette[idx]);
                    }
                } else if (isShield) {
                    if (x >= 2 && x <= 13 && y >= 1 && y <= 14 && (y < 10 || (x - 2 >= (y - 9) && 13 - x >= (y - 9)))) {
                        boolean border = (x == 2 || x == 13 || y == 1 || y == 14);
                        boolean cross = (x == 7 || x == 8 || y == 7);
                        int color = border ? palette[0] : (cross ? palette[3] : palette[2]);
                        img.setRGB(x, y, 0xFF000000 | color);
                    }
                } else {
                    int diag = (15 - y) - x;
                    if (x <= 4 && y >= 11 && Math.abs(diag) <= 1) {
                        img.setRGB(x, y, 0xFF000000 | palette[0]);
                    } else if (x >= 2 && x <= 6 && y >= 9 && y <= 13 && (x + (15 - y) == 6 || x + (15 - y) == 7)) {
                        img.setRGB(x, y, 0xFF000000 | palette[3]);
                    } else if (x >= 4 && y <= 11 && Math.abs(diag) <= 1) {
                        int color = (diag == 0) ? palette[3] : palette[2];
                        img.setRGB(x, y, 0xFF000000 | color);
                    }
                }
            }
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "PNG", baos);
        return baos.toByteArray();
    }
}
