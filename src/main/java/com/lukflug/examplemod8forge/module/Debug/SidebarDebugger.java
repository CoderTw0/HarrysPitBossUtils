package com.lukflug.examplemod8forge.module.Debug;

import com.lukflug.examplemod8forge.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Score;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SidebarDebugger extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Pattern COLOR_ENCODING_PATTERN = Pattern.compile("(?i)[§�][0-9A-FK-OR]");
    /**
     * Method credit to Aaron1998ish on 11/12/2017.
     */

    public SidebarDebugger() {
        super("SidebarScoreboardDebug", "Prints sidebar info from the Scoreboard", () -> true, true);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static List<String> getSidebarScores(Scoreboard scoreboard) {
        List<String> found = new ArrayList<>();

        ScoreObjective sidebar = scoreboard.getObjectiveInDisplaySlot(1);
        if (sidebar != null) {
            List<Score> scores = new ArrayList<>(scoreboard.getScores());

            scores.sort(Comparator.comparingInt(Score::getScorePoints));

            found = scores.stream()
                    .filter(score -> score.getObjective().getName().equals(sidebar.getName()))
                    .map(score -> getFormattedLine(scoreboard, score))
                    .collect(Collectors.toList());
        }
        return found;
    }

    private static String getFormattedLine(Scoreboard scoreboard, Score score) {
        String entry = score.getPlayerName();
        ScorePlayerTeam team = scoreboard.getPlayersTeam(entry);

        String prefix = (team != null && team.getColorPrefix() != null) ? team.getColorPrefix() : "";
        String suffix = (team != null && team.getColorSuffix() != null) ? team.getColorSuffix() : "";

        String raw = prefix + entry + suffix;
        return COLOR_ENCODING_PATTERN.matcher(raw).replaceAll("");
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!isEnabled().isOn() || mc.theWorld == null) return;

        Scoreboard scoreboard = mc.theWorld.getScoreboard();
        List<String> sidebarLines = getSidebarScores(scoreboard);

        System.out.println("=== Sidebar Lines ===");
        for (String line : sidebarLines) {
            System.out.println(line);

            if (line.contains("Streak:")) {
                System.out.println("[SidebarDebugger] Found streak line: " + line);

                String streakValue = line.replace("Streak:", "").trim();
                System.out.println("[SidebarDebugger] Clean streak value: " + streakValue);
            }
        }
        System.out.println("=== End of Sidebar ===");
    }
}
