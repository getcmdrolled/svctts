package net.scoobis.svctts;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler;

@Config(name = SvcTtsMod.MOD_ID)
public class ModConfig implements ConfigData {
    public enum Provider {
        FREETTS,
        LIBFLITE
    }

    @ConfigEntry.Category("general")
    @EnumHandler(option = EnumHandler.EnumDisplayOption.BUTTON)
    public Provider provider_option = Provider.FREETTS;

    @ConfigEntry.Category("general")
    @ConfigEntry.BoundedDiscrete(min = 0, max = 500)
    public int pitch = 100;

    public enum LibFliteVoice {
        KAL16,
        SLT,
        AWB
    }

    @ConfigEntry.Category("specific")
    @EnumHandler(option = EnumHandler.EnumDisplayOption.BUTTON)
    public LibFliteVoice libflite_voice = LibFliteVoice.KAL16;
}
