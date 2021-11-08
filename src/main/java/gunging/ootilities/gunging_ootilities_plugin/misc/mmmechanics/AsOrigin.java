package gunging.ootilities.gunging_ootilities_plugin.misc.mmmechanics;

import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.compatibilities.GooPMythicMobs;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.*;
import io.lumine.xikage.mythicmobs.skills.mechanics.CustomMechanic;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderString;
import io.lumine.xikage.mythicmobs.util.annotations.MythicMechanic;
import org.bukkit.entity.ArmorStand;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class AsOrigin extends SkillMechanic implements IMetaSkill {
    boolean targetArmorStands;
    PlaceholderString skillName;
    Skill metaskill;

    public AsOrigin(CustomMechanic skill, MythicLineConfig mlc) {
        super(skill.getConfigLine(), mlc);
        targetArmorStands = mlc.getBoolean(new String[]{"targetarmorstands", "ta"}, false);
        skillName = mlc.getPlaceholderString(new String[]{"skill", "s", "meta", "m", "mechanics", "$", "()"}, "skill not found");
        metaskill = GooPMythicMobs.GetSkill(skillName.get());

        // Attempt to fix meta skill
        if (metaskill == null) {
            //MM//OotilityCeption.Log("\u00a7c--->> \u00a7eMeta Skill Failure \u00a7c<<---");

            // Try again i guess?
            (new BukkitRunnable() {
                public void run() {

                    // Run Async
                    metaskill = GooPMythicMobs.GetSkill(skillName.get());

                }
            }).runTaskLater(Gunging_Ootilities_Plugin.getPlugin(), 1L);
        }
    }

    public boolean cast(@NotNull SkillMetadata data) {

        // Get from placeholders :eyes1:
        if (metaskill == null) { metaskill = GooPMythicMobs.GetSkill(skillName.get(data, data.getCaster().getEntity()));}
        if (metaskill == null) {
            //MM//OotilityCeption.Log("\u00a7c--- \u00a77Meta Skill not Found \u00a7c---");
            return false; }


        HashSet<AbstractLocation> locations = new HashSet<>();
        if (data.getEntityTargets() != null) { locations = new HashSet<>(data.getLocationTargets()); }

        //MM//OotilityCeption.Log("\u00a73--- \u00a77Original Locatons \u00a73---");
        for (AbstractLocation t : locations) {
            if (t == null) { continue; }

            // For every target
            //MM//OotilityCeption.Log("\u00a73 >>> \u00a77Running at\u00a7b " + t.getWorld() + " " + t.getX() + " " + t.getY() + " " + t.getZ());

            // Copy data and replace caster
            final SkillMetadata clonedData = data.deepClone();
            clonedData.setOrigin(t);

            // ??
            if (!metaskill.isUsable(clonedData)) { return false; }
            //MM//OotilityCeption.Log("\u00a7a  + \u00a77Usable");

            // Run skill sync or async
            if (forceSync) {
                //MM//OotilityCeption.Log("\u00a7a  + \u00a77Running Async");

                clonedData.setIsAsync(false);
                (new BukkitRunnable() {
                    public void run() {

                        // Run Async
                        clonedData.setIsAsync(false);
                        metaskill.execute(clonedData);

                    }
                }).runTask(MythicMobs.inst());

                // Forcing Sync
            } else {
                //MM//OotilityCeption.Log("\u00a7a  + \u00a77Running Sync");

                // Run Sync
                metaskill.execute(clonedData);
            }
        }

        HashSet<AbstractEntity> targets = new HashSet<>();
        if (data.getEntityTargets() != null) { targets = new HashSet<>(data.getEntityTargets()); }

        //MM//OotilityCeption.Log("\u00a73--- \u00a77Original Targets \u00a73---");
        for (AbstractEntity t : targets) {
            if (t == null) { continue; }

            // For every target
            //MM//OotilityCeption.Log("\u00a73 >>> \u00a77Running for " + t.getName());

            // Include?
            if ((t.getBukkitEntity() instanceof ArmorStand) && !targetArmorStands) {
                //MM//OotilityCeption.Log("\u00a7c >>> \u00a77Skipped: Armor Stand");
                continue; }

            // Copy data and replace caster
            final SkillMetadata clonedData = data.deepClone();
            clonedData.setOrigin(t.getLocation());

            // ??
            if (!metaskill.isUsable(clonedData)) { return false; }
            //MM//OotilityCeption.Log("\u00a7a  + \u00a77Usable");

            // Run skill sync or async
            if (forceSync) {
                //MM//OotilityCeption.Log("\u00a7a  + \u00a77Running Async");

                clonedData.setIsAsync(false);
                (new BukkitRunnable() {
                    public void run() {

                        // Run Async
                        clonedData.setIsAsync(false);
                        metaskill.execute(clonedData);

                    }
                }).runTask(MythicMobs.inst());

                // Forcing Sync
            } else {
                //MM//OotilityCeption.Log("\u00a7a  + \u00a77Running Sync");

                // Run Sync
                metaskill.execute(clonedData);
            }
        }
        // Success I guess
        return true;
    }
}
