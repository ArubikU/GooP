package gunging.ootilities.gunging_ootilities_plugin.compatibilities;

import gunging.ootilities.gunging_ootilities_plugin.GungingOotilities;
import gunging.ootilities.gunging_ootilities_plugin.Gunging_Ootilities_Plugin;
import gunging.ootilities.gunging_ootilities_plugin.OotilityCeption;
import gunging.ootilities.gunging_ootilities_plugin.events.GooP_FontUtils;
import gunging.ootilities.gunging_ootilities_plugin.events.SummonerClassUtils;
import gunging.ootilities.gunging_ootilities_plugin.events.XBow_Rockets;
import gunging.ootilities.gunging_ootilities_plugin.misc.ListPlaceholder;
import gunging.ootilities.gunging_ootilities_plugin.misc.OptimizedTimeFormat;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Calendar;

public class GooPPlaceholderAPI extends PlaceholderExpansion {

    public void CompatibilityCheck() {
        // Misc method that WILL fail if Placeholders API is not installed
        PlaceholderAPI.getExpansions();
    }

    // Vro parse dat shit
    public static String Parse(Player plyr, String strng) {
        return Parse((OfflinePlayer) plyr, strng);
    }
    public static String Parse(OfflinePlayer plyr, String strng) {

        // Parse and return
        return PlaceholderAPI.setPlaceholders(plyr, strng);
    }

    // Does it have papi shit
    public static Boolean HasPlaceholders(String strng) {

        // Parse and return
        return PlaceholderAPI.containsPlaceholders(strng);
    }

    /**
     * This method should always return true unless we
     * have a dependency we need to make sure is on the server
     * for our placeholders to work!
     *
     * @return always true since we do not have any dependencies.
     */
    @Override
    public boolean canRegister(){
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     *
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor(){
        return "gunging";
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>The identifier has to be lowercase and can't contain _ or %
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier(){
        return "goop";
    }

    /**
     * This is the version of this expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion(){
        return "1.0";
    }

    /**
     * This is the method called when a placeholder with our identifier
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param  player
     *         A {@link org.bukkit.OfflinePlayer OfflinePlayer}.
     * @param  identifier
     *         A String containing the identifier/value.
     *
     * @return Possibly-null String of the requested identifier.
     */
    @Override
    public String onRequest(OfflinePlayer player, String identifier) {

        // %goop_slot_provided%
        if(identifier.startsWith("slot_")) {

            // Strip slot_
            String identifier2 = identifier.substring("slot_".length());
            if (identifier2.equals("provided")) {

                if (GungingOotilities.providedSlot.containsKey(player.getUniqueId())) {

                    return String.valueOf(GungingOotilities.providedSlot.get(player.getUniqueId()));

                } else { return "Invalid Entity"; }
            }

        // %goop_bowdraw%
        } else if (identifier.equals("max_minions") && player instanceof Entity) {

            // Return the max minions
            return String.valueOf(SummonerClassUtils.GetMaxMinions((Entity) player));

        // %goop_bowdraw%
        } else if (identifier.equals("minions")) {

            // Return the current minions
            return String.valueOf(SummonerClassUtils.GetCurrentMinionCount(player.getUniqueId()));

        // %goop_bowdraw%
        } else if (identifier.equals("bowdraw")) {

            if (XBow_Rockets.bowDrawForce.containsKey(player.getUniqueId())) {

                return String.valueOf(XBow_Rockets.bowDrawForce.get(player.getUniqueId()));

            } else { return "0.0"; }

        // %goop_ordered_[lph name]_[index]%
        } else if (identifier.startsWith("ordered_")) {

            // If valid
            String identifier2 = identifier.substring("ordered_".length());

            // Strip ordered index
            int lastDot = identifier2.lastIndexOf('_');
            int orderedIndex = 0;

            // If there was a dot
            if (lastDot > 0 && identifier2.length() > (lastDot + 1)) {

                // Crop
                String postdot = identifier2.substring(lastDot + 1);

                // Does it parse
                if (OotilityCeption.IntTryParse(postdot)) {

                    // Store ordered index
                    orderedIndex = OotilityCeption.ParseInt(postdot);

                    // Crop
                    identifier2 = identifier2.substring(0, lastDot);
                }
            }

            // Get List
            ListPlaceholder lph = ListPlaceholder.Get(identifier2);

            // Did it exist?
            if (lph != null) {

                // Well return the next balue-yo!
                return lph.NextListItem(orderedIndex);

            } else {

                // Invalid list
                return "Invalid List of Name '" + identifier2 + "'";
            }

        // %goop_random_[lph name]%
        } else if (identifier.startsWith("random_")) {

            // If valid
            String identifier2 = identifier.substring("random_".length());

            // Get List
            ListPlaceholder lph = ListPlaceholder.Get(identifier2);

            // Did it exist?
            if (lph != null) {

                // Well return the next balue-yo!
                return lph.RandomListItem();

            } else {

                // Invalid list
                return "Invalid List of Name '" + identifier2 + "'";
            }

        // %goop_font_[CODE]%
        } else if (identifier.startsWith("font_")) {

            // If valid
            String identifier2 = identifier.substring("font_".length());

            // Get Code
            String code = GooP_FontUtils.CodeFrom(identifier2);

            // Wasit?
            if (code != null) { return code; }
            else { return "{invalid font code}"; }

        // %goop_mcmmostat_value%
        } else if (identifier.startsWith("mcmmostat_") && Gunging_Ootilities_Plugin.foundMCMMO) {

            if (!player.isOnline()) { return null; }

            // If valid
            String identifier2 = identifier.substring("mcmmostat_".length());

            // A value to return
            Double result = GooPMCMMO.MCMMODoubleStat(player.getPlayer(), identifier2);

            // Adjust
            if (result == null) { return "000"; }

            return OotilityCeption.RemoveDecimalZeros(String.valueOf(result));

        // %goop_mmostat_[stat]%
        } else if (identifier.startsWith("mmostat_") && (Gunging_Ootilities_Plugin.foundMMOItems || Gunging_Ootilities_Plugin.foundMMOCore)) {

            if (!player.isOnline()) { return null; }

            // If valid
            String identifier2 = identifier.substring("mmostat_".length());

            // A value to return
            Double result = GooPMMOLib.CDoubleStat(player.getPlayer(), identifier2);

            // Adjust
            if (result == null) { return "000"; }

            return OotilityCeption.RemoveDecimalZeros(String.valueOf(result));

        // %goop_weekday%
        } else if (identifier.equals("weekday")) {

            return OptimizedTimeFormat.GetWeekday();

        // %goop_time%
        } else if (identifier.startsWith("time_")) {

            // If valid
            String identifier2 = identifier.substring("time_".length());

            switch (identifier2) {

                // %goop_time_abs%
                case "abs":
                    return String.valueOf(System.currentTimeMillis());

                // %goop_time_s%
                case "s":
                    return String.valueOf((System.currentTimeMillis() - Gunging_Ootilities_Plugin.getBootTime()) / 1000.0);

                // %goop_time_m%
                case "m":
                    return String.valueOf((System.currentTimeMillis() - Gunging_Ootilities_Plugin.getBootTime()) / 60000.0);

                // %goop_time_h%
                case "h":
                    return String.valueOf((System.currentTimeMillis() - Gunging_Ootilities_Plugin.getBootTime()) / 3600000.0);

                // %goop_time_ms%
                default:
                    return String.valueOf(System.currentTimeMillis() - Gunging_Ootilities_Plugin.getBootTime());
            }
        }

        // was provided
        return null;
    }

    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist(){
        return true;
    }
}
