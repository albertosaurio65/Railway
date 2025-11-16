import os

# Define las equivalencias de importaciones
replacements = {
    "com.simibubi.create.foundation.data.CreateRegistrate": "com.railwayteam.railways.registry.forge.CreateRegistrateImpl",
    "com.simibubi.create.AllBlocks": "com.railwayteam.railways.registry.forge.CRBlocksImpl",
    "com.simibubi.create.AllItems": "com.railwayteam.railways.registry.forge.CRItemsImpl",
    "com.simibubi.create.AllTags": "com.railwayteam.railways.registry.forge.CRTagImpl",
    "net.fabricmc.fabric.api.resource.conditions.v1.ConditionJsonProvider": "// removed for NeoForge",
    "net.fabricmc.fabric.api.resource.conditions.v1.DefaultResourceConditions": "// removed for NeoForge",
    # agrega más según lo que necesites
}

# Carpeta raíz de tu proyecto
root_dir = "C:/Users/alber/Documents/GitHub/Railway/common/src/main/java"

for subdir, _, files in os.walk(root_dir):
    for file in files:
        if file.endswith(".java"):
            path = os.path.join(subdir, file)
            with open(path, "r", encoding="utf-8") as f:
                lines = f.readlines()

            new_lines = []
            changed = False
            for line in lines:
                stripped = line.strip()
                if stripped.startswith("import"):
                    for old, new in replacements.items():
                        if old in line:
                            line = line.replace(old, new)
                            changed = True
                new_lines.append(line)

            if changed:
                with open(path, "w", encoding="utf-8") as f:
                    f.writelines(new_lines)
                print(f"Modificado: {path}")
